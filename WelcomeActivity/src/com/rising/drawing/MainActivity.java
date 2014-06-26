package com.rising.drawing;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{

    ScreenThread myScreenThread;
    SurfaceHolder holder;
	Canvas canvas;
	Screen s;
	String score;
	private Config config = null;
	
	//  Gestión del metrónomo
	private Dialog MDialog;
	private ImageButton playButton;
	private NumberPicker metronome_speed;
	private SeekBar seekBar_metronome;
	private int tempo = 120;
	private boolean play;
	private boolean stop = false;

	//  Gestión del micrófono
	private boolean readingMicrophone = false;
	private Dialog MicrophoneDialog = null;
	private int sensibilidad = 5;
	private int velocidad = 5;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);	
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
				
		if(width<= 800){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}		
		
		Bundle b = this.getIntent().getExtras();
		score = b.getString("score");

		ActionBar aBar = getActionBar();	
		aBar.setTitle(R.string.score);	
		aBar.setIcon(R.drawable.ic_menu);
		aBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		s = new Screen(this, score, dm.widthPixels, dm.heightPixels ,dm.densityDpi);
		if (s.isValidScreen()) {
			myScreenThread = new ScreenThread(holder, s);
			config = s.getConfig();
		}else{
			Log.e("Valid Screen", "Is not a valid screen");
		}
		setContentView(s);	
	}
	
	@Override
	protected void onPause() {
		super.onPause();	
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return super.onContextItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_score, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case R.id.change_view:
		    	if (item.getTitle().toString().equals(getString(R.string.panoramic_view))) {
		    		s.cambiarVista(Vista.HORIZONTAL);
		    		item.setTitle(R.string.vertical_view);
		    	}
		    	else {
		    		s.cambiarVista(Vista.VERTICAL);
		    		item.setTitle(R.string.panoramic_view);
		    	}
		    	return true;
	    
	    	case R.id.metronome_button:
	    		metronome_options(tempo);
	    		return true;
	    		
	    	case R.id.readSound_Button:
	    		microphone_options();
	    		return true;
	    	
	    	case R.id.navigate_top:
	    		s.Back();
	    		return true;
	    		
	    	case R.id.navigate_bottom:
	    		s.Forward();
	    		return true;
	    		
	    	case R.id.navigate_to_bar:
	    		MDialog = new Dialog(MainActivity.this, R.style.cust_dialog);	
				MDialog.setContentView(R.layout.gotobar);
				MDialog.setTitle(R.string.navigate_to_bar);	

				final EditText barEditText = (EditText) MDialog.findViewById(R.id.editTextNumberOfBar);
				
				Button barButton = (Button) MDialog.findViewById(R.id.buttonNumberOfBar);
				barButton.setOnClickListener(new OnClickListener(){
		 
					@Override
					public void onClick(View v) {
						String barNumberString = barEditText.getText().toString();
						
						if (!s.goToBar(Integer.parseInt(barNumberString)))
							Toast.makeText(getApplicationContext(),
				                    R.string.wrong_bar_number, Toast.LENGTH_SHORT).show();
						
						MDialog.dismiss();
					}
				});
				
				MDialog.show();
	    		return true;
	    		
	    	case android.R.id.home:
	    		s.Metronome_Stop();
	    		finish();
	    		return true;
	    		
	    	default:
	    		return true;
	    }
	}

	@Override
	public void onActionModeFinished (ActionMode mode) {
		s.Metronome_Stop();
	}
	
	/*
	 * 
	 * MÉTODOS DEL MICRÓFONO
	 * 
	 */
	public void microphone_options() {
		MainActivity.this.startActionMode(new ActionBarMicrophone());
	}
	
	private class ActionBarMicrophone implements ActionMode.Callback {

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch(item.getItemId()){
	    		case R.id.microphone_sensitivity:
	    			gestionarSensibilidad();
	    			break;
	    			
	    		case R.id.microphone_speed:
	    			gestionarVelocidad();
	    			break;
	    		
	    		case R.id.microphone_start:    
	    			
	    			try {
	    				if (readingMicrophone) item.setTitle(R.string.microphone_start);
	    				else item.setTitle(R.string.microphone_stop);
	    				
	    				gestionarMicrofono();
	    				
	    			} catch (Exception e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
	    			
	    			break;
	    			
	    		default:
	    			break;
	    	}
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.microphone_menu, menu);			
            return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode arg0) {
			s.stopMicrophone();
			readingMicrophone = false;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
			// TODO Auto-generated method stub
			return false;
		}
		
		private void gestionarMicrofono() throws Exception {
			if (readingMicrophone) {
				s.stopMicrophone();
				
				readingMicrophone = false;
			}
			else {
				s.Back();
				s.readMicrophone(sensibilidad, velocidad);
				
				readingMicrophone = true;
			}
		}
		
		private void gestionarSensibilidad() {
			MicrophoneDialog = new Dialog(MainActivity.this, R.style.cust_dialog);	
			MicrophoneDialog.setContentView(R.layout.microphone_sensitivity_dialog);
			MicrophoneDialog.setTitle(R.string.setSensitivity);	
			MicrophoneDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
						
			final TextView texto = (TextView) MicrophoneDialog.findViewById(R.id.sensitivityValue);
			final SeekBar seekBar = (SeekBar) MicrophoneDialog.findViewById(R.id.sensitivityBar);
			
			seekBar.setProgress(sensibilidad);
			texto.setText(sensibilidad + "");
		
			seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					texto.setText(seekBar.getProgress() + "");
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
				}
			});
			
			Button sensitivityButton = (Button) MicrophoneDialog.findViewById(R.id.sensitivityButton);
			sensitivityButton.setOnClickListener(new OnClickListener(){
	 
				@Override
				public void onClick(View v) {
					sensibilidad = seekBar.getProgress();
					MicrophoneDialog.dismiss();
				}
			});
			
			MicrophoneDialog.show();
		}
		
		private void gestionarVelocidad() {
			MicrophoneDialog = new Dialog(MainActivity.this, R.style.cust_dialog);	
			MicrophoneDialog.setContentView(R.layout.microphone_speed_dialog);
			MicrophoneDialog.setTitle(R.string.setSpeed);	
			
			final TextView texto = (TextView) MicrophoneDialog.findViewById(R.id.speedValue);
			final SeekBar seekBar = (SeekBar) MicrophoneDialog.findViewById(R.id.speedBar);
			
			seekBar.setProgress(velocidad);
			texto.setText(velocidad + "");
			
			seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					texto.setText(seekBar.getProgress() + "");
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
				}
			});
			
			Button speedButton = (Button) MicrophoneDialog.findViewById(R.id.speedButton);
			speedButton.setOnClickListener(new OnClickListener(){
	 
				@Override
				public void onClick(View v) {
					velocidad = seekBar.getProgress();
					MicrophoneDialog.dismiss();
				}
			});
			
			MicrophoneDialog.show();
		}
	}
	
	/*
	 * 
	 * MÉTODOS DEL METRÓNOMO
	 * 
	 */
	private void metronome_options(int value){
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point screenSize = new Point();
		display.getSize(screenSize);
		int screenWith = screenSize.x;
		int screenHeight = screenSize.y;
		Log.i("Window", screenWith + ", " + screenHeight);
		
		MDialog = new Dialog(MainActivity.this, R.style.cust_dialog);	
		MDialog.setContentView(R.layout.metronome_dialog);
		MDialog.setTitle(R.string.metronome);
		MDialog.getWindow().setLayout(config.getAnchoDialogBpm(), config.getAltoDialogBpm());
		
		seekBar_metronome = (SeekBar)MDialog.findViewById(R.id.seekBar_metronome);
		metronome_speed = (NumberPicker)MDialog.findViewById(R.id.nm_metronome);
					
		metronome_speed.setMaxValue(300);
		metronome_speed.setMinValue(1);
		metronome_speed.setValue(value);
		metronome_speed.setWrapSelectorWheel(true);
		metronome_speed.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		metronome_speed.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChange(NumberPicker arg0, int arg1) {
				// TODO Auto-generated method stub
				seekBar_metronome.setProgress(arg0.getValue());
			}
		});
			 
		seekBar_metronome.setMax(300);
		seekBar_metronome.setProgress(value);
		seekBar_metronome.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				metronome_speed.setValue(progress);
				Log.i("Progress", progress + "");
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				Log.i("Seek", "StartTracking");
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Log.i("Seek", "StopTracking");
				
			}
		});
				
		playButton = (ImageButton)MDialog.findViewById(R.id.playButton1);
		playButton.setOnClickListener(new OnClickListener(){
 
			@Override
			public void onClick(View v) {
				
				tempo = metronome_speed.getValue();
								
				//Aquí, en vez de los números deben ir las lineas
				/*if(numeros_checkbox.isChecked()){
					numeros_bip = true;
				}else{
					numeros_bip = false;
				}*/
				
				if ( (tempo > 0) && (tempo < 301) ) {
					MainActivity.this.startActionMode(new ActionBarCallBack());
					
					play = true;
					stop = false;
					s.Back();
					s.Metronome_Play(tempo);
					MDialog.dismiss();
				}
				else {
					Toast.makeText(getApplicationContext(),
				                    R.string.speed_allowed, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		MDialog.show();
	}

	//  Cambia el icono entre el pause y el play dependiendo del estado del metrónomo
	private void PlayButton_Status(MenuItem item){
		if (play) {
			play = false;
			stop = false;
		}
		else
			play = true;
		
		if (!play){
    		item.setIcon(R.drawable.play_button);
    		s.Metronome_Pause();    		
    	}else{
    		item.setIcon(R.drawable.pause_button);
    		
    		if (stop)
    			s.Metronome_Play(tempo);
    		else
    			s.Metronome_Pause();
    	}
	}

	private void StopButton_Status(ActionMode m){
		stop = true;
		play = false;
		
		s.Metronome_Stop();
		
		Menu menu = m.getMenu();
        menu.getItem(2).setIcon(R.drawable.play_button);
	}
	
	//  Habilita o deshabilita elementos según esté o no activado el metrónomo
	private void PlayItemsControl(ActionMode m){
		Menu menu = m.getMenu();
		
		if(stop && !play){
			menu.getItem(0).setEnabled(true);
			menu.getItem(1).setEnabled(true);
			menu.getItem(4).setEnabled(true);
		}else{
			menu.getItem(0).setEnabled(false);
			menu.getItem(1).setEnabled(false);
			menu.getItem(4).setEnabled(true);
		}
	}
	
	//  Esto abre el ActionBar contextual
	class ActionBarCallBack implements ActionMode.Callback {
		  
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        	switch(item.getItemId()){
        		case R.id.close_metronome:
        			s.Metronome_Stop();
        			mode.finish();
        			tempo = 120;
        			break;
        		
        		case R.id.metronome_menu_back:    
        			s.Back();
        			break;
        			
        		case R.id.metronome_menu_pause:
        			PlayButton_Status(item);
        			PlayItemsControl(mode);
        			
        			Log.e("Data", "Item: " + item.toString() + ", Play: " + play + ", Stop: " + stop);
        			break;
        		
        		case R.id.metronome_menu_stop:
        			StopButton_Status(mode);
        			PlayItemsControl(mode);
                	   
                	Log.e("Data", "Item: " + item.toString() + ", Play: " + play + ", Stop: " + stop);  			       			        			
        			break;

        		case R.id.metronome_tempo:
        			metronome_options(tempo);
        			break;
        			
        		default:
        			break;
        	}
        	        	
            return true;
        }
  
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            //mode.setTitle(R.string.metronome);
            mode.getMenuInflater().inflate(R.menu.metronome_menu, menu);
                        
        	MenuItem item = menu.findItem(R.id.metronome_tempo);
            item.setTitle("" + tempo);

            return true;
        }
  
        @Override
        public void onDestroyActionMode(ActionMode mode) { }
  
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        	if (stop)
        		return true;
        	else {
	        	play = true;
	        	stop = false;
	        	PlayItemsControl(mode);
	            return true;
        	}
        }
    }
}
