package com.rising.mainscreen;

import com.rising.drawing.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class MainScreen_Errors {

	private Context ctx;
	private Dialog EDialog;
	
	public MainScreen_Errors(Context context){
		this.ctx = context;
	}
	
	public void ErrOrdenar(int code){
		
		EDialog = new Dialog(ctx, R.style.cust_dialog);
    	EDialog.getWindow();
        EDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		EDialog.setContentView(R.layout.error_errordialog);
		EDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		TextView tv_E = (TextView)EDialog.findViewById(R.id.error_tV);
		
		switch(code){
			case 0:
				tv_E.setText(R.string.sort_no_scores);
				break;
				
			default:
				tv_E.setText(R.string.sort_error);
				break;
		}
		Button  Login_Error_Close_Button = (Button)EDialog.findViewById(R.id.error_button);
		
		Login_Error_Close_Button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {		
				EDialog.dismiss();				
			}
		});
    	
		EDialog.show();	
	}
	
	public void ErrPDF(int code){
		EDialog = new Dialog(ctx, R.style.cust_dialog);
    	EDialog.getWindow();
        EDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		EDialog.setContentView(R.layout.error_errordialog);
		EDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		TextView tv_E = (TextView)EDialog.findViewById(R.id.error_tV);
		
		switch(code){
			case 0:
				tv_E.setText(R.string.pdf_upload_error);
				break;
				
			default:
				tv_E.setText(R.string.pdf_upload_error);
				break;
		}
		Button  Login_Error_Close_Button = (Button)EDialog.findViewById(R.id.error_button);
		
		Login_Error_Close_Button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {		
				EDialog.dismiss();				
			}
		});
    	
		EDialog.show();	
	}
	
	public void Incorrect_User(){
		EDialog = new Dialog(ctx, R.style.cust_dialog);
		EDialog.setContentView(R.layout.error_errordialog);
		EDialog.setTitle(R.string.incorrect_user);
		EDialog.show();
	}
	
}