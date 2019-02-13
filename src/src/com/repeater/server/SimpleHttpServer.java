package com.repeater.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SimpleHttpServer implements SimpleServer{
	private int port;
	private HttpServer server;
	private boolean status;
	
	@Override
	public void Start(int port) {
		try {
			this.port = port;
			server = HttpServer.create(new InetSocketAddress(port), 0);			
			server.createContext("/check", new Handlers.RootHandler());
			server.createContext("/", new Handlers.AllHandler());
			server.setExecutor(null);
			server.start();			
			status = true;
			System.out.println("server started at " + port);
		} catch (IOException e) {
			e.printStackTrace();
			status = false;
		}
	}
	
	@Override
	public void Stop() {
		server.stop(0);
		System.out.println("server stopped");
		status = false;
	}
	
	@Override
	public boolean getStatus() {
		return status;		
	}
}
