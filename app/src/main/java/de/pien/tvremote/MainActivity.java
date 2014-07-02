package de.pien.tvremote;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import de.pien.tvremote.client.Client;

public class MainActivity extends Activity {
	
	private static final String SERVER_IP_ADDRESS = "192.168.1.105";
	private static final int SERVER_PORT = 56111;
	
	private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new Client(SERVER_IP_ADDRESS, SERVER_PORT) {
            public void onConnect() {
                ((TextView)findViewById(R.id.textConnection)).setText(R.string.connection_connected);
            }

            public void onConnectFail() {
                ((TextView)findViewById(R.id.textConnection)).setText(R.string.connection_failed);
            }
        };
        client.connect();
    }
    
    public void clickUp(View v) {
    	client.sendString("{DOWN}");
    }
    
    public void clickDown(View v) {
    	client.sendString("{UP}");    	
    }
    
}
