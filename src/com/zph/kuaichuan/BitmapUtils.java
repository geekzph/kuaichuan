package com.zph.kuaichuan;

import java.util.ArrayList;  
import java.util.List;  
import android.app.Activity;  
import android.content.Intent;
import android.database.Cursor;  
import android.graphics.Bitmap;  
import android.graphics.BitmapFactory;  
import android.net.Uri;
import android.provider.MediaStore;    
public final class BitmapUtils {  
      
      
      
    public static Bitmap decodeBitmap(String path, int displayWidth, int displayHeight){  
        BitmapFactory.Options op = new BitmapFactory.Options();  
        op.inJustDecodeBounds = true;  
        Bitmap bmp = BitmapFactory.decodeFile(path, op); //获取尺寸信息  
        //获取比例大小  
        int wRatio = (int)Math.ceil(op.outWidth/(float)displayWidth);  
        int hRatio = (int)Math.ceil(op.outHeight/(float)displayHeight);  
        //如果超出指定大小，则缩小相应的比例  
        if(wRatio > 1 && hRatio > 1){  
            if(wRatio > hRatio){  
                op.inSampleSize = wRatio;  
            }else{  
                op.inSampleSize = hRatio;  
            }  
        }  
        op.inJustDecodeBounds = false;  
        bmp = BitmapFactory.decodeFile(path, op);  
        return Bitmap.createScaledBitmap(bmp, displayWidth, displayHeight, true);  
    }  
      
    /** 
     * 采用复杂计算来决定缩放 
     * @param path 
     * @param maxImageSize 
     * @return 
     */  
    public static Bitmap decodeBitmap(String path, int maxImageSize){  
        BitmapFactory.Options op = new BitmapFactory.Options();  
        op.inJustDecodeBounds = true;  
        Bitmap bmp = BitmapFactory.decodeFile(path, op); //获取尺寸信息  
        int scale = 1;  
        if(op.outWidth > maxImageSize || op.outHeight > maxImageSize){  
            scale = (int)Math.pow(2, (int)Math.round(Math.log(maxImageSize/(double)Math.max(op.outWidth, op.outHeight))/Math.log(0.5)));  
        }  
        op.inJustDecodeBounds = false;  
        op.inSampleSize = scale;  
        bmp = BitmapFactory.decodeFile(path, op);  
        return bmp;       
    }  
      
      //=================================
    public static Cursor queryImageThumbnails(Activity context){  
        String[] columns = new String[]{  
                MediaStore.Images.Thumbnails.DATA,  
                MediaStore.Images.Thumbnails._ID,  
                MediaStore.Images.Thumbnails.IMAGE_ID  
        };  
        return context.managedQuery(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, columns, null, null, MediaStore.Images.Thumbnails.DEFAULT_SORT_ORDER);  
    }  
    
