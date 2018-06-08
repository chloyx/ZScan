package com.chloyx.zscan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by yangxing on 2017/5/23.
 * 二维码生成工具类
 */

public class ZScanCodeUtil {
    public static Bitmap createQRCodeAddLogo(String content, Bitmap logoBm, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            if (logoBm != null) {
                return addLogo(Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565), logoBm);
            } else {
                return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

//    protected static Bitmap addLogo(Bitmap qrBitmap, Bitmap logoBitmap) {
//        int qrBitmapWidth = qrBitmap.getWidth();
//        int qrBitmapHeight = qrBitmap.getHeight();
//        int logoBitmapWidth = logoBitmap.getWidth();
//        int logoBitmapHeight = logoBitmap.getHeight();
//        Bitmap blankBitmap = Bitmap.createBitmap(qrBitmapWidth, qrBitmapHeight, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(blankBitmap);
//        canvas.drawBitmap(qrBitmap, 0, 0, null);
//        canvas.save(Canvas.ALL_SAVE_FLAG);
//        float scaleSize = 1.0f;
//        while ((logoBitmapWidth / scaleSize) > (qrBitmapWidth / 5) || (logoBitmapHeight / scaleSize) > (qrBitmapHeight / 5)) {
//            scaleSize *= 2;
//        }
//        float sx = 1.0f / scaleSize;
//        canvas.scale(sx, sx, qrBitmapWidth / 2, qrBitmapHeight / 2);
//        canvas.drawBitmap(logoBitmap, (qrBitmapWidth - logoBitmapWidth) / 2, (qrBitmapHeight - logoBitmapHeight) / 2, null);
//        canvas.restore();
//        return blankBitmap;
//    }

    /**
     ** 在二维码中间添加Logo图案
     *      
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

    /**
     * 生成二维码
     *
     * @param str
     * @param widthAndHeight
     * @return
     */
    public static Bitmap createQRCode(String str, int widthAndHeight) {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //如果生成的二维码背景色为透明，zxing无法再次解析
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xfff8f8f8;
                }

            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 生成二维码，效果很不错
     *
     * @param str
     * @return
     */
    public static Bitmap create2dcode(String str) {
        HashMap<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "gbk");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 500, 500, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0xffffffff;
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
    //生成条形码

    /**
     * 生成条形码
     *
     * @param context
     * @param contents
     * @param desiredWidth
     * @param desiredHeight
     * @param displayCode   是否要在条形码下方显示生成的内容
     * @return
     */
    public static Bitmap creatBarcode(Context context, String contents,
                                      int desiredWidth, int desiredHeight, boolean displayCode) {
        Bitmap resultBitmap = null;
        int marginW = 20;
        BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;

        if (displayCode) {
            Bitmap barcodeBitmap = null;
            try {
                barcodeBitmap = encodeAsBitmap(contents, barcodeFormat,
                        desiredWidth, desiredHeight);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            Bitmap codeBitmap = creatCodeBitmap(contents, desiredWidth + 2
                    * marginW, desiredHeight, context);
            resultBitmap = mixtureBitmap(barcodeBitmap, codeBitmap, new PointF(
                    0, desiredHeight));
        } else {
            try {
                resultBitmap = encodeAsBitmap(contents, barcodeFormat,
                        desiredWidth, desiredHeight);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

        return resultBitmap;
    }

    protected static Bitmap encodeAsBitmap(String contents,
                                           BarcodeFormat format, int desiredWidth, int desiredHeight) throws WriterException {
        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;
//        final int WHITE = 0xfff8f8f8;
//        final int BLACK = 0xff000000;


        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = null;
        result = writer.encode(contents, format, desiredWidth,
                desiredHeight, null);

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        // All are 0, or black, by default
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    protected static Bitmap creatCodeBitmap(String contents, int width,
                                            int height, Context context) {
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setText(contents);
        tv.setHeight(height);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setWidth(width);
        tv.setDrawingCacheEnabled(true);
        tv.setTextColor(Color.BLACK);
        tv.setBackgroundColor(Color.WHITE);
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

        tv.buildDrawingCache();
        Bitmap bitmapCode = tv.getDrawingCache();
        return bitmapCode;
    }

    protected static Bitmap mixtureBitmap(Bitmap first, Bitmap second,
                                          PointF fromPoint) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        int marginW = 20;
        Bitmap newBitmap = Bitmap.createBitmap(
                first.getWidth() + second.getWidth() + marginW,
                first.getHeight() + second.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, marginW, 0, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();

        return newBitmap;
    }

    /**
     * 用于将给定的内容生成成一维码 注：目前生成内容为中文的话将直接报错，要修改底层jar包的内容
     *
     * @param content 将要生成一维码的内容
     * @return 返回生成好的一维码bitmap
     * @throws WriterException WriterException异常
     */
    public static Bitmap CreateOneDCode(String content) {
        // 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = null;
        try {
            matrix = new MultiFormatWriter().encode(content,
                    BarcodeFormat.CODE_128, 500, 200);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 生成条形码
     *
     * @param str
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBarcode(String str, Integer width,
                                    Integer height) {

        if (width == null || width < 200) {
            width = 200;
        }

        if (height == null || height < 50) {
            height = 50;
        }

        try {
            // 文字编码
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

            BitMatrix bitMatrix = new MultiFormatWriter().encode(str,
                    BarcodeFormat.CODE_128, width, height, hints);

            return BitMatrixToBitmap(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * BitMatrix转换成Bitmap
     *
     * @param matrix
     * @return
     */
    private static Bitmap BitMatrixToBitmap(BitMatrix matrix) {
        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
            }
        }
        return createBitmap(width, height, pixels);
    }

    /**
     * 生成Bitmap
     *
     * @param width
     * @param height
     * @param pixels
     * @return
     */
    private static Bitmap createBitmap(int width, int height, int[] pixels) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 解析QR图内容，二维码
     *
     * @param bitmap
     * @return
     */
    public static String readQRImage(Bitmap bitmap) {
        String content = null;
        Map<DecodeHintType, String> hints = new HashMap<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
        //一定要加以下这个代码：
        bitmap.getPixels(data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), data);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
        QRCodeReader reader = new QRCodeReader();
        try {
            Result result = reader.decode(bitmap1, hints);
            // 得到解析后的文字
            content = result.getText();
        } catch (NotFoundException e) {
            Log.e("hxy", "NotFoundException");
        } catch (ChecksumException e) {
            Log.e("hxy", "ChecksumException");
        } catch (FormatException e) {
            Log.e("hxy", "FormatException");
        }
        return content;
    }

    /**
     * 解析条形码
     *
     * @param bitmap
     * @return
     */
    public static String readBarcodeImage(Bitmap bitmap) {
        String content = null;
        Map<DecodeHintType, String> hints = new HashMap<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
        //一定要加以下这个代码：
        bitmap.getPixels(data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), data);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        try {

            Result result = multiFormatReader.decode(bitmap1, hints);
            // 得到解析后的文字
            content = result.getText();
        } catch (NotFoundException e) {
            Log.e("hxy", "NotFoundException");
        }
        return content;
    }
//    /**
//     * 解析二维码图片
//     * @param bitmap
//     * @return
//     */
//    public static String scanningImage(Bitmap bitmap) {
////        if (TextUtils.isEmpty(path)) {
////            return null;
////
////        }
//        Hashtable<DecodeHintType, String> hints = new Hashtable();
//        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8"); // 设置二维码内容的编码
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true; // 先获取原大小
////        scanBitmap = BitmapFactory.decodeFile(path,options);
////        options.inJustDecodeBounds = false;
////        int sampleSize = (int) (options.outHeight / (float) 200);
////        if (sampleSize <= 0)
////            sampleSize = 1;
////
////        options.inSampleSize = sampleSize;
////
////        scanBitmap = BitmapFactory.decodeFile(path, options);
//        int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
//        bitmap.getPixels(data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
//        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap.getWidth(),bitmap.getHeight(),data);
//        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
//        QRCodeReader reader = new QRCodeReader();
//        Result result = null;
//        String code = "";
//        try {
//            result = reader.decode(binaryBitmap, hints);
//            code = result.getText();
//        } catch (NotFoundException e) {
//            Log.e("hxy","NotFoundException");
//        }catch (ChecksumException e){
//            Log.e("hxy","ChecksumException");
//        }catch(FormatException e){
//            Log.e("hxy","FormatException");
//        }
//
//        return code;
//
//
//    }
//public static String scanningImage(Bitmap scanBitmap) {
////    if (TextUtils.isEmpty(path)) {
////        return null;
////
////    }
//
//    BitmapFactory.Options options = new BitmapFactory.Options();
//    options.inJustDecodeBounds = true; // 先获取原大小
//    options.inJustDecodeBounds = false;
//    int sampleSize = (int) (options.outHeight / (float) 200);
//    if (sampleSize <= 0)
//        sampleSize = 1;
//
//    options.inSampleSize = sampleSize;
//    byte[] data = getYUV420sp(scanBitmap.getWidth(), scanBitmap.getHeight(), scanBitmap);
//
//    Hashtable<DecodeHintType, Object> hints = new Hashtable();
//    hints.put(DecodeHintType.CHARACTER_SET, "UTF-8"); // 设置二维码内容的编码
//    hints.put(DecodeHintType.TRY_HARDER,Boolean.TRUE);
//    hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
//    PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data,
//            scanBitmap.getWidth(),
//            scanBitmap.getHeight(),
//            0, 0,
//            scanBitmap.getWidth(),
//            scanBitmap.getHeight(),
//            false);
//
//    BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
//    QRCodeReader reader2= new QRCodeReader();
//    Result result = null;
//    String code = "";
//    try {
//        result = reader2.decode(bitmap1, hints);
//        code = result.getText();
//        Log.e("hxy",result.getText());
//    } catch (NotFoundException e) {
//        Log.e("hxy","NotFoundException");
//    }catch (ChecksumException e){
//        Log.e("hxy","ChecksumException");
//    }catch(FormatException e){
//        Log.e("hxy","FormatException");
//    }
//
//    return code;
//
//
//}
//
//
//
//    public static byte[] getYUV420sp(int inputWidth, int inputHeight,
//                                     Bitmap scaled) {
//        int[] argb = new int[inputWidth * inputHeight];
//
//        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);
//
//        byte[] yuv = new byte[inputWidth * inputHeight * 3 / 2];
//
//        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);
//
//        scaled.recycle();
//
//        return yuv;
//    }
//
//
//    private static void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width,
//                                       int height) {
//        // 帧图片的像素大小
//        final int frameSize = width * height;
//        // ---YUV数据---
//        int Y, U, V;
//        // Y的index从0开始
//        int yIndex = 0;
//        // UV的index从frameSize开始
//        int uvIndex = frameSize;
//
//        // ---颜色数据---
////      int a, R, G, B;
//        int R, G, B;
//        //
//        int argbIndex = 0;
//        //
//
//        // ---循环所有像素点，RGB转YUV---
//        for (int j = 0; j < height; j++) {
//            for (int i = 0; i < width; i++) {
//
//                // a is not used obviously
////              a = (argb[argbIndex] & 0xff000000) >> 24;
//                R = (argb[argbIndex] & 0xff0000) >> 16;
//                G = (argb[argbIndex] & 0xff00) >> 8;
//                B = (argb[argbIndex] & 0xff);
//                //
//                argbIndex++;
//
//                // well known RGB to YUV algorithm
//                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
//                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
//                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;
//
//                //
//                Y = Math.max(0, Math.min(Y, 255));
//                U = Math.max(0, Math.min(U, 255));
//                V = Math.max(0, Math.min(V, 255));
//
//                // NV21 has a plane of Y and interleaved planes of VU each
//                // sampled by a factor of 2
//                // meaning for every 4 Y pixels there are 1 V and 1 U. Note the
//                // sampling is every other
//                // pixel AND every other scanline.
//                // ---Y---
//                yuv420sp[yIndex++] = (byte) Y;
//
//                // ---UV---
////              if ((j % 2 == 0) && (i % 2 == 0)) {
////
////
////
////                  yuv420sp[uvIndex++] = (byte) V;
////
////                  yuv420sp[uvIndex++] = (byte) U;
////              }
//            }
//        }
//    }
}
