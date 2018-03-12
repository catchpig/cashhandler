package zhuazhu.cash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 创建时间:2018-03-11 20:01<br/>
 * 创建人: 李涛<br/>
 * 修改人: 李涛<br/>
 * 修改时间: 2018-03-11 20:01<br/>
 * 描述:异常处理
 */

public class CashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CashHandler";
    private static CashHandler mCashHandler;
    private Context mContext;
    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Map<String, String> mInfos = new HashMap<>();
    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");

    private CashHandler() {
    }

    public static CashHandler getInstance() {
        if (mCashHandler == null) {
            mCashHandler = new CashHandler();
        }
        return mCashHandler;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!HandlerException(e) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(t, e);
        }
    }

    /**
     * 异常回调接口
     */
    private CashListener mCashListener;
    public void setCashListener(CashListener listener){
        mCashListener = listener;
    }
    /**
     * 处理异常
     *
     * @param ex 异常信息
     */
    private boolean HandlerException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        try {
            collectDeviceInfo();
            final String fileName = saveCashInfo(ex);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    if (mCashListener!=null) {
                        mCashListener.cashFilePath(fileName);
                    }
                    Looper.loop();
                }
            }).start();
        } catch (Exception e) {
            Log.e(TAG,"HandlerException----"+e.getMessage());
        }
        return true;
    }

    /**
     * 设计设备和版本信息
     */
    private void collectDeviceInfo() throws Exception {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager
                .GET_ACTIVITIES);
        if (pi != null) {
            //版本号
            String versionName = pi.versionName;
            String versionCode = String.valueOf(pi.versionCode);
            mInfos.put("versionName", versionName);
            mInfos.put("versionCode", versionCode);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            mInfos.put(field.getName(), field.get(null).toString());
        }
    }

    /**
     * 保存异常信息到本地(路径:)
     * @param ex
     */
    private String saveCashInfo(Throwable ex){
        StringBuffer buffer = new StringBuffer();
        Iterator<String> iterator = mInfos.keySet().iterator();
        //拼装设备信息
        if (iterator.hasNext()) {
            String key = iterator.next();
            String value = mInfos.get(key);
            buffer.append(key);
            buffer.append("=");
            buffer.append(value);
            buffer.append("\n");
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        buffer.append(result);
        FileOutputStream fos = null;
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = time + "-" + timestamp + ".log";
            //文件路径:Android/data/包名/files/cash_log
            String file_dir = mContext.getExternalFilesDir("cash_log").getPath();
            File dir = new File(file_dir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(file_dir, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            fos.write(buffer.toString().getBytes("UTF-8"));
            fos.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "保存文件异常---"+e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
