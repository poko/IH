package net.ecoarttech.ihplus.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

public class PhotoProvider extends ContentProvider {

	private static final String TAG = "IH+ - PhotoProvider";
	// This must be the same value as the android:authorities attribute in the manifest.
	public static final String AUTHORITY = "net.ecoarttech.ihplus";

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.d(TAG, "delete " + uri);
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		Log.d(TAG, "getType: " + uri);
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d(TAG, "insert: " + uri);
		return uri.buildUpon().appendEncodedPath(values.getAsString(MediaStore.Images.Media.TITLE)).build();
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Log.d(TAG, "query: " + uri);
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		Log.d(TAG, "update: " + uri);
		return 0;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		Log.d(TAG, "open file:" + uri);
		String path = uri.getEncodedPath();
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), path);
		try {
			if (!file.exists())
				file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ParcelFileDescriptor parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
		Log.d(TAG, "Parcel: " + parcel);
		return parcel;
	}

}
