package com.zhihu.matisse.sample;

import android.app.Application;
import com.licola.llogger.LLogger;

/**
 * @author LiCola
 * @date 2019-05-06
 */
public class MyApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    LLogger.init(true,"Media");
  }
}
