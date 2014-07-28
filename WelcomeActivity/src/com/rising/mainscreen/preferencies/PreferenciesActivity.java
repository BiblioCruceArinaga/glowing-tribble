package com.rising.mainscreen.preferencies;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.login.Login_Utils;
import com.rising.login.SessionManager;
import com.rising.mainscreen.MainScreenActivity;
import com.rising.mainscreen.preferencies.ChangePassword.OnPasswordChanging;
import com.rising.mainscreen.preferencies.EraseAccount.OnTaskCompleted;

public class PreferenciesActivity extends Activity{

	private Button Logout, ChangePass, Terms_Conditions, Terms_Purchase, Delete_Account;
	private TextView Name, Mail, Credit;
	private Configuration conf;
	private Context ctx = this;	
	private SessionManager session;	
	private int fid;
	private Dialog MDialog;
	private Preferencies_Utils UTILS;
	
	
	//  Recibir la señal del proceso que cambia la contraseña
	private OnPasswordChanging listenerPass = new OnPasswordChanging() {
	    public void onPasswordChanged(int details) {       
			switch (details) {
				case 1: {
					Toast.makeText(getApplicationContext(), 
							R.string.mis_datos_clave_cambiada, Toast.LENGTH_LONG).show();
					break;
				}
				case 2: {
					Toast.makeText(getApplicationContext(), 
							R.string.mis_datos_error_verif, Toast.LENGTH_LONG).show();
					break;
				}
				case 3: {
					Toast.makeText(getApplicationContext(), 
							R.string.mis_datos_clave_erronea, Toast.LENGTH_LONG).show();
					break;
				}
				case 4: {
					Toast.makeText(getApplicationContext(), 
							R.string.err_login_unknown, Toast.LENGTH_LONG).show();
					break;
				}
				default:
					Toast.makeText(getApplicationContext(), 
							R.string.err_login_unknown, Toast.LENGTH_LONG).show();
			}		
			MDialog.dismiss();
	    }
	};

	//  Recibir la señal del proceso que elimina la cuenta
	private OnTaskCompleted listener = new OnTaskCompleted() {
	    public void onTaskCompleted(int details) {       
			switch (details) {
				case 1: {
					Toast.makeText(getApplicationContext(), 
							R.string.cuenta_eliminada, Toast.LENGTH_LONG).show();
					
					session.LogOutUser();
					finish();
					break;
				}
				case 2: {
					Toast.makeText(getApplicationContext(), 
							R.string.error_eliminar_cuenta_fallo_verif, Toast.LENGTH_LONG).show();
					break;
				}
				case 3: {
					Toast.makeText(getApplicationContext(), 
							R.string.error_eliminar_cuenta_identidad, Toast.LENGTH_LONG).show();
					break;
				}
				default:
					Toast.makeText(getApplicationContext(), 
						R.string.error_eliminar_cuenta, Toast.LENGTH_LONG).show();
			}
			
			MDialog.dismiss();
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.preferencies);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		this.UTILS = new Preferencies_Utils(this);
		
		conf = new Configuration(ctx);
		session = new SessionManager(getApplicationContext());
		
		fid = session.getFacebookId();
		
		ActionBar ABar = getActionBar();
    	
    	ABar.setDisplayHomeAsUpEnabled(true);
					
		Logout = (Button) findViewById(R.id.logout_button_preferencies);
		ChangePass = (Button) findViewById(R.id.changepass_preferencies);
		Terms_Conditions = (Button) findViewById(R.id.term_conditions_preferencies);
		Terms_Purchase = (Button) findViewById(R.id.term_purchase_preferencies);
		Delete_Account = (Button) findViewById(R.id.delete_account_preferencies);
		
		if(fid != -1){
			ChangePass.setVisibility(View.GONE);
		}
		
		Name = (TextView) findViewById(R.id.name_preferencies);
		Mail = (TextView) findViewById(R.id.mail_preferencies);
		Credit = (TextView) findViewById(R.id.credit_preferencies);
				
		Name.setText(Name.getText() + " " + conf.getUserName());
		Mail.setText(Mail.getText() + " " + conf.getUserEmail());
		Credit.setText(Credit.getText() + " " + conf.getUserMoney());
		Credit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.money_ico, 0);
		
