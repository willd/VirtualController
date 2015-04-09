package com.willd.virtualcontroller;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;

import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 00:06:23 - 11.07.2010
 * 
 * Rewritten by willd@kth.se for the Virtual Controller
 */
@SuppressLint("NewApi") public class Controller extends SimpleBaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static int CAMERA_WIDTH;
	private static int CAMERA_HEIGHT;
	private static final int DIALOG_ALLOWDIAGONAL_ID = 1;
    private Vibrator vibrator;
    
    
    @Override
    public synchronized void onWindowFocusChanged(boolean pHasWindowFocus) {
        super.onWindowFocusChanged(pHasWindowFocus);
        if (pHasWindowFocus) {
            if (Build.VERSION.SDK_INT >= 19) {
                int uiOptions
                        = 256 // View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | 4096; // View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            }
        }
    }
	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private BitmapTextureAtlas mOnScreenControlTexture;
	private ITextureRegion mOnScreenControlBaseTextureRegion;
	
	private ITextureRegion RightOnScreenControlBaseTextureRegion;
	
	private ITextureRegion mOnScreenControlKnobTextureRegion;
	private BitmapTextureAtlas LeftShoulderButton;
	private ITextureRegion LeftShoulderButtonRegion;
	private BitmapTextureAtlas RightShoulderButton;
	private ITextureRegion RightShoulderButtonRegion;
	private BitmapTextureAtlas SelectStartButtons;
	private ITextureRegion SelectStartButtonsRegion;
	private DigitalOnScreenControl mDigitalOnScreenControl;
	private DigitalOnScreenControl mDigitalOnScreenControl2;
	private final Scene scene = null;
	private Display display;
	@Override
	public EngineOptions onCreateEngineOptions() {
		vibrator = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
		Resources res = getResources();
		display = getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getRealMetrics(outMetrics);
		
	    if(display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270) {
			CAMERA_WIDTH = outMetrics.widthPixels;
		    CAMERA_HEIGHT = outMetrics.heightPixels;	    	
	    }
	    else {
			CAMERA_WIDTH = outMetrics.heightPixels;
		    CAMERA_HEIGHT = outMetrics.widthPixels;	
	    }
    	
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		return engineOptions;
	}	

	@SuppressLint("NewApi") @Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 64, TextureOptions.BILINEAR);
		this.mBitmapTextureAtlas.load();	

		this.mOnScreenControlTexture = new BitmapTextureAtlas(this.getTextureManager(), CAMERA_WIDTH, CAMERA_HEIGHT, TextureOptions.BILINEAR);
		this.LeftShoulderButton = new BitmapTextureAtlas(getTextureManager(), 256, 256, TextureOptions.DEFAULT);
		this.LeftShoulderButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(LeftShoulderButton, this, "tl_button_top.png", 0, 0);
		this.LeftShoulderButton.load();
		this.RightShoulderButton = new BitmapTextureAtlas(getTextureManager(), 256, 256, TextureOptions.DEFAULT);
		this.RightShoulderButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(RightShoulderButton, this, "tr_button_top.png", 0, 0);
		this.RightShoulderButton.load();
		
		this.SelectStartButtons = new BitmapTextureAtlas(getTextureManager(), 256, 256, TextureOptions.DEFAULT);
		this.SelectStartButtonsRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(SelectStartButtons, this, "select_start_buttons.png", 0, 0);
		this.SelectStartButtons.load();
		
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_base2.png", 0, 0);
		this.RightOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "snes2.png", CAMERA_WIDTH/3, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "transknob.png", 2*CAMERA_WIDTH/3, 0);
		this.mOnScreenControlTexture.load();

	}
		
	@Override
	public Scene onCreateScene() {

		final Scene scene = new Scene();

		scene.setOnAreaTouchTraversalFrontToBack();
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		
		scene.setBackground(new Background(0.45f, 0.45f, 0.45f));
		
		Bundle extras = getIntent().getExtras();
		String IPstring = extras.getString("IPstring");
		int LocalPort = extras.getInt("LP");
		final UDP_Client client = new UDP_Client();
		client.IPstring = IPstring;
		client.LocalPort = LocalPort;
		Log.i("VirtualController", "Current local port is: " + LocalPort);
		float scale = 1.6f;
		
		if(CAMERA_HEIGHT >= 1080) {
			scale = 3.2f;
		}
		else if(CAMERA_HEIGHT >= 720) {
			scale = 2.4f;
		}
		this.mDigitalOnScreenControl = new DigitalOnScreenControl(0, CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight()*scale , this.mCamera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, this.getVertexBufferObjectManager(), new IOnScreenControlListener() {
			private float tempX, tempY;
		    @Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {

		    	if((pValueX != tempX && pValueY != tempY)) {
						vibrator.vibrate(10);
				}
		    	Log.i("VirtualController", "Left: " + pValueX+ " " + pValueY);

		    		client.Message[0]= (int) pValueX;
		    		client.Message[1]= (int) pValueY;
		    	
				tempX = pValueX;
				tempY = pValueY;
			}
			
		});

		this.mDigitalOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.mDigitalOnScreenControl.getControlBase().setAlpha(0.5f);
		this.mDigitalOnScreenControl.getControlBase().setScaleCenter(0, 0);
		
		this.mDigitalOnScreenControl.getControlBase().setScale(scale);
		this.mDigitalOnScreenControl.getControlKnob().setScale(scale);
		this.mDigitalOnScreenControl.refreshControlKnobPosition();
		
		scene.setChildScene(this.mDigitalOnScreenControl);
		
		this.mDigitalOnScreenControl2 = new DigitalOnScreenControl(CAMERA_WIDTH-this.RightOnScreenControlBaseTextureRegion.getWidth()*(scale),  CAMERA_HEIGHT -this.RightOnScreenControlBaseTextureRegion.getHeight()*scale, this.mCamera, this.RightOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, this.getVertexBufferObjectManager(), new IOnScreenControlListener() {
			private float tempX, tempY;
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				if((pValueX != tempX && pValueY != tempY) || pValueX != tempX || pValueY != tempY) {
						vibrator.vibrate(10);
				}
				Log.i("VirtualController", "Right: " + pValueX+ " " + pValueY);
				if(pValueX == 0.708f) {
					client.Message[2]= 2;
				}
				else if(pValueX == -0.708f) {
					client.Message[2]= 3;
				}
				if(pValueY == 0.708f) {
					client.Message[3]= 2;
				}
				else if(pValueY == -0.708f) {
					client.Message[3]= 3;
				}
				else {
					client.Message[2]=(int) pValueX;
					client.Message[3]=(int) pValueY;
				}
				client.SendMessage();
				tempX = pValueX;
				tempY = pValueY;

				
				

			}
		});
	
		this.mDigitalOnScreenControl2.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.mDigitalOnScreenControl2.getControlBase().setAlpha(0.5f);
		this.mDigitalOnScreenControl2.getControlBase().setScaleCenter(0, 0);
		this.mDigitalOnScreenControl2.getControlBase().setScale(scale);
		this.mDigitalOnScreenControl2.getControlKnob().setScale(scale);
		this.mDigitalOnScreenControl2.refreshControlKnobPosition();
		this.mDigitalOnScreenControl2.setAllowDiagonal(true);
		

		this.mDigitalOnScreenControl.setChildScene(this.mDigitalOnScreenControl2);
		final Sprite LeftShoulderButtonSprite = new Sprite(0, 0, this.LeftShoulderButtonRegion, this.mEngine.getVertexBufferObjectManager())
		{
		    @Override
		    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float X, float Y) 
		    {
		    	if (pSceneTouchEvent.isActionDown()) {
			    	vibrator.vibrate(10);
			    	client.Message[4] = 1;
		    	}
		    	else if(pSceneTouchEvent.isActionUp()) {
		    		client.Message[4] = 0;
		    	}
		        return true;
		    };
		};
		final Sprite RightShoulderButtonSprite = new Sprite(CAMERA_WIDTH-RightShoulderButtonRegion.getWidth()*scale, 0, this.RightShoulderButtonRegion, this.mEngine.getVertexBufferObjectManager()) {
		
		    @Override
		    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float X, float Y) 
		    {
		        if (pSceneTouchEvent.isActionDown()) {
		     
		        	vibrator.vibrate(10); 
		        	client.Message[5] = 1;
		        }
		    	else if(pSceneTouchEvent.isActionUp()) {
		    		client.Message[5] = 0;
		    	}
		        return true;
		    };
		};
		final Sprite SelectStartButtonsSprite = new Sprite(CAMERA_WIDTH/2, 0, this.SelectStartButtonsRegion, this.mEngine.getVertexBufferObjectManager()) {
			
		    @Override
		    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float X, float Y) 
		    {
		        if (pSceneTouchEvent.isActionDown()) {
		        	if((float)RightShoulderButtonRegion.getWidth()/2 <= X) {
		        		client.Message[6] = 1;
		        	}
		        	else {
		        		client.Message[7] = 1;
		        	}
		        	vibrator.vibrate(10); 
		        }
		    	else if(pSceneTouchEvent.isActionUp()) {
		    		client.Message[6] = 0;
		    		client.Message[7] = 0;
		    	}
		        return true;
		    };
		};

		LeftShoulderButtonSprite.setScaleCenter(0, 0);
		LeftShoulderButtonSprite.setScale(scale);
		RightShoulderButtonSprite.setScaleCenter(0, 0);
		RightShoulderButtonSprite.setScale(scale);
		SelectStartButtonsSprite.setScaleCenter(64, 0);
		SelectStartButtonsSprite.setScale(1.75f*scale);
		this.mDigitalOnScreenControl.attachChild(LeftShoulderButtonSprite);
		this.mDigitalOnScreenControl.attachChild(RightShoulderButtonSprite);
		this.mDigitalOnScreenControl.attachChild(SelectStartButtonsSprite);
		
		scene.registerTouchArea(LeftShoulderButtonSprite);
		scene.registerTouchArea(RightShoulderButtonSprite);
		scene.registerTouchArea(SelectStartButtonsSprite);
		
		return scene;
	}


}
