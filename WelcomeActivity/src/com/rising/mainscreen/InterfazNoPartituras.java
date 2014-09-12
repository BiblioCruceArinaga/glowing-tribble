package com.rising.mainscreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.rising.drawing.R;
import com.rising.login.Login_Utils;
import com.rising.store.MainActivityStore;

public class InterfazNoPartituras {
	
	private Context ctx;
	private GridView scores_gallery;
		
	public InterfazNoPartituras(Context context, GridView Scores_Gallery){
		this.ctx = context;
		this.scores_gallery = Scores_Gallery;
		interfazCuandoNoHayPartituras();
	}
		
	public void interfazCuandoNoHayPartituras() {
		TextView textoColeccionVacia = (TextView) ((Activity)ctx).findViewById(R.id.textoColeccionVacia);
		textoColeccionVacia.setVisibility(0);
		Button tienda = (Button) ((Activity)ctx).findViewById(R.id.tienda);
		tienda.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(new Login_Utils(ctx).isOnline()){
					Intent i = new Intent(ctx, MainActivityStore.class);
					ctx.startActivity(i);
				}else{
					Toast.makeText(ctx, R.string.connection_err, Toast.LENGTH_LONG).show();
				}				
			}	
		});
		
		tienda.setVisibility(0);
		scores_gallery = (GridView) ((MainScreenActivity)ctx).findViewById(R.id.gV_scores);
		scores_gallery.setVisibility(8);
	}
	
}