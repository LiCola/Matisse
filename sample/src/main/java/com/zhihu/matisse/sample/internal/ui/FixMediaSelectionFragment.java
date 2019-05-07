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
package com.zhihu.matisse.sample.internal.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.licola.llogger.LLogger;
import com.zhihu.matisse.internal.entity.Album;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.ui.widget.MediaGridInset;
import com.zhihu.matisse.internal.utils.PathUtils;
import com.zhihu.matisse.internal.utils.UIUtils;
import com.zhihu.matisse.sample.R;
import com.zhihu.matisse.sample.internal.SourceType;
import com.zhihu.matisse.sample.internal.SourceType.Type;
import com.zhihu.matisse.sample.internal.model.AlbumCollection;
import com.zhihu.matisse.sample.internal.model.AlbumMediaCollection;
import com.zhihu.matisse.sample.internal.ui.adapter.AlbumMediaAdapter;
import com.zhihu.matisse.sample.internal.ui.adapter.AlbumMediaAdapter.OnMediaClickListener;

public class FixMediaSelectionFragment extends Fragment implements
    AlbumCollection.AlbumCallbacks,
    AlbumMediaCollection.AlbumMediaCallbacks, AlbumMediaAdapter.CheckStateListener,
    AlbumMediaAdapter.OnMediaClickListener {

  private static final String KEY_TYPE = "key:type";
  private static final String KEY_EMPTY_HINT = "key:empty_hint";

  private final AlbumMediaCollection mAlbumMediaCollection = new AlbumMediaCollection();
  private RecyclerView mRecyclerView;
  private TextView mTvEmpty;
  private AlbumMediaAdapter mAdapter;
  private OnMediaClickListener mOnMediaClickListener;

  private final AlbumCollection mAlbumCollection = new AlbumCollection();

  private Album mAlbum;//相册集

  @Type
  private int type;

  public static FixMediaSelectionFragment newInstance(@SourceType.Type int type, String emptyHint) {
    FixMediaSelectionFragment fragment = new FixMediaSelectionFragment();
    Bundle args = new Bundle();
    args.putInt(KEY_TYPE, type);
    args.putString(KEY_EMPTY_HINT, emptyHint);
    fragment.setArguments(args);
    return fragment;
  }

  public void setOnMediaClickListener(
      OnMediaClickListener mOnMediaClickListener) {
    this.mOnMediaClickListener = mOnMediaClickListener;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_media_over_selection, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mRecyclerView = view.findViewById(R.id.recyclerview);
    mTvEmpty = view.findViewById(R.id.tv_empty);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    type = getArguments().getInt(KEY_TYPE);

    mAlbumCollection.onCreate(getActivity(), this, type);
    mAlbumCollection.onRestoreInstanceState(savedInstanceState);
    mAlbumCollection.loadAlbums();

  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Context context = getContext();

    mAdapter = new AlbumMediaAdapter(context, mRecyclerView);
    mAdapter.registerCheckStateListener(this);
    mAdapter.registerOnMediaClickListener(this);
    mRecyclerView.setHasFixedSize(true);

    int spanCount;
    SelectionSpec selectionSpec = SelectionSpec.getInstance();
    if (selectionSpec.gridExpectedSize > 0) {
      spanCount = UIUtils.spanCount(getContext(), selectionSpec.gridExpectedSize);
    } else {
      spanCount = selectionSpec.spanCount;
    }

    mRecyclerView.setLayoutManager(new GridLayoutManager(context, spanCount));

    int spacing = getResources().getDimensionPixelSize(R.dimen.media_grid_spacing);
    mRecyclerView.addItemDecoration(new MediaGridInset(spanCount, spacing, false));
    mRecyclerView.setAdapter(mAdapter);

    mAlbumMediaCollection.onCreate(getActivity(), this, type);

  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    mAlbumCollection.onSaveInstanceState(outState);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mAlbumCollection.onDestroy();

  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mAlbumMediaCollection.onDestroy();
  }

  public void refreshMediaGrid() {
    mAdapter.notifyDataSetChanged();
  }

  public void refreshSelection() {
    mAdapter.refreshSelection();
  }

  @Override
  public void onAlbumMediaLoad(Cursor cursor) {
    mAdapter.swapCursor(cursor);
  }

  @Override
  public void onAlbumMediaReset() {
    mAdapter.swapCursor(null);
  }

  @Override
  public void onUpdate() {

  }

  @Override
  public void onMediaClick(Album album, Item item, int adapterPosition) {
    LLogger.d(album, item, adapterPosition, mAlbum);

    Uri uri = item.getContentUri();
    String path = PathUtils.getPath(getContext(), uri);

    LLogger.d(uri,path);

    if (mOnMediaClickListener != null) {
      mOnMediaClickListener.onMediaClick(mAlbum,
          item, adapterPosition);
    }
  }


  @Override
  public void onAlbumLoad(final Cursor cursor) {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
      @Override
      public void run() {
        cursor.moveToPosition(mAlbumCollection.getCurrentSelection());

        Album album = Album.valueOf(cursor);
        onAlbumSelected(album);
      }
    });
  }

  private void onAlbumSelected(Album album) {

    if (album.isEmpty()) {
      mTvEmpty.setVisibility(View.VISIBLE);
      mRecyclerView.setVisibility(View.GONE);
      mTvEmpty.setText(getArguments().getString(KEY_EMPTY_HINT));
    } else {
      mTvEmpty.setVisibility(View.GONE);
      mRecyclerView.setVisibility(View.VISIBLE);
      mAlbum = album;
      mAlbumMediaCollection.load(mAlbum, false);
    }


  }


  @Override
  public void onAlbumReset() {

  }
}
