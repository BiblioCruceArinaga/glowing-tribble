package com.rising.money;

import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Purchase;
import com.rising.drawing.R;
import com.rising.login.Configuration;
import com.rising.money.BuyMoneyNetworkConnection.OnBuyMoney;
import com.rising.money.BuyMoneyNetworkConnection.OnFailBuyMoney;
import com.rising.store.MainActivityStore;

public class MoneyActivity extends Activity implements IabHelper.OnIabSetupFinishedListener, IabHelper.OnIabPurchaseFinishedListener, IabHelper.OnConsumeFinishedListener{

	Configuration conf = new Configuration(this);
	private IabHelper billingHelper;
	private String ID;
	private String Money; 
	Context ctx = this;
	
	//Pay_Methods: 1-Google, 2-Paypal
	private String PayMethod;
	BuyMoneyNetworkConnection bmnc;
		
	private OnBuyMoney successbuy = new OnBuyMoney(){

		@Override
		public void onBuyMoney() {
			Toast.makeText(ctx, R.string.buy_ok, Toast.LENGTH_LONG).show();
		}		
	};
	
	private OnFailBuyMoney failbuy = new OnFailBuyMoney(){

		@Override
		public void onFailBuyMoney() {
			Toast.makeText(ctx, R.string.buy_fail2, Toast.LENGTH_LONG).show();
		}		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.money_layout);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	ActionBar ABar = getActionBar();
    	
    	ABar.setTitle(R.string.money);
    	ABar.setDisplayHomeAsUpEnabled(true); 
    	bmnc = new BuyMoneyNetworkConnection(successbuy, failbuy, this);
    	
    	TextView current_money = (TextView) findViewById(R.id.tVcurrent_money);
    	TextView free_money = (TextView) findViewById(R.id.tVfree_money);
    	TextView money_unit = (TextView) findViewById(R.id.tv_money_unit);
    	
    	Button B5 = (Button) findViewById(R.id.b5);
    	Button B10 = (Button) findViewById(R.id.b10);
    	Button B20 = (Button) findViewById(R.id.b20);
    	Button B50 = (Button) findViewById(R.id.bu50);
    	Button B100 = (Button) findViewById(R.id.b100);
    	
    	current_money.setText(R.string.current_money);
    	money_unit.setText(conf.getUserMoney()+"");
    	money_unit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.money_ico, 0);
    	
    	free_money.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MoneyActivity.this, FreeMoneyActivity.class);
				startActivity(i);
				finish();
			}
    		
    	});
    	
    	B5.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ID ="a_5";
				Money = "5";
				startBuyProcess();
			}
    		
    	});
    	
    	B10.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ID = "a_10";
				Money = "10";
				startBuyProcess();
			}
    		
    	});

    	B20.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ID = "a_20";
				Money = "20";
				startBuyProcess();
			}
    		
    	});

    	B50.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ID = "a_50";
				Money = "50";
				startBuyProcess();
			}
    		
    	});

    	B100.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ID = "a_100";
				Money = "100";
				startBuyProcess();
			}
    		
    	});
    		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    		        
	    	case android.R.id.home:
	    		Intent in = new Intent(this, MainActivityStore.class);
	    		startActivity(in);
	    		finish();
	    		
	    	default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	String clave;
	String payload = "";
	
	private void startBuyProcess(){  
	    clave = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArb6Sq+3KAPUC888MCCLvJas0AmKYmsOSpP7zDP9WXME9bIdFXPR91kcfisVRu3o0K4LFYRsw9NHeVSC24y6AKlFE9prAn6OuMpE+Z6NUQ27ggygAhxUhf3a9fN7tXooamdQ+IzO5WXRUeaOQPXcK9vlyYV/BCZKmgxuVH+nSW2IBMrM9ijeg6Iy0I5odRdmrv9sadb9HeZtKpx9hg/aHv5XkTrZekAPnT4RGgA6hP7ymYDq1OzHYWc8EOWBckE756VhHpxvwhEZ+S0UuI0oX4v5Uc/8N6AUf5BoM11m6tPF8Ee0xAvLPmgTtQULN3lwgNfRrBDAEsAbMVoHYbtxsRwIDAQAB";
	    
	    //Google recomienda partir la clave y unirla solo cuando se vaya a usar para añadir seguridad a la compra
	 
	    billingHelper = new IabHelper(this, clave);
	    billingHelper.startSetup(this);
	    Log.i("Clave-ID1", "La clave: " + clave + ", Id: " + ID);
	    PayMethod = "1";
	}
		
	//Se realiza la compra
	@Override
	public void onIabSetupFinished(IabResult result) {
		if (result.isSuccess()) {
			Log.i("Clave-ID2", "La clave: " + clave + "Id: " + ID);
			
			billingHelper.launchPurchaseFlow(this, ID, 101, this, payload);
		} else {
			errorAlIniciar();
		}
	}
	
	public void errorAlIniciar(){
		Toast.makeText(getApplicationContext(), R.string.google_init_error, Toast.LENGTH_LONG).show();
	}
	
	//Una vez finalizada la compra
	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase info) {
		if (result.isFailure()) {
		    compraFallida();
            return;
		} else if (ID.equals(info.getSku())) {
			compraCorrecta(result, info);
			Log.i("Clave-ID3", "La clave: " + clave + "Id: " + ID);
		}		
	}
	
	public void compraFallida(){
		Toast.makeText(getApplicationContext(), R.string.buy_fail, Toast.LENGTH_LONG).show();
	}
	
	protected void compraCorrecta(IabResult result, Purchase info){
		 
		// Consumimos los elementos a fin de poder probar varias compras
		billingHelper.consumeAsync(info, this);
	}
	
	@Override
	public void onConsumeFinished(Purchase purchase, IabResult result) {
        Log.d("TAG", "Consumption finished. Purchase: " + purchase + ", result: " + result);

        if (billingHelper == null) return;

        if (result.isSuccess()) {
        	        	
        	bmnc.execute(PayMethod, Money, Locale.getDefault().getDisplayLanguage());
        }
        else {
            Toast.makeText(this, R.string.buy_fail, Toast.LENGTH_LONG).show();
        }
    }
	
    @Override
    protected void onDestroy() {
        disposeBillingHelper();
        super.onDestroy();
    }
 
    private void disposeBillingHelper() {
        if (billingHelper != null) {
            billingHelper.dispose();
        }
        billingHelper = null;
    }

}
