package com.zph.kuaichuan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MediaActivity extends Activity implements SensorListener{    
	   
	 private ViewPager mPager;//页卡内容
	 private List<View> listViews; // Tab页面列表
	 private ImageView ingcursor;// 动画图片
	 private ImageView t1, t2;// 页卡头标
	 private int offset = 0;// 动画图片偏移量
	 private int currIndex = 0;// 当前页卡编号
	 private int bmpW;// 动画图片宽度

	 private DragGridView videoView,musicView;
	 private View music_page,video_page;
	 
	 private MediaPlayer back_player;
	 private MyServer myServer;
	 private MyClient myClient;
	 private SensorManager sensorManager;
	 
	 private List<MyFile> musicFiles = new ArrayList<MyFile>();
	 private List<MyFile> videoFiles = new ArrayList<MyFile>();
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.mediavp);
        InitTextView();
        InitImageView();
        InitViewPager();
        Vibrator vib = (Vibrator)this.getSystemService(Service.VIBRATOR_SERVICE);
        //加载音乐
        musicView=(DragGridView)music_page.findViewById(R.id.MusicGrid);
        musicView.vib=vib;
        //遍历音乐封面;
        musicBitmaps=SplashActivity.musicBitmaps;
        musicName=SplashActivity.musicName;
        musicFiles=SplashActivity.musicFiles;
        musicView.setAdapter(new MusicAdapter(this));
        //加载视频
        videoView=(DragGridView)video_page.findViewById(R.id.VideoGrid);
        videoView.vib=vib;
        //遍历视频缩略图;
        videoBitmaps=SplashActivity.videoBitmaps;
        videoDuration=SplashActivity.videoDuration;
        videoFiles=SplashActivity.videoFiles;
        videoView.setAdapter(new VideoAdapter(this));
        
      //甩**/
        this.back_player =  MediaPlayer.create(this,R.raw.dragthrow);
        myServer = (MyServer)getIntent().getSerializableExtra(MainTab.SERVER_KEY);
        myClient = (MyClient)getIntent().getSerializableExtra(MainTab.CLIEDNT_KEY);
      //获取传感器管理服务
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_NORMAL);
        
        
        Log.d("MediaActivity", "created");
        
    }  
    
    //=================music逻辑处理====================================================================
    private List<Bitmap> musicBitmaps = new ArrayList<Bitmap>();
    private List<String> musicName=new ArrayList<String>();
   
    
    public class MusicAdapter extends BaseAdapter {
        private Context mContext;
 

    	 private class GridHolder {  
    	         ImageView audioImage,selectImage; 
    	         TextView audioName;
    	     }  
    	   


        public MusicAdapter(Context c) {
            mContext = c;

        	mInflater = (LayoutInflater) mContext  
        	                 .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  


        }
 
        public int getCount() {
            return musicBitmaps.size();
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
        	 
        		 convertView = mInflater.inflate(R.layout.audio_grid_item, null);     
        	     holder = new GridHolder();  
        	     holder.audioImage = (ImageView)convertView.findViewById(R.id.imageView); 
        	     holder.audioName = (TextView)convertView.findViewById(R.id.audio_name); 
        	     holder.selectImage=(ImageView)convertView.findViewById(R.id.audio_click_item); 
        	     convertView.setTag(holder);     
        	   
             }else
             {  
        	     holder = (GridHolder) convertView.getTag();       
        	 }  
        	 holder.audioImage.setImageBitmap(musicBitmaps.get(position));
        	 holder.audioName.setText(musicName.get(position));
        	 holder.selectImage.setVisibility(View.GONE);
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
    
    //========================music逻辑处理===============================
    
    //===============
    //========================video逻辑处理===============================
    private List<Bitmap> videoBitmaps = new ArrayList<Bitmap>();
    private List<String> videoDuration=new ArrayList<String>();
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式。
    
    public class VideoAdapter extends BaseAdapter {
        private Context mContext;
 

    	 private class GridHolder {  
    	         ImageView videoImage,selectImage; 
    	         TextView videoDuration;
    	     }  
    	   


        public VideoAdapter(Context c) {
            mContext = c;

        	mInflater = (LayoutInflater) mContext  
        	                 .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  


        }
 
        public int getCount() {
            return videoBitmaps.size();
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
        	 
        		 convertView = mInflater.inflate(R.layout.video_grid_item, null);     
        	     holder = new GridHolder();  
        	     holder.videoImage = (ImageView)convertView.findViewById(R.id.imageView); 
        	     holder.videoDuration = (TextView)convertView.findViewById(R.id.video_duration); 
        	     holder.selectImage=(ImageView)convertView.findViewById(R.id.video_click_item); 
        	     convertView.setTag(holder);     
        	   
             }else
             {  
        	     holder = (GridHolder) convertView.getTag();       
        	 }  
        	 holder.videoImage.setImageBitmap(videoBitmaps.get(position));
        	 holder.videoDuration.setText(videoDuration.get(position));
        	 holder.selectImage.setVisibility(View.GONE);
        	         return convertView;  

        }
 
    }
    
    //========================video逻辑处理==============================
    /**
     * 初始化头标
*/
    private void InitTextView() 
    {
        t1 = (ImageView) findViewById(R.id.imageView1);
        t2 = (ImageView) findViewById(R.id.imageView2);

        t1.setOnClickListener(new MyOnClickListener(0));
        t2.setOnClickListener(new MyOnClickListener(1));
    }
    
    /**
     * 头标点击监听
*/
    int mcurrentIndex=1;
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        public void onClick(View v) {
            mPager.setCurrentItem(index);
            if(index==0)
            {
            	imageSelect(1);
            	mcurrentIndex=1;
            }
            else if(index==1)
            {
            	imageSelect(2);
            	mcurrentIndex=2;
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
     * 初始化ViewPager
*/
    private void InitViewPager() {
        mPager = (ViewPager) findViewById(R.id.vPager);
        listViews = new ArrayList<View>();
        LayoutInflater mInflater = getLayoutInflater();
        music_page=mInflater.inflate(R.layout.music, null);
        video_page=mInflater.inflate(R.layout.video, null);
        listViews.add(music_page);
        listViews.add(video_page);
        mPager.setAdapter(new MyPagerAdapter(listViews));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }
    
    /**
     * ViewPager适配器
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
     * 初始化动画
*/
    private void InitImageView() {
    	ingcursor = (ImageView) findViewById(R.id.imageView4);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.tab_indicator)
                .getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 2 - bmpW) / 2;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        ingcursor.setImageMatrix(matrix);// 设置动画初始位置
    }
    
    /**
     * 页卡切换监听
*/
    public class MyOnPageChangeListener implements OnPageChangeListener {

        int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
        
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
            case 0:
                if (currIndex == 1) {
                    animation = new TranslateAnimation(one, 0, 0, 0);
                    imageSelect(1);
                    mcurrentIndex=1;
                } 
                break;
            case 1:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, one, 0, 0);
                    imageSelect(2);
                    mcurrentIndex=2;
                } 
                break;
            
            }
            currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
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
		Log.d("yao","VideoActivity.onKeyDown");
		 // 按下键盘上返回按钮    
	       if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {    
	   
	           new AlertDialog.Builder(this)    
	                   .setMessage("确定退出吗？")    
	                   .setNegativeButton("取消",    
	                           new DialogInterface.OnClickListener() {    
	                               public void onClick(DialogInterface dialog,    
	                                       int which) {    
	                               }    
	                           })    
	                   .setPositiveButton("确定",    
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
        Log.d("MediaActivity", "onStop");
        
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this,SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_NORMAL);
        Log.d("MediaActivity", "onRseume");
    }
    
    @Override 
    protected void onPause() { 
        super.onPause(); 
        Log.d("MediaActivity", "onPause");
    }

	public void onAccuracyChanged(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	int m=0;
	public void onSensorChanged(int sensor, float[] values) {
		// TODO Auto-generated method stub
		if((Math.abs(values[0])>12||Math.abs(values[1])>11||Math.abs(values[2])>12))
	      {
	          if(musicView.dragIndex!=8888&&mcurrentIndex==1)
	          {
	          	String isServerClient=MainTab.isServerClient;
	          	musicView.rotateAnimation();
	          	musicView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, musicView.getLeft()+5, musicView.getTop()+5, 0));
	          	musicView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, musicView.getLeft()+5, musicView.getTop()+5, 0));
	          	//Toast.makeText(getApplicationContext(), String.valueOf(musicView.dragIndex),Toast.LENGTH_SHORT).show();
	          	m++;
	          	this.back_player.start();
	          	if(isServerClient.equals("Server"))
	          		myServer.sendFile(musicFiles.get(musicView.dragIndex));
	          	if(isServerClient.equals("Client"))
	          		myClient.sendFile(musicFiles.get(musicView.dragIndex));
	          	musicView.dragIndex=8888;
	          }
	          
	          if(videoView.dragIndex!=8888&&mcurrentIndex==2)
	          {
	          	String isServerClient=MainTab.isServerClient;
	          	videoView.rotateAnimation();
	          	videoView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, videoView.getLeft()+5, videoView.getTop()+5, 0));
	          	videoView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, videoView.getLeft()+5, videoView.getTop()+5, 0));
	          	//Toast.makeText(getApplicationContext(), "第"+m+"次",Toast.LENGTH_SHORT).show();
	          	m++;
	          	this.back_player.start();
	          	if(isServerClient.equals("Server"))
	          		myServer.sendFile(videoFiles.get(videoView.dragIndex));
	          	if(isServerClient.equals("Client"))
	          		myClient.sendFile(videoFiles.get(videoView.dragIndex));
	          	videoView.dragIndex=8888;
	          }
	      }
	}
}
      