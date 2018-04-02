package com.grj.flumedemo.underlyingprinciple;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSource {
	
	public static Logger logger = LoggerFactory.getLogger(DataSource.class);
	
	public static void main(String[] args) throws IOException, InterruptedException {
		// 网络服务端用来产生数据
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(999);
		while (true) {
			final Socket client = serverSocket.accept();
			new Thread(new Runnable() {

				@Override
				public void run() {
					OutputStream stream;
					try {
						stream = client.getOutputStream();
						while (true) {
							logger.info("write.");
							System.out.println("write.");
							stream.write("qwer\r\n".getBytes());
							Thread.sleep(1000);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}

		// stream.close();
	}

}
