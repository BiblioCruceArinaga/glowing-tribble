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
 
// Un BaseAdapter puede usarse para un Adapter en un listview o gridview
// hay que implementar algunos m�todos heredados de la clase Adapter,
// Porque BaseAdapter es una subclase de Adapter
// estos m�todos en este ejemplo son: getCount(), getItem(), getItemId(), getView()
public class ScoresAdapter extends BaseAdapter {
	String[] titulos;
	String[] autores; 
	MainScreenActivity MSA = new MainScreenActivity();
	private String img_path = "/.RisingScores/scores_images/";
	
	// Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Score> scores_list = null;
    private ArrayList<Score> arraylist;
 
    public ScoresAdapter(Context context, List<Score> scores_list) {
        mContext = context;
        this.scores_list = scores_list;
        //inflater = LayoutInflater.from(mContext);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
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
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.gallery_item, null);
            
            // Locate the TextViews in listview_item.xml
            holder.Title = (TextView) view.findViewById(R.id.tV_score_title);
            holder.Author = (TextView) view.findViewById(R.id.tV_score_author);
            
            // Locate the ImageView in listview_item.xml
            holder.image = (ImageView) view.findViewById(R.id.iV_score);                   
            holder.ImageLogo = (ImageView) view.findViewById(R.id.iV_logo_file);
            
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        
        // Set the results into TextViews
        holder.Title.setText(scores_list.get(position).getTitle());
        holder.Author.setText(scores_list.get(position).getAuthor());

        //holder.image.setImageBitmap(imagenFichero(scores_list.get(position).getImage()));
        //holder.image.setBackground(imagenFichero(scores_list.get(position).getImage()));
        holder.image.setBackgroundDrawable(imagenFichero(scores_list.get(position).getImage()));
                           
        if(scores_list.get(position).getFormat().equals("pdf")){
        	holder.ImageLogo.setBackgroundResource(R.drawable.pdf_logo);
        }
        
        return view;
    }
     
    public Drawable imagenFichero(String nombreImagen){
	     	
		File f = new File(Environment.getExternalStorageDirectory() + img_path + nombreImagen);
					
		//Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(f.getAbsolutePath(),o);

		//The new size we want to scale to
		final int REQUIRED_WIDTH=120;
		final int REQUIRED_HIGHT=500;
		
		//Find the correct scale value. It should be the power of 2.
		int scale=1;
		while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
			scale*=2;

		//Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize=scale;
				 	
		Bitmap myBitmap;
		if(f.exists()){
		    //myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
		    myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), o2);
		}else{
			myBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cover); 
		}
		
		Drawable drawable = new BitmapDrawable(mContext.getResources(),myBitmap);
		
	//return myBitmap;
	return drawable;
}  
    
    // Filter Class
    public void filter(String charText){
    	charText = charText.toLowerCase(Locale.getDefault());
    	
        scores_list.clear();
        String Author;
        String Title;
        //String Instrument;
        if(charText.length() == 0){
        	scores_list.addAll(arraylist);
        }else{
            for(Score ss : arraylist){
            	Author = ss.getAuthor().toLowerCase(Locale.getDefault());
            	Title = ss.getTitle().toLowerCase(Locale.getDefault());
            	//Instrument = ss.getInstrument().toLowerCase(Locale.getDefault());
                if(Author.contains(charText) || Title.contains(charText) /*|| Instrument.contains(charText)*/){
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
