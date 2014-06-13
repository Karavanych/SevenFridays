package seven.fridays.info;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MySimpleCursorAdapter extends RefactoredSimpleCursorAdapted {

	private LayoutInflater mLayoutInflater;
	
	public MySimpleCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		// Auto-generated constructor stub
        mLayoutInflater = LayoutInflater.from(context); 
	}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mLayoutInflater.inflate(R.layout.mysimplelistitem, parent, false);
        return v;
    }
    
    @Override
    public void bindView(View v, Context context, Cursor c) {
    	// TODO Auto-generated method stub
    	super.bindView(v, context, c);  	
    	
    	//set image
    	
		int imageIndex = c.getColumnIndex("image");
		if (imageIndex != -1) {
			byte[] bb = c.getBlob(imageIndex);
			ImageView myImage = (ImageView) v.findViewById(R.id.ivImg);
			if (myImage != null && bb!=null) {
				myImage.setImageBitmap(BitmapFactory.decodeByteArray(bb, 0,
						bb.length));
			}
			
			if (bb==null) {
				myImage.setImageResource(R.drawable.no_image_big);				
			}
		}
			
		
		
		   	
    }

}
