package ro.dailyhealth;

import android.util.Log;


public class Lock {
	private String request = null;
	boolean valueSet = false;
	
	public Lock() {
	}
	
	public synchronized String putLock() {
		if(!valueSet)
			try {
				wait();
			} catch (InterruptedException e) {
				Log.d(this.getClass().toString(), "InterruptedException caught");
			}
		
		System.out.println("Got the request: " + request);
		valueSet = false;
		notify();
		return request;
	}
	
	public synchronized void releaseLock(String request) {
		if (valueSet)
			try {
				wait();
			} catch (InterruptedException e) {
				Log.d(this.getClass().toString(), "InterruptedException caught");
			}
		
		valueSet = true;
		System.out.println("Put: " + request);
		this.request = request;
		notify();
	}
	
	
}
