package com.it.perf;

import com.it.client.ItClient;
import com.it.common.Config;
import com.it.server.ItServer;

public class PerfServer {
	public static void main(String[] args) throws Exception {
		Config config = new Config(args);

		if (config.isClient()) {
		    new ItClient("127.0.0.1", 8080);
		} else {
		    new ItServer(8080).run();
		}
	}
}
