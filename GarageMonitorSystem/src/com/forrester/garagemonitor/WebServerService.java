package com.forrester.garagemonitor;

import com.forrester.garagemonitor.TempFilesServer.ExampleManagerFactory;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class WebServerService extends Service {

	private CustomWebServer server = null;

	@Override
	public void onCreate() {
		Log.i("HTTPSERVICE", "Creating and starting httpService");
		super.onCreate();
		new RunServerTask().execute();
	}

	private class RunServerTask extends AsyncTask<String, Void, String> {
		@Override
        protected String doInBackground(String... urls) {

            try {
            	WebServer server = new WebServer(getApplicationContext(), WebServerService.this);
            	ServerRunner.executeInstance(server);
            	// Normal debug server
//                ServerRunner.run(DebugServer.class);

            	// temp file server

//                TempFilesServer server = new TempFilesServer();
//                server.setTempFileManagerFactory(new ExampleManagerFactory());
//                ServerRunner.executeInstance(server);
            } catch (Exception e) {
              e.printStackTrace();
            }
            return "";
		}

		@Override
		protected void onPostExecute(String result) {
//			textView.setText(result);
		}
	}

	@Override
	public void onDestroy() {
		Log.i("HTTPSERVICE", "Destroying httpService");
//		server.stopServer();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}