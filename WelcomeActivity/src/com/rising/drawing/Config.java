package com.rising.drawing;

public class Config {
	
	private boolean supported = true;
	
	public int altoDialogBpm;
	public int alturaArcoLigadurasExpresion;
	public int alturaArcoLigadurasUnion;
	public int alturaCrescendos;
	public int anchoBeams;
	public int anchoBeamsNotaGracia;
	public int anchoCabezaNota;
	public int anchoCabezaNotaGracia;
	public int anchoClaves;
	public int anchoDialogBpm;
	public int anchoHooks;
	public int anchoLigaduraUnionMax;
	public int anchoTempo;
	public int changeAccountHorizontal;
	public int changeAccountVertical;
	public int desplazamientoExtraNotaGracia;
	public int distanciaCorchetes;
	public int distanciaEntreBeams;
	public int distanciaEntreBeamsNotaGracia;
	public int distanciaLineasPentagrama;
	public int distanciaLineasPentagramaMitad;
	public int distanciaPentagramas;
	public int largoImagenCorchete;
	public int largoImagenCorcheteNotaGracia;
	public int longitudPlica;
	public int longitudPlicaNotaGracia;
	public int margenAnchoCabezaNota;
	public int margenAutor;
	public int margenBarlines;
	public int margenDerechoCompases;
	public int margenInferiorAutor;
	public int margenIzquierdoCompases;
	public int margenNotaGracia;
	public int margenObra;
	public int margenSuperior;
	public int mitadCabezaVertical;
	public int mitadCabezaVerticalNotaGracia;
	public int offsetAccent;
	public int offsetUltimaNotaGracia;
	public int offsetLigaduraExpresion;
	public int offsetLigaduraUnion;
	public int radioOctavarium;
	public int radioPuntillos;
	public int radioStaccatos;
	public int tamanoLetraAutor;
	public int tamanoLetraBipPreparacion;
	public int tamanoLetraBpm;
	public int tamanoLetraNumeroCompas;
	public int tamanoLetraObra;
	public int tamanoLetraPalmMute;
	public int tamanoLetraPulso;
	public int tamanoLetraTapping;
	public int tamanoLetraTempo;
	public int tamanoLetraTresillo;
	public int tamanoLetraWords;
	public int unidadDesplazamiento;
	public int width;
	public int height;
	
	public int xAccidental;
	public int xAccidental2;
	public int xAccidentalNotaGracia;
	public int xArpegio;
	public int xFermata;
	public int xFinalPentagramas;
	public int xFinSlash;
	public int xInicialPentagramas;
	public int xInicioSlash;
	public int xLigadurasUnion;
	public int xNumeroCompas;
	public int xPuntillo;
	public int xStaccato;
	public int xTresillo;

	public int yAccentUp;
	public int yAccidental;
	public int yAccidentalFlat;
	public int yBend;
	public int yBpm;
	public int yClaveSolSegunda;
	public int yFermata;
	public int yFinSlash;
	public int yInicioSlash;
	public int yLigadurasExpresion;
	public int yLigadurasUnion;
	public int yNumeroCompas;
	public int yOctavarium;
	public int yPalmMute;
	public int yPuntilloArriba;
	public int yPuntilloAbajo;
	public int ySilencioBlanca;
	public int ySlideTruncado;
	public int yStaccatoAbajo;
	public int yStaccatoArriba;
	public int yTapping;
	public int yTresilloAbajo;
	public int yTresilloArriba;
	public int yTrill;
	
