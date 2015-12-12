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
 * 文件上传处理类<br>
 * 使用者只需继承此类，然后在公用方法里面参与上传事件的处理即可<br>
 * 最简单的过程即为覆写onFileField()函数
 * @author tisnyi
 *
 */
public class FileUploadHandler {
	private HttpServletRequest request;
	/**
	 * 警告：此处为构造函数，请勿覆写
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
	 * 上传字节缓冲区大小，默认为1024B
	 */
	protected int buffersize = 1024;
	/**
	 * 设置每刷新一次上传状态读取的字节大小<br>
	 * <small><strong>该值不要小于1K, 如果没有读取到form_id便刷新状态, 会导致写到Session里面的是一个不受约束的“野”状态</strong></small>
	 */
	protected int updatePerLength = 2048;
	/**
	 * 最大上传大小,最大文件上传大小,默认为50M
	 */
	protected int maxUploadSize = 52428800;
	/**
	 * CPU限频<br>
	 * 使用方法：当读取到{@linkplain #cpulimit}个字节后,<br>
	 * 上传会自动休眠1ms,以此来降低单个任务CPU使用率
	 */
	protected int cpulimit = 10240;
	/**
	 * 当前上传的文件名
	 */
	protected String filename;
	/**
	 * 可选参数：当前正在上传的文件<br>
	 * 用户可以在方法域里面自行定义File对象，将OutputStream ops绑定到自定义的File对象
	 */
	protected File file;
	/**
	 * 当前文件上传表单标识号
	 */
	protected String form_id = null;
	/**
	 * 读取的参数键值
	 */
	protected Map<String, String> paraMap;
	/**
	 * 当前文件的输出流
	 */
	protected OutputStream ops;
	/**
	 * 上传状态列表
	 */
	protected Map<String, UploadStatus> uploadStatusList;
	/**
	 * 当前上传状态载体
	 */
	protected UploadStatus status;
	/**
	 * 初始化参数.初始化字节缓冲区和最大上传文件大小[可选项]
	 */
	public void initConfig(){
		buffersize = 1024;
		maxUploadSize = 1024*1024*50;
		cpulimit = 1024;
		updatePerLength = 2048;
	}
	/**
	 * 读取到文本参数时用户可以调用此方法，
	 * 再决定如何进行处理文件的保存[可选项]<br>
	 * @param paraName 参数名
	 * @param paraValue 参数值
	 * @return boolean <br>
	 *  true : 程序继续执行 <br>
	 *  false : 程序中断执行,读取结束
	 */
	public boolean onTextField(String paraName, String paraValue){
		return true;
	}
	
	/**
	 * 读取到文件时调用此方法
	 * 一般建议将文本域放在文件前面<br>
	 * 在程序读取到文本域之后可以决定文件的存储方式、存储位置等[必选项]<br>
	 * @param filename 文件名称(文件名已处理，不会带有盘符)
	 */
	public boolean onFileField(String filename){
		return true;
	}
	/**
	 * 当一个文件读取结束后调用此方法[可选项]<br>
	 * 用户可以在此关闭当前文件的输出流<br>
	 * PS:同一时刻只会上传一个文件, 因此不同担心线程安全问题
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
	 * 读取到文件输入流字节时调用此方法[可选项]<br>
	 * 读取一个文件可能多次调用此方法<br>
	 * 前提假设是用户已之前定义好文件输出流
	 * @param b 读取的文件字节数组
	 * @throws IOException 
	 */
	private void writeFile(byte[] b) throws IOException{
		writeFile(b, 0, b.length);
	}
	/**
	 * 读取到文件输入流字节时调用此方法[可选项]<br>
	 * 读取一个文件可能多次调用此方法<br>
	 * 前提假设是用户已之前定义好文件输出流
	 * @param b 读取的文件字节数组
	 * @param start 起始字节位置
	 * @param length 有效字节长度
	 * @throws IOException
	 */
	public void writeFile(byte[] b, int start, int length) throws IOException{
		if(null != ops){
			ops.write(b, start, length);
		}
	}
	/**
	 * 当客户端刷新或者程序<font style='color:red'>异常</font>导致输入流中断时调用该方法.[可选项]<br>
	 * 此时会自动删除未上传成功的文件
	 * @throws IOException 
	 */
	public void onRequestInputStreamInterrupt() throws IOException{
		status.setState(UploadStatus.ERROR);
		status.setErrorMsg("网络中断！");//上传输入流意外终止
		//上传失败，删除文件列队 的最后一个文件
		if(null != file && file.exists()){
			file.delete();
		}
		/*for(File f : regfile)
			System.out.println(f.getAbsolutePath());*/
	}
	/**
	 * 当上传发生未处理的异常调用此方法[可选项]<br><br>
	 * 该异常会导致上传终止
	 * @param e Exception
	 * @return boolean <br>
	 *  true : 程序继续执行 <br>
	 *  false : 程序中断执行,读取结束
	 */
	public boolean onError(Exception e){
		status.setState(UploadStatus.ERROR);
		status.setErrorMsg("未知错误！");
		return false;
	}
	
	/**
	 * 当上传大小超过限制时调用此方法[可选项]
	 * @return boolean <br>
	 *  true : 程序继续执行 <br>
	 *  false : 程序中断执行,读取结束
	 */
	public boolean onFileSizeExceed(){
		status.setState(UploadStatus.ERROR);
		status.setErrorMsg("请限制上传文件不大于" + maxUploadSize/1024/1024 + "MB");
		return false;
	}
	
	/**
	 * 文件上传读取完全结束并且没有发生错误时调用该方法[可选项]<br>
	 * @throws IOException 
	 */
	public void onUploadFinish() throws IOException{
	}
	/**
	 * 上传预处理,上传之前调用[可选项]<br><br>
	 * @return
	 * 	true ： 上传继续执行<br>
	 * 	false ：程序中断执行,读取结束
	 */
	public boolean preWorks(){
		return true;
	}
	/**
	 * 处理上传的过程
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
			uploadStatusList = new HashMap<String, UploadStatus>();//保存上传状态
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
						//System.out.println("已上传：" + (double)readed/total*100 + "%");
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
										if(total > maxUploadSize || total == -1){//限制文件上传最大大小
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
