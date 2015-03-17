package com.zph.kuaichuan;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TabHost;

class CustomTabHost extends TabHost
{
	
	private Animation slideLeftIn;  
    private Animation slideLeftOut;  
    private Animation slideRightIn;  
    private Animation slideRightOut;  
  
    private int tabCount;// tab页总数  
    private int device_x; //屏幕宽  
  
    public CustomTabHost(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        Resources resources = getResources();  
        DisplayMetrics metrics=resources.getDisplayMetrics();  
        device_x=metrics.widthPixels;  
          
        initAnima();  
    }  
  
    private void initAnima() {  
        slideLeftIn=new TranslateAnimation(device_x, 0, 1, 1);  
        slideLeftIn.setDuration(300);  
          
        slideRightIn=new TranslateAnimation(-device_x,0,1,1);  
        slideRightIn.setDuration(300);  
          
        slideLeftOut=new TranslateAnimation(0,-device_x,1,1);  
        slideLeftOut.setDuration(300);  
          
        slideRightOut=new TranslateAnimation(0,device_x,1,1);  
        slideRightOut.setDuration(300);  
    }  
      
    public void setDevice_X(int device_x)  
    {  
        this.device_x=device_x;  
        initAnima();  
    }  
  
    public int getTabCount() {  
        return tabCount;  
    }  
  
    @Override  
    public void addTab(TabSpec tabSpec) {  
        tabCount++;  
        super.addTab(tabSpec);  
    }  
  
      
    @Override  
    public void setCurrentTab(int index) {  
        // index为要切换到的tab页索引，currentTabIndex为现在要当前tab页的索引  
        int currentTabIndex = getCurrentTab();  
  
     // 设置当前tab页退出时的动画  
        if (null != getCurrentView()) {// 第一次进入MainActivity时，getCurrentView()取得的值为空  
            if (currentTabIndex == (tabCount - 1) && index == 0) {// 处理边界滑动  
                getCurrentView().startAnimation(slideLeftOut);  
            } else if (currentTabIndex == 0 && index == (tabCount - 1)) {// 处理边界滑动  
                getCurrentView().startAnimation(slideRightOut);  
            } else if (index > currentTabIndex) {// 非边界情况下从右往左fleep  
                getCurrentView().startAnimation(slideLeftOut);  
            } else if (index < currentTabIndex) {// 非边界情况下从左往右fleep  
                getCurrentView().startAnimation(slideRightOut);  
            }  
        }  
  
        super.setCurrentTab(index);  
  
     // 设置即将显示的tab页的动画  
        if (currentTabIndex == (tabCount - 1) && index == 0) {// 处理边界滑动  
            getCurrentView().startAnimation(slideLeftIn);  
        } else if (currentTabIndex == 0 && index == (tabCount - 1)) {// 处理边界滑动  
            getCurrentView().startAnimation(slideRightIn);  
        } else if (index > currentTabIndex) {// 非边界情况下从右往左fleep  
            getCurrentView().startAnimation(slideLeftIn);  
        } else if (index < currentTabIndex) {// 非边界情况下从左往右fleep  
            getCurrentView().startAnimation(slideRightIn);  
        }   
    }  
	
}