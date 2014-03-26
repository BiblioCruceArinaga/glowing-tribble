package com.rising.drawing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.rising.drawing.R;

class Screen extends SurfaceView implements SurfaceHolder.Callback {
	
	//  Para saber si el fichero de la partitura ya ha sido le�do
	boolean recovered;
	
	//  Hilo que continuamente dibuja la partitura
	public ScreenThread thread;
	
	//  Resources
	final Resources resources = getResources();
	
	//  M�rgenes de la partitura
	int y_margin;
	int x_ini;
	int x_end;
	float altoPantalla;
	
	//  Distancia Y para los l�mites
	private final int y_distance_limits = 60;
	
	//  Distancias Y entre las l�neas del pentagrama y entre los pentagramas en s�
	private final int y_distance = 10;
	private final int y_distance_half = 5;
	private final int y_distance_staves = 120;
	private final int yDistanceBy4 = y_distance * 4;

	//  N�mero de pentagramas en paralelo por comp�s
	int staves;
	
	//  Stream de la partitura
	ObjectInputStream ois;
	
	//  Paint
	Paint paint;
	
	//  Manipulaci�n de la partitura
    Partitura partitura;
    Compas compas;
    Subcompas subcompas;
    Nota nota;
    
    //  Nombre de la partitura
    private final String score;
    
    //  Metadatos de la partitura (obra, autor, etc.)
    String nombreObra = "";
    String nombreAutor = "";
    
    //  Manipulaci�n de las x asociadas a los subcompases
    int array_de_x[];
    int ind_x;
    
    //  �rdenes de dibujo a ejecutar
    static ArrayList<int[]> ordenes;
    private static int nOrdenes = 0;
    static ArrayList<String> ordenesStrings;
	
	//  Gesti�n del scroll
    private static float _yoffset;
    private float _prevy;
    private float div;
    private static float limiteVisibleArriba;
    private static float limiteVisibleAbajo;
    private float finalScroll;
    private boolean mostrarBarraLateral = false;
    private float offset_barraLateral = 0;
    private int tamanoBarraLateral = 0;
    private float porcentajeAltura = 0;
    
    //  Metr�nomo
    private Metronomo met = null;
    private int desplazamiento;
    private static ArrayList<int[]> pulsos = new ArrayList<int[]>();
    private static ArrayList<float[]> indicesPulsos = new ArrayList<float[]>();
    private int heightPulso;
    
    //  Bitmaps
	private final Bitmap trebleclef = BitmapFactory.decodeResource(resources, R.drawable.trebleclef);
	private final Bitmap bassclef = BitmapFactory.decodeResource(resources, R.drawable.bassclef);
	private final Bitmap mezzoforte = BitmapFactory.decodeResource(resources, R.drawable.mezzoforte);
	private final Bitmap forte = BitmapFactory.decodeResource(resources, R.drawable.forte);
	private final Bitmap rectangle = BitmapFactory.decodeResource(resources, R.drawable.rectangle);
	private final Bitmap quarterrest = BitmapFactory.decodeResource(resources, R.drawable.quarterrest);
	private final Bitmap eighthrest = BitmapFactory.decodeResource(resources, R.drawable.eighthrest);
	private final Bitmap noterest16 = BitmapFactory.decodeResource(resources, R.drawable.noterest16);
	private final Bitmap noterest32 = BitmapFactory.decodeResource(resources, R.drawable.noterest32);
	private final Bitmap noterest64 = BitmapFactory.decodeResource(resources, R.drawable.noterest64);
	private final Bitmap wh = BitmapFactory.decodeResource(resources, R.drawable.whitehead);
	private final Bitmap bhl = BitmapFactory.decodeResource(resources, R.drawable.blackheadlittle);
	private final Bitmap bh = BitmapFactory.decodeResource(resources, R.drawable.blackhead);
	private final Bitmap head = BitmapFactory.decodeResource(resources, R.drawable.head);
	private final Bitmap headinv = BitmapFactory.decodeResource(resources, R.drawable.headinv);
	private final Bitmap headinvlittle = BitmapFactory.decodeResource(resources, R.drawable.headinvlittle);
	private final Bitmap sharp = BitmapFactory.decodeResource(resources, R.drawable.sharp);
	private final Bitmap flat = BitmapFactory.decodeResource(resources, R.drawable.flat);
	private final Bitmap natural = BitmapFactory.decodeResource(resources, R.drawable.natural);
	private final Bitmap ligato = BitmapFactory.decodeResource(resources, R.drawable.ligato);
	private final Bitmap vibrato = BitmapFactory.decodeResource(resources, R.drawable.vibrato);
	private final Bitmap tremolobar = BitmapFactory.decodeResource(resources, R.drawable.tremolobar);
	private final Bitmap hammeron = BitmapFactory.decodeResource(resources, R.drawable.hammeron);
	private final Bitmap bend = BitmapFactory.decodeResource(resources, R.drawable.bend);

	//  Constructor
	public Screen(Context context, String path){
		super(context);
		getHolder().addCallback(this);
		
		//  Estos m�rgenes pueden ser asignados dependiendo de las dimensiones de la pantalla
    	x_ini = 40;
    	y_margin = 200;
    	
    	ind_x = 0;
    	score = path;
    	paint = new Paint();
    	partitura = new Partitura();
    	subcompas = new Subcompas();
    	
    	ordenes = new ArrayList<int[]>();
    	ordenesStrings = new ArrayList<String>();
    	
    	recovered = false;
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
		
		//  Parar el metr�nomo
		Metronome_Stop();
		
		//  Eliminar el hilo que dibuja la partitura
		thread.setRunning(false);
		thread = null;
		
		//  Liberar los bitmaps
		trebleclef.recycle();
		bassclef.recycle();
		mezzoforte.recycle();
		forte.recycle();
		rectangle.recycle();
		quarterrest.recycle();
		eighthrest.recycle();
		noterest16.recycle();
		noterest32.recycle();
		noterest64.recycle();
		wh.recycle();
		bhl.recycle();
		bh.recycle();
		head.recycle();
		headinv.recycle();
		headinvlittle.recycle();
		sharp.recycle();
		flat.recycle();
		natural.recycle();
		ligato.recycle();
		vibrato.recycle();
		tremolobar.recycle();
		hammeron.recycle();
		bend.recycle();
		
		//  Reiniciar todas las variables
		recovered = false;
    	staves = 0;
    	paint = null;
        partitura = null;
        compas = null;
        subcompas = null;
        nota = null;
        nombreObra = "";
        nombreAutor = "";
        array_de_x = null;
        ind_x = 0;
        ordenes.clear();
        nOrdenes = 0;
        ordenesStrings.clear();
        _yoffset = 0;
        _prevy = 0;
        div = 0;
        limiteVisibleArriba = 0;
        limiteVisibleAbajo = 0;
        finalScroll = 0;
        desplazamiento = 0;
        pulsos.clear();
        indicesPulsos.clear();
        heightPulso = 0;
        mostrarBarraLateral = false;
        offset_barraLateral = 0;
        porcentajeAltura = 0;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e){		
		switch (e.getAction()){
			case MotionEvent.ACTION_DOWN:{
	            _prevy = e.getY();
	            mostrarBarraLateral = true;
	            break;
	        }
	        case MotionEvent.ACTION_MOVE:{                        
	            _yoffset += e.getY() - _prevy;
	            porcentajeAltura = - _yoffset / finalScroll;
	            offset_barraLateral = -altoPantalla * porcentajeAltura;
	            div = e.getY() - _prevy;
	            _prevy = e.getY();
	            limiteVisibleArriba += div;
	            limiteVisibleAbajo += div;
	            
	            if (limiteVisibleArriba > 0) {
	            	_yoffset = 0;
	            	div = 0;
	            	limiteVisibleArriba = 0;
	            	limiteVisibleAbajo = altoPantalla;
	            }
	            
	            //  altoPantalla tiene un valor negativo
	            if (limiteVisibleAbajo < -finalScroll) {
	            	_yoffset = -finalScroll - altoPantalla;
	            	div = 0;
	            	limiteVisibleArriba = -finalScroll - altoPantalla;
	            	limiteVisibleAbajo = -finalScroll;
	            }

	            mostrarBarraLateral = true;
	            break;
	        }
	        
	        case MotionEvent.ACTION_UP: {
	        	mostrarBarraLateral = false;
	        	break;
	        }
	    }

	    return true;
	}
	
