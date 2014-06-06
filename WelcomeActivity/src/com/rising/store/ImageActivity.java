package com.rising.store;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Bundle bundle=getIntent().getExtras();
		url = bundle.getString("imagen");
		iml = ImageLoader.getInstance();
		final Context ctx = this;
						
		ImageView IV_Score_Preview = (ImageView) findViewById(R.id.iV_score_preview);
		final ProgressDialog Image_PDialog;		
		
		final DisplayImageOptions options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.cover)
        .showImageForEmptyUri(R.drawable.cover)
        .showImageOnFail(R.drawable.cover)
        .cacheInMemory(true)
        .considerExifParams(true)
        .displayer(new RoundedBitmapDisplayer(10))
        .build();
               
		Image_PDialog = ProgressDialog.show(ctx, "", getString(R.string.pleasewait));
		
		iml.displayImage(url, IV_Score_Preview, options, new SimpleImageLoadingListener(){
       	 boolean cacheFound;

            @Override
            public void onLoadingStarted(String url, View view) {
                List<String> memCache = MemoryCacheUtils.findCacheKeysForImageUri(url, iml.getMemoryCache());
                cacheFound = !memCache.isEmpty();
                if (!cacheFound) {
                	Log.i("Start Cache", "Loading Cache of: " + url);
                    File discCache = DiskCacheUtils.findInCache(url, iml.getDiskCache());
                    if (discCache != null) {
                        cacheFound = discCache.exists();
                    }
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (cacheFound) {
                    MemoryCacheUtils.removeFromCache(imageUri, iml.getMemoryCache());
                    DiskCacheUtils.removeFromCache(imageUri, iml.getDiskCache());

                    iml.displayImage(imageUri, (ImageView) view, options);
                    Log.i("Complete Cache", "Loading Cache Complete");
                }
                Image_PDialog.dismiss();
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