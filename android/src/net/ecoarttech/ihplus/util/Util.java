package net.ecoarttech.ihplus.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Util {
	private static final String TAG = "IH+ - Util";
	private static Typeface font;

	public static void setFont(Context c, View... views) {
		if (font == null)
			font = Typeface.createFromAsset(c.getAssets(), "fonts/calibri.ttf");
		for (View view : views) {
			((TextView) view).setTypeface(font);
		}
	}

	public static void setBoldFont(Context c, View... views) {
		if (font == null)
			font = Typeface.createFromAsset(c.getAssets(), "fonts/calibri_bold.ttf");
		for (View view : views) {
			((TextView) view).setTypeface(font);
		}
	}

	private static HashMap<String, Drawable> mImages = new HashMap<String, Drawable>();

	public static void downloadImage(final String url, final ImageView img) {
		Log.d(TAG, "downloading image: " + url);
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				img.setImageDrawable((Drawable) message.obj);
			}
		};

		if (mImages.containsKey(url)) { // already have image cached
			img.setImageDrawable(mImages.get(url));
		} else { // need to dl image
			new Thread() {
				@Override
				public void run() {
					try {
						InputStream is = (InputStream) new URL(url).getContent();
						Drawable d = Drawable.createFromStream(is, "img dl call");
						mImages.put(url, d);
						Message message = handler.obtainMessage(1, d);
						handler.sendMessage(message);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

}
