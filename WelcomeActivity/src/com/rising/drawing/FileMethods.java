package com.rising.drawing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.os.Environment;

public class FileMethods {

	private ObjectInputStream fichero;
	private Compas compas;
	
	public FileMethods(String path_folder, String path) throws StreamCorruptedException, IOException {		
		File f = new File(Environment.getExternalStorageDirectory() + path_folder + path);
        FileInputStream is = new FileInputStream(f);
		fichero = new ObjectInputStream(is);
		
		compas = new Compas();
	}
	
	public void cargarDatosDeFichero(Partitura horizontal, Partitura vertical) 
			throws IOException, CloneNotSupportedException {
		leerDatosBasicosDePartitura(horizontal, vertical);
		
		byte byteLeido = fichero.readByte();
		while (byteLeido != -128) {
			
			switch (byteLeido) {			
				case 126:
					leerFiguraGraficaCompas();
					break;
					
				case 127:
					horizontal.addCompas(compas);
					vertical.addCompas(compas.clonar());
					compas = new Compas();
					break;
				
				default:
					leerInfoNota(byteLeido);
					break;
			}
			
			byteLeido = fichero.readByte();
		}
		
		fichero.close();
	}

	private ArrayList<Byte> leerClaves() throws IOException {
		ArrayList<Byte> arrayBytes = new ArrayList<Byte>();
		
		byte pentagrama = 0;
		byte clave = 0;
		
		byte numClefs = fichero.readByte();
		arrayBytes.add(numClefs);
		
		for (int i=0; i<numClefs; i++) {
			pentagrama = fichero.readByte();
			clave = fichero.readByte();
			
			arrayBytes.add(pentagrama);
			arrayBytes.add(clave);
		}

		return arrayBytes;
	}
	
	private void leerDatosBasicosDePartitura(Partitura horizontal, Partitura vertical) 
			throws IOException {
		ArrayList<Byte> work = leerHastaAlmohadilla();
		ArrayList<Byte> creator = leerHastaAlmohadilla();
		byte staves = fichero.readByte();
		byte instrument = fichero.readByte();
		ArrayList<Byte> divisions = leerHastaAlmohadilla();
		int numeroCompas = fichero.readByte();
		
		vertical.setWork(work);
		vertical.setCreator(creator);
		vertical.setStaves(staves);
		vertical.setInstrument(instrument);
		vertical.setDivisions(divisions);
		vertical.setFirstNumber(numeroCompas);
		
		horizontal.setWork(work);
		horizontal.setCreator(creator);
		horizontal.setStaves(staves);
		horizontal.setInstrument(instrument);
		horizontal.setDivisions(divisions);
		horizontal.setFirstNumber(numeroCompas);
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
				compas.addPedalStart(elemento);
				break;

			case 26:
				elemento.addValue(figuraGrafica);
				elemento.setPosition(leerHastaAlmohadilla());
				compas.addPedalStop(elemento);
				break;

			case 27:
				elemento.addAllValues(leerHastaAlmohadilla());
				elemento.setPosition(leerHastaAlmohadilla());
				compas.addWords(elemento);
				break;

			case 28:
				elemento.addValue(fichero.readByte());
				elemento.setPosition(leerHastaAlmohadilla());
				compas.addBarline(elemento);
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
				
			case 32:
				elemento.addValue(fichero.readByte());
				elemento.addValue(fichero.readByte());
				elemento.setPosition(leerHastaAlmohadilla());
				compas.setFifths(elemento);
				break;
				
			case 33:
			case 34:
			case 35:
			case 36:
				elemento.addValue(figuraGrafica);
				elemento.setPosition(leerHastaAlmohadilla());
				compas.addWedge(elemento);
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
		byte pulsos = fichero.readByte();
		byte union = fichero.readByte();
		byte idUnion = fichero.readByte();
		byte plica = fichero.readByte();
		byte voz = fichero.readByte();
		byte pentagrama = fichero.readByte();
		
		ArrayList<Byte> figurasGraficas = leerHastaAlmohadilla();
		ArrayList<Byte> posicionEjeX = leerHastaAlmohadilla();

		compas.addNote(new Nota(nota, octava, figuracion, pulsos, union, 
				idUnion, plica, voz, pentagrama, figurasGraficas, posicionEjeX));
	}
}