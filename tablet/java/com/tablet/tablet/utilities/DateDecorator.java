package com.cathedralsw.schoolteacher.utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.cathedralsw.schoolteacher.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by alexis on 2/10/17.
 */

public class DateDecorator implements DayViewDecorator {

    private int color = 0;
    private final HashSet<CalendarDay> dates;
    private ColorDrawable drawable;
    private Drawable drawable_circle;
    private Context context;
    private Drawable hi;
    private Boolean current = false;

    public DateDecorator(Context context, int color, Collection<CalendarDay> dates) {
        this.context = context;
        this.color = color;
        this.dates = new HashSet<>(dates);
        drawable = new ColorDrawable(color);
//        hi =  this.context.getResources().getDrawable(R.drawable.circlebackground);
    }

    public DateDecorator(Context context, Collection<CalendarDay> dates, Boolean current) {
        this.context = context;
        this.dates = new HashSet<>(dates);
        this.current = current;
        drawable_circle = ContextCompat.getDrawable(context, R.drawable.circlebackground);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        if (current){
//            view.setBackgroundDrawable(drawable);
//            view.setBackgroundDrawable(drawable_circle);
//            view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.white)));
            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.addSpan(new RelativeSizeSpan(1.1f));
        }else{
            view.addSpan(new DotSpan(3, color));
        }


    }

}

