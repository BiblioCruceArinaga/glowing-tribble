package com.rising.drawing;

import java.util.ArrayList;

import com.rising.drawing.figurasGraficas.Compas;
import com.rising.drawing.figurasGraficas.Nota;
import com.rising.drawing.figurasGraficas.Partitura;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class Metronome {
	private Object mPauseLock;
    private boolean mPaused;
    private Thread th;
    private Vista vista;
    private Partitura partitura;
    private OrdenDibujo bip = null;
    private OrdenDibujo barra = null;
    private Config config;
    private Scroll scroll;
    
    SoundPool bipAgudo = null;
    SoundPool bipGrave = null;
    int bipAgudoInt = 0;
    int bipGraveInt = 0;

    public Metronome(Context context, Vista vista,
    		Partitura partitura, Scroll scroll) 
    {
        mPauseLock = new Object();
        mPaused = false;

        this.vista = vista;
        this.partitura = partitura;
        config = Config.getInstance();
        this.scroll = scroll;
        	        
        bipAgudo = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		bipAgudoInt = bipAgudo.load(context, R.raw.bip, 0);

		bipGrave = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		bipGraveInt = bipGrave.load(context, R.raw.bap, 0);
    }

    public void run(final int bpm) 
    {
    	th = new Thread( new Runnable() {
    		public void run() 
    		{	
    			try {
                	long speed = ( (240000/bpm) / 4 );

                	bipsDePreparacion(speed, partitura.getCompas(0).numPulsos());
                	dibujarPulsosDeMetronomo(speed);
                } 
                catch (InterruptedException e) {
    				e.printStackTrace();
    				Thread.currentThread().interrupt();
	    		} catch (IndexOutOfBoundsException e) {
    				e.printStackTrace();
    			}
    		}
    	});
    	
    	th.start();
    }
    
    //  El metrónomo no puede empezar de sopetón, ya que desconcertaría al músico. 
    //  Esta función emite unos bips iniciales que orientan al músico sobre la 
	//  velocidad a la que deberá empezar a tocar
	private void bipsDePreparacion(long speed, int pulsos) throws InterruptedException 
	{
		for (int j=0; j<pulsos; j++) 
		{
			establecerOrdenDeDibujo(pulsos - j);
			emitirSonido(j);
			
			Thread.sleep(speed);
		}
		
		bip = null;
	}
	
	private void establecerOrdenDeDibujo(int numero)
	{
		if (bip == null) {
			bip = new OrdenDibujo(config.tamanoLetraBipPreparacion, 
					true, numero + "", partitura.getWidth() / 2, partitura.getHeight() / 2);
			bip.setARGBRed();
		}
		else {
			bip.setTexto(numero + "");
		}
	}
	
	private void emitirSonido(int pulso) 
	{
		if (pulso == 0)
			bipAgudo.play(bipAgudoInt, 1, 1, 1, 0, 1);
		else 
			bipGrave.play(bipGraveInt, 1, 1, 1, 0, 1);
	}
	
	private void dibujarPulsosDeMetronomo(long speed) throws InterruptedException
	{
		int currentX = 0;
    	int primerCompas = 0;
    	final int numCompases = partitura.getCompases().size();
    	
    	for (int i=0; i<numCompases; i++) 
    	{
    		Compas compas = partitura.getCompas(i);
    		
    		if (compas.hasBpm())
    			speed = ( (240000/compas.getBpm()) / 4 );

			//  Gestión del scroll
			if (currentX != compas.getXFin()) 
			{
				currentX = compas.getXFin();
				final int currentY = compas.getYFin();
				final int ultimoCompas = i;
				
				if (scroll.outOfBoundaries(currentX, currentY, vista)) {
					final int distanciaDesplazamiento = 
						scroll.distanciaDesplazamiento(partitura, 
							primerCompas, ultimoCompas, vista);
					
					scroll.hacerScroll(vista, distanciaDesplazamiento);
					
					primerCompas = ultimoCompas;
				}
			}
    		
    		dibujarBarras(compas, speed);
    	}
	}
	
	private void dibujarBarras(Compas compas, long speed) throws InterruptedException
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
	
	private void dibujarBarra(Compas compas, Nota nota) 
	{
		int x = nota.haciaArriba() ? nota.getX() + config.anchoCabezaNota : nota.getX();
		
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
    	th.interrupt();
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