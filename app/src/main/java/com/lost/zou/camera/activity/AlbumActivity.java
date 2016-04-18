package com.lost.zou.camera.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.lost.zou.camera.R;
import com.lost.zou.camera.common.Constant;
import com.lost.zou.camera.common.album.Album;
import com.lost.zou.camera.common.util.FileUtil;
import com.lost.zou.camera.common.util.StringUtils;
import com.lost.zou.camera.common.view.component.PagerSlidingTabStrip;
import com.lost.zou.camera.fragment.AlbumFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 相册界面
 */
public class AlbumActivity extends AppCompatActivity {
    private String dcimPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera";

    private Map<String, Album> albums;
    private List<String> mPaths = new ArrayList<String>();

    @Bind(R.id.indicator)
    PagerSlidingTabStrip tab;
    @Bind(R.id.pager)
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ButterKnife.bind(this);

        albums = FileUtil.findGalleries(this, mPaths);

        //ViewPager的adapter
        FragmentPagerAdapter adapter = new TabPageIndicatorAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tab.setViewPager(pager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent result) {
        if (requestCode == Constant.REQUEST_CROP && resultCode == RESULT_OK) {
//            Intent newIntent = new Intent(this, PhotoProcessActivity.class);
//            newIntent.setData(result.getData());
//            startActivity(newIntent);
        }
    }


    class TabPageIndicatorAdapter extends FragmentPagerAdapter {
        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //新建一个Fragment来展示ViewPager item的内容，并传递参数
            return AlbumFragment.newInstance(albums.get(mPaths.get(position)).getPhotos());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Album album = albums.get(mPaths.get(position % mPaths.size()));
            if (StringUtils.equalsIgnoreCase(dcimPath, album.getAlbumUri())) {
                return "胶卷相册";
            } else if (album.getTitle().length() > 13) {
                return album.getTitle().substring(0, 11) + "...";
            }
            return album.getTitle();
        }

        @Override
        public int getCount() {
            return mPaths.size();
        }
    }


}
