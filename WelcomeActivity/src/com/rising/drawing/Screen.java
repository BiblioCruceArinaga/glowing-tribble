package com.rising.drawing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.rising.drawing.figurasgraficas.Compas;
import com.rising.drawing.figurasgraficas.OrdenDibujo;
import com.rising.drawing.figurasgraficas.Partitura;
import com.rising.drawing.figurasgraficas.Vista;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class Screen extends SurfaceView implements SurfaceHolder.Callback, Observer 
{
	private transient boolean isValidScreen;
	private transient ScreenThread thread;
	private transient Context context;
	private transient Config config;
	private transient final String pathFolder = "/.RisingScores/scores/";
	
	//  Gestión de las posibles vistas y su scroll
	private transient Partitura partitura = new Partitura();
	private transient ArrayList<OrdenDibujo> ordenesDibujo = new ArrayList<OrdenDibujo>();
	private transient Vista vista = Vista.VERTICAL;
	private transient AbstractScroll scroll;
	private transient Metronome metronomo;
	private transient Dialog mDialog;
	
	//  Gestión de la lectura de micrófono
	private transient SoundReader soundReader;
	private transient int compasActual;
	private transient int golpeSonidoActual;
	private transient int xActual;
	private transient int primerCompas;

	public Screen(final Context context, final String path, 
			final int width, final int height, final int densityDPI)
	{
		super(context);
		getHolder().addCallback(this);
		
		try {
			prepareScreenInstance(context, path, width, height, densityDPI);
						
		} catch (FileNotFoundException e) {
			Log.e("FileNotFoundException: ", e.getMessage() + "\n");
		} catch (StreamCorruptedException e) {
			Log.e("StreamCorruptedException: ", e.getMessage() + "\n");
		} catch (IOException e) {
			Log.i("IOException: ", e.getMessage() + "\n");
		} catch (CloneNotSupportedException e) {
			Log.i("CloneNotSupportedException: ", e.getMessage() + "\n");
		}
    }
	
	private void prepareScreenInstance(
			final Context context, final String path, final int width, final int height, final int densityDPI) 
			throws StreamCorruptedException, IOException, CloneNotSupportedException
	{
		this.context = context;
		
		final FileMethods fileMethods = new FileMethods(pathFolder, path);
		fileMethods.cargarDatosDeFichero(partitura);

		config = Config.getInstance(densityDPI, width, height);
		scroll = vista == Vista.VERTICAL ? new ScrollVertical() : new ScrollHorizontal();		
						
		isValidScreen = true;
	}

	public boolean validScreen() {
		return isValidScreen;
	}
	
	public Vista getVista()
	{
		return vista;
	}
	
	public void crearOrdenesDibujo(final Vista vista)
	{
		scroll = vista == Vista.VERTICAL ? new ScrollVertical() : new ScrollHorizontal();
		
		final DrawingMethods metodosDibujo = new DrawingMethods(partitura, getResources());
		if (metodosDibujo.canDraw()) {
			ordenesDibujo = metodosDibujo.crearOrdenesDeDibujo(vista);
		}
	}

	public void draw(final Canvas canvas) 
	{
		if (canvas != null) 
		{
			inicializarParametrosScroll(canvas);
		
			canvas.drawARGB(255, 255, 255, 255);
			canvas.save();
            
			translateCanvas(canvas);
			drawToCanvas(canvas);
            scroll.dibujarBarra(canvas);
            
            canvas.restore();
		}
    }
	
	//  Scroll parameters are constantly initialized.
	//  Check it out later on
	private void inicializarParametrosScroll(final Canvas canvas) 
	{
		partitura.setWidth(canvas.getWidth());
		partitura.setHeight(canvas.getHeight());

		if (vista == Vista.HORIZONTAL) {
			final int xFin = partitura.getCompas(partitura.getNumeroDeCompases() - 1).getXFin();
			scroll.inicializar(canvas.getWidth(), xFin);
		}
		else {
			scroll.inicializar(canvas.getHeight(), partitura.getLastMarginY());
		}
	}
	
	private void translateCanvas(final Canvas canvas)
	{
		if (vista == Vista.VERTICAL) {
			canvas.translate(0, scroll.getCooOffset());
		} else { 
			canvas.translate(scroll.getCooOffset(), 0);
		}
	}
	
	private void drawToCanvas(final Canvas canvas) 
	{
		OrdenDibujo ordenDibujo;
		
		final int numOrdenes = ordenesDibujo.size();
		for (int i=0; i<numOrdenes; i++) 
		{
			ordenDibujo = ordenesDibujo.get(i);
			if (ordenDibujo == null) { 
				continue;
			}
			
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
					canvas.drawPath(preparePath(ordenDibujo), ordenDibujo.getPaint());
					break;
					
				default:
					break;
			}
		}
		
		drawMetronome(canvas);
	}
	
	private Path preparePath(final OrdenDibujo ordenDibujo)
	{
		final RectF rectf = ordenDibujo.getRectF();
		
		final Path path = new Path();
		if (ordenDibujo.clockwiseAngle()) {
			path.addArc(rectf, 0, -180);
		} else {
			path.addArc(rectf, 0, 180);
		}
		
		final Matrix matrix = getMatrix(rectf, ordenDibujo.getAngulo());
		path.transform(matrix, path);
		
		return path;
	}
	
	/*  
	 * La rotación de una matriz produce una traslación
	 * involuntaria e indeseada que debemos contrarrestar
	 * manualmente para que el resultado quede bien.  
	 */
	public Matrix getMatrix(final RectF rectf, final float angulo) {
		final Matrix matrix = new Matrix();
		
		if (angulo > 0) {
			matrix.postRotate(angulo, rectf.left, rectf.bottom);
		}
		else {
			matrix.postRotate(angulo, rectf.right, rectf.top);
		}

		//  Contrarrestar traslación accidental. En el futuro
		//  considerar que girar a la izquierda y a la derecha
		//  requieren valores diferentes para obtener el mismo resultado
		if (angulo < 0) {
			matrix.postTranslate(angulo, 0);
		}
		
		return matrix;
	}
	
	private void drawMetronome(final Canvas canvas)
	{
		if (metronomo != null) {
			final OrdenDibujo bip = metronomo.getBip();
			final OrdenDibujo barra = metronomo.getBarra();
			
			if (bip != null) {
				canvas.drawText(bip.getTexto(), bip.getX1(), 
						bip.getY1(), bip.getPaint());
			}
			if (barra != null) {
				canvas.drawLine(barra.getX1(), barra.getY1(), 
						barra.getX2(), barra.getY2(), barra.getPaint());
			}
		}
	}

	public void cambiarVista(final Vista vista) 
	{
		this.vista = vista;
	}

	public void metronomePause()
	{
		if (metronomo != null) {
			if (metronomo.paused()) {
				metronomo.onResume();
			} else { 
				metronomo.onPause();
			}
		}
	}
	
	public void metronomePlay(final int bpm)
	{
		if (metronomo == null) {			
    		metronomo = new Metronome(context, partitura, scroll);
    		metronomo.run(bpm, vista);
		}
	}

	public void metronomeStop()
	{
		if (metronomo != null) {
			metronomo.onDestroy();
			metronomo = null;
		}
	}

	public void readMicrophone(final int sensibilidad, final int velocidad) throws Exception 
	{
		prepareSoundReader(sensibilidad, velocidad);
		
		xActual = partitura.getCompas(0).getXIni();
		
		Toast.makeText(context, R.string.startPlaying, Toast.LENGTH_SHORT).show();
	}
	
	private void prepareSoundReader(final int sensibilidad, final int velocidad) throws Exception
	{
		soundReader = new SoundReader(velocidad);
		soundReader.addObserver(this);
		soundReader.setSensitivity(sensibilidad);
	}
	
	public void stopMicrophone() 
	{
		if (soundReader != null) 
		{
			soundReader.deleteObservers();
			soundReader.onDestroy();
			soundReader = null;
		}
	}

	@Override
	public void update(final Observable observable, final Object data) 
	{
		if ((Integer) data > 0) 
		{
			Compas compas = partitura.getCompas(compasActual);
			final int golpesSonido = compas.golpesDeSonido();
			
			if (golpeSonidoActual++ >= golpesSonido) 
			{
				compas = partitura.getCompas(++compasActual);
				golpeSonidoActual = 0;
				
				gestionScroll(compas);
			}
		}
	}
	
	private void gestionScroll(final Compas compas)
	{
		if (compas.getXIni() != xActual) 
		{
			xActual = compas.getXFin();
			
			primerCompas = StaticMethods.manageScroll(compas.getYFin(), vista, xActual, 
					scroll, partitura, primerCompas, compasActual);
		}
	}

	public void back()
	{
		scroll.back();
	}

	public void forward()
	{
		scroll.forward();
	}
	
	public boolean goToBar(int bar) 
	{
		final Compas primerCompas = partitura.getCompas(0);
		final int numeroPrimerCompas = primerCompas.getNumeroCompas();
		
		if (!checkBoundaries(bar, numeroPrimerCompas)) {
			return false;
		}
		
		//  numeroPrimerCompas puede valer 1 ó 0. Si vale 1, 
		//  hay que restar este valor a bar para que acceda 
		//  al compás correcto dentro del array de compases
		bar -= numeroPrimerCompas;
		
		if (vista == Vista.HORIZONTAL) {
			move(partitura.getCompas(bar).getXIni(), 0);
		}
		else {
			move(partitura.getCompas(bar).getYIni(), config.distanciaPentagramas);
		}
		
		return true;
	}
	
	private boolean checkBoundaries(final int bar, final int numeroPrimerCompas)
	{
		boolean validBoundaries = true;
		
		if (bar > partitura.getNumeroDeCompases() || bar < 0) {
			validBoundaries = false;
		}

		//  Rango [0, numeroCompases - 1]
		if ( numeroPrimerCompas == 0 && bar == partitura.getNumeroDeCompases() ) {
			validBoundaries = false;
		}
		
		//  Rango [1, numeroCompases]
		if ( numeroPrimerCompas == 1 && bar == 0 ){
			validBoundaries = false;
		}
		
		return validBoundaries;
	}

	private void move(final int cooIni, final int distanciaPentagramas)
	{
		final float cooOffset = - scroll.getCooOffset();
		final float distancia = cooIni < cooOffset ? 
				Math.abs(cooOffset - cooIni) * -1 : Math.abs(cooOffset - cooIni);
		
		scroll.hacerScroll((int) distancia - distanciaPentagramas);
	}
	
	@Override
	public void surfaceChanged(final SurfaceHolder arg0, final int arg1, final int arg2, final int arg3) {}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) 
	{
		thread = new ScreenThread(getHolder(), this);
		thread.setRunning(true);
		thread.start();	
	}

	@Override
	public void surfaceDestroyed(final SurfaceHolder holder) 
	{
		thread.setRunning(false);
		thread = null;
		
		if (metronomo != null) {
			metronomo.onDestroy();
			metronomo = null;
		}
		
		isValidScreen = false;
		
		partitura.destruir();
		partitura = null;
		
		ordenesDibujo.clear();
		ordenesDibujo = null;
		
		scroll.back();
		scroll = null;
		config = null;
		
		stopMicrophone();
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event)
	{
		final float coo = vista == Vista.VERTICAL ? event.getY() : event.getX();
		
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				scroll.down(coo);
	            break;
	            
	        case MotionEvent.ACTION_MOVE:
	        	scroll.move(coo);
	            break;
	            
	        case MotionEvent.ACTION_UP:
	        	if (scroll.up(coo)) 
	        	{
	        		final BpmManagement bpmManagement = 
	        				new BpmManagement(partitura, ordenesDibujo, context); 
	    			bpmManagement.tapManagement(event, scroll, vista);
	        	}
	        	break;
	        	
	        default:
	        	break;
	    }

	    return true;
	}
	
	@Override
	public boolean dispatchTouchEvent(final MotionEvent event) 
	{
		if (mDialog != null) {
			final Rect dialogBounds = new Rect();
		    mDialog.getWindow().getDecorView().getHitRect(dialogBounds);
	
		    if (!dialogBounds.contains((int) event.getX(), (int) event.getY())) {
		        mDialog.dismiss();
		        mDialog = null;
		    }
		}
		
	    return super.dispatchTouchEvent(event);
	}
}
