package com.rising.drawing;

import java.util.ArrayList;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class Metronome {
	private Object mPauseLock;
    private boolean mPaused;
    private Thread th;
    private int mbpm;
    private Vista vista;
    private Partitura partitura;
    private OrdenDibujo bip = null;
    private OrdenDibujo barra = null;
    private Config config;
    private Scroll scroll;
    
    //  Bips sonoros del metrónomo
    SoundPool bipAgudo = null;
    SoundPool bipGrave = null;
    int bipAgudoInt = 0;
    int bipGraveInt = 0;

    public Metronome(int bpm, Context context, Vista vista,
    		Partitura partitura, Config config, Scroll scroll) {
    	
        mPauseLock = new Object();
        mPaused = false;
        mbpm = bpm;

        this.vista = vista;
        this.partitura = partitura;
        this.config = config;
        this.scroll = scroll;
        	        
        bipAgudo = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		bipAgudoInt = bipAgudo.load(context, R.raw.bip, 0);

		bipGrave = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		bipGraveInt = bipGrave.load(context, R.raw.bap, 0);
    }

    public void run() {
    	th = new Thread(new Runnable(){
    		public void run() {	
    			
                try {
                	long speed = ((240000/mbpm)/4);

                	int currentX = 0;
                	int currentY = 0;
                	int primerCompas = 0;
                	int ultimoCompas = 0;
                	int distanciaDesplazamiento = 0;
                	
                	bipsDePreparacion(speed, partitura.getCompas(0).numPulsos());
                	
                	int numCompases = partitura.getCompases().size();
                	for (int i=0; i<numCompases; i++) {
                		Compas compas = partitura.getCompas(i);
                		
                		//  Si hay un bpm distinto para este compás...
                		if (compas.getBpm() != -1) {
                			mbpm = compas.getBpm();
                			speed = ((240000/mbpm)/4);
                		}

            			//  Gestión del scroll
            			if (currentX != compas.getXFin()) {
            				currentX = compas.getXFin();
            				currentY = compas.getYFin();
            				ultimoCompas = i;
            				
            				if (scroll.outOfBoundaries(currentX, currentY, vista)) {
        						distanciaDesplazamiento = 
        							scroll.distanciaDesplazamiento(partitura, 
        								primerCompas, ultimoCompas, vista);
            					
            					scroll.hacerScroll(vista, distanciaDesplazamiento);
            					
            					primerCompas = ultimoCompas;
            				}
            			}
                		
                		ArrayList<Nota> notasConPulsos = compas.notasConPulsos();
                		int numNotas = notasConPulsos.size();
                		for (int j=0; j<numNotas; j++) {
                			
                			int numPulsos = notasConPulsos.get(j).getPulsos();
                			for (int k=0; k<numPulsos; k++) {
                			
                				dibujarBarra(compas, notasConPulsos.get(j));
	                			emitirSonido(j, k);
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

    /**
     * Call this on pause.
     */
    public void onPause() {
        synchronized (mPauseLock) {
            mPaused = true;
        }
    }

    /**
     * Call this on resume.
     */
    public void onResume() {
        synchronized (mPauseLock) {
            mPaused = false;
            mPauseLock.notifyAll();
        }
    }

    /**
     * Destroy metronome
     */
    public void onDestroy() {
    	mPaused = false;
    	th.interrupt();
    }

    /**
     * Know metronome state
     */
    public boolean paused() {
    	return this.mPaused;
    }
    
    //  El metrónomo no puede empezar de sopetón, ya que
	//  desconcertaría al músico. Esta función emite unos
	//  bips iniciales que orientan al músico sobre la 
	//  velocidad a la que deberá empezar a tocar
	private void bipsDePreparacion(long speed, int pulsos) throws InterruptedException {
		for (int j=0; j<pulsos; j++) {
			if (bip == null) emitirSonido(j, 0);
			else emitirSonido(j, 1);
			
			int numero = pulsos - j;
			
			if (bip == null) {
				bip = new OrdenDibujo(config.getTamanoLetraBipPreparacion(), 
						true, numero + "", partitura.getWidth() / 2, partitura.getHeight() / 2);
				bip.setARGBRed();
			}
			else
				bip.setTexto(numero + "");
			
			Thread.sleep(speed);
		}
		
		bip = null;
	}
	
	private void dibujarBarra(Compas compas, Nota nota) {
		int x = nota.getX();
		if (nota.haciaArriba()) x += config.getAnchoCabezaNota();
		
		barra = new OrdenDibujo(5, x, compas.getYIni(), x, compas.getYFin());
		barra.setARGBRed();
	}
	
	private void emitirSonido(int nota, int pulso) {
		if ( (nota == 0) && (pulso == 0) )
			bipAgudo.play(bipAgudoInt, 1, 1, 1, 0, 1);
		else 
			bipGrave.play(bipGraveInt, 1, 1, 1, 0, 1);
	}
	
	public OrdenDibujo getBarra() {
		return barra;
	}
	
	public OrdenDibujo getBip() {
		return bip;
	}
}