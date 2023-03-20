package ru.serzhe.mychecks.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by sergio on 17.01.2018.
 */

public class DynamicHeightListView extends ListView {


    public DynamicHeightListView(Context context) {
        super(context);
    }

    public DynamicHeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicHeightListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    public DynamicHeightListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    @Override
    public void onViewAdded(View child) {
        justifyListViewHeightBasedOnChildren();
    }

    @Override
    public void onViewRemoved(View child) {
        justifyListViewHeightBasedOnChildren();
    }

    public void justifyListViewHeightBasedOnChildren() {
        ListAdapter adapter = getAdapter();
        if (adapter == null) {
            return;
        }
        ViewGroup vg = (ViewGroup)this;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = getLayoutParams();
        par.height = totalHeight + (8 * (adapter.getCount() - 1));
        setLayoutParams(par);
        requestLayout();
    }
}
