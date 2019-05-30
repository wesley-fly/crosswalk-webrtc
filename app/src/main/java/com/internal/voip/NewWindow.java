package com.internal.voip;

import android.webkit.ValueCallback;

import org.chromium.base.Log;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

/**
 * Created by Android Studio.
 * User: lixiaofan0122@gmail.com
 * Date: 5/23/19
 * Time: 12:21 PM
 */
public class NewWindow extends XWalkUIClient {
    public NewWindow(XWalkView view) {
        super(view);
    }

    @Override
    public boolean onCreateWindowRequested(XWalkView view, InitiateBy initiator, ValueCallback<XWalkView> callback)
    {
        Log.e("NewWindow URL :: ", view.getUrl() );
        return super.onCreateWindowRequested(view, initiator, callback);
    }
}
