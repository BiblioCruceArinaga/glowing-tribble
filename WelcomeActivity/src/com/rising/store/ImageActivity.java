package com.rising.store;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.rising.drawing.R;

public class ImageActivity extends Activity{
	
	static ImageLoader iml;
	private String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.imageactivity_layout);	
		Bundle bundle=getIntent().getExtras();
		url = bundle.getString("imagen");
		iml = ImageLoader.getInstance();
						
		ImageView IV_Score_Preview = (ImageView) findViewById(R.id.iV_score_preview);
				
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.cover)
        .showImageForEmptyUri(R.drawable.cover)
        .showImageOnFail(R.drawable.cover)
        .cacheInMemory(true)
        .considerExifParams(true)
        .displayer(new RoundedBitmapDisplayer(10))
        .build();
               
		//iml.displayImage(url, IV_Score_Preview, options);
		iml.displayImage(url, IV_Score_Preview, options, new SimpleImageLoadingListener(){
       	 boolean cacheFound;

            @Override
            public void onLoadingStarted(String url, View view) {
                List<String> memCache = MemoryCacheUtils.findCacheKeysForImageUri(url, ImageLoader.getInstance().getMemoryCache());
                cacheFound = !memCache.isEmpty();
                if (!cacheFound) {
                    File discCache = DiskCacheUtils.findInCache(url, ImageLoader.getInstance().getDiskCache());
                    if (discCache != null) {
                        cacheFound = discCache.exists();
                    }
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (cacheFound) {
                    MemoryCacheUtils.removeFromCache(imageUri, ImageLoader.getInstance().getMemoryCache());
                    DiskCacheUtils.removeFromCache(imageUri, ImageLoader.getInstance().getDiskCache());

                    ImageLoader.getInstance().displayImage(imageUri, (ImageView) view);
                }
            }
       });
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		 switch (item.getItemId()) {
	    	case android.R.id.home:
	    		finish();	    	    
	    	    return true; 
	    	    
	    	default:
	    		return super.onOptionsItemSelected(item);
		 }
	}
	
}