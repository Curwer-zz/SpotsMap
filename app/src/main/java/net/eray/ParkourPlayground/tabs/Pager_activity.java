package net.eray.ParkourPlayground.tabs;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;

import net.eray.ParkourPlayground.floatingActionButton.FloatingActionButton;

import net.eray.ParkourPlayground.R;

/**
 * Created by Niclas on 2014-09-06.
 */
public class Pager_activity extends FragmentActivity {

    ViewPager Tab;
    TabPagerAdapter TabAdapter;
    FloatingActionButton mFab;
    String inputUsername, inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pager_);

        mFab.bringToFront();

        TabAdapter = new TabPagerAdapter(getSupportFragmentManager());

        Tab = (ViewPager) findViewById(R.id.pager);
        Tab.setAdapter(TabAdapter);
        Tab.setCurrentItem(1);

        Tab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, float v, final int i2) {

            }

            @Override
            public void onPageSelected(final int position) {
                if (position == 0) {
                    mFab.setDrawable(getResources().getDrawable(R.drawable.ic_action_done));
                } else if (position == 1) {
                    mFab.setDrawable(getResources().getDrawable(R.drawable.ic_action_lock_open));
                } else if (position == 2) {
                    mFab.setDrawable(getResources().getDrawable(R.drawable.ic_action_done));
                }

                mFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position == 0) {

                        } else if (position == 1) {

                        } else if (position == 2) {

                        }
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int position) {

            }
        });
    }

}