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
  
    private int tabCount;// tabҳ����  
    private int device_x; //��Ļ��  
  
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
        // indexΪҪ�л�����tabҳ������currentTabIndexΪ����Ҫ��ǰtabҳ������  
        int currentTabIndex = getCurrentTab();  
  
     // ���õ�ǰtabҳ�˳�ʱ�Ķ���  
        if (null != getCurrentView()) {// ��һ�ν���MainActivityʱ��getCurrentView()ȡ�õ�ֵΪ��  
            if (currentTabIndex == (tabCount - 1) && index == 0) {// ����߽绬��  
                getCurrentView().startAnimation(slideLeftOut);  
            } else if (currentTabIndex == 0 && index == (tabCount - 1)) {// ����߽绬��  
                getCurrentView().startAnimation(slideRightOut);  
            } else if (index > currentTabIndex) {// �Ǳ߽�����´�������fleep  
                getCurrentView().startAnimation(slideLeftOut);  
            } else if (index < currentTabIndex) {// �Ǳ߽�����´�������fleep  
                getCurrentView().startAnimation(slideRightOut);  
            }  
        }  
  
        super.setCurrentTab(index);  
  
     // ���ü�����ʾ��tabҳ�Ķ���  
        if (currentTabIndex == (tabCount - 1) && index == 0) {// ����߽绬��  
            getCurrentView().startAnimation(slideLeftIn);  
        } else if (currentTabIndex == 0 && index == (tabCount - 1)) {// ����߽绬��  
            getCurrentView().startAnimation(slideRightIn);  
        } else if (index > currentTabIndex) {// �Ǳ߽�����´�������fleep  
            getCurrentView().startAnimation(slideLeftIn);  
        } else if (index < currentTabIndex) {// �Ǳ߽�����´�������fleep  
            getCurrentView().startAnimation(slideRightIn);  
        }   
    }  
	
}