	public Config(int densityDPI, int width, int height) {
		
		switch (densityDPI) {
			case 120:
				break;
			case 160:
				break;
			case 213:
				break;
				
			case 240:
				break;
				
			case 320:
				
				switch(height){
					case 720:
						altoDialogBpm = 850;
						alturaArcoLigadurasExpresion = 30;
						alturaArcoLigadurasUnion = 50;
						alturaCrescendos = 20;
						anchoBeams = 8;
						anchoBeamsNotaGracia = 4;
						anchoCabezaNota = 26;
						anchoCabezaNotaGracia = 15;
						anchoClaves = 88;
						anchoDialogBpm = 600;
						anchoHooks = 16;
						anchoLigaduraUnionMax = 150;
						anchoTempo = 65;
						changeAccountHorizontal = 2;
						changeAccountVertical = 4;
						desplazamientoExtraNotaGracia = 40;
						distanciaCorchetes = 15;
						distanciaEntreBeams = 12;
						distanciaEntreBeamsNotaGracia = 6;
						distanciaLineasPentagrama = 19;
						distanciaLineasPentagramaMitad = 9;
						distanciaPentagramas = 240;
						largoImagenCorchete = 25;
						largoImagenCorcheteNotaGracia = 5;
						longitudPlica = 60;
						longitudPlicaNotaGracia = 30;
						margenAnchoCabezaNota = 5;
						margenAutor = 180;
						margenBarlines = 10;
						margenDerechoCompases = 70;
						margenInferiorAutor = 320;
						margenIzquierdoCompases = 50;
						margenNotaGracia = 4;
						margenObra = 90;
						margenSuperior = 80;
						mitadCabezaVertical = 10;
						mitadCabezaVerticalNotaGracia = 4;
						offsetAccent = 8;
						offsetLigaduraExpresion = 40;
						offsetLigaduraUnion = 50;
						offsetUltimaNotaGracia = 24;
						radioOctavarium = 3;
						radioPuntillos = 4;
						radioStaccatos = 4;
						tamanoLetraAutor = 50;
						tamanoLetraBipPreparacion = 300;
						tamanoLetraBpm = 50;
						tamanoLetraNumeroCompas = 30;
						tamanoLetraObra = 80;
						tamanoLetraPalmMute = 20;
						tamanoLetraPulso = 50;
						tamanoLetraTapping = 30;
						tamanoLetraTresillo = 25;
						tamanoLetraTempo = 45;
						tamanoLetraWords = 30;
						unidadDesplazamiento = 50;
						this.width = width;
						
						xAccidental = 20;
						xAccidental2 = 10;
						xAccidentalNotaGracia = 5;
						xArpegio = 55;
						xInicialPentagramas = 80;
						xInicioSlash = 15;
						xFermata = 15;
						xFinalPentagramas = width - xInicialPentagramas;
						xFinSlash = 5;
						xLigadurasUnion = 20;
						xNumeroCompas = 50;
						xPuntillo = anchoCabezaNota + 10;
						xStaccato = 15;
						xTresillo = 18;
						
						yAccentUp = 30;
						yAccidental = 5;
						yAccidentalFlat = 12;
						yBend = 40;
						yBpm = 30;
						yClaveSolSegunda = 63;
						yInicioSlash = 10;
						yFermata = 50;
						yFinSlash = 20;
						yLigadurasExpresion = 50;
						yLigadurasUnion = 24;
						yNumeroCompas = 15;
						yOctavarium = 30;
						yPalmMute = 50;
						yPuntilloArriba = mitadCabezaVertical - 10;
						yPuntilloAbajo = mitadCabezaVertical + 10;
						ySilencioBlanca = 10;
						ySlideTruncado = 20;
						yStaccatoArriba = 28;
						yStaccatoAbajo = 14;
						yTapping = 50;
						yTresilloArriba = 9;
						yTresilloAbajo = 26;
						yTrill = 44;
						break;
					case 800:
						
					case 960:
						break;
					case 1080:
						break;
					default:
						altoDialogBpm = 850;
						alturaArcoLigadurasExpresion = 30;
						alturaArcoLigadurasUnion = 50;
						alturaCrescendos = 20;
						anchoBeams = 8;
						anchoBeamsNotaGracia = 4;
						anchoCabezaNota = 26;
						anchoCabezaNotaGracia = 15;
						anchoClaves = 88;
						anchoDialogBpm = 600;
						anchoHooks = 16;
						anchoLigaduraUnionMax = 150;
						anchoTempo = 65;
						changeAccountHorizontal = 2;
						changeAccountVertical = 4;
						desplazamientoExtraNotaGracia = 40;
						distanciaCorchetes = 15;
						distanciaEntreBeams = 12;
						distanciaEntreBeamsNotaGracia = 6;
						distanciaLineasPentagrama = 19;
						distanciaLineasPentagramaMitad = 9;
						distanciaPentagramas = 240;
						largoImagenCorchete = 25;
						largoImagenCorcheteNotaGracia = 5;
						longitudPlica = 60;
						longitudPlicaNotaGracia = 30;
						margenAnchoCabezaNota = 5;
						margenAutor = 180;
						margenBarlines = 10;
						margenDerechoCompases = 70;
						margenInferiorAutor = 320;
						margenIzquierdoCompases = 50;
						margenNotaGracia = 4;
						margenObra = 90;
						margenSuperior = 80;
						mitadCabezaVertical = 10;
						mitadCabezaVerticalNotaGracia = 4;
						offsetAccent = 8;
						offsetLigaduraExpresion = 40;
						offsetLigaduraUnion = 50;
						offsetUltimaNotaGracia = 24;
						radioOctavarium = 3;
						radioPuntillos = 4;
						radioStaccatos = 4;
						tamanoLetraAutor = 50;
						tamanoLetraBipPreparacion = 300;
						tamanoLetraBpm = 50;
						tamanoLetraNumeroCompas = 30;
						tamanoLetraObra = 80;
						tamanoLetraPalmMute = 20;
						tamanoLetraPulso = 50;
						tamanoLetraTapping = 30;
						tamanoLetraTresillo = 25;
						tamanoLetraTempo = 45;
						tamanoLetraWords = 30;
						unidadDesplazamiento = 50;
						this.width = width;
						
						xAccidental = 20;
						xAccidental2 = 10;
						xAccidentalNotaGracia = 5;
						xArpegio = 55;
						xInicialPentagramas = 80;
						xInicioSlash = 15;
						xFermata = 15;
						xFinalPentagramas = width - xInicialPentagramas;
						xFinSlash = 5;
						xLigadurasUnion = 20;
						xNumeroCompas = 50;
						xPuntillo = anchoCabezaNota + 10;
						xStaccato = 15;
						xTresillo = 18;
						
						yAccentUp = 30;
						yAccidental = 5;
						yAccidentalFlat = 12;
						yBend = 40;
						yBpm = 30;
						yClaveSolSegunda = 63;
						yInicioSlash = 10;
						yFermata = 50;
						yFinSlash = 20;
						yLigadurasExpresion = 50;
						yLigadurasUnion = 24;
						yNumeroCompas = 15;
						yOctavarium = 30;
						yPalmMute = 50;
						yPuntilloArriba = mitadCabezaVertical - 10;
						yPuntilloAbajo = mitadCabezaVertical + 10;
						ySilencioBlanca = 10;
						ySlideTruncado = 20;
						yStaccatoArriba = 28;
						yStaccatoAbajo = 14;
						yTapping = 50;
						yTresilloArriba = 9;
						yTresilloAbajo = 26;
						yTrill = 44;
						break;
				}

			case 400:
				break;
			case 480:
				break;
			default: 
				supported = false;
		}
	}
	
	/**
	 * 
	 * @return True if the screen specifications of the device are supported, false otherwise
	 */
	public boolean supported() {
		return supported;
	}
}