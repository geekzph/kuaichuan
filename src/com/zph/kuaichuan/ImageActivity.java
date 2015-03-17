package com.zph.kuaichuan;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageActivity extends Activity {    
    private GridView photoView;  
    private Cursor cursor;   
    private int[] thumbIds;  
      
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.image);  
          
        photoView = (GridView)this.findViewById(R.id.ImageGrid);  
        //photoView.setOnItemClickListener(photoClickListener);  
        showThumbnails();  
        photoView.setAdapter(new ImageAdapter(this));
        
    }  
      
    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();  
    private void showThumbnails(){  
          
        cursor = BitmapUtils.queryImageThumbnails(this);  
        if(cursor.moveToFirst()){  
            
            thumbIds = new int[cursor.getCount()];  
            for(int i=0; i<cursor.getCount();i++){  
                cursor.moveToPosition(i);  
                String currPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));  
                thumbIds[i] = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID));  
                Bitmap b = BitmapUtils.decodeBitmap(currPath,100,100);  
                bitmaps.add(b);  
            }  
            
        }  
        
    }  
    
   
      /**
    private AdapterView.OnItemClickListener photoClickListener = new AdapterView.OnItemClickListener() {  
        public void onItemClick(AdapterView<?> p, View v, int position,  
                long id) {  
            //点击某张缩略图时，转到图片显示界面           
            Intent intent = new Intent();  
            //intent.setClass(MediaActivity.this, GalleryActivity.class);  
            intent.putExtra("thumbIds", thumbIds);  
            intent.putExtra("currentPos", position);  
            startActivity(intent);  
        }  
    };  **/
    
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
 

    	 private class GridHolder {  
    	         ImageView appImage; 
    	         ImageView selectImage;
    	     }  
    	   


        public ImageAdapter(Context c) {
            mContext = c;

        	mInflater = (LayoutInflater) mContext  
        	                 .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  


        }
 
        public int getCount() {
            return bitmaps.size();
        }
 
        public Object getItem(int position) {
            return null;
        }
 
        public long getItemId(int position) {
            return 0;
        }
 
        private LayoutInflater mInflater;
        public View getView(int position, View convertView, ViewGroup parent) 
        {

        	 GridHolder holder;  
        	 if (convertView == null) 
        	 {     
        	 
        		 convertView = mInflater.inflate(R.layout.image_grid_item1, null);     
        	     holder = new GridHolder();  
        	     holder.appImage = (ImageView)convertView.findViewById(R.id.imageView); 
        	     holder.selectImage = (ImageView)convertView.findViewById(R.id.selected_photo); 
        	     convertView.setTag(holder);     
        	   
             }else
             {  
        	     holder = (GridHolder) convertView.getTag();       
        	 }  
        	 holder.appImage.setImageBitmap(bitmaps.get(position));
        	 holder.selectImage.setVisibility(2);
        	         return convertView;  


        /**
        	MyImageView imageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
            	// 给ImageView设置资源
            	imageView = new MyImageView(mContext);
            	//imageView.setBackgroundColor(R.drawable.image_border);
            	//imageView.setPadding(3, 3, 3, 3);
            	// 设置布局 图片120×120显示
            	//imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            	// 设置显示比例类型
            	//imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            } else {
                imageView = (MyImageView) convertView;
            }
            imageView.setImageBitmap(bitmaps.get(position));
            return imageView;**/
        }
 
    }

      
}  