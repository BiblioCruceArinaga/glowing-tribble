package com.rising.drawing;

import java.util.ArrayList;

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

public class BpmManagement {

	private Vista vista;
	private Partitura horizontalScore;
	private Partitura verticalScore;
	private ArrayList<OrdenDibujo> horizontalDrawing;
	private ArrayList<OrdenDibujo> verticalDrawing;
	private Config config;
	private Context context;
	private Dialog MDialog = null;
	
	public BpmManagement(Vista vista, Partitura horizontalScore, Partitura verticalScore,
			ArrayList<OrdenDibujo> horizontalDrawing, ArrayList<OrdenDibujo> verticalDrawing,
			Config config, Context context) {
		
		this.vista = vista;
		this.config = config;
		this.context = context;
		
		this.horizontalDrawing = horizontalDrawing;
		this.horizontalScore = horizontalScore;
		this.verticalDrawing = verticalDrawing;
		this.verticalScore = verticalScore;
	}
	
	public void tapManagement(MotionEvent e, Scroll scroll) {
		int compas;
		
		if (vista == Vista.VERTICAL)
			compas = compasAPartirDeTap(e.getX(), - scroll.getYOffset() + scroll.getYDown());
		else
			compas = compasAPartirDeTap(- scroll.getXOffset() + scroll.getXDown(), e.getY());
		
		establecerVelocidadAlCompas(compas);
	}
	
	//  Devuelve el índice del compás que se encuentra
	//  en la posición X e Y del tap del usuario
	private int compasAPartirDeTap(float x, float y) {
		ArrayList<Compas> compases;
		if (vista == Vista.VERTICAL)
			compases = verticalScore.getCompases();
		else
			compases = horizontalScore.getCompases();
		
		int numCompases = compases.size();
		for (int i=0; i<numCompases; i++) {
			if ( (compases.get(i).getYIni() <= y) && (y <= compases.get(i).getYFin()) ) {
				if ( (compases.get(i).getXIni() <= x) && (x < compases.get(i).getXFin()) ) {
					return i;
				}
			}
		}
		
		return -1;
	}
	
	private int dibujarBpm(Compas compas) {
		
		if (vista == Vista.VERTICAL) {
			verticalDrawing.add(new OrdenDibujo(config.getTamanoLetraBpm(), 
					false, "Bpm = " + compas.getBpm(), compas.getXIni(), 
					compas.getYIni() - config.getYBpm()));
			return verticalDrawing.size() - 1;
		}
		else {
			horizontalDrawing.add(new OrdenDibujo(config.getTamanoLetraBpm(), 
					false, "Bpm = " + compas.getBpm(), compas.getXIni(), 
					compas.getYIni() - config.getYBpm()));
			return horizontalDrawing.size() - 1;
		}
	}
	
	//  Prepara el diálogo que permitirá al usuario
	//  escoger una velocidad de metrónomo para este compás
	private void establecerVelocidadAlCompas(final int index) {
		MDialog = new Dialog(context,  R.style.cust_dialog);	
		MDialog.setContentView(R.layout.metronome_dialog_compas);
		MDialog.setTitle(R.string.metronome);
		MDialog.getWindow().setLayout(config.getAnchoDialogBpm(), config.getAltoDialogBpm());	

		final SeekBar seekBar_metronome = (SeekBar)MDialog.findViewById(R.id.seekBar_metronome);
		
		final NumberPicker metronome_speed = (NumberPicker)MDialog.findViewById(R.id.nm_metronome);
		metronome_speed.setMaxValue(300);
		metronome_speed.setMinValue(1);
		metronome_speed.setValue(120);
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
		seekBar_metronome.setProgress(120);
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
		
		ImageButton playButton = (ImageButton)MDialog.findViewById(R.id.playButton1);
		playButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				int bpm = metronome_speed.getValue();
												
				if ( (bpm < 1) || (bpm > 300) ) {
					Toast toast1 = Toast.makeText(context,
				                    R.string.speed_allowed, Toast.LENGTH_SHORT);
				    toast1.show();
				}
				else {
					Compas compas;
					if (vista == Vista.VERTICAL)
						compas = verticalScore.getCompas(index);
					else
						compas = horizontalScore.getCompas(index);
					
					compas.setBpm(bpm);
					
					if (compas.getBpmIndex() > -1) {
						if (vista == Vista.VERTICAL)
							verticalDrawing.set(compas.getBpmIndex(), null);
						else
							horizontalDrawing.set(compas.getBpmIndex(), null);
					}
					
					int bpmIndex = dibujarBpm(compas);
					compas.setBpmIndex(bpmIndex);
					
					MDialog.dismiss();
					MDialog = null;
				}
			}
		});
		
		ImageButton deleteButton = (ImageButton)MDialog.findViewById(R.id.playButton2);
		deleteButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Compas compas;
				if (vista == Vista.VERTICAL)
					compas = verticalScore.getCompas(index);
				else
					compas = horizontalScore.getCompas(index);

				if (compas.getBpmIndex() > -1) {
					if (compas.getBpmIndex() > -1) {
						if (vista == Vista.VERTICAL)
							verticalDrawing.set(compas.getBpmIndex(), null);
						else
							horizontalDrawing.set(compas.getBpmIndex(), null);
					}
					
					compas.setBpm(-1);
					compas.setBpmIndex(-1);
				}

				MDialog.dismiss();
				MDialog = null;
			}
		});
		
		MDialog.show();
	}
}
