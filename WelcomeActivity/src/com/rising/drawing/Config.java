package com.rising.drawing;

public final class Config {
	
	private static Config configInstance;
	private boolean supportedConfig = true;
	
	public transient int altoDialogBpm;
	public transient int alturaArcoLigadurasExpresion;
	public transient int alturaArcoLigadurasUnion;
	public transient int alturaCrescendos;
	public transient int anchoBeams;
	public transient int anchoBeamsNotaGracia;
	public transient int anchoCabezaNota;
	public transient int anchoCabezaNotaGracia;
	public transient int anchoClaves;
	public transient int anchoDialogBpm;
	public transient int anchoHooks;
	public transient int anchoLigaduraUnionMax;
	public transient int anchoTempo;
	public transient int changeAccountHorizontal;
	public transient int changeAccountVertical;
	public transient int desplazamientoExtraNotaGracia;
	public transient int distanciaCorchetes;
	public transient int distanciaEntreBeams;
	public transient int distanciaEntreBeamsNotaGracia;
	public transient int distanciaLineasPentagrama;
	public transient int distanciaLineasPentagramaMitad;
	public transient int distanciaPentagramas;
	public transient int divisions;
	public transient int largoImagenCorchete;
	public transient int largoImagenCorcheteNotaGracia;
	public transient int longitudPlica;
	public transient int longitudPlicaNotaGracia;
	public transient int margenAnchoCabezaNota;
	public transient int margenAutor;
	public transient int margenBarlines;
	public transient int margenDerechoCompases;
	public transient int margenInferiorAutor;
	public transient int margenIzquierdoCompases;
	public transient int margenNotaGracia;
	public transient int margenObra;
	public transient int margenSuperior;
	public transient int mitadCabezaVertical;
	public transient int mitadCabezaVerticalNotaGracia;
	public transient int offsetAccent;
	public transient int offsetUltimaNotaGracia;
	public transient int offsetLigaduraExpresion;
	public transient int offsetLigaduraUnion;
	public transient int radioOctavarium;
	public transient int radioPuntillos;
	public transient int radioStaccatos;
	public transient int tamanoLetraAutor;
	public transient int tamanoLetraBipPreparacion;
	public transient int tamanoLetraBpm;
	public transient int tamanoLetraNumeroCompas;
	public transient int tamanoLetraObra;
	public transient int tamanoLetraPalmMute;
	public transient int tamanoLetraPulso;
	public transient int tamanoLetraTapping;
	public transient int tamanoLetraTempo;
	public transient int tamanoLetraTresillo;
	public transient int tamanoLetraWords;
	public transient int unidadDesplazamiento;
	public transient int width;
	public transient int height;
	
	public transient int xAccidental;
	public transient int xAccidental2;
	public transient int xAccidentalNotaGracia;
	public transient int xArpegio;
	public transient int xFermata;
	public transient int xFinalPentagramas;
	public transient int xFinSlash;
	public transient int xInicialPentagramas;
	public transient int xInicioSlash;
	public transient int xLigadurasUnion;
	public transient int xNumeroCompas;
	public transient int xPuntillo;
	public transient int xStaccato;
	public transient int xTresillo;

	public transient int yAccentUp;
	public transient int yAccidental;
	public transient int yAccidentalFlat;
	public transient int yBend;
	public transient int yBpm;
	public transient int yClaveSolSegunda;
	public transient int yFermata;
	public transient int yFinSlash;
	public transient int yInicioSlash;
	public transient int yLigadurasExpresion;
	public transient int yLigadurasUnion;
	public transient int yNumeroCompas;
	public transient int yOctavarium;
	public transient int yPalmMute;
	public transient int yPuntilloArriba;
	public transient int yPuntilloAbajo;
	public transient int ySilencioBlanca;
	public transient int ySlideTruncado;
	public transient int yStaccatoAbajo;
	public transient int yStaccatoArriba;
	public transient int yTapping;
	public transient int yTresilloAbajo;
	public transient int yTresilloArriba;
	public transient int yTrill;
	
	private Config(final int densityDPI, final int width, final int height) {
		
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
						divisions = 16;
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
						divisions = 16;
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
				supportedConfig = false;
		}
	}
	
	public boolean supported() {
		return supportedConfig;
	}
	
	public static synchronized Config getInstance(final int densityDPI, final int width, final int height)
	{
		if (configInstance == null) {
			configInstance = new Config(densityDPI, width, height);
		}
		
		return configInstance;
	}
	
	public static synchronized Config getInstance()
	{
		return configInstance;
	}
}