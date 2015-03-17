package com.zph.kuaichuan;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class HistoryActivity extends Activity {    
	
	private ListView historyList;
	
	HistoryInfo m1=new HistoryInfo();
    HistoryInfo m2=new HistoryInfo();
    
    HistoryAdapter mHistoryAdapter;
    private Handler mHandler = null;
    
    private MyServer myServer;
	private MyClient myClient;
	private Context mContext;
	
	private int savedId=0;
	public List<HistoryInfo> mHisInfo = new ArrayList<HistoryInfo>();
	public List<HistoryInfo> mSavedHisInfo = new ArrayList<HistoryInfo>();
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.history);
        myServer = (MyServer)getIntent().getSerializableExtra(MainTab.SERVER_KEY);
        myClient = (MyClient)getIntent().getSerializableExtra(MainTab.CLIEDNT_KEY);
        mContext=this;
        shwoHistoryList();
        historyList=(ListView)findViewById(R.id.history_listview);
        
        mHistoryAdapter=new HistoryAdapter(mContext);
        historyList.setAdapter(mHistoryAdapter);
        historyList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view,
                    int position, long id) {
            	startActivity(BitmapUtils.openFile(mHisInfo.get(position)));
            }

			
        });
        mHandler = new Handler()
        {
            public void handleMessage(Message msg)
            {
            		
                    switch (msg.what) {
                    case 6:
                    	int id=msg.getData().getInt("id")+savedId;
                        String fileName = msg.getData().getString("fileName");
                        String filePath = msg.getData().getString("savePath");
                        Long fileSize = msg.getData().getLong("fileSize");
                        HistoryInfo mm=new HistoryInfo();
                        mm.fileName=fileName;
                        mm.fileSize=formetFileSize(fileSize);
                        mm.Path=filePath;
                        mm.icon=null;
                        mHisInfo.add(mm);
                        mHistoryAdapter=new HistoryAdapter(mContext);
                        historyList.setAdapter(mHistoryAdapter);
                        historyList.setSelection(id);
                        Log.d("HistoryActivity", "handleMessage");
                        break;
                    case 7:
                        int mid = msg.getData().getInt("id")+savedId;
                        int current = msg.getData().getInt("current_progress");
                        String speed = msg.getData().getString("speed");
                        String filetype=msg.getData().getString("filetype");
                        Drawable icon=null;
						if(filetype.equals("pic"))
                        	icon=getResources().getDrawable(R.drawable.messenger_icon_image);
                    	else if(filetype.equals("audio"))
                        	icon=getResources().getDrawable(R.drawable.messenger_icon_audio);
                    	else if(filetype.equals("video"))
                        	icon=getResources().getDrawable(R.drawable.messenger_icon_video);
                        if(current==100&&filetype.equals("apk"))
                        	icon=getApkIcon(mContext,"/mnt/sdcard/easy share/app/"+mHisInfo.get(mid).fileName);
							
                        mHisInfo.get(mid).icon=icon;
                		updateIcon(mid,icon);
                        updateProgress(mid, current,speed);
                        Log.d("HistoryActivity", "handleMessage");
                        break;
                    }
                    super.handleMessage(msg);
                                                           
                
            }
        };
        myClient.mHandler=mHandler;
        myServer.mHandler=mHandler;

        Log.d("HistoryActivity", "created");
    }  
    private void shwoHistoryList()
    {
    	File hList= new File ("/mnt/sdcard/easy share/app");
    	File[] currentFiles = hList.listFiles();
		for (int i = 0; i < currentFiles.length; i++) 
		{
			HistoryInfo mm=new HistoryInfo();
			mm.fileName=currentFiles[i].getName();
			mm.fileSize=formetFileSize(currentFiles[i].length());
			mm.current_progress=100;
			mm.icon=getApkIcon(mContext,"/mnt/sdcard/easy share/app/"+currentFiles[i].getName());
			mm.Path = "/mnt/sdcard/easy share/app/" + mm.fileName;
			mHisInfo.add(mm);
			savedId++;
		}
		hList= new File ("/mnt/sdcard/easy share/picture");
		currentFiles = hList.listFiles();
		for (int i = 0; i < currentFiles.length; i++) 
		{
			HistoryInfo mm=new HistoryInfo();
			mm.fileName=currentFiles[i].getName();
			mm.fileSize=formetFileSize(currentFiles[i].length());
			mm.current_progress=100;
			mm.icon=getResources().getDrawable(R.drawable.messenger_icon_image);
			mm.Path = "/mnt/sdcard/easy share/picture/" + mm.fileName;
			mHisInfo.add(mm);
			savedId++;
		}
		hList= new File ("/mnt/sdcard/easy share/audio");
		currentFiles = hList.listFiles();
		for (int i = 0; i < currentFiles.length; i++) 
		{
			HistoryInfo mm=new HistoryInfo();
			mm.fileName=currentFiles[i].getName();
			mm.fileSize=formetFileSize(currentFiles[i].length());
			mm.current_progress=100;
			mm.icon=getResources().getDrawable(R.drawable.messenger_icon_audio);
			mm.Path = "/mnt/sdcard/easy share/audio/" + mm.fileName;
			mHisInfo.add(mm);
			savedId++;
		}
		hList= new File ("/mnt/sdcard/easy share/video");
		currentFiles = hList.listFiles();
		for (int i = 0; i < currentFiles.length; i++) 
		{
			HistoryInfo mm=new HistoryInfo();
			mm.fileName=currentFiles[i].getName();
			mm.fileSize=formetFileSize(currentFiles[i].length());
			mm.current_progress=100;
			mm.icon=getResources().getDrawable(R.drawable.messenger_icon_video);
			mm.Path = "/mnt/sdcard/easy share/video/" + mm.fileName;
			mHisInfo.add(mm);
			savedId++;
		}
    }
    public class HistoryAdapter extends BaseAdapter {
        private Context mContext;


    	 private class GridHolder {  
    	         TextView history_item_file_name;
    	         TextView history_item_file_size;
    	         TextView history_item_file_speed;
    	         ProgressBar history_progressbar_normal;
    	         ImageView icon;

    	     }  
    	   


        public HistoryAdapter(Context c) {
            mContext = c;

        	mInflater = (LayoutInflater) mContext  
        	                 .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  


        }

        public int getCount() {
            //return cameraBitmaps.size();
        	return mHisInfo.size();
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
        	 
        		 convertView = mInflater.inflate(R.layout.history_progressbar, null);     
        	     holder = new GridHolder();  
        	     holder.history_item_file_name = (TextView)convertView.findViewById(R.id.pro_name); 
        	     holder.history_item_file_size = (TextView)convertView.findViewById(R.id.pro_filesize);
        	     holder.history_item_file_speed = (TextView)convertView.findViewById(R.id.pro_speed);
        	     holder.history_progressbar_normal= (ProgressBar) convertView.findViewById(R.id.progressBar1);
        	     holder.icon=(ImageView)convertView.findViewById(R.id.icon_image);
        	     convertView.setTag(holder);     
        	   
             }else
             {  
        	     holder = (GridHolder) convertView.getTag();       
        	 }  
        	 holder.history_item_file_name.setText(mHisInfo.get(position).fileName);
        	 holder.history_item_file_size.setText(mHisInfo.get(position).fileSize);
        	 if(mHisInfo.get(position).speed!=null)
        		 holder.history_item_file_speed.setText(mHisInfo.get(position).speed+"kb");
        	 else
        		 holder.history_item_file_speed.setText("");
        	 holder.history_progressbar_normal.setProgress(mHisInfo.get(position).current_progress);
        	 if(mHisInfo.get(position).icon!=null)
        		 holder.icon.setImageDrawable(mHisInfo.get(position).icon);
        	 else
        		 holder.icon.setImageDrawable(getResources().getDrawable(R.drawable.messenger_icon_apk));
        	 Log.d("HistoryActivity", "adapter");
        	         return convertView;  
        }
        
     // 改变进度，postion就是要改的那个进度
        public void changeProgress() {
            notifyDataSetChanged();
        }

    }
    
     private void updateProgress(int id, int currentPos,String speed) {
    	 try
    	 {
    		 mHisInfo.get(id).current_progress=currentPos;
    		 mHisInfo.get(id).speed=speed;
        	 mHistoryAdapter.changeProgress();
    	 }
    	 catch(IndexOutOfBoundsException ex)
    	 {Log.d("HistoryActivity", "updateProgress哟长");}
        	
        }
     
     private void updateIcon(int id,Drawable icon)
     {
    	 try
    	 {	
    		 mHisInfo.get(id).icon=icon;
    		 mHistoryAdapter.changeProgress();
    	 }
    	 catch(IndexOutOfBoundsException ex)
    	 {Log.d("HistoryActivity", "updateProgress哟长");}
    	
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
 	
 	 public Drawable getApkIcon(Context context, String apkPath) {
         PackageManager pm = context.getPackageManager();
         PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                 PackageManager.GET_ACTIVITIES);
         if (info != null) {
             ApplicationInfo appInfo = info.applicationInfo;
             appInfo.sourceDir = apkPath;
             appInfo.publicSourceDir = apkPath;
             try {
                 return appInfo.loadIcon(pm);
             } catch (OutOfMemoryError e) {
                 Log.e("ApkIconLoader", e.toString());
             }
         }
         return null;
     }
 	 
 	public String getSuffix(String m)//呆点
	{
		int startindex=m.lastIndexOf(".");
		return m.substring(startindex, m.length());
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
        Log.d("HistoryActivity", "onStop");
        
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        Log.d("HistoryActivity", "onRseume");
    }
    
    @Override 
    protected void onPause() { 
        super.onPause(); 
        Log.d("HistoryActivity", "onPause");
    }
      
   
      
}  