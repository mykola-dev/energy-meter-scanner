package ds.meterscanner.ui.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import ds.meterscanner.R;


public class RefreshLayout extends SwipeRefreshLayout {

    public RefreshLayout(Context context) {
        super(context);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        setColorSchemeResources(R.color.colorAccent);
        setEnabled(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }
}
