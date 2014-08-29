package com.rising.drawing;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
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
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity{

	private static final int MAX_WIDTH = 800;
	
    private transient SurfaceHolder holder;
	private transient Screen screen;
	private transient String score;
	private transient Config config;
	private transient Dialog mDialog;

	//  Gestión del metrónomo
	private transient NumberPicker metronomeSpeed;
	private transient SeekBar seekBarMetronome;
	private transient int tempo = 120;
	
	@Override
	protected void onCreate(final Bundle savedInstance){
		super.onCreate(savedInstance);	
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		
		final Display display = getWindowManager().getDefaultDisplay();
		final Point size = new Point();
		display.getSize(size);
		final int width = size.x;
				
		if (width <= MAX_WIDTH) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}		
		
		final Bundle bundle = this.getIntent().getExtras();
		score = bundle.getString("score");

		final ActionBar aBar = getActionBar();	
		aBar.setTitle(R.string.score);	
		aBar.setIcon(R.drawable.ic_menu);
		aBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		final DisplayMetrics display = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(display);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		screen = new Screen(this, score, display.widthPixels, display.heightPixels, display.densityDpi);
		if (screen.isValidScreen()) {
			new ScreenThread(holder, screen);
			config = Config.getInstance();
		}else{
			Log.e("Valid Screen", "Is not a valid screen");
		}
		setContentView(screen);	
	}
	
	@Override
	protected void onPause() {
		super.onPause();	
	}
	
	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		return super.onContextItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.main_score, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) 
	{
	    switch (item.getItemId()) 
	    {
		    case R.id.change_view:
		    	changeView(item);
		    	break;
	    
	    	case R.id.metronome_button:
	    		metronomeOptions(tempo);
	    		break;
	    		
	    	case R.id.readSound_Button:
	    		microphoneOptions();
	    		break;
	    	
	    	case R.id.navigate_top:
	    		screen.Back();
	    		break;
	    		
	    	case R.id.navigate_bottom:
	    		screen.Forward();
	    		break;
	    		
	    	case R.id.navigate_to_bar:
	    		navigateToBar();
				break;
	    		
	    	case android.R.id.home:
	    		screen.Metronome_Stop();
	    		finish();
	    		break;
	    		
	    	default:
	    		break;
	    }
	    
	    return true;
	}

	@Override
	public void onActionModeFinished (final ActionMode mode) {
		screen.Metronome_Stop();
	}
	
	private void changeView(final MenuItem item)
	{
		if (item.getTitle().toString().equals(getString(R.string.panoramic_view))) {
    		screen.cambiarVista(Vista.HORIZONTAL);
    		item.setTitle(R.string.vertical_view);
    	}
    	else {
    		screen.cambiarVista(Vista.VERTICAL);
    		item.setTitle(R.string.panoramic_view);
    	}
	}
	
	private void microphoneOptions() 
	{
		MainActivity.this.startActionMode(new ActionBarMicrophone(screen, MainActivity.this));
	}
	
	private void metronomeOptions(final int value){
		final WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		final Display display = windowManager.getDefaultDisplay();
		final Point screenSize = new Point();
		display.getSize(screenSize);
		final int screenWith = screenSize.x;
		final int screenHeight = screenSize.y;
		Log.i("Window", screenWith + ", " + screenHeight);
		
		mDialog = new Dialog(MainActivity.this, R.style.cust_dialog);	
		mDialog.setContentView(R.layout.metronome_dialog);
		mDialog.setTitle(R.string.metronome);
		mDialog.getWindow().setLayout(config.anchoDialogBpm, config.altoDialogBpm);
		
		seekBarMetronome = (SeekBar)mDialog.findViewById(R.id.seekBar_metronome);
		metronomeSpeed = (NumberPicker)mDialog.findViewById(R.id.nm_metronome);
					
		metronomeSpeed.setMaxValue(300);
		metronomeSpeed.setMinValue(1);
		metronomeSpeed.setValue(value);
		metronomeSpeed.setWrapSelectorWheel(true);
		metronomeSpeed.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		metronomeSpeed.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChange(final NumberPicker arg0, final int arg1) {
				// TODO Auto-generated method stub
				seekBarMetronome.setProgress(arg0.getValue());
			}
		});
			 
		seekBarMetronome.setMax(300);
		seekBarMetronome.setProgress(value);
		seekBarMetronome.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
				metronomeSpeed.setValue(progress);
				Log.i("Progress", Integer.toString(progress));
				
			}

			@Override
			public void onStartTrackingTouch(final SeekBar seekBar) {
				Log.i("Seek", "StartTracking");
				
			}

			@Override
			public void onStopTrackingTouch(final SeekBar seekBar) {
				Log.i("Seek", "StopTracking");
				
			}
		});
				
		final ImageButton playButton = (ImageButton)mDialog.findViewById(R.id.playButton1);
		playButton.setOnClickListener(new OnClickListener(){
 
			@Override
			public void onClick(final View view) {
				
				tempo = metronomeSpeed.getValue();
				
				if ( tempo > 0 && tempo < 301 ) {
					MainActivity.this.startActionMode(new ActionBarMetronome(screen, tempo));

					screen.Back();
					screen.Metronome_Play(tempo);
					mDialog.dismiss();
				}
				else {
					Toast.makeText(getApplicationContext(),
				                    R.string.speed_allowed, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		mDialog.show();
	}
	
	private void navigateToBar()
	{
		mDialog = new Dialog(MainActivity.this, R.style.cust_dialog);	
		mDialog.setContentView(R.layout.gotobar);
		mDialog.setTitle(R.string.navigate_to_bar);	

		final EditText barEditText = (EditText) mDialog.findViewById(R.id.editTextNumberOfBar);
		
		final Button barButton = (Button) mDialog.findViewById(R.id.buttonNumberOfBar);
		barButton.setOnClickListener(new OnClickListener(){
 
			@Override
			public void onClick(final View view) {
				final String barNumberString = barEditText.getText().toString();
				
				if (!screen.goToBar(Integer.parseInt(barNumberString))) {
					Toast.makeText(getApplicationContext(),
		                    R.string.wrong_bar_number, Toast.LENGTH_SHORT).show();
				}
				
				mDialog.dismiss();
			}
		});
		
		mDialog.show();
	}
}