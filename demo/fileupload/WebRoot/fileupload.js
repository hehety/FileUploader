/**
 * 文件上传公共类库
 * 上传文件的表单均需要在此js中注册事件
 */

var form_id;
var watcher;//Interval:监视表单的文件数量，当文件数量大于0时提交表单
var listener;//Interval:监视上传进度
var fileList;//文件列表
var ua;//浏览器类型
var fileinput;//文件input
var file_form;
var progress;
var eachSize = new Array();
var fireEvent = false;//上传时为true，避免再次点击文件对话框按钮
var fetchStatusURL;
var sessionAttrBuilderURL;
var fileUploadURL;



$(document).ready(function(){
	ua = navigator.userAgent.toLowerCase();
});
/**
 * 上传之前调用函数
 */
function beforeUpload(){
	//alert("上传之前");
}
/**
 * 上传之后调用函数
 */
function finishUpload(jsn){
	//alert("上传之后");
	
}
/**
 * 上传错误调用函数
 */
function errorUpload(jsn){
	//alert("上传错误");
}
/**
 * 上传过程调用
 * @param jsn
 */
function flushUpload(jsn){
	//$("body").append(jsn.readed + "/" + jsn.total +"<br>");
}
/**
 * 随机生成form_id
 */
function createRadomFormId(){
	var r1 = Math.floor(Math.random() * 10);
	var r2 = Math.floor(Math.random() * 10);
	var r3 = Math.floor(Math.random() * 10);
	var r4 = Math.floor(Math.random() * 10);
	var r5 = Math.floor(Math.random() * 10);
	return r1 + "" + r2 + "" + r3 + "" + r4 + "" + r5;
}



/**
 * 监视表单的文件数量，当文件数量大于0时提交表单
 */
function watcherFileInput(){
	if(fileinput.value != ""){
		window.clearInterval(watcher);
		fileList = new Array();
		$(progress).html("");
		if(ua.match(/^.*msie [5-9].0.*$/)){
			//alert("低版本的浏览器");
			var str = new String(fileinput.value);
			fileList.push(str.substring(str.lastIndexOf("\\") + 1));
			eachSize.push(fileinput.length);
			//alert(fileinput.size);
			for(var i=0;i<fileList.length;i++){
				$(progress).append("<li>" +
						"<p class='_fileupload_filenames'>"+fileList[i]+"</p>" +
						"<div class='_fileupload_progressWrapper'>" +
						"<div class='progressValue' style='width:0'></div>" +
						"</div>" +
					"</li>");
			}
			$(progress).append("<li class='_fileupload_status'>" +
					"<p>正在准备...</p>" +
				"</li>");
		}else{
			for(var j=0;j<fileinput.files.length;j++){
				var str = new String(fileinput.files[j].name);
				fileList.push(str.substring(str.lastIndexOf("\\") + 1));
				eachSize.push(fileinput.files[j].size);
			}
			for(var i=0;i<fileList.length;i++){
				$(progress).append("<li>" +
										"<p class='_fileupload_filenames'>"+fileList[i]+"</p>" +
										"<div class='_fileupload_progressWrapper'>" +
										"<div class='progressValue' style='width:0'></div>" +
										"</div>" +
									"</li>");
			}
			$(progress).append("<li class='_fileupload_status'>" +
					"<p>正在准备...</p>" +
				"</li>");
		}
		subform();
	}
}
/**
 * 延时监听
 */
function delayListen(){
	listener = window.setInterval("startlistener()", 500);
}

/**
 * 上传进度监听器<br>
 * 此处需要设置监听上传进度服务的URL
 */
