package com.rising.drawing;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ActionBarMicrophone implements ActionMode.Callback {
	
	private transient boolean readingMicrophone;
	private transient Dialog microphoneDialog;
	private transient int sensibilidad = 10;
	private transient int velocidad = 10;
	
	private final transient Screen screen;
	private final transient Context context;
	
	public ActionBarMicrophone(final Screen screen, final Context context)
	{
		this.screen = screen;
		this.context = context;
	}
	
	@Override
	public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
		switch(item.getItemId()){
    		case R.id.microphone_sensitivity:
    			gestionarSensibilidad();
    			break;
    			
    		case R.id.microphone_speed:
    			gestionarVelocidad();
    			break;
    		
    		case R.id.microphone_start:    
    			
    			try {
    				if (readingMicrophone) {
    					item.setTitle(R.string.microphone_start);
    				}
    				else { 
    					item.setTitle(R.string.microphone_stop);
    				}
    				
    				gestionarMicrofono();
    				
    			} catch (Exception e) {
    				Log.i("Microphone reading error", e.getMessage());
    			}
    			
    			break;
    			
    		default:
    			break;
    	}
		return true;
	}

	@Override
	public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
		mode.getMenuInflater().inflate(R.menu.microphone_menu, menu);			
        return true;
	}

	@Override
	public void onDestroyActionMode(final ActionMode arg0) {
		screen.stopMicrophone();
		readingMicrophone = false;
	}

	@Override
	public boolean onPrepareActionMode(final ActionMode arg0, final Menu arg1) {
		return false;
	}
	
	private void gestionarMicrofono() throws Exception {
		if (readingMicrophone) {
			screen.stopMicrophone();
			
			readingMicrophone = false;
		}
		else {
			screen.Back();
			screen.readMicrophone(sensibilidad, velocidad);
			
			readingMicrophone = true;
		}
	}
	
	private void gestionarSensibilidad() {
		microphoneDialog = new Dialog(context, R.style.cust_dialog);	
		microphoneDialog.setContentView(R.layout.microphone_sensitivity_dialog);
		microphoneDialog.setTitle(R.string.setSensitivity);	
		microphoneDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					
		final TextView texto = (TextView) microphoneDialog.findViewById(R.id.sensitivityValue);
		final SeekBar seekBar = (SeekBar) microphoneDialog.findViewById(R.id.sensitivityBar);
		
		seekBar.setProgress(sensibilidad);
		texto.setText(Integer.toString(sensibilidad));
	
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
				texto.setText(Integer.toString(seekBar.getProgress()));
			}

			@Override
			public void onStartTrackingTouch(final SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(final SeekBar seekBar) {}
		});
		
		final Button sensitivityButton = (Button) microphoneDialog.findViewById(R.id.sensitivityButton);
		sensitivityButton.setOnClickListener(new OnClickListener(){
 
			@Override
			public void onClick(final View view) {
				sensibilidad = seekBar.getProgress();
				microphoneDialog.dismiss();
			}
		});
		
		microphoneDialog.show();
	}
	
	private void gestionarVelocidad() {
		microphoneDialog = new Dialog(context, R.style.cust_dialog);	
		microphoneDialog.setContentView(R.layout.microphone_speed_dialog);
		microphoneDialog.setTitle(R.string.setSpeed);	
		
		final TextView texto = (TextView) microphoneDialog.findViewById(R.id.speedValue);
		final SeekBar seekBar = (SeekBar) microphoneDialog.findViewById(R.id.speedBar);
		
		seekBar.setProgress(velocidad);
		texto.setText(Integer.toString(velocidad));
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
				texto.setText(Integer.toString(seekBar.getProgress()));
			}

			@Override
			public void onStartTrackingTouch(final SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(final SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
		});
		
		final Button speedButton = (Button) microphoneDialog.findViewById(R.id.speedButton);
		speedButton.setOnClickListener(new OnClickListener(){
 
			@Override
			public void onClick(final View view) {
				velocidad = seekBar.getProgress();
				microphoneDialog.dismiss();
			}
		});
		
		microphoneDialog.show();
	}
}
