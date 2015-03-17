package com.zph.kuaichuan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

@SuppressWarnings("deprecation")
public class PictureActivity extends Activity implements SensorListener {    
	   
	private ViewPager mPager;//ҳ������
	 private List<View> listViews; // Tabҳ���б�
	 private ImageView ingcursor;// ����ͼƬ
	 private ImageView t1, t2;// ҳ��ͷ��
	 private int offset = 0;// ����ͼƬƫ����
	 private int currIndex = 0;// ��ǰҳ�����
	 private int bmpW;// ����ͼƬ���
	 
	 
	 DragGridView photoView,cameraView;;
	 private View image_page,camera_page;
	 private MediaPlayer back_player;
	 private MyServer myServer;
	 private MyClient myClient;
	 private SensorManager sensorManager;
	 private int screenWidth;
	 private TranslateAnimation translateAnimation;
   @Override  
   public void onCreate(Bundle savedInstanceState) {  
       super.onCreate(savedInstanceState);  
       setContentView(R.layout.picturevp);
       InitTextView();
       InitImageView();
       InitViewPager();
       Vibrator vib = (Vibrator)this.getSystemService(Service.VIBRATOR_SERVICE);//�𶯷���
       //����ͼƬ
       imgBitmaps=SplashActivity.imgBitmaps;
       cameraBitmaps=SplashActivity.cameraBitmaps;
       camFiles=SplashActivity.camFiles;
       imgFiles=SplashActivity.imgFiles;
       cameraView=(DragGridView)camera_page.findViewById(R.id.CameraGrid);
       cameraView.vib=vib;
       cameraView.setOnItemClickListener(cameraClickListener);
       cameraView.setAdapter(new CameraAdapter(this));
       
       
       photoView=(DragGridView)image_page.findViewById(R.id.ImageGrid);
       photoView.vib=vib;
       photoView.setOnItemClickListener(photoClickListener);
       photoView.setAdapter(new ImageAdapter(this));
       
     //˦**/
       this.back_player =  MediaPlayer.create(this,R.raw.dragthrow);
       myServer = (MyServer)getIntent().getSerializableExtra(MainTab.SERVER_KEY);
       myClient = (MyClient)getIntent().getSerializableExtra(MainTab.CLIEDNT_KEY);
     //��ȡ�������������
       sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
       sensorManager.registerListener(this,SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_NORMAL);
       //��������
       Display display = getWindowManager().getDefaultDisplay();
       screenWidth = display.getWidth();
       Log.d("PictureActivity", "created");
   }  
   
   private List<Bitmap> imgBitmaps = new ArrayList<Bitmap>();
   private List<Bitmap> cameraBitmaps = new ArrayList<Bitmap>();
   
   private List<MyFile> camFiles = new ArrayList<MyFile>();
   private List<MyFile> imgFiles = new ArrayList<MyFile>();
   
   AdapterView.OnItemClickListener cameraClickListener = new AdapterView.OnItemClickListener() {  
       public void onItemClick(AdapterView<?> p, View v, int position,  
               long id) {  
    	   Log.d("cameraClickListener", String.valueOf(position));
    	   //MyShowImg myShowImg=new MyShowImg(mContext,R.style.dialog,BitmapFactory.decodeFile(camFiles.get(position).getFilePath()));;
    	   //myShowImg.show();
       }  
   }; 
   
   AdapterView.OnItemClickListener photoClickListener = new AdapterView.OnItemClickListener() {  
       public void onItemClick(AdapterView<?> p, View v, int position,  
               long id) {  
    	   Log.d("photoClickListener", String.valueOf(position));
    	   //MyShowImg myShowImg=new MyShowImg(mContext,R.style.dialog,BitmapFactory.decodeFile(imgFiles.get(position).getFilePath()));
    	   //myShowImg.show();
       }  
   }; 
   
   public class CameraAdapter extends BaseAdapter {
       private Context mContext;


   	 private class GridHolder {  
   	         ImageView appImage; 
   	         ImageView selectImage;
   	     }  
   	   


