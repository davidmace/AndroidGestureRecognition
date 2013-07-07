package dmace.gesture_recognition;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class UpdateThread extends Thread {
        static public SurfaceHolder _surfaceHolder;
        private Panel _panel;
        private boolean _run = false;
 
        public UpdateThread(SurfaceHolder surfaceHolder, Panel panel) {
            _surfaceHolder = surfaceHolder;
            _panel = panel;
        }
 
        public SurfaceHolder getSurfaceHolder() {
            return _surfaceHolder;
        }
        
        public void setRunning(boolean run) {
            _run = run;
        }
 
        @Override
        public void run() {
            Canvas c;
            while (_run) {
                c = null;
                try {
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {
                        _panel.onDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }