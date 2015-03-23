package mcm.accelerometer.gyroscope;

import java.io.File;
import java.io.IOException;

import mcm.client.rest.HttpManager;
import mcm.client.rest.Utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

/*
*author: Huber Flores
*/


public class DrawView extends View
{
  private final float height = 0.4F;
  private float offsetX = 0.0F;
  private float offsetY = 0.0F;
  private final float width = 0.4F;
  
  public DrawView(Context paramContext)
  {
    super(paramContext);
  }

  private void drawRect(Canvas paramCanvas, float paramFloat1, float paramFloat2, Paint paramPaint)
  {
    float f1 = 0.5F * getWidth() - 0.5F * paramFloat1 + this.offsetX;
    float f2 = 0.5F * getHeight() - 0.5F * paramFloat2 + this.offsetY;
    float f3 = 0.5F * getWidth() + 0.5F * paramFloat1 + this.offsetX;
    float f4 = 0.5F * getHeight() + 0.5F * paramFloat2 + this.offsetY;
    paramCanvas.drawRect(f1, f2, f3, f4, paramPaint);
    paramCanvas.drawLine(0.0F, 0.0F, f1, f2, paramPaint);
    paramCanvas.drawLine(getWidth(), 0.0F, f3, f2, paramPaint);
    paramCanvas.drawLine(0.0F, getHeight(), f1, f4, paramPaint);
    paramCanvas.drawLine(getWidth(), getHeight(), f3, f4, paramPaint);
  }

  public float getOffsetX()
  {
    return this.offsetX;
  }

  public float getOffsetY()
  {
    return this.offsetY;
  }

  public void moveX(float paramFloat)
  {
    this.offsetX = (paramFloat + this.offsetX);
  }

  public void moveY(float paramFloat)
  {
    this.offsetY = (paramFloat + this.offsetY);
  }

  protected void onDraw(Canvas paramCanvas)
  {
    //Paint localPaint1 = new Paint();
    //localPaint1.setColor(-16777216); 
    //localPaint1.setStyle(Paint.Style.FILL);
    //paramCanvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), localPaint1);
	  
    Paint localPaint2 = new Paint();
    //localPaint2.setColor(-1);
    localPaint2.setColor(-16777216);
    localPaint2.setStyle(Paint.Style.STROKE);
    drawRect(paramCanvas, 0.4F * getWidth(), 0.4F * getHeight(), localPaint2);
	  
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
	//change cloneDatabase to a more suitable method  
	/*cloneDatabase base = new cloneDatabase();
	try {
		if (base.fileToCopy()){
			base.copyDataBase();
		}
		} catch (IOException e) {
			e.printStackTrace();
		}
		  
	File file = new File(base.getDataBasePath());
	try {
		httpManager.UploadFile(file,Utilities.AMAZON_S3);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
				
		
    if (paramMotionEvent.getAction() == 0)
    {
      this.offsetX = 0.0F;
      this.offsetY = 0.0F;
    }
    for (int i = 1; ; i = 0)
      if (i==1){ return true;}else{return false;}
  }

  public void setOffsetX(float paramFloat)
  {
    this.offsetX = paramFloat;
  }

  public void setOffsetY(float paramFloat)
  {
    this.offsetY = paramFloat;
  }
}
