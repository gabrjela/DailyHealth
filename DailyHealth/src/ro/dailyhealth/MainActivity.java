package ro.dailyhealth;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import ro.dailyhealth.maps.UserLocations;
import ro.dailyhealth.notification.Notif;
import ro.dailyhealth.utils.FragmentPreferences;
import ro.dailyhealth.utils.Payload;
import ro.dailyhealth.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/** 
 * Start Activity of the application.
 * Here is where rx and tx threads are started - threads for reading and writing data
 * to DB connection socket.
 */
public class MainActivity extends Activity implements OnSharedPreferenceChangeListener {
	public static String username = null;
	public static int usr_id = 0;
	public static PrintWriter out = null;
	public static BufferedReader in = null;
	public static RxThread rx = null;
	public static TxThread tx = null;
	public static Lock txRequestLock = null;
	public static boolean dataAlreadyLoaded = false;
	public static boolean notifOnStatusBar = true;
	public static boolean notifByEmail = false;
	public static String emailAddress = null;
	
	public static final String PREFS_NAME = "UsernameFile";
	public static final int LOGIN = 1;
	
	//socket for communication with a db server
	private Socket socket = null;
	private ListView lv = null;
	private SharedPreferences prefs = null;
	private boolean settingsChanged = false;
    private String[] buttonsList = null;
    private int[] icons = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
		
		this.setContentView(R.layout.main);
		
		if (txRequestLock == null) {
			txRequestLock = new Lock();
		}
		
		if (UserLocations.notif == null) {
			UserLocations.notif = new Notif();
		}
		
		
	    // Restore preferences
		if (username == null || usr_id == 0) {
		    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		    username = settings.getString(LoginActivity.USERNAME, null);
		    usr_id = settings.getInt(LoginActivity.USR_ID, 0);
		}
	    
		if (prefs == null) {
		    prefs = PreferenceManager
	                .getDefaultSharedPreferences(this);
		    prefs.registerOnSharedPreferenceChangeListener(this);
		}
                
                
		if (rx == null) {
			// start RxThread
			rx = new RxThread();
			rx.start();
				
			while ((socket = rx.getSocket()) == null);
			while ((in = rx.getIn()) == null);
			while ((out = rx.getOut()) == null);
		}
		
		if (tx == null) {
			// start TxThread
			tx = new TxThread(out);
			tx.start();
		}		
		
		if (username == null) {
			//show login page
			Intent  i = new Intent(this, LoginActivity.class);
			startActivityForResult(i, LOGIN);
		} else { //already registered once
				Log.d(this.getClass().toString(), "User already registered : usr_id = " + usr_id + ", username = " + username);
				
				this.setContentView(R.layout.layout_animation_7);
				
				MainActivity.txRequestLock.releaseLock(Payload.LOAD_MYLOCATIONS + "#" + username);
				
				createList();
		}
		
	}
	
	 
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == LOGIN) {
    		if (resultCode == RESULT_OK) {
				this.setContentView(R.layout.layout_animation_7);
				
				username = data.getStringExtra(LoginActivity.USERNAME);
				usr_id = data.getIntExtra(LoginActivity.USR_ID, 0);
				
				MainActivity.txRequestLock.releaseLock(Payload.LOAD_MYLOCATIONS + "#" + username);
				
				createList();  			
    		}
    	}
	}
    

	@Override
	 public void onResume() {
	    super.onResume();
	    
	    if (settingsChanged) {
		    notifOnStatusBar = prefs.getBoolean("checkbox_preference", false);
		    notifByEmail = prefs.getBoolean("parent_checkbox_preference", false);
		    if (notifByEmail) {
		    	emailAddress = prefs.getString("edittext_preference", null);
		    }
		    System.out.println("notifOnStatusBar:" + notifOnStatusBar);
		    System.out.println("notifByEmail:" + notifByEmail);
		    System.out.println("emailAddress:" + emailAddress);
		    
		    // settings changed#usr_id#notif_status_bar#notif_by_email#email_address
			MainActivity.txRequestLock.releaseLock(Payload.SETTINGS_CHANGED + "#" 
								+ usr_id + "#"
								+ notifOnStatusBar + "#"
								+ notifByEmail + "#"
								+ emailAddress);
		    
		    settingsChanged = false;
	    }
	    
	}
	
    @Override
    protected void onStop(){
       super.onStop();

      // We need an Editor object to make preference changes.
      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putString(LoginActivity.USERNAME, username);
      editor.putInt(LoginActivity.USR_ID, usr_id);

      // Commit the edits!
      editor.commit();
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.d(this.getClass().toString(), "Changed attribute in SharedPreferences is " + key);
//		if (key.equalsIgnoreCase("checkbox_preference")) { // notifOnStatusBar
//			
//		}
//		if (key.equalsIgnoreCase("parent_checkbox_preference")) { //notifByEmail
//			
//		}
//		if (key.equalsIgnoreCase("edittext_preference")) { //emailAddress
//			
//		}
		settingsChanged = true;
		
	}	
	
	private void createList() {
		lv = (ListView) this.findViewById(R.id.listView01);
		buttonsList = new String[] {
				"Browse",
				"Settings",
		};
		icons = new int[] {
				R.drawable.browse,
				R.drawable.settings
		};
		lv.setAdapter(new CustomAdapter());
		
	}	
	    
	class CustomAdapter extends ArrayAdapter<String> {
		
		CustomAdapter() {
    		super(MainActivity.this, R.layout.row, buttonsList);
    	}
    	
    	public View getView(final int position, View convertView, ViewGroup parent) {
    		LayoutInflater inflater = getLayoutInflater();
    		View row = inflater.inflate(R.layout.row, parent, false);
    		
    		ImageButton img =  (ImageButton) row.findViewById(R.id.icon);
    		img.setBackgroundDrawable(getResources().getDrawable(icons[position]));
    		img.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (position == 0) {
						Intent  i = new Intent(MainActivity.this, UserLocations.class);
		            	startActivity(i);
					} else {
						Intent  i = new Intent(MainActivity.this, FragmentPreferences.class);
		            	startActivity(i);
					}
					
				}
			});
    		
    		TextView itemText = (TextView)row.findViewById(R.id.item);
    		itemText.setText(buttonsList[position]);
    		
    		row.setMinimumHeight(150);
    		return(row);
    	}
    }
	

}
