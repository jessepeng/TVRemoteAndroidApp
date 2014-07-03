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
    private Socket clientSocket;
	
	public Client(String server, int port) {
		this.server = server;
		this.port = port;
	}

    /**
     * Connects to the server using the given IP address and port.
     */
	public void connect() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					clientSocket = new Socket(server, port);
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

    /**
     * Disconnects from the server.
     */
    public void disconnect() {
        try {
            if (writer != null) writer.close();
        } catch (IOException e) { // Resume
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) { // Resume
            } finally {
                try {
                    if (clientSocket != null) clientSocket.close();
                } catch (IOException e) { // Resume
                }
            }
        }
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

    /**
     * Is called when the connection has been successfully established.
     */
    public abstract void onConnect();

    /**
     * Is called when the connection fails for any reason.
     */
    public abstract void onConnectFail();

    /**
     * Sends a line to the server
     * @param line message
     */
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