       public CameraAdapter(Context c) {
           mContext = c;

       	mInflater = (LayoutInflater) mContext  
       	                 .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  


       }

       public int getCount() {
           return cameraBitmaps.size();
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
       	 
       		 convertView = mInflater.inflate(R.layout.image_grid_item2, null);     
       	     holder = new GridHolder();  
       	     holder.appImage = (ImageView)convertView.findViewById(R.id.imageView); 
       	     holder.selectImage = (ImageView)convertView.findViewById(R.id.selected_photo); 
       	     convertView.setTag(holder);     
       	   
            }else
            {  
       	     holder = (GridHolder) convertView.getTag();       
       	 }  
       	 holder.appImage.setImageBitmap(cameraBitmaps.get(position));
       	 holder.selectImage.setVisibility(View.GONE);
       	Log.d("PictureActivity", "cameraadapter");
       	         return convertView;  


       /**
       	MyImageView imageView;
           if (convertView == null) {  // if it's not recycled, initialize some attributes
           	// ��ImageView������Դ
           	imageView = new MyImageView(mContext);
           	//imageView.setBackgroundColor(R.drawable.image_border);
           	//imageView.setPadding(3, 3, 3, 3);
           	// ���ò��� ͼƬ120��120��ʾ
           	//imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
           	// ������ʾ��������
           	//imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

           } else {
               imageView = (MyImageView) convertView;
           }
           imageView.setImageBitmap(bitmaps.get(position));
           return imageView;**/
       }

   }
 
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
           return imgBitmaps.size();
       }

       public Object getItem(int position) {
           return null;
       }

       public long getItemId(int position) {
           return 0;
       }

       private LayoutInflater mInflater;
       
       public View getView(int position,  View convertView, ViewGroup parent) 
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
       	 holder.appImage.setImageBitmap(imgBitmaps.get(position));
       	 holder.selectImage.setVisibility(View.GONE);
       	 Log.d("PictureActivity", "imgapapter");
       	 return convertView;
       	   

       }
       

   }
   /**
   public class ImageAndTextListAdapter extends ArrayAdapter<ImageAndText> {

       private GridView gridView;
       private AsyncImageLoader asyncImageLoader;
       public ImageAndTextListAdapter(Activity activity, List<ImageAndText> imageAndTexts, GridView gridView1) {
           super(activity, 0, imageAndTexts);
           this.gridView = gridView1;
           asyncImageLoader = new AsyncImageLoader();
       }

       public View getView(int position, View convertView, ViewGroup parent) {
           Activity activity = (Activity) getContext();

           // Inflate the views from XML
           View rowView = convertView;
           ViewCache viewCache;
           if (rowView == null) {
               LayoutInflater inflater = activity.getLayoutInflater();
               rowView = inflater.inflate(R.layout.image_grid_item1, null);
               viewCache = new ViewCache(rowView);
               rowView.setTag(viewCache);
           } else {
               viewCache = (ViewCache) rowView.getTag();
           }
           ImageAndText imageAndText = getItem(position);

           // Load the image and set it on the ImageView
           String imageUrl = imageAndText.getImageUrl();
           ImageView imageView = viewCache.getImageView();
           imageView.setTag(imageUrl);
           Drawable cachedImage = asyncImageLoader.loadDrawable(imageUrl, new ImageCallback() {
               public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                   ImageView imageViewByTag = (ImageView) gridView.findViewWithTag(imageUrl);
                   if (imageViewByTag != null) {
                       imageViewByTag.setImageDrawable(imageDrawable);
                   }
               }
           });
           if (cachedImage == null) {
               imageView.setImageResource(R.drawable.icon);
           }else{
               imageView.setImageDrawable(cachedImage);
           }
           // Set the text on the TextView
           //TextView textView = viewCache.getTextView();
           //textView.setText(imageAndText.getText());
           return rowView;
       }

}
   **/
   //=======================================================
   /**
    * ��ʼ��ͷ��
*/
   private void InitTextView() 
   {
       t1 = (ImageView) findViewById(R.id.imageView1);
       t2 = (ImageView) findViewById(R.id.imageView2);

       t1.setOnClickListener(new MyOnClickListener(0));
       t2.setOnClickListener(new MyOnClickListener(1));
       Log.d("PictureActivity", "InitTextView");
   }
   
   /**
    * ͷ��������
*/
   int mcurrentIndex=1;
   public class MyOnClickListener implements View.OnClickListener {
       private int index=0;

       public MyOnClickListener(int i) {
           index = i;
       }

       public void onClick(View v) {
           mPager.setCurrentItem(index);
           if(index==0)
           {
        	mcurrentIndex=1;
           	imageSelect(1);
           }
           else if(index==1)
           {
        	mcurrentIndex=2;
           	imageSelect(2);
           }
       }
   };
   
   private void imageSelect(int index)
   {
   	if(index==1)
   	{
   		t1.setSelected(true);
       	t2.setSelected(false);
   	}
   	else if(index==2)
   	{
   		t2.setSelected(true);
       	t1.setSelected(false);
   	}
   }
   /**
    * ��ʼ��ViewPager
*/
   private void InitViewPager() {
       mPager = (ViewPager) findViewById(R.id.vPager);
       listViews = new ArrayList<View>();
       LayoutInflater mInflater = getLayoutInflater();
       camera_page=mInflater.inflate(R.layout.camera, null);
       image_page=mInflater.inflate(R.layout.image, null);
       listViews.add(camera_page);
       listViews.add(image_page);
       mPager.setAdapter(new MyPagerAdapter(listViews));
       mPager.setCurrentItem(0);
       mPager.setOnPageChangeListener(new MyOnPageChangeListener());
       Log.d("PictureActivity", "InitViewPager");
       
   }
   
   /**
    * ViewPager������
*/
   public class MyPagerAdapter extends PagerAdapter {
       public List<View> mListViews;

       public MyPagerAdapter(List<View> mListViews) {
           this.mListViews = mListViews;
       }

       @Override
       public void destroyItem(View arg0, int arg1, Object arg2) {
       	
           ((ViewPager) arg0).removeView(mListViews.get(arg1));
       }

       @Override
       public void finishUpdate(View arg0) {
       }

       @Override
       public int getCount() {
           return mListViews.size();
       }

       @Override
       public Object instantiateItem(View arg0, int arg1) {
           ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
           return mListViews.get(arg1);
       }

       @Override
       public boolean isViewFromObject(View arg0, Object arg1) {
           return arg0 == (arg1);
       }

       @Override
       public void restoreState(Parcelable arg0, ClassLoader arg1) {
       }

       @Override
       public Parcelable saveState() {
           return null;
       }

       @Override
       public void startUpdate(View arg0) {
       }
   }
   
   /**
    * ��ʼ������
*/
   private void InitImageView() {
   	ingcursor = (ImageView) findViewById(R.id.imageView4);
       bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.tab_indicator)
               .getWidth();// ��ȡͼƬ���
       DisplayMetrics dm = new DisplayMetrics();
       getWindowManager().getDefaultDisplay().getMetrics(dm);
       int screenW = dm.widthPixels;// ��ȡ�ֱ��ʿ��
       offset = (screenW / 2 - bmpW) / 2;// ����ƫ����
       Matrix matrix = new Matrix();
       matrix.postTranslate(offset, 0);
       ingcursor.setImageMatrix(matrix);// ���ö�����ʼλ��
       Log.d("PictureActivity", "InitImageView");
   }
   
   /**
    * ҳ���л�����
*/
   public class MyOnPageChangeListener implements OnPageChangeListener {

       int one = offset * 2 + bmpW;// ҳ��1 -> ҳ��2 ƫ����
       
       public void onPageSelected(int arg0) {
           Animation animation = null;
           switch (arg0) {
           case 0:
               if (currIndex == 1) {
                   animation = new TranslateAnimation(one, 0, 0, 0);
                   mcurrentIndex=1;
                   imageSelect(1);
               } 
               break;
           case 1:
               if (currIndex == 0) {
                   animation = new TranslateAnimation(offset, one, 0, 0);
                   mcurrentIndex=2;
                   imageSelect(2);
               } 
               break;
           
           }
           currIndex = arg0;
           animation.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��
           animation.setDuration(300);
           ingcursor.startAnimation(animation);
       }

       public void onPageScrolled(int arg0, float arg1, int arg2) {
       }

       public void onPageScrollStateChanged(int arg0) {
       }
   }
   
   @Override  
   public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("yao","PictureActivity.onKeyDown");
		 // ���¼����Ϸ��ذ�ť    
	       if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {    
	   
	           new AlertDialog.Builder(this)    
	                   .setMessage("ȷ���˳���")    
	                   .setNegativeButton("ȡ��",    
	                           new DialogInterface.OnClickListener() {    
	                               public void onClick(DialogInterface dialog,    
	                                       int which) {    
	                               }    
	                           })    
	                   .setPositiveButton("ȷ��",    
	                           new DialogInterface.OnClickListener() {    
	                               public void onClick(DialogInterface dialog,    
	                                       int whichButton) {    
	                                   finish();    
	                               }    
	                           }).show();    
	   
	           return true;    
	       } else {    
	           return super.onKeyDown(keyCode, event);    
	       }    
	}
   @Override
   protected void onStop(){
       super.onStop(); 
       sensorManager.unregisterListener(this);
       Log.d("PictureActivity", "onStop");
       
   }
   
   @Override
   protected void onResume(){
       super.onResume();
       sensorManager.registerListener(this,SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_NORMAL);
       Log.d("PictureActivity", "onRseume");
   }
   
   @Override 
   protected void onPause() { 
       super.onPause(); 
       Log.d("PictureActivity", "onPause");
   }

