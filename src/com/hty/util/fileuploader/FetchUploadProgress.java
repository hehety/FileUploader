package com.hty.util.fileuploader;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * �ļ��ϴ�״̬��ȡ��
 */
public class FetchUploadProgress  {

	@SuppressWarnings("unchecked")
	public static void getProgress(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Pragma", "No-cache");//������Ӧͷ��Ϣ�������������Ҫ���������
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
        response.setCharacterEncoding("UTF-8");
		String form_id = request.getParameter("fi");
		Map<String, UploadStatus> uploadStatusList = (Map<String, UploadStatus>) request.getSession().getAttribute("uploadStatusList");
		PrintWriter out = response.getWriter();
		if(null == uploadStatusList || uploadStatusList.isEmpty()){
			UploadStatus status = new UploadStatus();
			status.setState(UploadStatus.NULL);
			//��д
			out.write(status.toString());
			out.flush();
			out.close();
			return;
		}
		UploadStatus status = uploadStatusList.get(form_id);
		if(uploadStatusList.get(form_id)==null){
			status = new UploadStatus();
			status.setState(UploadStatus.NULL);
			//��д
			out.write(status.toString());
			out.flush();
			out.close();
		}else{
			out.write(status.toString());
			out.flush();
			out.close();
		}
	}
}
