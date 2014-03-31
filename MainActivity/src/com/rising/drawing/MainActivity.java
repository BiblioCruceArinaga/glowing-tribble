package com.rising.drawing;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.rising.drawing.R;
import com.rising.mainscreen.MainScreenActivity;

public class MainActivity extends Activity {

    ScreenThread myScreenThread;
    SurfaceHolder holder;
	Canvas canvas;
	Screen s;
	private Dialog MDialog;
	private Dialog CDialog;
	private ImageButton playButton;
	private NumberPicker metronome_speed;
	private TextView countdown;
	private int tempo = 120;
	String score;
	private boolean play;
	private boolean stop = false;

	private ActionMode mActionMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);	
		Bundle b = this.getIntent().getExtras();
		score = b.getString("score");
		
		setContentView(new Screen(this, score));
		
		s = new Screen(this, score);
		myScreenThread = new ScreenThread(holder, s);
		
		ActionBar aBar = getActionBar();	
		aBar.setTitle(R.string.pa);	
		aBar.setIcon(R.drawable.ic_menu);
		aBar.setDisplayHomeAsUpEnabled(true);			
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
	    	case R.id.metronome_button:
	    		metronome_options(tempo);
	    		return true;
	    	
	    	case android.R.id.home:
	    		Intent i = new Intent(this, MainScreenActivity.class);
	    		startActivity(i);
	    		finish();
	    		return true;

	    	default:
	    		return true;
	    }
	}
	
	//  M�todo que controla el dialog de las opciones del metr�nomo
	private void metronome_options(int value){
		MDialog = new Dialog(MainActivity.this);
		
		MDialog.setContentView(R.layout.metronome_dialog);
		MDialog.setTitle(R.string.metronome);
		
		//  Cambia el tama�o de la ventana de di�logo
		MDialog.getWindow().setLayout(350, 420);
									
		playButton = (ImageButton)MDialog.findViewById(R.id.playButton1);
		metronome_speed = (NumberPicker)MDialog.findViewById(R.id.nm_metronome);
		
		metronome_speed.setMaxValue(300);
		metronome_speed.setMinValue(1);
		metronome_speed.setValue(value);
		metronome_speed.setWrapSelectorWheel(true);
		metronome_speed.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
						
		playButton.setOnClickListener(new OnClickListener(){
 
			@Override
			public void onClick(View v) {
				tempo = metronome_speed.getValue();
				mActionMode = MainActivity.this.startActionMode(new ActionBarCallBack());
				DialogCountdown();
				MDialog.dismiss();
			}
			
		});
		MDialog.show();
	}
	
	//Método que controla el dialog de la cuenta atr�s
	private void DialogCountdown(){
		CDialog = new Dialog(MainActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		CDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		CDialog.setContentView(R.layout.countdown_dialog);
		
		//Cambia el tama�o de la ventana de dialogo
		CDialog.getWindow().setLayout(200, 200);
		CDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
				
		countdown = (TextView)CDialog.findViewById(R.id.count);
		new CountDownTimer(4000, 1000){
			
			public void onTick(long millisUntilFinished) {
			     countdown.setText("" + millisUntilFinished / 1000);
			 }

			public void onFinish() {
				play = true;
				stop = false;
				
				s.Metronome_Play(tempo);
			    CDialog.dismiss();
			 }
		}.start();
		
		CDialog.show();
	}
	
	//Cambia el icono entre el pause y el play dependiendo del estado del metrónomo
	private void PlayButton_Status(MenuItem item){
		if(play){
			play = false;
			stop = false;
		}else{
			play = true;
			
		}
		
		if(play == false){
    		item.setIcon(R.drawable.play_button);
    		s.Metronome_Pause();    		
    	}else{
    		item.setIcon(R.drawable.pause_button);
    		if(stop){
    			s.Metronome_Play(tempo);
    		}else{
    			s.Metronome_Pause();
    		}
    	}
	}
	
	//Si pulso el Stop todos los enables se ponen a True y el pause se cambia por el icono play
	private void StopButton_Status(ActionMode m){

		stop = true;
		play = false;
		
		Menu menu = m.getMenu();
		s.Metronome_Stop();
        if(stop){
        	menu.getItem(2).setIcon(R.drawable.play_button);
    	}else{
    		menu.getItem(2).setIcon(R.drawable.pause_button);
    	}
	}
	
	// Habilita o deshabilita elementos seg�n est� o no activado el metr�nomo
	private void PlayItemsControl(ActionMode m){
		
		Menu menu = m.getMenu();
		
		if(stop && !play){
			menu.getItem(0).setEnabled(true);
			menu.getItem(1).setEnabled(true);
			menu.getItem(5).setEnabled(true);
		}else{
			menu.getItem(0).setEnabled(false);
			menu.getItem(1).setEnabled(false);
			menu.getItem(5).setEnabled(false);
		}
	}
	
	//Esto abre la ActionBar contextual
	class ActionBarCallBack implements ActionMode.Callback {
		  
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        	switch(item.getItemId()){
        		case R.id.close_metronome:
        			s.Metronome_Stop();
        			mode.finish();
        			tempo = 0;
        			break;
        		
        		case R.id.metronome_menu_back:    
        			s.Metronome_Back();
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
        			
        	}
        	        	
            return true;
        }
  
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(R.string.metronome);
                        
        	//menu.add(0, 0, Menu.NONE, "custom").setActionView(R.layout.header).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                        
            mode.getMenuInflater().inflate(R.menu.metronome_menu, menu);
                        
        	MenuItem item = menu.findItem(R.id.metronome_tempo);
            item.setTitle("" + tempo);

            return true;
        }
  
        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
  
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        	play = true;
        	stop = false;
        	PlayItemsControl(mode);
            return true;
        }
    }
}
