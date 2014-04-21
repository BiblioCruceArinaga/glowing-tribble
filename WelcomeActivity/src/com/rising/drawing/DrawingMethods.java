package com.rising.drawing;

import java.util.ArrayList;

public class DrawingMethods {
	
	private static ArrayList<OrdenDibujo> ordenesDibujo = new ArrayList<OrdenDibujo>();
	
	public static ArrayList<OrdenDibujo> crearOrdenesDeDibujo(Partitura partitura,
			int margin_top, int width) {
		/*
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, 40);
		ordenDibujo.setTexto(partitura.getWork());
		ordenDibujo.setX1(50);
		ordenDibujo.setY1(margin_top + 10);
		ordenesDibujo.add(ordenDibujo);
		
		ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_CIRCLE);
		ordenDibujo.setRadius(6);
		ordenDibujo.setX1(100);
		ordenDibujo.setY1(margin_top + 20);
		ordenesDibujo.add(ordenDibujo);
		
		ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_LINE);
		ordenDibujo.setPaint(PaintOptions.SET_STROKE_WIDTH, 10);
		ordenDibujo.setX1(150);
		ordenDibujo.setY1(margin_top + 30);
		ordenDibujo.setX2(350);
		ordenDibujo.setY2(margin_top + 30);
		ordenesDibujo.add(ordenDibujo);
		
		final Bitmap quarterrest = BitmapFactory.decodeResource(getResources(), R.drawable.quarterrest);
		ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_BITMAP);
		ordenDibujo.setImagen(quarterrest);
		ordenDibujo.setX1(200);
		ordenDibujo.setY1(margin_top + 40);
		ordenesDibujo.add(ordenDibujo);
		*/
		
		OrdenDibujo ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, 80);
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_ALIGN, -1);
		ordenDibujo.setTexto(partitura.getWork());
		ordenDibujo.setX1(width / 2);
		ordenDibujo.setY1(margin_top + 100);
		ordenesDibujo.add(ordenDibujo);
		
		ordenDibujo = new OrdenDibujo();
		ordenDibujo.setOrden(DrawOrder.DRAW_TEXT);
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_SIZE, 50);
		ordenDibujo.setPaint(PaintOptions.SET_TEXT_ALIGN, -1);
		ordenDibujo.setTexto(partitura.getCreator());
		ordenDibujo.setX1(width / 2);
		ordenDibujo.setY1(margin_top + 180);
		ordenesDibujo.add(ordenDibujo);
		
		crearOrdenesDeCompases(partitura);
		
		return ordenesDibujo;
	}
	
	private static void crearOrdenesDeCompases(Partitura partitura) {
		ArrayList<Compas> compases = partitura.getCompases();
		int numCompases = compases.size();
		
		for (int i=0; i<numCompases; i++)
			crearOrdenDeCompas(compases.get(i));
	}
	
	private static void crearOrdenDeCompas(Compas compas) {
		
	}
}