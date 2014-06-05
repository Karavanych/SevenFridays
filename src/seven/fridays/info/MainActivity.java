package seven.fridays.info;


import java.util.concurrent.TimeUnit;







import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2e014f")));
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
	    
	    //XXX создааем адаптер и настраиваем список
	    scAdapter = new MySimpleCursorAdapter(this, R.layout.mysimplelistitem, null, from, to, 0);
	}
	
	@Override
	protected void onStart() {
		
		super.onStart();
	

	    // добавляем контекстное меню к списку
	    registerForContextMenu(lvData);
	    
	    // создаем лоадер для чтения данных
	    getSupportLoaderManager().initLoader(0, null, this);	
	    getSupportLoaderManager().getLoader(0).forceLoad();
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
		Loader<Object> loader = getSupportLoaderManager().getLoader(0);
		if (loader!=null) {
			
			if (!DBWork.categorySelected()) {
				DBWork.selectCategory(number - 1);
			} else {
				if (number == 1) {
					DBWork.unselectCategory();
					DBWork.unselectCountry();
					mNavigationDrawerFragment.openNavigationDrawler();
				} else {
					DBWork.selectCountry(number - 2);
				}
			}		
			
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
		} else {
			mTitle = getString(R.string.app_name);
		}
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
            	
            	Log.d("MyLogs","clickitem");
            	View viewtextfull = view.findViewById(R.id.itemlayout);
            	
            	LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewtextfull.getLayoutParams();
            	params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            	viewtextfull.setLayoutParams(params);            	            	
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
		DB_HELPER.close();
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
	
	
	

}
