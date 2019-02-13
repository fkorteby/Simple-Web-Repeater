package com.repeater.server;

public interface SimpleServer {
	
	public void Start(int port);
	public void Stop();
	public boolean getStatus();	
}
