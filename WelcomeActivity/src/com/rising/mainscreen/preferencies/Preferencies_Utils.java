package com.rising.mainscreen.preferencies;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.util.Linkify;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.rising.drawing.R;

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
	
	public void AboutDialog(){
    	MDialog = new Dialog(ctx, R.style.AppBaseTheme);
		MDialog.setContentView(R.layout.preferencies_about);
		
		MDialog.setTitle(R.string.about);
		MDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		TextView Link_Web = (TextView) MDialog.findViewById(R.id.link);
		Link_Web.setLinkTextColor(Color.BLACK);
		Linkify.addLinks(Link_Web, Linkify.ALL);
		TextView Link_Metronome_Icon = (TextView) MDialog.findViewById(R.id.metronome_link);
		Link_Metronome_Icon.setLinkTextColor(Color.BLACK);
		Linkify.addLinks(Link_Metronome_Icon, Linkify.ALL);
		MDialog.show();
	}
	
}