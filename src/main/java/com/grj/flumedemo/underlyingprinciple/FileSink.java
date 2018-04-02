package com.grj.flumedemo.underlyingprinciple;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class FileSink implements Runnable {

	public void run() {
		while (true) {
			try {
				String line = MyChannel.queue.take();// 取出
				appendToFile(line);// 追加
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void appendToFile(String line) throws IOException {
		File file = new File("d:\\grj\\filesink.txt");
		FileWriter fileWriter = new FileWriter(file, true);
		fileWriter.write(line);
		fileWriter.close();

	}

}
