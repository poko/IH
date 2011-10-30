package net.ecoarttech.ihplus.db;

import net.ecoarttech.ihplus.model.ScenicVista;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "IHPlus.db";
	private static final int DB_VERSION = 1;

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String hikeTable = "CREATE TABLE hikes (id integer primary key autoincrement, "
				+ "name varchar (100) not null, start_lat double not null, " + "start_long double not null, "
				+ "date TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP);";
		db.execSQL(hikeTable);
		String vistaTable = "CREATE TABLE " + ScenicVista.TABLE_NAME + " (id integer primary key autoincrement, "
				+ ScenicVista.COL_HIKE_ID + " integer not null, " + ScenicVista.COL_LAT + " double not null, "
				+ ScenicVista.COL_LNG + " double not null, " + ScenicVista.COL_ACTION_ID + " int not null, "
				+ ScenicVista.COL_NOTE + " text not null, " + ScenicVista.COL_PHOTO + " text not null, "
				+ ScenicVista.COL_DATE + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);";
		db.execSQL(vistaTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
