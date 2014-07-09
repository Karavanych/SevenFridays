package seven.fridays.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		  SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
          long lastUpdate = pref.getLong("lastUpdate", 0);

          
		if (Math.abs(System.currentTimeMillis()-lastUpdate)>SynchronizationSite.UPDATE_SYNCHRONIZATION_TIME) { // need update
			if (isDeviceOnline(context)) {
				//Toast.makeText(context, "Автоматически запущено обновление данных.", Toast.LENGTH_LONG).show();				
				context.startService(new Intent(context, MyDBUpdateService.class));
			} else {
				Toast.makeText(context, "Данные устарели, доступ в интеренет отключен. Подключитесь к интернету для выполнения обновления.", Toast.LENGTH_LONG).show();				
			}
			
		}		

	}

    private boolean isDeviceOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
        		context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        NetworkInfo net3gInfo=connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        // Если есть 3g
        if (net3gInfo !=null && net3gInfo.isConnected()) {
        	return true;
        }
        
        return false;
    }
	
}
