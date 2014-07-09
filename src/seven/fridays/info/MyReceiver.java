package seven.fridays.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

	
	public static String EXTRA_TASK="task";
	public static String EXTRA_STATUS="status";
	
	public static final int START_PARAM=1;
	public static final int END_PARAM=999;
	public static final int TASK_PROGRESS = 100;
	
	MainActivity mActivity;
	
	/*public MyReceiver(MainActivity mainActivity) {
		// TODO Auto-generated constructor stub
		mActivity=
	}*/

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub		
		int task = intent.getIntExtra("task", 0);
        int status = intent.getIntExtra("status", 0);
       
        if (task==MyReceiver.START_PARAM) {
        	Toast.makeText(context, "Запуск. Обновление данных.", Toast.LENGTH_LONG).show();      	
        } else if (task==MyReceiver.END_PARAM) {        	
        	Toast.makeText(context, "Обновление данных завершено.", Toast.LENGTH_LONG).show();
        	((MainActivity)context).forseload();
        	ProgressBar progressBar =(ProgressBar) ((MainActivity)context).findViewById(R.id.progressBar1);
        	progressBar.setVisibility(View.INVISIBLE);
        } else if (task==MyReceiver.TASK_PROGRESS) {
        	ProgressBar progressBar =(ProgressBar) ((MainActivity)context).findViewById(R.id.progressBar1);
        	progressBar.setProgress(status);
        	progressBar.setVisibility(View.VISIBLE);
        }
      }		

}
