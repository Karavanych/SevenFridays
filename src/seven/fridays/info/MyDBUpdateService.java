package seven.fridays.info;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyDBUpdateService extends Service {

	 private boolean inProgress=false;
	  
	  @Override
	  public void onCreate() {
	    super.onCreate();
	 
	  }
	  
	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	   
	    // сообщаем об старте задачи
	    
	    //echoIntent(MyReceiver.START_PARAM);
        
		  if (!inProgress) someTask();
	    

               
	    return super.onStartCommand(intent, flags, startId);
	  }

	  @Override
	  public void onDestroy() {
	    super.onDestroy();
	   
	  }

	  @Override
	  public IBinder onBind(Intent intent) {
	    
	    return null;
	  }
	  
	 
	  void someTask() {
		  
		  inProgress=true;
		  echoIntent(MyReceiver.START_PARAM);
          SynchronizationSite mSynchronizationSite = new SynchronizationSite(this);
          mSynchronizationSite.doParseSite(new String[]{getResources().getString(R.string.siteSevenFrides),getResources().getString(R.string.siteSevenFridesAlhoTable)});
		  
	  }
	  
	  public void echoIntent(int arg1) {
		  Intent echoIntent = new Intent(MainActivity.BROADCAST_ACTION);
		  
		  if (arg1==MyReceiver.END_PARAM) {
			inProgress=false;
		    echoIntent.putExtra(MyReceiver.EXTRA_TASK, MyReceiver.END_PARAM);
		    echoIntent.putExtra(MyReceiver.EXTRA_STATUS,MyReceiver.END_PARAM);
		    sendBroadcast(echoIntent);
		    return;
		  }
		  
		  if (arg1==MyReceiver.START_PARAM) {
			
			echoIntent.putExtra(MyReceiver.EXTRA_TASK, MyReceiver.START_PARAM);
			echoIntent.putExtra(MyReceiver.EXTRA_STATUS,MyReceiver.START_PARAM);	    
		    sendBroadcast(echoIntent);	
		    return;
		  }
		  
			echoIntent.putExtra(MyReceiver.EXTRA_TASK, MyReceiver.TASK_PROGRESS);
			echoIntent.putExtra(MyReceiver.EXTRA_STATUS,arg1);	    
		    sendBroadcast(echoIntent);		  
		  
	  }
}
