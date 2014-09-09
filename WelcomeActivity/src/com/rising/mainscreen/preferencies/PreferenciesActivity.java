package com.rising.mainscreen.preferencies;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.login.Login_Utils;
import com.rising.login.SessionManager;
import com.rising.mainscreen.MainScreenActivity;

//Clase que muestra las distintas opciones de la pantalla de preferencias
public class PreferenciesActivity extends Activity{

	private Button Logout, ChangePass, Terms_Conditions, Terms_Purchase, Delete_Account;
	private TextView Name, Mail, Credit;
	
	private Context ctx = this;		
	private Preferencies_Utils UTILS;
	
	//Clases usadas
	private Configuration CONF;
	private SessionManager SESSION;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.preferencies_preferencieslayout);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		ActionBar ABar = getActionBar();
    	ABar.setDisplayHomeAsUpEnabled(true);
		
		this.UTILS = new Preferencies_Utils(this);
		this.CONF = new Configuration(ctx);
		this.SESSION = new SessionManager(getApplicationContext());
    	
		Name = (TextView) findViewById(R.id.name_preferencies);
		Mail = (TextView) findViewById(R.id.mail_preferencies);
		Credit = (TextView) findViewById(R.id.credit_preferencies);
		Logout = (Button) findViewById(R.id.logout_button_preferencies);
		ChangePass = (Button) findViewById(R.id.changepass_preferencies);
		Terms_Conditions = (Button) findViewById(R.id.term_conditions_preferencies);
		Terms_Purchase = (Button) findViewById(R.id.term_purchase_preferencies);
		Delete_Account = (Button) findViewById(R.id.delete_account_preferencies);
		
		ShowUser_Data();
		
		Logout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				 LogoutButton_Actions();
 			}
		});

		ChangePass.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				new Login_Utils(ctx).openFragment(ChangePassword_Fragment.class);
			}
	
		});

		Terms_Conditions.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				UTILS.Legal_Displays(getString(R.string.terminos));
			}
			
		});

		Terms_Purchase.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				UTILS.Legal_Displays(getString(R.string.condiciones));
			}
			
		});

		Delete_Account.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				new Login_Utils(ctx).openFragment(DeleteAccount_Fragment.class);				
			}
			
		});
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
			
		switch(item.getItemId()){
		
			case android.R.id.home:
				Intent i = new Intent(ctx, MainScreenActivity.class);
				startActivity(i);
				finish(); 
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void ShowUser_Data(){
		Name.setText(Name.getText() + " " + CONF.getUserName());
		Mail.setText(Mail.getText() + " " + CONF.getUserEmail());
		Credit.setText(Credit.getText() + " " + CONF.getUserMoney());
		Credit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.money, 0);
		
		if(SESSION.getFacebookId() != -1){
			ChangePass.setVisibility(View.GONE);
		}
	}
	
	private void LogoutButton_Actions(){
		
		if(SESSION.getFacebookId() > -1){
			SESSION.LogOutFacebook();	    			
		}else{
			SESSION.LogOutUser();
		}
		CONF.setUserEmail("");
		CONF.setUserId("");
		CONF.setUserMoney(0);
		CONF.setUserName("");
    	finish();
	}
	
}