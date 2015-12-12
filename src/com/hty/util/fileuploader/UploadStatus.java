package com.hty.util.fileuploader;

import java.io.Serializable;

/**
 * �ļ��ϴ�״̬��<br>
 * �ͻ��˵��ò�ѯ��ʱ��action������һ��UploadStatus�࣬��װSession���������
 * ��JSON��ʽ����
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
	 * form_id���ͻ����ϴ�֮ǰΪform��ӵ�����ַ����������ĸ�form
	 */
	private String form_id = "";
	/**
	 * �ϴ����ֽ�
	 */
	private long total = 0;
	/**
	 * ���ϴ��ֽ�
	 */
	private int readed = 0;
	/**
	 * ��ǰ�����ϴ��ڼ����ļ���default 1
	 */
	private int fileindex = 1;
	/**
	 * �ϴ�״̬<br>
	 * �ϴ�״̬�����У�<br>
	 * null��û���ļ������ϴ�����uploading���ϴ��У���<br>
	 * saving�������У���finish���ϴ���ɣ���error���ϴ�����
	 */
	private String state = NULL;
	/**
	 * ������Ϣ
	 */
	private String errorMsg = "";
	/**
	 * ��ʱ�ٶ�(δ����)
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
