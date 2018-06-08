package com.chloyx.zscan;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.scan.young.R;
import com.zbar.lib.decoding.CaptureActivityHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类描述：过滤规则
 * 创建人：yangxing
 * 创建时间：2018/5/30 18:35
 * 修改人：yangxing
 * 修改时间：2018/5/30 18:35
 * 修改备注：
 */

public class FilterRules {

    private static String regEx = "[`~!@#$%^&*()+=|{}':',\\[\\].<>/?~！@#￥%……&;*（）——+|{}【】‘；：”“’。，、？|-]";

    /**
     * 检查是否符合快递单号规则
     * @return
     */
    public static boolean isConformCodeRulesScan(Context context, String expressCode, CaptureActivityHandler handler,boolean isShowToast){
        if (TextUtils.isEmpty(expressCode)){
            return false;
        }
        if (expressCode.length() <= 6 || expressCode.contains("www")
                ||expressCode.contains(":")
                ||expressCode.contains("http")
                ||expressCode.contains("https")
                || expressCode.length() > 30) {
            if (isShowToast) {
                Toast.makeText(context,"您的单号格式不正确！",Toast.LENGTH_SHORT);
            }
            if (handler != null) {
                handler.sendEmptyMessage(R.id.restart_preview);
            }
            return false;
        }
        if (expressCode.length() > 2 && expressCode.substring(0,2).toLowerCase().equals("lp")
                ||isHasSpecialSymbol(expressCode)) {
            if (isShowToast) {
                Toast.makeText(context,"扫描失败，请重新扫描正确单号！",Toast.LENGTH_SHORT);
            }
            if (handler != null) {
                handler.sendEmptyMessage(R.id.restart_preview);
            }
            return false;
        }
        return true;
    }

    /**
     * 是否含有特殊字符
     * @param code
     * @return
     */
    public static boolean isHasSpecialSymbol(String code){
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(code);
        return matcher.matches();
    }
}
