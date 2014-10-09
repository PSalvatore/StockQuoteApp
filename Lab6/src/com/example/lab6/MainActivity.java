package com.example.lab6;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private EditText companyEt;
	private EditText nameEt;
	private EditText priceEt;
	private Button quoteBt;
	public String stringJSON;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		companyEt = (EditText) findViewById(R.id.companyName);
		nameEt = (EditText) findViewById(R.id.putName);
		priceEt = (EditText) findViewById(R.id.putPrice);
		quoteBt = (Button) findViewById(R.id.getQuote);
		
		quoteBt.setOnClickListener(buttonListener);
	}
	
	private OnClickListener buttonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try{
			DownloadJSONInfo dl = new DownloadJSONInfo();
			String urlTxt = "http://finance.yahoo.com/webservice/v1/symbols/" 
								+ (companyEt.getText().toString()) + "/quote?format=json";
			URL url = new URL(urlTxt);
			dl.execute(url);
			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
			}
			
		}
	};
	
	
	 private class DownloadJSONInfo extends AsyncTask<URL, Void, String>{
	    	private static final int URL_CONTENT = 0;

			@Override
	    	protected String doInBackground(URL... url) {
	    		String urlContent = "";
	    		StringBuilder contentBuilder = new StringBuilder();
	    		try {
	    			URLConnection yc = (url[0]).openConnection();
	    			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
	    			
	    			String str;
	    			while ((str = in.readLine()) != null){
	    				contentBuilder.append(str);
	    				if(str != null){
	    					contentBuilder.append("\n");
	    				} 
	    			}
	    			in.close();
	    			
	    		} catch (Exception e) {
	    			Log.e("Error: ", e.getMessage());
	    		}
	    		urlContent = contentBuilder.toString();
	    		System.out.println("Url Content:\n" + urlContent);
	    		return urlContent;
	    	}
	    	
	    	protected void onPostExecute(String result){
	    		Message msg = messageHandler.obtainMessage(URL_CONTENT, result);
	    		messageHandler.sendMessage(msg);
	    	}

	    }
	 
	 @SuppressLint("HandlerLeak")
		public Handler messageHandler = new Handler() {
	        public void handleMessage(Message msg) {
	        	stringJSON = (String) msg.obj;
	        	try {   
	        		JSONObject rootObj = new JSONObject(stringJSON);
	        		JSONObject listObj = rootObj.getJSONObject("list");
	        		JSONArray resourcesArr = listObj.getJSONArray("resources");
	        		JSONObject indexZero = resourcesArr.getJSONObject(0);
	        		JSONObject resObj = indexZero.getJSONObject("resource");
	        		JSONObject fieldsObj = resObj.getJSONObject("fields");
	        		
	        		nameEt.setText(fieldsObj.getString("name")); 
	        		priceEt.setText(fieldsObj.getString("price"));
	        		
				
				} catch (JSONException e) {
					e.printStackTrace();
				}
	        	
	        }
	    };

}
