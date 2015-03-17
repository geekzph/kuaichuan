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
    public int dragIndex=8888;//item����
    public Vibrator vib;      //�𶯶���
    public int windowHeight=0;
    public int windowWidth=0;
    int bmpbmpWidth=0;   
    int bmpbmpHeight=0; 
    
  //���涨��Ҫʹ�õ����б���
    private ImageView dragImageView;//����ק���Ӱ����ʵ����һ��ImageView
    private int dragPosition;//��ָ�϶���ʱ�򣬵�ǰ�϶������б��е�λ��
     
    private int dragPoint;//�ڵ�ǰ�������е�λ��
    private int dragOffset;//��ǰ��ͼ����Ļ�ľ���(����ֻʹ����y������)
     
    private WindowManager windowManager;//windows���ڿ�����
    private WindowManager.LayoutParams windowParams;//���ڿ�����ק�����ʾ�Ĳ���
     
    private int scaledTouchSlop;//�жϻ�����һ������
    private int upScrollBounce;//�϶���ʱ�򣬿�ʼ���Ϲ����ı߽�
    private int downScrollBounce;//�϶���ʱ�򣬿�ʼ���¹����ı߽�
    
    private View dragger=null;
    
    public DragListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void rotateAnimation()
    {
    	// �����趨(ָ����ת����) (startAngle, endAngle, rotateX, rotateY)
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
            	 //����down�¼�
               
                int x = (int)ev.getX();
                int y = (int)ev.getY();
                dragIndex=pointToPosition(x,y);
                //ѡ�е�������λ�ã�ʹ��ListView�Դ���pointToPosition(x, y)����
                dragPosition = pointToPosition(x, y);
                //�������Чλ��(�����߽磬�ָ��ߵ�λ��)������
                if(dragPosition==AdapterView.INVALID_POSITION){
                    return false;
                }
         
                //��ȡѡ����View
                //getChildAt(int position)��ʾdisplay�ڽ����positionλ�õ�View
                //getFirstVisiblePosition()���ص�һ��display�ڽ����view��adapter��λ��position��������0��Ҳ������4
                ViewGroup itemView = (ViewGroup) getChildAt(dragPosition-getFirstVisiblePosition());
                 
                //dragPoint���λ���ڵ��View�ڵ����λ��
                //dragOffset��Ļλ�ú͵�ǰListViewλ�õ�ƫ����������ֻ�õ�y�����ϵ�ֵ
                //�������������ں����϶��Ŀ�ʼλ�ú��ƶ�λ�õļ���
                dragPoint = y - itemView.getTop();
                dragOffset = (int) (ev.getRawY() - y);
              //��
            	vib.vibrate(30);
                //��ȡ�ұߵ��϶�ͼ�꣬����Ժ��������ק������
                dragger = itemView.findViewById(R.id.shared_file_icon);
                //������ұ�λ�ã���קͼƬ��ߵ�20px���ұ�����
                if(dragger!=null&&x>dragger.getLeft()-20){
                    //׼���϶�
                    //��ʼ���϶�ʱ��������
                    //scaledTouchSlop�������϶���ƫ��λ(һ��+-10)
                    //upScrollBounce������Ļ���ϲ�(����1/3����)���߸��ϵ�����ִ���϶��ı߽磬downScrollBounceͬ����
                    upScrollBounce = Math.min(y-scaledTouchSlop, getHeight()/3);
                    downScrollBounce = Math.max(y+scaledTouchSlop, getHeight()*2/3);
                     
                    //����DrawingcacheΪtrue�����ѡ�����Ӱ��bm�����Ǻ��������϶����ĸ�ͷ��
                    itemView.setDrawingCacheEnabled(true);
                    Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
                     
                    //׼���϶�Ӱ��(��Ӱ����뵽��ǰ���ڣ���û���϶����϶��������Ƿ���onTouchEvent()��move��ִ��)
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
     * ׼���϶�����ʼ���϶����ͼ��
     * @param bm
     * @param y
     */
    public void startDrag(Bitmap bm ,int y){
       //�ͷ�Ӱ����׼��Ӱ���ʱ�򣬷�ֹӰ��û�ͷţ�ÿ�ζ�ִ��һ�� 
       stopDrag();
         
        windowParams = new WindowManager.LayoutParams();
        //���ϵ��¼���y�����ϵ����λ�ã�
        windowParams.gravity = Gravity.TOP;
        windowParams.x = 0;
        windowParams.y = y - dragPoint + dragOffset;
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //������Щ�����ܹ�����׼ȷ��λ��ѡ������λ�ã��ճ�����
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
     
        //��Ӱ��ImagView��ӵ���ǰ��ͼ��
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(bm);
        windowManager = (WindowManager)getContext().getSystemService("window");
        windowManager.addView(imageView, windowParams);
        //��Ӱ��ImageView���õ�����drawImageView�����ں�������(�϶����ͷŵȵ�)
        dragImageView = imageView;
    }
    
    /**
     * ֹͣ�϶���ȥ���϶����ͷ��
     */
    public void stopDrag(){
        if(dragImageView!=null){
            windowManager.removeView(dragImageView);
            dragImageView = null;
        }
    }
    
    /**
     * �����¼�
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //���dragmageViewΪ�գ�˵�������¼����Ѿ��ж������ǵ���������϶�������
        //������������Чλ�ã����أ���Ҫ�����ж�
        if(dragImageView!=null&&dragPosition!=INVALID_POSITION){
            int action = ev.getAction();
            switch(action){
                case MotionEvent.ACTION_UP:
                    //int upY = (int)ev.getY();
                    //�ͷ��϶�Ӱ��
                    stopDrag();
                    //���º��ж�λ�ã�ʵ����Ӧ��λ��ɾ���Ͳ���
                    //onDrop(upY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveY = (int)ev.getY();
                    //�϶�Ӱ��
                    onDrag(moveY);
                    break;
                default:break;
            }
            return true;
        }
        //�������ֵ�ܹ�ʵ��selected��ѡ��Ч�����������true����ѡ��Ч��
        return super.onTouchEvent(ev);
    }
    
    /**
     * �϶�ִ�У���Move������ִ��
     * @param y
     */
    public void onDrag(int y){
        if(dragImageView!=null){
            windowParams.alpha = 0.8f;
            windowParams.y = y - dragPoint + dragOffset;
            windowManager.updateViewLayout(dragImageView, windowParams);
        }
        //Ϊ�˱��⻬�����ָ��ߵ�ʱ�򣬷���-1������
        int tempPosition = pointToPosition(0, y);
        if(tempPosition!=INVALID_POSITION){
            dragPosition = tempPosition;
        }
         
        //����
        int scrollHeight = 0;
        if(y<upScrollBounce){
            scrollHeight = 8;//�������Ϲ���8�����أ�����������Ϲ����Ļ�
        }else if(y>downScrollBounce){
            scrollHeight = -8;//�������¹���8�����أ�������������Ϲ����Ļ�
        }
         
        if(scrollHeight!=0){
            //���������ķ���setSelectionFromTop()
            setSelectionFromTop(dragPosition, getChildAt(dragPosition-getFirstVisiblePosition()).getTop()+scrollHeight);
        }
    }
}
