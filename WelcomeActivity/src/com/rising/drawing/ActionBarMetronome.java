package com.rising.drawing;

import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

public class ActionBarMetronome implements ActionMode.Callback {
	
	private transient boolean play;
	private transient boolean stop;
	private transient int tempo;
	private final transient Screen screen;
	
	public ActionBarMetronome(final Screen screen, final int tempo)
	{
		this.screen = screen;
		this.tempo = tempo;
	}
	
	@Override
    public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
    	switch(item.getItemId()){
    		case R.id.close_metronome:
    			screen.metronomeStop();
    			mode.finish();
    			tempo = 120;
    			break;
    		
    		case R.id.metronome_menu_back:    
    			screen.back();
    			break;
    			
    		case R.id.metronome_menu_pause:
    			playButtonStatus(item);
    			playItemsControl(mode);
    			
    			Log.e("Data", "Item: " + item.toString() + ", Play: " + play + ", Stop: " + stop);
    			break;
    		
    		case R.id.metronome_menu_stop:
    			stopButtonStatus(mode);
    			playItemsControl(mode);
            	   
            	Log.e("Data", "Item: " + item.toString() + ", Play: " + play + ", Stop: " + stop);  			       			        			
    			break;

    		case R.id.metronome_tempo:
    			//metronomeOptions(tempo);
    			break;
    			
    		default:
    			break;
    	}
    	        	
        return true;
    }

    @Override
    public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
        mode.getMenuInflater().inflate(R.menu.metronome_menu, menu);
                    
    	final MenuItem item = menu.findItem(R.id.metronome_tempo);
        item.setTitle(Integer.toString(tempo));

        return true;
    }

    @Override
    public void onDestroyActionMode(final ActionMode mode) { }

    @Override
    public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
    	if (!stop) {
        	play = true;
        	playItemsControl(mode);
    	}
    	
    	return true;
    }
    
    //  Cambia el icono entre el pause y el play dependiendo del estado del metrónomo
	private void playButtonStatus(final MenuItem item){
		if (play) {
			play = false;
			stop = false;
		}
		else {
			play = true;
		}
		
		if (play){
			item.setIcon(R.drawable.pause_button);
    		
    		if (stop) {
    			screen.metronomePlay(tempo);
    		}
    		else {
    			screen.metronomePause();
    		}	
    	}else{
    		item.setIcon(R.drawable.play_button);
    		screen.metronomePause();
    	}
	}

	private void stopButtonStatus(final ActionMode mode){
		stop = true;
		play = false;
		
		screen.metronomeStop();
		
		final Menu menu = mode.getMenu();
        menu.getItem(2).setIcon(R.drawable.play_button);
	}
	
	//  Habilita o deshabilita elementos según esté o no activado el metrónomo
	private void playItemsControl(final ActionMode mode){
		final Menu menu = mode.getMenu();
		
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
}
