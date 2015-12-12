package com.demo.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.Cookie;

import com.hty.util.fileuploader.FileUpload;

public class Uploader extends FileUpload {
	
	@Override
	public void initConfig() {
		// TODO Auto-generated method stub
		super.initConfig();
		this.maxUploadSize = 1073741824;
		Cookie[] c = request.getCookies();
		
		System.out.println("cookie大小："+c.length);
	}
	
	@Override
	public boolean onFileField(String filename) {
		// TODO Auto-generated method stub
		System.out.println("收到文件：" + filename);
		file = new File("D:/" + filename);
		try {
			ops = new FileOutputStream(file);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean onTextField(String paraName, String paraValue) {
		// TODO Auto-generated method stub
		System.out.println(paraName + "=" + paraValue);
		return true;
	}
	
	@Override
	public void onRequestInputStreamInterrupt() throws IOException {
		// TODO Auto-generated method stub
		super.onRequestInputStreamInterrupt();
	}
}
