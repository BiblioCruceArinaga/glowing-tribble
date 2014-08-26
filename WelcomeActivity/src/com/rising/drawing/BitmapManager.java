package com.rising.drawing;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapManager 
{
	private static BitmapManager bitmapManagerInstance = null;
	private Resources resources;
	
	private Bitmap accent = null;
	private Bitmap arpegio = null;
	private Bitmap bassClef = null;
	private Bitmap bendRelease = null;
	private Bitmap blackHeadLittle = null;
	private Bitmap blackHead = null;
	private Bitmap eighthRest = null;
	private Bitmap fermata = null;
	private Bitmap fermataInverted = null;
	private Bitmap flat = null;
	private Bitmap forte = null;
	private Bitmap fortissimo = null;
	private Bitmap forzando = null;
	private Bitmap forzandop = null;
	private Bitmap head = null;
	private Bitmap headLittle = null;
	private Bitmap headInv = null;
	private Bitmap headInvLittle = null;
	private Bitmap marcato = null;
	private Bitmap mezzoforte = null;
	private Bitmap natural = null;
	private Bitmap noterest16 = null;
	private Bitmap noterest32 = null;
	private Bitmap noterest64 = null;
	private Bitmap octavarium = null;
	private Bitmap pedalStart = null;
	private Bitmap pedalStop = null;
	private Bitmap piano = null;
	private Bitmap pianissimo = null;
	private Bitmap pianississimo = null;
	private Bitmap quarterRest = null;
	private Bitmap rectangle = null;
	private Bitmap sharp = null;
	private Bitmap trebleClef = null;
	private Bitmap trill = null;
	private Bitmap vibrato = null;
	private Bitmap whitehead = null;
	
	private BitmapManager(Resources resources) 
	{
		this.resources = resources;
	}
	
	public static synchronized BitmapManager getInstance(Resources resources)
	{
		if (bitmapManagerInstance == null)
			bitmapManagerInstance = new BitmapManager(resources);
		
		return bitmapManagerInstance;
	}
	
	public Bitmap getAccent()
	{
		if (accent == null)
			accent = BitmapFactory.decodeResource(resources, R.drawable.accent);
		
		return accent;
	}
	
	public Bitmap getArpegio()
	{
		if (arpegio == null)
			arpegio = BitmapFactory.decodeResource(resources, R.drawable.arpegio);
		
		return arpegio;
	}
	
	public Bitmap getBassClef()
	{
		if (bassClef == null)
			bassClef = BitmapFactory.decodeResource(resources, R.drawable.bassclef);
		
		return bassClef;
	}
	
	public Bitmap getBendRelease()
	{
		if (bendRelease == null)
			bendRelease = BitmapFactory.decodeResource(resources, R.drawable.bendrelease);
		
		return bendRelease;
	}
	
	public Bitmap getBlackHeadLittle()
	{
		if (blackHeadLittle == null)
			blackHeadLittle = BitmapFactory.decodeResource(resources, R.drawable.blackheadlittle);
		
		return blackHeadLittle;
	}
	
	public Bitmap getBlackHead()
	{
		if (blackHead == null)
			blackHead = BitmapFactory.decodeResource(resources, R.drawable.blackhead);
		
		return blackHead;
	}
	
	public Bitmap getEighthRest()
	{
		if (eighthRest == null)
			eighthRest = BitmapFactory.decodeResource(resources, R.drawable.eighthrest);
		
		return eighthRest;
	}
	
	public Bitmap getFermata()
	{
		if (fermata == null)
			fermata = BitmapFactory.decodeResource(resources, R.drawable.fermata);
		
		return fermata;
	}
	
	public Bitmap getFermataInverted()
	{
		if (fermataInverted == null)
			fermataInverted = BitmapFactory.decodeResource(resources, R.drawable.fermata_inverted);
		
		return fermataInverted;
	}
	
	public Bitmap getFlat()
	{
		if (flat == null)
			flat = BitmapFactory.decodeResource(resources, R.drawable.flat);
		
		return flat;
	}
	
	public Bitmap getForte()
	{
		if (forte == null)
			forte = BitmapFactory.decodeResource(resources, R.drawable.forte);
		
		return forte;
	}
	
	public Bitmap getFortissimo()
	{
		if (fortissimo == null)
			fortissimo = BitmapFactory.decodeResource(resources, R.drawable.fortissimo);
		
		return fortissimo;
	}
	
	public Bitmap getForzando()
	{
		if (forzando == null)
			forzando = BitmapFactory.decodeResource(resources, R.drawable.forzando);
		
		return forzando;
	}
	
	public Bitmap getForzandoP()
	{
		if (forzandop == null)
			forzandop = BitmapFactory.decodeResource(resources, R.drawable.forzandop);
		
		return forzandop;
	}
	
	public Bitmap getHead()
	{
		if (head == null)
			head = BitmapFactory.decodeResource(resources, R.drawable.head);
		
		return head;
	}
	
	public Bitmap getHeadInv()
	{
		if (headInv == null)
			headInv = BitmapFactory.decodeResource(resources, R.drawable.headinv);
		
		return headInv;
	}
	
	public Bitmap getHeadInvLittle()
	{
		if (headInvLittle == null)
			headInvLittle = BitmapFactory.decodeResource(resources, R.drawable.headinvlittle);
		
		return headInvLittle;
	}
	
	public Bitmap getHeadLittle()
	{
		if (headLittle == null)
			headLittle = BitmapFactory.decodeResource(resources, R.drawable.headlittle);
		
		return headLittle;
	}
	
	public Bitmap getMarcato()
	{
		if (marcato == null)
			marcato = BitmapFactory.decodeResource(resources, R.drawable.marcato);
		
		return marcato;
	}
	
	public Bitmap getMezzoforte()
	{
		if (mezzoforte == null)
			mezzoforte = BitmapFactory.decodeResource(resources, R.drawable.mezzoforte);
		
		return mezzoforte;
	}
	
	public Bitmap getNatural()
	{
		if (natural == null)
			natural = BitmapFactory.decodeResource(resources, R.drawable.natural);
		
		return natural;
	}
	
	public Bitmap getNoteRest16()
	{
		if (noterest16 == null)
			noterest16 = BitmapFactory.decodeResource(resources, R.drawable.noterest16);
		
		return noterest16;
	}
	
	public Bitmap getNoteRest32()
	{
		if (noterest32 == null)
			noterest32 = BitmapFactory.decodeResource(resources, R.drawable.noterest32);
		
		return noterest32;
	}
	
	public Bitmap getNoteRest64()
	{
		if (noterest64 == null)
			noterest64 = BitmapFactory.decodeResource(resources, R.drawable.noterest64);
		
		return noterest64;
	}
	
	public Bitmap getOctavarium()
	{
		if (octavarium == null)
			octavarium = BitmapFactory.decodeResource(resources, R.drawable.octavarium);
		
		return octavarium;
	}
	
	public Bitmap getPedalStart()
	{
		if (pedalStart == null)
			pedalStart = BitmapFactory.decodeResource(resources, R.drawable.pedalstart);
		
		return pedalStart;
	}
	
	public Bitmap getPedalStop()
	{
		if (pedalStop == null)
			pedalStop = BitmapFactory.decodeResource(resources, R.drawable.pedalstop);
		
		return pedalStop;
	}
	
	public Bitmap getPiano()
	{
		if (piano == null)
			piano = BitmapFactory.decodeResource(resources, R.drawable.piano);
		
		return piano;
	}
	
	public Bitmap getPianissimo()
	{
		if (pianissimo == null)
			pianissimo = BitmapFactory.decodeResource(resources, R.drawable.pianissimo);
		
		return pianissimo;
	}
	
	public Bitmap getPianississimo()
	{
		if (pianississimo == null)
			pianississimo = BitmapFactory.decodeResource(resources, R.drawable.pianississimo);
		
		return pianississimo;
	}
	
	public Bitmap getQuarterRest()
	{
		if (quarterRest == null)
			quarterRest = BitmapFactory.decodeResource(resources, R.drawable.quarterrest);
		
		return quarterRest;
	}
	
	public Bitmap getRectangle()
	{
		if (rectangle == null)
			rectangle = BitmapFactory.decodeResource(resources, R.drawable.rectangle);
		
		return rectangle;
	}
	
	public Bitmap getSharp()
	{
		if (sharp == null)
			sharp = BitmapFactory.decodeResource(resources, R.drawable.sharp);
		
		return sharp;
	}
	
	public Bitmap getTrebleClef()
	{
		if (trebleClef == null)
			trebleClef = BitmapFactory.decodeResource(resources, R.drawable.trebleclef);
		
		return trebleClef;
	}
	
	public Bitmap getTrill()
	{
		if (trill == null)
			trill = BitmapFactory.decodeResource(resources, R.drawable.trill);
		
		return trill;
	}
	
	public Bitmap getVibrato()
	{
		if (vibrato == null)
			vibrato = BitmapFactory.decodeResource(resources, R.drawable.vibrato);
		
		return vibrato;
	}
	
	public Bitmap getWhiteHead()
	{
		if (whitehead == null)
			whitehead = BitmapFactory.decodeResource(resources, R.drawable.whitehead);
		
		return whitehead;
	}
}
