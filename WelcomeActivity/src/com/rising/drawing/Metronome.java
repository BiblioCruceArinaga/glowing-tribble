package com.rising.drawing;

import java.util.ArrayList;

import com.rising.drawing.figurasgraficas.Compas;
import com.rising.drawing.figurasgraficas.Nota;
import com.rising.drawing.figurasgraficas.OrdenDibujo;
import com.rising.drawing.figurasgraficas.Partitura;
import com.rising.drawing.figurasgraficas.Vista;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

public class Metronome {
	private transient final Object mPauseLock;
    private transient boolean mPaused;
    private transient Thread thread;
    private transient final Partitura partitura;
    private transient OrdenDibujo bip;
    private transient OrdenDibujo barra;
    private transient final Config config;
    private transient final AbstractScroll scroll;
    
    private transient final SoundPool bipAgudo;
    private transient final SoundPool bipGrave;
    private transient final int bipAgudoInt;
    private transient final int bipGraveInt;

    public Metronome(final Context context, final Partitura partitura, final AbstractScroll scroll) 
    {
        mPauseLock = new Object();
        mPaused = false;

        this.partitura = partitura;
        config = Config.getInstance();
        this.scroll = scroll;
        	        
        bipAgudo = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		bipAgudoInt = bipAgudo.load(context, R.raw.bip, 0);

		bipGrave = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		bipGraveInt = bipGrave.load(context, R.raw.bap, 0);
    }

    public void run(final int bpm, final Vista vista) 
    {
    	thread = new Thread( new Runnable() {
    		public void run() 
    		{	
    			try {
                	final long speed = (240000 / bpm / 4);

                	bipsDePreparacion(speed, partitura.getCompas(0).numPulsos());
                	dibujarPulsosDeMetronomo(speed, vista);
                } 
                catch (InterruptedException e) {
                	Log.i("InterruptedException", "Interrupted Exception Error");
    				Thread.currentThread().interrupt();
	    		} catch (IndexOutOfBoundsException e) {
    				Log.i("IndexOutOfBoundsException", "IndexOutOfBounds Exception Error");
    			}
    		}
    	});
    	
    	thread.start();
    }
    
    //  El metrónomo no puede empezar de sopetón, ya que desconcertaría al músico. 
    //  Esta función emite unos bips iniciales que orientan al músico sobre la 
	//  velocidad a la que deberá empezar a tocar
	private void bipsDePreparacion(final long speed, final int pulsos) 
			throws InterruptedException 
	{
		for (int j=0; j<pulsos; j++) 
		{
			establecerOrdenDeDibujo(pulsos - j);
			emitirSonido(j);
			
			Thread.sleep(speed);
		}
		
		bip = null;
	}
	
	private void establecerOrdenDeDibujo(final int numero)
	{
		if (bip == null) {
			bip = new OrdenDibujo(config.tamanoLetraBipPreparacion, 
					true, Integer.toString(numero), partitura.getWidth() / 2, partitura.getHeight() / 2);
			bip.setARGBRed();
		}
		else {
			bip.setTexto(Integer.toString(numero));
		}
	}
	
	private void emitirSonido(final int pulso) 
	{
		if (pulso == 0) {
			bipAgudo.play(bipAgudoInt, 1, 1, 1, 0, 1);
		} else { 
			bipGrave.play(bipGraveInt, 1, 1, 1, 0, 1);
		}
	}
	
	private void dibujarPulsosDeMetronomo(long speed, final Vista vista) throws InterruptedException
	{
		int currentX = 0;
    	int primerCompas = 0;
    	final int numCompases = partitura.getCompases().size();
    	
    	for (int i=0; i<numCompases; i++) 
    	{
    		final Compas compas = partitura.getCompas(i);
    		
    		if (compas.hasBpm()) {
    			speed = (240000 / compas.getBpm() / 4);
    		}

			//  Gestión del scroll
			if (currentX != compas.getXFin()) 
			{
				currentX = compas.getXFin();
				
				primerCompas = StaticMethods.manageScroll(compas.getYFin(), 
						vista, currentX, scroll, partitura, primerCompas, i);
			}
    		
    		dibujarBarras(compas, speed);
    	}
	}
	
	private void dibujarBarras(final Compas compas, final long speed) 
			throws InterruptedException
	{
		final ArrayList<Nota> notasConPulsos = compas.notasConPulsos();
		final int numNotas = notasConPulsos.size();
		
		for (int j=0; j<numNotas; j++) 
		{
			final Nota nota = notasConPulsos.get(j);
			final int numPulsos = nota.getPulsos();
			
			for (int k=0; k<numPulsos; k++) 
			{
				dibujarBarra(compas, nota);
    			emitirSonido(j + k);
    			
    			Thread.sleep(speed);
    			
    			synchronized (mPauseLock) {
	                while (mPaused) {
	                    try {
	                        mPauseLock.wait();
	                    } catch (InterruptedException e) {
	                    	Thread.currentThread().interrupt();
	                    	mPauseLock.notifyAll();
	                    	return;
	                    }
	                }
	            }
			}
		}
	}
	
	private void dibujarBarra(final Compas compas, final Nota nota) 
	{
		final int x = nota.haciaArriba() ? nota.getX() + config.anchoCabezaNota : nota.getX();
		
		barra = new OrdenDibujo(5, x, compas.getYIni(), x, compas.getYFin());
		barra.setARGBRed();
	}

    public void onPause() 
    {
        synchronized (mPauseLock) {
            mPaused = true;
        }
    }

    public void onResume() 
    {
        synchronized (mPauseLock) {
            mPaused = false;
            mPauseLock.notifyAll();
        }
    }

    public void onDestroy() 
    {
    	mPaused = false;
    	thread.interrupt();
    }

    public boolean paused() 
    {
    	return this.mPaused;
    }

	public OrdenDibujo getBarra() 
	{
		return barra;
	}
	
	public OrdenDibujo getBip() 
	{
		return bip;
	}
}