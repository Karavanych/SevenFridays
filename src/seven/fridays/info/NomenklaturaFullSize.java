package seven.fridays.info;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class NomenklaturaFullSize extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// do fullscreen app
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		
		setContentView(R.layout.myfullitem);
		
		
		//set background color for action bar
		android.app.ActionBar bar = getActionBar();
		//for color
		bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.BASEFON)));
		
/*		//change title background color
		View titleView = getWindow().findViewById(android.R.id.title);
		if (titleView != null) {
		  ViewParent parent = titleView.getParent();
		  if (parent != null && (parent instanceof View)) {
		    View parentView = (View)parent;
		    parentView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2e014f")));
		  }
		}	*/	
				
        Intent intent = getIntent();        
        String url = intent.getStringExtra("url");
        
        ContentValues loaderParams = new ContentValues();		
		loaderParams.put("url",url);		
		
		Cursor cr = DBWork.getAllData(loaderParams);
		
		if (cr.getCount() > 0) {
		
			cr.moveToFirst();
			
			// set image
			int imageIndex = cr.getColumnIndex("image");
			if (imageIndex != -1) {
				byte[] bb = cr.getBlob(imageIndex);
				ImageView myImage = (ImageView) findViewById(R.id.fullImg);
				if (myImage != null && bb!=null) {
					myImage.setImageBitmap(BitmapFactory.decodeByteArray(bb, 0,
							bb.length));
				}			
				if (bb==null) {
					myImage.setImageResource(R.drawable.no_image_big);				
				}
			}		
			
			
			//set title
			int nameIndex = cr.getColumnIndexOrThrow("name");
			this.setTitle(Html.fromHtml(cr.getString(nameIndex)));
									
			//set text
			int descrIndex= cr.getColumnIndexOrThrow("description");
			String textDescString=cr.getString(descrIndex);
			
			if (textDescString!=null && !textDescString.isEmpty()) {			
				String[] newLinePatterns=getResources().getStringArray(R.array.newlinepattern);
				for (String tekPattern: newLinePatterns){
					textDescString=textDescString.replace(tekPattern, "<br><strong>"+tekPattern+"</strong>");								
				}
									
				Spanned textdecr = (Html.fromHtml(textDescString));
				
				((TextView)findViewById(R.id.fulltext2)).setText(textdecr);	
			}
			
		}
		cr.close();
        
	}

}
