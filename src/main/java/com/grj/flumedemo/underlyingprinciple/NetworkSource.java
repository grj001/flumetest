package com.grj.flumedemo.underlyingprinciple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

//用来接收网络数据的source  用线程实现
//java 中提供了一个接口 runnable 实现其中的run方法,就可以创建一个线程
public class NetworkSource implements Runnable{

	public void run() {
		//System.out.println("source");
		//获取网络连接
		try {
			Socket socket = new Socket("localhost",999);
			//获取输入流
			InputStream inputStream = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while((line=reader.readLine())!=null){
				//System.out.println(line);
				MyChannel.queue.put(line);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