	public void draw(Canvas canvas) {
        try {
        	
        	if (!recovered && (canvas != null)) {
        		
        		final int separacionX = 27;
                final int separacionClave = 30;
                final int separacionTempo = 50;

                int elementoAnterior = 0;
                byte instrumento = 0;
                byte[] clave = {0,0};
                int clave_ind = 0;
                byte tempo = 0;
                byte repeticiones = 0;
                int compases = 0;
                
                //  Gesti�n de las notas de gracia
                ArrayList<NotaDeGracia> notasGracia = new ArrayList<NotaDeGracia>(); 
                int posicionElemento = -1;
                
                int numSubcompases;
                int numNotas;
                int x_anterior;
                int recolocar_j;
                int staves_aux;
                int max;
                Subcompas sc;
                Nota nt;
                
                //  Recolocaci�n de x
                int indiceInicio = 0;
                int indiceFinal = -1;
                
				char c = 0;
				Byte b = 0;
                
        		//  Estos valores dependen del canvas
                x_end = canvas.getWidth() - x_ini;
        		limiteVisibleArriba = 0;
        		limiteVisibleAbajo = - canvas.getHeight();
        		altoPantalla = limiteVisibleAbajo;

        		abrirFichero();
        		
				//  Primer byte de la partitura: n�mero de subcompases en paralelo por rengl�n
				b = ois.readByte();
				elementoAnterior = b;
	       		b = ois.readByte();
	       		staves = b;
	       		
	       		//  Desplazamiento por metr�nomo
	       		heightPulso = yDistanceBy4 + (y_distance_staves + yDistanceBy4) * (staves - 1);
		        desplazamiento = y_margin + heightPulso - 80;

	       		compas = new Compas(staves);
	       		paint.setTextSize(26);
	       		compas.asignarNumeroAlCompas(compases + 1);
	       		array_de_x = new int[staves];
	       		for (int i=0; i<staves; i++) array_de_x[i] = x_ini;
	       		compas.asignarXInicial(x_ini);
		        
				while (c != '*') {
					c = ois.readChar();
					nombreObra += c;
				}
				while (c != '#') {
					c = ois.readChar();
					nombreAutor += c;
				}
				
				nombreObra = nombreObra.substring(0,nombreObra.length() - 1);
				nombreAutor = nombreAutor.substring(0,nombreAutor.length() - 1);
				
		        while (b != -128) {
		        	
		        	b = ois.readByte();

		        	//  Nota normal y corriente
		        	if ( (b > -1) && (b < 120) ) {
						
	        			array_de_x[ind_x] = array_de_x[ind_x] + separacionX;
						
						Byte valorNota = b;
						Byte octava = ois.readByte();
			            Byte figuracion = ois.readByte();
			            Byte accion = ois.readByte();
			            Byte union = ois.readByte();
			            int ypos = DrawingMethods.posicion_nota(valorNota, 
			            		octava, figuracion, clave[ind_x], instrumento, y_margin, y_distance, y_distance_half);
			            
			            //  Si es un acorde, la posici�n de X es la misma que la de la nota anterior
			            if ( (accion > 30) && (accion < 36) ) array_de_x[ind_x] -= separacionX;
			            else posicionElemento++;
			            
			            nota = new Nota(array_de_x[ind_x],ypos,valorNota,octava,figuracion,accion,union);
			            if (accion != 9) subcompas.nuevaNota(nota);
			            else {
			            	notasGracia.add(new NotaDeGracia(nota,compases,ind_x,posicionElemento));
			            }
			          
		        	}else{
		        	
		        		//  Fin del comp�s
		        		if (b == 127) {
	
		        			//  Un nuevo comp�s ha sido manipulado
		        			compases++;
		        			posicionElemento = -1;
		        			
		        			//  Mover x a posici�n final
		        			array_de_x[ind_x] = array_de_x[ind_x] + separacionX + separacionX;
		        			
		        			//  A�adir el �ltimo subcomp�s
		        			compas.nuevoSubcompas(subcompas);
		        			subcompas = new Subcompas();

		        			//  Obtener el ancho del subcomp�s m�s ancho
		        			max = 0;
		        			for (int i=0; i<staves; i++) {
		        				if (max < array_de_x[i]) max = array_de_x[i];
		        			}
		        			
							//  Prevenir que estemos tratando varios pentagramas en paralelo
							staves_aux = staves;
							while (staves_aux-- > 1) {
								y_margin -= yDistanceBy4 + y_distance_staves;
								ind_x--;
							}

							if (max < x_end){
								
								//  Establecer el valor de x del final del comp�s
								compas.asignarXFinal(max);
									
								//  Meto el comp�s en la partitura y limpio el comp�s para usarlo de nuevo.
								partitura.nuevoCompas(compas, max, y_margin, tempo);
								tratarDisonancias(compases - 1);
								compas = new Compas(staves);
								
								//  Igualo las x de todos los subcompases 
								for (int i=0; i<staves; i++) array_de_x[i] = max;
								
								//  Por �ltimo, para este nuevo comp�s, establecemos su x inicial
								compas.asignarXInicial(max);
								
								indiceFinal++;

							}else{						

								//  Recolocamos los elementos de los compases anteriores pertenencientes a este rengl�n
								reajustarAnchoCompases(indiceInicio, indiceFinal, separacionX, separacionClave, separacionTempo);
								indiceInicio = ++indiceFinal;
								
								y_margin += yDistanceBy4 + y_distance_staves;
								
								if (staves == 2) {
									y_margin += yDistanceBy4 + y_distance_staves;
								}
								
								//  Puesto que este comp�s ir� al inicio de un rengl�n, le pondremos n�mero
								compas.asignarNumeroAlCompas(compases);
								
								//  Puesto que recolocamos el comp�s sobrante en el pentagrama 
								//  posterior, recolocamos las posiciones de cada nota del comp�s
								
								//  Necesitamos la x anterior para saber si nos encontramos acordes
								//  Adem�s, si tal cosa ocurre, como las nuevas x son calculadas
								//  a partir de la variable i del bucle, tendremos que ajustarla convenientemente
								x_anterior = 0;
								recolocar_j = 0;
								
								//  Reinicializar el array de x
								for (int i=0; i<staves; i++) array_de_x[i] = x_ini;
								
								//  Antes de tratar las notas, chequeamos si hay que poner una repetici�n al principio
								if (compas.repeticion(0) == 1) {
									compas.asignarRepeticion(1, x_ini);
								}
								
								//  Siempre deben aparecer las claves al inicio del rengl�n
								for (int i=0; i<clave_ind; i++) {
									array_de_x[i] = array_de_x[i] + separacionClave;
									compas.nuevaClave(clave[i], array_de_x[i]);
								}
								
								//  Si el comp�s tiene un tempo especificado, debemos dibujarlo
								if (compas.tempo() > 0) {
									for (int i=0; i<clave_ind; i++) {
										array_de_x[i] = array_de_x[i] + separacionTempo;
										compas.asignarTempo(compas.tempo(), array_de_x[i]);
									}
								}
								
								//  Si el comp�s tiene alguna intensidad marcada, debemos reajustar su x
								if (compas.intensidad() > 0) {
									array_de_x[0] = array_de_x[0] + separacionX;
									compas.asignarIntensidad(compas.intensidad(), array_de_x[0]);
								}

								numSubcompases = compas.numeroDeSubcompases();
								for (int i=0; i<numSubcompases; i++) {
									
									sc = compas.subcompas(i);
									numNotas = sc.numeroDeNotas();
									
									for (int j=0; j < numNotas; j++) {
										nt = sc.nota(j);
										if (j == 0) {
											x_anterior = nt.x();
											nt.asignarX(x_ini + separacionClave + separacionTempo + separacionX + separacionX * (j - recolocar_j));
											nt.asignarY(nt.y() + yDistanceBy4 + y_distance_staves);
											array_de_x[i] = nt.x() + separacionX;
										}
										else {
											if (nt.x() == x_anterior) {
												x_anterior = nt.x();
												nt.asignarX(sc.nota(j - 1).x());
												nt.asignarY(nt.y() + yDistanceBy4 + y_distance_staves);
												array_de_x[i] = nt.x() + separacionX;
												recolocar_j++;
											}
											else {
												x_anterior = nt.x();
												nt.asignarX(x_ini + separacionClave + separacionTempo + separacionX + separacionX * (j - recolocar_j));
												nt.asignarY(nt.y() + yDistanceBy4 + y_distance_staves);
												array_de_x[i] = nt.x() + separacionX;
											}
										}
									}
									
									recolocar_j = 0;
								}

								if (staves == 2) {
									for (int i=0; i<numSubcompases; i++) {
										sc = compas.subcompas(i);
										numNotas = sc.numeroDeNotas();
										for (int j=0; j < numNotas; j++) {
											nt = sc.nota(j);
											nt.asignarY(nt.y() + yDistanceBy4 + y_distance_staves);
										}
									}
								}
								
								for (int i=0; i<staves; i++) array_de_x[i] = array_de_x[i] + separacionX;
								
								//  Obtener el ancho del subcomp�s m�s ancho e igualar el ancho de todos los subcompases
			        			max = 0;
			        			for (int i=0; i<staves; i++) {
			        				if (max < array_de_x[i]) max = array_de_x[i];
			        			}
			        			for (int i=0; i<staves; i++) {
			        				array_de_x[i] = max;
			        			}
								
								//  Chequeamos si hay una repetici�n al final
								if (compas.repeticion(1) == 1) {
									compas.asignarRepeticion(2, array_de_x[ind_x]);
								}
								
								//  Posici�n del inicio del comp�s
								compas.asignarXInicial(x_ini);
								
								//  Posici�n del final del comp�s
								compas.asignarXFinal(array_de_x[ind_x]);
								
								//  A�adir comp�s
							    partitura.nuevoCompas(compas, array_de_x[ind_x], y_margin, tempo);
							    tratarDisonancias(compases - 1);
							    compas = new Compas(staves);
							    
							    //  Para el nuevo comp�s, establecer x inicial
							    compas.asignarXInicial(array_de_x[ind_x]);
							}

							elementoAnterior = b;
		        		}else{
			        	
		        			//  Nuevo subcomp�s
			        		if (b == 122) {
					       		elementoAnterior = b;
					       		
					       		array_de_x[ind_x] = array_de_x[ind_x] + separacionX + separacionX;
					       		
					       		compas.nuevoSubcompas(subcompas);
					       		subcompas = new Subcompas();
					       		
					       		y_margin += yDistanceBy4 + y_distance_staves;
					       		ind_x++;
							
			        		}else{	
			        		
			        			//  Repeticiones (faltan casos por probar)
				        		if (b == 126) {
		
									repeticiones = ois.readByte();

									//  Determinados valores para las repeticiones requieren un tratamiento especial
									switch (repeticiones) {
										
										//  Repetici�n al principio
										case 1: {
											compas.asignarRepeticion(repeticiones, array_de_x[ind_x]);
											break;
										}
									
										//  Repetici�n al final
										case 2: {
											for(int i=0; i<staves;i++) 
												array_de_x[i] = array_de_x[i] + separacionX + separacionX;
											
											compas.asignarRepeticion(repeticiones, array_de_x[ind_x]);
											
											for(int i=0; i<staves;i++)
												array_de_x[i] = array_de_x[i] - separacionX - separacionX;
											break;
										}
										
										case 3: 
										case 4:
											compas.asignarEnding(repeticiones);
											break;
										
										default: break;
									}
									
									elementoAnterior = b;
								
				        		}else{
		        			
				        			//  Tempo (faltan casos por probar)
					        		if (b == 125) {
										
										//  Mover x
										for (int i=0; i<staves; i++) array_de_x[i] = array_de_x[i] + separacionTempo;

										//  Si el elemento anterior no era una clave, la separaci�n puede disminuirse
										if (elementoAnterior != 124) array_de_x[ind_x] = array_de_x[ind_x] - 20;

										tempo = ois.readByte();
							            compas.asignarTempo(tempo, array_de_x[ind_x]);
							            
							            elementoAnterior = b;
									
					        		}else{
				        			
					        			//  Clave (faltan casos por probar)
						        		if (b == 124) {
		
											//  Mover x (sólo hay que hacerlo una vez, 
						        			//  es la misma x para todas las claves)
											if (clave_ind == 0) 
												for (int i=0; i<staves; i++) 
													array_de_x[i] = array_de_x[i] + separacionClave;
											
											//  Asignar clave
											clave[clave_ind] = ois.readByte();
								            compas.nuevaClave(clave[clave_ind++], array_de_x[ind_x]);
		
								            elementoAnterior = b;
										
						        		}else{

								        	//  Intensidad del sonido (forte, piano, mezzoforte, etc.)
								        	if (b == 120) {
								        		
								        		elementoAnterior = b;
												
												//  Asignar intensidad
												b = ois.readByte();
									            compas.asignarIntensidad(b, array_de_x[ind_x] + separacionX);
								        	
								        	}else{

								        		//  Instrumento
								        		if (b == 123) {
										            instrumento = ois.readByte();
										            elementoAnterior = b;
								        		}
								        	}
							        	}
						        	}
				        		}
			        		}
		        		}
	        		}
		        }

		        //  El margen inferior de la partitura debe estar distanciado con respecto al �ltimo comp�s
		        partitura.asignarMargenInferior(partitura.margenY(partitura.numeroDeMargenesY() - 1) + (yDistanceBy4) + 
						( (staves - 1) * (y_distance_staves + yDistanceBy4)) + y_distance_staves);
		        finalScroll = partitura.margenInferior();

		        _yoffset = 0;
		        limiteVisibleArriba = 0;
        		limiteVisibleAbajo = altoPantalla;

        		reajustarElementosCompases();
        		hacerHuecoNotasDeGracia(notasGracia);
        		generacionDePulsos();
        		
        		_yoffset = 0;
		        limiteVisibleArriba = 0;
        		limiteVisibleAbajo = altoPantalla;
        		tamanoBarraLateral = (int) ( (altoPantalla / finalScroll) * altoPantalla);
        		
		        //  Sentencias que dibujan la partitura
		        ordenes = DrawingMethods.drawScore(ordenesStrings, canvas, paint, resources, partitura, 
                        y_distance, y_distance_half, y_distance_staves, staves, x_ini, x_end, y_distance_limits, nombreObra, nombreAutor);

		        recovered = true;
        	}

        	//  N�mero de �rdenes
        	nOrdenes = ordenes.size();
        	
        	//  Redibujar canvas
        	if (canvas != null) {
	        	canvas.drawARGB(255, 255, 255, 255);
	        	canvas.save();
	            canvas.translate(0, _yoffset);
	            drawToCanvas(canvas);
	            if (mostrarBarraLateral) dibujarBarraLateral(canvas);
	            canvas.restore();
	            
	            Log.i("Canvas Draw", "Dibujando. Offset = " + _yoffset + "\n");
        	}

        }catch(IOException IOE){
			Log.i("Error", "Fallo: " + IOE.getMessage() + "\n");
		}finally{
			try{
				ois.close();
			}catch (IOException e){}			
		}
    }
	
