package com.zph.kuaichuan;

import android.content.Context;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.ListView;

public class DragListView extends ListView {   
    private int x=0;
    private int y=0;
    public int dragIndex=8888;//item索引
    public Vibrator vib;      //震动对象
    public int windowHeight=0;
    public int windowWidth=0;
    int bmpbmpWidth=0;   
    int bmpbmpHeight=0; 
    
  //下面定义要使用的所有变量
    private ImageView dragImageView;//被拖拽项的影像，其实就是一个ImageView
    private int dragPosition;//手指拖动的时候，当前拖动项在列表中的位置
     
    private int dragPoint;//在当前数据项中的位置
    private int dragOffset;//当前视图和屏幕的距离(这里只使用了y方向上)
     
    private WindowManager windowManager;//windows窗口控制类
    private WindowManager.LayoutParams windowParams;//用于控制拖拽项的显示的参数
     
    private int scaledTouchSlop;//判断滑动的一个距离
    private int upScrollBounce;//拖动的时候，开始向上滚动的边界
    private int downScrollBounce;//拖动的时候，开始向下滚动的边界
    
    private View dragger=null;
    
    public DragListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void rotateAnimation()
    {
    	// 动画设定(指定旋转动画) (startAngle, endAngle, rotateX, rotateY)
        Animation rotateAnimation = new RotateAnimation (0f,360f,Animation.RELATIVE_TO_SELF, 0.5f, 
        		Animation.RELATIVE_TO_SELF, 0.5f );
        TranslateAnimation translateAnimation = new TranslateAnimation(0f,windowWidth-x,0f,-y);
        rotateAnimation. setDuration (250 );
        translateAnimation. setDuration (450);
        
        //rotateView.setAnimation(rotateAnimation);
        dragger.setAnimation(translateAnimation);
    }
    public boolean setOnItemLongClickListener(final MotionEvent ev)  
    {  
        this.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()  
        {  
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,  
                    int arg2, long arg3)  
            {  
            	 //捕获down事件
               
                int x = (int)ev.getX();
                int y = (int)ev.getY();
                dragIndex=pointToPosition(x,y);
                //选中的数据项位置，使用ListView自带的pointToPosition(x, y)方法
                dragPosition = pointToPosition(x, y);
                //如果是无效位置(超出边界，分割线等位置)，返回
                if(dragPosition==AdapterView.INVALID_POSITION){
                    return false;
                }
         
                //获取选中项View
                //getChildAt(int position)显示display在界面的position位置的View
                //getFirstVisiblePosition()返回第一个display在界面的view在adapter的位置position，可能是0，也可能是4
                ViewGroup itemView = (ViewGroup) getChildAt(dragPosition-getFirstVisiblePosition());
                 
                //dragPoint点击位置在点击View内的相对位置
                //dragOffset屏幕位置和当前ListView位置的偏移量，这里只用到y坐标上的值
                //这两个参数用于后面拖动的开始位置和移动位置的计算
                dragPoint = y - itemView.getTop();
                dragOffset = (int) (ev.getRawY() - y);
              //震动
            	vib.vibrate(30);
                //获取右边的拖动图标，这个对后面分组拖拽有妙用
                dragger = itemView.findViewById(R.id.shared_file_icon);
                //如果在右边位置（拖拽图片左边的20px的右边区域）
                if(dragger!=null&&x>dragger.getLeft()-20){
                    //准备拖动
                    //初始化拖动时滚动变量
                    //scaledTouchSlop定义了拖动的偏差位(一般+-10)
                    //upScrollBounce当在屏幕的上部(上面1/3区域)或者更上的区域，执行拖动的边界，downScrollBounce同理定义
                    upScrollBounce = Math.min(y-scaledTouchSlop, getHeight()/3);
                    downScrollBounce = Math.max(y+scaledTouchSlop, getHeight()*2/3);
                     
                    //设置Drawingcache为true，获得选中项的影像bm，就是后面我们拖动的哪个头像
                    itemView.setDrawingCacheEnabled(true);
                    Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
                     
                    //准备拖动影像(把影像加入到当前窗口，并没有拖动，拖动操作我们放在onTouchEvent()的move中执行)
                    startDrag(bm, y);
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
        
    /**
     * 准备拖动，初始化拖动项的图像
     * @param bm
     * @param y
     */
    public void startDrag(Bitmap bm ,int y){
       //释放影像，在准备影像的时候，防止影像没释放，每次都执行一下 
       stopDrag();
         
        windowParams = new WindowManager.LayoutParams();
        //从上到下计算y方向上的相对位置，
        windowParams.gravity = Gravity.TOP;
        windowParams.x = 0;
        windowParams.y = y - dragPoint + dragOffset;
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //下面这些参数能够帮助准确定位到选中项点击位置，照抄即可
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
     
        //把影像ImagView添加到当前视图中
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(bm);
        windowManager = (WindowManager)getContext().getSystemService("window");
        windowManager.addView(imageView, windowParams);
        //把影像ImageView引用到变量drawImageView，用于后续操作(拖动，释放等等)
        dragImageView = imageView;
    }
    
    /**
     * 停止拖动，去除拖动项的头像
     */
    public void stopDrag(){
        if(dragImageView!=null){
            windowManager.removeView(dragImageView);
            dragImageView = null;
        }
    }
    
    /**
     * 触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //如果dragmageView为空，说明拦截事件中已经判定仅仅是点击，不是拖动，返回
        //如果点击的是无效位置，返回，需要重新判断
        if(dragImageView!=null&&dragPosition!=INVALID_POSITION){
            int action = ev.getAction();
            switch(action){
                case MotionEvent.ACTION_UP:
                    //int upY = (int)ev.getY();
                    //释放拖动影像
                    stopDrag();
                    //放下后，判断位置，实现相应的位置删除和插入
                    //onDrop(upY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveY = (int)ev.getY();
                    //拖动影像
                    onDrag(moveY);
                    break;
                default:break;
            }
            return true;
        }
        //这个返回值能够实现selected的选中效果，如果返回true则无选中效果
        return super.onTouchEvent(ev);
    }
    
    /**
     * 拖动执行，在Move方法中执行
     * @param y
     */
    public void onDrag(int y){
        if(dragImageView!=null){
            windowParams.alpha = 0.8f;
            windowParams.y = y - dragPoint + dragOffset;
            windowManager.updateViewLayout(dragImageView, windowParams);
        }
        //为了避免滑动到分割线的时候，返回-1的问题
        int tempPosition = pointToPosition(0, y);
        if(tempPosition!=INVALID_POSITION){
            dragPosition = tempPosition;
        }
         
        //滚动
        int scrollHeight = 0;
        if(y<upScrollBounce){
            scrollHeight = 8;//定义向上滚动8个像素，如果可以向上滚动的话
        }else if(y>downScrollBounce){
            scrollHeight = -8;//定义向下滚动8个像素，，如果可以向上滚动的话
        }
         
        if(scrollHeight!=0){
            //真正滚动的方法setSelectionFromTop()
            setSelectionFromTop(dragPosition, getChildAt(dragPosition-getFirstVisiblePosition()).getTop()+scrollHeight);
        }
    }
}
