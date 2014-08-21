package com.rising.money.social;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.rising.drawing.R;

public class Social_Utils {

	private Context ctx;
	private Dialog ADialog;
	
	public Social_Utils(Context context){
		this.ctx = context;
	}
	
    public void Dialog_Aviso(String Message){
    	    	
    	ADialog = new Dialog(ctx, R.style.cust_dialog);
    	ADialog.getWindow();
        ADialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		ADialog.setContentView(R.layout.error_errordialog);
		ADialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		TextView tv_A = (TextView)ADialog.findViewById(R.id.error_tV);
		
		tv_A.setText(Message);
		
		Button  Login_Error_Close_Button = (Button)ADialog.findViewById(R.id.error_button);
		
		Login_Error_Close_Button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {		
				ADialog.dismiss();				
			}
		});
    	
		ADialog.show();	
    }
    
	public long cantidadTotalHoras(long fechaInicial, long fechaFinal){
		long totalMinutos=0; 
		totalMinutos=((fechaFinal-fechaInicial)/1000/60/60); 
		return totalMinutos; 
	}
	
}