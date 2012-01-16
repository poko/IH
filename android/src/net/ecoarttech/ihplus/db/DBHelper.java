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
	private static final int DB_VERSION = 5;
	private static DBHelper instance;

	private static ArrayList<String> TABLE_COLUMNS = new ArrayList<String>() {
		private static final long serialVersionUID = 8795389409303129031L;
		{
			add(Hike.TABLE_NAME);
			add(ScenicVista.TABLE_NAME);
			add("vista_actions");
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
				+ "verbiage varchar(10000) not null, action_type varchar(10) not null);";
		db.execSQL(vistaActionsTable);
		db
				.execSQL("INSERT INTO `vista_actions` VALUES(1, 'There were people here before you that have left traces upon the landscape. Take a picture as evidence that their specters still walk amongst us.', 'photo');");
		db
				.execSQL("INSERT INTO `vista_actions` VALUES(2, 'If you are near a place of business, walk inside and browse the inventory. If there is no business nearby, sit down and enjoy a respite from commerce.', 'note');");
		db
				.execSQL("INSERT INTO `vista_actions` VALUES(3, 'Turn your nose to the air and sniff the wifi. Text a friend and tell him/her what the wifi smells like.', 'text');");
		db
				.execSQL("INSERT INTO `vista_actions` VALUES(4, 'Put your ear against the ground if you can and listen to the mood of the earth for at least 2 minutes. Write a field note that says: \"The earth here sounds like...\"', 'note');");
		db
				.execSQL("INSERT INTO `vista_actions` VALUES(5, 'Locate a dwelling, whether a cave, a den, an apartment, a cabin, or a nest. Take a photo of this place. and imagine who lives there and what life might be like for them.', 'photo');");
		db
				.execSQL("INSERT INTO `vista_actions` VALUES(6, 'Search for a creative plant, an obedient plant, or a badly behaving plant. Move closer, study its behavior, and take a picture.', 'photo');");
		db
				.execSQL("INSERT INTO `vista_actions` VALUES(7, 'Make friends with the nearest tree. If there is no tree then locate a mailbox or a stone. Place your hand upon your new friend and commend it for its valuable role in our networked ecological system. Take photo that best represents your interactions.', 'photo');");
		db
				.execSQL("INSERT INTO `vista_actions` VALUES(8, 'Listen: Something serious, frivolous, or meaningless is being uttered here and will quickly be forgotten. Write down the message you hear and text it to yourself.', 'text');");
		db
				.execSQL("INSERT INTO `vista_actions` VALUES(9, 'Look for symbolic markings on the rocks, buildings or trees. Compose a field note about what you think the markingsŐs location and what you think they mean.', 'note');");
		db
				.execSQL("INSERT INTO `vista_actions` VALUES(10, 'This place used to be different than it is today. Slowly turn around in a complete circle keeping your eyes on the horizon. If you notice anything strange and out of place then take a picture. If you think everything is as it should be then take a picture.', 'photo');");
	}

	public static Cursor getVistaActions(Context context, Integer size) {
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
