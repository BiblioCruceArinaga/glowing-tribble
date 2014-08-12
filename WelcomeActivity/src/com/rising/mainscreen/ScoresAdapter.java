package com.rising.mainscreen;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rising.drawing.R;
 
//Adaptador de la vista de la biblioteca de partituras. 
public class ScoresAdapter extends BaseAdapter {
	
	//Variables
	private Context ctx;
	private ViewHolder holder;
	private LayoutInflater inflater;
	private List<Score> scores_list = null;
	private ArrayList<Score> arraylist;
	
	//Folders
	private String img_path = "/.RisingScores/scores_images/";
	       
    public ScoresAdapter(Context context, List<Score> scores_list) {
        this.ctx = context;
        this.scores_list = scores_list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
        this.arraylist = new ArrayList<Score>();
        this.arraylist.addAll(scores_list);
    }
   
    public class ViewHolder {
        TextView Author;
        TextView Title;
        ImageView image;
        ImageView ImageLogo;
    }
    
    @Override
    public int getCount() {
        return scores_list.size();
    }
 
    @Override
    public Score getItem(int position) {
        return scores_list.get(position);
    }
 
    public String getItemAuthor(int position){
    	return scores_list.get(position).getAuthor();
    }
    
    public String getItemTitle(int position){
    	return scores_list.get(position).getTitle();
    }
    
    public String getItemInstrument(int position){
    	return scores_list.get(position).getInstrument();
    }
    
    public String getItemFormat(int position){
    	return scores_list.get(position).getFormat();
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @SuppressWarnings("deprecation")
	public View getView(final int position, View view, ViewGroup parent) {
            	
        if(view == null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.gallery_item, parent, false);
            
            holder.Title = (TextView) view.findViewById(R.id.tV_score_title);
            holder.Author = (TextView) view.findViewById(R.id.tV_score_author);
            holder.image = (ImageView) view.findViewById(R.id.iV_score);                   
            holder.ImageLogo = (ImageView) view.findViewById(R.id.iV_logo_file);
            
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        
        holder.Title.setText(scores_list.get(position).getTitle());
        holder.Author.setText(scores_list.get(position).getAuthor());

        holder.image.setBackgroundDrawable(imagenFichero(scores_list.get(position).getImage()));
                           
        if(scores_list.get(position).getFormat().equals("pdf")){
        	holder.ImageLogo.setBackgroundResource(R.drawable.pdf_logo);
        }
        
        return view;
    }
     
    public Drawable imagenFichero(String nombreImagen){
	     	
		File f = new File(Environment.getExternalStorageDirectory() + img_path + nombreImagen);
				
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(f.getAbsolutePath(),o);

		final int REQUIRED_WIDTH=120;
		final int REQUIRED_HIGHT=500;
		
		int scale=1;
		while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
			scale*=2;

		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize=scale;
				 	
		Bitmap myBitmap;
		if(f.exists()){
		    myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), o2);
		}else{
			myBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.cover); 
		}
		
		Drawable drawable = new BitmapDrawable(ctx.getResources(),myBitmap);
		
	return drawable;
}  
    
    public void filter(String charText){
    	charText = charText.toLowerCase(Locale.getDefault());
    	
        scores_list.clear();
        String Author;
        String Title;
        String Instrument;
        String Format;
        if(charText.length() == 0){
        	scores_list.addAll(arraylist);
        }else{
            for(Score ss : arraylist){
            	Author = ss.getAuthor().toLowerCase(Locale.getDefault());
            	Title = ss.getTitle().toLowerCase(Locale.getDefault());
            	Instrument = ss.getInstrument().toLowerCase(Locale.getDefault());
            	Format = ss.getFormat().toLowerCase(Locale.getDefault());
                if(Author.contains(charText) || Title.contains(charText) || Instrument.contains(charText) || Format.contains(charText)){
                	scores_list.add(ss);
                }
            }
        }
        
        notifyDataSetChanged();
    }
    
    public void removeAllSelected(List<Score> elementosAEliminar){
    	scores_list.removeAll(elementosAEliminar);
    	arraylist.clear();
    	arraylist.addAll(scores_list);
    	notifyDataSetChanged();
    }
    
    public void showAll() {
    	scores_list.clear();
    	for(Score ss : arraylist){
    		scores_list.add(ss);
        }
    	notifyDataSetChanged();
    }
    
    public void sortByName() {
    	Collections.sort(scores_list);
    	notifyDataSetChanged();
    }
}