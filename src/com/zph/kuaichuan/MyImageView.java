package com.zph.kuaichuan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView;

public class MyImageView extends ImageView {

    public MyImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

    
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub    
        
        super.onDraw(canvas);    
        //»­±ß¿ò
        Rect rec=canvas.getClipBounds();
        rec.bottom--;
        rec.right=rec.right-9;
        rec.top=rec.top+1;
        rec.left=rec.left+9;
        Paint paint=new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rec, paint);
    }
}