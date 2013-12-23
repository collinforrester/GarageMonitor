package com.forrester.garagemonitor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import java.io.InputStream;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MainActivity extends Activity {
	private String TAG = "MainActivity";

    Camera camera;
    Preview preview;
    Button buttonClick;

	private static final int REQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Context c = getApplicationContext();
		preview = new Preview(this);
        ((FrameLayout) findViewById(R.id.preview)).addView(preview);

        buttonClick = (Button) findViewById(R.id.button1);
        buttonClick.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                    preview.camera.takePicture(shutterCallback, rawCallback,
                                jpegCallback);
              }
        });
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);

		Log.d(TAG, "IP Address: " + getLocalIpAddress() + " OR " + ip);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					// Intent serviceIntent = new Intent();
					// serviceIntent.setAction("com.forrester.garagemonitor.WebServerService");
					// startService(serviceIntent);
					Log.d(TAG, "Start service command sent.");

					WebServer server = new WebServer(MainActivity.this);
					server.startServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		thread.start();
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
              Log.d(TAG, "onShutter'd");
        }
  };

  /** Handles data for raw picture */
  PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
              Log.d(TAG, "onPictureTaken - raw");
        }
  };

  /** Handles data for jpeg picture */
  PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
              FileOutputStream outStream = null;
              try {
                   
                    outStream = new FileOutputStream(String.format(
                                "/sdcard/downloads/%d.jpg", System.currentTimeMillis()));
                    outStream.write(data);
                    outStream.close();
                    Log.d(TAG, "I JUST SAVED A MOTHOFUCKING PHTO");
                   
              } catch (FileNotFoundException e) {
                    e.printStackTrace();
              } catch (IOException e) {
                    e.printStackTrace();
              }
       
        }
  };

	public String intToIp(int i) {

		return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
				+ ((i >> 8) & 0xFF) + "." + (i & 0xFF);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					// Log.d(TAG, "inetAddress : " +
					// inetAddress.getHostAddress().toString());
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(TAG, ex.toString());
		}
		return "";
	}

	class WebServerTask extends AsyncTask<String, Void, String> {

		private Exception exception;

		protected String doInBackground(String... urls) {
			try {

			} catch (Exception e) {
				this.exception = e;
			}
			return "";
		}

		protected void onPostExecute(String feed) {
			// TODO: check this.exception
			// TODO: do something with the feed
		}
	}

}
