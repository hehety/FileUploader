/**
 * �ļ��ϴ��������
 * �ϴ��ļ��ı�����Ҫ�ڴ�js��ע���¼�
 */

var form_id;
var watcher;//Interval:���ӱ����ļ����������ļ���������0ʱ�ύ��
var listener;//Interval:�����ϴ�����
var fileList;//�ļ��б�
var ua;//���������
var fileinput;//�ļ�input
var file_form;
var progress;
var eachSize = new Array();
var fireEvent = false;//�ϴ�ʱΪtrue�������ٴε���ļ��Ի���ť
var fetchStatusURL;
var sessionAttrBuilderURL;
var fileUploadURL;



$(document).ready(function(){
	ua = navigator.userAgent.toLowerCase();
});
/**
 * �ϴ�֮ǰ���ú���
 */
function beforeUpload(){
	//alert("�ϴ�֮ǰ");
}
/**
 * �ϴ�֮����ú���
 */
function finishUpload(jsn){
	//alert("�ϴ�֮��");
	
}
/**
 * �ϴ�������ú���
 */
function errorUpload(jsn){
	//alert("�ϴ�����");
}
/**
 * �ϴ����̵���
 * @param jsn
 */
function flushUpload(jsn){
	//$("body").append(jsn.readed + "/" + jsn.total +"<br>");
}
/**
 * �������form_id
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
 * ���ӱ����ļ����������ļ���������0ʱ�ύ��
 */
function watcherFileInput(){
	if(fileinput.value != ""){
		window.clearInterval(watcher);
		fileList = new Array();
		$(progress).html("");
		if(ua.match(/^.*msie [5-9].0.*$/)){
			//alert("�Ͱ汾�������");
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
					"<p>����׼��...</p>" +
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
					"<p>����׼��...</p>" +
				"</li>");
		}
		subform();
	}
}
/**
 * ��ʱ����
 */
function delayListen(){
	listener = window.setInterval("startlistener()", 500);
}

/**
 * �ϴ����ȼ�����<br>
 * �˴���Ҫ���ü����ϴ����ȷ����URL
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
									"<p>�ϴ��ɹ�!</p>" +
								"</li>");
						}
					}
					else{
						for(var i=0;i<fileList.length;i++){
							$(progress).children().eq(i).children().eq(1).children().eq(0).animate({"width":"100%"}, 500);
							$(progress).children().eq(i).children().eq(1).children().eq(0).css("background","#6AAD1A");
						}
						$(progress).children().eq($(progress).children().length - 1).children().eq(0).text("�ϴ��ɹ�!");
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
									"<p>�ϴ���("+jsn.fileindex+"/"+fileList.length+") : "+ Math.ceil(jsn.readed/jsn.total*100) +"%</p>" +
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
						$(progress).children().eq($(progress).children().length - 1).children().eq(0).text("�ϴ���("+jsn.fileindex+"/"+fileList.length+") : " + Math.ceil(jsn.readed/jsn.total*100) + "%");
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
									"<p>�ϴ����� : " + jsn.errorMsg + "</p>" +
								"</li>");
						}
					}
					else{
						$(progress).children().eq(jsn.fileindex-1).children().eq(1).children().eq(0).css("background", "red");
						$(progress).children().eq($(progress).children().length - 1).children().eq(0).text("�ϴ����� : " + jsn.errorMsg);
					}
				}
			}
		}
	});
}


/**
 * ���㵱ǰ�ϴ��ļ��İٷֱ�
 * @param index �ڼ����ļ�
 * @param readed ���ϴ�
 * @param thissize ��ǰ�ļ���С
 * @param total ���ļ��ܴ�С
 * @returns ��ǰ�ļ����ϴ��ٷֱ�
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
 * ���ñ�����
 */
function resetForm(){
	fileList = new Array();
	fileinput.form.reset();
	eachSize = new Array();
}
/**
 * �ύ��
 */
function subform(){
	fireEvent = true;
	form_id = createRadomFormId();//Ϊ������form_id
	$("#form_id").remove();
	$(file_form).prepend("<input type='hidden' id='form_id' name='form_id' value='"+form_id+"'>");
	sab();
}
/**
 * ���ýӿڷ���, �ϴ�֮ǰ�ڷ����������ϴ�״̬�Ự
 * �����������Ӧ�ɹ�, ��ʼ�ϴ�, ���򱨳��쳣
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
							"<p>����������Ӧ!</p>" +
						"</li>");
				}
			}
			else{
				$(progress).children().eq($(progress).children().length - 1).children().eq(0).text("����������Ӧ!");
			}
		}
	});
}


/**
 * 
 * @param _fileinput �ļ�input��ť
 * @param _progress ��ʾ�ļ��ϴ�״̬����
 * @param _fileUploadURL �ļ��ϴ���ַ
 * @param _fetchStatusURL �ϴ�״̬��ȡ��ַ
 * @param _sessionAttrBuilderURL �����ļ��ϴ��Ự��ַ 
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
	//���ļ��Ի���
	$(fileinput).click(function(e){
		if(fireEvent){
			return false;
		}
		window.clearInterval(watcher);
		$(this).val("");
		watcher = window.setInterval("watcherFileInput()", 100);
	});
	
}
