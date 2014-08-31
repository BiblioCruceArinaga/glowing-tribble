package com.rising.drawing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import com.rising.drawing.figurasGraficas.Compas;
import com.rising.drawing.figurasGraficas.Nota;
import com.rising.drawing.figurasGraficas.Partitura;

import android.os.Environment;

public class FileMethods {

	private transient final ObjectInputStream fichero;
	private transient Compas compas;
	
	public FileMethods(final String pathFolder, final String path) 
			throws StreamCorruptedException, IOException 
	{		
		final File file = new File(Environment.getExternalStorageDirectory() + pathFolder + path);

        fichero = new ObjectInputStream(new FileInputStream(file));
		compas = new Compas();
	}
	
	public void cargarDatosDeFichero(final Partitura partitura) 
			throws IOException, CloneNotSupportedException 
	{
		leerDatosBasicosDePartitura(partitura);
		
		byte byteLeido = fichero.readByte();
		while (byteLeido != -128) 
		{	
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
		
		fichero.close();
	}
	
	private void leerDatosBasicosDePartitura(final Partitura partitura) 
			throws IOException 
	{
		final ArrayList<Byte> work = leerHastaAlmohadilla();
		final ArrayList<Byte> creator = leerHastaAlmohadilla();
		final byte staves = fichero.readByte();
		final byte instrument = fichero.readByte();
		final int numeroCompas = fichero.readByte();
		
		partitura.setWork(work);
		partitura.setCreator(creator);
		partitura.setStaves(staves);
		partitura.setInstrument(instrument);
		partitura.setFirstNumber(numeroCompas);
	}
	
	private void leerFiguraGraficaCompas() throws IOException 
	{
		final ElementoGrafico elemento = new ElementoGrafico();

		final byte posicionFiguraGrafica = fichero.readByte();
		elemento.addValue(posicionFiguraGrafica);
		
		final byte figuraGrafica = fichero.readByte();
		switch (figuraGrafica) 
		{
			case 25:
				leerPedal(elemento, figuraGrafica);
				break;

			case 26:
				leerPedal(elemento, figuraGrafica);
				break;

			case 27:
				leerIndicacionTextual(elemento);
				break;

			case 28:
				leerBarraVertical(elemento);
				break;

			case 30:
				leerClave(elemento);
				break;
		
			case 31:
				leerTempo(elemento);
				break;
				
			case 32:
				leerQuintas(elemento);
				break;
				
			case 33:
			case 34:
			case 35:
			case 36:
				leerCrescendoODiminuendo(elemento, figuraGrafica);
				break;
				
			default:
				leerIntensidad(elemento, figuraGrafica);
				break;
		}
	}
	
	private void leerPedal(ElementoGrafico elemento, byte figuraGrafica) 
			throws IOException
	{
		elemento.addValue(figuraGrafica);
		elemento.setPosition(leerHastaAlmohadilla());
		
		compas.addPedalStart(elemento);
	}
	
	private void leerIndicacionTextual(ElementoGrafico elemento) throws IOException 
	{
		elemento.addAllValues(leerHastaAlmohadilla());
		elemento.setPosition(leerHastaAlmohadilla());
		
		compas.addWords(elemento);
	}
	
	private void leerBarraVertical(ElementoGrafico elemento) throws IOException
	{
		elemento.addValue(fichero.readByte());
		elemento.setPosition(leerHastaAlmohadilla());
		
		compas.addBarline(elemento);
	}
	
	private void leerClave(ElementoGrafico elemento) throws IOException
	{
		elemento.addValue(fichero.readByte());
		elemento.addValue(fichero.readByte());
		elemento.setPosition(leerHastaAlmohadilla());
		
		compas.addClef(elemento);
	}
	
	private void leerTempo(ElementoGrafico elemento) throws IOException
	{
		elemento.addValue(fichero.readByte());
		elemento.setPosition(leerHastaAlmohadilla());
		
		compas.setTime(elemento);
	}
	
	private void leerQuintas(ElementoGrafico elemento) throws IOException
	{
		elemento.addValue(fichero.readByte());
		elemento.addValue(fichero.readByte());
		elemento.setPosition(leerHastaAlmohadilla());
		
		compas.setFifths(elemento);
	}
	
	private void leerCrescendoODiminuendo(ElementoGrafico elemento, byte figuraGrafica) 
			throws IOException
	{
		elemento.addValue(figuraGrafica);
		elemento.setPosition(leerHastaAlmohadilla());
		
		compas.addWedge(elemento);
	}
	
	private void leerIntensidad(ElementoGrafico elemento, byte figuraGrafica)
			throws IOException
	{
		elemento.addValue(figuraGrafica);
		elemento.setPosition(leerHastaAlmohadilla());
		
		compas.addDynamics(elemento);
	}
	
	private ArrayList<Byte> leerHastaAlmohadilla() throws IOException 
	{
		final ArrayList<Byte> bytesArray = new ArrayList<Byte>();
		byte byteLeido = 0;
		
		do {
			byteLeido = fichero.readByte();
			bytesArray.add(byteLeido);
		} while (byteLeido != 35);
		
		bytesArray.remove(bytesArray.size() - 1);
		return bytesArray;
	}
	
	private void leerInfoNota(final byte nota) throws IOException 
	{
		final byte octava = fichero.readByte();
		final byte figuracion = fichero.readByte();
		final byte pulsos = fichero.readByte();
		final byte union = fichero.readByte();
		final byte idUnion = fichero.readByte();
		final byte plica = fichero.readByte();
		final byte voz = fichero.readByte();
		final byte pentagrama = fichero.readByte();
		
		final ArrayList<Byte> figurasGraficas = leerHastaAlmohadilla();
		final ArrayList<Byte> posicionEjeX = leerHastaAlmohadilla();

		compas.addNote(new Nota(nota, octava, figuracion, pulsos, union, 
				idUnion, plica, voz, pentagrama, figurasGraficas, posicionEjeX));
	}
}