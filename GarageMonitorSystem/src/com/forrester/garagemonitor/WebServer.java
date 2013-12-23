package com.forrester.garagemonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class WebServer extends NanoHTTPD {
	private Context mContext;
	private Service mService;
	private static String TAG = "WebServer";
	public static final String MIME_PLAINTEXT = "text/plain",
			MIME_HTML = "text/html", MIME_JS = "application/javascript",
			MIME_CSS = "text/css", MIME_PNG = "image/png"
			,
			MIME_DEFAULT_BINARY = "application/octet-stream",
			MIME_XML = "text/xml";
	private static final Status HTTP_OK = Status.OK;

	public WebServer(Context c, Service s) {
		super(8080);
		mService = s;
		mContext = c;
	}

	public WebServer(Context c, Integer port) {
		super(port);
		mContext = c;
	}

	@Override
	public Response serve(IHTTPSession session) {
		String uri = session.getUri();
		Log.d(TAG, "SERVE ::  URI " + uri);
		Map<String, String> parms = session.getParms();
		final StringBuilder buf = new StringBuilder();

		InputStream mbuffer = null;

		try {
			if (uri != null) {

				if (uri.contains(".js")) {
					mbuffer = mContext.getAssets().open(uri.substring(1));
					return new NanoHTTPD.Response(HTTP_OK, MIME_JS, mbuffer);
				} else if (uri.contains(".css")) {
					mbuffer = mContext.getAssets().open(uri.substring(1));
					return new NanoHTTPD.Response(HTTP_OK, MIME_CSS, mbuffer);

				} else if (uri.contains(".png")) {
					mbuffer = mContext.getAssets().open(uri.substring(1));
					return new NanoHTTPD.Response(HTTP_OK, MIME_PNG, mbuffer);
				} else if (uri.contains("/mnt/sdcard")) {
					Log.d(TAG, "request for media on sdCard " + uri);
					File request = new File(uri);
					mbuffer = new FileInputStream(request);
					FileNameMap fileNameMap = URLConnection.getFileNameMap();
					String mimeType = fileNameMap.getContentTypeFor(uri);

					Response streamResponse = new Response(HTTP_OK, mimeType,
							mbuffer);
					Random rnd = new Random();
					String etag = Integer.toHexString(rnd.nextInt());
					streamResponse.addHeader("ETag", etag);
					streamResponse.addHeader("Connection", "Keep-alive");

					return streamResponse;
				} else if (uri.contains("takephoto")) {
					mService.sendBroadcast(new Intent(MainActivity.PHOTO_COMMAND_CODE));
				} else {
					mbuffer = mContext.getAssets().open("index.html");
					return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, mbuffer);
				}
			}

		} catch (IOException e) {
			Log.d(TAG, "Error opening file" + uri.substring(1));
			e.printStackTrace();
		}

		return null;

	}
}
