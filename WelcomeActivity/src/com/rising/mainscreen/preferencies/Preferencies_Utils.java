package com.rising.mainscreen.preferencies;

import com.rising.drawing.R;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class Preferencies_Utils {

	private Context ctx;
	private Dialog MDialog;
	
	public Preferencies_Utils(Context context){
		this.ctx = context;
	}
	
	public void Legal_Displays(String text){
		MDialog = new Dialog(ctx, R.style.cust_dialog);
		MDialog.setContentView(R.layout.preferencies_legaldisplay);
		MDialog.setTitle(R.string.terminos_condiciones);
		MDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		TextView Text = (TextView) MDialog.findViewById(R.id.textView1);
		Text.setText(text);
		MDialog.show();
	}
	
}