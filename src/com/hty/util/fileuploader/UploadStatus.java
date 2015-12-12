package com.hty.util.fileuploader;

import java.io.Serializable;

/**
 * 文件上传状态类<br>
 * 客户端调用查询的时候action会生成一个UploadStatus类，包装Session里面的数据
 * 以JSON格式返回
 * @author tisnyi
 *
 */
public class UploadStatus implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 698929460845320214L;
	public static String NULL = "null";
	public static String UPLOADING = "uploading";
	public static String SAVING = "saving";
	public static String FINISH = "finish";
	public static String ERROR = "error";
	
	/**
	 * form_id：客户端上传之前为form添加的随机字符串代表是哪个form
	 */
	private String form_id = "";
	/**
	 * 上传总字节
	 */
	private long total = 0;
	/**
	 * 已上传字节
	 */
	private int readed = 0;
	/**
	 * 当前正在上传第几个文件：default 1
	 */
	private int fileindex = 1;
	/**
	 * 上传状态<br>
	 * 上传状态可以有：<br>
	 * null（没有文件正在上传）、uploading（上传中）、<br>
	 * saving（保存中）、finish（上传完成）和error（上传错误）
	 */
	private String state = NULL;
	/**
	 * 错误信息
	 */
	private String errorMsg = "";
	/**
	 * 即时速度(未开发)
	 */
	private int speed = 0;
	
	public String getForm_id() {
		return form_id;
	}
	public void setForm_id(String form_id) {
		this.form_id = form_id;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public int getReaded() {
		return readed;
	}
	public void setReaded(int readed) {
		this.readed = readed;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getFileindex() {
		return fileindex;
	}
	public void setFileindex(int fileindex) {
		this.fileindex = fileindex;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("'form_id':'"+form_id+"',");
		sb.append("'total':'"+total+"',");
		sb.append("'readed':'"+readed+"',");
		sb.append("'fileindex':'"+fileindex+"',");
		sb.append("'state':'"+state+"',");
		sb.append("'errorMsg':'"+errorMsg+"',");
		sb.append("'speed':'"+speed+"'");
		sb.append("}");
		return sb.toString();
	}
}
