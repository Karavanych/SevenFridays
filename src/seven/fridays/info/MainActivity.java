package seven.fridays.info;


import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks, LoaderCallbacks<Cursor> {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	
	public static DBHelper DB_HELPER;
	static MySimpleCursorAdapter scAdapter;
	static ListView lvData;
	static ContentValues loaderParams;	
	public boolean isRun=false;
	//long lastUpdate; 
	
	MyReceiver br;
	public final static String BROADCAST_ACTION = "sevenfridays.serviceresiver";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		// do fullscreen app
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		DB_HELPER = new DBHelper(this);				
		
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		
		
		//set background color for action bar
		android.app.ActionBar bar = getActionBar();
		//for color
		bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.BASEFON)));
		//for image
		//bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_launcher));		

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		
		
		
		
	  /*  // формируем столбцы сопоставления
	    String[] from = new String[] { "name","url" };
	    int[] to = new int[] { R.id.itemtext1, R.id.itemtext2 };
	    
	 // создааем адаптер и настраиваем список
	    scAdapter = new SimpleCursorAdapter(this, R.layout.mysimplelistitem, null, from, to, 0);
	    lvData = (ListView) findViewById(R.id.listViewData);
	    lvData.setAdapter(scAdapter);

	    // добавляем контекстное меню к списку
	    registerForContextMenu(lvData);
	    
	    // создаем лоадер для чтения данных
	    getSupportLoaderManager().initLoader(0, null, this);	*/    
	    
		
		// формируем столбцы сопоставления
	    String[] from = new String[] { "name","description" };
	    int[] to = new int[] { R.id.itemtext1, R.id.itemtext2 };
	    	    
	    scAdapter = new MySimpleCursorAdapter(this, R.layout.mysimplelistitem, null, from, to, 0);
	    
	    br=new MyReceiver();
	    // создаем фильтр для BroadcastReceiver
	    IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
	    // регистрируем (включаем) BroadcastReceiver
	    registerReceiver(br, intFilt);
	    
	    //readSPref();
	    
	    
	    // задем расписание для обновления
	    AlarmManager am=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
	    Intent intent = new Intent(this, AlarmReceiver.class);
	    PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
	    //After after 3 seconds
	    am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ 1000 * 3,SynchronizationSite.CHECK_SYNCHRONIZATION_TIME , pi);
	    
	}
		
	
	@Override
	protected void onStart() {
		
		super.onStart();
	/*	if (mNavigationDrawerFragment==null) {
			mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
					.findFragmentById(R.id.navigation_drawer);
			
			// Set up the drawer.
			mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
					(DrawerLayout) findViewById(R.id.drawer_layout));
			
		}*/
	
	    // добавляем контекстное меню к списку
	    registerForContextMenu(lvData);
	    
	    // создаем лоадер для чтения данных
	    getSupportLoaderManager().initLoader(0, null, this);	
	    getSupportLoaderManager().getLoader(0).forceLoad();
	    isRun=true;
	    
	   //verifeUpdate();
	}
	

	@Override
	protected void onStop() {
		
		isRun=false;
		super.onStop();
	}
	

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction() 
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
		
	}	

	public void onSectionAttached(int number) {
		
		
		// TODO !!! после пробуждения, здесь падаем, надо сохранять текущую секцию
		Loader<Object> loader = getSupportLoaderManager().getLoader(0);
		if (loader!=null) {
			
			if (!DBWork.categorySelected()) {
				DBWork.selectCategory(number - 1);
				if (mNavigationDrawerFragment!=null) mNavigationDrawerFragment.openNavigationDrawler();
			} else {
				if (number == 1) {
					DBWork.unselectCategory();
					DBWork.unselectCountry();
					if (mNavigationDrawerFragment!=null) mNavigationDrawerFragment.openNavigationDrawler();
				} else {
					DBWork.selectCountry(number - 2);
				}
			}	
			
			setDbFilters(loader);
			

		} else {
			mTitle = getString(R.string.app_name);
		}
	}
	
	public void setDbFilters(Loader<Object> loader) {
		mTitle = DBWork.getSelectedCategory()+"/"+DBWork.getSelectedCountry();								
		// передаем параметры для фильтров
		loaderParams = new ContentValues();
		if (DBWork.categorySelected()) {
			loaderParams.put("category", DBWork.getSelectedCategory());
		}
		
		if (DBWork.countrySelected()) {
			loaderParams.put("country", DBWork.getSelectedCountry());
		}
		
		loader.forceLoad();		
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
	/*		TextView textView = (TextView) rootView
					.findViewById(R.id.section_label);
			textView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));*/
			
			//set adapter for sqlbd
		    lvData = (ListView) rootView.findViewById(R.id.listViewData);		 
		    lvData.setAdapter(scAdapter);
		    lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	          
            	View viewtextfull = view.findViewById(R.id.itemlayout);
            	
            	LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewtextfull.getLayoutParams();
            	params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            	viewtextfull.setLayoutParams(params);     
            	          
            	Cursor mCurs = (Cursor) parent.getItemAtPosition(position);
            	int columnIndexUrl = mCurs.getColumnIndexOrThrow("url");
            	String url = mCurs.getString(columnIndexUrl);     	
	  	  	    Intent intent = new Intent(view.getContext(),NomenklaturaFullSize.class);
	  	  	    intent.putExtra("url",url);
	  	  	    startActivityForResult(intent,1);  
            	
            }
        });
			
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			
				((MainActivity) activity).onSectionAttached(getArguments().getInt(
						ARG_SECTION_NUMBER));
			
		}		
		
	}

	@Override
	protected void onDestroy() {
		
		super.onDestroy();	
		
		unregisterReceiver(br);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		
		return new MyCursorLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		
		scAdapter.swapCursor(cursor);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		
		
	}
	
	static class MyCursorLoader extends CursorLoader {

	   
	    
	    public MyCursorLoader(Context context) {
	      super(context);    
	    }
	    
	    @Override
	    public Cursor loadInBackground() {
	      Cursor cursor = DBWork.getAllData(loaderParams);
	      try {
	        TimeUnit.SECONDS.sleep(3);
	      } catch (InterruptedException e) {
	        e.printStackTrace();
	      }
	      return cursor;
	    }
	    
	  }
	
	
	public void forseload() {		
		getSupportLoaderManager().getLoader(0).forceLoad();		
	}		
	
	//проверку обновлений сделали через AlarmReceiver
	/*protected void readSPref() {
	    //Context con;
      //  try {
            SharedPreferences pref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
            lastUpdate=pref.getLong("lastUpdate", 0);           
	}*/
	
	
	//проверку обновлений сделали через AlarmReceiver
	/*private void verifeUpdate() {
		
		
		readSPref();
			
		if (Math.abs(System.currentTimeMillis()-lastUpdate)>SynchronizationSite.UPDATE_SYNCHRONIZATION_TIME) { // need update
			if (isDeviceOnline()) {
				Toast.makeText(this, "Автоматически запущено обновление данных.", Toast.LENGTH_LONG).show();				
				startService(new Intent(this, MyDBUpdateService.class));
			} else {
				Toast.makeText(this, "Данные устарели, доступ в интеренет отключен. Подключитесь к интернету для выполнения обновления.", Toast.LENGTH_LONG).show();				
			}
			
		}
		
	}*/
	
	
    /** Checks whether the device currently has a network connection */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
    
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt("categoryListIndexSelect",DBWork.getCategoryListIndexSelect());
		outState.putInt("countryListIndexSelect",DBWork.getCountryListIndexSelect());
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		DBWork.selectCategory(savedInstanceState.getInt("categoryListIndexSelect"));
		DBWork.selectCountry(savedInstanceState.getInt("countryListIndexSelect"));		
		
		Loader<Object> loader = getSupportLoaderManager().getLoader(0);
		if (loader!=null) {
			setDbFilters(loader);
		}
		
		//restoreActionBar();
	}
	
	
	

}
