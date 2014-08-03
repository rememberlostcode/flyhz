
package com.flyhz.avengers.framework.common.event;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.flyhz.avengers.framework.AvengersExecutor;
import com.flyhz.avengers.framework.common.dto.PageRowDataDto;
import com.flyhz.avengers.framework.config.TemplateConfig;
import com.flyhz.avengers.framework.config.XConfiguration;
import com.flyhz.avengers.framework.lang.AVTable.AVColumn;
import com.flyhz.avengers.framework.lang.AVTable.AVFamily;

public class ContentAnalyzeEvent extends AbstractPageEvent {

	@Override
	Set<AVColumn> getColumns() {
		Set<AVColumn> columns = new HashSet<AVColumn>();
		columns.add(AVColumn.url);
		columns.add(AVColumn.bid);
		columns.add(AVColumn.e);
		columns.add(AVColumn.r);
		columns.add(AVColumn.c);
		return columns;
	}

	@SuppressWarnings("unchecked")
	@Override
	void processRowData(Result result, Map<String, Object> context, int index) throws Throwable {
		byte[] family = Bytes.toBytes(AVFamily.i.name());
		byte[] columnUrl = Bytes.toBytes(AVColumn.url.name());
		byte[] columnBid = Bytes.toBytes(AVColumn.bid.name());
		byte[] columnE = Bytes.toBytes(AVColumn.e.name());
		byte[] columnR = Bytes.toBytes(AVColumn.r.name());
		byte[] columnC = Bytes.toBytes(AVColumn.c.name());
		Long batchId = (Long) context.get(AvengersExecutor.BATCH_ID);
		String url = Bytes.toString(result.getValue(family, columnUrl));
		Long id = Bytes.toLong(result.getRow());
		String encoding = Bytes.toString(result.getValue(family, columnE));
		String root = Bytes.toString(result.getValue(family, columnR));
		String content = Bytes.toString(result.getValue(family, columnC));
		log.info("{} processRowData is start:url > {},id > {},batchId >{},encoding > {},root > {}",
				getClass(), url, id, batchId, encoding, root);
		if (batchId.equals(Bytes.toLong(result.getValue(family, columnBid)))) {
			PageRowDataDto pageRowDataDto = new PageRowDataDto();
			pageRowDataDto.setId(id);
			pageRowDataDto.setBatchId(batchId);
			pageRowDataDto.setEncoding(encoding);
			pageRowDataDto.setRoot(root);
			pageRowDataDto.setUrl(url);
			pageRowDataDto.setContent(content);
			Map<String, Object> domainMap = (Map<String, Object>) context.get(pageRowDataDto.getRoot());
			List<TemplateConfig> templateConfigs = (List<TemplateConfig>) domainMap.get(XConfiguration.DOMAIN_TEMPLATES);
			for (TemplateConfig templateConfig : templateConfigs) {
				if (StringUtils.isNotBlank(templateConfig.getPattern())) {
					if (StringUtils.isNotBlank(pageRowDataDto.getUrl())) {
						// 校验URL是否匹配正则表达式
						if (Pattern.compile(templateConfig.getPattern())
									.matcher(pageRowDataDto.getUrl()).find()) {
							templateConfig.getTemplate().apply(pageRowDataDto, context);
						}
					}
				}
			}
		}

	}
}
