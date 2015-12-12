package com.hty.util.fileuploader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * ����Session�ػ�����
 * ���û���һ���ϴ�ʱ������ļ��ϴ������漴ʱ����uploadStatusList��������ᵼ�±������ܼ�ʱͬ��
 * ���ͻ��˶�ȡ�ϴ����ȵ�ʱ��ͻ��ȡ����uploadStatusList
 * ���������þ������ϴ�֮ǰ�ɿͻ��˷���һ��ajax���󣬸�������÷�����Ϊ�ػ�����uploadStatusList
 * �����ϴ��ͽ��ȶ�ȡ����ͬʱ���úͶ�ȡuploadStatusList
 * 
 * <small>��������Ի���˴�����</small>
 * @author ������
 * @return ����һ��form_id
 *
 */
public class SessionUploadAttributeBuilder {

	@SuppressWarnings("unchecked")
	public static void build(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("����Session����...");
		response.setHeader("Pragma", "Public");//������Ӧͷ��Ϣ��������������Ի��������
        response.setHeader("Cache-Control", "Public");
		Map<String, UploadStatus> uploadStatusList = (Map<String, UploadStatus>) request.getSession().getAttribute("uploadStatusList");
		if(null == uploadStatusList) 
			uploadStatusList = new HashMap<String, UploadStatus>();//�����ϴ�״̬
		request.getSession().setAttribute("uploadStatusList", uploadStatusList);
		response.getWriter().write("ok");
		response.getWriter().flush();
		response.getWriter().close();
	}
}
