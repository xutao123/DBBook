package com.xt.dbbook.tools;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by xt on 2018/01/29.
 */

public class HorizontalAnnoItemDecoration extends RecyclerView.ItemDecoration {

    private int left;

    public HorizontalAnnoItemDecoration(int left) {
        this.left = left;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildPosition(view) != 0)
            outRect.left = left;
    }
}
