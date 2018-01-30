package com.ws.chaodemo;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * desc:近期忙于其他事情，代码并不是很规范
 * author: ws
 * date: 2018/1/28.
 */

public class CoordinatorActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    TabLayout mTabLayout;
    ViewPager mViewPager;

    AppBarLayout mAppBarLayout;
    RelativeLayout mHeaderView;

    float mHeaderEndX;
    float mHeaderEndY;
    float mHeaderStartX;
    float mHeaderStartY;

    LinearLayout mNameLayout;
    float mNameStartY;

    TextView mIntroView;

    RelativeLayout mAttentionView;

    LinearLayout mItemLayout;
    float mItemStartY;
    float mItemEndY;

    TextView mWorksView;
    TextView mTodoView;
    TextView mTogoView;
    TextView mWorksNumView;
    TextView mTodoNumView;
    TextView mTogoNumView;
    FloatEvaluator mItemEvaluator;

    int mSelectedPosition = 0;

    RelativeLayout mTodoLayout;
    RelativeLayout mTogoLayout;
    RelativeLayout mWorksLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator);
        initComponent();
        initData();

        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);
        setUpIndicatorWidth();

        tabSelectedListener();

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

            }
        });
    }

    private void tabSelectedListener() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mSelectedPosition = tab.getPosition();
                if (mSelectedPosition == 0) {
                    mWorksLayout.setAlpha(1.0f);
                } else if (mSelectedPosition == 1) {
                    mTodoLayout.setAlpha(1.0f);
                } else if (mSelectedPosition == 2) {
                    mTogoLayout.setAlpha(1.0f);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    mWorksLayout.setAlpha(0.5f);
                } else if (tab.getPosition() == 1) {
                    mTodoLayout.setAlpha(0.5f);
                } else if (tab.getPosition() == 2) {
                    mTogoLayout.setAlpha(0.5f);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initData() {
        mHeaderEndX = dip2px(this, 18);
        mHeaderEndY = dip2px(this, 12);
        mItemEndY = dip2px(this, -18);

        mHeaderView.setPivotX(0);
        mHeaderView.setPivotY(0);
        mWorksNumView.setPivotX(0);
        mWorksNumView.setPivotY(0);
        mTodoNumView.setPivotX(0);
        mTodoNumView.setPivotY(0);
        mTogoNumView.setPivotX(0);
        mTogoNumView.setPivotY(0);

        mItemEvaluator = new FloatEvaluator();

        mTabLayout.post(new Runnable() {
            @Override
            public void run() {
                mHeaderStartX = mHeaderView.getX();
                mHeaderStartY = mHeaderView.getY();

                mNameStartY = mNameLayout.getY();

                mItemStartY = mItemLayout.getY();
            }
        });

    }


    private void initComponent() {
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        mHeaderView = (RelativeLayout) findViewById(R.id.rl_header);
        mNameLayout = (LinearLayout) findViewById(R.id.ll_name);
        mIntroView = (TextView) findViewById(R.id.tv_intro);
        mAttentionView = (RelativeLayout) findViewById(R.id.rl_attention);
        mItemLayout = (LinearLayout) findViewById(R.id.ll_tab);
        mWorksView = (TextView) findViewById(R.id.tv_works);
        mTodoView = (TextView) findViewById(R.id.tv_to_do);
        mTogoView = (TextView) findViewById(R.id.tv_to_go);
        mWorksNumView = (TextView) findViewById(R.id.tv_works_num);
        mTodoNumView = (TextView) findViewById(R.id.tv_to_do_num);
        mTogoNumView = (TextView) findViewById(R.id.tv_to_go_num);
        mTodoLayout = (RelativeLayout) findViewById(R.id.rl_to_do);
        mTogoLayout = (RelativeLayout) findViewById(R.id.rl_to_go);
        mWorksLayout = (RelativeLayout) findViewById(R.id.rl_works);
    }

    public void onWorks(View view) {
        mAppBarLayout.setExpanded(false, true);
        mViewPager.setCurrentItem(0);
    }

    public void onTodo(View view) {
        mAppBarLayout.setExpanded(false, true);
        mViewPager.setCurrentItem(1);
    }

    public void onTogo(View view) {
        mAppBarLayout.setExpanded(false, true);
        mViewPager.setCurrentItem(2);
    }

    public void onHeader(View view) {
        mAppBarLayout.setExpanded(true, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        mAppBarLayout.removeOnOffsetChangedListener(this);
        super.onDestroy();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        float ratio = Math.abs((float) verticalOffset / appBarLayout.getTotalScrollRange());
        //设置头像
        mHeaderView.setScaleY(0.5f + 0.5f * (1.0f - ratio));
        mHeaderView.setScaleX(0.5f + 0.5f * (1.0f - ratio));
        if (mHeaderStartX != 0 && mHeaderStartY != 0) {
            mHeaderView.setY(mHeaderStartY + Math.abs(verticalOffset) - (mHeaderStartY - mHeaderEndY) * ratio);
            mHeaderView.setX(mHeaderStartX - (mHeaderStartX - mHeaderEndX) * ratio);
        }

        //设置名称
        if (mNameStartY != 0) {
            mNameLayout.setY(mNameStartY + Math.abs(verticalOffset));
            mNameLayout.setAlpha(1.0f - ratio);
        }

        mIntroView.setAlpha(1.0f - ratio);

        mAttentionView.setAlpha(1.0f - ratio);

        mTabLayout.setAlpha(ratio);

        if (mItemStartY != 0) {
            mItemLayout.setY(mItemStartY + Math.abs(verticalOffset) - (mItemStartY - mItemEndY) * ratio);
        }

        mWorksView.setScaleY(0.7f + 0.3f * (1.0f - ratio));
        mWorksView.setScaleX(0.7f + 0.5f * (1.0f - ratio));
        mTodoView.setScaleY(0.7f + 0.3f * (1.0f - ratio));
        mTodoView.setScaleX(0.7f + 0.3f * (1.0f - ratio));
        mTogoView.setScaleY(0.7f + 0.3f * (1.0f - ratio));
        mTogoView.setScaleX(0.7f + 0.3f * (1.0f - ratio));

        mWorksNumView.setScaleY(0.7f + 0.3f * (1.0f - ratio));
        mWorksNumView.setScaleX(0.7f + 0.3f * (1.0f - ratio));
        mTodoNumView.setScaleY(0.7f + 0.3f * (1.0f - ratio));
        mTodoNumView.setScaleX(0.7f + 0.5f * (1.0f - ratio));
        mTogoNumView.setScaleY(0.7f + 0.3f * (1.0f - ratio));
        mTogoNumView.setScaleX(0.7f + 0.3f * (1.0f - ratio));

        if (mSelectedPosition == 0) {
            mWorksLayout.setAlpha(1.0f);
            mTodoLayout.setAlpha(0.5f + 0.5f * (1.0f - ratio));
            mTogoLayout.setAlpha(0.5f + 0.5f * (1.0f - ratio));
        } else if (mSelectedPosition == 1) {
            mTodoLayout.setAlpha(1.0f);
            mWorksLayout.setAlpha(0.5f + 0.5f * (1.0f - ratio));
            mTogoLayout.setAlpha(0.5f + 0.5f * (1.0f - ratio));
        } else if (mSelectedPosition == 2) {
            mTogoLayout.setAlpha(1.0f);
            mWorksLayout.setAlpha(0.5f + 0.5f * (1.0f - ratio));
            mTodoLayout.setAlpha(0.5f + 0.5f * (1.0f - ratio));
        }
    }


    public class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MyFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 3;
        }

    }

    private void setUpIndicatorWidth() {
        Class<?> tabLayoutClass = mTabLayout.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayoutClass.getDeclaredField("mTabStrip");
            tabStrip.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        LinearLayout layout = null;
        try {
            if (tabStrip != null) {
                layout = (LinearLayout) tabStrip.get(mTabLayout);
            }
            for (int i = 0; i < layout.getChildCount(); i++) {
                View child = layout.getChildAt(i);
                child.setPadding(0, 0, 0, 0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams
                        .MATCH_PARENT, 1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.setMarginStart(dip2px(this, 36f));
                    params.setMarginEnd(dip2px(this, 36f));
                }
                child.setLayoutParams(params);
                child.invalidate();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
