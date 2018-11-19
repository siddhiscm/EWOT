package app.json.com.myjson.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import app.json.com.myjson.model.ProtocalData;

public class DatabaseHelper extends SQLiteOpenHelper {

	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "protocalManager";

	// Table Names

	private static final String TABLE_PROTOCAL_DETAILS = "pDetails";
	private static final String TABLE_PROTOCAL_STAGES = "pStages";

	// PDetails Table column names
	private static final String KEY_PID = "p_id";
	private static final String KEY_PROTOCAL_NAME = "p_name";
	private static final String KEY_PROTOCAL_DESCRIPTION= "p_description";
	private static final String KEY_PROTOCAL_NO_STAGES = "p_no_stages";
	private static final String KEY_PROTOCAL_SUTIABLEPERSONALITY = "p_sutiabl_personality";

	// pStages Table - column names
	private static final String KEY_SID = "s_id";
	private static final String KEY_FID = "f_id";
	private static final String KEY__STAGE_NAME = "s_name";
	private static final String KEY__STAGE_POSITIVE = "s_positive";
	private static final String KEY__STAGE_NEGATIVE = "s_negative";
	private static final String KEY__STAGE_ALTITUDE = "s_altitude";
	private static final String KEY__STAGE_METHOD = "s_method";
	private static final String KEY__STAGE_DESCRIPTION = "s_description";

	// Protocal Table Create Statements
	// Todo table create statement
	private static final String CREATE_TABLE_PROTOCAL = "CREATE TABLE "
			+ TABLE_PROTOCAL_DETAILS + "(" + KEY_PID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_PROTOCAL_NAME
			+ " TEXT," + KEY_PROTOCAL_DESCRIPTION + " TEXT," + KEY_PROTOCAL_NO_STAGES
			+ " TEXT," + KEY_PROTOCAL_SUTIABLEPERSONALITY + "TEXT" + ")";

	// Stages table create statement
	private static final String CREATE_TABLE_STAGE = "CREATE TABLE " + TABLE_PROTOCAL_STAGES
			+ "(" + KEY_SID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_FID + "INTEGER," +KEY__STAGE_NAME + " TEXT,"
			+ KEY__STAGE_POSITIVE + " TEXT," +  KEY__STAGE_NEGATIVE + "TEXT," + KEY__STAGE_ALTITUDE + "TEXT," + KEY__STAGE_METHOD
	+"TEXT,"+ KEY__STAGE_DESCRIPTION + "TEXT" + "FOREIGN KEY"  +KEY_FID+ "REFERENCES" + CREATE_TABLE_PROTOCAL +"(" + KEY_PID +"))";





	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
		db.execSQL(CREATE_TABLE_PROTOCAL);
		db.execSQL(CREATE_TABLE_STAGE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROTOCAL_DETAILS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROTOCAL_STAGES);
		// create new tables
		onCreate(db);
	}

	// ------------------------ "todos" table methods ----------------//

	/*
	 * Creating a todo
	 */
	public long createToDo(ProtocalData todo, long[] tag_ids) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_PROTOCAL_NAME, todo.getpName());
		values.put(KEY_PROTOCAL_DESCRIPTION, todo.getpDescription());
		values.put(KEY_PROTOCAL_NO_STAGES, todo.getpNoOfStages());
		values.put(KEY_PROTOCAL_SUTIABLEPERSONALITY, todo.getpSuitablePersonality());
		Log.d("SIDDHI",CREATE_TABLE_PROTOCAL);
		Log.d("siddhi",CREATE_TABLE_STAGE);

		// insert row
		//long todo_id = db.insert(TABLE_TODO, null, values);
/*

		// insert tag_ids
		for (long tag_id : tag_ids) {
			createTodoTag(todo_id, tag_id);
		}
*/

		return 0;
	}

}
