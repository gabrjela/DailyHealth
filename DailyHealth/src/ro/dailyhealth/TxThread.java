package ro.dailyhealth;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import ro.dailyhealth.utils.Payload;

import android.util.Log;


public class TxThread extends Thread {
	PrintWriter out = null;
	BufferedReader in = null;
	String username = null;
	String password = null;
	Socket socket = null;
	
	public TxThread(PrintWriter out) {
		super();
		
		this.out = out;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void run() {
		
		String txRequest = null;
		
		while(true) {
			txRequest = MainActivity.txRequestLock.putLock();  // wait a realeaseLock
			Log.d(this.getClass().toString(), "txRequest to server : " + txRequest);
			
			if (username != null && password != null && txRequest.startsWith(Payload.INSERT_USER)) {
				out.println(txRequest + "#" + username + "#" + password );// "insert user#username#password"
			} else {
				out.println(txRequest);
			}
			
		}
        
	} //end run
	
}
