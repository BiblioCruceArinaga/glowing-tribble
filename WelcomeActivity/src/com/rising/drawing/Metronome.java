package com.rising.drawing;

import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.SoundPool;

public class Metronome {
	private Object mPauseLock;
    private boolean mPaused;
    private Thread th;
    private int mbpm;
    private boolean ayudaVisual;
    private Vista vista;
    private Partitura partitura;
    private OrdenDibujo bip = null;
    private Config config;
    private Scroll scroll;
    
    //  Bips sonoros del metrónomo
    SoundPool bipAgudo = null;
    SoundPool bipGrave = null;
    int bipAgudoInt = 0;
    int bipGraveInt = 0;

    public Metronome(int bpm, boolean ayudaVisual, Context context, Vista vista,
    		Partitura partitura, Config config, Scroll scroll) {
    	
        mPauseLock = new Object();
        mPaused = false;
        mbpm = bpm;
        
        this.ayudaVisual = ayudaVisual;
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

                	int currentY = partitura.getCompas(0).getYIni();
                	int changeAccount = 0;
                	int finalChangeAccount = 
                			scroll.getOrientation() == Configuration.ORIENTATION_PORTRAIT ? 
                					config.getChangeAccountVertical() : config.getChangeAccountHorizontal();
                	int staves = partitura.getStaves();
                	
                	boolean primerScrollHecho = false;
                	int distanciaDesplazamiento = 
                			scroll.distanciaDesplazamiento(currentY, 
                					primerScrollHecho, config, staves);
                	
                	bipsDePreparacion(speed, partitura.getCompas(0).numeroDePulsos());
                	
                	int numCompases = partitura.getCompases().size();
                	for (int i=0; i<numCompases; i++) {
                		Compas compas = partitura.getCompas(i);
                		
                		//  Si hay un bpm distinto para este compás...
                		if (compas.getBpm() != -1) {
                			mbpm = compas.getBpm();
                			speed = ((240000/mbpm)/4);
                		}
                			
                		//  Si ha cambiado la Y, hacemos scroll
                		if (currentY != compas.getYIni()) {
                			currentY = compas.getYIni();
                			changeAccount += staves;
                			
                			if (changeAccount == finalChangeAccount) {
                				scroll.hacerScroll(vista, distanciaDesplazamiento);
	                			
                				if (!primerScrollHecho) {
		                			primerScrollHecho = true;
		                			
		                			distanciaDesplazamiento = 
		                				scroll.distanciaDesplazamiento(
		                						currentY, primerScrollHecho, config, staves);
                				}
	                			
	                			changeAccount = 0;
                			}
                		}

                		int xPos = (compas.getXFin() - compas.getXIni()) / 2;
                		xPos += compas.getXIni();
                				
                		int pulsos = compas.numeroDePulsos();
                		for (int j=0; j<pulsos; j++) {
                			
                			emitirSonido(j);
                			if (ayudaVisual)
                				dibujarBip(j, xPos, compas.getYIni());
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
			emitirSonido(j);
			int numero = pulsos - j;
			
			if (bip == null) {
				bip = new OrdenDibujo();
				bip.setOrden(DrawOrder.DRAW_TEXT);
				bip.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraBipPreparacion());
				bip.setPaint(PaintOptions.SET_ARGB_RED, -1);
				bip.setPaint(PaintOptions.SET_TEXT_ALIGN, -1);
				bip.setTexto(numero + "");

				bip.setX1(partitura.getWidth() / 2);
				bip.setY1(partitura.getHeight() / 2);
			}
			else
				bip.setTexto(numero + "");
			
			Thread.sleep(speed);
		}
	}

	private void dibujarBip(int pulso, int x, int y) {
		bip = new OrdenDibujo();
		bip.setOrden(DrawOrder.DRAW_TEXT);
		bip.setPaint(PaintOptions.SET_TEXT_SIZE, config.getTamanoLetraPulso());
		bip.setPaint(PaintOptions.SET_ARGB_RED, -1);
		bip.setTexto((pulso + 1) + "");
		bip.setX1(x);
		bip.setY1(y);
	}
	
	private void emitirSonido(int pulso) {
		if (pulso == 0)
			bipAgudo.play(bipAgudoInt, 1, 1, 1, 0, 1);
		else 
			bipGrave.play(bipGraveInt, 1, 1, 1, 0, 1);
	}
	
	public OrdenDibujo getBip() {
		return bip;
	}
}
