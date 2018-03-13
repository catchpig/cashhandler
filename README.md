# cashhandler
[![](https://jitpack.io/v/zhuazhu/cashhandler.svg)](https://jitpack.io/#zhuazhu/cashhandler)

* 异常信息的捕获和收集
* 日志信息的保存路径:Android/data/包名/files/cash_log
* 可实现异常日志上传到远程服务器

## 使用Gradle方式
   在Project的build.gradle中添加:
   ```
   allprojects {
    	repositories {
    		maven { url 'https://jitpack.io' }
    	}
    }
   ```

   添加依赖:
   ```
   compile 'com.github.zhuazhu:cashhandler:1.0.0'
   ```

## 使用方式

1.添加权限
```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```
2.在Application中初始化
```
CashHandler cashHandler = CashHandler.getInstance();
```
3.如果需要收集日志到远程服务器,可以实现接口
```
cashHandler.setCashListener(new CashListener() {
     @Override
     public void cashFilePath(String fileName) {
            //返回日志的本地路径
            Toast.makeText(getApplicationContext(),fileName,Toast.LENGTH_LONG).show();
     }
});
```
