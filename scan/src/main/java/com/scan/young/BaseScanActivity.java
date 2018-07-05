package com.scan.young;

import android.Manifest;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.zxing.BarcodeFormat;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decoding.CaptureActivityHandler;

import java.io.IOException;
import java.util.Vector;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;

/**
 * 类描述：
 * 创建人：yangxing
 * 创建时间：2018/5/30 13:02
 * 修改人：yangxing
 * 修改时间：2018/5/30 13:02
 * 修改备注：
 */

public abstract class BaseScanActivity extends Activity implements SurfaceHolder.Callback {
    /**
     * The Handler.
     */
//手机扫描
    protected static CaptureActivityHandler handler;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;
    protected RelativeLayout mContainer = null;
    protected RelativeLayout mCropLayout = null;
    protected ImageView mQrLineView;
    public boolean isNeedCapture = false;
    public SurfaceView surfaceView;

    /**
     * Is need capture boolean.
     *
     * @return the boolean
     */
    public boolean isNeedCapture() {
        return isNeedCapture;
    }

    /**
     * Sets need capture.
     *
     * @param isNeedCapture the is need capture
     */
    public void setNeedCapture(boolean isNeedCapture) {
        this.isNeedCapture = isNeedCapture;
    }

    /**
     * Gets x.
     *
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * Sets x.
     *
     * @param x the x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets y.
     *
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * Sets y.
     *
     * @param y the y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets crop width.
     *
     * @return the crop width
     */
    public int getCropWidth() {
        return cropWidth;
    }

    /**
     * Sets crop width.
     *
     * @param cropWidth the crop width
     */
    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    /**
     * Gets crop height.
     *
     * @return the crop height
     */
    public int getCropHeight() {
        return cropHeight;
    }

    /**
     * Sets crop height.
     *
     * @param cropHeight the crop height
     */
    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCamera();
        initParmers();
        setContentView(bindLayoutId());

        initScanViews(savedInstanceState);
        initScan();
        initViewsAndValues(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    /**
     * 设置Fragment布局
     *
     * @return int
     */
    public abstract int bindLayoutId();

    /**
     * 初始化变量
     */
    public abstract void initParmers();

    /**
     * 初始化页面控件
     *
     * @param savedInstanceState the saved instance state
     */
    public abstract void initViewsAndValues(Bundle savedInstanceState);

    protected void initScan() {
        setCameraAuth();
        TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
        mAnimation.setDuration(1500);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());
        mQrLineView.setAnimation(mAnimation);
    }

    /**
     * 初始化扫描ui，不做会导致空指针崩溃
     */
    public abstract void initScanViews(Bundle savedInstanceState);

    public void initCamera() {
        CameraManager.init(getApplication(), this);
        hasSurface = false;
    }

    /**
     * 检查相机权限
     */
    protected void setCameraAuth() {
        HiPermission.create(this).checkSinglePermission(Manifest.permission.CAMERA, new PermissionCallback() {
            @Override
            public void onClose() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onDeny(String permission, int position) {

            }

            @Override
            public void onGuarantee(String permission, int position) {
                restartScan();
            }
        });
    }

    /**
     * 是否播放声音
     * @return
     */
    public abstract boolean isPlayBeep();
    public void restartScan() {
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        if (isPlayBeep()) {
            //播放声音
            initBeepSound();
        }
        vibrate = true;
    }

    /**
     * 摄像头
     *
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            Point point = CameraManager.get().getCameraResolution();
            int width = point.y;
            int height = point.x;

            int x = mCropLayout.getLeft() * width / mContainer.getWidth();
            int y = mCropLayout.getTop() * height / mContainer.getHeight();

            int cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();

            setX(x);
            setY(y);
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);
            // 设置是否需要截图
            setNeedCapture(false);

        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(Looper.getMainLooper(), this, decodeFormats,
                    characterSet);
        }
    }

    /**
     * 设置扫描范围，CameraManager中调用
     *
     * @param screenResolution
     */
    public abstract Rect setFramingRect(Point screenResolution);

    @Override
    protected void onResume() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                restartScan();
            }
        }).start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeScan();
    }

    /**
     * Close scan.
     */
    public void closeScan() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    /**
     * Handle decode.
     *
     * @param str the str
     */
    public void handleDecode(String str) {
        playBeepSoundAndVibrate();
        scanResult(str);
        if (!isSingleScan()){
            restartPreview();
        }
    }

    /**
     *
     */
    protected void restartPreview(){
        //连续扫描，不发送此消息扫描一次结束后就不能再次扫描
        handler.sendEmptyMessage(R.id.restart_preview);
    }
    /**
     *  判断是单扫还是连扫
     Judge whether it's single sweep or continuous sweep
     */
    public abstract boolean isSingleScan();
    /**
     * Scan result.
     *
     * @param scanResult 扫描结果（单号）
     */
    public abstract void scanResult(String scanResult);

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }
    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }
    private static final long VIBRATE_DURATION = 200L;
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    /**
     * 摄像头
     *
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * 摄像头
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    /**
     * 摄像头
     *
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    /**
     * 摄像头
     *
     * @return handler
     */
    public Handler getHandler() {
        return handler;
    }
}
