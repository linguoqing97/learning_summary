package com.jdh.fuhsi.portal.util;

import com.jdh.fuhsi.api.model.other.PubFileReq;
import com.jdh.log.LogTools;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.net.URLEncoder;

public class CommonFileUtil {

	/**
	 * 生成ftp缓存文件
	 * @param is
	 * @param file
	 */
	public static void genFtpTempFile(InputStream is, PubFileReq file, String tempFilePath){
		File targetFile = new File(CommonFileUtil.genFtpTempName(file.getFileId(), file.getFileName(), tempFilePath));
		try {
			FileUtils.copyInputStreamToFile(is, targetFile);
		} catch (IOException e) {
			if(null != is){
				try {
					is.close();
				} catch (IOException e1) {
					LogTools.info("错误信息：{}", e);
				}
			}
			LogTools.info("错误信息：{}", e);
		}
	}

	public static String genFtpTempName(String fileId, String fileName, String tempFilePath){
		StringBuilder builder = new StringBuilder();
		builder.append(tempFilePath)
			   .append(File.separator)
			   .append(fileId)
			   .append("-")
			   .append(fileName);
		return builder.toString();
	}

	/**
	 * 下载文件
	 * @param fileName 带文件后缀的文件名
	 * @param is	   文件读入流
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	public static void writeFileToClient(String fileName, InputStream is, HttpServletResponse response,
										 HttpServletRequest request) throws Exception {
		long startTime = System.currentTimeMillis();
		String logMsg = null;

		// 清空response
		response.reset();
		// 设置response的Header
		response.addHeader("Content-Disposition", "attachment;filename=" + setFileName(fileName));
		response.setContentLength(is.available());
		response.setContentType("application/octet-stream");

		OutputStream toClient = null;
		// 打开文件输入流 和 servlet输出流
		try {
			toClient = new BufferedOutputStream(response.getOutputStream());
			// 通过ioutil 对接输入输出流，实现文件下载
			IOUtils.copy(is, toClient);
			toClient.flush();
			logMsg = "cost time:" + (System.currentTimeMillis() - startTime) + " file:" + fileName;
			LogTools.info("【文件下载成功】" + logMsg);
		} catch (Exception e) {
			logMsg = "cost time:" + (System.currentTimeMillis() - startTime) + " file:" + fileName;
			LogTools.error("【文件下载失败】" + logMsg, e);
			throw new RuntimeException("文件下载失败");
		} finally {
			// 关闭流
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(toClient);
		}
	}

	public static void writeFileToClient(String fileName, URLConnection connection, HttpServletResponse response,
										 HttpServletRequest request) throws Exception {
		long startTime = System.currentTimeMillis();
		String logMsg = null;
        String downloadFile = setFileName(fileName);
		// 清空response
		response.reset();
		// 设置response的Header
		response.addHeader("Content-Disposition", "attachment;filename=" + downloadFile);
		response.setContentType("application/octet-stream");
        LogTools.info("下载文件名: {}", downloadFile);
		OutputStream toClient = null;
		InputStream is = connection.getInputStream();
		// 打开文件输入流 和 servlet输出流
		try {
			toClient = new BufferedOutputStream(response.getOutputStream());
			while(is.available()>0){
				// 通过ioutil 对接输入输出流，实现文件下载
				IOUtils.copy(is, toClient);
			}
			toClient.flush();
			logMsg = "cost time:" + (System.currentTimeMillis() - startTime) + " file:" + fileName;
			LogTools.info("【文件下载成功】" + logMsg);
		} catch (Exception e) {
			logMsg = "cost time:" + (System.currentTimeMillis() - startTime) + " file:" + fileName;
			LogTools.error("【文件下载失败】" + logMsg, e);
			throw new RuntimeException("文件下载失败");
		} finally {
			// 关闭流
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(toClient);
		}

	}

	/**
	 * 删除文件夹及下面所有文件
	 * @param sPath
	 * @return
	 */
	public static boolean deleteFolder(String sPath){
		//如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		//如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		//删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		if (ObjectUtils.isNotEmpty(files)) {
			for (int i = 0; i < files.length; i++) {
				//删除子文件
				if (files[i].isFile()) {
					flag = files[i].delete();
					if (!flag) {
						break;
					}
				} //删除子目录
				else {
					flag = deleteFolder(files[i].getAbsolutePath());
					if (!flag) {
						break;
					}
				}
			}
		}
		if (!flag) {return false;}
		//删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 文件下载根据不同客户端进行中文转码处理
	 * @param fileName
	 * @param request
	 * @return
	 */
	public static String getChineseStr(String fileName, HttpServletRequest request){
		String userAgent = request.getHeader("USER-AGENT").toLowerCase();
		LogTools.info("获取的Agent为: {}", userAgent);
		try {
			// 谷歌，火狐浏览器
			if(StringUtils.contains(userAgent, "msie")){
				fileName = URLEncoder.encode(fileName, "UTF8");
			}else {
				fileName = URLEncoder.encode(fileName, "ISO8859-1");
			}
		} catch (UnsupportedEncodingException e) {
			LogTools.error("修改文件名失败：{}", e.getMessage());
		}
		return fileName;
	}

	/**
	 * 文件名utf-8转码
	 *
	 * @param str
	 * @return
	 */
	public static String getChineseStr(String str) {
		try {
			return URLEncoder.encode(str, "utf-8").replaceAll("\\+", "%20");
		} catch (Exception e) {
			LogTools.error("FileUtils.getChineseStr，异常", e);
		}
		return "download_";
	}

	/**
	 * 根据地址获取byte数组
	 * @param filePath
	 * @return
	 */
	public static byte[] getByteArrayByPath(String filePath){
		ByteArrayOutputStream bos=null;
		BufferedInputStream in=null;
		File file = new File(filePath);
		try{
			if(!file.exists()){
				throw new FileNotFoundException("文件不存在");
			}
			bos=new ByteArrayOutputStream((int)file.length());
			in=new BufferedInputStream(new FileInputStream(file));
			int bufSize=1024;
			byte[] buffer=new byte[bufSize];
			int len=0;
			while(-1 != (len=in.read(buffer,0,bufSize))){
				bos.write(buffer,0,len);
			}
			return bos.toByteArray();
		}catch(Exception e){
			LogTools.info("获取文件出错！");
			return null;
		}
		finally{
			try{
				if(in!=null){
					in.close();
				}
				if(bos!=null){
					bos.close();
				}
			}catch(Exception e){
				LogTools.info("关闭文件流出错！");
			}
		}
	}

	public static String setFileName(String fileName) {
		String suffixName = fileName.substring(fileName.lastIndexOf("."));
		String downloadFileName = "downloadFile_" + (Math.random() * Math.pow(10.0D, (double) 5) + Math.pow(10.0D, (double) 5));
		String temp = downloadFileName.substring(0, downloadFileName.lastIndexOf("."));
		return temp+suffixName;
	}
}
