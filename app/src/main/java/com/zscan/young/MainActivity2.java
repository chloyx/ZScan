package com.zscan.young;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.chloyx.zscan.ZScanCodeUtil;
import com.zscan.test.R;

/**
 * 类描述：
 * 创建人：yangxing
 * 创建时间：2018/5/30 18:54
 * 修改人：yangxing
 * 修改时间：2018/5/30 18:54
 * 修改备注：
 */

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener{
    ImageView imageView;
    ImageView imageView2;
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.iv_test1);
        imageView2 = (ImageView)findViewById(R.id.iv_test2);

        findViewById(R.id.btn_test1).setOnClickListener(this);
        findViewById(R.id.btn_test2).setOnClickListener(this);
        findViewById(R.id.btn_test3).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_test1:
                imageView.setImageBitmap(ZScanCodeUtil.createQRCode("vnsdnfvsklnklsnvkjdsnv",200));
                break;
            case R.id.btn_test2:
                imageView2.setImageBitmap(ZScanCodeUtil.creatBarcode(this,"nsdfnklsnfklsngfnsd",600,100,true));
//                imageView2.setImageBitmap(QRCodeUtil.getBarcode("nsdfnklsnfklsngfnsd",200,100));
                break;
            case R.id.btn_test3:
//                String code = QRCodeUtil.readImage(((BitmapDrawable) imageView.getDrawable()).getBitmap());
//                Toast.makeText(this,code,Toast.LENGTH_SHORT).show();
                String code2 = ZScanCodeUtil.readBarcodeImage(((BitmapDrawable) imageView2.getDrawable()).getBitmap());
                Toast.makeText(this,code2,Toast.LENGTH_SHORT).show();

//                QRCodeUtil.scanningImage(((BitmapDrawable) imageView2.getDrawable()).getBitmap());
                break;
        }
    }
}