     static Cursor queryMusicThumbnails(Activity context){  
        String[] columns = new String[]{  
                MediaStore.Audio.Media.DATA,  
                MediaStore.Audio.Media._ID,  
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE
        };  
        return context.managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);  
    }
    
    public static Cursor queryVideoThumbnails(Activity context){  
        String[] columns = new String[]{  
        		MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails._ID,
        };  
        return context.managedQuery(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, columns, null, null, MediaStore.Video.Thumbnails.DEFAULT_SORT_ORDER);  
    }  
      
    
    public static Cursor queryThumbnails(Activity context, String selection, String[] selectionArgs){  
        String[] columns = new String[]{  
        		MediaStore.Images.Thumbnails.DATA,  
                MediaStore.Images.Thumbnails._ID,  
                MediaStore.Images.Thumbnails.IMAGE_ID  
        };  
        return context.managedQuery(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, columns, selection, selectionArgs, MediaStore.Images.Thumbnails.DEFAULT_SORT_ORDER);       
    }  
    
    public static Cursor queryVideo(Activity context, String selection, String[] selectionArgs){  
        String[] columns = new String[]{  
                MediaStore.Video.Media._ID,  
                MediaStore.Video.Media.DATA,  
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.TITLE
        };  
        return context.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, selection, selectionArgs, MediaStore.Video.Media.DEFAULT_SORT_ORDER);         
    }  
      
    public static String queryVideoNameById(Activity context, int imageId){  
        String selection = MediaStore.Video.Media._ID + "=?";  
        String[] selectionArgs = new String[]{  
                imageId + ""  
        };  
        Cursor cursor = BitmapUtils.queryVideo(context, selection, selectionArgs);  
        if(cursor.moveToFirst()){  
            String dur = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));  
            cursor.close();  
            //return BitmapUtils.decodeBitmap(path, 260, 260);  
            return dur;//持续时间
        }else{  
            cursor.close();  
            return null;  
        }  
    }
    public static String queryVideoPathById(Activity context, int imageId){  
        String selection = MediaStore.Video.Media._ID + "=?";  
        String[] selectionArgs = new String[]{  
                imageId + ""  
        };  
        Cursor cursor = BitmapUtils.queryVideo(context, selection, selectionArgs);  
        if(cursor.moveToFirst()){  
            String dur = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));  
            cursor.close();  
            //return BitmapUtils.decodeBitmap(path, 260, 260);  
            return dur;//持续时间
        }else{  
            cursor.close();  
            return null;  
        }  
    }
    //========================================================================  
    public static Bitmap queryThumbnailById(Activity context, int thumbId){  
        String selection = MediaStore.Images.Thumbnails._ID + " = ?";  
        String[] selectionArgs = new String[]{  
            thumbId+""    
        };  
        Cursor cursor = BitmapUtils.queryThumbnails(context,selection,selectionArgs);  
          
        if(cursor.moveToFirst()){  
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));  
            cursor.close();  
            return BitmapUtils.decodeBitmap(path, 100, 100);  
        }else{  
            cursor.close();  
            return null;  
        }  
    }  
      
    public static Bitmap[] queryThumbnailsByIds(Activity context, Integer[] thumbIds){  
        Bitmap[] bitmaps = new Bitmap[thumbIds.length];  
        for(int i=0; i<bitmaps.length; i++){  
            bitmaps[i] = BitmapUtils.queryThumbnailById(context, thumbIds[i]);  
        }  
          
        return bitmaps;  
    }  
      
    /** 
     * 获取全部 
     * @param context 
     * @return 
     */  
    public static List<Bitmap> queryThumbnailList(Activity context){  
        List<Bitmap> bitmaps = new ArrayList<Bitmap>();  
        Cursor cursor = BitmapUtils.queryImageThumbnails(context);  
        for(int i=0; i<cursor.getCount(); i++){  
            cursor.moveToPosition(i);  
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));  
            Bitmap b = BitmapUtils.decodeBitmap(path, 100, 100);  
            bitmaps.add(b);  
        }  
        cursor.close();  
        return bitmaps;  
    }  
      
    public static List<Bitmap> queryThumbnailListByIds(Activity context, int[] thumbIds){  
        List<Bitmap> bitmaps = new ArrayList<Bitmap>();  
        for(int i=0; i<thumbIds.length; i++){  
            Bitmap b = BitmapUtils.queryThumbnailById(context, thumbIds[i]);  
            bitmaps.add(b);  
        }  
          
        return bitmaps;  
    }     
      
    public static Cursor queryImages(Activity context){  
        String[] columns = new String[]{  
                MediaStore.Images.Media._ID,  
                MediaStore.Images.Media.DATA,  
                MediaStore.Images.Media.DISPLAY_NAME  
        };  
        return context.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);  
    }  
      
    public static Cursor queryImages(Activity context, String selection, String[] selectionArgs){  
        String[] columns = new String[]{  
                MediaStore.Images.Media._ID,  
                MediaStore.Images.Media.DATA,  
                MediaStore.Images.Media.DISPLAY_NAME  
        };  
        return context.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection, selectionArgs, MediaStore.Images.Media.DEFAULT_SORT_ORDER);         
    }  
      
    public static String queryImageById(Activity context, int imageId){  
        String selection = MediaStore.Images.Media._ID + "=?";  
        String[] selectionArgs = new String[]{  
                imageId + ""  
        };  
        Cursor cursor = BitmapUtils.queryImages(context, selection, selectionArgs);  
        if(cursor.moveToFirst()){  
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));  
            cursor.close();  
            //return BitmapUtils.decodeBitmap(path, 260, 260);  
            //return BitmapUtils.decodeBitmap(path, 220); //看看和上面这种方式的差别,看了，差不多  
            return path;
        }else{  
            cursor.close();  
            return null;  
        }  
    }  
      
    /** 
     * 根据缩略图的Id获取对应的大图 
     * @param context 
     * @param thumbId 
     * @return 
     */  
    /**
    public static Bitmap queryImageByThumbnailId(Activity context, Integer thumbId){  
          
        String selection = MediaStore.Images.Thumbnails._ID + " = ?";  
        String[] selectionArgs = new String[]{  
            thumbId+""    
        };  
        Cursor cursor = BitmapUtils.queryThumbnails(context, selection, selectionArgs);  
          
        if(cursor.moveToFirst()){  
            int imageId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.IMAGE_ID));  
            cursor.close();  
            return BitmapUtils.queryImageById1(context, imageId);              
        }else{  
            cursor.close();  
            return null;  
        }  
    } 
    **/
    //============================================================================获取音乐封面
    /** 
     * 打开文件 
     * @param file 
     */  
    public static Intent openFile(MyFile file){  
          
        Intent intent = new Intent();  
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        //设置intent的Action属性  
        intent.setAction(Intent.ACTION_VIEW);  
        //获取文件file的MIME类型  
        String type = getMIMEType(file);  
        //设置intent的data和Type属性。  
        intent.setDataAndType(Uri.parse("file://"+file.Path), type);  
        return intent;
        //跳转  
        //startActivity(intent);    
          
    } 
    
    public static Intent openFile(HistoryInfo file){  
        
        Intent intent = new Intent();  
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        //设置intent的Action属性  
        intent.setAction(Intent.ACTION_VIEW);  
        //获取文件file的MIME类型  
        String type = getMIMEType(file);  
        //设置intent的data和Type属性。  
        intent.setDataAndType(Uri.parse("file://"+file.Path), type);  
        return intent;
        //跳转  
        //startActivity(intent);    
          
    }  
      
    /** 
     * 根据文件后缀名获得对应的MIME类型。 
     * @param file 
     */  
    private static String getMIMEType(MyFile file) {  
          
        String type="*/*";  
        String fName = file.Name;  
        //获取后缀名前的分隔符"."在fName中的位置。  
        int dotIndex = fName.lastIndexOf(".");  
        if(dotIndex < 0){  
            return type;  
        }  
        /* 获取文件的后缀名 */  
        String end=fName.substring(dotIndex,fName.length()).toLowerCase();  
        if(end=="")return type;  
        //在MIME和文件类型的匹配表中找到对应的MIME类型。  
        for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？  
            if(end.equals(MIME_MapTable[i][0]))  
                type = MIME_MapTable[i][1];  
        }         
        return type;  
    }  
    
    private static String getMIMEType(HistoryInfo file) {  
        
        String type="*/*";  
        String fName = file.fileName;  
        //获取后缀名前的分隔符"."在fName中的位置。  
        int dotIndex = fName.lastIndexOf(".");  
        if(dotIndex < 0){  
            return type;  
        }  
        /* 获取文件的后缀名 */  
        String end=fName.substring(dotIndex,fName.length()).toLowerCase();  
        if(end=="")return type;  
        //在MIME和文件类型的匹配表中找到对应的MIME类型。  
        for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？  
            if(end.equals(MIME_MapTable[i][0]))  
                type = MIME_MapTable[i][1];  
        }         
        return type;  
    }  
    
    private final static String[][] MIME_MapTable={  
            //{后缀名， MIME类型}  
            {".3gp",    "video/3gpp"},  
            {".apk",    "application/vnd.android.package-archive"},  
            {".asf",    "video/x-ms-asf"},  
            {".avi",    "video/x-msvideo"},  
            {".bin",    "application/octet-stream"},  
            {".bmp",    "image/bmp"},  
            {".c",  "text/plain"},  
            {".class",  "application/octet-stream"},  
            {".conf",   "text/plain"},  
            {".cpp",    "text/plain"},  
            {".doc",    "application/msword"},  
            {".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},  
            {".xls",    "application/vnd.ms-excel"},   
            {".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},  
            {".exe",    "application/octet-stream"},  
            {".gif",    "image/gif"},  
            {".gtar",   "application/x-gtar"},  
            {".gz", "application/x-gzip"},  
            {".h",  "text/plain"},  
            {".htm",    "text/html"},  
            {".html",   "text/html"},  
            {".jar",    "application/java-archive"},  
            {".java",   "text/plain"},  
            {".jpeg",   "image/jpeg"},  
            {".jpg",    "image/jpeg"},  
            {".js", "application/x-javascript"},  
            {".log",    "text/plain"},  
            {".m3u",    "audio/x-mpegurl"},  
            {".m4a",    "audio/mp4a-latm"},  
            {".m4b",    "audio/mp4a-latm"},  
            {".m4p",    "audio/mp4a-latm"},  
            {".m4u",    "video/vnd.mpegurl"},  
            {".m4v",    "video/x-m4v"},   
            {".mov",    "video/quicktime"},  
            {".mp2",    "audio/x-mpeg"},  
            {".mp3",    "audio/x-mpeg"},  
            {".mp4",    "video/mp4"},  
            {".mpc",    "application/vnd.mpohun.certificate"},        
            {".mpe",    "video/mpeg"},    
            {".mpeg",   "video/mpeg"},    
            {".mpg",    "video/mpeg"},    
            {".mpg4",   "video/mp4"},     
            {".mpga",   "audio/mpeg"},  
            {".msg",    "application/vnd.ms-outlook"},  
            {".ogg",    "audio/ogg"},  
            {".pdf",    "application/pdf"},  
            {".png",    "image/png"},  
            {".pps",    "application/vnd.ms-powerpoint"},  
            {".ppt",    "application/vnd.ms-powerpoint"},  
            {".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"},  
            {".prop",   "text/plain"},  
            {".rc", "text/plain"},  
            {".rmvb",   "audio/x-pn-realaudio"},  
            {".rtf",    "application/rtf"},  
            {".sh", "text/plain"},  
            {".tar",    "application/x-tar"},     
            {".tgz",    "application/x-compressed"},   
            {".txt",    "text/plain"},  
            {".wav",    "audio/x-wav"},  
            {".wma",    "audio/x-ms-wma"},  
            {".wmv",    "audio/x-ms-wmv"},  
            {".wps",    "application/vnd.ms-works"},  
            {".xml",    "text/plain"},  
            {".z",  "application/x-compress"},  
            {".zip",    "application/x-zip-compressed"},  
            {"",        "*/*"}    
        };  
}  
