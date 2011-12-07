package net.ecoarttech.ihplus.db;

import java.util.ArrayList;

import net.ecoarttech.ihplus.model.Hike;
import net.ecoarttech.ihplus.model.ScenicVista;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "IHPlus.db";
	private static final int DB_VERSION = 3;
	private static DBHelper instance;

	private static ArrayList<String> TABLE_COLUMNS = new ArrayList<String>() {
		private static final long serialVersionUID = 8795389409303129031L;
		{
			add(Hike.TABLE_NAME);
			add(ScenicVista.TABLE_NAME);
		}
	};

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	public static DBHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DBHelper(context);
		}
		return instance;
	}

	public static SQLiteDatabase getDB(Context context) {
		return getInstance(context).getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String hikeTable = "CREATE TABLE " + Hike.TABLE_NAME + " (id integer primary key autoincrement, "
				+ "name varchar (100) not null, start_lat double not null, " + "start_long double not null, "
				+ "date TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP);";
		db.execSQL(hikeTable);
		String vistaTable = "CREATE TABLE " + ScenicVista.TABLE_NAME + " (id integer primary key autoincrement, "
				+ ScenicVista.COL_HIKE_ID + " long not null, " + ScenicVista.COL_LAT + " double not null, "
				+ ScenicVista.COL_LNG + " double not null, " + ScenicVista.COL_ACTION_ID + " int not null, "
				+ ScenicVista.COL_NOTE + " text, " + ScenicVista.COL_PHOTO + " text, " + ScenicVista.COL_DATE
				+ " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);";
		db.execSQL(vistaTable);
		String vistaActionsTable = "CREATE TABLE vista_actions (action_id integer primary key autoincrement, "
				+ "verbiage varchar (10000) not null, action_type varchar(10) not null);";
		db.execSQL(vistaActionsTable);
		//TODO - CP - get a list of actions to have on the device
		db.execSQL("INSERT INTO `vista_actions` VALUES(1, 'Take a picture of a cat', 'note');");
		db.execSQL("INSERT INTO `vista_actions` VALUES(2, 'make note of the grass', 'note');");
		db.execSQL("INSERT INTO `vista_actions` VALUES(3, 'Find a penny. Take a picture of it.', 'note');");
		db.execSQL("INSERT INTO `vista_actions` VALUES(4, 'What color is the sky?', 'note');");
		db.execSQL("INSERT INTO `vista_actions` VALUES(5, 'hug a tree', 'note');");
		db.execSQL("INSERT INTO `vista_actions` VALUES(6, 'find a leaf', 'note');");
		db.execSQL("INSERT INTO `vista_actions` VALUES(7, 'jump up and down', 'note');");
		db.execSQL("INSERT INTO `vista_actions` VALUES(8, 'take a nap', 'note');");
		db.execSQL("INSERT INTO `vista_actions` VALUES(9, 'run in place', 'note');");
		db.execSQL("INSERT INTO `vista_actions` VALUES(10, 'note how you are feeling', 'note');");
	}
	
	public static Cursor getVistaActions(Context context, Integer size){
		return getDB(context).query("vista_actions", null, null, null, null, null, "random()", size.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// drop tables
		for (String TABLE_NAME : TABLE_COLUMNS) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		}
		onCreate(db);
	}

}
