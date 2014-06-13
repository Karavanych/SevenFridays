package seven.fridays.info;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBWork {
	
	static int NOT_SELECTED=-1;
	static String[] categoryList;
	static int categoryListIndexSelect=NOT_SELECTED;
	static String[] countryList;
	static int countryListIndexSelect=NOT_SELECTED;
	
	public static boolean categorySelected() {
		if (categoryListIndexSelect==NOT_SELECTED) return false; else return true;
	}
	
	public static boolean countrySelected() {
		if (countryListIndexSelect==NOT_SELECTED) return false; else return true;
	}
	
	public static void selectCategory(int tekcategory) {
		categoryListIndexSelect=tekcategory;				
	}
	
	public static void selectCountry(int tekcountry) {
		countryListIndexSelect=tekcountry;				
	}	
	
	
	public static String getSelectedCategory() {
		if (categorySelected()) return DBWork.categoryList[DBWork.categoryListIndexSelect];
		else return "";
	}
	
	public static String getSelectedCountry() {
		if (countrySelected()) return DBWork.countryList[DBWork.countryListIndexSelect];
		else return "";
	}	
	
	public static void unselectCategory() {
		categoryListIndexSelect=NOT_SELECTED;		
	}
	
	public static void unselectCountry() {
		countryListIndexSelect=NOT_SELECTED;		
	}

	public static void updateCategory(String tekName, String tekUrl) {
		SQLiteDatabase db = MainActivity.DB_HELPER.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("url", tekUrl);
		cv.put("name", tekName);		
		
	    int updCount = db.update("category", cv, "name = ?", new String[] { tekName });
	    if (updCount==0) {
	    	db.insert("category", null, cv);		    	
	    }
	}

	public static void updateCountry(String tekName, String tekUrl,String tekcategory) {
		SQLiteDatabase db = MainActivity.DB_HELPER.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("url", tekUrl);
		cv.put("name", tekName);		
		cv.put("category",tekcategory);
		
	    int updCount = db.update("country", cv, "url = ?", new String[] { tekUrl });
	    if (updCount==0) {
	    	db.insert("country", null, cv);		    	
	    }	
	}
	
	public static void updateNomenklatura(String tekName, String tekUrl,String tekcountry,String tekcategory) {
		SQLiteDatabase db = MainActivity.DB_HELPER.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("url", tekUrl);
		cv.put("name", tekName);	
		cv.put("country",tekcountry);
		cv.put("category",tekcategory);
		
	    int updCount = db.update("nomenklatura", cv, "url = ?", new String[] { tekUrl });
	    if (updCount==0) {
	    	db.insert("nomenklatura", null, cv);		    	
	    }	
	}
	
	public static void updateNomenklaturaLongDescriptionAndImage(String tekUrl,String tekDescription,String tekImageUrl) {
		SQLiteDatabase db = MainActivity.DB_HELPER.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("url", tekUrl);
		cv.put("description", tekDescription);	
		cv.put("imageurl", tekImageUrl);
		
	    int updCount = db.update("nomenklatura", cv, "url = ?", new String[] { tekUrl });
	    if (updCount==0) {
	    	db.insert("nomenklatura", null, cv);		    	
	    }	
	}
	
	public static void updateImage(byte[] byteArray,String tekUrl) {
		SQLiteDatabase db = MainActivity.DB_HELPER.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("url", tekUrl);
	    cv.put("image", byteArray);
	    
	    int updCount = db.update("nomenklatura", cv, "url = ?", new String[] { tekUrl });
	    if (updCount==0) {
	    	db.insert("nomenklatura", null, cv);		    	
	    }	        	
	}
	
	
	// получить все данные из таблицы DB_TABLE
	public static Cursor getAllData(ContentValues loaderParams) {
		SQLiteDatabase db = MainActivity.DB_HELPER.getReadableDatabase();
		if (loaderParams != null) {
			String querySelection = "";
			String[] querySelectionArgs = new String[loaderParams.size()];
			int tekpoz=0;
			String tekSelection;
			
			if (loaderParams.containsKey("category")) {
				tekSelection = "category = ?";
				if (0==tekpoz) querySelection+=tekSelection; else querySelection+="AND "+tekSelection;								
				querySelectionArgs[tekpoz++] = loaderParams
						.getAsString("category");
			}

			if (loaderParams.containsKey("country")) {
				tekSelection = " country = ?";
				if (0==tekpoz) querySelection+=tekSelection; else querySelection+="AND "+tekSelection;
				querySelectionArgs[tekpoz++] = loaderParams.getAsString("country");
			}
			
			if (loaderParams.containsKey("url")) {
				tekSelection = "url = ?";
				if (0==tekpoz) querySelection+=tekSelection; else querySelection+="AND "+tekSelection;
				querySelectionArgs[tekpoz++] = loaderParams.getAsString("url");
			}			


			
			 return db.query("nomenklatura", null, querySelection,querySelectionArgs, null, null, null);
		}

		return db.query("nomenklatura", null, null, null, null, null, null);

	}
	
	public static Cursor getImageUrlData() {
		SQLiteDatabase db = MainActivity.DB_HELPER.getReadableDatabase();
		return db.query("nomenklatura", new String[] {"imageurl","url"}, null, null, null, null, null);		
	}
	
	public static String[] getCategoryList() {
		SQLiteDatabase db = MainActivity.DB_HELPER.getReadableDatabase();
		Cursor qr = db.query("category", new String[] { "name", "url" }, null,
				null, null, null, null);

		int nameIndex = qr.getColumnIndexOrThrow("name");

		if (qr.getCount() > 0) {
			String[] arr = new String[qr.getCount()];
			int i = 0;

			qr.moveToFirst();
			while (qr.isAfterLast() == false) {
				arr[i] = qr.getString(nameIndex);
				i++;
				qr.moveToNext();
			}

			DBWork.categoryList=arr;
		} else
			DBWork.categoryList=new String[] {""};
		return categoryList;
	}
	
	
	public static String[] getCountryList() {
		SQLiteDatabase db = MainActivity.DB_HELPER.getReadableDatabase();
		
		String uslovie=null;
		String[] uslovieargs=null;
		if (categorySelected()) {		
			uslovie="category = ?";
			uslovieargs=new String[]{getSelectedCategory()};
		} 
		Cursor qr=db.query("country", new String[] { "name", "url" }, uslovie,
				uslovieargs, null, null, null);

		int nameIndex = qr.getColumnIndexOrThrow("name");

		if (qr.getCount() > 0) {
			String[] arr = new String[qr.getCount()];
			int i = 0;

			qr.moveToFirst();
			while (qr.isAfterLast() == false) {
				arr[i] = qr.getString(nameIndex);
				i++;
				qr.moveToNext();
			}

			DBWork.countryList=arr;
		} else
			DBWork.countryList=new String[] {""};
		return countryList;
	}
	


	
}
