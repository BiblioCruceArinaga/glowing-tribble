package com.rising.drawing;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SeekBar;

import com.rising.drawing.figurasgraficas.Partitura;
import com.rising.drawing.figurasgraficas.Vista;

/**
 * Bunch of helper methods used by different classes.
 * These methods allow us to avoid duplicity
 */

public class StaticMethods 
{
	private StaticMethods() {}
	
	public static String convertByteArrayListToString(
			final ArrayList<Byte> bytes, int start)
	{
		byte[] bytesArray = new byte[bytes.size()];
        for (int i=start; i<bytesArray.length; i++) 
        {
        	bytesArray[i] = bytes.get(i);
        }
        
        try {
            return new String(bytesArray, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return "";
        }
	}
	
	public static String sanitizeString(final String oldString) 
	{
		String newString = oldString;
		
    	final int index = oldString.indexOf('&');
    	if (index > -1) {
    		switch (oldString.charAt(index - 1)) {
	    		case 'a':
	    			newString = oldString.replace("a&", "á");
	    			break;
	    		case 'e':
	    			newString = oldString.replace("e&", "é");
	    			break;
	    		case 'i':
	    			newString = oldString.replace("i&", "í");
	    			break;
	    		case 'o':
	    			newString = oldString.replace("o&", "ó");
	    			break;
	    		case 'u':
	    			newString = oldString.replace("u&", "ú");
	    			break;
	    		case 'n':
	    			newString = oldString.replace("n&", "ñ");
	    			break;
    			default:
    				break;
    		}
    	}
    	
    	return newString;
    }
	
	@SuppressWarnings("rawtypes")
	public static Object[] joinToArrayListsIntoOneArray(ArrayList array1, ArrayList array2)
	{
		final int lengthArray1 = array1.size();
		final int totalLength = lengthArray1 + array2.size();
		Object[] joinedArrays = new Object[totalLength];
		
		int i = 0;
		for (; i<lengthArray1; i++) {
			joinedArrays[i] = array1.get(i);
		}
		for (int j=0; i<totalLength; i++) {
			joinedArrays[i] = array2.get(j++);
		}
		
		return joinedArrays;
	}
	
	public static int manageScroll(final int yFin, final Vista vista, final int xActual,
			final AbstractScroll scroll, final Partitura partitura, int primerCompas, final int compasActual)
	{
		final int yActual = yFin;
		final int coo = vista == Vista.VERTICAL ? yActual : xActual;
		/*
		if (scroll.outOfBoundaries(coo)) 
		{
			
			final int desplazamiento = 
				scroll.distanciaDesplazamiento(partitura, 
					primerCompas, compasActual);
			*/
			scroll.hacerScroll(10);
			/*
			primerCompas = compasActual;
		}
		*/
		return primerCompas;
	}
	
	public static Dialog initializeDialog(final Context context, final int layout)
	{
		Dialog dialog = new Dialog(context, R.style.cust_dialog);	
		dialog.setContentView(layout);
		dialog.setTitle(R.string.metronome);

		return dialog;
	}
	
	public static void initializeMetronomeSpeed(final NumberPicker metronomeSpeed,
			final SeekBar seekBarMetronome, final int value)
	{
		metronomeSpeed.setMaxValue(300);
		metronomeSpeed.setMinValue(1);
		metronomeSpeed.setValue(value);
		metronomeSpeed.setWrapSelectorWheel(true);
		metronomeSpeed.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		metronomeSpeed.setOnScrollListener(new OnScrollListener() 
		{
			@Override
			public void onScrollStateChange(final NumberPicker arg0, final int arg1) 
			{
				seekBarMetronome.setProgress(arg0.getValue());
			}
		});
	}
	
	public static void initializeSeekBarMetronome(final SeekBar seekBarMetronome, 
			final NumberPicker metronomeSpeed, final int value)
	{
		seekBarMetronome.setMax(300);
		seekBarMetronome.setProgress(value);
		seekBarMetronome.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
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
	}
}