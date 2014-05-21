package com.rising.drawing;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class ScreenThread extends Thread {
	
    private SurfaceHolder msurfaceHolder;
    private Screen screenView;
	private boolean run;

	public ScreenThread(SurfaceHolder SH, Screen screenView){
		this.msurfaceHolder = SH;
		this.screenView = screenView;
		run = false;
	}
	
	public void setRunning(boolean run){
		this.run = run;
	}
	
	public void run(){
		Canvas c;
		while(run){
			c = null;
			try{
				c = msurfaceHolder.lockCanvas(null);			
				synchronized(msurfaceHolder){
					screenView.draw(c);
				}
			}finally{
				if(c != null){
					msurfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}
}
