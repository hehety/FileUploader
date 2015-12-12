package com.hty.util.fileuploader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 创建Session回话变量
 * 当用户第一次上传时如果在文件上传类里面即时加入uploadStatusList这个变量会导致变量不能及时同步
 * 当客户端读取上传进度的时候就会读取不到uploadStatusList
 * 这个类的作用就是在上传之前由客户端发送一个ajax请求，该请求会让服务器为回话创建uploadStatusList
 * 这样上传和进度读取就能同时设置和读取uploadStatusList
 * 
 * <small>浏览器可以缓存此次请求</small>
 * @author 何天意
 * @return 返回一个form_id
 *
 */
public class SessionUploadAttributeBuilder {

	@SuppressWarnings("unchecked")
	public static void build(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("创建Session变量...");
		response.setHeader("Pragma", "Public");//设置响应头信息，告诉浏览器可以缓存此内容
        response.setHeader("Cache-Control", "Public");
		Map<String, UploadStatus> uploadStatusList = (Map<String, UploadStatus>) request.getSession().getAttribute("uploadStatusList");
		if(null == uploadStatusList) 
			uploadStatusList = new HashMap<String, UploadStatus>();//保存上传状态
		request.getSession().setAttribute("uploadStatusList", uploadStatusList);
		response.getWriter().write("ok");
		response.getWriter().flush();
		response.getWriter().close();
	}
}
