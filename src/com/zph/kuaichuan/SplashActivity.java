package com.zph.kuaichuan;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity{
	
	private Cursor cursor;
	private Handler mHandler;
	private MyClient myClient;
	private MyWifi myWifi;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效*/  
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /**全屏设置，隐藏窗口所有装饰*/  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        WindowManager.LayoutParams.FLAG_FULLSCREEN);  
          
        setContentView(R.layout.splash);
        myFiles = new ArrayList<MyFile>();
        imgBitmaps = new ArrayList<Bitmap>();
        cameraBitmaps = new ArrayList<Bitmap>();
        camFiles = new ArrayList<MyFile>();
        
        musicBitmaps = new ArrayList<Bitmap>();
        musicName=new ArrayList<String>();
        musicFiles = new ArrayList<MyFile>();
        imgFiles = new ArrayList<MyFile>();
        
        videoBitmaps = new ArrayList<Bitmap>();
        videoDuration=new ArrayList<String>();
        videoFiles = new ArrayList<MyFile>();
        myClient=new MyClient((WifiManager)this.getSystemService(Context.WIFI_SERVICE));
        myWifi=new MyWifi((WifiManager)this.getSystemService(Context.WIFI_SERVICE));
        splashhandler splashThread=new splashhandler();
        splashThread.start();
        mHandler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                switch(msg.what)
                {
	                case 99://资源加载完毕
	                	startActivity(new Intent(getApplication(),MainTab.class));
	                    SplashActivity.this.finish();
	                    break;
                }
            }
        };
        
    }
    class splashhandler extends Thread{

        public void run() {
        	myWifi.stopAP();
        	myClient.stiopWifi();
        	FindAllApk();
            showImageThumbnails();
            showMusicThumbnails();
            showVideoThumbnails();
            Message message = new Message();  
            message.what = 99;
            message.obj ="已成功连接";
            //发送消息
            mHandler.sendMessage(message);
        }
        
    }
    //========================应用加载================================
    public static List<MyFile> myFiles;
    private void FindAllApk()
    {
    	 //搜索apk
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
            //判断该软件包是否在/data/app目录下
            File f1 = new File( "/data/app/" +  p.packageName + "-1.apk");
            File f2 = new File( "/data/app/" +  p.packageName + "-2.apk");
            File f3 = new File( "/mnt/asec/" +  p.packageName + "-1"+"/pkg.apk");
            File f4 = new File( "/mnt/asec/" +  p.packageName + "-2"+"/pkg.apk");
            if(f1.exists()) 
            {
	            ApkPath="/data/app/" +  p.packageName + "-1.apk";
	            /**获取apk的图标 */
				appInfo.sourceDir = ApkPath;
				appInfo.publicSourceDir = ApkPath;
				Drawable apk_icon = appInfo.loadIcon(pckMan);
				//apk_icon.setBounds(0, 0, 10, 10);
				myFile.apk_icon=apk_icon;
				/** apk的绝对路劲 */
				myFile.Path=ApkPath;
				/** apk的名字 */
				myFile.Name=p.applicationInfo.loadLabel(pckMan).toString();
				myFiles.add(myFile);
            }
            if(f2.exists()) 
            {
            	ApkPath="/data/app/" +  p.packageName + "-2.apk";
	            /**获取apk的图标 */
				appInfo.sourceDir = ApkPath;
				appInfo.publicSourceDir = ApkPath;
				Drawable apk_icon = appInfo.loadIcon(pckMan);
				//apk_icon.setBounds(0, 0, 10, 10);
				myFile.apk_icon=apk_icon;
				/** apk的绝对路劲 */
				myFile.Path=ApkPath;
				/** apk的名字 */
				myFile.Name=p.applicationInfo.loadLabel(pckMan).toString();
				myFiles.add(myFile);
            }
            if(f3.exists()) 
            {
	            ApkPath="/mnt/asec/" +  p.packageName + "-1"+"/pkg.apk";
	            /**获取apk的图标 */
				appInfo.sourceDir = ApkPath;
				appInfo.publicSourceDir = ApkPath;
				Drawable apk_icon = appInfo.loadIcon(pckMan);
				//apk_icon.setBounds(0, 0, 10, 10);
				myFile.apk_icon=apk_icon;
				/** apk的绝对路劲 */
				myFile.Path=ApkPath;
				/** apk的名字 */
				myFile.Name=p.applicationInfo.loadLabel(pckMan).toString();
				myFiles.add(myFile);
            }
            if(f4.exists()) 
            {
            	ApkPath="/mnt/asec/" +  p.packageName+ "-2"+"/pkg.apk";
	            /**获取apk的图标 */
				appInfo.sourceDir = ApkPath;
				appInfo.publicSourceDir = ApkPath;
				Drawable apk_icon = appInfo.loadIcon(pckMan);
				//apk_icon.setBounds(0, 0, 10, 10);
				myFile.apk_icon=apk_icon;
				/** apk的绝对路劲 */
				myFile.Path=ApkPath;
				/** apk的名字 */
				myFile.Name=p.applicationInfo.loadLabel(pckMan).toString();
				myFiles.add(myFile);
            }
        }
    }
    //========================图片加载================================
    public static List<Bitmap> imgBitmaps;
    public static List<Bitmap> cameraBitmaps;
    public static List<MyFile> camFiles;
    public static List<MyFile> imgFiles;
    private void showImageThumbnails(){  
        
        cursor = BitmapUtils.queryImageThumbnails(this);  
        if(cursor.moveToFirst()){  
  
            for(int i=0; i<cursor.getCount();i++){  
                cursor.moveToPosition(i);
                String currPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));  
                int m = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID)); 
                String picPath = BitmapUtils.queryImageById(this,m);
                if(picPath!=null)
                {
             	   Bitmap b;
             	   try
             	   {
 	               b = BitmapUtils.decodeBitmap(currPath,100,100);
             	   }
             	   catch(NullPointerException ex)
 	               {
 	            	   continue;
 	               } 
 	               String picname=getPicFileName(picPath);
 	               MyFile myFile = new MyFile();
 	               myFile.Name=picname;
 	               myFile.Path=picPath;
 	               try
 	               {
 		               if(picPath.substring(0,16).equals("/mnt/sdcard/DCIM"))
 		               {
 		            	   cameraBitmaps.add(b);
 		            	   camFiles.add(myFile);
 		               }
 		               else
 		               {
 		            	   imgBitmaps.add(b); 
 		            	   imgFiles.add(myFile);
 		               }
 	               }
 	               catch(NullPointerException ex)
 	               {
 	            	   imgBitmaps.add(b); 
 	            	   imgFiles.add(myFile);
 	               } 
                }
                
            }  
            
        }  
        Log.d("PictureActivity", "showImageThumbnails");
    }
    
    private String getPicFileName(String picPath)
    {
 	   int startindex = -1;
 	   if(picPath!=null)
 	   startindex=picPath.lastIndexOf("/");
 	   if(startindex!=-1)
 			return picPath.substring(startindex+1, picPath.length()-4);
 		else
 			return "未命名";
    }

    //==============================图片加载=========================================
    public static List<Bitmap> musicBitmaps;
    public static List<String> musicName;
    public static List<MyFile> musicFiles;
    
    private void showMusicThumbnails(){  
        
        cursor = BitmapUtils.queryMusicThumbnails(this);  
        if(cursor.moveToFirst()){  
            
            //thumbIds = new int[cursor.getCount()];  
            for(int i=0; i<cursor.getCount();i++){  
                cursor.moveToPosition(i);
                Long albumid = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ID));  
                Long songid = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)); 
                String songname=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String imageid=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));//路径
                Bitmap b;
                try
          	   	{
                	b= getArtwork(this, songid, albumid,true); 
          	   	}
                catch(NullPointerException ex)
	            {
	            	continue;
	            } 
                musicBitmaps.add(b);
                musicName.add(songname);
                MyFile myFile = new MyFile();
                myFile.Name=songname;
                myFile.Path=imageid;
                musicFiles.add(myFile);
            }  
            
        }  
        
    }
    
    public static Bitmap getArtwork(Context context, long song_id, long album_id,
            boolean allowdefault) {
        if (album_id < 0) {
            // This is something that is not in the database, so get the album art directly
            // from the file.
            if (song_id >= 0) {
                Bitmap bm = getArtworkFromFile(context, song_id, -1);
                if (bm != null) {
                    return bm;
                }
            }
            if (allowdefault) {
                return getDefaultArtwork(context);
            }
            return null;
        }
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, sBitmapOptions);
            } catch (FileNotFoundException ex) {
                // The album art thumbnail does not actually exist. Maybe the user deleted it, or
                // maybe it never existed to begin with.
                Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                if (bm != null) {
                    if (bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                        if (bm == null && allowdefault) {
                            return getDefaultArtwork(context);
                        }
                    }
                } else if (allowdefault) {
                    bm = getDefaultArtwork(context);
                }
                return bm;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                }
            }
        }
        
        return null;
    }
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    private static Bitmap mCachedBit = null;
    private static Bitmap getArtworkFromFile(Context context, long songid, long albumid) {
        Bitmap bm = null;
        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try {
            if (albumid < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            }
        } catch (FileNotFoundException ex) {
 
        }
        if (bm != null) {
            mCachedBit = bm;
        }
        return bm;
    }
    
    private static Bitmap getDefaultArtwork(Context context) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;        
        return BitmapFactory.decodeStream(
                context.getResources().openRawResource(R.drawable.messenger_icon_audio), null, opts);               
    }
    
    
    
    //===============================video加载============================================
    public static List<Bitmap> videoBitmaps;
    public static List<String> videoDuration;
    public static List<MyFile> videoFiles;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式。
    private void showVideoThumbnails(){  
        
        cursor = BitmapUtils.queryVideoThumbnails(this);  
        if(cursor.moveToFirst()){  
            for(int i=0; i<cursor.getCount();i++){  
            	cursor.moveToPosition(i);
                String currPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));//缩略图路径
                int m=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails._ID));
                String vName = BitmapUtils.queryVideoNameById(this,m);
                String vPath = BitmapUtils.queryVideoPathById(this,m);
                Bitmap b;
          	   try
          	   {
	               b = BitmapUtils.decodeBitmap(currPath,100,100);    
          	   }
          	   catch(NullPointerException ex)
	               {
	            	   continue;
	               }   
                videoBitmaps.add(b);
                videoDuration.add(vName);
                MyFile myFile = new MyFile();
                myFile.Name =vName;
                myFile.Path=vPath;
                videoFiles.add(myFile);
            }  
            
        }  
        
    } 
    
}
