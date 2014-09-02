package cn.ahern88.concurrent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 多线程下载文件
 * @author: ahern88
 * @date: 2014-9-1 下午11:29:18
 */
public class MultiThreadDownloadFile {
	
	private final static String FILE_URL = 
			"http://mirrors.cnnic.cn/apache//struts/binaries/struts-2.3.16.3-all.zip";
			//"http://101.44.1.6/files/30270000024DE405/jaist.dl.sourceforge.net/project/hibernate/hibernate4/4.3.6.Final/hibernate-release-4.3.6.Final.zip";
	private final static String FOLDER = "D:\\tmp\\";
	private final static int THREAD_SIZE = 4;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		String[] strs = FILE_URL.split("/");
		String fileName = null;
		if(strs.length > 0) {
			fileName = strs[strs.length - 1];
		}
		File[] files = new File[THREAD_SIZE];
		for (int i = 0; i < files.length; i++) {
			files[i] = new File(FOLDER + fileName + ".part" + i);
		}

		URL url = null;
		HttpURLConnection conn = null;
		try{
			url = new URL(FILE_URL);
			conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			long fileLength = conn.getContentLength();
			long preLength = fileLength / THREAD_SIZE;
			Thread[] threads = new Thread[THREAD_SIZE];
			for(int i = 0; i < THREAD_SIZE; i++ ){
				Long startLen = i * preLength;
				Long endLen = Math.min(((i + 1) * preLength - 1), fileLength);
				HttpURLConnection _conn = (HttpURLConnection) url.openConnection();
				_conn.setRequestProperty("RANGE", "bytes= " + startLen + "-" + endLen );
				_conn.connect();
				threads[i] = new DownLoadThread(files[i], _conn);
			}
			for (Thread thread : threads) {
				thread.start();
			}
			for (Thread thread : threads) {
				thread.join();
			}
			File file = new File(FOLDER + fileName);
			FileOutputStream fos = new FileOutputStream(file);
			try{
				for(File f : files) {
					FileInputStream fis = new FileInputStream(f);
					try{
						byte[] buffers = new byte[1 * 1024 * 1024];
						int len = fis.read(buffers, 0, buffers.length);
						while(len != -1) {
							fos.write(buffers, 0, len);
							len = fis.read(buffers, 0, buffers.length);
						}
					} finally {
						fis.close();
					}
					f.deleteOnExit();
				}
			} finally {
				fos.close();
			}
			long end = System.currentTimeMillis();
			System.out.println("耗时：" + (end -start) / 1000 + "s");
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
		
	}
	
	private static class DownLoadThread extends Thread {
		
		private File file;
		private HttpURLConnection conn;
		
		public DownLoadThread(File file, HttpURLConnection conn) {
			this.file = file;
			this.conn = conn;
		}
		
		@Override
		public void run() {
			System.out.println(this.getName() + " 开始下载");
			FileOutputStream fos = null;
			InputStream is = null;
			BufferedInputStream bis = null;
			try {
				is = conn.getInputStream();
				bis = new BufferedInputStream(is);
				fos = new FileOutputStream(file);
				byte[] buffers = new byte[2048];
				long size = 0;
				int len = 0;
				while( (len = bis.read(buffers, 0, 2048)) != -1) {
					fos.write(buffers, 0, len);
					size = size + len;
					System.out.println(this.getName() + " 下载了 " + (size / 1024) + "K >>>" );
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(bis != null) {
					try {
						bis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(is != null) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
				if(fos != null) {
					try{
						fos.close();
					} catch(Exception e) {
					}
				}
				conn.disconnect();
			}
		}
		
	}

}
