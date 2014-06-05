var appServlet = '';
/** ***********登录部分******************** */
function fnShowLogin() {
	var h = fnGetWindowHeight();
	var w = fnGetWindowWidth();
	if (!document.getElementById('divDialogBg')) {
		var div = document.createElement('div');
		div.id = 'divDialogBg';
		div.style.backgroundColor = 'black';
		div.style.position = 'absolute';
		div.style.visibility = 'visible';
		div.style.filter = 'alpha(opacity=50)';
		div.style.opacity = '.50';
		div.style.zIndex = 100001;
		div.style.left = 0;
		div.style.top = 0;
		div.style.width = w + 'px';
		div.style.height = h + 'px';
		document.body.appendChild(div);
	}
	if(!document.getElementById('divLoginDialog')){
		var divLogin = document.createElement('div');
		divLogin.id = 'divLoginDialog';
		divLogin.style.left = (w / 2 - 150) + 'px';
		divLogin.style.top = (h / 2 - 100) + 'px';
		divLogin.style.position = 'absolute';
		divLogin.style.zIndex = 100002;
		divLogin.style.width = '259px';
		divLogin.style.height = 'auto';
		var loginHtml = '<div class="LoginNavT"></div><div class="LoginNavD"><div style="text-align:center; line-height:200%;display:none;position:absolute;top:70px;left:106px;" id="loginImg"><img src="'+baseUrl+'/static/css/logo/loading.gif"><br />正在登录...</div><div id="loginTable"><h3>用户登录</h3>';
		loginHtml += '<form action="' + baseUrl + '/loginAuth" target="_self" method="post" id="theform">'+
	    '<input type="hidden" name="issubmit" value="true" />'+
	    '<p style="line-height:30px">用户名：<input id="username" name="username" type="text" onkeypress="if(event.keyCode==13){document.getElementById("password").focus(); return false;}"/></p>'+
	    '<p style="line-height:30px">密　码：<input id="password" name="password" type="password" /></p>'+
	    '<p style="line-height:30px">验证码：<input id="yzmcode" type="text" style="width:50px;"   /></label><input type="button" id="checkCode" class="code" style="width:60px" onClick="createCode()" /> <a href="javascript:createCode();"><img title="单击换一个" id="imgVerifyCode" style="cursor:pointer" alt="换一个" /></a><br/></p>'+
	    '<p style="line-height:30px">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<button type="button" name="savedl" onclick="return loginclose()">登 录</button></p></form></div>';
		document.body.appendChild(divLogin);
	    //IE6的延时加载
	    setTimeout(function(){document.getElementById('divLoginDialog').innerHTML=loginHtml;},1);
	}
}
//判断是否是数字
function checkCode(obj) {
	var reg = new RegExp("^[0-9]*$");
    if(!reg.test(obj.value)){
    	try{
    		obj.value = obj.value.substring(0,obj.value.length-1);
    	}catch(e){
    	
    	}
    }
}

