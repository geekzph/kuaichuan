package com.zph.kuaichuan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

public class DragGridViews extends GridView {
    //定义基本的成员变量
    private ImageView dragImageView;
    private int dragSrcPosition;
    private int dragPosition;
    //x,y坐标的计算
    private int dragPointX;
    private int dragPointY;
    private int dragOffsetX;
    private int dragOffsetY;
    
    private int x=0;
    private int y=0;
    
    public int dragIndex=8888;//item索引
    public Vibrator vib;      //震动对象
    public int windowHeight=0;
    public int windowWidth=0;
    private WindowManager windowManager;
    private WindowManager.LayoutParams windowParams;
    private int upScrollBounce;
    private int downScrollBounce;
    
    int bmpbmpWidth=0;   
    int bmpbmpHeight=0; 
    
    private ViewGroup itemView=null,translateView=null;
    private View dragger=null,rotateView=null;
    
    public DragGridViews(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void rotateAnimation()
    {
    	  
    	//itemView.setClickable(false);
    	// 动画设定(指定旋转动画) (startAngle, endAngle, rotateX, rotateY)
        Animation rotateAnimation = new RotateAnimation (0f,360f,Animation.RELATIVE_TO_SELF, 0.5f, 
        		Animation.RELATIVE_TO_SELF, 0.5f );
        TranslateAnimation translateAnimation = new TranslateAnimation(0f,windowWidth-x,0f,-y);
        rotateAnimation. setDuration (250 );
        translateAnimation. setDuration (450);
        //stopDrag();
        
        rotateView.setAnimation(rotateAnimation);
        translateView.setAnimation(translateAnimation);
        // 动画开始
        //rotateAnimation.startNow ();
        //translateAnimation.startNow();
        //stopDrag();
        //dragImageView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, dragImageView.getLeft()+5, dragImageView.getTop()+5, 0));
        //Toast.makeText(getContext(), "        播放动画                                    ", Toast.LENGTH_SHORT).show();
    }
    
    public boolean setOnItemLongClickListener(final MotionEvent ev)  
    {  
        this.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()  
        {  
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,  
                    int arg2, long arg3)  
            {  
                // onInterceptTouchEvent(ev);  
                // TODO Auto-generated method stub  
                
            	
                x = (int) ev.getX();  
                y = (int) ev.getY();  
                dragSrcPosition  = dragPosition  = pointToPosition(x, y); 
                
                dragIndex=dragSrcPosition;
                System.out.println(dragIndex);
                
                if (dragPosition == AdapterView.INVALID_POSITION)  
                {  
                	 return false;
                }  
                
                
                itemView = (ViewGroup) getChildAt(dragPosition  
                        - getFirstVisiblePosition());  
                translateView=itemView;
                rotateView=itemView.findViewById(R.id.imageView);
                rotateView.setFocusable(false);
                translateView.setFocusable(false);
                //int m=getChildAt(dragPosition - getFirstVisiblePosition());
                // 得到当前点在item内部的偏移量 即相对于item左上角的坐标  
                dragPointX = x - itemView.getLeft()+1;  
                dragPointY = y - itemView.getTop()-1;  
  
                dragOffsetX = (int) (ev.getRawX() - x);  
                dragOffsetY = (int) (ev.getRawY() - y);  
                
                int test=getHeight();
                System.out.println(test);
                dragger = itemView.findViewById(R.id.imageView);
                //如果选中拖动图标
              //震动
            	vib.vibrate(30);
            	
                itemView.destroyDrawingCache();
                itemView.setDrawingCacheEnabled(true);
                Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
                startDrag(bm, x, y);
                
                if(dragger!=null&&dragPointX>dragger.getLeft()&&dragPointX<dragger.getRight()&&dragPointY>dragger.getTop()&&dragPointY<dragger.getBottom()+20)
                {
                    //upScrollBounce = Math.min(y-scaledTouchSlop, getHeight()/4);
                    //downScrollBounce = Math.max(y+scaledTouchSlop, getHeight()*6/7);
                    
                    upScrollBounce = bmpbmpHeight;
                    downScrollBounce = getHeight()-bmpbmpHeight;
                    
                }
                return false;  
            };  
        });  
        return super.onInterceptTouchEvent(ev);  
    }  
  
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	if (ev.getAction() == MotionEvent.ACTION_DOWN)  
        {  
            return setOnItemLongClickListener(ev);  
        }  
        return super.onInterceptTouchEvent(ev);  
    }
    
    public void startDrag(Bitmap bm, int x, int y){
    	stopDrag();
        
        windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP|Gravity.LEFT;
        windowParams.x = x-bmpbmpWidth/2 - dragPointX + dragOffsetX;
        windowParams.y = y-bmpbmpHeight/2- dragPointY + dragOffsetY;
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
        bmpbmpWidth=bm.getWidth();   
        bmpbmpHeight=bm.getHeight(); 
        
        /* 产生reSize后的Bitmap对象 */ 
        
        Matrix matrix = new Matrix();  
        matrix.postScale((float)(2), (float)(2));  
        Bitmap resizeBmp =  Bitmap.createBitmap(bm,0,0,bmpbmpWidth,bmpbmpHeight,matrix,true);  
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(resizeBmp);
        
        
        
        windowManager = (WindowManager)getContext().getSystemService("window");
        windowManager.addView(imageView, windowParams);
     
        
        dragImageView = imageView;
        
        itemView.setFocusableInTouchMode(false);
        /**
        int bmpbmpWidth=bm.getWidth();   
        int bmpbmpHeight=bm.getHeight(); 
        
        /* 产生reSize后的Bitmap对象 */ 
        /**
        Matrix matrix = new Matrix();  
        matrix.postScale((float)(bmpbmpWidth*1.1), (float)(bmpbmpHeight*1.1));  
        Bitmap resizeBmp =  Bitmap.createBitmap(bm,0,0,bmpbmpWidth,bmpbmpHeight,matrix,true);  
       
        ImageView imageView = new ImageView(getContext());
        
        imageView.setImageBitmap(bm);
        windowManager = (WindowManager)getContext().getSystemService("window");
        windowManager.addView(imageView, windowParams);
        dragImageView = imageView; **/
    }
    
    /**
     * 停止拖动，去除拖动项的头像
     */
    public void stopDrag(){
        if(dragImageView!=null){
            windowManager.removeView(dragImageView);
            dragImageView = null;
            //dragIndex=8888;
        }
    }
     
    public void onDrag(int x, int y){
    	if(dragImageView!=null){
            windowParams.alpha = 0.8f;
            windowParams.x = x-bmpbmpWidth/2 - dragPointX + dragOffsetX;
            windowParams.y = y-bmpbmpHeight/2- dragPointY + dragOffsetY;
            windowManager.updateViewLayout(dragImageView, windowParams);
            
           
            
        
        }
     
        int tempPosition = pointToPosition(x, y);
        if(tempPosition!=INVALID_POSITION){
            dragPosition = tempPosition;
        }
         
        if(y<upScrollBounce||y>downScrollBounce){
            //使用setSelection来实现滚动
            setSelection(dragPosition);
        }               
    }
    
    public boolean onTouchEvent(MotionEvent ev) {
    	if (dragImageView != null  
                && dragPosition != AdapterView.INVALID_POSITION)  
        {  
            int x = (int) ev.getX();  
            int y = (int) ev.getY();  
            switch (ev.getAction())  
            {  
                case MotionEvent.ACTION_MOVE:  
                    onDrag(x, y);  
                    break;  
                case MotionEvent.ACTION_UP:  
                    stopDrag();  
                    //onDrop(x, y);  
                    break;  
            }  
        }  
        return super.onTouchEvent(ev);  
    } 
}

