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
	private Metronome metronomo = null;
	private OrdenDibujo bip = null;
	private Dialog MDialog = null;
    
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
		if (metronomo != null) {
			bip = metronomo.getBip();
			
			if (bip != null)
				canvas.drawText(metronomo.getBip().getTexto(), metronomo.getBip().getX1(), 
					metronomo.getBip().getY1(), metronomo.getBip().getPaint());
		}
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
			partituraVertical.setWidth(canvas.getWidth());
			partituraVertical.setHeight(canvas.getHeight());
			
			scroll.inicializarVertical(canvas.getHeight(), partituraVertical.getLastMarginY());
		}
		else {
			partituraHorizontal.setWidth(canvas.getWidth());
			partituraHorizontal.setHeight(canvas.getHeight());
			
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
	 * Gestión del metrónomo desde Main Activity
	 * 
	 */
	public void Metronome_Pause(){
		if (metronomo != null) {
			if (metronomo.paused()) 
				metronomo.onResume();
			else 
				metronomo.onPause();
		}
	}
	
	public void Metronome_Play(int bpm, boolean ayudaVisual){
		if (metronomo == null) {
			Partitura partitura = 
        			vista == Vista.VERTICAL ? partituraVertical : partituraHorizontal;
			
			scroll.setOrientation(getResources().getConfiguration().orientation);
			
    		metronomo = new Metronome(bpm, ayudaVisual, context, 
    				vista, partitura, config, scroll);
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
		//int sound = (Integer) data;
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
	
	public void Back(){
		scroll.back(vista);
	}

	public void Forward() {
		scroll.forward(vista);
	}
}
