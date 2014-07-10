package com.rising.drawing;

public class Quintas {
	private byte notaQuintas;
	private byte valorQuintas;
	private int x;
	private int margenY;
	
	public Quintas() {
		notaQuintas = 0;
		valorQuintas = 0;
		x = 0;
		margenY = 0;
	}
	
	public int getMargenY() {
		return margenY;
	}
	
	public byte getNotaQuintas() {
		return notaQuintas;
	}
	
	public byte getValorQuintas() {
		return valorQuintas;
	}
	
	public int getX() {
		return x;
	}
	
	public void setMargenY(int margenY) {
		this.margenY = margenY;
	}
	
	public void setNotaQuintas(byte notaQuintas) {
		this.notaQuintas = notaQuintas;
	}
	
	public void setValorQuintas(byte valorQuintas) {
		this.valorQuintas = valorQuintas;
	}
	
	public void setX(int x) {
		this.x = x;
	}	
}