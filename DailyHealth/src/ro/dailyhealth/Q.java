package ro.dailyhealth;

import android.util.Log;


public class Q {
	private boolean lock = false;
	
	public synchronized void putLock() {
		if (!lock)
			try {
				lock = true;
				wait();
			} catch (InterruptedException e) {
				Log.d(this.getClass().toString(), "InterruptedException caught");
			}
		else {
			lock = false;
			notify();
		}
		
	}
	
}
