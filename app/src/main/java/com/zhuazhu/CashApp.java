package com.zhuazhu;

import android.app.Application;
import android.widget.Toast;

import zhuazhu.cash.CashHandler;
import zhuazhu.cash.CashListener;

/**
 * 创建时间:2018-03-12 18:23<br/>
 * 创建人: 李涛<br/>
 * 修改人: 李涛<br/>
 * 修改时间: 2018-03-12 18:23<br/>
 * 描述:
 */

public class CashApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CashHandler cashHandler = CashHandler.getInstance();
        //异常日志路径回调
        cashHandler.setCashListener(new CashListener() {
            @Override
            public void cashFilePath(String fileName) {
                Toast.makeText(getApplicationContext(),fileName,Toast.LENGTH_LONG).show();
            }
        });
        cashHandler.init(this);
    }
}
