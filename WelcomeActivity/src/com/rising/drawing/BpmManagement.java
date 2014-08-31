package com.rising.drawing;

import java.util.ArrayList;

import com.rising.drawing.figurasGraficas.Compas;
import com.rising.drawing.figurasGraficas.Partitura;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class BpmManagement 
{
	private final transient Partitura partitura;
	private final transient ArrayList<OrdenDibujo> ordenesDibujo;
	private final transient Config config;
	private final transient Context context;
	private transient Dialog mDialog;
	
	public BpmManagement(final Partitura partitura, 
			final ArrayList<OrdenDibujo> ordenesDibujo, final Context context) 
	{
		this.config = Config.getInstance();
		this.context = context;
		this.partitura = partitura;
		this.ordenesDibujo = ordenesDibujo;
	}
	
	public void tapManagement(final MotionEvent evt, final AbstractScroll scroll, final Vista vista) 
	{
		int compas;
		
		if (vista == Vista.VERTICAL) {
			compas = compasAPartirDeTap(evt.getX(), - scroll.getCooOffset() + scroll.getCooDown());
		} else {
			compas = compasAPartirDeTap(- scroll.getCooOffset() + scroll.getCooDown(), evt.getY());
		}
		
		establecerVelocidadAlCompas(compas);
	}
	
	//  Devuelve el índice del compás que se encuentra
	//  en la posición X e Y del tap del usuario
	private int compasAPartirDeTap(final float x, final float y) 
	{
		int compasIndex = -1;
		final ArrayList<Compas> compases = partitura.getCompases();
		final int numCompases = compases.size();
		
		for (int i=0; i<numCompases; i++) 
		{
			if (tapDentroDeLimitesDelCompas(compases.get(i), x, y)) {
				compasIndex = i;
				break;
			}
		}
		
		return compasIndex;
	}
	
	private boolean tapDentroDeLimitesDelCompas(final Compas compas, final float x, final float y)
	{
		return ( compas.getYIni() <= y && y <= compas.getYFin() ) &&
			 ( compas.getXIni() <= x && x < compas.getXFin() );
	}
	
	private void establecerVelocidadAlCompas(final int index) {
		mDialog = new Dialog(context,  R.style.cust_dialog);	
		mDialog.setContentView(R.layout.metronome_dialog_compas);
		mDialog.setTitle(R.string.metronome);
		mDialog.getWindow().setLayout(config.anchoDialogBpm, config.altoDialogBpm);	

		final SeekBar seekBar_metronome = (SeekBar)mDialog.findViewById(R.id.seekBar_metronome);
		
		final NumberPicker metronome_speed = (NumberPicker)mDialog.findViewById(R.id.nm_metronome);
		metronome_speed.setMaxValue(300);
		metronome_speed.setMinValue(1);
		metronome_speed.setValue(120);
		metronome_speed.setWrapSelectorWheel(true);
		metronome_speed.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		metronome_speed.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChange(final NumberPicker arg0, final int arg1) {
				// TODO Auto-generated method stub
				seekBar_metronome.setProgress(arg0.getValue());
			}
		});
		
		seekBar_metronome.setMax(300);
		seekBar_metronome.setProgress(120);
		seekBar_metronome.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
				metronome_speed.setValue(progress);
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
				establecerBpm(metronome_speed, index);
			}
		});
		
		final ImageButton deleteButton = (ImageButton)mDialog.findViewById(R.id.playButton2);
		deleteButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View view) {
				borrarBpm(index);
			}
		});
		
		mDialog.show();
	}
	
	private void establecerBpm(final NumberPicker metronome_speed, int index)
	{
		final int bpm = metronome_speed.getValue();
		
		if ( bpm < 1 || bpm > 300 ) {
			final Toast toast1 = Toast.makeText(context,
		                    R.string.speed_allowed, Toast.LENGTH_SHORT);
		    toast1.show();
		}
		else {
			final Compas compas = partitura.getCompas(index);
			compas.setBpm(bpm);
			
			if (compas.getBpmIndex() > -1) {
				ordenesDibujo.set(compas.getBpmIndex(), null);
			}
			
			final int bpmIndex = dibujarBpm(compas);
			compas.setBpmIndex(bpmIndex);
			
			mDialog.dismiss();
		}
	}
	
	private int dibujarBpm(final Compas compas) 
	{
		ordenesDibujo.add(new OrdenDibujo(config.tamanoLetraBpm, 
			false, "Bpm = " + compas.getBpm(), compas.getXIni(), 
				compas.getYIni() - config.yBpm));
		
		return ordenesDibujo.size() - 1;
	}
	
	private void borrarBpm(int index)
	{
		final Compas compas = partitura.getCompas(index);

		if (compas.getBpmIndex() > -1) {
			ordenesDibujo.set(compas.getBpmIndex(), null);
			
			compas.setBpm(-1);
			compas.setBpmIndex(-1);
		}

		mDialog.dismiss();
	}
}
