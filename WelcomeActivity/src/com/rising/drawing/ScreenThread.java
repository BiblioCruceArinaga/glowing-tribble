package com.rising.drawing;

import com.rising.drawing.figurasgraficas.Vista;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class ScreenThread extends Thread {
	
    private transient final SurfaceHolder msurfaceHolder;
    private transient final Screen screenView;
	private transient boolean isRunning;
	private transient Vista vista = Vista.HORIZONTAL;

	public ScreenThread(final SurfaceHolder surfaceHolder, final Screen screenView) 
	{
		super();
		
		this.msurfaceHolder = surfaceHolder;
		this.screenView = screenView;
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
			changeView(screenView.getVista());
			
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
	
	private void changeView(final Vista vista)
	{
		if (this.vista != vista) 
		{
			screenView.crearOrdenesDibujo(vista);
			this.vista = vista;
		}
	}
}
