
package com.flyhz.shop.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.PropertyException;

import org.apache.ibatis.builder.xml.dynamic.ForEachSqlNode;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.framework.tool.Pager;
import com.flyhz.framework.util.ReflectHelper;
import com.flyhz.framework.util.StringUtil;

@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class SqlPlugin implements Interceptor {

	private static final transient Logger	log			= LoggerFactory.getLogger(SqlPlugin.class);

	private static String					dialect		= "";										// 数据库方言
	private static String					pageSqlId	= "";										// 分页Id,mapper.xml中需要拦截的ID(正则匹配)
																									// 数据范围组Service

	public Object intercept(Invocation ivk) throws Throwable {
		if (ivk.getTarget() instanceof RoutingStatementHandler) {
			RoutingStatementHandler statementHandler = (RoutingStatementHandler) ivk.getTarget();
			BaseStatementHandler delegate = (BaseStatementHandler) ReflectHelper.getValueByFieldName(
					statementHandler, "delegate");
			MappedStatement mappedStatement = (MappedStatement) ReflectHelper.getValueByFieldName(
					delegate, "mappedStatement");
			Pager page = Pager.currentPager();
			if (page != null) {
				if (isPages(page, mappedStatement.getId())) {
					// PageSort ps = page.getPageSort();
					if (mappedStatement.getId().matches(pageSqlId)) { // 拦截需要分页的SQL
						BoundSql boundSql = delegate.getBoundSql();
						Connection connection = null;
						ResultSet rs = null;
						PreparedStatement countStmt = null;
						Object parameterObject = boundSql.getParameterObject();// 分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
						try {
							connection = (Connection) ivk.getArgs()[0];
							String sql = boundSql.getSql();
							if (sql.contains("select")) {// 判断是否是select查询语句
								String countSql = "select count(0) as tmp_count from (" + sql
										+ ")  "; // 记录统计
								countStmt = connection.prepareStatement(countSql);
								BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(),
										countSql, boundSql.getParameterMappings(), parameterObject);
								setParameters(countStmt, mappedStatement, countBS, parameterObject);
								rs = countStmt.executeQuery();
								int count = 0;
								if (rs.next()) {
									count = rs.getInt(1);
								}
								page.setTotalRowsAmount(count);// 设置总数
								if (!StringUtil.isEmpty(page.getSortName())) {
									sql = sql + " order by " + page.getSortName() + " "
											+ page.getSortWay();
								}

								String pageSql = generatePageSql(sql, page);
								ReflectHelper.setValueByFieldName(boundSql, "sql", pageSql); // 将分页sql语句反射回BoundSql.
							}
						} catch (Exception e) {
							log.error("SqlPlugin 程序出错！");
						} finally {
							if (rs != null)
								rs.close();
							if (countStmt != null)
								countStmt.close();
						}
						// }
					}
				}
			}
		}

		return ivk.proceed();
	}

	/**
	 * 判断是否要分页
	 * 
	 * @param page
	 * @return
	 */
	private boolean isPages(Pager page, String daoSqlId) {
		boolean flag = true;
		if (page == null) {
			return false;
		} else if (page.getPageSize() > 1) {
			if (StringUtil.isEmpty(page.getPageId())) {
				flag = true;
			} else if (page.getPageId().equals(daoSqlId)) {
				flag = true;
			} else {
				flag = false;
			}
		}
		return flag;
	}

	/**
	 * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.
	 * DefaultParameterHandler
	 * 
	 * @param ps
	 * @param mappedStatement
	 * @param boundSql
	 * @param parameterObject
	 * @throws SQLException
	 */
	private void setParameters(PreparedStatement ps, MappedStatement mappedStatement,
			BoundSql boundSql, Object parameterObject) throws SQLException {
		ErrorContext.instance().activity("setting parameters")
					.object(mappedStatement.getParameterMap().getId());
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		if (parameterMappings != null) {
			Configuration configuration = mappedStatement.getConfiguration();
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			MetaObject metaObject = parameterObject == null ? null
					: configuration.newMetaObject(parameterObject);
			for (int i = 0; i < parameterMappings.size(); i++) {
				ParameterMapping parameterMapping = parameterMappings.get(i);
				if (parameterMapping.getMode() != ParameterMode.OUT) {
					Object value;
					String propertyName = parameterMapping.getProperty();
					PropertyTokenizer prop = new PropertyTokenizer(propertyName);
					if (parameterObject == null) {
						value = null;
					} else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
						value = parameterObject;
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						value = boundSql.getAdditionalParameter(propertyName);
					} else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX)
							&& boundSql.hasAdditionalParameter(prop.getName())) {
						value = boundSql.getAdditionalParameter(prop.getName());
						if (value != null) {
							value = configuration.newMetaObject(value).getValue(
									propertyName.substring(prop.getName().length()));
						}
					} else {
						value = metaObject == null ? null : metaObject.getValue(propertyName);
					}
					TypeHandler typeHandler = parameterMapping.getTypeHandler();
					if (typeHandler == null) {
						throw new ExecutorException("There was no TypeHandler found for parameter "
								+ propertyName + " of statement " + mappedStatement.getId());
					}
					typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
				}
			}
		}
	}

	/**
	 * 根据数据库方言，生成特定的分页sql
	 * 
	 * @param sql
	 * @param page
	 * @return
	 */
	private String generatePageSql(String sql, Pager page) {
		if (page != null && !StringUtil.isEmpty(dialect)) {
			StringBuffer pageSql = new StringBuffer();
			if ("oracle".equals(dialect)) {
				pageSql.append("select * from (select tmp_tb.*,ROWNUM row_id from (");
				pageSql.append(sql);
				pageSql.append(") tmp_tb where ROWNUM<=");
				pageSql.append((page.getCurrentPage() - 1) * page.getPageSize()
						+ page.getPageSize());
				pageSql.append(") where row_id>");
				pageSql.append((page.getCurrentPage() - 1) * page.getPageSize());
			}
			return pageSql.toString();
		} else {
			return sql;
		}
	}

	/**
	 * 分解SQL语句
	 * 
	 * @param sql
	 * @return
	 */
	public Map<String, String> resolve(String sql) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		try {
			int begin = sql.indexOf("(");
			int end = sql.indexOf(")");
			String subSql = sql.substring(begin + 1, end);
			String str[] = StringUtil.split(subSql, ";");
			if (str != null && str.length != 4) {
				log.error("输入参数不符合规则！");
			}
			map.put("tableName", str[0].replace("\"", ""));
			map.put("matchField", str[1].replace("\"", ""));
			map.put("tableField", str[2].replace("\"", ""));
			map.put("filterField", str[3].replace("\"", ""));
			map.put("requireField", sql.substring(0, sql.indexOf("func")));
		} catch (Exception e) {
			log.error("程序出错！");
		}
		return map;
	}

	public Object plugin(Object arg0) {
		return Plugin.wrap(arg0, this);
	}

	public void setProperties(Properties p) {
		dialect = p.getProperty("dialect");
		if (StringUtil.isEmpty(dialect)) {
			try {
				throw new PropertyException("dialect property is not found!");
			} catch (PropertyException e) {
				e.printStackTrace();
			}
		}
		pageSqlId = p.getProperty("pageSqlId");
		if (StringUtil.isEmpty(pageSqlId)) {
			try {
				throw new PropertyException("pageSqlId property is not found!");
			} catch (PropertyException e) {
				e.printStackTrace();
			}
		}
		/*
		 * scopeId = p.getProperty("scopeId"); if (StringUtil.isEmpty(scopeId))
		 * { try { throw new
		 * PropertyException("scopeId property is not found!"); } catch
		 * (PropertyException e) { e.printStackTrace(); } }
		 */
	}

}
