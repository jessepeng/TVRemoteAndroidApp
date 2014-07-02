package de.pien.tvremote.client;

import android.os.Handler;
import android.os.Looper;

import java.io.*;
import java.net.*;

public abstract class Client {

	private String server;
	private int port;

	private BufferedWriter writer;
    private BufferedReader reader;
	
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
                    reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    clientSocket.setSoTimeout(5000);
                    String approvalLine = reader.readLine();
                    if (approvalLine.equals("Connection approved.")) {
                        connectHandler();
                    } else {
                        connectFailHandler();
                    }
				} catch (IOException e) {
                    e.printStackTrace();
                    connectFailHandler();
				}
			}
			
		}).start();
	}

    private void connectHandler() {
        Handler refresh = new Handler(Looper.getMainLooper());
        refresh.post(new Runnable() {
            public void run() {
                onConnect();
            }
        });
    }

    private void connectFailHandler() {
        Handler refresh = new Handler(Looper.getMainLooper());
        refresh.post(new Runnable() {
            public void run() {
                onConnectFail();
            }
        });
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
                connectFailHandler();
			}
		}
	}
	
}
