package com.internal.voip.pullrefresh.widget;

/**
 * Created by Android Studio.
 * User: lixiaofan0122@gmail.com
 * Date: 5/21/19
 * Time: 10:02 AM
 */

import android.view.View;

public interface PtrHandler {

    /**
     * Check can do refresh or not. For example the content is empty or the first child is in view.
     * <p/>
     */
    public boolean checkCanDoRefresh(final PtrFrameLayout frame, final View content, final View header);

    /**
     * When refresh begin
     *
     * @param frame
     */
    public void onRefreshBegin(final PtrFrameLayout frame);
}