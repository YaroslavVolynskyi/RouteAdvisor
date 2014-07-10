package com.maps.photolocation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class LoginActivity extends Activity{

	Button loginButton;
	EditText login, pass;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_pass_activity);
		
		login = (EditText) findViewById(R.id.login);
		pass = (EditText) findViewById(R.id.pass);
		
		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				login();				
			}
		});
		
		/*login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Ed
			}
		});*/
	}
	
	private void login(){
		String loginValue = login.getText().toString();
		String passValue = pass.getText().toString();
		
		String passwordToHash = "password";
        String generatedPassword = null;
        
        		
		pass.setText(hash(passValue));
		
		Intent photoIntentActivity = new Intent(this, PhotoIntentActivity.class);
		startActivity(photoIntentActivity);
	}
	
	private String hash(String input){
        MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
		}
        md.update(input.getBytes());
        byte[] bytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < bytes.length; i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        
        return sb.toString();    
	}
}
