package seven.fridays.info;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class MyCompatible {
	
	
	public static Point getDisplaySize(final Activity ac,boolean nostatusbar) {
		final Point point = new Point();
		Display display = ac.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
	    display.getMetrics(metrics);
	    
	    int StatusBarHeight=0;
	    if (nostatusbar) StatusBarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, metrics);//25 dip	    
	    
	    point.x=metrics.widthPixels;
	    point.y=metrics.heightPixels+StatusBarHeight;  
		
		return point;				
	}
	
	public static Point getDisplaySize(final Display display) {
		
	    final Point point = new Point();
	    
	    DisplayMetrics metrics = new DisplayMetrics();
	    display.getMetrics(metrics);
	    point.x=metrics.widthPixels;
	    point.y=metrics.heightPixels;

	    return point;
	}

	public static Point getDisplaySize(Display display, boolean nostatusbar){
		// TODO Auto-generated method stub
		final Point point = new Point();
		
	 	//android.graphics.Rect rectgle= new android.graphics.Rect();
    	//window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
    	//int StatusBarHeight= rectgle.top; 
    	
    	//int viewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		
    	
        DisplayMetrics metrics = new DisplayMetrics();
	    display.getMetrics(metrics);
	    
	    int StatusBarHeight=0;
	    if (nostatusbar) StatusBarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, metrics);//25 dip	    
	    
	    point.x=metrics.widthPixels;
	    point.y=metrics.heightPixels+StatusBarHeight;  
	
    	
		return point;
	}	
	
	
	public static int getPixels(Context c, float dipValue) {
		Resources r = c.getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dipValue, r.getDisplayMetrics());
		return px;
	}

	
	public static int getRotation(Context context){
        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
               switch (rotation) {
                case Surface.ROTATION_0:
                    return 0;
                case Surface.ROTATION_90:
                    return 90;
                case Surface.ROTATION_180:
                    return 180;
                default:
                    return 270;
                }
            }	
}
