package com.cathedralsw.schoolteacher.utilities;

import com.cathedralsw.schoolteacher.classes.Class;
import com.cathedralsw.schoolteacher.classes.Schedule;

import java.util.Comparator;

/**
 * Created by alexis on 6/11/17.
 */

public class ScheduleComparator implements Comparator<Schedule> {

    @Override
    public int compare(Schedule o1, Schedule o2) {
        return o1.getTime().compareTo(o2.getTime());
    }
}
