package com.grj.flumedemo.flumeclient;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;
import java.nio.charset.Charset;

/**
 * 转载自http://flume.apache.org/FlumeDeveloperGuide.html
 * 在Flume 1.4.0中，Avro是默认的RPC协议。
 * NettyAvroRpcClient和ThriftRpcClient实现了RpcClient接口。
 * 客户端需要使用target Flume agent的 host 和 port 创建这个对象，
 * 然后可以使用RpcClient将数据发送到agent。
 * 下面的示例展示了如何在用户的数据生成应用程序中使用Flume客户端SDK API
 * 
 * resources/avro_m_logger.conf
 * 
 */
public class FlumeRPCClient {
	
	public static final String HOST_BIND = "192.168.197.131";

	public static void main(String[] args) {
		MyRpcClientFacade client = new MyRpcClientFacade();
		// Initialize client with the remote Flume agent's host and port
		client.init(HOST_BIND, 4141);

		// Send 10 events to the remote Flume agent. That agent should be
		// configured to listen with an AvroSource.
		String sampleData = "Hello Flume!";
		for (int i = 0; i < 10; i++) {
			client.sendDataToFlume(sampleData);
		}

		client.cleanUp();
	}

}

/**
 *	一个flume client类
 */
class MyRpcClientFacade {
	private RpcClient client;
	private String hostname;
	private int port;

	/**
	 * 初始化RpcClient
	 */
	public void init(String hostname, int port) {
		// Setup the RPC connection
		this.hostname = hostname;
		this.port = port;
		this.client = RpcClientFactory.getDefaultInstance(hostname, port);
		// Use the following method to create a thrift client (instead of
		// the above line):
		// this.client = RpcClientFactory.getThriftInstance(hostname, port);
	}

	/**
	 * 构建Event对象,client发送event
	 */
	public void sendDataToFlume(String data) {
		// Create a Flume Event object that encapsulates the sample data
		Event event = EventBuilder.withBody(data, Charset.forName("UTF-8"));

		// Send the event
		try {
			client.append(event);
		} catch (EventDeliveryException e) {
			// clean up and recreate the client
			client.close();
			client = null;
			client = RpcClientFactory.getDefaultInstance(hostname, port);
			// Use the following method to create a thrift client (instead
			// of the above line):
			// this.client = RpcClientFactory.getThriftInstance(hostname,
			// port);
		}
	}

	/**
	 * 关闭RPC connection
	 */
	public void cleanUp() {
		// Close the RPC connection
		client.close();
	}

}