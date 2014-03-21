package com.rising.store;

public class DatosUsuario {

	private String id;
	private String name;
	private double money;
	private String mail;
	
	public DatosUsuario(String id, String name, double money, String mail){
		this.id = id;
		this.name = name;
		this.money = money;
		this.mail = mail;
	}
	
	public String getId() {
		return id;
	}

	public double getMoney() {
		return money;
	}

	public String getMail() {
		return mail;
	}
	
	public String getName(){
		return name;
	}

}
