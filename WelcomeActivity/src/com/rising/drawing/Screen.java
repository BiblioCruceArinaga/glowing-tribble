package com.rising.drawing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class Screen extends SurfaceView implements SurfaceHolder.Callback, Observer {

	private boolean isValidScreen = false;
	private ScreenThread thread;
	private Context context = null;
	private Config config = null;
	private String path_folder = "/RisingScores/scores/";

	private Partitura partituraHorizontal = new Partitura();
	private Partitura partituraVertical = new Partitura();
	
	//  Gestión de las posibles vistas y su scroll
	private ArrayList<OrdenDibujo> verticalDrawing = new ArrayList<OrdenDibujo>();
	private ArrayList<OrdenDibujo> horizontalDrawing = new ArrayList<OrdenDibujo>();
	private Vista vista;
	private Scroll scroll;
	private Thread verticalThread;
	private Thread horizontalThread;
	
	//  Gestión de la lectura de micrófono
	private SoundReader soundReader = null;
	private int compasActual = 0;
	private int golpeSonidoActual = 0;
	private int yActual = 0;
	private int desplazamiento = 0;
	private int changeAccount = 0;
	private boolean primerDesplazamientoHecho = false;
	
	//  Metrónomo y su gestión
	private Metronomo metronomo = null;
	private OrdenDibujo bip = null;
	private Dialog MDialog = null;
	
	private int width = 0;
	private int height = 0;
    
	//  ========================================
	//  Constructor y métodos heredados
	//  ========================================
	public Screen(Context context, String path, int width, int densityDPI){
		super(context);
		getHolder().addCallback(this);
		
		try {
			this.context = context;
			
			FileMethods fileMethods = new FileMethods(path_folder, path);
			fileMethods.cargarDatosDeFichero(partituraHorizontal, partituraVertical);

			config = new Config(densityDPI, width);
			scroll = new Scroll(config);

			crearVistasDePartitura();

			horizontalThread.join();
			verticalThread.join();

			cambiarVista(Vista.VERTICAL);
			isValidScreen = true;
			
		} catch (FileNotFoundException e) {
			Log.i("FileNotFoundException: ", e.getMessage() + "\n");
		} catch (StreamCorruptedException e) {
			Log.i("StreamCorruptedException: ", e.getMessage() + "\n");
		} catch (IOException e) {
			Log.i("IOException: ", e.getMessage() + "\n");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
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
		
		partituraHorizontal.destruir();
		partituraHorizontal = null;
		partituraVertical.destruir();
		partituraVertical = null;
		
		verticalDrawing.clear();
		verticalDrawing = null;
		horizontalDrawing.clear();
		horizontalDrawing = null;
		
		stopMicrophone();
	}

	@Override
	public boolean onTouchEvent(MotionEvent e){		
		switch (e.getAction()){
			case MotionEvent.ACTION_DOWN:
				scroll.down(e);
	            break;
	            
	        case MotionEvent.ACTION_MOVE:
	        	scroll.move(e, vista);
	            break;
	            
	        case MotionEvent.ACTION_UP:
	        	if (scroll.up(e)) {
		        	if (MDialog == null) {
		        		BpmManagement bpmManagement = new BpmManagement(vista,
		        				partituraHorizontal, partituraVertical, 
		        				horizontalDrawing, verticalDrawing, config, context); 
		    			bpmManagement.tapManagement(e, scroll);
		    		}
	        	}
	        	break;
	        	
	        default:
	        	break;
	    }

	    return true;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (MDialog != null) {
		    Rect dialogBounds = new Rect();
		    MDialog.getWindow().getDecorView().getHitRect(dialogBounds);
	
		    if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
		        MDialog.dismiss();
		        MDialog = null;
		    }
		}
		
	    return super.dispatchTouchEvent(ev);
	}
	
	public Config getConfig() {
		return config;
	}
	
	public boolean isValidScreen() {
		return isValidScreen;
	}
	
	//  ========================================
	//  Métodos de dibujo
	//  ========================================
	public void draw(Canvas canvas) {
		if (canvas != null) {
		
			inicializarParametrosScroll(canvas);
		
			canvas.drawARGB(255, 255, 255, 255);
			canvas.save();
            
			if (vista == Vista.VERTICAL) canvas.translate(0, scroll.getYOffset());
			else canvas.translate(scroll.getXOffset(), 0);
            
			drawToCanvas(canvas);
            scroll.dibujarBarra(canvas, vista);
            
            canvas.restore();
		}
    }
	
	private void drawToCanvas(Canvas canvas) {
		ArrayList<OrdenDibujo> ordenes;
		if (vista == Vista.VERTICAL)
			ordenes = verticalDrawing;
		else
			ordenes = horizontalDrawing;
		
		int numOrdenes = ordenes.size();
		for (int i=0; i<numOrdenes; i++) {
			OrdenDibujo ordenDibujo = ordenes.get(i);
			if (ordenDibujo == null) continue;
			
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
					
					RectF rectf = ordenDibujo.getRectF();
					Matrix matrix = getMatrix(rectf, ordenDibujo.getAngulo());
					
					Path path = new Path();
					path.addArc(rectf, 0, -180);
					path.transform(matrix, path);
					
					canvas.drawPath(path, ordenDibujo.getPaint());
					break;
				default:
					break;
			}
		}
		
		//  Dibuja el número rojo que marca los pulsos encima del compás
		if (bip != null)
			canvas.drawText(bip.getTexto(), bip.getX1(), bip.getY1(), bip.getPaint());
	}
	
	private void crearVistasDePartitura() {
		verticalThread = new Thread(new Runnable(){
    		public void run() {
    			DrawingMethods metodosDibujo = 
    					new DrawingMethods(partituraVertical, config, getResources(), Vista.VERTICAL);
				if (metodosDibujo.isValid()) {
					verticalDrawing = metodosDibujo.crearOrdenesDeDibujo();
				}
    		}
		});
		
		horizontalThread = new Thread(new Runnable(){
    		public void run() {
    			DrawingMethods metodosDibujo = 
    					new DrawingMethods(partituraHorizontal, config, getResources(), Vista.HORIZONTAL);
				if (metodosDibujo.isValid()) {
					horizontalDrawing = metodosDibujo.crearOrdenesDeDibujo();
				}
    		}
		});
		
		horizontalThread.start();
		verticalThread.start();
	}

	public void cambiarVista(Vista vista) {
		this.vista = vista;
	}
	
	private void inicializarParametrosScroll(Canvas canvas) {
		if (vista == Vista.VERTICAL) {
			scroll.inicializarVertical(canvas.getHeight(), partituraVertical.getLastMarginY());
		}
		else {
			int xFin = partituraHorizontal.getCompas(
					partituraHorizontal.getNumeroDeCompases() - 1).getXFin();
			scroll.inicializarHorizontal(canvas.getWidth(), xFin);
		}
	}
	
	//  La rotación de una matriz produce una traslación
	//  involuntaria e indeseada que debemos contrarrestar
	//  manualmente para que el resultado quede bien.
	//  Además, aquí controlamos que la rotación
	//  se haga en el sentido adecuado
	public Matrix getMatrix(RectF rectf, float angulo) {
		Matrix matrix = new Matrix();
		
		//  Rotación
		if (angulo > 0) {
			matrix.postRotate(angulo, rectf.left, rectf.bottom);
		}
		else {
			matrix.postRotate(angulo, rectf.right, rectf.top);
		}

		//  Contrarrestar traslación accidental. En el futuro
		//  considerar que girar a la izquierda y a la derecha
		//  requieren valores diferentes para obtener el mismo resultado
		if (angulo < 0)
			matrix.postTranslate(angulo, 0);
		
		return matrix;
	}
	
	/*
	 * 
	 * Gestión del metrónomo
	 * 
	 */
	public void Back(){
		scroll.back(vista);
	}

	public void Forward() {
		scroll.forward(vista);
	}
	
	public void Metronome_Pause(){
		if (metronomo != null) {
			if (metronomo.paused()) 
				metronomo.onResume();
			else 
				metronomo.onPause();
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
			bip = null;
		}
	}
	
	public class Metronomo {
		private Object mPauseLock;
	    private boolean mPaused;
	    private Thread th;
	    private int mbpm;
	    private boolean numeros_bip;
	    
	    //  Bips sonoros del metrónomo
	    SoundPool bipAgudo = null;
	    SoundPool bipGrave = null;
	    int bipAgudoInt = 0;
	    int bipGraveInt = 0;

	    public Metronomo(int bpm) {
	        mPauseLock = new Object();
	        mPaused = false;
	        mbpm = bpm;
	        	        
	        bipAgudo = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
			bipAgudoInt = bipAgudo.load(context, R.raw.bip, 0);

			bipGrave = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
			bipGraveInt = bipGrave.load(context, R.raw.bap, 0);
	    }

	    public void run() {
	    	th = new Thread(new Runnable(){
	    		public void run() {	
	    			/*
	                try {
	                	long speed = ((240000/mbpm)/4);
	                	int currentY = partitura.getCompas(0).getYIni();
	                	int changeAccount = 0;
	                	int staves = partitura.getStaves();
	                	
	                	boolean primerScrollHecho = false;
	                	int distanciaDesplazamiento = 
	                			obtenerDistanciaDesplazamiento(currentY, primerScrollHecho);
	                	
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
	                			
	                			if (changeAccount == config.getChangeAccount()) {
	                				//hacerScroll(distanciaDesplazamiento);
		                			
	                				if (!primerScrollHecho) {
			                			primerScrollHecho = true;
			                			distanciaDesplazamiento = 
			                				obtenerDistanciaDesplazamiento(currentY, primerScrollHecho);
	                				}
		                			
		                			changeAccount = 0;
	                			}
	                		}

	                		int xPos = (compas.getXFin() - compas.getXIni()) / 2;
	                		xPos += compas.getXIni();
	                				
	                		int pulsos = compas.numeroDePulsos();
	                		for (int j=0; j<pulsos; j++) {
	                			
	                			emitirSonido(j);
	                			if(numeros_bip){
	                				dibujarBip(j, xPos, compas.getYIni());
	                			}
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
	    			*/
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
					bip.setX1(width / 2);
					bip.setY1(height / 2);
				}
				else
					bip.setTexto(numero + "");
				
				Thread.sleep(speed);
				
				if (numero == 1) 
					bip = null;
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
	}
	
	
	
	
	/*
	 * 
	 * GESTIÓN DE LA LECTURA DEL MICRÓFONO
	 * 
	 */
	public void readMicrophone(int sensibilidad, int velocidad) throws Exception {
		soundReader = new SoundReader(velocidad);
		soundReader.addObserver(this);
		soundReader.setSensitivity(sensibilidad);
		/*
		yActual = partitura.getCompas(0).getYIni();
		desplazamiento = obtenerDistanciaDesplazamiento(yActual, false);
		
		ArrayList<Integer> golpesSonido = new ArrayList<Integer>();
		int numCompases = partitura.getCompases().size();
		for (int i=0; i<numCompases; i++) 
			golpesSonido.add(partitura.getCompas(i).golpesDeSonido());
		*/
		Toast.makeText(context, R.string.startPlaying, Toast.LENGTH_SHORT).show();
	}
	
	public void stopMicrophone() {
		if (soundReader != null) {
			soundReader.deleteObservers();
			soundReader.onDestroy();
			soundReader = null;
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		int sound = (Integer) data;
		/*
		if (sound > 0) {
			Compas compas = partitura.getCompas(compasActual);
			int golpesSonido = compas.golpesDeSonido();
			
			if (golpeSonidoActual >= golpesSonido) {
				Log.i("Check", "Compás nº " + compasActual + ": " + golpesSonido);
				
				compasActual++;
				golpeSonidoActual = 0;
				
				if (partitura.getCompas(compasActual).getYIni() != yActual) {
					
					changeAccount += partitura.getStaves();
					
					if (changeAccount == config.getChangeAccount()) {
						//hacerScroll(desplazamiento);
						
						if (!primerDesplazamientoHecho) {
							primerDesplazamientoHecho = true;
							desplazamiento = 
	                				obtenerDistanciaDesplazamiento(yActual, primerDesplazamientoHecho);
						}
						
						changeAccount = 0;
					}
				}
			}
			else
				golpeSonidoActual++;
		}
		*/
	}
	
	/*
	 * 
	 *  Métodos de gestión del scroll
	 *
	 */
	
	
	/*
	private void hacerScroll(int distanciaDesplazamiento) {
		if (vista == Vista.VERTICAL) {
			limiteVisibleArriba -= distanciaDesplazamiento;
			limiteVisibleAbajo -= distanciaDesplazamiento;
			yOffset -= distanciaDesplazamiento;
		}
		else {
			limiteVisibleIzquierda -= distanciaDesplazamiento;
			limiteVisibleDerecha -= distanciaDesplazamiento;
			xOffset -= distanciaDesplazamiento;
		}
	}
	*/
	private int obtenerDistanciaDesplazamiento(int currentY, boolean primerScrollHecho) {
		
		//  La distancia de desplazamiento en la primera iteración
		//  es diferente al resto porque hay que contar con la
		//  distancia extra del título de la obra y el nombre del autor
		if (!primerScrollHecho)
			return currentY + (config.getDistanciaPentagramas() + 
							   config.getDistanciaLineasPentagrama()) * 4;
		else
			return (config.getDistanciaPentagramas() + 
			        config.getDistanciaLineasPentagrama() * 4) * 4;
	}
}
