package mcm.accelerometer.gyroscope;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import mcm.client.rest.HttpManager;
import mcm.client.rest.Utilities;

/*public class TouchGyroscopeActivity extends Activity {
    /** Called when the activity is first created. */
   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}*/

public class TouchGyroscopeActivity extends Activity implements AccelerometerListener, android.view.View.OnClickListener {

  private DrawView dv;
  
  private TextView tv_acc;
  
  private StringBuilder localStringBuilder; 
  
  private HttpManager httpManager = HttpManager.getInstance();
  
  private AccelerometerDatabase DbMotion;
  private double px, py, pz;
  private double gx, gy, gz;
  
  private boolean finishThread = false;
  
  private String actualUrl;
  
  private Button favorites;
  
  private static Context CONTEXT;
  private WebView contenedor;
  private SensorEventListener lis = new SensorEventListener()
  
  {
    public void onAccuracyChanged(Sensor paramSensor, int paramInt)
    {
    }

    public void onSensorChanged(SensorEvent paramSensorEvent)
    {
      localStringBuilder = new StringBuilder();
      for (int i = 0; ; i++)
      {
        if (i >= paramSensorEvent.values.length)
        {
          ((TextView)TouchGyroscopeActivity.this.findViewById(R.id.textView1)).setText(localStringBuilder.toString());
          if (TouchGyroscopeActivity.this.dv != null)
          {
        	float f1 = paramSensorEvent.values[2];
            float f2 = paramSensorEvent.values[1];
            TouchGyroscopeActivity.this.dv.moveX(-5.0F * f2);
            float f3 = paramSensorEvent.values[0];
            TouchGyroscopeActivity.this.dv.moveY(-7.0F * f3);
            TouchGyroscopeActivity.this.dv.invalidate();
            gx = f2;
        	gy = f3;
        	gz = f1;
          }
          return;
        }
        if (i > 0)
          localStringBuilder.append("\n");
        float[] arrayOfFloat = paramSensorEvent.values;
        float f1 = (int)(24.0F * paramSensorEvent.values[i]) / 24.0F;
        arrayOfFloat[i] = f1;
        localStringBuilder.append(f1);
      }
    }
  };

  private void register()
  {
    SensorManager localSensorManager = (SensorManager)getSystemService("sensor");
    localSensorManager.registerListener(this.lis, localSensorManager.getDefaultSensor(4), 1);
    Log.d("GyroTest", "--SENSORS--");
    Iterator localIterator = localSensorManager.getSensorList(-1).iterator();
    while (true)
    {
      if (!localIterator.hasNext())
        return;
      Log.d("GyroTest", ((Sensor)localIterator.next()).getName());
    }
  }

  private void unregister()
  {
    ((SensorManager)getSystemService("sensor")).unregisterListener(this.lis);
  }

  @Override
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.main);
    
    CONTEXT = this;
    DbMotion = new AccelerometerDatabase(this);
    
    initializacionWebView();
    FrameLayout localFrameLayout = (FrameLayout)findViewById(R.id.frameLayout1);
    this.dv = new DrawView(this);
    localFrameLayout.addView(this.dv);
    
    collectInformation(); //collect gyroscope and accelerometer data
    
    tv_acc = (TextView)findViewById(R.id.textView4);
    
    favorites = (Button) findViewById(R.id.button1);
    favorites.setOnClickListener(this);
    
    contenedor.setWebViewClient(new WebViewClient() {
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			
			// appendToFile(url);
			actualUrl = url;
			view.loadUrl("http://www.google.com");

			return true;
		}
	});
  }

  protected void onDestroy()
  {
    super.onDestroy();
    if (AccelerometerManager.isListening()) {
        AccelerometerManager.stopListening();
    }
    finishThread = true;
  }

  protected void onPause()
  {
    super.onPause();
    unregister();
    finishThread = true;
	finish();
  }

  protected void onResume()
  {
    super.onResume();
    register();
    if (AccelerometerManager.isSupported()) {
        AccelerometerManager.startListening(this);
    }
  }

  protected void onStart()
  {
    super.onStart();
  }
  
  public static Context getContext() {
      return CONTEXT;
  }

public void onAccelerationChanged(float x, float y, float z) {
	// TODO Auto-generated method stub	
	tv_acc.setText("X :"+ String.valueOf(x) +"\n"+
			   "Y :"+ String.valueOf(y) +"\n"+
			   "Z :"+ String.valueOf(z));
	px = x;
	py = y;
	pz = z;
	
}

public void onShake(float force) {
	// TODO Auto-generated method stub
	//Toast.makeText(this, "Phone shaked : " + force, 1000).show();
	
}

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	if ((keyCode == KeyEvent.KEYCODE_BACK) && contenedor.canGoBack()) {
		contenedor.goBack();
		return true;
	}
	return super.onKeyDown(keyCode, event);
}
  
  public void initializacionWebView() {
		// Initialization of the WebView
		contenedor = (WebView) findViewById(R.id.webView1); 
		contenedor.getSettings().setJavaScriptEnabled(true);
		contenedor.getSettings()
				.setJavaScriptCanOpenWindowsAutomatically(false);
		contenedor.getSettings().setPluginsEnabled(false);
		contenedor.getSettings().setSupportMultipleWindows(false);
		contenedor.getSettings().setSupportZoom(false);
		contenedor.getSettings().setJavaScriptEnabled(true);
		contenedor.setVerticalScrollBarEnabled(true);
		contenedor.setHorizontalScrollBarEnabled(true);
		contenedor.loadUrl("http://digitalquimia.blogspot.com/");
		actualUrl="http://digitalquimia.blogspot.com/";

		contenedor.canGoBack();
		

	}
  
  public void collectInformation() {
		Thread t = new Thread() {
			public void run() {

				while (!finishThread) {

					messageHandler.sendMessage(Message
							.obtain(messageHandler, 1));
					// one second input
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		t.start();
	}

	Handler messageHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				DbMotion.open();
				Calendar calendar = Calendar.getInstance();
				// calendar.getTime().toString()
				// DbMotion.createAccelerometerEntry(calendar.getTimeInMillis(),actualUrl,px,
				// py, pz);
				//DbMotion.createAccelerometerEntry(calendar.getTime().getSeconds(), actualUrl, px / 10, py / 10, pz / 10);
				DbMotion.createAccelerometerEntry(calendar.getTime().getSeconds(), actualUrl, px, py, pz,gx,gy,gz);

				DbMotion.close();
				break;
			}
		}

	};

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.button1:
			
			        //Upload to the cloud and Processing invokation
					cloneDatabase base = new cloneDatabase();
					try {
						if (base.fileToCopy()){
							base.copyDataBase();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				  
					File file = new File(base.getDataBasePath());
					try {
						httpManager.UploadFile(file,Utilities.AMAZON_S3);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					/*ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
					parameters.add(new BasicNameValuePair(Utilities.SECRETKEY_PARAM, Utilities.secretKey));
					parameters.add(new BasicNameValuePair(Utilities.IDKEY_PARAM,Utilities.idKey));	
					parameters.add(new BasicNameValuePair(Utilities.IMAGE_PARAM, Utilities.image));
					
					try {
						httpManager.postRequest(Utilities.client, parameters);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					
					break;
		}
		
	}

}

