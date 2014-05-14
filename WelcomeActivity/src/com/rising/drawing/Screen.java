package com.rising.drawing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class Screen extends SurfaceView implements SurfaceHolder.Callback {

	private boolean isValidScreen = false;
	private ObjectInputStream fichero = null;
	private ScreenThread thread;
	
	private Partitura partitura = new Partitura();
	private Compas compas = new Compas();
	private ArrayList<OrdenDibujo> ordenesDibujo = new ArrayList<OrdenDibujo>();
	private Metronomo metronomo = null;
	
	//  Gestión del scroll
	private float altoPantalla = 0;
	private boolean canvasDependentDataRecovered = false;
	private float div = 0;
	private float finalScroll = 0;
	private static float limiteVisibleArriba = 0;
    private static float limiteVisibleAbajo = 0;
    private int margenFinalScroll = 0;
    private boolean mostrarBarraLateral = false;
    private float offsetBarraLateral = 0;
    private float porcentajeAltura = 0;
    private int tamanoBarraLateral = 0;
    private static float yOffset = 0;
    private float yPrevious = 0;
	
	//  ========================================
	//  Constructor y métodos heredados
	//  ========================================
	public Screen(Context context, String path, int width, int densityDPI){
		super(context);
		getHolder().addCallback(this);
		
		try {
			File f = new File(Environment.getExternalStorageDirectory() + 
					"/RisingScores/scores/" + path);
	        FileInputStream is = new FileInputStream(f);
			fichero = new ObjectInputStream(is);
			
			cargarDatosDeFichero();
			
			Config config = new Config(densityDPI, width);
			margenFinalScroll = config.getDistanciaPentagramas();
			
			DrawingMethods metodosDibujo = new DrawingMethods(partitura, config, getResources());
			if (metodosDibujo.isValid()) {
				ordenesDibujo = metodosDibujo.crearOrdenesDeDibujo();
				isValidScreen = true;
			}
			
		} catch (FileNotFoundException e) {
			Log.i("FileNotFoundException: ", e.getMessage() + "\n");
		} catch (StreamCorruptedException e) {
			Log.i("StreamCorruptedException: ", e.getMessage() + "\n");
		} catch (IOException e) {
			Log.i("IOException: ", e.getMessage() + "\n");
		}
    }
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread = new ScreenThread(getHolder(), this);
		thread.setRunning(true);
		thread.start();	
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		thread.setRunning(false);
		thread = null;
		
		if (metronomo != null) {
			metronomo.onDestroy();
			metronomo = null;
		}
		
		isValidScreen = false;
		fichero = null;
		
		partitura.destruir();
		partitura = null;
		compas = null;
		
		ordenesDibujo.clear();
		ordenesDibujo = null;
		
		altoPantalla = 0;
		canvasDependentDataRecovered = false;
		div = 0;
		finalScroll = 0;
		limiteVisibleArriba = 0;
	    limiteVisibleAbajo = 0;
	    margenFinalScroll = 0;
	    mostrarBarraLateral = false;
	    offsetBarraLateral = 0;
	    porcentajeAltura = 0;
	    tamanoBarraLateral = 0;
	    yOffset = 0;
	    yPrevious = 0;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e){		
		switch (e.getAction()){
			case MotionEvent.ACTION_DOWN:
				yPrevious = e.getY();
	            mostrarBarraLateral = true;
	            break;
	            
	        case MotionEvent.ACTION_MOVE:
	        	yOffset += e.getY() - yPrevious;
	            porcentajeAltura = - yOffset / finalScroll;
	            offsetBarraLateral = -altoPantalla * porcentajeAltura;
	            div = e.getY() - yPrevious;
	            yPrevious = e.getY();
	            limiteVisibleArriba += div;
	            limiteVisibleAbajo += div;

	            if (limiteVisibleArriba > 0) {
	            	yOffset = 0;
	            	div = 0;
	            	limiteVisibleArriba = 0;
	            	limiteVisibleAbajo = altoPantalla;
	            }

	            if (limiteVisibleAbajo < -finalScroll) {
	            	yOffset = -finalScroll - altoPantalla;
	            	div = 0;
	            	limiteVisibleArriba = -finalScroll - altoPantalla;
	            	limiteVisibleAbajo = -finalScroll;
	            }

	            mostrarBarraLateral = true;
	            break;
	            
	        case MotionEvent.ACTION_UP:
	        	mostrarBarraLateral = false;
	        	break;
	        	
	        default:
	        	break;
	    }

	    return true;
	}
	//  ========================================
	
	
	//  ========================================
	//  Métodos de gestión del fichero
	//  ========================================
	private void cargarDatosDeFichero() throws IOException {
		leerDatosBasicosDePartitura();

		byte byteLeido = fichero.readByte();
		while (byteLeido != -128) {
			
			switch (byteLeido) {			
				case 126:
					leerFiguraGraficaCompas();
					break;
					
				case 127:
					partitura.addCompas(compas);
					compas = new Compas();
					break;
				
				default:
					leerInfoNota(byteLeido);
					break;
			}
			
			byteLeido = fichero.readByte();
		}	
	}

	public boolean isValidScreen() {
		return isValidScreen;
	}
	
	private ArrayList<Byte> leerClaves() throws IOException {
		ArrayList<Byte> arrayBytes = new ArrayList<Byte>();
		
		byte pentagrama = 0;
		byte clave = 0;
		byte alteracion = 0;
		
		byte numClefs = fichero.readByte();
		arrayBytes.add(numClefs);
		
		for (int i=0; i<numClefs; i++) {
			pentagrama = fichero.readByte();
			clave = fichero.readByte();
			alteracion = fichero.readByte();
			
			arrayBytes.add(pentagrama);
			arrayBytes.add(clave);
			arrayBytes.add(alteracion);
		}

		return arrayBytes;
	}
	
	private void leerDatosBasicosDePartitura() throws IOException {
		ArrayList<Byte> work = leerHastaAlmohadilla();
		ArrayList<Byte> creator = leerHastaAlmohadilla();
		byte staves = fichero.readByte();
		byte instrument = fichero.readByte();
		ArrayList<Byte> divisions = leerHastaAlmohadilla();
		
		partitura.setWork(work);
		partitura.setCreator(creator);
		partitura.setStaves(staves);
		partitura.setInstrument(instrument);
		partitura.setDivisions(divisions);
	}
	
	private void leerFiguraGraficaCompas() throws IOException {
		ElementoGrafico elemento = new ElementoGrafico();

		byte posicionFiguraGrafica = fichero.readByte();
		elemento.addValue(posicionFiguraGrafica);
		
		byte figuraGrafica = fichero.readByte();
		switch (figuraGrafica) {
			case 1:
			case 2:
			case 3:
			case 4:
				elemento.addValue(figuraGrafica);
				elemento.setPosition(leerHastaAlmohadilla());
				compas.setDynamics(elemento);
				break;
				
			case 24:
				elemento.addValue(figuraGrafica);
				elemento.addValue(fichero.readByte());
				elemento.addAllValues(leerHastaAlmohadilla());
				elemento.setPosition(leerHastaAlmohadilla());
				compas.setMetronome(elemento);
				break;
				
			case 25:
				elemento.addValue(figuraGrafica);
				elemento.setPosition(leerHastaAlmohadilla());
				compas.setPedalStart(elemento);
				break;
				
			case 26:
				elemento.addValue(figuraGrafica);
				elemento.setPosition(leerHastaAlmohadilla());
				compas.setPedalStop(elemento);
				break;
				
			case 27:
				elemento.addAllValues(leerHastaAlmohadilla());
				elemento.setPosition(leerHastaAlmohadilla());
				compas.setWords(elemento);
				break;
				
			case 28:
				elemento.addValue(fichero.readByte());
				elemento.setPosition(leerHastaAlmohadilla());
				compas.addBarline(elemento);
				break;
				
			case 29:
				compas.setRepeatOrEnding(fichero.readByte());
				leerHastaAlmohadilla();
				break;
				
			case 30:
				elemento.addAllValues(leerClaves());
				elemento.setPosition(leerHastaAlmohadilla());
				compas.addClef(elemento);
				break;
				
			case 31:
				elemento.addValue(fichero.readByte());
				elemento.setPosition(leerHastaAlmohadilla());
				compas.setTime(elemento);
				break;
				
			default: 
				break;
		}
	}
	
	private ArrayList<Byte> leerHastaAlmohadilla() throws IOException {
		ArrayList<Byte> bytesArray = new ArrayList<Byte>();
		byte byteLeido = 0;
		
		do {
			byteLeido = fichero.readByte();
			bytesArray.add(byteLeido);
		} while (byteLeido != 35);
		
		bytesArray.remove(bytesArray.size() - 1);
		return bytesArray;
	}
	
	private void leerInfoNota(byte nota) throws IOException {
		byte octava = fichero.readByte();
		byte figuracion = fichero.readByte();
		byte union = fichero.readByte();
		byte plica = fichero.readByte();
		byte voz = fichero.readByte();
		byte pentagrama = fichero.readByte();
		
		ArrayList<Byte> figurasGraficas = leerHastaAlmohadilla();
		ArrayList<Byte> posicionEjeX = leerHastaAlmohadilla();

		compas.addNote(new Nota(nota, octava, figuracion, union, plica,
				voz, pentagrama, figurasGraficas, posicionEjeX));
	}
	//  ================================
	
	//  ========================================
	//  Métodos de dibujo
	//  ========================================
	public void draw(Canvas canvas) {
		
		if (canvas != null) {
		
			//  Estos valores dependen del canvas y sólo deben recogerse una vez
			if (!canvasDependentDataRecovered) {
	    		limiteVisibleAbajo = - canvas.getHeight();
	    		altoPantalla = limiteVisibleAbajo;
	    		
	    		finalScroll = partitura.getLastMarginY() + margenFinalScroll;
	    		tamanoBarraLateral = (int) ( (altoPantalla / finalScroll) * altoPantalla);
	    		
	    		canvasDependentDataRecovered = true;
			}
		
			canvas.drawARGB(255, 255, 255, 255);
			canvas.save();
            canvas.translate(0, yOffset);
            drawToCanvas(canvas);
            if (mostrarBarraLateral) dibujarBarraLateral(canvas);
            canvas.restore();
		}
    }
	
	private void drawToCanvas(Canvas canvas) {
		int numOrdenes = ordenesDibujo.size();
		for (int i=0; i<numOrdenes; i++) {
			OrdenDibujo ordenDibujo = ordenesDibujo.get(i);
			
			switch (ordenDibujo.getOrden()) {
				case DRAW_BITMAP:
					canvas.drawBitmap(ordenDibujo.getImagen(), ordenDibujo.getX1(), 
							ordenDibujo.getY1(), ordenDibujo.getPaint());
					break;
				case DRAW_CIRCLE:
					canvas.drawCircle(ordenDibujo.getX1(), ordenDibujo.getY1(), 
							ordenDibujo.getRadius(), ordenDibujo.getPaint());
					break;
				case DRAW_LINE:
					canvas.drawLine(ordenDibujo.getX1(), ordenDibujo.getY1(), 
							ordenDibujo.getX2(), ordenDibujo.getY2(), ordenDibujo.getPaint());
					break;
				case DRAW_TEXT:
					canvas.drawText(ordenDibujo.getTexto(), ordenDibujo.getX1(), 
							ordenDibujo.getY1(), ordenDibujo.getPaint());
					break;
				case DRAW_ARC:
					canvas.drawArc(ordenDibujo.getRectF(), 0, -180, false, ordenDibujo.getPaint());
					break;
				default:
					break;
			}
		}
	}
	
	public void dibujarBarraLateral(Canvas canvas) {
		int x_end = canvas.getWidth() - 30;
		
		Paint paint = new Paint();
    	paint.setStrokeWidth(5);
    	paint.setARGB(255, 0, 0, 0);
    	canvas.drawLine(x_end, offsetBarraLateral - yOffset, 
    			x_end, offsetBarraLateral + tamanoBarraLateral - yOffset, paint);
    }
	
	/*
	 * 
	 * Gestión del metrónomo
	 * 
	 */
	public void Metronome_Back(){
		yOffset = 0;
		limiteVisibleArriba = 0;
		limiteVisibleAbajo = altoPantalla;
	}
	
	public void Metronome_Pause(){
		if (metronomo != null) {
			if (metronomo.paused()) metronomo.onResume();
			else metronomo.onPause();
		}
	}
	
	public void Metronome_Play(int bpm){
		if (metronomo == null) {
    		metronomo = new Metronomo(bpm);
    		metronomo.run();
		}
	}

	public void Metronome_Stop(){
		if (metronomo != null) {
			metronomo.onDestroy();
			metronomo = null;
		}
	}
	
	public class Metronomo {
		private Object mPauseLock;
	    private boolean mPaused;
	    private Thread th;
	    private int mbpm;

	    public Metronomo(int bpm) {
	        mPauseLock = new Object();
	        mPaused = false;
	        mbpm = bpm;
	    }

	    public void run() {
	    	th = new Thread(new Runnable(){
	    		public void run() {	
	                try {
	                	final long speed = ((240000/mbpm)/4);
	    				
	                	int numCompases = partitura.getCompases().size();
	                	for (int i=0; i<numCompases; i++) {
	                		
	                		int pulsos = partitura.getCompas(i).numeroDePulsos();
	                		for (int j=0; j<pulsos; j++) {
	                			colocarNuevoPulso(j + 1);
	                			
	                			Thread.sleep(speed);
	                			
	                			quitarPulsoAnterior();
	                		}
	                	}
	                	
	    				synchronized (mPauseLock) {
	    	                while (mPaused) {
	    	                    try {
	    	                        mPauseLock.wait();
	    	                    } catch (InterruptedException e) {
	    	                    	quitarPulsoAnterior();
	    	                    	Thread.currentThread().interrupt();
	    	                    	mPauseLock.notifyAll();
	    	                    	return;
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
	}
	
	//  Quita la orden de dibujo del pulso anterior
	//  del array de órdenes de dibujo
	private void quitarPulsoAnterior() {
		ordenesDibujo.remove(ordenesDibujo.size() - 1);
	}
	
	private void colocarNuevoPulso(int pulso) {
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, 50);
		ordenDibujo.setTexto(pulso + "");
		ordenDibujo.setX1(500);
		ordenDibujo.setY1(500);
		ordenesDibujo.add(ordenDibujo);
	}
}