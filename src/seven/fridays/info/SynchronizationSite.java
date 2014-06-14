package seven.fridays.info;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.htmlcleaner.TagNode;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;


public class SynchronizationSite {
	Activity mActivity;
	MyDBUpdateService mService;
	//Диалог ожидания
	 //private ProgressDialog pd;
	 protected WakeLock wakeLock;
	 static protected boolean TEST_LOAD=false;
	 
	// 86400000 - 1 день в мс, 172800000 - 2 дня 	
	 public static long UPDATE_SYNCHRONIZATION_TIME=172800000l;
	 public static long CHECK_SYNCHRONIZATION_TIME=3600000l; // 1 час
	 
	 static String NO_IMAGE_URL="http://37.98.243.100:88/img/no-image-big.png";
	 
	 protected float loadProgress=0;
	
	public SynchronizationSite(Activity cc) {
		mActivity=cc;
		mService=null;
	}
	
	public SynchronizationSite(MyDBUpdateService myDBUpdateService) {
		// TODO Auto-generated constructor stub
		mActivity=null;
		mService=myDBUpdateService;
	}

	@SuppressWarnings("deprecation")
	public void doParseSite(String[] strings) {
		//pd = ProgressDialog.show(mActivity, "Working...", "request to server", true, false);		
		//запрещаем засыпать во время синхронизации		  
		if (mActivity!=null) {
		    PowerManager pm = (PowerManager) mActivity.getSystemService(Context.POWER_SERVICE);
		    //нет нормальной альтернативы пока
			wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Synchronization 7Fridays");
		    wakeLock.acquire();
		}
	    
	      //Запускаем парсинг
	      new ParseCategory().execute(strings[0],strings[1]);			
	}
	  
/*	  private class ParseSite extends AsyncTask<String, Void, List<String>> {
	    //Фоновая операция
	    protected List<String> doInBackground(String... arg) {
	      List<String> output = new ArrayList<String>();
	      try
	      {
	        HtmlHelper hh = new HtmlHelper(new URL(arg[0]));
	        List<TagNode> links = hh.getLinksByClass("item");

	        for (Iterator<TagNode> iterator = links.iterator(); iterator.hasNext();)
	        {
	          TagNode divElement = (TagNode) iterator.next();
	          output.add(divElement.getText().toString());
	          output.add(divElement.getAttributeByName("href").toString());
	        }
	      }
	      catch(Exception e)
	      {
	        e.printStackTrace();
	      }
	      return output;
	    }

	    //Событие по окончанию парсинга	    
	    protected void onPostExecute(List<String> output) {
	      //Убираем диалог загрузки
	      pd.dismiss();
	      //Находим ListView
	      
	      ListView listview = (ListView) mActivity.findViewById(R.id.listViewData);
	      //Загружаем в него результат работы doInBackground
	      listview.setAdapter(new ArrayAdapter<String>(mActivity,
	          R.layout.mysimplelistitem , output));	      	     
	    }
	  }*/
	
