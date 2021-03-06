package com.andysoft.light;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private boolean hasFlash;
	private boolean flashState;
	private Camera camera;
	private Parameters params;
	ImageView lightButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		lightButton = (ImageView) findViewById(R.id.imgbtn);
		
		hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
		if (!hasFlash) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage("Your device does not support camera flash").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	            	finish();
	            	}
	            });	
	    	AlertDialog alert = builder.create();
	    	alert.show();
		}	
		lightButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				toggleLight();
			}
		});

        final TextView attriblink = (TextView) findViewById(R.id.attribution);
        attriblink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.flaticon.com/free-icon/lightbulb_2613"));
                startActivity(browserIntent);
            }
        });
	}
	private void getCamera() {
		if (camera == null) {
			try {
				camera = Camera.open();
				params = camera.getParameters();
				
			} catch (RuntimeException e) {
				Log.e("Camera error. Failed to open", e.getMessage());
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public void toggleLight() {
		if (!flashState) { //turn on flash if it is off
			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(params);
			//camera.startPreview();
			flashState = true;
			//lightButton.setText(getString(R.string.light_on));
            lightButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_light_on));
		} else if (flashState) { //turn off flash if it is on
			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(params);
			//camera.stopPreview();
			flashState = false;
			//lightButton.setText(getString(R.string.light_off));
            lightButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_light_off));
		}
		lightButton.setKeepScreenOn(flashState);
	}
	
	@Override
	protected void onStart() {
	    super.onStart();
	    // on starting the app get the camera params
	    getCamera();
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
        params = camera.getParameters();
        params.setFlashMode(Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
        flashState = false;
        lightButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_light_off));
	}
	
	@Override
	protected void onStop() {
	    super.onStop();
	    // on stop release the camera
	    if (camera != null) {
	        camera.release();
	        camera = null;
	    }
	}

}
