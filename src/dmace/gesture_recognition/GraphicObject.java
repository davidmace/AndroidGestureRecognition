package dmace.gesture_recognition;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

class GraphicObject {
        protected String text;
        protected int x,y;
        public static Paint paint;
        public static Bitmap bitmap;
     
        public GraphicObject() {}
        
        public GraphicObject(String text, int x, int y) {
            this.text=text;
        	this.x=x;
        	this.y=y;
        }
        
        public void clicked() {}
        
        public void setCoords(int x, int y) {
        	this.x=x;
        	this.y=y;
        }
        
        public Bitmap getGraphic() {
            return bitmap;
        }
     
        public int getX() {
        	return x;
        }	
        
        public int getY() {
        	return y;
        }
        
        public int getWidth() {
        	return bitmap.getWidth();
        }
        
        public int getHeight() {
        	return bitmap.getHeight();
        }
        
        public boolean contains(MotionEvent e) {
    		int sx=x, sy=y;
    		int ex=sx+bitmap.getWidth(), ey=sy+bitmap.getHeight();
    		float x=e.getX(), y=e.getY();
    		if(x>ex || x<sx || y>ey || y<sy) return false;
    		else return true;
    	}
        
        public void draw(Canvas canvas) {
        	canvas.drawBitmap(bitmap,x,y,null);
        	canvas.drawText(text,x,y+(int)(bitmap.getHeight()*0.8), paint);
        }
    }