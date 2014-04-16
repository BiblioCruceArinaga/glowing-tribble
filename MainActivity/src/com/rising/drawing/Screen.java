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
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class Screen extends SurfaceView implements SurfaceHolder.Callback {

	private boolean isValidScreen = false;
	private ObjectInputStream fichero = null;
	private Partitura partitura;
	private Compas compas;
	private Nota nota;
	
	//  === Constructor y métodos heredados
	public Screen(Context context, String path){
		super(context);
		getHolder().addCallback(this);
		
		partitura = new Partitura();
		compas = new Compas();
		nota = new Nota();

		try {
			File f = new File(Environment.getExternalStorageDirectory() + 
					"/RisingScores/scores/" + path);
	        FileInputStream is = new FileInputStream(f);
			fichero = new ObjectInputStream(is);
			
			cargarDatosDeFichero();
			
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
	public void surfaceCreated(SurfaceHolder holder) {}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {}

	@Override
	public boolean onTouchEvent(MotionEvent e){		
		switch (e.getAction()){
			case MotionEvent.ACTION_DOWN:
	            break;
	        case MotionEvent.ACTION_MOVE:                      
	            break;
	        case MotionEvent.ACTION_UP:
	        	break;
	        default:
	        	break;
	    }

	    return true;
	}
	//  ================================
	
	//  === Métodos de gestión del fichero
	private void cargarDatosDeFichero() throws IOException {
		leerDatosBasicosDePartitura();
		
		byte byteLeido = 0;
		while (byteLeido != -128) {
			
			switch (byteLeido) {
				case 125:
					//leerFiguraGraficaCompas();
					break;
					/*
				case 126:
					leerFiguraGraficaNota();
					break;
					*/
				case 127:
					partitura.addCompas(compas);
					break;
				
				default:
					leerInfoNota();
					break;
			}
		}
		
		isValidScreen = true;
	}

	public boolean isValidScreen() {
		return isValidScreen;
	}
	
	private ArrayList<Byte> leerClave() throws IOException {
		ArrayList<Byte> arrayBytes = new ArrayList<Byte>();
		
		byte pentagrama = 0;
		byte clave = 0;
		byte alteracion = 0;
		
		int numClefs = partitura.getStaves();
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
				elemento.addValue(fichero.readByte());
				elemento.setPosition(leerHastaAlmohadilla());
				compas.setRepeatOrEnding(elemento);
				break;
				
			case 30:
				elemento.addAllValues(leerClave());
				elemento.setPosition(leerHastaAlmohadilla());
				compas.addClef(elemento);
				break;
				
			case 31:
				elemento.addAllValues(leerTempo());
				elemento.setPosition(leerHastaAlmohadilla());
				compas.setTime(elemento);
				break;
				
			default: 
				break;
		}
	}
	
	private void leerFiguraGraficaNota() {
		
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
	
	private void leerInfoNota() {
		
	}
	
	private ArrayList<Byte> leerTempo() throws IOException {
		ArrayList<Byte> arrayBytes = new ArrayList<Byte>();
		
		byte tempo = fichero.readByte();
		arrayBytes.add(tempo);
		
		return arrayBytes;
	}
	//  ================================
	
	//  === Método draw
	public void draw(Canvas canvas) {
        
    }
	//  ================================
}