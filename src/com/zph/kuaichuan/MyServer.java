package com.zph.kuaichuan;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MyServer implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5254448988033787519L;
	//private OutputStream outStream = null;
	private DataInputStream din = null;  
	private DataOutputStream dout = null;  

	private Socket clientSocket = null;
	private ServerSocket mServerSocket = null;
	public Handler mHandler = null;
	
	private AcceptThread mAcceptThread = null;
	private ReceiveThread mReceiveThread = null;
	//private isConnected mConnect = null;
	private boolean stop = true;
	//private boolean conStop=true;
	private boolean isSendComplete=true;
	private List<MyFile> myFiles=new ArrayList<MyFile>();
	private int fileindex=-1;
	private int mindex=0;
	
	public MyServer()
	{
		fileindex=-1;
		//mConnect=new isConnected();
		//conStop=false;
	}
	/**
	private class isConnected extends Thread
	{
		@Override
		public void run()
		{
			while(!conStop)
			{
				
					Log.d("isConnected","====test====="); 
					try {
						clientSocket.sendUrgentData(0xFF);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.d("isConnected","#####send######"); 
						Message message = new Message();  
	                    message.what = 10;
	                    message.obj="已断开";
	                    mHandler.sendMessage(message);
					}  
				
					
				 
			}
		}
	}**/
	public void sendFile(MyFile mFile)
	{
		myFiles.add(mFile);
		fileindex++;
	}
	
	sendFileThread ss=null;
	public void sendFieThread()
	{
		
        ss=new sendFileThread();
        ss.start();
	}
	
	private class sendFileThread extends Thread
	{
		String str=null;
		InputStream inputStream;
   	 	int passedlen=0;
   	 	int FileLength=0;
   	 	MyFile mFile=null;
		sendFileThread()
		{
			
		}
		
		@Override
        public void run()
        {
			while(true)
			{
				//Log.d("MyServer",String.valueOf(MyClient.cfileComplete));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				if(mindex<=fileindex&&isSendComplete&&MyClient.cfileComplete==true)
				{
					isSendComplete=false;
					mFile=myFiles.get(mindex);
					File savePath = new File(mFile.Path);
			   	 	FileLength=(int)savePath.length();
					try 
					{
		            	inputStream = new FileInputStream(savePath);
						din = new DataInputStream(new BufferedInputStream(inputStream)); // 用缓存流包装文件输入流（提高读取速度），然后再包装成数据输入流  
						
						dout = new DataOutputStream(clientSocket.getOutputStream());
						dout.writeUTF(mFile.Name+getSuffix(mFile.Path)); // 文件名  
			            dout.flush();  
			            dout.writeLong(FileLength);  //文件长度
			            dout.flush();  
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try 
					{
			            byte data[] = new byte[1024*16];
			            int read = 0; 
			            while(true)
			            {
			            	if(din!=null)
			           		//发送数据
			           		read=din.read(data);
			            	passedlen += read;  
			                if (read == -1) 
			                {  
			                   mindex++;
			                   passedlen=0;
			                   break;
			                }  
			                /**
			                this.str=String.valueOf(passedlen*100/FileLength);
	 	                    Message message = new Message();  
	 	                    message.what = 5;
	 	                    message.obj =this.str;
	 	                    mHandler.sendMessage(message);
	 	                    **/
			                //从InputStream对象中读取数据并写入到 OutputStream对象当中
			                dout.write(data,0,read);
			           }
			            dout.flush();
		                din.close();
		                isSendComplete=true;
		                //Toast.makeText(Context(), "第"+m+"次",Toast.LENGTH_SHORT).show();
		                Log.d("MyServer", String.valueOf(mindex)); 
					} 
					catch (FileNotFoundException e1) 
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
					catch (IOException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
					continue;
				
			}
			
        }
		
		
	}
	private OutputStream soutStream = null;
	public void slideToSend(String msg) throws IOException
	{
		byte[] msgBuffer = null;
		msgBuffer = msg.getBytes("GB2312");
		soutStream = clientSocket.getOutputStream();
		soutStream.write(msgBuffer);		
	}
	public String getSuffix(String m)//呆点
	{
		int startindex=m.lastIndexOf(".");
		return m.substring(startindex, m.length());
	}
	public void startServer()
	{
		//发送创建连接信息
        Message message = new Message();  
        message.what = 2;
        message.obj= "创建成功，等待用户连接...";
        mHandler.sendMessage(message);
		mAcceptThread = new AcceptThread();
        //开启监听线程
        mAcceptThread.start();
      
        
	}
	
	public void closeServer()
	{
		Log.d("MyServer","停止"); 
        if(mReceiveThread != null)
        {
            stop = true;
            mReceiveThread.interrupt();
            
        }
       if(mAcceptThread!=null)
        mAcceptThread.interrupt();
       Log.d("MyServer","mAcceptThread.interrupt();");
       if(ss!=null)
       ss.interrupt();
        try {
  			if(mServerSocket!=null&&!mServerSocket.isClosed())
  			{
  				mServerSocket.close();
  			//clientSocket.close();
  				Log.d("MyServer","mServerSocket.close();");
  			}
  		} 
        catch (IOException e) 
  		{
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} 
	}
	
	
	
	public class AcceptThread extends Thread
	{
	    @Override
	    public void run()
	    {
	        try 
	        {
	            //实例化ServerSocket对象并设置端口号为8888
	            mServerSocket = new ServerSocket(8888);
	            System.out.println("建立服务器");
	        } 
	        catch (IOException e) 
	        {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        stop = false;
	        try 
	        {
	       	 
	            //等待客户端的连接（阻塞）
	            clientSocket = mServerSocket.accept();
	            Message message = new Message();  
                message.what = 21;
                message.obj ="已成功连接";
                //发送消息
                mHandler.sendMessage(message);
	            Log.d("MyServer","accept"); 
	            //mConnect.start();
	            System.out.println("等待客户端的连接（阻塞）");
	        } 
	        catch (IOException e) 
	        {
	            // TODO Auto-generated catch block
	        	Log.d("MyServer",e.toString()); 
	            e.printStackTrace();
	            stop=true;
	        }
	        
	        if(!stop)
	        {
	       	 	mReceiveThread = new ReceiveThread(clientSocket);
	       	 	Log.d("MyServer","thread start========="); 
	       	 	//开启接收线程
	       	 	mReceiveThread.start();
	        }
	        
	        
	    }
	    
	}
	Boolean fileComplete=true;
	public static Boolean cfileComplete=true;
	public class ReceiveThread extends Thread
    {
   	 
		private long passedlen = 0;  
        private long FileLength = 0;  
        private DataInputStream mInputStream = null;
        private byte[] buf ;  
        private int str;
        private DataOutputStream fileOut=null;
        private Socket s2=null;

        private String fileName=null;
        private int id=0;
        private String fileType=null;
        ReceiveThread(Socket s)
        {
            s2=s;
           	Log.d("测试","ReceiveThread构造函数"); 
        }
        
        @Override
        public void run()
        {
        	try
        	{

        		//获得输入流
        		this.mInputStream = new DataInputStream(new BufferedInputStream(s2.getInputStream())); 
        		Log.d("MyServer","s.getInputStream()"); 
        		fileComplete=false;
        		cfileComplete=false;
        		fileName=mInputStream.readUTF();
        		File savePath=null;
        		fileType=isFileType(fileName);
        		savePath = new File("/mnt/sdcard/easy share/app", fileName);
        		if(fileType.equals("pic"))
        			savePath = new File("/mnt/sdcard/easy share/picture", fileName);
        		if(fileType.equals("audio"))
        			savePath = new File("/mnt/sdcard/easy share/audio", fileName);
        		if(fileType.equals("video"))
        			savePath = new File("/mnt/sdcard/easy share/video", fileName);
        		
        		Log.d("MyServer","File savePath"); 
        		Log.d("MyServer",String.valueOf(MyClient.cfileComplete)+"   cfileComplete");
        		FileLength=mInputStream.readLong();
        		//发送tab跳转信息
                Message tabmsg = new Message();  
                tabmsg.what = 8;
                tabmsg.arg1= 11;
                mHandler.sendMessage(tabmsg);
                Thread.sleep(1000);
        		//发送文件属性信息
        		Message message = new Message();  
                message.what = 6;
                Bundle bundle = new Bundle();    
                bundle.putString("fileName",fileName); 
                bundle.putString("savePath",savePath.getPath()); 
                bundle.putLong("fileSize",FileLength); 
                bundle.putInt("id",id);  
                message.setData(bundle);
                mHandler.sendMessage(message);
                 
        		Log.d("MyServer","文件长度1      "+String.valueOf(FileLength));
        		fileOut = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(savePath)))); 
        	} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	//Date beginDate = new Date(System.currentTimeMillis());//获取当前时间
        	Log.d("MyClient","ReceiveThread run函数"); 
                this.buf = new byte[1024*16];
                //读取输入的数据(阻塞读)
                try 
                {
                    //this.mInputStream.read(buf);
               	 	while(!stop) 
               	 	{  
                        int read = 0;  
                        if (mInputStream != null&&fileComplete==false) 
                        {  
                            read = mInputStream.read(buf);
                            cfileComplete=false;
                        }
                        else
                        {
                       
                        	Log.d("MyServer", "mInputStream.read(buf)"); 
                        	//获得输入流
                          	this.mInputStream = new DataInputStream(new BufferedInputStream(s2.getInputStream())); 
                        	fileComplete=false;
                        	cfileComplete=false;
                        	Log.d("MyServer", "mInputStream.read(buf)下方  "); 
                        	fileName=mInputStream.readUTF();
                        	File savePath=null;
                    		fileType=isFileType(fileName);
                    		savePath = new File("/mnt/sdcard/easy share/app", fileName);
                    		if(fileType.equals("pic"))
                    			savePath = new File("/mnt/sdcard/easy share/picture", fileName);
                    		if(fileType.equals("audio"))
                    			savePath = new File("/mnt/sdcard/easy share/audio", fileName);
                    		if(fileType.equals("video"))
                    			savePath = new File("/mnt/sdcard/easy share/video", fileName);
                        	
                        	FileLength=mInputStream.readLong();
                        	Log.d("MyServer","文件长度2     "+String.valueOf(FileLength));
                        	
                        	//发送tab跳转信息
 	 	                    Message tabmsg = new Message();  
 	 	                    tabmsg.what = 8;
 	 	                    tabmsg.arg1= 11;
 	 	                    mHandler.sendMessage(tabmsg);
                        	//发送文件属性信息
                        	Message message = new Message();  
                            message.what = 6;
                            Bundle bundle = new Bundle();    
 	                        bundle.putString("fileName",fileName); 
 	                        bundle.putString("savePath",savePath.getPath()); 
 	                        bundle.putLong("fileSize",FileLength);   
 	                        bundle.putInt("current_progress",this.str);  
 	                        bundle.putInt("id",id);  
 	                        message.setData(bundle);
 	 	                    mHandler.sendMessage(message);
                            
                            fileOut = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(savePath))));
                            
                        	read = mInputStream.read(buf);
                        	//beginDate = new Date(System.currentTimeMillis());//获取当前时间   
                            Log.d("MyServer", "继续接受"); 
                        }
                        passedlen += read;
                        //Date endDate = new Date(System.currentTimeMillis());//获取当前时间   
                        Log.d("MyServer","文件接收了"+(passedlen*100/FileLength)+"%/n"); 
 	                    this.str=(int) (passedlen*100/FileLength);
 	                    
	 	               Log.d("MyServer","sendmessage"); 
 	                   fileOut.write(buf,0,read); 
 	                   

 	                   //传递文件发送进度信息
 	                    Message message = new Message();  
 	                    message.what = 7;
	 	                Bundle bundle = new Bundle();     
	                    bundle.putInt("current_progress",this.str); 
	                    bundle.putInt("id",id); 
	                    bundle.putString("filetype",fileType); 
	                    bundle.putString("speed",String.valueOf(passedlen/1024)+"/"+String.valueOf(FileLength/1024)); 
	                    message.setData(bundle);
	 	                mHandler.sendMessage(message);
	 	                
 	                   Log.d("MyServer","fileoutWrite"); 
                        if(passedlen==FileLength)
                        {
	                       
	                        fileOut.close(); 
                        	fileComplete=true;
                        	cfileComplete=true;
                        	passedlen=0;
                        	id++;
                        	Log.d("MyServer",String.valueOf(MyClient.cfileComplete));
                        }
                    }  
               	 
                    
                    //Log.d("AndroidClient","@@@文件接收完成"+savePath);  
                    //fileOut.close(); 
                } 
                catch (IOException e1) 
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                catch (ArithmeticException e1) 
                {
                    // TODO Auto-generated catch block
                	 //发送tab跳转信息
	                   
                }
        }
    }
    
	private String isFileType(String fileName)
	{
		String fileType="";
		int statindex=fileName.lastIndexOf(".");
		String mFileName=fileName.substring(statindex+1).toLowerCase();
		String[] pic=new String[5];
		pic[0]="jpg";
		pic[1]="png";
		pic[2]= "gif";
		pic[3]="bmp";
		String[] video=new String[8];
		video[0]="mp4";
		video[1]="3gp";
		video[2]= "rm";
		video[3]="flv";
		video[4]="3gp";
		video[5]="avi";
		video[6]="wmv";
		video[7]="rmvb";
		String[] audio=new String[5];
		audio[0]="mp3";
		audio[1]="wav";
		audio[2]= "mid";
		audio[3]="ape";
		audio[4]= "flac";
		for(int i=0;i<4;i++)
		{
			if(mFileName.equals(pic[i]))
					{
						fileType="pic";
						break;
					}
			else
				continue;
					
		}
		for(int i=0;i<8;i++)
		{
			if(mFileName.equals(video[i]))
					{
						fileType="video";
						break;
					}
			else
				continue;
					
		}
		for(int i=0;i<5;i++)
		{
			if(mFileName.equals(audio[i]))
					{
						fileType="audio";
						break;
					}
			else
				continue;
					
		}
		if(fileType.equals(""))
			return "apk";
		else
		return fileType;
	}
}