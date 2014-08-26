package com.rising.drawing;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class ScreenThread extends Thread {
	
    private SurfaceHolder msurfaceHolder;
    private Screen screenView;
	private boolean run;
	private Vista vista = Vista.HORIZONTAL;

	public ScreenThread(SurfaceHolder SH, Screen screenView) {
		this.msurfaceHolder = SH;
		this.screenView = screenView;
		run = false;
	}
	
	public void setRunning(boolean run) {
		this.run = run;
	}
	
	public void run() {
		Canvas c = null;
		
		while(run)
		{
			changeView(screenView.getVista());
			
			try {
				c = msurfaceHolder.lockCanvas(null);			
				synchronized(msurfaceHolder) {
					screenView.draw(c);
				}
			} finally {
				if (c != null) {
					msurfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}
	
	private void changeView(Vista vista)
	{
		if (this.vista != vista) 
		{
			screenView.crearOrdenesDibujo(vista);
			this.vista = vista;
		}
	}
}
