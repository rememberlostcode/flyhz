/*
var api = frameElement.api, W = api.opener;
api.button({
    id:'valueOk',
	name:'保存',
	callback:productSave
});
api.button({
	name:'取消',
	callback:true
});
*/

function productSave(){
	var name = $("#name").val();
	var brandstyle = $("#brandstyle").val();
	var foreighprice = $("#foreighprice").val();
	var recommendprice = $("#recommendprice").val();
	if(checkStr('name',name,128) & checkStr('brandstyle',brandstyle,16) 
	    && checkPrice('foreighprice',foreighprice) && checkPrice('recommendprice',recommendprice)){
		if(foreighprice == '请填写数字'){
			 $("#foreighprice").val("");
		}
		if(recommendprice == '请填写数字'){
			 $("#recommendprice").val("");
		}
		$("#addProduct").submit();
	}
}

function checkPrice(type,str){
	//如果提示未填写数字
	if(str.replace(/^\s*/, '') == '请填写数字'){
		return true;
	}
	//如果未填写价格，不校验
	if(str.replace(/^\s*/, '') == ''){
		if(type == 'foreighprice'){
			$("#foreighprice").val("请填写数字");
		}else if(type == 'recommendprice'){
			$("#recommendprice").val("请填写数字");
		}
		return true;
	}
	var message = '';
	var regex = /^[1-9][0-9]{0,7}$|[1-9][0-9]{0,7}[.]\d{1,2}$/;
	if(!regex.test(str)){
		if(type == 'foreighprice'){
			message = '国外价格格式错误!';
		}else if(type == 'recommendprice'){
			message = '推荐价格格式错误!';
		}
	}
	//设置提示信息
	if(message != ''){
		if(type == 'foreighprice'){
			$("#foreighpriceMessage").html(message);
		}else if(type == 'recommendprice'){
			$("#recommendpriceMessage").html(message);
		}
		return false;
	}
	//校验信息无误
	if(type == 'foreighprice'){
		$("#foreighpriceMessage").html(message);
	}else if(type == 'recommendprice'){
		$("#recommendpriceMessage").html(message);
	}
	return true;
}

function checkStr(type,str,len){
	var message = '';
	if(str.replace(/^\s*/, '') == ''){
		if(type == 'name'){
			message = '产品名称不能为空!';
		}else if(type == 'brandstyle'){
			message = '产品款号不能为空!';
		}
	}
	//校验字符串是否过长
	if(str.replace(/^\s*/, '').length > len){
		if(type == 'name'){
			message = '产品名称最长为128个字符!';
		}else if(type == 'brandstyle'){
			message = '产品款号最长为16个字符!';
		}
	}
	//设置提示信息
	if(message != ''){
		if(type == 'name'){
			$("#nameMessage").html(message);
		}else if(type == 'brandstyle'){
			$("#brandstyleMessage").html(message);
		}
		return false;
	}
	//校验信息无误
	if(type == 'name'){
		$("#nameMessage").html(message);
	}else if(type == 'brandstyle'){
		$("#brandstyleMessage").html(message);
	}
	return true;
}

function chooseImg(fileId,filetxtId){
	$('input[id=' + fileId + ']').click();
	$('input[id=' + fileId + ']').change(function() {
       $('#'+ filetxtId).val($(this).val());
    });
}

function closeAddPage(){
	var api = frameElement.api, W = api.opener; 
	api.close();
}

function reChooseImg(){
	$("#showColorImg").empty();//删除图片
	$("#oldColorimg").val("");//清除旧图片路径
	var addHtml = '<input id="colorimg" name="colorimg" type="file" style="display:none">';
	addHtml += '<div><input id="colorimgCover" type="text" class="filetxtweidth1">';
	addHtml += '<input type="button" onclick="javascript:chooseImg(\'colorimg\',\'colorimgCover\');" value="选择图片"/></div>';
	$("#showColorImg").append(addHtml);
}

function addImgsUpload(tdId){
	var lastId = $('div[id^=div_imgs]').last().attr("id");
	var imgsLength =  parseInt(lastId.substring(8,lastId.length)) + 1;
	var addHtml = '<div id="div_imgs' + imgsLength + '" class="divImgs">';
	addHtml += '<input id="imgs' + imgsLength + '" name="imgs" type="file" style="display:none">';
	addHtml += '<input id="imgs' + imgsLength + 'Cover" type="text" class="filetxtweidth">';
	addHtml += '&nbsp;';
	addHtml += '<input type="button" onclick="javascript:chooseImg(\'imgs' + imgsLength + '\',\'imgs' + imgsLength + 'Cover\');" value="选择图片"/>';
	addHtml += '&nbsp;';
	addHtml += '<input type="button" onclick="javascript:delImgsUpload(\'div_imgs' + imgsLength + '\');" value="删  除"/>';
	addHtml += '</div>';
	//$("#" + tdId).after(addHtml);
	$("#productImgs").append(addHtml);
}

function delImgsUpload(divId){
	$("#" + divId).remove();
}

function delSrcImgs(divId,count){
	$("#" + divId).remove();
	if(count == '0'){
		$("#coverSmallDel").val("1");
	}
	var length = $("tr[id^=tr_srcImg_]").length + 1;
	$("#rowProductImg").attr("rowspan",length);
}

//校验产品图片上传时是否大于100K
function checkImgLength(){
	var files = $("input[type=file][id^=imgs]");
	var flag = true;
	if(files.length > 0){
		files.each(function(){
			var filepath=$(this).val();
			var id = $(this).attr("id");
			alert(filepath);
			alert(id);
			if(filepath != null && filepath != ''){
				var img = new Image();
			    img.src = filepath;  
				while(true){
				   if(img.fileSize < 1024){
				   		flag = false;
				   		//增加提示信息
				   		$("#div_" + id).append("&nbsp;&nbsp;<font color=\"red\">图片不能小于100K</font>");
				   }
			    }
			}
		});
	} 
	return flag;
}