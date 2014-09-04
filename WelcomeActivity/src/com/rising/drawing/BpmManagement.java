package com.rising.drawing;

import java.util.ArrayList;

import com.rising.drawing.figurasgraficas.Compas;
import com.rising.drawing.figurasgraficas.OrdenDibujo;
import com.rising.drawing.figurasgraficas.Partitura;
import com.rising.drawing.figurasgraficas.Vista;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Toast;

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
		
		establecerVelocidadAlCompas(compas, 120);
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
	
	private void establecerVelocidadAlCompas(final int index, final int value) 
	{
		mDialog = StaticMethods.initializeDialog(context, R.layout.metronome_dialog_compas);
		mDialog.getWindow().setLayout(config.anchoDialogBpm, config.altoDialogBpm);

		final SeekBar seekBarMetronome = (SeekBar)mDialog.findViewById(R.id.seekBar_metronome);
		final NumberPicker metronomeSpeed = (NumberPicker)mDialog.findViewById(R.id.nm_metronome);
		
		StaticMethods.initializeMetronomeSpeed(metronomeSpeed, seekBarMetronome, value);
		StaticMethods.initializeSeekBarMetronome(seekBarMetronome, metronomeSpeed, value);
		
		final ImageButton playButton = (ImageButton)mDialog.findViewById(R.id.playButton1);
		playButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View view) {
				establecerBpm(metronomeSpeed, index);
			}
		});
		
		final ImageButton deleteButton = (ImageButton)mDialog.findViewById(R.id.playButton2);
		deleteButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View view) {
				borrarBpm(index);
			}
		});
		
		mDialog.show();
	}
	
	private void establecerBpm(final NumberPicker metronomeSpeed, int index)
	{
		final int bpm = metronomeSpeed.getValue();
		
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
