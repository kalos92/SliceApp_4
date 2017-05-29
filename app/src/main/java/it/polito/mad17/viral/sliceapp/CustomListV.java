package it.polito.mad17.viral.sliceapp;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Kalos on 29/05/2017.
 */

public class CustomListV extends ListView {

    private android.view.ViewGroup.LayoutParams params;
    private int oldCount = 0;

    public CustomListV(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }




    public CustomListV  (Context context) {
        super(context);
    }

    public CustomListV  (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


}