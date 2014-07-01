package com.rising.pdf;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

import com.rising.drawing.R;

public class PDFReaderActivity extends Activity{

	private String pdf_score;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.pdfread_layout);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
				
		if(width<= 800){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}	
		
		Bundle b = this.getIntent().getExtras();
		pdf_score = b.getString("score");

		ActionBar aBar = getActionBar();	
		aBar.setTitle(R.string.score);	
		aBar.setIcon(R.drawable.ic_menu);
		aBar.setDisplayHomeAsUpEnabled(true);
		
	}
	
}