package ro.dailyhealth;

import ro.dailyhealth.utils.Payload;
import ro.dailyhealth.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class LoginActivity extends Activity implements OnClickListener{
	public static final String USERNAME = "username";
	public static final String USR_ID = "usr_id"; 
	
	private EditText username = null;
	private EditText password = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
		
        Button loginButton = (Button)findViewById(R.id.button1);
        loginButton.setOnClickListener(this);
        
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
    }

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED, null);
		finish();
	}
	
	@Override
	public void onClick(View arg0) {
		Bundle extras = new Bundle();
		
		TextView messageText = (TextView) this.findViewById(R.id.message);
		messageText.setText("");
		
		String usernameText = ""; 
		String passwordText = "";
		usernameText = username.getText().toString();
		passwordText = password.getText().toString();
		
		if (usernameText.length() > 0 && passwordText.length() > 0
				&& !usernameText.contains("#") && !passwordText.contains("#")) {
			//write to socket a request to server
			MainActivity.tx.setUsername(usernameText);
			MainActivity.tx.setPassword(passwordText);
			MainActivity.txRequestLock.releaseLock(Payload.INSERT_USER);
			
			String responseLine = null;
			//wait for user login validation response from server 
			while((responseLine = MainActivity.rx.getUserAuthenticationResponse()) == null);
			//reset userAuthenticationResponse
			MainActivity.rx.setUserAuthenticationResponse(null);
			Log.d(this.getClass().toString(), "User authentication response : " + responseLine);
			
			if (responseLine.contains(Payload.VALID_ACCOUNT) ||
					responseLine.contains(Payload.ACCOUNT_CREATED)) {
					
					String[] fields = responseLine.split("#");
					MainActivity.usr_id = Integer.parseInt(fields[2]);
					
					if (responseLine.startsWith(Payload.ACCOUNT_CREATED)) {
						messageText.setText(responseLine);
					}
					
					extras.putString(USERNAME, usernameText);
					extras.putInt(USR_ID, MainActivity.usr_id);
					setResult(RESULT_OK,
							(new Intent()).setAction("inline-data").putExtras(extras));
					
					finish();
				
			} else {
				if (responseLine.contains(Payload.INVALID_ACCOUNT)) {
					messageText.setText(Payload.INVALID_ACCOUNT);
				}
			}

		} else {
			messageText.setText(Payload.CREDENTIALS_MISSING);
		}
		
		
	}
}