		Logout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				 LogoutButton_Actions();
 			}
			
		});

		ChangePass.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ChangePassButton_Actions();
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
				DeleteAccountButton_Actions();				
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
	
	private void LogoutButton_Actions(){
		
		if(fid > -1){
			session.LogOutFacebook();	    			
		}else{
			session.LogOutUser();
		}
		conf.setUserEmail("");
		conf.setUserId("");
		conf.setUserMoney(0);
		conf.setUserName("");
    	finish();
	}
	
	private void ChangePassButton_Actions(){
		MDialog = new Dialog(PreferenciesActivity.this, R.style.cust_dialog);
    	MDialog.setTitle(R.string.cambiar_pass);
		MDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		MDialog.setContentView(R.layout.mis_datos);
    	
		final EditText claveVieja = (EditText) MDialog.findViewById(R.id.misDatosClaveVieja);
	    final EditText claveNueva = (EditText) MDialog.findViewById(R.id.misDatosClaveNueva);
	    final EditText claveRepetir = (EditText) MDialog.findViewById(R.id.misDatosClaveRepetir);
	        				    		
		Button misDatosBoton = (Button)MDialog.findViewById(R.id.misDatosBoton);
	   
		misDatosBoton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				if(new Login_Utils(ctx).isOnline()){	
					
					//  Usuario normal
					if (fid == -1) {
						
						if ( 
							( claveVieja.getText().length() == 0 ) ||
							( claveNueva.getText().length() == 0 ) ||
							( claveRepetir.getText().length() == 0 )
						) {
							Toast.makeText(getApplicationContext(), 
								R.string.err_campos_vacios, Toast.LENGTH_LONG).show();
						} else {
							if (!claveNueva.getText().toString().equals(
									claveRepetir.getText().toString())) {
								Toast.makeText(getApplicationContext(), 
									R.string.err_pass, Toast.LENGTH_LONG).show();
							}
							else {
								new ChangePassword(PreferenciesActivity.this, listenerPass).execute(
									session.getMail(), 
									claveVieja.getText().toString(), 
									claveNueva.getText().toString());    
			        			
								claveVieja.setText("");
								claveNueva.setText("");
								claveRepetir.setText("");
							}
						}
					}
				}else{
					Toast.makeText(ctx, R.string.connection_err, Toast.LENGTH_LONG).show();	
				}
				
			}
	    	
	    });
	    MDialog.show();

	}

	private void DeleteAccountButton_Actions(){
		MDialog = new Dialog(PreferenciesActivity.this, R.style.cust_dialog);
		MDialog.setContentView(R.layout.eliminar_cuenta);
		MDialog.setTitle(R.string.eliminar_cuenta);
		MDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		MDialog.show();
		
		final EditText clave = (EditText) MDialog.findViewById(R.id.claveEliminarCuenta);
		if (fid > -1) {
			clave.setVisibility(View.INVISIBLE);
			
			TextView texto = (TextView) MDialog.findViewById(R.id.textoEliminarCuenta);
			texto.setText(R.string.esta_seguro_facebook);
		}
		
		Button botonEliminarCuenta = (Button)MDialog.findViewById(R.id.botonEliminarCuenta);
	    botonEliminarCuenta.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(new Login_Utils(ctx).isOnline()){
					String mail = session.getMail();
					
					//  Usuario normal
					if (clave.getText().length() > 0) {
						new EraseAccount(PreferenciesActivity.this, listener).execute(
								mail, clave.getText().toString());    
	        			clave.setText("");
					}
					else {
						
						//  Usuario de facebook
						if (fid > -1) {
							new EraseAccount(PreferenciesActivity.this, listener).execute(
									mail, fid + "");    
		        			clave.setText("");
						}
						
						else {
							Toast.makeText(getApplicationContext(), 
								R.string.err_campos_vacios, Toast.LENGTH_LONG).show();
						}
					}
				}else{
					Toast.makeText(ctx, R.string.connection_err, Toast.LENGTH_LONG).show();	
				}
			}
	    	
	    });
	}
	
}