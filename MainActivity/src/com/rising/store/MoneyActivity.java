package com.rising.store;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.rising.drawing.R;
import com.rising.login.Configuration;

public class MoneyActivity extends Activity{

	Configuration conf = new Configuration(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.money_layout);
								
    	ActionBar ABar = getActionBar();
    	
    	ABar.setTitle(R.string.money);
    	ABar.setDisplayHomeAsUpEnabled(true); 
    	
    	TextView current_money = (TextView) findViewById(R.id.tVcurrent_money);
    	TextView free_money = (TextView) findViewById(R.id.tVfree_money);
    	TextView money_unit = (TextView) findViewById(R.id.tv_money_unit);
    	
    	current_money.setText(R.string.current_money);
    	money_unit.setText(conf.getUserMoney() + "€");
    	
    	free_money.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MoneyActivity.this, FreeMoneyActivity.class);
				startActivity(i);
			}
    		
    	});
    	
    	
    	
	}
}
