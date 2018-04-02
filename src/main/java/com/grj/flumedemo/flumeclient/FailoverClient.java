package com.grj.flumedemo.flumeclient;

import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * flume 的 fileover client
 * 一个IP192.168.197.131
 * 两个端口 4141,4142 开启flume-ng
 */
public class FailoverClient {

	public static final Logger LOGGER = LoggerFactory.getLogger(FailoverClient.class);
	
	public static void main(String[] args) {
		LOGGER.info("start ....................................");
		
		//初始化一个FailoverRpcClientFacade对象
		FailoverRpcClientFacade failoverRpcClient = new FailoverRpcClientFacade();

		//初始化failover客户端
		failoverRpcClient.init();

		// Send ? events to the remote Flume agent. That agent should be
		// configured to listen with an AvroSource.
		String sampleData = "Hello Flume!";
		for (int i = 0; i < 1000000; i++) {
			failoverRpcClient.sendDataToFlume(sampleData);
			LOGGER.info(".");
		}

		failoverRpcClient.cleanUp();
		
		LOGGER.info("end ....................................");
	}
}

/**
 * 一个failover flume client类
 * failover client进行初始化,发送event,关闭client
 */
class FailoverRpcClientFacade {
	private Properties props = new Properties();
	private RpcClient failoverClient;

	// 需要的配置参数
	public static final String CLIENT_TYPE = "default_failover";
	public static final String HOSTS = "h1 h2";
	public static final String HOST1 = "192.168.197.131:4141";
	public static final String HOST2 = "192.168.197.131:4142";

	/**
	 * 初始化failover RpcClient
	 */
	public void init() {
		// 也可以使用下面的方法加载properties
		// InputStream inStream = new
		// FileInputStream("default_failover.properties");
		// properties.load(inStream);

		// Setup properties for the failover
		props = new Properties();
		props.put("client.type", CLIENT_TYPE);

		// 可以开多个host,多个port

		// List of hosts (space-separated list of user-chosen host aliases)
		// 注意:这个是别名alias,h1,h2,h3
		props.put("hosts", HOSTS);

		// host/port pair for each host alias
		props.put("hosts.h1", HOST1);
		props.put("hosts.h2", HOST2);

		//最多尝试的次数
		props.put("max-attempts", "3");
		
		
		// create the client with failover properties
		failoverClient = RpcClientFactory.getInstance(props);
	}

	/**
	 * 构建Event对象,client发送event
	 */
	public void sendDataToFlume(String data) {
		// Create a Flume Event object that encapsulates the sample data
		Event event = EventBuilder.withBody(data, Charset.forName("UTF-8"));

		// Send the event, 解决了数据丢失?
		try {
			failoverClient.append(event);
			Thread.sleep(1000);
		} catch (EventDeliveryException | InterruptedException e) {
			// clean up and recreate the client
			failoverClient.close();
			failoverClient = null;
			init();
		}
	}

	/**
	 * 关闭RPC connection
	 */
	public void cleanUp() {
		// Close the RPC connection
		failoverClient.close();
	}

}
