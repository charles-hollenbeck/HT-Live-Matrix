package com.haxxedtech.lwp.matrix;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class HTLiveMatrixService extends WallpaperService {
	private final Handler mHandler = new Handler();

	@Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
	@Override
    public Engine onCreateEngine() {
		return new HTLiveMatrixEng();
    }
    class HTLiveMatrixEng extends Engine {  
       	private Chart chart;
    	private final Paint mPaint = new Paint();
    	private float mCenterX, mCenterY,mEndY,mEndX, mCharWidth, mCharHeight;
    	private boolean mVisible;
    	private int mMaxW,mMaxH,speed=100;   
    	private final Runnable mDrawMatrix = new Runnable() {
             public void run() {
                 updateMatrixFrame();
             }
         };
         OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
 	        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
 	        	int color = Color.parseColor(prefs.getString("list_preference_color", "GREEN"));
 	        	mPaint.setColor(color);
 	        	speed = Integer.parseInt(prefs.getString("list_preference_speed","100"));
 	        }
 	      };  
         HTLiveMatrixEng() {
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
     	   	int color = Color.parseColor(prefs.getString("list_preference_color", "GREEN"));
	        speed = Integer.parseInt(prefs.getString("list_preference_speed","100"));
     	   	mPaint.setColor(color); 
     	   	mPaint.setAntiAlias(true);
     	   	mPaint.setStrokeWidth(1); 
     	   	mPaint.setStrokeCap(Paint.Cap.BUTT);
     	   	mPaint.setTextAlign(Paint.Align.CENTER);
     	   	WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
     	   	Display display = wm.getDefaultDisplay();
     	   	int width = display.getWidth(); //deprecated 
     	   	int height = display.getHeight();
     	   	if(width>height){
     	   		mPaint.setTextSize(30.0f/540 * height);   
     	   	}else{
     	   		mPaint.setTextSize(30.0f/540 * width);   
     	   	}
     	   	mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
     	   	mCharWidth  = mPaint.measureText("0");
     	   	mCharHeight = Math.abs(mPaint.ascent()) + mPaint.descent();
     	    mMaxW =  (int) Math.floor(width / mCharWidth );
            mMaxH = (int) Math.floor(height/ mCharHeight);
     	 	prefs.registerOnSharedPreferenceChangeListener(listener);
            chart = new Chart(mMaxW,mMaxH);
         }
         @Override
         public void onDestroy() {
             super.onDestroy();
             mHandler.removeCallbacks(mDrawMatrix);
         }
         @Override
         public void onVisibilityChanged(boolean visible) {
             mVisible = visible;
             if (visible)
                 updateMatrixFrame();
             else
                 mHandler.removeCallbacks(mDrawMatrix);
         }
         @Override
         public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
             super.onSurfaceChanged(holder, format, width, height);
             mCenterX = width /2.0f;
             mCenterY = height /2.0f;  
             mEndY = height;
             mEndX = width;
             mMaxW =  (int) Math.floor(mEndX / mCharWidth );
             mMaxH = (int) Math.floor(mEndY / mCharHeight);
             chart.reverse();
             updateMatrixFrame();
         }
         @Override
         public void onSurfaceCreated(SurfaceHolder holder) {
             super.onSurfaceCreated(holder);
         }
         @Override
         public void onSurfaceDestroyed(SurfaceHolder holder) {
             super.onSurfaceDestroyed(holder);
             mVisible = false;
             mHandler.removeCallbacks(mDrawMatrix);
         }
         public void updateMatrixFrame() {
             final SurfaceHolder holder = getSurfaceHolder();
             Canvas c = null;
             try {
                 c = holder.lockCanvas();
                 if (c != null){
	            	 drawMatrix(c);
	            	 chart.shiftMatrixDown();
                 }
             } finally {
                 if (c != null) holder.unlockCanvasAndPost(c);
             }
             // Reschedule the next redraw
             mHandler.removeCallbacks(mDrawMatrix);
             if (mVisible)
                 mHandler.postDelayed(mDrawMatrix, speed);
         }
         public void drawMatrix(Canvas c) {
             c.save();             
             c.translate(mCenterX,mCenterY);     
             c.drawColor(Color.BLACK);
             int alpha = 255;
             String[][] table = chart.getTable();
             int i=0,x=0;
        	 for(String[] arr:table){
        		this.mPaint.setAlpha(alpha);
        		for(String str:arr){
        			if(i==0){ i++; }
        			float[] pos = {0.0f-this.mCenterX + this.mCharWidth*x + this.mCharWidth ,0.0f - this.mCenterY + this.mCharHeight*i};              		  
        			c.drawPosText(str,pos,mPaint);
	           	 	x++;
        		}	
       		 	alpha = alpha - (int)Math.round(alpha/(this.mMaxH/2));
        		i++;
        		x=0;
        	 }
            c.restore();
         }
    }
}
