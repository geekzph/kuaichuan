package com.zph.kuaichuan;

import java.io.File;
import android.media.MediaPlayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements SensorListener{
	
	DragGridViews gridView=null;
	
	private String texts[] = null;
	private Drawable images[] = null;
	private List<MyFile> myFiles = new ArrayList<MyFile>();
	
	private SensorManager sensorManager;
	
	MyWifi myWifi=null;
    public MyClient myClient=null;
    private MyServer myServer=null;
    private static final float BLANK = -999;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**����������View�ģ����Դ������е����β��ֱ����غ������Ȼ��Ч*/  
	    requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.activity_main);
        myServer = (MyServer)getIntent().getSerializableExtra(MainTab.SERVER_KEY);
        myClient = (MyClient)getIntent().getSerializableExtra(MainTab.CLIEDNT_KEY);

        //FindAllApk();
        myFiles=SplashActivity.myFiles;
        //=========================================
        texts=new String[myFiles.size()];
        images=new Drawable[myFiles.size()];
        for(int i=0;i<myFiles.size();i++)
        {
        	images[i]=myFiles.get(i).apk_icon;
        	texts[i]=myFiles.get(i).Name;
        }
        
        Vibrator vib = (Vibrator)this.getSystemService(Service.VIBRATOR_SERVICE);
        // ���GridView
        gridView = (DragGridViews) findViewById(R.id.gridView1);
        gridView.vib=vib;
        gridView.windowWidth=this.getWindow().getWindowManager().getDefaultDisplay().getWidth();
        gridView.windowHeight=this.getWindow().getWindowManager().getDefaultDisplay().getHeight(); 
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, fillMap(),
        			R.layout.itemview, 
        			new String[] { "imageView", "imageTitle" },
        			new int[] { R.id.imageView, R.id.imageTitle });
        gridView.setAdapter(simpleAdapter);
        simpleAdapter.setViewBinder(new ViewBinder()
        {
        	public boolean setViewValue(View view,Object data,String textRepresentation)
        	{
        		if(view instanceof ImageView && data instanceof Drawable)
        		{
        			ImageView iv=(ImageView)view;
        			//iv.setMaxHeight(70);
        			//iv.setMaxWidth(70);
        			//iv.setAdjustViewBounds(true);
        	        iv.setImageDrawable((Drawable)data);
        	        return true;
        	    }
        	    else return false;
        	}
        });
        
        
  
        //˦**/
        this.back_player =  MediaPlayer.create(this,R.raw.dragthrow);
        
      //��ȡ�������������
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_NORMAL);
        
        Log.d("MainActivity", "created");
    }

    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("yao","MainActivity.onKeyDown");
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
        Log.d("MainActivity", "onStop");
        
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this,SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_NORMAL);
        Log.d("MainActivity", "onRseume");
    }
    
    @Override 
    protected void onPause() { 
        super.onPause(); 
        Log.d("MainActivity", "onPause");
    }
    
    //
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
        
    }

    public void onSensorChanged(SensorEvent e) {
       
    }  
    
    public float getMedian(float[] values) 
    {
        float[] tmp = values.clone();
        Arrays.sort(tmp);
        int len = tmp.length;
        int first = 0;
        for (int i = 0; i < len; i++) {
          first = i;
          if(tmp[i] != BLANK) break;
        }
        return tmp[(len - first) / 2 + first];
      }

    
    
    public List<Map<String, Object>> fillMap() {
     	  List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
     	  for (int i = 0, j = texts.length; i < j; i++) {
     	   Map<String, Object> map = new HashMap<String, Object>();
     	   map.put("imageView", images[i]);
     	   map.put("imageTitle", texts[i]);
     	   list.add(map);
     	  }
     	  return list;
     	 }
    
    public void FindAllApk()
    {
    	 //����apk
        //=========================================
        PackageManager pckMan = getPackageManager();
        List<PackageInfo> packs = pckMan.getInstalledPackages(0);
        int count = packs.size();
        ApplicationInfo appInfo;
        String ApkPath="";
        for(int i = 0; i < count; i++) { 
        	MyFile myFile = new MyFile();
        	PackageInfo p = packs.get(i);
            appInfo = p.applicationInfo;
            //�жϸ�������Ƿ���/data/appĿ¼��
            File f1 = new File( "/data/app/" +  p.packageName + "-1.apk");
            File f2 = new File( "/data/app/" +  p.packageName + "-2.apk");
            File f3 = new File( "/mnt/asec/" +  p.packageName + "-1"+"/pkg.apk");
            File f4 = new File( "/mnt/asec/" +  p.packageName + "-2"+"/pkg.apk");
            if(f1.exists()) 
            {
	            ApkPath="/data/app/" +  p.packageName + "-1.apk";
	            /**��ȡapk��ͼ�� */
				appInfo.sourceDir = ApkPath;
				appInfo.publicSourceDir = ApkPath;
				Drawable apk_icon = appInfo.loadIcon(pckMan);
				//apk_icon.setBounds(0, 0, 10, 10);
				myFile.apk_icon=apk_icon;
				/** apk�ľ���·�� */
				myFile.Path=ApkPath;
				/** apk������ */
				myFile.Name=p.applicationInfo.loadLabel(pckMan).toString();
				myFiles.add(myFile);
            }
            if(f2.exists()) 
            {
            	ApkPath="/data/app/" +  p.packageName + "-2.apk";
	            /**��ȡapk��ͼ�� */
				appInfo.sourceDir = ApkPath;
				appInfo.publicSourceDir = ApkPath;
				Drawable apk_icon = appInfo.loadIcon(pckMan);
				//apk_icon.setBounds(0, 0, 10, 10);
				myFile.apk_icon=apk_icon;
				/** apk�ľ���·�� */
				myFile.Path=ApkPath;
				/** apk������ */
				myFile.Name=p.applicationInfo.loadLabel(pckMan).toString();
				myFiles.add(myFile);
            }
            if(f3.exists()) 
            {
	            ApkPath="/mnt/asec/" +  p.packageName + "-1"+"/pkg.apk";
	            /**��ȡapk��ͼ�� */
				appInfo.sourceDir = ApkPath;
				appInfo.publicSourceDir = ApkPath;
				Drawable apk_icon = appInfo.loadIcon(pckMan);
				//apk_icon.setBounds(0, 0, 10, 10);
				myFile.apk_icon=apk_icon;
				/** apk�ľ���·�� */
				myFile.Path=ApkPath;
				/** apk������ */
				myFile.Name=p.applicationInfo.loadLabel(pckMan).toString();
				myFiles.add(myFile);
            }
            if(f4.exists()) 
            {
            	ApkPath="/mnt/asec/" +  p.packageName+ "-2"+"/pkg.apk";
	            /**��ȡapk��ͼ�� */
				appInfo.sourceDir = ApkPath;
				appInfo.publicSourceDir = ApkPath;
				Drawable apk_icon = appInfo.loadIcon(pckMan);
				//apk_icon.setBounds(0, 0, 10, 10);
				myFile.apk_icon=apk_icon;
				/** apk�ľ���·�� */
				myFile.Path=ApkPath;
				/** apk������ */
				myFile.Name=p.applicationInfo.loadLabel(pckMan).toString();
				myFiles.add(myFile);
            }
        }
    }

    static final String InitWifi="InitWifi";
    
    
	public void onAccuracyChanged(int sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	int m=0;
	int shakecount=0;
	private MediaPlayer back_player;
	
	
	public void onSensorChanged(int sensor, float[] values) {
		// TODO Auto-generated method stub
		
	        if((Math.abs(values[0])>12||Math.abs(values[1])>11||Math.abs(values[2])>12))
	        {
	            if(gridView.dragIndex!=8888)
	            {
	            	String isServerClient=MainTab.isServerClient;
	            	gridView.rotateAnimation();
	            	gridView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, gridView.getLeft()+5, gridView.getTop()+5, 0));
	            	gridView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, gridView.getLeft()+5, gridView.getTop()+5, 0));
	            	//Toast.makeText(getApplicationContext(), "��"+m+"��",Toast.LENGTH_SHORT).show();
	            	m++;
	            	this.back_player.start();
	            	if(isServerClient.equals("Server"))
	            		myServer.sendFile(myFiles.get(gridView.dragIndex));
	            	if(isServerClient.equals("Client"))
	            		myClient.sendFile(myFiles.get(gridView.dragIndex));
	            	gridView.dragIndex=8888;
	            }
	        }
		
	}
	
}


