package com.rising.drawing;

import com.rising.drawing.figurasgraficas.Vista;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class ScreenThread extends Thread 
{
    private transient final SurfaceHolder msurfaceHolder;
    private transient final Screen screenView;
    private transient final Resources resources;
	private transient boolean isRunning;
	private transient Vista vista = Vista.HORIZONTAL;
	private transient int orientation; // NOPMD by joel on 5/09/14 18:14
	
	public ScreenThread(final SurfaceHolder surfaceHolder, final Screen screenView,
			final Resources resources) 
	{
		super();
		
		this.msurfaceHolder = surfaceHolder;
		this.screenView = screenView;
		this.resources = resources;
		
		orientation = Configuration.ORIENTATION_UNDEFINED;
	}
	
	public void setRunning(final boolean run) 
	{
		this.isRunning = run;
	}
	
	public void run() 
	{
		Canvas canvas = null;
		
		while(isRunning)
		{
			checkView(screenView.getVista());
			checkOrientation();
			
			try {
				canvas = msurfaceHolder.lockCanvas(null);
				synchronized(msurfaceHolder) {
					screenView.draw(canvas);
				}
			} finally {
				if (canvas != null) {
					msurfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	private void checkView(final Vista vista)
	{
		if (this.vista != vista) 
		{
			screenView.crearOrdenesDibujo(vista);
			this.vista = vista;
		}
	}
	
	private void checkOrientation() 
	{
		if (orientation != resources.getConfiguration().orientation)
		{
			if (orientation != Configuration.ORIENTATION_UNDEFINED) 
			{
				Config.getInstance().changeOrientation();
				
				screenView.crearOrdenesDibujo(vista);
			}
			
			orientation = resources.getConfiguration().orientation;
		}
	}
}