/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2015 cpoopc
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package com.cpoopc.scrollablelayoutlib;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ScrollView;

/**
 * Created by cpoopc(303727604@qq.com) on 2015-02-10.
 */
public class ScrollableHelper {

    private ScrollableContainer mCurrentScrollableCainer;

    /**
     * a viewgroup whitch contains ScrollView/ListView/RecycelerView..
     */
    public interface ScrollableContainer{
        /**
         * @return ScrollView/ListView/RecycelerView..'s instance
         */
        View getScrollableView();
        int getItemDecoration();
    }

    public void setCurrentScrollableContainer(ScrollableContainer scrollableContainer) {
        this.mCurrentScrollableCainer = scrollableContainer;
    }

    private View getScrollableView() {
        if (mCurrentScrollableCainer == null) {
            return null;
        }
        return mCurrentScrollableCainer.getScrollableView();
    }

    /**
     *
     * 判断是否滑动到顶部方法,ScrollAbleLayout根据此方法来做一些逻辑判断
     * 目前只实现了AdapterView,ScrollView,RecyclerView
     * 需要支持其他view可以自行补充实现
     * @return
     */
    public boolean isTop() {
        View scrollableView = getScrollableView();
        if (scrollableView == null) {
//            throw new NullPointerException("You should call ScrollableHelper.setCurrentScrollableContainer() to set ScrollableContainer.");
            return true;
        }
        if (scrollableView instanceof AdapterView) {
            return isAdapterViewTop(mCurrentScrollableCainer);
        }
        if (scrollableView instanceof ScrollView) {
            return isScrollViewTop(mCurrentScrollableCainer);
        }
        if (scrollableView instanceof RecyclerView) {
            return isRecyclerViewTop(mCurrentScrollableCainer);
        }
        if (scrollableView instanceof WebView) {
            return isWebViewTop(mCurrentScrollableCainer);
        }
        throw new IllegalStateException("scrollableView must be a instance of AdapterView|ScrollView|RecyclerView");
    }

    private static boolean isRecyclerViewTop(ScrollableContainer scrollableContainer) {
        RecyclerView recyclerView = (RecyclerView) scrollableContainer.getScrollableView();

        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                View childAt = recyclerView.getChildAt(0);
                if (childAt == null || (firstVisibleItemPosition == 0 && childAt.getTop() ==
                    scrollableContainer.getItemDecoration())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isAdapterViewTop(ScrollableContainer scrollableContainer) {
        AdapterView adapterView = (AdapterView) scrollableContainer.getScrollableView();

        if(adapterView != null){
            int firstVisiblePosition = adapterView.getFirstVisiblePosition();
            View childAt = adapterView.getChildAt(0);
            if(childAt == null || (firstVisiblePosition == 0 && childAt.getTop() == 0)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isScrollViewTop(ScrollableContainer scrollableContainer) {
        ScrollView scrollView = (ScrollView) scrollableContainer.getScrollableView();
        if(scrollView != null) {
            int scrollViewY = scrollView.getScrollY();
            return scrollViewY <= 0;
        }
        return false;
    }

    private static boolean isWebViewTop(ScrollableContainer scrollableContainer) {
        WebView webView = (WebView) scrollableContainer.getScrollableView();
        if(webView != null) {
            int scrollViewY = webView.getScrollY();
            return scrollViewY <= 0;
        }
        return false;
    }

    @SuppressLint("NewApi")
    public void smoothScrollBy(int velocityY, int distance, int duration) {
        View scrollableView = getScrollableView();
        if (scrollableView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) scrollableView;
            if (Build.VERSION.SDK_INT >= 21) {
                absListView.fling(velocityY);
            } else {
                absListView.smoothScrollBy(distance, duration);
            }
        } else if (scrollableView instanceof ScrollView) {
            ((ScrollView) scrollableView).fling(velocityY);
        } else if (scrollableView instanceof RecyclerView) {
            ((RecyclerView) scrollableView).fling(0, velocityY);
        } else if (scrollableView instanceof WebView) {
            ((WebView) scrollableView).flingScroll(0, velocityY);
        }
    }

}
