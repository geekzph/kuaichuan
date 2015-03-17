package com.zph.kuaichuan;

import java.io.File;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TextView;

public class MainTab extends ActivityGroup {
	private CustomTabHost mHost; //ĸ��ǩ
	private Intent app;//Ӧ�ñ�ǩ
	private Intent pic;//ͼƬ��ǩ
	private Intent media;//ý���ǩ
	private Intent file;//�ļ������ǩ
	private Intent history;//��ʷ��¼��ǩ
	private TextView textview1,textview2,textview3,textview4,textview5,connect_friends; //ĸ��ǩ�ı���
	private TextView loading_text;
	private ImageView main_tab_indicator;//��ǩָʾ��
	private ImageView loading_image;//loading
	Animation rotateAnimation=null;//loading����
	int bmpW=0,offset=0,currIndex=0;//ָʾ����ƫ����
	
	MyWifi myWifi=null;
    public MyClient myClient=null;
    private MyServer myServer=null;
    private Handler mHandler = null;
    WifiReceiver mWifiReceiver;   //���ܹ㲥
    private IntentFilter mIntentFilter; //�㲥����
    private ConnetFriendDialog cd; //���ӶԻ���
    
    public final static String SERVER_KEY = "com.zph.server";  
    public final static String CLIEDNT_KEY = "com.zph.client"; 
  