function fnLogin(){
    var sUserName = document.getElementById('txtUserName').value;
    var sPassword = document.getElementById('txtPassword').value;
    var sVerifyCode = document.getElementById('txtVerifyCode').value;
    var isInvalidate= document.getElementById('chkInvalidate').checked;
    
    //document.getElementById('txtUserName').value = '';
    document.getElementById('txtPassword').value = '';
    document.getElementById('txtVerifyCode').value = '';
    document.getElementById('chkInvalidate').checked = false;
    
    
    if(sUserName.length < 1 || sPassword.length < 1){
        document.getElementById('divLoginResultMsg').style.display = 'block';
        document.getElementById('divLoginResultMsg').innerHTML = '用户名或者密码不能为空！';
        document.getElementById('loginImg').style.display = 'none';
        document.getElementById('loginTable').style.display = 'block';
        document.getElementById('coloeImg').style.display = 'block';
        return;
    }
    
 	setTimeout(function(){document.getElementById('divLoginResultMsg').style.display='none' ;document.getElementById('loginTable').style.display = 'none';document.getElementById('loginImg').style.display = 'block';document.getElementById('coloeImg').style.display ='none'},10);
 	
 	var url = baseUrl + "/loginAuth";
 	jQuery.post(url,{
		"username":sUserName,
		"password":sPassword,
		"verifycode":sVerifyCode,
		"isInvalidate":isInvalidate
	},function(data){
		if(data=='verifycode'){
			setTimeout(function(){document.getElementById('loginTable').style.display = 'block';document.getElementById('loginImg').style.display = 'none';document.getElementById('coloeImg').style.display = 'block';},10);
			document.getElementById('divLoginResultMsg').style.display = 'block';
			document.getElementById('divLoginResultMsg').innerHTML = '验证码错误。';
			return;
		} else if(data=='error'){
			setTimeout(function(){document.getElementById('loginTable').style.display = 'block';document.getElementById('loginImg').style.display = 'none';document.getElementById('coloeImg').style.display = 'block';},10);
			document.getElementById('divLoginResultMsg').style.display = 'block';
			document.getElementById('divLoginResultMsg').innerHTML = '请确认密码和用户名是否正确。';
			return;
		} else if(data=='success'){
			window.location.reload();
		} else {
			setTimeout(function(){document.getElementById('loginTable').style.display = 'block';document.getElementById('loginImg').style.display = 'none';document.getElementById('coloeImg').style.display = 'block';},10);
			document.getElementById('divLoginResultMsg').style.display = 'block';
			document.getElementById('divLoginResultMsg').innerHTML = '登录出现错误，请稍后重试！';
			return;
		}
	});
	return;
}

function fnLoginOut(username){
	if(window.confirm('是否退出平台')){
		var sLoginUrl = baseUrl + "/logout";
		var indexUrl = baseUrl + "/index";
		jQuery.post(sLoginUrl,{"username":username},function(data){
			window.location.reload();
    	});
	}
}

function fnGetWindowHeight(){
	var vh = 0;
	var _dEt = document.documentElement;
	var _dBy = document.body;
	if(typeof window.innerHeight=='number'){
		vh = window.innerHeight;
	}else{
		if(_dEt&&_dEt.clientHeight){
			vh = _dEt.clientHeight;
		}else{
			if(_dBy&&_dBy.clientHeight){
				vh = _dBy.clientHeight;
			}
		}
	}
	if(!vh||vh<100)vh = 100;
	return vh;
}

function fnGetWindowWidth(){
	var vw = 0;
	var _dEt = document.documentElement;
	var _dBy = document.body;
	if(typeof window.innerWidth=='number'){
		vw = window.innerWidth;
	}else{
		if(_dEt&&_dEt.clientWidth){
			vw = _dEt.clientWidth;
		}else{
			if(_dBy&&_dBy.clientWidth){
				vw = _dBy.clientWidth;
			}
		}
	}
	if(!vw||vw<100)vw = 100;
	return vw;
}

function check() {
	if(document.getElementById("username").value=="") {
		alert("请输入用户名！")
		return false;
	}else if(document.getElementById("password").value=="") {
		alert("请输入密码！")
		return false;
    }else {
        return validate();
    }
}

function KeyDown(){
　　if (event.keyCode == 13){
　　　　event.returnValue=false;
　　　　event.cancel = true;
　　　　loginclose();
　　}
}	

function loginclose(){
	if(check()){
	    var theform = document.getElementById('theform');
	    theform.submit();
    }
}

var code ; //在全局 定义验证码
function createCode(){ 
	code = new Array();
	var codeLength = 4;//验证码的长度
	var checkCode = document.getElementById("checkCode");
	checkCode.value = "";
	var selectChar = new Array(2,3,4,5,6,7,8,9,'A','B','C','D','E','F','G','H','J','K','L','M','N','P','Q','R','S','T','U','V','W','X','Y','Z');
	for(var i=0;i<codeLength;i++) {
	   var charIndex = Math.floor(Math.random()*32);
	   code +=selectChar[charIndex];
	}
	if(code.length != codeLength){
	   createCode();
	}
	checkCode.value = code;
	document.getElementById("yzmcode").value = "";
}

function validate() {
	var inputCode = document.getElementById("yzmcode").value.toUpperCase();
	if(inputCode.length <=0) {
	   alert("请输入验证码！");
	   return false;
	}else if(inputCode != code ){
	   alert("验证码输入错误！");
	   return false;
	}
	return true;
}