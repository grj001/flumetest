package com.grj.flumedemo.underlyingprinciple;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		NetworkSource networkSource = new NetworkSource();
		FileSink fileSink = new FileSink();
		new Thread(networkSource).start();
		new Thread(fileSink).start();
		//Thread.sleep(1000);//让主线程睡一秒
		//System.out.println("main");
		/*while(true){
			System.out.println("main");
		}*/
	}

}
