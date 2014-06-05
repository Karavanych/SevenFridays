package seven.fridays.info;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "sfiDB", null, 1);
      }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	      // создаем таблицу с полями
	      db.execSQL("create table category ("
	          + "_id integer primary key autoincrement," 
	          + "url varchar,"
	          + "name varchar" + ");");	
	      
	      db.execSQL("create table country ("
		          + "_id integer primary key autoincrement,"
		          + "category varchar,"
		          + "url varchar,"
		          + "name varchar" + ");");	
	      
	      db.execSQL("create table nomenklatura ("
		          + "_id integer primary key autoincrement,"
		          + "image BLOB,"
		          + "category varchar,"
		          + "country varchar,"
		          + "imageurl varchar,"
		          + "url varchar,"
		          + "description varchar,"
		          + "name varchar" + ");");	

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}	

}
