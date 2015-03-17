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
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MyClient implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4225811453952162977L;
	private Socket clientSocket = null;
    private DataInputStream din = null;  
    private DataOutputStream dout = null;  
    public Handler mHandler = null;
    private ReceiveThread mReceiveThread = null;
    private boolean stop = true;
    //private SlideThread mSlideThread = null;
    public boolean slidestop = true;
    public boolean wslidestop = false;
    public WifiManager wifimanager = null; 
    //定义一个WifiInfo对象  
    //private WifiInfo mWifiInfo;  
    //网络连接列表  
    private List<ScanResult> mWifiList;  
 
	public MyClient( WifiManager mwifimanager)
	{
		
		wifimanager =  mwifimanager;
		
		
		
        /**
		mWifiInfo=wifimanager.getConnectionInfo();  
		wifimanager.setWifiEnabled(true); 
		//取得WifiInfo对象  
	
		DhcpInfo d;
		d=wifimanager.getDhcpInfo();
		//得到配置好的网络连接
		mWifiConfigurations=wifimanager.getConfiguredNetworks();
		System.out.println("wifi state------->" + wifimanager.getWifiState()); 
	
		System.out.println("wifi state------->ygjghjghjghjj"); 
		//获得WIFI信息   
		StringBuffer sb = new StringBuffer();   
		sb.append("Wifi信息\n");   
		sb.append("MAC地址：" + mWifiInfo.getMacAddress() + "\n");   
		sb.append("接入点的BSSID：" + mWifiInfo.getBSSID() + "\n");  
		sb.append("SSID：" + mWifiInfo.getSSID() + "\n"); 
		sb.append("IP地址（int）：" + mWifiInfo.getIpAddress() + "\n");   
		sb.append("IP地址（Hex）：" + Integer.toHexString(mWifiInfo.getIpAddress()) + "\n"); sb.append("IP地址：" +ipIntToString(mWifiInfo.getIpAddress()) + "\n");   
		sb.append("网络ID：" + mWifiInfo.getNetworkId() + "\n");
		sb.append("网关：" + ipIntToString(d.gateway) + "\n"); 
		sb.append("ip地址：" + ipIntToString(d.ipAddress) + "\n"); 
		sb.append("租赁时间：" + d.leaseDuration + "\n"); 
		sb.append("子网掩码：" + ipIntToString(d.netmask)+ "\n"); 
		sb.append("服务器地址：" + ipIntToString(d.serverAddress) + "\n"); 
		System.out.println(sb.toString()); 
		for (WifiConfiguration wifiConfiguration : mWifiConfigurations) {   
		System.out.println("wfit ssid："+wifiConfiguration.SSID +wifiConfiguration.networkId+ "\n");   
		} 
		**/
	}
	
	public void openWifi()
	{
		isScanComplete=false;
		if (!wifimanager.isWifiEnabled()) 
		{  //如果wifi没有开启，则开启。
			wifimanager.setWifiEnabled(true);
		}
		
		wifimanager.startScan();
	}
	public void stiopWifi()
	{
		if (wifimanager.isWifiEnabled()) 
		  //如果wifi开启，则关闭。
			wifimanager.setWifiEnabled(false);
	}
	private String netBSSID="";
	public String SSID="";
	public boolean isScanComplete=false;
	public void getScanResult()
	{
		
		mWifiList=wifimanager.getScanResults();
		//List<WifiConfiguration> wifiConfigList = wifimanager.getConfiguredNetworks(); //
		for(int i=0;i<mWifiList.size();i++){  
            if(mWifiList.get(i).SSID.equals("Easy Share"))
            {
            	netBSSID=mWifiList.get(i).BSSID;
            	SSID=mWifiList.get(i).SSID;
            	
            }
        }  
	}
	public boolean connnectToAP()
	{
		isScanComplete=true;
		WifiConfiguration wc = new WifiConfiguration();
		wc.BSSID = netBSSID;
		wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wc.status = WifiConfiguration.Status.ENABLED;
        int res = wifimanager.addNetwork(wc);
        //Log.d("WifiPreference", "add Network returned " + res);
        boolean b = wifimanager.enableNetwork(res, true);
        //Log.d("WifiPreference", "enableNetwork returned " + b);
        return b;
	}
	
	public void connectServer()
	{
		try 
        {
			//mWifiInfo=wifimanager.getConnectionInfo();  
			DhcpInfo d;
			d=wifimanager.getDhcpInfo();
            //实例化对象并连接到服务器
            clientSocket = new Socket(ipIntToString(d.gateway),8888);
        } 
        catch (UnknownHostException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		//开启文件接收线程
        mReceiveThread = new ReceiveThread(clientSocket);
        stop = false;
        mReceiveThread.start();
        //开启滑动接收线程
        //mSlideThread = new SlideThread(clientSocket);
        //mSlideThread.start();
	}
	
	
	public void sendCloseMsg()
	{
		//MyFile mfile=new MyFile();
		//mfile.Name="zphclosezph";
		//sendFile(mfile);
	}
	public void closeClient()
	{

        if(mReceiveThread != null)
        {
            stop = true;
            slidestop =true;
            wslidestop = true;
            //mReceiveThread.stop();
            //mReceiveThread.interrupt();
        }
        if(ss!=null)
        	ss.interrupt();
	}
	Boolean fileComplete=true;
	public static Boolean cfileComplete=true;
	private class ReceiveThread extends Thread
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
        //private InputStreamReader br=null;
        ReceiveThread(Socket s)
        {
            s2=s;
            
             Message message = new Message();  
             message.what = 5;
             mHandler.sendMessage(message);
           	Log.d("测试","ReceiveThread构造函数"); 
        }
        
        @Override
        public void run()
        {
        	try
        	{
        		//获得输入流
        		this.mInputStream = new DataInputStream(new BufferedInputStream(s2.getInputStream())); 
        		Log.d("MyClient","s.getInputStream()"); 
        		fileComplete=false;
        		cfileComplete=false;
        		fileName=mInputStream.readUTF();
        		//slidestop=true;
        		File savePath=null;
        		fileType=isFileType(fileName);
        		savePath = new File("/mnt/sdcard/easy share/app", fileName);
        		if(fileType.equals("pic"))
        			savePath = new File("/mnt/sdcard/easy share/picture", fileName);
        		if(fileType.equals("audio"))
        			savePath = new File("/mnt/sdcard/easy share/audio", fileName);
        		if(fileType.equals("video"))
        			savePath = new File("/mnt/sdcard/easy share/video", fileName);
        		Log.d("MyClient","File savePath"); 
        		Log.d("MyClient",String.valueOf(MyClient.cfileComplete)+"   cfileComplete");
        		FileLength=mInputStream.readLong();
        		//发送tab跳转信息
                Message tabmsg = new Message();  
                tabmsg.what = 8;
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
                
                
        		Log.d("MyClient","文件长度1      "+String.valueOf(FileLength));
        		fileOut = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(savePath)))); 
        	} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
                       
                        	Log.d("MyClient", "mInputStream.read(buf)"); 
                        	//获得输入流
                          	this.mInputStream = new DataInputStream(new BufferedInputStream(s2.getInputStream())); 
                        	fileComplete=false;
                        	cfileComplete=false;
                        	Log.d("MyClient", "mInputStream.read(buf)下方  "); 
                        	fileName=mInputStream.readUTF();
                        	//slidestop = true;
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
                        	Log.d("MyClient","文件长度2     "+String.valueOf(FileLength));
                        	 //发送tab跳转信息
 	 	                    Message tabmsg = new Message();  
 	 	                    tabmsg.what = 8;
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
                            Log.d("MyClient", "继续接受"); 
                        }
                        passedlen += read;
                        Log.d("MyClient","文件接收了"+(passedlen*100/FileLength)+"%/n"); 
 	                    this.str=(int) (passedlen*100/FileLength);
 
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
                        if(passedlen==FileLength)
                        {
	                       
	                        fileOut.close(); 
                        	fileComplete=true;
                        	cfileComplete=true;
                        	passedlen=0;
                        	id++;
                        	//slidestop=false;
                        	Log.d("MyClient",String.valueOf(MyClient.cfileComplete));
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
	                    Message message = new Message();  
	                    message.what = 10;
	                    message.obj="已断开";
	                    mHandler.sendMessage(message);
                }
        }
        
        
    }
	/**
	private class SlideThread extends Thread
    {
        private InputStream inStream = null;
        
        private byte[] buf;  
        private String str = null;
        private Socket s2=null;
        SlideThread(Socket s)
        {
           s2=s;
        }      
        
        @Override
        public void run()
        { try {
        	Log.d("MyClient","SlideThread run()");  
            //获得输入流
            this.inStream = s2.getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
            while(true)
            {
            	if(!slidestop)
            	{
            		this.buf = new byte[512];
                    
                    try {
                        //读取输入数据（阻塞）
                        this.inStream.read(this.buf);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } 
                    
                    //字符编码转换
                    try {
                        this.str = new String(this.buf, "GB2312").trim();
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Log.d("MyClient",this.str);  
                    Message msg = new Message();
                    msg.obj = this.str;
                    //发送消息
                    mHandler.sendMessage(msg);
            	}
                
                
            }
        }
        
        
    }
	**/
	private boolean isSendComplete=true;
	private List<MyFile> myFiles=new ArrayList<MyFile>();
	private int fileindex=-1;
	private int mindex=0;
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
	
	class sendFileThread extends Thread
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
				if(mindex<=fileindex&&isSendComplete&&MyServer.cfileComplete==true)
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
			                
			                this.str=String.valueOf(passedlen*100/FileLength);
 	                       
	 	                    
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
	
	private String getSuffix(String m)
	{
		int startindex=m.lastIndexOf(".");
		return m.substring(startindex, m.length());
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
			// 将int类型的IP转换成字符串形式的IP   
	private String ipIntToString(int ip) 
	{   
		try 
		{   
			byte[] bytes = new byte[4];   
			bytes[0] = (byte) (0xff & ip);   
			bytes[1] = (byte) ((0xff00 & ip) >> 8);   
			bytes[2] = (byte) ((0xff0000 & ip) >> 16);   
			bytes[3] = (byte) ((0xff000000 & ip) >> 24);   
			return Inet4Address.getByAddress(bytes).getHostAddress();   
		} 
		catch (Exception e) 
		{   
			return "";   
		}   
	}
			
			

			
}