	  private class ParseCategory extends AsyncTask<String, Void, List<String>> {
		    //Фоновая операция
		    protected List<String> doInBackground(String... arg) {
		    			  //TODO remove this, after debbug
		    //android.os.Debug.waitForDebugger();
		    	
		    
		     //  ..screen will stay on during this section..
		    	
		    	loadProgress=1;sendProgress(loadProgress);
		    	   
		      List<String[]> category = new ArrayList<String[]>();
		      try
		      {
		    	//XXX парсим основную страницу
		        HtmlHelper hh = new HtmlHelper(new URL(arg[0]+arg[1]));
		        List<TagNode> links = hh.getLinksByClass("item");

		        for (Iterator<TagNode> iterator = links.iterator(); iterator.hasNext();)
		        {
		          TagNode divElement = (TagNode) iterator.next();
		          category.add(new String[] { divElement.getText().toString(),divElement.getAttributeByName("href").toString()});
		        /*  output.add(divElement.getText().toString());
		          output.add(divElement.getAttributeByName("href").toString());*/
		          DBWork.updateCategory(divElement.getText().toString(), divElement.getAttributeByName("href").toString());
		          if (true==TEST_LOAD) break;
		        }
		        
		        loadProgress=2;sendProgress(loadProgress);
		        
		        links.clear();// ссылки запомнены? можно их очистить, дальше опять будем использовать эту переменную
		        
		        List<String[]> country = new ArrayList<String[]>();
		        
		        // парсим вложенные страницы, должны быть страны
		        for(String[] tekCategory: category)	{
		        	//category - [0]категория,[1] url
		        	
		        	//XXX парсим страницы стран		        	
		        	// arg[0] - адрес сайта, tekCategory[1] - href - страницы категории
		        	String urlstring=arg[0]+tekCategory[1];
			        hh = new HtmlHelper(new URL(urlstring));
			        links = hh.getLinksByClass("item");
			        
			        for (Iterator<TagNode> iterator = links.iterator(); iterator.hasNext();)
			        {
			        	TagNode divElement = (TagNode) iterator.next();
			        	//country - страна,url,категория
			        	country.add(new String[] {divElement.getText().toString(),divElement.getAttributeByName("href").toString(),tekCategory[0]});		        				     
			        	DBWork.updateCountry(divElement.getText().toString(), divElement.getAttributeByName("href").toString(),tekCategory[0]);
			        	if (true==TEST_LOAD) break;
			        	
			        }	
			        links.clear();// ссылки запомнены? можно их очистить, дальше опять будем использовать эту переменную
		        }
		        loadProgress=5;sendProgress(loadProgress);
		        //удаляем ссылки на категории, они больше не нужны
		        category.clear();
		        category=null;
		        //
		        
		        
		        List<String[]> nomenklatura = new ArrayList<String[]>();
		        		       
		        //XXX парсим вложенные страницы, должны быть сами элементы номенклатуры, пока свернутые
		        float progressIncrement=30f/country.size(); // 30% загрузки
		        for(String[] tekCountry: country)	{
		        	//country - страна,url,категория
		        	String urlstring=arg[0]+tekCountry[1];
		        	
			        hh = new HtmlHelper(new URL(urlstring));
			        links = hh.getLinksByClass("item");
			        
			        for (Iterator<TagNode> iterator = links.iterator(); iterator.hasNext();)
			        {
			        	TagNode divElement = (TagNode) iterator.next();	
			        	/*List<TagNode> childList = divElement.getChildTagList();
			        	if (!childList.isEmpty()) {
			        		String aa = childList.get(0).getAttributeByName("src").toString();
			        				        		
			        	}*/
			        	
			        	//elements - name, url,страна, категория
			        	String elementText = divElement.getText().toString();
			        	elementText=elementText.replaceFirst("\\s+","");
			        	nomenklatura.add(new String[] {elementText,divElement.getAttributeByName("href").toString(),tekCountry[0],tekCountry[2]});
			        	DBWork.updateNomenklatura(elementText, divElement.getAttributeByName("href").toString(),tekCountry[0],tekCountry[2]);			        	
			        }
			        links.clear(); //очищаяем ссылки на url
			        loadProgress+=progressIncrement;sendProgress(loadProgress);
		        }
		        		 
		         progressIncrement=30f/nomenklatura.size(); // 30% загрузки
		         
		        for (String[] tekNomenklatura: nomenklatura)	{
		        	String urlstring=arg[0]+tekNomenklatura[1];
		        	String urlShort=tekNomenklatura[1];
		        	hh = new HtmlHelper(new URL(urlstring));
		        	links = hh.getFullPageLinks();

		        	if (links.size()==2) {
		        		//линк на картинку
		        		TagNode divElement = links.get(0);
		        		String urlImg=arg[0]+divElement.getAttributeByName("src");
		        		
		        		
		        		//описание номенклатуры
		        		divElement = links.get(1);
		        		        		
		        		String descriptionNomenklatur=divElement.getText().toString();
		        		//descriptionNomenklatur=descriptionNomenklatur.replaceFirst("\\s+","");		        		
		        		
		        		//url,description,imageurl
		        		DBWork.updateNomenklaturaLongDescriptionAndImage(urlShort, descriptionNomenklatur,urlImg);
		        		
		        	}
		        	loadProgress+=progressIncrement;sendProgress(loadProgress);
		        	//if (true==TEST_LOAD) break;
		        
		        }
		        
		        loadImageData();
		        
		        //удаляем ссылки на страны, они больше не нужны
		        country.clear();
		        country=null;
		        
		        	
		      }
		      catch(Exception e)
		      {
		        e.printStackTrace();
		      }
			return null;
		    }
		    

