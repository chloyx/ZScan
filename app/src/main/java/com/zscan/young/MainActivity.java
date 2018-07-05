package com.zscan.young;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scan.young.BaseScanActivity;
import com.zscan.test.R;

public class MainActivity extends BaseScanActivity {
    @Override
    public int bindLayoutId() {
        return R.layout.activity_base_scan;
    }

    @Override
    public void initParmers() {

    }

    @Override
    public void initViewsAndValues(Bundle savedInstanceState) {

    }

    @Override
    public void initScanViews(Bundle savedInstanceState) {
        surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
        mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
        mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);

        mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);

    }

    @Override
    public boolean isPlayBeep() {
        return true;
    }

    @Override
    public Rect setFramingRect(Point screenResolution) {
        int width = screenResolution.x;
        //普通扫描框，正方形位于页面中部
        int height = screenResolution.y;
        int leftOffset = DensityUtils.dp2px(this, this.getResources().getDimension(R.dimen.dip50));
        int topOffset = DensityUtils.dp2px(this, this.getResources().getDimension(R.dimen.dip50));
        return new Rect(leftOffset, topOffset, width - leftOffset * 2, height - topOffset * 2);
    }

    @Override
    public boolean isSingleScan() {
        return false;
    }

    @Override
    public void scanResult(String scanResult) {
        Toast.makeText(this,scanResult,Toast.LENGTH_SHORT).show();
    }
}
