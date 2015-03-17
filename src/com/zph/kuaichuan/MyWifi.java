package com.zph.kuaichuan;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

public class MyWifi 
{
	private WifiManager wifimanager = null; 
	//定义一个WifiInfo对象  
	//private WifiInfo mWifiInfo;  
	//网络连接列表  
	//private List<WifiConfiguration> mWifiConfigurations;  
	
	public MyWifi(WifiManager wifiManager)
	{
		this.wifimanager=wifiManager;
	}
	
	public int startAP()
	{
		int state = 6;
		
			//mWifiInfo=wifimanager.getConnectionInfo();  
			wifimanager.setWifiEnabled(false); 
			Method method1 = null;
			try {
				method1 = wifimanager.getClass().getMethod("setWifiApEnabled",WifiConfiguration.class, boolean.class);
			} catch (SecurityException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (NoSuchMethodException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	        WifiConfiguration netConfig = new WifiConfiguration();
	        netConfig.SSID = "Easy Share";
	        
			try {
				method1.invoke(wifimanager, netConfig, true);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        Method method2 = null;
			try {
				method2 = wifimanager.getClass().getMethod("getWifiApState");
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        try {
				state = (Integer) method2.invoke(wifimanager);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
        return state;
	}
	
	public void stopAP()
	{
		Method method1 = null;
		try {
			method1 = wifimanager.getClass().getMethod("setWifiApEnabled",WifiConfiguration.class, boolean.class);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 try {
			method1.invoke(wifimanager, null, false);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	// 将int类型的IP转换成字符串形式的IP   
	/**
		private String ipIntToString(int ip) 
		{   
			try {   
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
**/
}
