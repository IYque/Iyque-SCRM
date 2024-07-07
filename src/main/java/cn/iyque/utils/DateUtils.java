package cn.iyque.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateUtils {
    public static List<String> getTimePeriods(Date startDate, Date endDate) {
        if (startDate == null && endDate == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date today = sdf.parse(sdf.format(new Date()));
                Date sevenDaysAgo = new Date(today.getTime() - 6 * 24 * 60 * 60 * 1000); // Adjusted to include today
                startDate = sevenDaysAgo;
                endDate = today;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        List<String> timePeriods = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long oneDay = 24 * 60 * 60 * 1000; // milliseconds in one day

        for (long start = startDate.getTime(); start <= endDate.getTime(); start += oneDay) {
            long end = Math.min(start + oneDay, endDate.getTime());
            String period = sdf.format(new Date(start));
            timePeriods.add(period);
        }

        return timePeriods;
    }

}
