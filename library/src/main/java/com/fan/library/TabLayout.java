package com.fan.library;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class TabLayout extends HorizontalScrollView {
    private int mTextColor;
    private int mTextSelectColor;

    private int mTabIndicatorColor;
    private int mTabMargin;
    private RootContainer mRootContainer;
    private int mIndicatorHeight;

    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabLayout);
        mTextColor = a.getColor(R.styleable.TabLayout_tabTextColor, Color.GRAY);
        mTextSelectColor = a.getColor(R.styleable.TabLayout_tabSelectTextColor, Color.GRAY);
        mTabIndicatorColor = a.getColor(R.styleable.TabLayout_tabIndicatorColor, mTextSelectColor);
        mTabMargin = a.getDimensionPixelSize(R.styleable.TabLayout_tabMargin, dp2px(20));
        mIndicatorHeight = a.getDimensionPixelSize(R.styleable.TabLayout_tabIndicatorHeight, dp2px(2));
        a.recycle();
        mRootContainer = new RootContainer(context);
        addView(mRootContainer);
        setHorizontalScrollBarEnabled(false);
    }

    private int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getContext().getResources().getDisplayMetrics());
    }

    public void addTab(String s) {
        TextView title = new TextView(getContext());
        title.setTextColor(mTextColor);
        title.setText(s);
        mRootContainer.addTab(title);
    }

    private ViewPager mPager;

    public void setupWithViewPager(ViewPager pager) {
        if (pager == null || pager.getAdapter() == null) return;
        mPager = pager;
        PagerAdapter adapter = pager.getAdapter();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            addTab(adapter.getPageTitle(i).toString());
        }
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                View child = mRootContainer.getChildAt(position);
                int left = child.getLeft();
                int right = child.getRight();
                int targetLeft;
                int targetRight;
                View target = mRootContainer.getChildAt(position + 1);
                if (target != null) {
                    targetLeft = target.getLeft();
                    targetRight = target.getRight();
                    mRootContainer.indicatorLeft = (int) (left + (targetLeft - left) * positionOffset);
                    mRootContainer.indicatorRight = mRootContainer.indicatorLeft + mRootContainer.getChildAt(mRootContainer.pos).getWidth();
                    mRootContainer.invalidate();
                }
            }

            @Override
            public void onPageSelected(int position) {
                setSelectPos(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    void setSelectPos(int pos) {
        mRootContainer.getChildAt(pos).performClick();
    }

    private class RootContainer extends LinearLayout implements OnClickListener {
        Paint mPaint;
        private int pos;
        private ValueAnimator mIndicatorAnim;
        int indicatorLeft = 0;
        int indicatorRight = 0;
        boolean shouldReset = true;
        private int mItemWidth;

        public RootContainer(Context context) {
            super(context);
            setOrientation(HORIZONTAL);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(mTabIndicatorColor);

            mIndicatorAnim = new ValueAnimator();
            mIndicatorAnim.setDuration(500);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);

            if (shouldReset) {
                for (int i = 0; i < getChildCount(); i++) {
                    TextView item = (TextView) getChildAt(i);
                    if (pos == i) {
                        indicatorLeft = item.getLeft();
                        indicatorRight = item.getRight();
                        View selectView = getChildAt(pos);
                        int target = selectView.getLeft() + (selectView.getWidth() / 2) - (TabLayout.this.getWidth() / 2);
                        TabLayout.this.scrollTo(target, 0);
                    }
                    item.setTextColor(pos == i ? mTextSelectColor : mTextColor);
                }
                shouldReset = false;
            }
            canvas.drawRect(indicatorLeft, getHeight() - mIndicatorHeight, indicatorRight, getHeight(), mPaint);
        }

        void animIndicator(final int targetLeft, final int targetRight) {
            mIndicatorAnim.setIntValues(indicatorLeft, targetLeft);
            mIndicatorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float frac = animation.getAnimatedFraction();
//                    indicatorLeft = (indicatorLeft + Math.round((targetLeft - indicatorLeft) * frac));
//                    indicatorRight = (indicatorRight + Math.round((targetRight - indicatorRight) * frac));
//                    Log.e("main",   "frac  "+frac+"  indicatorLeft:  " + indicatorLeft + "   indicatorRight" + indicatorRight);
                    indicatorLeft = (int) animation.getAnimatedValue();
                    indicatorRight = indicatorLeft + getChildAt(pos).getWidth();
                    invalidate();
                    if (frac == 1) shouldReset = true;
                }
            });
            mIndicatorAnim.start();
        }


        void addTab(TextView tab) {
//            LayoutParams params = new LayoutParams(-2, -2);
//            params.leftMargin = mTabMargin / 2;
//            params.rightMargin = mTabMargin / 2;
//            params.gravity = Gravity.CENTER_VERTICAL;
            addView(tab);
            tab.setOnClickListener(this);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            int count = getChildCount();
            int parentWidth = TabLayout.this.getWidth();
            if (getWidth() < parentWidth) {
                for (int i = 0; i < count; i++) {
                    TextView tv = (TextView) getChildAt(i);
                    LinearLayout.LayoutParams params = (LayoutParams) tv.getLayoutParams();
                    params.gravity = Gravity.CENTER_VERTICAL;
                    int itemWidth = parentWidth / count;
                    params.leftMargin = (itemWidth - tv.getWidth()) / 2;
                    params.rightMargin = (itemWidth - tv.getWidth()) / 2;
                    tv.setLayoutParams(params);
                }
            } else {
                for (int i = 0; i < count; i++) {
                    TextView tv = (TextView) getChildAt(i);
                    LinearLayout.LayoutParams params = (LayoutParams) tv.getLayoutParams();
                    params.gravity = Gravity.CENTER_VERTICAL;
                    params.leftMargin =mTabMargin/2;
                    params.rightMargin = mTabMargin/2;
                    tv.setLayoutParams(params);
                }
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int mode = MeasureSpec.getMode(heightMeasureSpec);
            if (mode == MeasureSpec.AT_MOST) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(dp2px(48), MeasureSpec.EXACTLY);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        public void onClick(View v) {
            for (int i = 0; i < getChildCount(); i++) {
                TextView item = (TextView) getChildAt(i);
                if (v == item) {
                    if (pos != i) {
                        pos = i;
                        animIndicator(item.getLeft(), item.getRight());
                    }
                    if (mPager != null) {
                        if (mPager.getCurrentItem() != i)
                            mPager.setCurrentItem(i);
                    }
                }
            }
        }
    }
}
