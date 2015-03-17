package com.zph.kuaichuan;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressWarnings("deprecation")
public class FileActivity extends Activity implements SensorListener{

	private TextView tvpath;
    private DragListView lvFiles;
    private Button btnParent;
 
    private MediaPlayer back_player;
	private MyServer myServer;
	private MyClient myClient;
	private SensorManager sensorManager;
    // 记录当前的父文件夹
    File currentParent;
    // 记录当前路径下的所有文件夹的文件数组
    File[] currentFiles;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Calendar cal = Calendar.getInstance();
    // 获取文件列表
    ArrayList<MyFile> fileList = new ArrayList<MyFile>();
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.file);  
        Vibrator vib = (Vibrator)this.getSystemService(Service.VIBRATOR_SERVICE);//震动服务
        lvFiles = (DragListView) this.findViewById(R.id.files);
        lvFiles.vib = vib;
        
        tvpath = (TextView) this.findViewById(R.id.tvpath);
        btnParent = (Button) this.findViewById(R.id.btnParent);
 
        // 获取系统的SDCard的目录
        File root = new File("/mnt/sdcard/");
        // 如果sd卡存在的话
        if (root.exists()) {
 
            currentParent = root;
            currentFiles = root.listFiles();
            
            
    		for (int i = 0; i < currentFiles.length; i++) {
    			File file = currentFiles[i];
    			MyFile fileInfo = new MyFile();
    			fileInfo.Name = file.getName();
    			fileInfo.IsDirectory = file.isDirectory();
    			fileInfo.Path = file.getPath();
    			fileInfo.Size = formetFileSize(file.length());
    			cal.setTimeInMillis(file.lastModified());
    			fileInfo.ModifiedTime=dateFormat.format(cal.getTime());
    			fileList.add(fileInfo);
    		}
    		Collections.sort(fileList, new FileComparator());
            // 使用当前目录下的全部文件、文件夹来填充ListView
            inflateListView(fileList);
            
          //甩**/
            this.back_player =  MediaPlayer.create(this,R.raw.dragthrow);
            myServer = (MyServer)getIntent().getSerializableExtra(MainTab.SERVER_KEY);
            myClient = (MyClient)getIntent().getSerializableExtra(MainTab.CLIEDNT_KEY);
          //获取传感器管理服务
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensorManager.registerListener(this,SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_NORMAL);
            
 
    }  
        
        lvFiles.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view,
                    int position, long id) {
                // 如果用户单击了文件，直接返回，不做任何处理
                if (!fileList.get(position).IsDirectory) {
                    // 也可自定义扩展打开这个文件等
                	startActivity(BitmapUtils.openFile(fileList.get(position)));
                    return;
                }
                // 获取用户点击的文件夹 下的所有文件
                File[] tem = new File(fileList.get(position).Path).listFiles();
                if (tem == null || tem.length == 0) {
 
                    Toast.makeText(FileActivity.this,
                            "当前路径不可访问或者该路径下没有文件", Toast.LENGTH_LONG).show();
                } else {
                    // 获取用户单击的列表项对应的文件夹，设为当前的父文件夹
                    currentParent = currentFiles[position];
                    // 保存当前的父文件夹内的全部文件和文件夹
                    currentFiles = tem;
                    fileList = new ArrayList<MyFile>();
            		// 获取文件列表
            		for (int i = 0; i < currentFiles.length; i++) {
            			File file = currentFiles[i];
            			MyFile fileInfo = new MyFile();
            			fileInfo.Name = file.getName();
            			fileInfo.IsDirectory = file.isDirectory();
            			fileInfo.Path = file.getPath();
            			fileInfo.Size = formetFileSize(file.length());
            			cal.setTimeInMillis(file.lastModified());
            			fileInfo.ModifiedTime=dateFormat.format(cal.getTime());
            			fileList.add(fileInfo);
            		}
            		Collections.sort(fileList, new FileComparator());
                    // 再次更新ListView
                    inflateListView(fileList);
                }
 
            }

			
        });
 
        // 获取上一级目录
        btnParent.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View v) {
 
                try {
 
                    if (!currentParent.getCanonicalPath().equals("/mnt/sdcard")) {
 
                        // 获取上一级目录
                        currentParent = currentParent.getParentFile();
                        // 列出当前目录下的所有文件
                        currentFiles = currentParent.listFiles();
                        
                        fileList = new ArrayList<MyFile>();
                		// 获取文件列表
                		for (int i = 0; i < currentFiles.length; i++) {
                			File file = currentFiles[i];
                			MyFile fileInfo = new MyFile();
                			fileInfo.Name = file.getName();
                			fileInfo.IsDirectory = file.isDirectory();
                			fileInfo.Path = file.getPath();
                			fileInfo.Size = formetFileSize(file.length());
                			cal.setTimeInMillis(file.lastModified());
                			fileInfo.ModifiedTime=dateFormat.format(cal.getTime());
                			fileList.add(fileInfo);
                		}
                		Collections.sort(fileList, new FileComparator());
                        // 再次更新ListView
                        inflateListView(fileList);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
 
            }
        });
 
    }
 
    /**
     * 根据文件夹填充ListView
     *
     * @param files
     */
    private void inflateListView(ArrayList<MyFile> fileList) {
 
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
 
        for (int i = 0; i < fileList.size(); i++) {
 
            Map<String, Object> listItem = new HashMap<String, Object>();
            String mPathSuffix=getSuffix(fileList.get(i).Path);
            if (fileList.get(i).IsDirectory) 
            {
                // 如果是文件夹就显示的图片为文件夹的图片
                listItem.put("icon", R.drawable.file_icon_folder);
            } 
            else if(mPathSuffix.equals(".apk"))
            {
                listItem.put("icon", R.drawable.file_icon_apk);
            }
            else if(mPathSuffix.equals(".mid"))
            {
                listItem.put("icon", R.drawable.file_icon_mid);
            }
            else if(mPathSuffix.equals(".mp3"))
            {
                listItem.put("icon", R.drawable.file_icon_mp3);
            }
            else if(mPathSuffix.equals(".offcie"))
            {
                listItem.put("icon", R.drawable.file_icon_office);
            }
            else if(mPathSuffix.equals(".pdf"))
            {
                listItem.put("icon", R.drawable.file_icon_pdf);
            }
            else if(mPathSuffix.equals(".png"))
            {
                listItem.put("icon", R.drawable.file_icon_picture);
            }
            else if(mPathSuffix.equals(".jpg"))
            {
                listItem.put("icon", R.drawable.file_icon_picture);
            }
            else if(mPathSuffix.equals(".rar"))
            {
                listItem.put("icon", R.drawable.file_icon_rar);
            }
            else if(mPathSuffix.equals(".theme"))
            {
                listItem.put("icon", R.drawable.file_icon_theme);
            }
            else if(mPathSuffix.equals(".txt"))
            {
                listItem.put("icon", R.drawable.file_icon_txt);
            }
            else if(mPathSuffix.equals(".3gp"))
            {
                listItem.put("icon", R.drawable.file_icon_video);
            }
            else if(mPathSuffix.equals(".mp4"))
            {
                listItem.put("icon", R.drawable.file_icon_video);
            }
            else if(mPathSuffix.equals(".flv"))
            {
                listItem.put("icon", R.drawable.file_icon_video);
            }
            else if(mPathSuffix.equals(".wav"))
            {
                listItem.put("icon", R.drawable.file_icon_wav);
            }
            else if(mPathSuffix.equals(".wma"))
            {
                listItem.put("icon", R.drawable.file_icon_wma);
            }
            else if(mPathSuffix.equals(".zip"))
            {
                listItem.put("icon", R.drawable.file_icon_zip);
            }
            else if(mPathSuffix.equals("isfile"))
            	listItem.put("icon", R.drawable.file_icon_default);
            else
            	listItem.put("icon", R.drawable.file_icon_default);
            
            listItem.put("filename",""+fileList.get(i).Name);
            if(fileList.get(i).IsDirectory)
            	listItem.put("modify",""+fileList.get(i).ModifiedTime);
            else
            	listItem.put("modify",""+fileList.get(i).ModifiedTime+"  " + fileList.get(i).Size);
            listItems.add(listItem);
        }
 
        // 定义一个SimpleAdapter
        SimpleAdapter adapter = new SimpleAdapter(
        		FileActivity.this, listItems, R.layout.shared_file_browser_item,
                new String[] { "filename", "icon", "modify" }, new int[] {
                        R.id.shared_file_name, R.id.shared_file_icon, R.id.shared_file_size });
 
        // 填充数据集
        lvFiles.setAdapter(adapter);
 
        try {
        	if(fileList.size()!=0)
            tvpath.setText(getParentPath(fileList.get(0).Path));
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
    
    private String getSuffix(String m)//获取扩展名，带点
	{
		int startindex=m.indexOf(".", m.length()-4);
		String s=m.toLowerCase();
		if(startindex!=-1)
			return s.substring(startindex, s.length());
		else
			return "isfile";
	}
    
    private String getParentPath(String m)//上级目录
    {
    	int endindex=m.lastIndexOf("/");

		return m.substring(0, endindex);
    }
    /** 转换文件大小 **/
	public static String formetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = fileS + " B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + " K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + " M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + " G";
		}
		return fileSizeString;
	}
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("yao","FileActivity.onKeyDown");
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
        Log.d("FileActivity", "onStop");
        
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this,SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_NORMAL);
        Log.d("FileActivity", "onRseume");
    }
    
    @Override 
    protected void onPause() { 
        super.onPause(); 
        Log.d("FileActivity", "onPause");
    }

	public void onAccuracyChanged(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	int m=0;
	public void onSensorChanged(int sensor, float[] values) {
		// TODO Auto-generated method stub
		 if((Math.abs(values[0])>12||Math.abs(values[1])>11||Math.abs(values[2])>12))
	        {
	            if(lvFiles.dragIndex!=8888&&(!fileList.get(lvFiles.dragIndex).IsDirectory))
	            {
	            	String isServerClient=MainTab.isServerClient;
	            	lvFiles.rotateAnimation();
	            	lvFiles.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, lvFiles.getLeft()+5, lvFiles.getTop()+5, 0));
	            	lvFiles.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, lvFiles.getLeft()+5, lvFiles.getTop()+5, 0));
	            	//Toast.makeText(getApplicationContext(), "第"+m+"次",Toast.LENGTH_SHORT).show();
	            	m++;
	            	this.back_player.start();
	            	if(isServerClient.equals("Server"))
	            		myServer.sendFile(fileList.get(lvFiles.dragIndex));
	            	if(isServerClient.equals("Client"))
	            		myClient.sendFile(fileList.get(lvFiles.dragIndex));
	            	lvFiles.dragIndex=8888;
	            }
	        }
	}
}
