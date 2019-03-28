package com.brunel.group30.fitnessapp.Custom;

import android.os.Handler;

public class CustomAutoSwipeTask {
    private Handler mHandler;
    private static final int DELAY = 3750;

    private CustomViewPager mCustomViewPager;
    private int numOfPages;

    public CustomAutoSwipeTask(CustomViewPager mCustomViewPager, int numOfPages) {
        this.mCustomViewPager = mCustomViewPager;
        this.numOfPages = numOfPages;

        this.mCustomViewPager.setCurrentItem(0);
        mHandler = new Handler();
        mRunnable.run();
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCustomViewPager.getCurrentItem() == numOfPages - 1) {
                mCustomViewPager.setCurrentItem(0);
            } else {
                mCustomViewPager.setCurrentItem(mCustomViewPager.getCurrentItem() + 1);
            }

            mHandler.postDelayed(mRunnable, DELAY);
        }
    };
}