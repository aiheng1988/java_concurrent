package cn.ahern88.concurrent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SingleThreadDownloadFile {
	
	private final static String FILE_URL = 
			//"http://mirrors.neusoft.edu.cn/centos/7.0.1406/isos/x86_64/CentOS-7.0-1406-x86_64-DVD.iso";
			//"http://101.44.1.6/files/30270000024DE405/jaist.dl.sourceforge.net/project/hibernate/hibernate4/4.3.6.Final/hibernate-release-4.3.6.Final.zip";
			"http://mirrors.cnnic.cn/apache//struts/binaries/struts-2.3.16.3-all.zip";
	
	private final static String FILE_DIR = "D:\\tmp\\";
	
	public static void main(String[] args) throws Exception{
		long start = System.currentTimeMillis();
		URL url = new URL(FILE_URL);
		HttpURLConnection hc = (HttpURLConnection)url.openConnection();
		hc.connect();
		String[] strs = FILE_URL.split("/");
		String fileName = null;
		if(strs.length > 0) {
			fileName = strs[strs.length - 1];
		}
		File file = new File(FILE_DIR + fileName);
		// 先删除再写入
		OutputStream os = new FileOutputStream(file);
		InputStream is = hc.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(is);
		byte[] bytes = new byte[2048];
		System.out.println("正在下载....");
		int len = bis.read(bytes, 0, 2048);
		while(len != -1){
			os.write(bytes,0 ,len);
			len = bis.read(bytes, 0, 2048);
		}
		System.out.println("下载完成! 耗时：" + (System.currentTimeMillis() - start) / 1000 + "s");
		os.close();
		bis.close();
		hc.disconnect();
	}

}
