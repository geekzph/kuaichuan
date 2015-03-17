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
	   
	 private ViewPager mPager;//ҳ������
	 private List<View> listViews; // Tabҳ���б�
	 private ImageView ingcursor;// ����ͼƬ
	 private ImageView t1, t2;// ҳ��ͷ��
	 private int offset = 0;// ����ͼƬƫ����
	 private int currIndex = 0;// ��ǰҳ�����
	 private int bmpW;// ����ͼƬ���

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
        //��������
        musicView=(DragGridView)music_page.findViewById(R.id.MusicGrid);
        musicView.vib=vib;
        //�������ַ���;
        musicBitmaps=SplashActivity.musicBitmaps;
        musicName=SplashActivity.musicName;
        musicFiles=SplashActivity.musicFiles;
        musicView.setAdapter(new MusicAdapter(this));
        //������Ƶ
        videoView=(DragGridView)video_page.findViewById(R.id.VideoGrid);
        videoView.vib=vib;
        //������Ƶ����ͼ;
        videoBitmaps=SplashActivity.videoBitmaps;
        videoDuration=SplashActivity.videoDuration;
        videoFiles=SplashActivity.videoFiles;
        videoView.setAdapter(new VideoAdapter(this));
        
      //˦**/
        this.back_player =  MediaPlayer.create(this,R.raw.dragthrow);
        myServer = (MyServer)getIntent().getSerializableExtra(MainTab.SERVER_KEY);
        myClient = (MyClient)getIntent().getSerializableExtra(MainTab.CLIEDNT_KEY);
      //��ȡ�������������
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_NORMAL);
        
        
        Log.d("MediaActivity", "created");
        
    }  
    
    //=================music�߼�����====================================================================
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
    
    //========================music�߼�����===============================
    
    //===============
    //========================video�߼�����===============================
    private List<Bitmap> videoBitmaps = new ArrayList<Bitmap>();
    private List<String> videoDuration=new ArrayList<String>();
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");//��ʼ��Formatter��ת����ʽ��
    
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
    
    //========================video�߼�����==============================
    /**
     * ��ʼ��ͷ��
*/
    private void InitTextView() 
    {
        t1 = (ImageView) findViewById(R.id.imageView1);
        t2 = (ImageView) findViewById(R.id.imageView2);

        t1.setOnClickListener(new MyOnClickListener(0));
        t2.setOnClickListener(new MyOnClickListener(1));
    }
    
    /**
     * ͷ��������
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
     * ��ʼ��ViewPager
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
		Log.d("yao","VideoActivity.onKeyDown");
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
	          	//Toast.makeText(getApplicationContext(), "��"+m+"��",Toast.LENGTH_SHORT).show();
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
      