		    //Событие по окончанию парсинга	    
		    protected void onPostExecute(List<String> output) {
		      //Убираем диалог загрузки
		      //pd.dismiss();
		      
		    	writeSPref();
		    	
		    	if (wakeLock!=null) {
		    		wakeLock.release();
		    	}
		      
		      //Находим ListView
		      
		     /* ListView listview = (ListView) mActivity.findViewById(R.id.listViewData);
		      //Загружаем в него результат работы doInBackground
		      listview.setAdapter(new ArrayAdapter<String>(mActivity,
		          R.layout.mysimplelistitem , output));*/
		      
		      if (null!=mActivity && !mActivity.isFinishing()) {	
		    	  Toast.makeText(mActivity,"Обновление базы завершено.", Toast.LENGTH_LONG).show();
		    	  ((MainActivity)mActivity).forseload();
		      }
		      
		      if (null!=mService) {
		    	  mService.echoIntent(MyReceiver.END_PARAM);
		    	  mService.stopSelf();
		    	  
		      }
		    }
		    
		    
		    protected void loadImageData() throws ClientProtocolException, IOException {
		    	Cursor cur = DBWork.getImageUrlData();
		    	int imageUrlIndex = cur.getColumnIndex("imageurl");
		    	int urlIndex = cur.getColumnIndex("url");
		    	
		    	cur.moveToFirst();
		    	float progressIncrement=Math.abs(100-loadProgress)/cur.getCount();
		    	while (cur.isAfterLast() == false) 
		    	{
		    		String tekUrl=cur.getString(urlIndex);
		    		String tekImageUrl=cur.getString(imageUrlIndex);
		    		//Log.d("MyLogs", "url="+tekUrl);
		    		//Log.d("MyLogs", "tekImageUrl="+tekImageUrl);
		    		try {
		    			getImageFromUrl(tekUrl,tekImageUrl);
					} catch (Exception e) {
						// TODO: handle exception
					}		    		
		    	    cur.moveToNext();
		    	    loadProgress+=progressIncrement;sendProgress(loadProgress);
		    	}		    	
		    }
		    
		    protected void getImageFromUrl(String tekUrl,String tekImageUrl) throws ClientProtocolException, IOException {
		    	if (!tekImageUrl.equals(SynchronizationSite.NO_IMAGE_URL)) {
			    	DefaultHttpClient mHttpClient = new DefaultHttpClient();
			    	HttpGet mHttpGet = new HttpGet(tekImageUrl);
			    	HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
			    	if (mHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			    	  HttpEntity entity = mHttpResponse.getEntity();
			    	    if ( entity != null) {
			    	      // insert to database
			    	    	DBWork.updateImage(EntityUtils.toByteArray(entity),tekUrl); 
			    	    	//Log.d("MyLogs", "image "+tekImageUrl+" is loaded.");
			    	    }
			    	}
		    	}
		    }
		    
		  }	  
	  
	  protected void writeSPref() {
			
		  Resources mRes;
		  SharedPreferences prefs;
		  if (mActivity!=null) {mRes=mActivity.getResources();prefs = mActivity.getSharedPreferences(mRes.getString(R.string.app_name),Context.MODE_PRIVATE);}
		  if (mService!=null) {mRes = mService.getResources();prefs = mService.getSharedPreferences(mRes.getString(R.string.app_name),Context.MODE_PRIVATE);}
		  else prefs=null; //exeption
						           
	        SharedPreferences.Editor editor = prefs.edit();
	     
	        editor.putLong("lastUpdate",System.currentTimeMillis());	       
	        editor.commit();				
	}
	  
	  protected void sendProgress(float prog) {
		  if (null!=mService) {
			  mService.echoIntent((int)prog);
		  }
	  }


}