	public void drawToCanvas(Canvas canvas) {
		int[] arrayOrdenes = new int[5];
		
		for (int i=0; i<nOrdenes; i++) {
			
			arrayOrdenes = ordenes.get(i);
			
			//  Si la orden dibuja un elemento visible en ese momento en pantalla, dibujamos la orden
			if (findY(arrayOrdenes)) {
			
				switch (arrayOrdenes[0]) {
					case 1: {
						canvas.drawLine(arrayOrdenes[1], arrayOrdenes[2], arrayOrdenes[3], arrayOrdenes[4], paint);
						break;
					}
					case 2: {
						switch (arrayOrdenes[1]) {
							
							//  Casos m�s probables primero
							case 13: {
								canvas.drawBitmap(bh, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 1: {
								canvas.drawBitmap(trebleclef, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 2: {
								canvas.drawBitmap(bassclef, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 6: {
								canvas.drawBitmap(quarterrest, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 7: {
								canvas.drawBitmap(eighthrest, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 8: {
								canvas.drawBitmap(noterest16, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 14: {
								canvas.drawBitmap(head, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 15: {
								canvas.drawBitmap(headinv, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 17: {
								canvas.drawBitmap(sharp, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 18: {
								canvas.drawBitmap(flat, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 19: {
								canvas.drawBitmap(natural, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							
							case 3: {
								canvas.drawBitmap(mezzoforte, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 4: {
								canvas.drawBitmap(forte, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 5: {
								canvas.drawBitmap(rectangle, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 9: {
								canvas.drawBitmap(noterest32, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 10: {
								canvas.drawBitmap(noterest64, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 11: {
								canvas.drawBitmap(wh, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 12: {
								canvas.drawBitmap(bhl, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 16: {
								canvas.drawBitmap(headinvlittle, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 20: {
								canvas.drawBitmap(ligato, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 21: {
								canvas.drawBitmap(vibrato, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 22: {
								canvas.drawBitmap(tremolobar, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 23: {
								canvas.drawBitmap(hammeron, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
							case 24: {
								canvas.drawBitmap(bend, arrayOrdenes[2], arrayOrdenes[3], null);
								break;
							}
						
							//  Dar�a un error
							default: break;
						}

						break;
					}
					case 3: {
						canvas.drawCircle(arrayOrdenes[1], arrayOrdenes[2], arrayOrdenes[3], paint);
						break;
					}
					case 4: {
						canvas.drawText(ordenesStrings.get(arrayOrdenes[3]), arrayOrdenes[1], arrayOrdenes[2], paint);
						break;
					}
					case 5: {
						paint.setStrokeWidth(arrayOrdenes[1]);
						break;
					}
					case 6: {
						paint.setTextSize(arrayOrdenes[1]);
						break;
					}
					case 7: {
						paint.setStyle(Paint.Style.FILL);
						break;
					}
					case 8: {
						paint.setARGB(255, arrayOrdenes[1], arrayOrdenes[2], arrayOrdenes[3]);
						break;
					}
					default: break;
				}
			}
		}
	}
	
	//  Encuentra el valor de la Y asociado a la orden recibida por p�rametro
	public boolean findY(int[] orden) {
		switch (orden[0]) {
			case 1: return ( (- orden[2] < limiteVisibleArriba) && (- orden[2] > limiteVisibleAbajo) ) ||
					( (- orden[4] < limiteVisibleArriba) && (- orden[4] > limiteVisibleAbajo) );
			case 2: return ( (- orden[3] < limiteVisibleArriba) && (- orden[3] > limiteVisibleAbajo) );
			case 3: return ( (- orden[2] < limiteVisibleArriba) && (- orden[2] > limiteVisibleAbajo) );
			case 4: return ( (- orden[2] < limiteVisibleArriba) && (- orden[2] > limiteVisibleAbajo) );
			case 5:
			case 6:
			case 7:
			case 8:
				return true;
			default: return false;
		}
	}
	
	//  Devuelve, para un valor de tempo determinado, el n�mero de pulsos de ese tempo
	public int pulsosDeTempo(int tempo) {
		switch (tempo) {
			case 2: return 2;
			case 4: return 4;
			case 7: return 7;
			case 19: return 3;
			default: return 0;
		}
	}
	
	//  Devuelve, para el fichero recibido por par�metro, su stream para leerlo
	public void abrirFichero() throws StreamCorruptedException, IOException {
		File f = new File(Environment.getExternalStorageDirectory() + "/RisingScores/scores/" + score);
        FileInputStream is = new FileInputStream(f);
		ois = new ObjectInputStream(is);
	}
	
	//  Genera los pulsos del metr�nomo
	private void generacionDePulsos() {
		
		//  Gesti�n del metr�nomo
        int repeticionPendiente = -1;
        int indicePulso = 0;
        int[] ordenPulsos;
        float[] limitesPulsos;
        int paginacion = 0;
        int numeroDePulsos = 0;
        int y_anterior = partitura.margenY(0);
        int numCompases = partitura.numeroDeCompases();
        
        for (int j=0; j<numCompases; j++) {
        	
        	//  Paginaci�n del metr�nomo
			if (y_anterior != partitura.margenY(j)) {
				paginacion++;
				if (staves == 1 && paginacion == 6) {
					limiteVisibleArriba += -desplazamiento * 6;
					limiteVisibleAbajo += -desplazamiento * 6;
					paginacion = 0;
				}
				if (staves == 2 && paginacion == 3) {
					limiteVisibleArriba += -desplazamiento * 3;
					limiteVisibleAbajo += -desplazamiento * 3;
					paginacion = 0;
				}
				y_anterior = partitura.margenY(j);
			}
			
			numeroDePulsos = partitura.compas(j).numeroPulsos();
			
			//  Si en este comp�s hay una repetici�n, la gestionamos de cara al metr�nomo
			if (partitura.compas(j).repeticion(0) == 1) {
				repeticionPendiente = indicePulso;
			}
			indicePulso += numeroDePulsos;
			
			for (int i=0; i<numeroDePulsos - 1; i++) {
				ordenPulsos = new int[5];
				ordenPulsos[0] = 1;
				ordenPulsos[1] = partitura.compas(j).pulso(i);
				ordenPulsos[2] = partitura.margenY(j);
				ordenPulsos[3] = partitura.compas(j).pulso(i);
				ordenPulsos[4] = partitura.margenY(j) + heightPulso;
				pulsos.add(ordenPulsos);
	
				limitesPulsos = new float[4];
				limitesPulsos[0] = -1;
				limitesPulsos[1] = limiteVisibleArriba;
				limitesPulsos[2] = limiteVisibleAbajo;
				
				//  Endings
				if ( (i==0) && (partitura.compas(j).ending() == 3) ) {
					indicesPulsos.get(indicesPulsos.size() - 1)[3] = indicePulso;
				}
				else limitesPulsos[3] = -1;
				
				indicesPulsos.add(limitesPulsos);
			}
			
			//  El pulso final es en la x final
			ordenPulsos = new int[5];
			ordenPulsos[0] = 1;
			ordenPulsos[1] = partitura.compas(j).pulso(numeroDePulsos - 1);
			ordenPulsos[2] = partitura.margenY(j);
			ordenPulsos[3] = partitura.compas(j).pulso(numeroDePulsos - 1);
			ordenPulsos[4] = partitura.margenY(j) + heightPulso;
			pulsos.add(ordenPulsos);
			
			if (partitura.compas(j).repeticion(1) == 1) {
				limitesPulsos = new float[4];
				limitesPulsos[0] = repeticionPendiente;
				limitesPulsos[1] = limiteVisibleArriba;
				limitesPulsos[2] = limiteVisibleAbajo;
				limitesPulsos[3] = -1;
				indicesPulsos.add(limitesPulsos);
			}
			else {
				limitesPulsos = new float[4];
				limitesPulsos[0] = -1;
				limitesPulsos[1] = limiteVisibleArriba;
				limitesPulsos[2] = limiteVisibleAbajo;
				limitesPulsos[3] = -1;
				indicesPulsos.add(limitesPulsos);
			}
        }
    }
	
	//  Reajusta el ancho de los compases entre indInicio e indFinal, ambos inclusive
    private void reajustarAnchoCompases(int indInicio, int indFinal, int separacionX, int separacionClave, int separacionTempo) {
        int espacio = x_end - partitura.compas(indFinal).x_final();
        
        //  38 es una distancia lo bastante peque�a como para simplemente desplazar la divisi�n y ya est�
        if (espacio <= 38) {
            partitura.compas(indFinal).asignarXFinal(partitura.compas(indFinal).x_final() + espacio);
            partitura.asignarDivision(indFinal, partitura.division(indFinal) + espacio);
        }
        else {
            
            //  44 es uan distancia lo bastante peque�a como para reajustar �nicamente el �ltimo comp�s
            if (espacio <= 44) {
                partitura.compas(indFinal).asignarXFinal(partitura.compas(indFinal).x_final() + espacio);
                partitura.asignarDivision(indFinal, partitura.division(indFinal) + espacio);
                
                int divisor0 = 0;
                int divisor1 = 0;
                int xAnterior = 0;
                int distanciaX = 0;
                for (int k=0; k<partitura.compas(indFinal).subcompas(0).numeroDeNotas(); k++) {
                    if (xAnterior != partitura.compas(indFinal).subcompas(0).nota(k).x()) {
                        divisor0++;
                        xAnterior = partitura.compas(indFinal).subcompas(0).nota(k).x();
                    }
                }
                if (partitura.compas(indFinal).numeroDeSubcompases() > 1) {
                    xAnterior = 0;
                    for (int k=0; k<partitura.compas(indFinal).subcompas(1).numeroDeNotas(); k++) {
                        if (xAnterior != partitura.compas(indFinal).subcompas(1).nota(k).x()) {
                            divisor1++;
                            xAnterior = partitura.compas(indFinal).subcompas(1).nota(k).x();
                        }
                    }
                }

                if (divisor0 > divisor1) distanciaX = espacio / divisor0;
                else distanciaX = espacio / divisor1;

                for (int j=0; j<partitura.compas(indFinal).numeroDeSubcompases(); j++) {
                    for (int k=0; k<partitura.compas(indFinal).subcompas(j).numeroDeNotas(); k++) {
                        partitura.compas(indFinal).subcompas(j).nota(k).asignarX(
                        		partitura.compas(indFinal).subcompas(j).nota(k).x() + distanciaX);
                    }
                }
            }
            
            //  Debemos reajustar todos los compases del pentagrama
            else {
                int numCompases = (indFinal - indInicio) + 1;
                int anchoParaCadaCompas = espacio / numCompases;
                int posicionX = partitura.compas(indInicio).x_final() + anchoParaCadaCompas;

                //  Primer paso: reajustar ancho y posici�n de los compases
                partitura.compas(indInicio).asignarXFinal(posicionX);
                if (partitura.compas(indInicio).repeticion(1) == 1) partitura.compas(indInicio).asignarRepeticion(2, posicionX);
                if (partitura.compas(indInicio).tempo() > 0) 
                    partitura.compas(indInicio).asignarTempo(partitura.compas(indInicio).tempo(), x_ini + separacionClave + separacionTempo);
                if (partitura.compas(indInicio).intensidad() > 0)
                    partitura.compas(indInicio).asignarIntensidad(partitura.compas(indInicio).intensidad(), x_ini + separacionX);
                partitura.asignarDivision(indInicio, posicionX);

                for (int i=indInicio+1; i<=indFinal; i++) {
                    partitura.compas(i).asignarXInicial(posicionX);
                    if (partitura.compas(i).repeticion(0) == 1) partitura.compas(i).asignarRepeticion(1, posicionX);
                    if (partitura.compas(i).tempo() > 0) 
                    	partitura.compas(i).asignarTempo(partitura.compas(i).tempo(), posicionX + separacionTempo);
                    if (partitura.compas(i).intensidad() > 0)
                        partitura.compas(i).asignarIntensidad(partitura.compas(i).intensidad(), posicionX + separacionX);
                    posicionX = partitura.compas(i).x_final() + anchoParaCadaCompas;
                    if (i == indFinal) posicionX = x_end;
                    partitura.compas(i).asignarXFinal(posicionX);
                    partitura.asignarDivision(i, posicionX);
                    if (partitura.compas(i).repeticion(1) == 1) partitura.compas(i).asignarRepeticion(2, posicionX);
                }

                //  Segundo paso: recolocar cada nota en funci�n del ancho del comp�s al que pertenece
                int distanciaX = 0;
                int divisor0 = 0;
                int divisor1 = 0;
                int xInicio = 0;
                int xFinal = 0;
                int xAnterior = 0;
                for (int i=indInicio; i<indFinal + 1; i++) {
                    xInicio = partitura.compas(i).x_inicial() + separacionX;
                    if (partitura.compas(i).clave(0) != 0) xInicio += separacionClave;
                    if ( (partitura.compas(i).tempo() != 0) || (i == indInicio) ) xInicio += separacionTempo;
                    xFinal = partitura.compas(i).x_final() - separacionX;
                    
                    //  Contamos el n�mero de elementos a repartir en el ancho del comp�s
                    //  Un acorde, que son varias notas, cuenta como un �nico elemento
                    //  Ajustaremos en funci�n del subcomp�s que tenga m�s elementos
                    divisor0 = 0;
                    divisor1 = 0;
                    xAnterior = 0;
                    for (int k=0; k<partitura.compas(i).subcompas(0).numeroDeNotas(); k++) {
                        if (xAnterior != partitura.compas(i).subcompas(0).nota(k).x()) {
                            divisor0++;
                            xAnterior = partitura.compas(i).subcompas(0).nota(k).x();
                        }
                    }
                    if (partitura.compas(i).numeroDeSubcompases() > 1) {
                        xAnterior = 0;
                        for (int k=0; k<partitura.compas(i).subcompas(1).numeroDeNotas(); k++) {
                            if (xAnterior != partitura.compas(i).subcompas(1).nota(k).x()) {
                                divisor1++;
                                xAnterior = partitura.compas(i).subcompas(1).nota(k).x();
                            }
                        }
                    }

                    if (divisor0 > divisor1) distanciaX = (xFinal - xInicio) / divisor0;
                    else distanciaX = (xFinal - xInicio) / divisor1;

                    for (int j=0; j<partitura.compas(i).numeroDeSubcompases(); j++) {
                        xAnterior = 0;
                        posicionX = xInicio;
                        for (int k=0; k<partitura.compas(i).subcompas(j).numeroDeNotas(); k++) {
                            if (xAnterior != partitura.compas(i).subcompas(j).nota(k).x()) {
                                xAnterior = partitura.compas(i).subcompas(j).nota(k).x();
                                partitura.compas(i).subcompas(j).nota(k).asignarX(posicionX);
                                posicionX += distanciaX;
                            }
                            else {
                                partitura.compas(i).subcompas(j).nota(k).asignarX(posicionX - distanciaX);
                            }
                        }
                    }
                }
            }
        }
    }    
	
    //  Recoloca las notas de un comp�s para que coincidan en posici�n con las del pentagrama inferior
    //  Por elemento se entienden aqu� tanto notas individuales como acordes, ya que ambas ocupan
    //  una �nica posici�n
    private void reajustarElementosCompases() {
		int n = partitura.numeroDeCompases();
		int numNotas = 0;
		int tempo = 0;
		int figuracion = 0;
		int distanciaX = 0;
		int compasIni = 0;
		int compasFin = 0;
		int x_anterior = 0;
		int pulsos = 0;
		
		//  Cantidades de espacio en el eje X que hay que 
		//  dejar para colocar la nota siguiente
		int distancia = 0;
		int distancia2 = 0;
		int distancia4 = 0;
		int distancia8 = 0;
		int distancia16 = 0;
		int distancia32 = 0;
		int distancia64 = 0;
		int[] distancias = new int[21];
		
		for (int i=0; i<n; i++) {
			for (int j=0; j<staves; j++) {
				if (partitura.compas(i).tempo() != 0) {
	    			tempo = partitura.compas(i).tempo();
				}
    			
				switch (tempo) {
				
    				//  2/4
    				case 2: {
    					compasIni = partitura.compas(i).subcompas(j).nota(0).x();
    					compasFin = partitura.compas(i).x_final() - 20;
    					distancia = (compasFin - compasIni) / 2;
    					distancia2 = distancia / 2;
    					distancia4 = distancia2 / 2;
    					distancia8 = distancia4 / 2;
    					distancia16 = distancia8 / 2;
    					
    					distancias[0] = 0;
    					distancias[1] = 0;
    					distancias[2] = 0;
    					distancias[3] = distancia * 2;
    					distancias[4] = 0;
    					distancias[5] = 0;
    					distancias[6] = distancia;
    					distancias[7] = distancia + distancia2;
    					distancias[8] = distancia + distancia2 + distancia4;
    					distancias[9] = distancia2;
    					distancias[10] = distancia2 + distancia4;
    					distancias[11] = distancia2 + distancia4 + distancia8;
    					distancias[12] = distancia4;
    					distancias[13] = distancia4 + distancia8;
    					distancias[14] = distancia4 + distancia8 + distancia16;
    					distancias[15] = distancia8;
    					distancias[16] = distancia8 + distancia16;
    					distancias[17] = distancia8 + distancia16 + distancia32;
    					distancias[18] = distancia16;
    					distancias[19] = distancia16 + distancia32;
    					distancias[20] = distancia16 + distancia32 + distancia64;
						
    					pulsos = 2;
    					
						break;
					}
				
    				//  4/4
    				case 4: {
    					compasIni = partitura.compas(i).subcompas(j).nota(0).x();
    					compasFin = partitura.compas(i).x_final() - 20;
    					distancia = (compasFin - compasIni) / 4;
    					distancia2 = distancia / 2;
    					distancia4 = distancia2 / 2;
    					distancia8 = distancia4 / 2;
    					distancia16 = distancia8 / 2;
    					distancia32 = distancia16 / 2;
    					
    					distancias[0] = distancia * 4;
    					distancias[1] = 0;
    					distancias[2] = 0;
    					distancias[3] = distancia * 2;
    					distancias[4] = distancia * 3;
    					distancias[5] = distancia * 3 + distancia2;
    					distancias[6] = distancia;
    					distancias[7] = distancia + distancia2;
    					distancias[8] = distancia + distancia2 + distancia4;
    					distancias[9] = distancia2;
    					distancias[10] = distancia2 + distancia4;
    					distancias[11] = distancia2 + distancia4 + distancia8;
    					distancias[12] = distancia4;
    					distancias[13] = distancia4 + distancia8;
    					distancias[14] = distancia4 + distancia8 + distancia16;
    					distancias[15] = distancia8;
    					distancias[16] = distancia8 + distancia16;
    					distancias[17] = distancia8 + distancia16 + distancia32;
    					distancias[18] = distancia16;
    					distancias[19] = distancia16 + distancia32;
    					distancias[20] = distancia16 + distancia32 + distancia64;
						
    					pulsos = 4;
    					
						break;
					}
    				
    				//  7/4
    				case 7: {
    					compasIni = partitura.compas(i).subcompas(j).nota(0).x();
    					compasFin = partitura.compas(i).x_final() - 20;
    					distancia = (compasFin - compasIni) / 7;
    					distancia2 = distancia / 2;
    					distancia4 = distancia2 / 2;
    					distancia8 = distancia4 / 2;
    					distancia16 = distancia8 / 2;
    					distancia32 = distancia16 / 2;
    					
    					distancias[0] = distancia * 4;
    					distancias[1] = distancia * 6;
    					distancias[2] = distancia * 7;
    					distancias[3] = distancia * 2;
    					distancias[4] = distancia * 3;
    					distancias[5] = distancia * 3 + distancia2;
    					distancias[6] = distancia;
    					distancias[7] = distancia + distancia2;
    					distancias[8] = distancia + distancia2 + distancia4;
    					distancias[9] = distancia2;
    					distancias[10] = distancia2 + distancia4;
    					distancias[11] = distancia2 + distancia4 + distancia8;
    					distancias[12] = distancia4;
    					distancias[13] = distancia4 + distancia8;
    					distancias[14] = distancia4 + distancia8 + distancia16;
    					distancias[15] = distancia8;
    					distancias[16] = distancia8 + distancia16;
    					distancias[17] = distancia8 + distancia16 + distancia32;
    					distancias[18] = distancia16;
    					distancias[19] = distancia16 + distancia32;
    					distancias[20] = distancia16 + distancia32 + distancia64;
						
    					pulsos = 7;
    					
						break;
					}
				
    				//  3/8
    				case 19: {
    					compasIni = partitura.compas(i).subcompas(j).nota(0).x();
    					compasFin = partitura.compas(i).x_final() - 20;
    					distancia = (compasFin - compasIni) / 3;
    					distancia2 = distancia / 2;
    					distancia4 = distancia2 / 2;
    					distancia8 = distancia4 / 2;
    					
    					distancias[0] = 0;
    					distancias[1] = 0;
    					distancias[2] = 0;
    					distancias[3] = 0;
    					distancias[4] = 0;
    					distancias[5] = 0;
    					distancias[6] = distancia * 2;
    					distancias[7] = distancia * 3;
    					distancias[8] = 0;
    					distancias[9] = distancia;
    					distancias[10] = distancia + distancia2;
    					distancias[11] = distancia + distancia2 + distancia4;
    					distancias[12] = distancia2;
    					distancias[13] = distancia2 + distancia4;
    					distancias[14] = distancia2 + distancia4 + distancia8;
    					distancias[15] = distancia4;
    					distancias[16] = distancia4 + distancia8;
    					distancias[17] = distancia4 + distancia8 + distancia8 / 2;
    					distancias[18] = distancia8;
    					distancias[19] = distancia8 + distancia8 / 2;
    					distancias[20] = distancia8 + distancia8 / 2 + distancia8 / 4;
						
    					pulsos = 3;
    					
						break;
					}
    				
    				default: {
    					distancias[0] = 0;
    					distancias[1] = 0;
    					distancias[2] = 0;
    					distancias[3] = 0;
    					distancias[4] = 0;
    					distancias[5] = 0;
    					distancias[6] = 0;
    					distancias[7] = 0;
    					distancias[8] = 0;
    					distancias[9] = 0;
    					distancias[10] = 0;
    					distancias[11] = 0;
    					distancias[12] = 0;
    					distancias[13] = 0;
    					distancias[14] = 0;
    					distancias[15] = 0;
    					distancias[16] = 0;
    					distancias[17] = 0;
    					distancias[18] = 0;
    					distancias[19] = 0;
    					distancias[20] = 0;
    					
    					pulsos = 0;
    				}
    			}
				
    			numNotas = partitura.compas(i).subcompas(j).numeroDeNotas();
    			x_anterior = partitura.compas(i).subcompas(j).nota(0).x();
    			distanciaX = x_anterior;
    			figuracion = partitura.compas(i).subcompas(j).nota(0).figuracion();
    			
    			//  Habrá que añadir casos en el futuro
    			partitura.compas(i).inicializarPulsos(pulsos);
				for (int pulso=0; pulso<pulsos; pulso++) 
					partitura.compas(i).nuevoPulsoMetronomo(distanciaX + distancia * pulso);
    			
    			for (int k=0; k<numNotas; k++) {
    				
    				//  Gestión de tresillos
    				if ( ( (k+2) < numNotas ) && (partitura.compas(i).subcompas(j).nota(k+2).union() == 38) ) {
						int distancia3 = distancia / 3;
    					partitura.compas(i).subcompas(j).nota(k).asignarX(distanciaX);
    					partitura.compas(i).subcompas(j).nota(k+1).asignarX(distanciaX + distancia3);
    					partitura.compas(i).subcompas(j).nota(k+2).asignarX(distanciaX + distancia3 * 2);
    					distanciaX += distancia;
    					k += 2;
    				}
    				
    				//  Resto de notas
    				else {
	    				if (partitura.compas(i).subcompas(j).nota(k).x() != x_anterior) {
	    					x_anterior = partitura.compas(i).subcompas(j).nota(k).x();
	    					
	    					distanciaX += distancias[figuracion];
	    					figuracion = partitura.compas(i).subcompas(j).nota(k).figuracion();
	    					
	    					partitura.compas(i).subcompas(j).nota(k).asignarX(distanciaX);
	    				}
	    				else {
	    					
	    					//  Las notas del primer elemento son las �nicas que no movemos
	    					if (k != 0) {
	    						partitura.compas(i).subcompas(j).nota(k).asignarX(distanciaX);
	    					}
	    				}
    				}
    			}
			}
		}
    }
    
    //  Hace hueco para las notas de gracia, 
    //  que ser�n incrustadas en el algoritmo de dibujo
    private void hacerHuecoNotasDeGracia(ArrayList<NotaDeGracia> notasGracia) {
    	int numNotasGracia = notasGracia.size();
    	
    	for (int i=0; i<numNotasGracia; i++) {
    		NotaDeGracia notaGracia = notasGracia.get(i);
    		int posicion = notaGracia.posicion();
    		int compas = notaGracia.compas();
    		int subcompas = notaGracia.subcompas();
    		int otroSubcompas = subcompas == 0 ? 1 : 0;
    		Nota nota = notaGracia.nota();
    		
    		//  Encontrar el punto desde el que hay que abrir el hueco
    		int indiceNotaGracia = 0;
    		int x_anterior = partitura.compas(compas).subcompas(subcompas).nota(0).x();
    		for (int j=0; indiceNotaGracia<posicion; j++) {
    			if (x_anterior != partitura.compas(compas).subcompas(subcompas).nota(j).x()) {
    				indiceNotaGracia++;
    				x_anterior = partitura.compas(compas).subcompas(subcompas).nota(j).x();
    			}
    		}
    		
    		//  Abrir el hueco e insertar la nota
    		int figuracion = 0;
    		if (indiceNotaGracia > 0) 
    			figuracion = partitura.compas(compas).subcompas(subcompas).nota(indiceNotaGracia - 1).figuracion();
    		
    		//  La(s) nota(s) de gracia est�(n) al principio
    		if (indiceNotaGracia == 0) {
	    		int numNotas = partitura.compas(compas).subcompas(subcompas).numeroDeNotas();
	    		for (int j=indiceNotaGracia; j<numNotas - 1; j++) {
	    			partitura.compas(compas).subcompas(subcompas).nota(j).asignarX(
	    				partitura.compas(compas).subcompas(subcompas).nota(j).x() + 17	
	    			);
	    		}

	    		partitura.compas(compas).subcompas(subcompas).nuevaNotaEnIndice(indiceNotaGracia, nota);
	    		partitura.compas(compas).subcompas(subcompas).nota(indiceNotaGracia).asignarX(
	    			partitura.compas(compas).subcompas(subcompas).nota(indiceNotaGracia + 1).x() - 17
	    		);

	    		numNotas = partitura.compas(compas).subcompas(otroSubcompas).numeroDeNotas();
	    		for (int j=0; j<numNotas - 1; j++) {
	    			partitura.compas(compas).subcompas(otroSubcompas).nota(j).asignarX(
	    				partitura.compas(compas).subcompas(otroSubcompas).nota(j).x() + 17	
	    			);
	    		}
	    		
	    		//  La �ltima nota del otro subcomp�s s�lo se desplaza si hay espacio para ello
	    		if ( (x_end - partitura.compas(compas).subcompas(otroSubcompas).nota(numNotas-1).x()) > 50 ) {
	    			partitura.compas(compas).subcompas(otroSubcompas).nota(numNotas-1).asignarX(
	    				partitura.compas(compas).subcompas(otroSubcompas).nota(numNotas-1).x() + 17	
	    			);
	    		}
    		}
    		
    		else {
    			
    			//  La(s) nota(s) de gracia no dispone(n) de hueco suficiente, ya sea porque la nota
    			//  anterior es otra nota de gracia o porque la nota anterior es una 
    			//  semicorchea o de menor duraci�n
				if (
					(partitura.compas(compas).subcompas(subcompas).nota(indiceNotaGracia - 1).accion() == 9) ||
					( (partitura.compas(compas).subcompas(subcompas).nota(indiceNotaGracia - 1).accion() != 9) &&
						( ( (figuracion > 11) && (figuracion < 21) ) || ( figuracion > 32 ) ) ) 
				) {
					int numNotas = partitura.compas(compas).subcompas(subcompas).numeroDeNotas();
		    		for (int j=indiceNotaGracia; j<numNotas - 1; j++) {
		    			partitura.compas(compas).subcompas(subcompas).nota(j).asignarX(
		    				partitura.compas(compas).subcompas(subcompas).nota(j).x() + 17	
		    			);
		    		}
		    		partitura.compas(compas).subcompas(subcompas).nuevaNotaEnIndice(indiceNotaGracia, nota);
		    		partitura.compas(compas).subcompas(subcompas).nota(indiceNotaGracia).asignarX(
		    			partitura.compas(compas).subcompas(subcompas).nota(indiceNotaGracia + 1).x() - 17
		    		);

		    		numNotas = partitura.compas(compas).subcompas(otroSubcompas).numeroDeNotas();
		    		for (int j=0; j<numNotas - 1; j++) {
		    			partitura.compas(compas).subcompas(otroSubcompas).nota(j).asignarX(
		    				partitura.compas(compas).subcompas(otroSubcompas).nota(j).x() + 17	
		    			);
		    		}
				}
				else {
					partitura.compas(compas).subcompas(subcompas).nuevaNotaEnIndice(indiceNotaGracia, nota);
					partitura.compas(compas).subcompas(subcompas).nota(indiceNotaGracia).asignarX(
		    			partitura.compas(compas).subcompas(subcompas).nota(indiceNotaGracia + 1).x() - 17
		    		);
					
					//  Esta comprobaci�n no funcionar� en el 100% de los casos. Puede haber
					//  notas muy agudas por encima del margenY. Adem�s, falta a�adir esta
		    		//  comprobaci�n en los otros casos
		    		if (partitura.margenY(compas) > partitura.compas(compas).subcompas(subcompas).nota(indiceNotaGracia).y()) {
		    			int nuevaY = partitura.compas(compas).subcompas(subcompas).nota(indiceNotaGracia).y() +
		    					yDistanceBy4 + y_distance_staves;
						if (staves == 2) {
							nuevaY += yDistanceBy4 + y_distance_staves;
						}
						
		    			partitura.compas(compas).subcompas(subcompas).nota(indiceNotaGracia).asignarY(nuevaY);
		    		}
				}
    		}
    	}
    }
    
    //  Si hay disonancias de una nota en los pentagramas,
    //  desplazamos la cabeza de una de las notas para
    //  facilitar la visualizaci�n
    public void tratarDisonancias(int indCompas) {   	
    	int inicioAcorde = 0;
    	final int numSubCompases = partitura.compas(indCompas).numeroDeSubcompases();
    	int numNotas;
    	int max = 0;
    	
    	for (int i=0; i<numSubCompases; i++) {
    		numNotas = partitura.compas(indCompas).subcompas(i).numeroDeNotas();
    		
    		//  Necesitamos hallar el m�ximo valor de y para asegurarnos
    		//  de que no invertimos la �ltima nota del acorde
    		for (int j=0; j<numNotas; j++) {
    			if (max < partitura.compas(indCompas).subcompas(i).nota(j).y())
    				max = partitura.compas(indCompas).subcompas(i).nota(j).y();
    		}
    		
    		for (int j=0; j<numNotas; j++) {
    			if ( (partitura.compas(indCompas).subcompas(i).nota(j).accion() < 31) || 
    				 (partitura.compas(indCompas).subcompas(i).nota(j).accion() > 35) ) {
    				inicioAcorde = j;
    			}
    			else {
    				if ( (j == numNotas - 1) ||
    						( 
    							( partitura.compas(indCompas).subcompas(i).nota(j+1).accion() < 31 ) ||
    							( partitura.compas(indCompas).subcompas(i).nota(j+1).accion() > 35 )
    						) ) {
    					
    					//  Fin de un acorde, tratar disonancias
    					for (int k=inicioAcorde; k<=j-1; k++) {
    						if ( Math.abs(partitura.compas(indCompas).subcompas(i).nota(k).y() - 
    								partitura.compas(indCompas).subcompas(i).nota(k+1).y()) <= y_distance_half ) {
    							
    							if (partitura.compas(indCompas).subcompas(i).nota(k).y() > 
    								partitura.compas(indCompas).subcompas(i).nota(k+1).y()) {
    								
    								if (partitura.compas(indCompas).subcompas(i).nota(k).y() < max)
    									partitura.compas(indCompas).subcompas(i).nota(k).asignarInversionEnX(true);
    								else
    									partitura.compas(indCompas).subcompas(i).nota(k+1).asignarInversionEnX(true);
    							}
    								
    							else {
    								if (partitura.compas(indCompas).subcompas(i).nota(k+1).y() < max)
    									partitura.compas(indCompas).subcompas(i).nota(k+1).asignarInversionEnX(true);
    								else
    									partitura.compas(indCompas).subcompas(i).nota(k).asignarInversionEnX(true);
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    }
    
    public void dibujarBarraLateral(Canvas canvas) {
    	paint.setStrokeWidth(5);
    	paint.setARGB(255, 0, 0, 0);
    	canvas.drawLine(x_end + 30, offset_barraLateral - _yoffset, 
    			x_end + 30, offset_barraLateral + tamanoBarraLateral - _yoffset, paint);
    }
    
    //  ===============================
    //             METR�NOMO
    //  ===============================
    
	public void Metronome_Play(int bpm){
		if (met == null) {
    		met = new Metronomo(bpm);
    		met.run();
		}
	}
	
	public void Metronome_Pause(){
		if (met != null) {
			if (met.paused()) met.onResume();
			else met.onPause();
		}
	}
	
	public void Metronome_Stop(){
		if (met != null) {
			met.onDestroy();
			met = null;
		}
	}
	
	public void Metronome_Back(){
		Thread hilo = new Thread(new Runnable(){
    		public void run() {	
    			_yoffset = indicesPulsos.get(0)[1];
    			limiteVisibleArriba = indicesPulsos.get(0)[1];
    			limiteVisibleAbajo = indicesPulsos.get(0)[2];
    			
    			Log.i("Metronome Back", "Metronome Back pulsado. Offset = " + _yoffset + "\n");
    		}
		});
		hilo.start();
	}
	
	private class Metronomo implements Runnable {
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
	    			final int nPulsos = pulsos.size();
	    			final long speed = ((240000/mbpm)/4);
                    boolean repeticionCompletada = false;
                    int numOrdenes = 1;
	    			
                    try {
		    			ordenes.add(pulsos.get(0));
		    			numOrdenes = ordenes.size();

	    				Thread.sleep(speed);

		    			_yoffset = indicesPulsos.get(0)[1];
		    			limiteVisibleArriba = indicesPulsos.get(0)[1];
		    			limiteVisibleAbajo = indicesPulsos.get(0)[2];
		    			
		    			for (int i=1; i<nPulsos; i++) {
		    				ordenes.remove(numOrdenes - 1);
		    				ordenes.add(pulsos.get(i));

		    				Thread.sleep(speed);
		    				
		    				_yoffset = indicesPulsos.get(i)[1];
		    				limiteVisibleArriba = indicesPulsos.get(i)[1];
		    				limiteVisibleAbajo = indicesPulsos.get(i)[2];
	
		    				//  Ending de una repetici�n, la segunda vez saltamos al siguiente ending
	                        if (indicesPulsos.get(i)[3] > -1) {
	                            if (repeticionCompletada) {
	                                i = (int) indicesPulsos.get(i)[3] - 1;	//  -1 para que el incremento autom�tico del bucle no afecte
	                                repeticionCompletada = false;
	                            }
	                        }
	                        
	                        //  Final de una repetici�n, movemos el cursor al inicio de la repetici�n
	                        else {
		                        if (indicesPulsos.get(i)[0] > -1) {
		                            if (!repeticionCompletada) {
		                            	i = (int) indicesPulsos.get(i)[0] - 1;  //  -1 para que el incremento autom�tico del bucle no afecte
		                            	repeticionCompletada = true;
		                            }
		                            else repeticionCompletada = false;
		                        }
	                        }
		    				
		    				synchronized (mPauseLock) {
		    	                while (mPaused) {
		    	                    try {
		    	                        mPauseLock.wait();
		    	                    } catch (InterruptedException e) {
		    	                    	Thread.currentThread().interrupt();
		    	                    	ordenes.remove(numOrdenes - 1);
		    	    					nOrdenes--;
		    	                    	mPauseLock.notifyAll();
		    	                    	return;
		    	                    }
		    	                }
		    	            }
		    	        }
                    } catch (InterruptedException e) {
	    				e.printStackTrace();
	    				Thread.currentThread().interrupt();
    					ordenes.remove(numOrdenes - 1);
    					nOrdenes--;
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
}