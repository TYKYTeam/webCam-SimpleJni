package com.tyky.test.webcam_sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class Main extends Activity {

	CameraPreview cp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// cp = new CameraPreview(this);
		// setContentView(cp);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		CommandUtil.grandCameraPermission();
		setContentView(R.layout.activity_main);
		 cp = (CameraPreview) findViewById(R.id.cameraView);;
		Button takebtn = (Button) findViewById(R.id.take_pic_bt);
		takebtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(Main.this, "take photo", Toast.LENGTH_SHORT).show();
				cp.takePic();
			}
		});

		Log.i("webcam", "start.");
	}

}