    private PowerManager powerManager = null; 
    private WakeLock wakeLock = null; 
    //public static List<HistoryInfo> mHisInfo = new ArrayList<HistoryInfo>();
    protected void onCreate(Bundle savedInstanceState) 
	  {
	    super.onCreate(savedInstanceState);
  
	    /**����������View�ģ����Դ������е����β��ֱ����غ������Ȼ��Ч*/  
	    requestWindowFeature(Window.FEATURE_NO_TITLE);  
	    setContentView(R.layout.maintabs);
	    //��ʼ���ȵ�
        myWifi=new MyWifi((WifiManager)this.getSystemService(Context.WIFI_SERVICE)); 
        //��ʼ��������
        myServer=new MyServer();
        //��ʼ���ͻ���
        myClient=new MyClient((WifiManager)this.getSystemService(Context.WIFI_SERVICE)); 
        mHandler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                switch(msg.what)
                {
	                case 1://Server����������
	                {
	                	connect_friends.setVisibility(View.GONE);
	                	loading_image.setVisibility(View.VISIBLE);
	                	loading_image.setAnimation(rotateAnimation);
	                	loading_image.startAnimation(rotateAnimation);
	                	loading_text.setVisibility(View.VISIBLE);
	                	loading_text.setText(msg.obj.toString());
	                    break;
	                }
	                
	                case 2://Server�����ɹ����ȴ��û�����...
	                {
	                	
	                	loading_text.setText(msg.obj.toString());
	                    break;
	                }
	                case 21://Server �ѳɹ�����
	                {
	                	loading_image.setVisibility(View.INVISIBLE);
	                	loading_image.clearAnimation();
	                	loading_text.setText("");
	                	connect_friends.setVisibility(View.VISIBLE);
	                	connect_friends.setText("        �Ͽ�����        ");
	                	Toast.makeText(getBaseContext(), "�ѳɹ�����", Toast.LENGTH_SHORT).show();
	                    break;
	                }
	                case 3://client ��ʼ����
	                {
	                	connect_friends.setVisibility(View.GONE);
	                	loading_image.setVisibility(View.VISIBLE);
	                	loading_image.setAnimation(rotateAnimation);
	                	loading_image.startAnimation(rotateAnimation);
	                	loading_text.setVisibility(View.VISIBLE);
	                	loading_text.setText(msg.obj.toString());
	                    break;
	                }
	                case 4://client������ɣ���ʼ����
	                {
	                	
	                	loading_text.setText(msg.obj.toString());
	                    break;
	                }
	                case 5://client�ѳɹ�����
	                {
	                	loading_image.setVisibility(View.INVISIBLE);
	                	loading_image.clearAnimation();
	                	loading_text.setText("");
	                	connect_friends.setVisibility(View.VISIBLE);
	                	connect_friends.setText("        �Ͽ�����        ");
	                	Toast.makeText(getBaseContext(), "�ѳɹ�����", Toast.LENGTH_LONG).show();
	                    break;
	                }
	                case 8:
	                {
	                	
	                	Log.d("MainTab", "��ת");
	                	mHost.setCurrentTab(4);
	                	whcihSelect(5);
	                	tabIndicatorAni(4);
	                	currIndex=4;
	                    break;
	                }
	                case 10:// client: myserver�ѶϿ�
	                {
	                	Toast.makeText(getBaseContext(), "�����ѶϿ�", Toast.LENGTH_LONG).show();
	                	Log.d("MainTab", msg.obj.toString());
	                	loading_text.setText("");
	                	connect_friends.setVisibility(View.VISIBLE);
	                	connect_friends.setText("        ��������        ");
	                    break;
	                }
                
                }                                           
                
            }
        };
        myServer.mHandler=mHandler;
        myClient.mHandler=mHandler;
        mWifiReceiver=new WifiReceiver();
		mIntentFilter=new IntentFilter();
        mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION); 
        mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(mWifiReceiver,mIntentFilter);
		
		myServer.sendFieThread();
		myClient.sendFieThread();
        
       cd = new ConnetFriendDialog(this);  
        
        connect_friends = (TextView) findViewById(R.id.btn_connect_friends);  
        connect_friends.setOnClickListener(new Button.OnClickListener(){  
 
               public void onClick(View v) {  
                   // TODO Auto-generated method stub 
            	   if(connect_friends.getText().equals("        ��������        "))
            		   cd.show();
            	   else if(connect_friends.getText().equals("        �Ͽ�����        ")&&isServerClient=="Client")
            	   {
            		   myClient.sendCloseMsg();
            		   myClient.closeClient();
            		   myClient.stiopWifi();
            		   connect_friends.setText("        ��������        ");
            	   }
            	   else if(connect_friends.getText().equals("        �Ͽ�����        ")&&isServerClient=="Server")
            	   {
            		   myServer.closeServer();
            		   connect_friends.setText("        ��������        ");
            	   }   
               }  
           });  
        //======================main tab==============================
	    textview1=(TextView) findViewById(R.id.textView1);//app
	    textview2=(TextView) findViewById(R.id.textView2);//pic
	    textview3=(TextView) findViewById(R.id.textView3);//media
	    textview4=(TextView) findViewById(R.id.textView4);//file
	    textview5=(TextView) findViewById(R.id.textView5);//history
	    // 设置TabHost
	    InitImageView();
	    initTabs();
	    initRadios();
	    setupIntent();
	    mHost.setCurrentTab(0);
    	whcihSelect(1);
    	
    	loading_image=(ImageView) findViewById(R.id.loading_image);
    	loading_image.setVisibility(View.INVISIBLE);
    	rotateAnimation = new RotateAnimation (0f,359f,Animation.RELATIVE_TO_SELF, 0.5f, 
        		Animation.RELATIVE_TO_SELF, 0.5f );
    	rotateAnimation.setDuration (400);
    	rotateAnimation.setRepeatCount(-1);
    	rotateAnimation.setRepeatMode(-1);
    	loading_text=(TextView) findViewById(R.id.loading_text);
    	loading_text.setVisibility(View.INVISIBLE);
    	
        
        File appfile = new File ("/mnt/sdcard/easy share/app");
        if(!appfile.exists())//���������
        {
        	appfile.mkdirs();//����������ܷ񴴽��༶Ŀ¼�ṹ
        }
        File picfile = new File ("/mnt/sdcard/easy share/picture");
        if(!picfile.exists())//���������
        {
        	picfile.mkdirs();//����������ܷ񴴽��༶Ŀ¼�ṹ
        }
        File musicfile = new File ("/mnt/sdcard/easy share/audio");
        if(!musicfile.exists())//���������
        {
        	musicfile.mkdirs();//����������ܷ񴴽��༶Ŀ¼�ṹ
        }
        File videofile = new File ("/mnt/sdcard/easy share/video");
        if(!videofile.exists())//���������
        {
        	videofile.mkdirs();//����������ܷ񴴽��༶Ŀ¼�ṹ
        }
        
        this.powerManager = (PowerManager) this 
                .getSystemService(Context.POWER_SERVICE); 
        this.wakeLock = this.powerManager.newWakeLock( 
                PowerManager.FULL_WAKE_LOCK, "My Lock"); 
    	Log.d("MainTab", "created");
	  }
    
	 private void initTabs() 
	  {
	    mHost = (CustomTabHost) findViewById(R.id.tabhost);
	    mHost.setup(this.getLocalActivityManager());
	    
	    Bundle mBundle = new Bundle();     
        mBundle.putSerializable(SERVER_KEY,myServer); 
        mBundle.putSerializable(CLIEDNT_KEY,myClient);
	    app = new Intent(this, MainActivity.class);
	    app.putExtras(mBundle); 
	    pic = new Intent(this,PictureActivity.class);
	    pic.putExtras(mBundle);
	    media = new Intent(this, MediaActivity.class);
	    media.putExtras(mBundle);
	    file = new Intent(this, FileActivity.class);
	    file.putExtras(mBundle);
	    history = new Intent(this, HistoryActivity.class);
	    history.putExtras(mBundle);
	    
	    }
	  
	private void whcihSelect(int m)
	{
		switch(m)
		{
		case 1:
			textview1.setSelected(true);
        	textview2.setSelected(false);
        	textview3.setSelected(false);
        	textview4.setSelected(false);
        	textview5.setSelected(false);
		break;
		case 2:
			textview1.setSelected(false);
        	textview2.setSelected(true);
        	textview3.setSelected(false);
        	textview4.setSelected(false);
        	textview5.setSelected(false);
		break;
		case 3:
			textview1.setSelected(false);
        	textview2.setSelected(false);
        	textview3.setSelected(true);
        	textview4.setSelected(false);
        	textview5.setSelected(false);
		break;
		case 4:
			textview1.setSelected(false);
        	textview2.setSelected(false);
        	textview3.setSelected(false);
        	textview4.setSelected(true);
        	textview5.setSelected(false);
		break;
		case 5:
			textview1.setSelected(false);
        	textview2.setSelected(false);
        	textview3.setSelected(false);
        	textview4.setSelected(false);
        	textview5.setSelected(true);
		break;
		}
	}
	private void initRadios() 
	{
		textview1.setOnClickListener(new Button.OnClickListener(){  
			 
            public void onClick(View v) {  
                // TODO Auto-generated method stub 
                  //app��ǩ
            	mHost.setCurrentTab(0);
            	whcihSelect(1);
            	tabIndicatorAni(0);
            	currIndex=0;
            }  
        });  
		textview2.setOnClickListener(new Button.OnClickListener(){  
			 
            public void onClick(View v) {  
                // TODO Auto-generated method stub 
                  //pic��ǩ
            	mHost.setCurrentTab(1);
            	whcihSelect(2);
            	tabIndicatorAni(1);
            	currIndex=1;
            }  
        });
		
		textview3.setOnClickListener(new Button.OnClickListener(){  
			 
            public void onClick(View v) {  
                // TODO Auto-generated method stub 
                  //media��ǩ
            	mHost.setCurrentTab(2);
            	whcihSelect(3);
            	tabIndicatorAni(2);
            	currIndex=2;
            }  
        });  
		textview4.setOnClickListener(new Button.OnClickListener(){  
			 
            public void onClick(View v) {  
                // TODO Auto-generated method stub 
                  //dialog();
            	mHost.setCurrentTab(3);
            	whcihSelect(4);
            	tabIndicatorAni(3);
            	currIndex=3;
            }  
        });  
		textview5.setOnClickListener(new Button.OnClickListener(){  
			 
            public void onClick(View v) {  
                // TODO Auto-generated method stub 
                  //dialog();
            	mHost.setCurrentTab(4);
            	whcihSelect(5);
            	tabIndicatorAni(4);
            	currIndex=4;
            }  
        });  


	}
	
	 
	
	private void setupIntent() 
	{

		mHost.addTab(buildTabSpec("app","aaa",R.drawable.icon,app));
		mHost.addTab(buildTabSpec("pic","bbb",R.drawable.icon,pic));
		mHost.addTab(buildTabSpec("med","ccc",R.drawable.icon,media));
		mHost.addTab(buildTabSpec("fil","ddd",R.drawable.icon,file));
		mHost.addTab(buildTabSpec("his","eee",R.drawable.icon,history));
		
	}
	  
	 /**
     * ��ʼ������
*/
    private void InitImageView() {
    	main_tab_indicator = (ImageView) findViewById(R.id.imageView5);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.main_tab_anim)
                .getWidth();// ��ȡͼƬ���
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// ��ȡ�ֱ��ʿ��
        offset = (screenW / 5 - bmpW) / 2;// ����ƫ����
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        main_tab_indicator.setImageMatrix(matrix);// ���ö�����ʼλ��
    }
    
    private void tabIndicatorAni(int arg0)
    {
    	int one = offset * 2 + bmpW;// ҳ��1 -> ҳ��2 ƫ����
    	int two = offset * 2 + bmpW*2;// ҳ��1 -> ҳ��3ƫ����
    	int three = offset * 2 + bmpW*3;// ҳ��1 -> ҳ��4 ƫ����
    	int four = offset * 2 + bmpW*4;// ҳ��1 -> ҳ��5����
        Animation animation = null;
        switch (arg0) {
        case 0:
        	
        	 if (currIndex == 1) 
        	 {
        		 animation = new TranslateAnimation(one, 0, 0, 0);
        	 }else if (currIndex == 2) 
        	 {
        		 animation = new TranslateAnimation(two, 0, 0, 0);
        	 }else if (currIndex == 3) 
        	 {
        		 animation = new TranslateAnimation(three, 0, 0, 0);
        	 }else if (currIndex == 4) 
        	 {
        		 animation = new TranslateAnimation(four, 0, 0, 0);
        	 }
            break;
        case 1:
             if (currIndex == 0) 
             {
                 animation = new TranslateAnimation(offset, one, 0, 0);
             }else if (currIndex == 2) 
           	 {
               	animation = new TranslateAnimation(two, one, 0, 0);
           	 }else if (currIndex == 3) 
           	 {
           	 	animation = new TranslateAnimation(three,one, 0, 0);
           	 }else if (currIndex == 4) 
           	 {
           	 	animation = new TranslateAnimation(four,one, 0, 0);
           	 } 
             break;
        case 2:
            if (currIndex == 0) 
            {
                animation = new TranslateAnimation(offset, two, 0, 0);
            }else if (currIndex == 1) 
          	 {
              	animation = new TranslateAnimation(one, two, 0, 0);
          	 }else if (currIndex == 3) 
          	 {
          	 	animation = new TranslateAnimation(three,two, 0, 0);
          	 }else if (currIndex == 4) 
          	 {
          	 	animation = new TranslateAnimation(four,two, 0, 0);
          	 } 
            break;
        case 3:
            if (currIndex == 0) 
            {
                animation = new TranslateAnimation(offset, three, 0, 0);
            }else if (currIndex == 2) 
          	 {
              	animation = new TranslateAnimation(two, three, 0, 0);
          	 }else if (currIndex == 1) 
          	 {
          	 	animation = new TranslateAnimation(one,three, 0, 0);
          	 }else if (currIndex == 4) 
          	 {
          	 	animation = new TranslateAnimation(four,three, 0, 0);
          	 } 
            break;
        case 4:
            if (currIndex == 0) 
            {
                animation = new TranslateAnimation(offset, four, 0, 0);
            }else if (currIndex == 2) 
          	 {
              	animation = new TranslateAnimation(two, four, 0, 0);
          	 }else if (currIndex == 3) 
          	 {
          	 	animation = new TranslateAnimation(three,four, 0, 0);
          	 }else if (currIndex == 1) 
          	 {
          	 	animation = new TranslateAnimation(one,four, 0, 0);
          	 } 
            break;    
            }
            currIndex = arg0;
            try{
            animation.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��
            animation.setDuration(200);
            main_tab_indicator.startAnimation(animation);
            }
            catch(NullPointerException ex)
            {}

        
    }
    
	private TabHost.TabSpec buildTabSpec(String tag, String resLabel, int resIcon,
		    final Intent content) {
		return this.mHost
		        .newTabSpec(tag)
		        .setIndicator(resLabel,
		                getResources().getDrawable(resIcon))
		        .setContent(content);
		}
	
	//=============================================================
	static final String InitWifi="InitWifi";
    public static String isServerClient="no";
    
    
    int m=0;
    public class WifiReceiver extends BroadcastReceiver 
	{
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction(); 
			if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)&&m==0)
			{
				myClient.getScanResult();				
				myClient.connnectToAP();
				//������Ϣ
                Message message = new Message();  
                message.what = 4;
                message.obj= "������ɣ�������...";
                mHandler.sendMessage(message);
				//Toast.makeText(context, "�����ɨ�裬׼������ap�ȵ�"+myClient.SSID+String.valueOf(m), Toast.LENGTH_LONG).show();
				m=1;			
			}
			if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)&&m==1)
			{
				NetworkInfo nwInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			    if(NetworkInfo.State.CONNECTED.equals(nwInfo.getState())&&myClient.SSID.equals("Easy Share"))
			    {//This implies the WiFi connection is through
			    	//Toast.makeText(context, "�ѳɹ�������Easy Share", Toast.LENGTH_LONG).show();
			    	myClient.connectServer();
			    	//���ʹ���������Ϣ
	                Message message = new Message();  
	                message.what = 5;
	                message.obj= "�ѳɹ����ӣ�";
	                mHandler.sendMessage(message);
			    	m=3;
			    }
				
			}
			
	 
		}
	 
	}
    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("yao","MainTab.onKeyDown");
		 // ���¼����Ϸ��ذ�ť    
	       if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {    
	   
	           new AlertDialog.Builder(this)    
	                   .setMessage("ȷ���˳�ColorfulWorld��")    
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
    protected void onDestroy() { 
        super.onDestroy(); 
        
        if(isServerClient.equals("Server"))
        	myServer.closeServer();
        if(isServerClient.equals("Client"))
        {
        	myClient.sendCloseMsg();
        	myClient.closeClient();
        }
        	
        unregisterReceiver(mWifiReceiver); 
        myWifi.stopAP();
        Log.d("MainTab", "onDestroy");
    }
    @Override
    protected void onStop(){
        super.onStop();
        this.wakeLock.release(); 
        
        Log.d("MainTab", "onStop");
        
    }
    
    
    @Override
    protected void onResume(){
        super.onResume();
        this.wakeLock.acquire(); 
     // ע��wifi��Ϣ������ 
        registerReceiver(mWifiReceiver,mIntentFilter);     
        Log.d("MainTab", "onRseume");
    }
    
    @Override 
    protected void onPause() { 
        super.onPause(); 
        Log.d("MainTab", "onPause");
    }
    
    
    public class ConnetFriendDialog extends Dialog
	{

		
		public ConnetFriendDialog(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		
		protected void onCreate(Bundle savedInstanceState){  
	        super.onCreate(savedInstanceState);  
	        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ�������� 
	        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	        WindowManager.LayoutParams.FLAG_FULLSCREEN);//ȥ����Ϣ��
	        setContentView(R.layout.connectfriends);  
	        
	        
	        
	        Button chuangjian = (Button) findViewById(R.id.button1);  
	        chuangjian.setHeight(5);  
	        chuangjian.setOnClickListener(new Button.OnClickListener(){  
	 
	               public void onClick(View v) {  
	                   // TODO Auto-generated method stub  
	            	isServerClient="Server";
	            	//���ʹ���������Ϣ
	                Message message = new Message();  
	                message.what = 1;
	                message.obj= "���ڴ�������";
	                mHandler.sendMessage(message);
	       			myWifi.startAP();
	       			myServer.startServer();
	       			dismiss();
	                     
	               }  
	           });  
	        Button jiaru = (Button) findViewById(R.id.button2);  
	        jiaru.setSingleLine(true);  
	        jiaru.setOnClickListener(new Button.OnClickListener(){  
	 
	               public void onClick(View v) {  
	                   // TODO Auto-generated method stub  
	            	   m=0;
	            	   isServerClient="Client";
	       		   	   myClient.openWifi();
	       		   	   //���������ȵ���Ϣ
		                Message message = new Message();  
		                message.what = 3;
		                message.obj= "��ʼ����...";
		                mHandler.sendMessage(message);
	                   dismiss();
	               }  
	           });  
	        
	        ImageView guanbi=(ImageView) findViewById(R.id.imageView1);
	        guanbi.setOnClickListener(new Button.OnClickListener(){  
	        	 
	            public void onClick(View v) {  
	                // TODO Auto-generated method stub  
	                dismiss();
	            }  
	        });  
	    }  
	      
	    //called when this dialog is dismissed  
	    protected void onStop() {  
	        Log.d("TAG","+++++++++++++++++++++++++++");  
	    }    
	}
	
	}