public void onSensorChanged(int sensor, float[] values) {
	// TODO Auto-generated method stub
	  if((Math.abs(values[0])>12||Math.abs(values[1])>11||Math.abs(values[2])>12))
      {
          if(cameraView.dragIndex!=8888&&mcurrentIndex==1)
          {
          	String isServerClient=MainTab.isServerClient;
          	cameraView.rotateAnimation();
          	cameraView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, cameraView.getLeft()+5, cameraView.getTop()+5, 0));
          	cameraView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, cameraView.getLeft()+5, cameraView.getTop()+5, 0));
          	//Toast.makeText(getApplicationContext(), String.valueOf(cameraView.dragIndex),Toast.LENGTH_SHORT).show();
          	this.back_player.start();
          	if(isServerClient.equals("Server"))
          		myServer.sendFile(camFiles.get(cameraView.dragIndex));
          	if(isServerClient.equals("Client"))
          		myClient.sendFile(camFiles.get(cameraView.dragIndex));
          	cameraView.dragIndex=8888;
          }
          
          if(photoView.dragIndex!=8888&&mcurrentIndex==2)
          {
          	String isServerClient=MainTab.isServerClient;
          	photoView.rotateAnimation();
          	photoView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, photoView.getLeft()+5, photoView.getTop()+5, 0));
          	photoView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, photoView.getLeft()+5, photoView.getTop()+5, 0));
          	//Toast.makeText(getApplicationContext(), String.valueOf(photoView.dragIndex),Toast.LENGTH_SHORT).show();
          	this.back_player.start();
          	if(isServerClient.equals("Server"))
          		myServer.sendFile(imgFiles.get(photoView.dragIndex));
          	if(isServerClient.equals("Client"))
          		myClient.sendFile(imgFiles.get(photoView.dragIndex));
          	photoView.dragIndex=8888;
          }
      }
}
public void onAccuracyChanged(int sensor, int accuracy) {
	// TODO Auto-generated method stub
	
}
   
   ///=====================================================
