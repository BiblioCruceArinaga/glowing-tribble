package com.rising.money;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.rising.drawing.R;

public class Facebook_Publish {
	
	Context ctx;
	
	public Facebook_Publish(Context context){
		this.ctx = context;
	}
	
	public void publish(){
		 Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		   shareIntent.setType("text/plain");
		   shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ctx.getText(R.string.app_name));
		   shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, ctx.getText(R.drawable.ic_launcher));

		   PackageManager pm = ctx.getPackageManager();
		   List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
		   	for (final ResolveInfo app : activityList){
		         if ((app.activityInfo.name).contains("katana")){
		        	 
		        	 final ActivityInfo activity = app.activityInfo;
		        	 final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
		        	 shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		        	 shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		        	 shareIntent.setComponent(name);
		        	 ctx.startActivity(shareIntent);
		        	 break;
		        }
		     }
	}
	
	
}