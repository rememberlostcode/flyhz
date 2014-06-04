/**
 * 提示登录超时或者未登录
 */
function showLogin(){
	showMessage('登录超时或者未登录，请登录后重试！');
}
/**
 * 提示权限提示框
 */
function showRight(){
	showMessage('抱歉，您没有此操作的权限！');
}
/**
 * 提示成功提示框
 */
function showSuccess(){
	jQuery.dialog.tips('操作成功！'); 
}

/**
 * 提示错误提示框
 */
function showFail(){
	jQuery.dialog.tips('抱歉，操作失败，请稍后重试！'); 
}

/**
 * 提示成功提示框
 */
function showMessage(msg){
	jQuery.dialog.tips(msg); 
}
/**
 * 全选或者全不选,obj是checkbox本身，用this传入；使用方法：onclick="allCheck(this)"
 * @param obj
 */
function allCheck(obj){
	if(obj.checked){
		all_select("checkbox","qx");
	} else {
		all_select("checkbox","bx");
	}
}
/**
 * @param obj 复选框ID
 * @param flag 选择标记 空或者fx=反选 qx=全选 bx=不选 qx1=待定的全选，实际实现的是反选功能（主要用在那个红色的全选字样上的单击事件）
 * @作用 复选框选择功能
 */
function all_select(obj,flag){
	if(flag == "" || flag == "fx"){
		for(var i=0; i<document.getElementsByName(obj).length; i++){
			if(document.getElementsByName(obj)[i].checked){
				document.getElementsByName(obj)[i].checked = false;
			}else{
				document.getElementsByName(obj)[i].checked = true;
			}
		}
	}else if(flag == "qx"){
		for(var i=0; i<document.getElementsByName(obj).length; i++){
			document.getElementsByName(obj)[i].checked = true;
		}
	}else if(flag == "qx1"){  //名为全选一，功能实现为反选功能
		var isqx = "yes";
		for(var i=0; i<document.getElementsByName(obj).length; i++){
			if(! document.getElementsByName(obj)[i].checked){
				isqx = "no";
				break;
			}
		}
		if(isqx == "yes"){
			for(var j=0; j<document.getElementsByName(obj).length; j++){
				document.getElementsByName(obj)[j].checked = false;
			}
		}else{
			for(var j=0; j<document.getElementsByName(obj).length; j++){
				document.getElementsByName(obj)[j].checked = true;
			}
		}
	}else{
		for(var i=0; i<document.getElementsByName(obj).length; i++){
			document.getElementsByName(obj)[i].checked = false;
		}
	}
}

/**
 * 获得选中的checkbox的值，值以井号（#）分开
 * @returns {String}
 */
function getAllCheckboxIds(){
	var checks = document.getElementsByName("checkbox");
	var ids = '';
	for(var i=0,length=checks.length;i<length;i++){
		if(checks[i].checked){
			if(ids==''){
				ids = checks[i].value;
			} else {
				ids += '#' + checks[i].value;
			}
		}
	}
	return ids;
}
/**
 * 获得选中的checkbox的值，值以逗号（,）分开
 * @returns {String}
 */
function getAllCheckboxIdsNew(){
	var checks = document.getElementsByName("checkbox");
	var ids = '';
	for(var i=0,length=checks.length;i<length;i++){
		if(checks[i].checked){
			if(ids==''){
				ids = checks[i].value;
			} else {
				ids += ',' + checks[i].value;
			}
		}
	}
	return ids;
}

/**
 * @作用　页面跳转
 * @param url 跳转页面的url
 */
function code_save(url){
	window.location = url;
}

function goback(){
	history.go(-1);
}