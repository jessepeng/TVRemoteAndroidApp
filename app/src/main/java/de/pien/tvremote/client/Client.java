package de.pien.tvremote.client;

import java.io.*;
import java.net.*;

public abstract class Client {

	private String server;
	private int port;

	private BufferedWriter writer;
	
	public Client(String server, int port) {
		this.server = server;
		this.port = port;
	}
	
	public void connect() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Socket clientSocket = new Socket(server, port);
					writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    onConnect();
				} catch (IOException e) {
					e.printStackTrace();
                    onConnectFail();
				}
			}
			
		}).start();
	}

    public abstract void onConnect();

    public abstract void onConnectFail();
	
	public void sendString(String line) {
		if (writer != null) {
			try {
				writer.write(line);
				writer.newLine();
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
