package com.hty.util.fileuploader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
/**
 * �ļ��ϴ�������<br>
 * ʹ����ֻ��̳д��࣬Ȼ���ڹ��÷�����������ϴ��¼��Ĵ�����<br>
 * ��򵥵Ĺ��̼�Ϊ��дonFileField()����
 * @author tisnyi
 *
 */
public class FileUploadHandler {
	private HttpServletRequest request;
	/**
	 * ���棺�˴�Ϊ���캯��������д
	 * @param request
	 */
	public FileUploadHandler(HttpServletRequest request) {
		// TODO Auto-generated constructor stub
		this.request = request;
		try {
			beginUpload();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * �ϴ��ֽڻ�������С��Ĭ��Ϊ1024B
	 */
	protected int buffersize = 1024;
	/**
	 * ����ÿˢ��һ���ϴ�״̬��ȡ���ֽڴ�С<br>
	 * <small><strong>��ֵ��ҪС��1K, ���û�ж�ȡ��form_id��ˢ��״̬, �ᵼ��д��Session�������һ������Լ���ġ�Ұ��״̬</strong></small>
	 */
	protected int updatePerLength = 2048;
	/**
	 * ����ϴ���С,����ļ��ϴ���С,Ĭ��Ϊ50M
	 */
	protected int maxUploadSize = 52428800;
	/**
	 * CPU��Ƶ<br>
	 * ʹ�÷���������ȡ��{@linkplain #cpulimit}���ֽں�,<br>
	 * �ϴ����Զ�����1ms,�Դ������͵�������CPUʹ����
	 */
	protected int cpulimit = 10240;
	/**
	 * ��ǰ�ϴ����ļ���
	 */
	protected String filename;
	/**
	 * ��ѡ��������ǰ�����ϴ����ļ�<br>
	 * �û������ڷ������������ж���File���󣬽�OutputStream ops�󶨵��Զ����File����
	 */
	protected File file;
	/**
	 * ��ǰ�ļ��ϴ�����ʶ��
	 */
	protected String form_id = null;
	/**
	 * ��ȡ�Ĳ�����ֵ
	 */
	protected Map<String, String> paraMap;
	/**
	 * ��ǰ�ļ��������
	 */
	protected OutputStream ops;
	/**
	 * �ϴ�״̬�б�
	 */
	protected Map<String, UploadStatus> uploadStatusList;
	/**
	 * ��ǰ�ϴ�״̬����
	 */
	protected UploadStatus status;
	/**
	 * ��ʼ������.��ʼ���ֽڻ�����������ϴ��ļ���С[��ѡ��]
	 */
	public void initConfig(){
		buffersize = 1024;
		maxUploadSize = 1024*1024*50;
		cpulimit = 1024;
		updatePerLength = 2048;
	}
	/**
	 * ��ȡ���ı�����ʱ�û����Ե��ô˷�����
	 * �پ�����ν��д����ļ��ı���[��ѡ��]<br>
	 * @param paraName ������
	 * @param paraValue ����ֵ
	 * @return boolean <br>
	 *  true : �������ִ�� <br>
	 *  false : �����ж�ִ��,��ȡ����
	 */
	public boolean onTextField(String paraName, String paraValue){
		return true;
	}
	
	/**
	 * ��ȡ���ļ�ʱ���ô˷���
	 * һ�㽨�齫�ı�������ļ�ǰ��<br>
	 * �ڳ����ȡ���ı���֮����Ծ����ļ��Ĵ洢��ʽ���洢λ�õ�[��ѡ��]<br>
	 * @param filename �ļ�����(�ļ����Ѵ�����������̷�)
	 */
	public boolean onFileField(String filename){
		return true;
	}
	/**
	 * ��һ���ļ���ȡ��������ô˷���[��ѡ��]<br>
	 * �û������ڴ˹رյ�ǰ�ļ��������<br>
	 * PS:ͬһʱ��ֻ���ϴ�һ���ļ�, ��˲�ͬ�����̰߳�ȫ����
	 * @throws IOException 
	 */
	public void onFileEnd() throws IOException{
		if(null != ops)
			try {
				ops.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	/**
	 * ��ȡ���ļ��������ֽ�ʱ���ô˷���[��ѡ��]<br>
	 * ��ȡһ���ļ����ܶ�ε��ô˷���<br>
	 * ǰ��������û���֮ǰ������ļ������
	 * @param b ��ȡ���ļ��ֽ�����
	 * @throws IOException 
	 */
	private void writeFile(byte[] b) throws IOException{
		writeFile(b, 0, b.length);
	}
	/**
	 * ��ȡ���ļ��������ֽ�ʱ���ô˷���[��ѡ��]<br>
	 * ��ȡһ���ļ����ܶ�ε��ô˷���<br>
	 * ǰ��������û���֮ǰ������ļ������
	 * @param b ��ȡ���ļ��ֽ�����
	 * @param start ��ʼ�ֽ�λ��
	 * @param length ��Ч�ֽڳ���
	 * @throws IOException
	 */
	public void writeFile(byte[] b, int start, int length) throws IOException{
		if(null != ops){
			ops.write(b, start, length);
		}
	}
	/**
	 * ���ͻ���ˢ�»��߳���<font style='color:red'>�쳣</font>�����������ж�ʱ���ø÷���.[��ѡ��]<br>
	 * ��ʱ���Զ�ɾ��δ�ϴ��ɹ����ļ�
	 * @throws IOException 
	 */
	public void onRequestInputStreamInterrupt() throws IOException{
		status.setState(UploadStatus.ERROR);
		status.setErrorMsg("�����жϣ�");//�ϴ�������������ֹ
		//�ϴ�ʧ�ܣ�ɾ���ļ��ж� �����һ���ļ�
		if(null != file && file.exists()){
			file.delete();
		}
		/*for(File f : regfile)
			System.out.println(f.getAbsolutePath());*/
	}
	/**
	 * ���ϴ�����δ������쳣���ô˷���[��ѡ��]<br><br>
	 * ���쳣�ᵼ���ϴ���ֹ
	 * @param e Exception
	 * @return boolean <br>
	 *  true : �������ִ�� <br>
	 *  false : �����ж�ִ��,��ȡ����
	 */
	public boolean onError(Exception e){
		status.setState(UploadStatus.ERROR);
		status.setErrorMsg("δ֪����");
		return false;
	}
	
	/**
	 * ���ϴ���С��������ʱ���ô˷���[��ѡ��]
	 * @return boolean <br>
	 *  true : �������ִ�� <br>
	 *  false : �����ж�ִ��,��ȡ����
	 */
	public boolean onFileSizeExceed(){
		status.setState(UploadStatus.ERROR);
		status.setErrorMsg("�������ϴ��ļ�������" + maxUploadSize/1024/1024 + "MB");
		return false;
	}
	
	/**
	 * �ļ��ϴ���ȡ��ȫ��������û�з�������ʱ���ø÷���[��ѡ��]<br>
	 * @throws IOException 
	 */
	public void onUploadFinish() throws IOException{
	}
	/**
	 * �ϴ�Ԥ����,�ϴ�֮ǰ����[��ѡ��]<br><br>
	 * @return
	 * 	true �� �ϴ�����ִ��<br>
	 * 	false �������ж�ִ��,��ȡ����
	 */
	public boolean preWorks(){
		return true;
	}
	/**
	 * �����ϴ��Ĺ���
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void beginUpload() throws IOException{
		
		initConfig();
		
		if(!preWorks()){
			return;
		}
		long total = request.getContentLength();
		
		uploadStatusList = (Map<String, UploadStatus>) request.getSession().getAttribute("uploadStatusList");
		if(null == uploadStatusList) 
			uploadStatusList = new HashMap<String, UploadStatus>();//�����ϴ�״̬
		status = new UploadStatus();
		status.setState(UploadStatus.UPLOADING);
		status.setTotal(total);
		status.setReaded(0);
		
		String contentType = request.getContentType();
		
		if(contentType.matches("multipart/form-data; boundary=.*")){
			String boundary = contentType.replaceAll(".*boundary=(.*)", "$1");
			int readed = 0;
			int fileindex = 0;
			
			String paraSeparator = "--" + boundary;
			String endSeparator = "--" + boundary + "--";
			
			paraMap = new HashMap<String, String>();
			boolean readname = false;
			boolean readStrValue = false;
			boolean readByteValue = false;
			boolean skipToFile = false;
			boolean skipToValue = false;
			
			boolean endRead = false;
			
			String paraName = null;
			String paraValue = null;
			boolean begin = false;
			
			InputStream ips = request.getInputStream();
			byte tmp[] = new byte[1];
			byte[] move = new byte[2];
			byte b;
			ByteBuffer bf = ByteBuffer.allocate(buffersize);
			int bf_pos = 0;
			try {
				while(ips.read(tmp) != -1){
					readed++;
					if(readed % updatePerLength == 0){
						//System.out.println("���ϴ���" + (double)readed/total*100 + "%");
						status.setReaded(readed);
					}
					if(bf_pos == buffersize){
						if(readStrValue){
							if(null == paraValue)
								paraValue = new String(bf.array(), 0, bf_pos-1, "utf-8");
							else
								paraValue += new String(bf.array(), 0, bf_pos-1, "utf-8");
							bf.clear();
							bf_pos=0;
						}
						else{
							if(begin){
								writeFile(new byte[]{13, 10});
								begin = false;
							}
							writeFile(bf.array());
							bf.clear();
							bf_pos=0;
						}
					}
					
					b = tmp[0];
					bf.put(b);
					bf_pos++;
					
					if(b == 13){
						move[0] = b;
						if(ips.read(tmp) != -1){
							b = tmp[0];
							move[1] = b;
							if(b == 10){
								
								String sb = new String(bf.array(), 0, bf_pos-1, "utf-8");
								
								if(sb.equals(endSeparator)){
									if(readStrValue){
										paraMap.put(paraName, paraValue);
										if(!onTextField(paraName, paraValue))
											return;
										paraValue = null;
									}
									if(readByteValue){
										if(null != ops)
											ops.close();
										onFileEnd();
									}
									
									endRead = true;
									break;
								}
								if(sb.equals(paraSeparator)){
									if(readStrValue){
										paraMap.put(paraName, paraValue);
										if(!onTextField(paraName, paraValue))
											return;
										paraValue = null;
									}
									if(readByteValue){
										if(null != ops)
											ops.close();
										onFileEnd();
									}
									readname = true;
									readStrValue = false;
									readByteValue = false;
									skipToFile = false;
									skipToValue = false;
									bf.clear();
									bf_pos=0;
									continue;
								}
								
								if(readStrValue){
									if(null == paraValue)
										paraValue = sb;
									else
										paraValue += sb;
									if(paraName.equals("form_id")){
										form_id = paraValue;
										status.setForm_id(form_id);
										status.setReaded(readed);
										uploadStatusList.put(form_id, status);
										request.getSession().setAttribute("uploadStatusList", uploadStatusList);
										if(total > maxUploadSize || total == -1){//�����ļ��ϴ�����С
											if(!onFileSizeExceed())
												return;
										}
									}
									bf.clear();
									bf_pos=0;
									continue;
								}
								if(readByteValue){
									if(begin){
										writeFile(new byte[]{13, 10});
									}
									
									writeFile(bf.array(), 0, bf_pos-1);
									begin = true;
									bf.clear();
									bf_pos=0;
									continue;
								}
								if(skipToValue){
									readname = false;
									readStrValue = true;
									readByteValue = false;
									skipToFile = false;
									skipToValue = false;
									bf.clear();
									bf_pos=0;
									continue;
								}
								if(skipToFile){
									readname = false;
									readStrValue = false;
									readByteValue = true;
									skipToFile = false;
									skipToValue = false;
									ips.skip(2);
									bf.clear();
									bf_pos=0;
									begin = false;
									continue;
								}
								if(readname){
									if(sb.matches(".*filename=\".*\"")){
										paraName = sb.replaceAll(".*filename=\"(.*)\"", "$1");
										if(paraName.contains("\\"))
											paraName = paraName.substring(paraName.lastIndexOf("\\") + 1);
										filename = paraName;
										if(fileindex > 0){
											if(null != ops)
												ops.close();
											onFileEnd();
										}
										if(!onFileField(paraName))
											break;
										
										readname = false;
										readStrValue = false;
										readByteValue = false;
										skipToFile = true;
										skipToValue = false;
										fileindex++;
										status.setFileindex(fileindex);
									}else{
										paraName = sb.replaceAll(".*name=\"(.*)\"", "$1");
										paraName = new String(paraName.getBytes());
										readname = false;
										readStrValue = false;
										readByteValue = false;
										skipToFile = false;
										skipToValue = true;
									}
									bf.clear();
									bf_pos=0;
									continue;
								}
							}else{
								if(bf_pos == buffersize){
									if(readStrValue){
										if(null == paraValue)
											paraValue = new String(bf.array(), 0, bf_pos-1, "utf-8");
										else
											paraValue += new String(bf.array(), 0, bf_pos-1, "utf-8");
										bf.clear();
										bf_pos=0;
									}
									else{
										if(begin){
											writeFile(new byte[]{13, 10});
											begin = false;
										}
										writeFile(bf.array());
										bf.clear();
										bf_pos=0;
									}
								}
								b = tmp[0];
								bf.put(b);
								bf_pos++;
							}
						}else{
							endRead = true;
							break;
						}
					}
				}
			} catch (Exception e) {
				if(null != ops)
					ops.close();
				if(!onError(e)){
					return;
				}
			}
			
			if(!endRead){
				if(null != ops)
					ops.close();
				onRequestInputStreamInterrupt();
			}else{
				if(null != ops)
					ops.close();
				onUploadFinish();
				status.setReaded((int)total);
				status.setState(UploadStatus.FINISH);
			}
		}
	}
}
