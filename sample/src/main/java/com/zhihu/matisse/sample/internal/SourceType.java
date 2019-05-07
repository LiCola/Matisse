package com.zhihu.matisse.sample.internal;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author LiCola
 * @date 2019-05-06
 */
public class SourceType {

  public static final int TYPE_IMAGE = 0;
  public static final int TYPE_VIDEO = 1;

  @IntDef({TYPE_IMAGE, TYPE_VIDEO})
  @Retention(RetentionPolicy.SOURCE)
  public @interface Type {

  }
}