function startlistener(){
	$.ajax({
		type:"post",
		url:fetchStatusURL,
		timeout:1000,
		data:{
			fi:form_id
		},
		complete:function(XMLHttpRequest, status){
			if(status == "success"){
				var jsn = eval("("+XMLHttpRequest.responseText+")");
				//$("body").append(XMLHttpRequest.responseText+"<br>");
				if(jsn.state == "null"){
					window.clearInterval(listener);
					resetForm();
					fireEvent = false;
					return;
				}
				if(jsn.state == "finish"){
					window.clearInterval(listener);
					if(ua.match(/^.*msie [5-9].0.*$/)){
						$(progress).html("");
						for(var i=0;i<fileList.length;i++){
							$(progress).append("<li><p class='_fileupload_filenames'>"+fileList[i]+"</p><div class='_fileupload_progressWrapper'><div class='progressValue'></div></div></li>");
							$(progress).append("<li class='_fileupload_status'>" +
									"<p>上传成功!</p>" +
								"</li>");
						}
					}
					else{
						for(var i=0;i<fileList.length;i++){
							$(progress).children().eq(i).children().eq(1).children().eq(0).animate({"width":"100%"}, 500);
							$(progress).children().eq(i).children().eq(1).children().eq(0).css("background","#6AAD1A");
						}
						$(progress).children().eq($(progress).children().length - 1).children().eq(0).text("上传成功!");
					}
					resetForm();
					fireEvent = false;
					return;
				}
				if(jsn.state == "uploading"){
					if(ua.match(/^.*msie [5-9].0.*$/)){
						$(progress).html("");
						for(var i=0;i<fileList.length;i++){
							if(jsn.fileindex > (i+1)){
								$(progress).append("<li><p class='_fileupload_filenames'>"+fileList[i]+"</p><div class='_fileupload_progressWrapper'><div class='progressValue'></div></div></li>");
							}
							else if(jsn.fileindex == (i+1)){
								$(progress).append("<li><p class='_fileupload_filenames'>"+fileList[i]+"</p><div class='_fileupload_progressWrapper'><div class='progressValue' style='background:#1688EF;width:"+thisFileUpload(jsn.fileindex, jsn.readed, eachSize[i], jsn.total)+"%'></div></div></li>");
							}else if(jsn.fileindex < (i+1)){
								$(progress).append("<li><p class='_fileupload_filenames'>"+fileList[i]+"</p><div class='_fileupload_progressWrapper'><div class='progressValue' style='width:0%'></div></div></li>");
								
							}
							$(progress).append("<li class='_fileupload_status'>" +
									"<p>上传中("+jsn.fileindex+"/"+fileList.length+") : "+ Math.ceil(jsn.readed/jsn.total*100) +"%</p>" +
								"</li>");
						}
					}
					else{
						for(var i=0;i<fileList.length;i++){
							if(jsn.fileindex > (i+1)){
								$(progress).children().eq(i).children().eq(1).children().eq(0).animate({"width": "100%"},500);
								$(progress).children().eq(i).children().eq(1).children().eq(0).css("background","#6AAD1A");
							}
							else if(jsn.fileindex == (i+1)){
								var percent = thisFileUpload(jsn.fileindex, jsn.readed, eachSize[jsn.fileindex-1], jsn.total);
								$(progress).children().eq(jsn.fileindex-1).children().eq(1).children().eq(0).animate({"width": (percent + "%")}, 500,"linear");
								$(progress).children().eq(jsn.fileindex-1).children().eq(1).children().eq(0).css("background", "#1688EF");
							}
						}
						$(progress).children().eq($(progress).children().length - 1).children().eq(0).text("上传中("+jsn.fileindex+"/"+fileList.length+") : " + Math.ceil(jsn.readed/jsn.total*100) + "%");
					}
					return;
				}else if(jsn.state == "error"){
					window.clearInterval(listener);
					resetForm();
					fireEvent = false;
					
					if(ua.match(/^.*msie [5-9].0.*$/)){
						$(progress).html("");
						for(var i=0;i<fileList.length;i++){
							if(jsn.fileindex > (i+1)){
								$(progress).append("<li><p class='_fileupload_filenames'>"+fileList[i]+"</p><div class='_fileupload_progressWrapper'><div class='progressValue'></div></div></li>");
							}
							else if(jsn.fileindex == (i+1)){
								$(progress).append("<li><p class='_fileupload_filenames'>"+fileList[i]+"</p><div class='_fileupload_progressWrapper'><div class='progressValue' style='background:red;width:"+thisFileUpload(jsn.fileindex, jsn.readed, eachSize[i], jsn.total)+"%'></div></div></li>");
							}else if(jsn.fileindex < (i+1)){
								$(progress).append("<li><p class='_fileupload_filenames'>"+fileList[i]+"</p><div class='_fileupload_progressWrapper'><div class='progressValue' style='width:0%'></div></div></li>");
								
							}
							$(progress).append("<li class='_fileupload_status'>" +
									"<p>上传错误 : " + jsn.errorMsg + "</p>" +
								"</li>");
						}
					}
					else{
						$(progress).children().eq(jsn.fileindex-1).children().eq(1).children().eq(0).css("background", "red");
						$(progress).children().eq($(progress).children().length - 1).children().eq(0).text("上传错误 : " + jsn.errorMsg);
					}
				}
			}
		}
	});
}


