/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihu.matisse.sample.internal.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.ui.widget.SquareFrameLayout;
import com.zhihu.matisse.sample.R;
import java.util.Locale;

public class MediaGrid extends SquareFrameLayout implements View.OnClickListener {

  private ImageView mThumbnail;
  private ImageView mOver;
  private ImageView mGifTag;
  private TextView mVideoDuration;

  private Item mMedia;
  private PreBindInfo mPreBindInfo;
  private OnMediaGridClickListener mListener;

  public MediaGrid(Context context) {
    super(context);
    init(context);
  }

  public MediaGrid(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  private void init(Context context) {
    LayoutInflater.from(context).inflate(R.layout.media_grid_over_content, this, true);

    mThumbnail = (ImageView) findViewById(R.id.media_thumbnail);
    mOver = (ImageView) findViewById(R.id.over);
    mGifTag = (ImageView) findViewById(R.id.gif);
    mVideoDuration = (TextView) findViewById(R.id.video_duration);

    mThumbnail.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    if (mListener != null) {
      if (v == mThumbnail) {
        mListener.onThumbnailClicked(mThumbnail, mMedia, mPreBindInfo.mViewHolder);
      }
    }
  }

  public void preBindMedia(PreBindInfo info) {
    mPreBindInfo = info;
  }

  public void bindMedia(Item item) {
    mMedia = item;
    setGifTag();
    setImage();
    setVideoDuration();
  }

  public Item getMedia() {
    return mMedia;
  }

  private void setGifTag() {
    mGifTag.setVisibility(mMedia.isGif() ? View.VISIBLE : View.GONE);
  }


  private void setImage() {
    if (mMedia.isGif()) {
      SelectionSpec.getInstance().imageEngine.loadGifThumbnail(getContext(), mPreBindInfo.mResize,
          mPreBindInfo.mPlaceholder, mThumbnail, mMedia.getContentUri());
    } else {
      SelectionSpec.getInstance().imageEngine.loadThumbnail(getContext(), mPreBindInfo.mResize,
          mPreBindInfo.mPlaceholder, mThumbnail, mMedia.getContentUri());
    }
  }

  private void setVideoDuration() {
    if (mMedia.isVideo()) {
      mVideoDuration.setVisibility(VISIBLE);
      mVideoDuration.setText(
          String.format(Locale.US, "%d.%ds", mMedia.duration / 1000, mMedia.duration % 1000 / 100));
    } else {
      mVideoDuration.setVisibility(GONE);
    }
  }

  public void setOnMediaGridClickListener(OnMediaGridClickListener listener) {
    mListener = listener;
  }

  public void removeOnMediaGridClickListener() {
    mListener = null;
  }

  public interface OnMediaGridClickListener {

    void onThumbnailClicked(ImageView thumbnail, Item item, RecyclerView.ViewHolder holder);

  }

  public static class PreBindInfo {

    int mResize;
    Drawable mPlaceholder;
    boolean mCheckViewCountable;
    RecyclerView.ViewHolder mViewHolder;

    public PreBindInfo(int resize, Drawable placeholder, boolean checkViewCountable,
        RecyclerView.ViewHolder viewHolder) {
      mResize = resize;
      mPlaceholder = placeholder;
      mCheckViewCountable = checkViewCountable;
      mViewHolder = viewHolder;
    }
  }

}
