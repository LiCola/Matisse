package com.zhihu.matisse.sample;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.sample.internal.SourceType;
import com.zhihu.matisse.sample.internal.ui.FixMediaSelectionFragment;

public class GroupActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group);

    SelectionSpec selectionSpec = SelectionSpec.getCleanInstance();
    selectionSpec.mimeTypeSet = MimeType.ofAll();
    selectionSpec.mediaTypeExclusive = true;
    selectionSpec.orientation = SCREEN_ORIENTATION_UNSPECIFIED;
    selectionSpec.imageEngine = new Glide4Engine();
    selectionSpec.spanCount = 4;

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    Fragment fragment = FixMediaSelectionFragment.newInstance(SourceType.TYPE_IMAGE,"空内容");
    transaction.replace(R.id.container, fragment).commitNowAllowingStateLoss();


  }
}