/**
 * 计算当前上传文件的百分比
 * @param index 第几个文件
 * @param readed 已上传
 * @param thissize 当前文件大小
 * @param total 表单文件总大小
 * @returns 当前文件的上传百分比
 */
function thisFileUpload(index, readed, thissize, total){
	var finishsize = 0;
	if(ua.match(/^.*msie [5-9].0.*$/)){
		return Math.ceil(readed/total*100);
	}else{
		for(var i=0;i<index-1;i++){
			finishsize+=eachSize[i];
		}
		return Math.ceil((readed - finishsize)/eachSize[index-1]*100);
	}
	
}
/**
 * 重置表单数据
 */
function resetForm(){
	fileList = new Array();
	fileinput.form.reset();
	eachSize = new Array();
}
/**
 * 提交表单
 */
function subform(){
	fireEvent = true;
	form_id = createRadomFormId();//为表单生成form_id
	$("#form_id").remove();
	$(file_form).prepend("<input type='hidden' id='form_id' name='form_id' value='"+form_id+"'>");
	sab();
}
/**
 * 调用接口服务, 上传之前在服务器创建上传状态会话
 * 如果服务器响应成功, 则开始上传, 否则报出异常
 */
function sab(){
	$.ajax({
		type:"GET",
		url:sessionAttrBuilderURL,
		timeout:3000,
		success:function(XMLHttpRequest, status){
			$(file_form).submit();
			window.setTimeout("delayListen()",500);
		},
		error:function(XMLHttpRequest){
			window.clearInterval(listener);
			resetForm();
			fireEvent = false;
			if(ua.match(/^.*msie [5-9].0.*$/)){
				$(progress).html("");
				for(var i=0;i<fileList.length;i++){
					if(jsn.fileindex > (i+1)){
						$(progress).append("<li><p class='_fileupload_filenames'>"+fileList[i]+"</p><div class='_fileupload_progressWrapper'><div class='progressValue'></div></div></li>");
					}
					else if(jsn.fileindex == (i+1)){
						$(progress).append("<li><p class='_fileupload_filenames'>"+fileList[i]+"</p><div class='_fileupload_progressWrapper'><div class='progressValue' style='background:red;width:"+thisFileUpload(jsn.fileindex, jsn.readed, eachSize[i], jsn.total)+"%'></div></div></li>");
					}else if(jsn.fileindex < (i+1)){
						$(progress).append("<li><p class='_fileupload_filenames'>"+fileList[i]+"</p><div class='_fileupload_progressWrapper'><div class='progressValue' style='width:0%'></div></div></li>");
						
					}
					$(progress).append("<li class='_fileupload_status'>" +
							"<p>服务器无响应!</p>" +
						"</li>");
				}
			}
			else{
				$(progress).children().eq($(progress).children().length - 1).children().eq(0).text("服务器无响应!");
			}
		}
	});
}


/**
 * 
 * @param _fileinput 文件input按钮
 * @param _progress 显示文件上传状态容器
 * @param _fileUploadURL 文件上传地址
 * @param _fetchStatusURL 上传状态获取地址
 * @param _sessionAttrBuilderURL 创建文件上传会话地址 
 */
function fileupload(_fileinput, 
					_progress, 
					_fileUploadURL,
					_fetchStatusURL, 
					_sessionAttrBuilderURL){
	file_form = _fileinput.form;
	fileinput = _fileinput;
	progress = _progress;
	fileUploadURL = _fileUploadURL;
	fetchStatusURL = _fetchStatusURL;
	sessionAttrBuilderURL = _sessionAttrBuilderURL;
	
	_fileinput.form.action = _fileUploadURL;
	//打开文件对话框
	$(fileinput).click(function(e){
		if(fireEvent){
			return false;
		}
		window.clearInterval(watcher);
		$(this).val("");
		watcher = window.setInterval("watcherFileInput()", 100);
	});
	
}