public class MyShowImg extends Dialog
{

	Bitmap bm=null;
	public MyShowImg(Context context,int theme,Bitmap bm) {
		super(context,theme);
		// TODO Auto-generated constructor stub
		this.bm = bm;
	}
	
	protected void onCreate(Bundle savedInstanceState){  
        super.onCreate(savedInstanceState);  
        
        Window mWindow = getWindow();  
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.alpha = 0.8f;//����͸����
        mWindow.setAttributes(lp);
        setContentView(R.layout.imgdia);  
        final ImageView myImg = (ImageView) findViewById(R.id.imageView1);
        myImg.setImageBitmap(bm);
        translateAnimation = new TranslateAnimation(0f,0f,0f,0f);
        
        
        myImg.setOnTouchListener(new OnTouchListener() {
        int[] temp = new int[] { 0, 0 };
        	int m=0;
        	int startX,startY,endX,endY;
        	Boolean isDismiss=false;
            public boolean onTouch(final View v, MotionEvent event) {
		        if(m==0)
		        {
            	startX=myImg.getLeft();
			    startY=myImg.getTop();
			    endX=myImg.getRight();
			    endY=myImg.getBottom();
		        }
		        
            	translateAnimation.setAnimationListener(new AnimationListener() {
    	            public void onAnimationStart(Animation animation) {
    	            }
    	            public void onAnimationRepeat(Animation animation) {
    	            }
    	            public void onAnimationEnd(Animation animation) {
    	            	Log.i("########", "onAnimationEnd");
    	            	if(isDismiss==true)
    	                {
    	            	dismiss();
    	            	Log.i("########", "dismis");
    	                }
    	                else
    	                {
    	                //���䶯������....
    	            	myImg.clearAnimation();
    	            	v.layout(startX, startY,endX,endY);
    	            	v.postInvalidate(); //redraw
    	            	Log.i("########", "meiyou dismiss");
    	                }
    	            }
    	        });
            	int[] location = new int[2];  
                myImg.getLocationOnScreen(location);  
                int eventaction = event.getAction();
                Log.i("&&&", "onTouchEvent:" +String.valueOf(startX));
                int	y = myImg.getTop(); 
                int x = (int) event.getRawX();
          
                
                switch (eventaction) {
                case MotionEvent.ACTION_DOWN: // touch down so check if the
                    temp[0] = (int) event.getX() ; 
                    temp[1] = y - v.getTop();
                    break;

                case MotionEvent.ACTION_MOVE: // touch drag with the ball
                	Log.i("============", String.valueOf((double)(x + v.getWidth() - temp[0]-screenWidth)/v.getWidth()));
                	
                	try {
						myServer.slideToSend(String.valueOf((double)(x + v.getWidth() - temp[0]-screenWidth)/v.getWidth()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    v.layout(x - temp[0], y - temp[1], x + v.getWidth() - temp[0], y - temp[1] + v.getHeight());
                    v.postInvalidate(); //redraw
                    
                    break;
                case MotionEvent.ACTION_UP:
                	if((x + v.getWidth() - temp[0]-screenWidth)>v.getWidth()/5)
                	{
                		
                         translateAnimation = new TranslateAnimation(0f, v.getWidth()-(x + v.getWidth() - temp[0]-screenWidth),0f,0f);
                   	     translateAnimation.setDuration (500);
                   	     translateAnimation.setFillAfter(true);
                   	     myImg.setAnimation(translateAnimation);
                   	     //isTouchUp = true;
                   	     translateAnimation.startNow();
                   	     isDismiss = true;
                   	     //dismiss();       	      
                   	     Log.i("####outout###", String.valueOf(x + v.getWidth() - temp[0]-screenWidth)+"   "+String.valueOf(v.getWidth()/5));
                   	     m++;
                	     break;
                	}
                	/**
                	else
                	{
                		translateAnimation = new TranslateAnimation(0f,-(x - temp[0]),0f,0f);
               	     	translateAnimation. setDuration (500);
               	     	translateAnimation.setFillAfter(true);
               	     	myImg.setAnimation(translateAnimation);
               	     	//translateAnimation.startNow();
               	     	isDismiss = false;
               	     	isTouchUp = true;
               	     	m++;
               	     	Log.i("####����hi####", String.valueOf(x + v.getWidth() - temp[0]-screenWidth)+"   "+String.valueOf(v.getWidth()/5));
               	     	break;
                	}**/
                		
                    
                }

                return false;
            }
        });
        //
        
    }
        
    } 
      
}  