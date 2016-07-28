package com.tyky.test.webcam_sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Runnable {
	static final String tag = "CameraPreview";
	private static final boolean DEBUG = true;
	protected Context context;
	private SurfaceHolder holder;
	Thread mainLoop = null;
	private Bitmap bmp = null;

	private boolean cameraExists = false;
	private boolean shouldStop = false;

	/// /dev/videox (x=cameraId+cameraBase) is used.
	// In some omap devices, system uses /dev/video[0-3],
	// so users must use /dev/video[4-].
	// In such a case, try cameraId=0 and cameraBase=4
	private int cameraId = 1;
	private int cameraBase = 0;
	//

	// This definition also exists in ImageProc.h.
	// Webcam must support the resolution 640x480 with YUYV format.
	static final int IMG_WIDTH = 1280;
	static final int IMG_HEIGHT = 960;

	// The following variables are used to draw camera images.
	private int winWidth = 0;
	private int winHeight = 0;
	private Rect rect;
	private int dw, dh;
	private float rate;

	// JNI functions
	public native int prepareCamera(int videoid);

	public native int prepareCameraWithBase(int videoid, int camerabase);

	public native void processCamera();

	public native void stopCamera();

	public native void pixeltobmp(Bitmap bitmap);

	public native void setFrame(int wight, int high);

	static {
		System.loadLibrary("ImageProc");
	}

	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CameraPreview(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (DEBUG)
			Log.d("WebCam", "CameraPreview constructed");

		setFrame(1280, 960);

		setFocusable(true);
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
	}

	int count = 0;

	@Override
	public void run() {
		while (true && cameraExists) {
			Log.i(tag, "loop");
			// obtaining display area to draw a large image
			if (winWidth == 0) {
				winWidth = this.getWidth();
				winHeight = this.getHeight();

				if (winWidth * 3 / 4 <= winHeight) {
					dw = 0;
					dh = (winHeight - winWidth * 3 / 4) / 2;
					rate = ((float) winWidth) / IMG_WIDTH;
					rect = new Rect(dw, dh, dw + winWidth - 1, dh + winWidth * 3 / 4 - 1);
				} else {
					dw = (winWidth - winHeight * 4 / 3) / 2;
					dh = 0;
					rate = ((float) winHeight) / IMG_HEIGHT;
					rect = new Rect(dw, dh, dw + winHeight * 4 / 3 - 1, dh + winHeight - 1);
				}
			}

			// obtaining a camera image (pixel data are stored in an array in
			// JNI).
			processCamera();
			// camera image to bmp
			pixeltobmp(bmp);

			Canvas canvas = getHolder().lockCanvas();
			if (canvas != null) {
				// draw camera bmp on canvas
				canvas.drawBitmap(bmp, null, rect, null);

				getHolder().unlockCanvasAndPost(canvas);
			}

			if (shouldStop) {
				shouldStop = false;
				Log.i(tag, "break");
				break;
			}
		}
		Log.i(tag, "线程退出12");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (DEBUG)
			Log.d("WebCam", "surfaceCreated");
		if (bmp == null) {
			bmp = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT, Bitmap.Config.ARGB_8888);
		}
		// /dev/videox (x=cameraId + cameraBase) is used
		int ret = prepareCameraWithBase(cameraId, cameraBase);

		if (ret != -1)
			cameraExists = true;

		mainLoop = new Thread(this);
		mainLoop.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (DEBUG)
			Log.d("WebCam", "surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (DEBUG)
			Log.d("WebCam", "surfaceDestroyed");
		if (cameraExists) {
			shouldStop = true;
			while (shouldStop) {
				try {
					Thread.sleep(100); // wait for thread stopping
				} catch (Exception e) {
				}
			}
		}
		stopCamera();
	}

	public void takePic() {
		if (bmp != null) {
//			if (DEBUG) {
//				Log.i("WebCam", "开始保存图片");
//			}
//			File f = new File("/sdcard/namecard/", "1001" + "_webcam.jpg");
//			if (f.exists()) {
//				f.delete();
//			}
//			try {
//				f.createNewFile();
//				FileOutputStream out = new FileOutputStream(f);
//				bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
//				out.flush();
//				out.close();
//				if (DEBUG) {
//					Log.i("WebCam", "图片已经保存");
//				}
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			
			 File f = new File("/sdcard/" + "test" + ".png");
			  try {
			   f.createNewFile();
			  } catch (IOException e) {
				  e.printStackTrace();
			  }
			  FileOutputStream fOut = null;
			  try {
			   fOut = new FileOutputStream(f);
			  } catch (FileNotFoundException e) {
			   e.printStackTrace();
			  }
			  bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			  try {
			   fOut.flush();
			  } catch (IOException e) {
			   e.printStackTrace();
			  }
			  try {
			   fOut.close();
			  } catch (IOException e) {
			   e.printStackTrace();
			  }
			
		}
	}

}
