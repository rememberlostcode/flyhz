/*****
 * 
 * @param {} url 后面只能.action结束
 * @param {} names 每一个name都是以,隔开
 * @param {} value 每一个valeu,隔开
 * name 和value必须要一对一对应
 */
var oform_id = 1;
function PostWindowsUrl(url, names, values) {
	oform_id++;
	var oForm = document.createElement("form");
	oForm.id = "testid_" + oform_id;
	oForm.method = "post";
	oForm.action = url;
	oForm.target = "";
	var value_array = values.split(",");
	var names_array = names.split(",");
	for ( var i = 0; i < values.length; i++) {
		var hasitemsids_input = document.createElement("input");
		hasitemsids_input.type = "hidden";
		hasitemsids_input.name = names_array[i];
		hasitemsids_input.value = value_array[i];
		if (hasitemsids_input.name != "undefined") {
			oForm.appendChild(hasitemsids_input);
		}
	}
	document.body.appendChild(oForm);
	oForm.submit();
}

/*  
 * 
 * @param {} url 
 * @param {} names 每一个name都是以,隔开
 * @param {} value 每一个valeu,隔开
 * name 和value必须要一对一对应
 */
var oform_id = 1;
function GetWindowsUrl(url, names, values) {
	oform_id++;
	var oForm = document.createElement("form");
	oForm.id = "testid_" + oform_id;
	oForm.method = "get";
	oForm.action = url;
	oForm.target = "";
	var value_array = values.split(",");
	var names_array = names.split(",");
	for ( var i = 0; i < values.length; i++) {
		var hasitemsids_input = document.createElement("input");
		hasitemsids_input.type = "hidden";
		hasitemsids_input.name = names_array[i];
		hasitemsids_input.value = value_array[i];
		if (hasitemsids_input.name != "undefined") {
			oForm.appendChild(hasitemsids_input);
		}
	}
	document.body.appendChild(oForm);
	oForm.submit();
}