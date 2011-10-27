package net.ecoarttech.ihplus.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

public class Util {

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
}
