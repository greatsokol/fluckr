package com.greatsokol.fluckr;

import android.content.res.Resources;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

abstract class ConstsAndUtils {
    final static String TAG_ARGS = "ARGS";
    final static String TAG_TR_POSITION = "TR_POSITION";
    final static String TAG_TR_NAME = "TR_NAME";
    final static String TAG_TITLE = "TITLE";
    final static String TAG_DETAILS = "DETAILS";
    final static String TAG_THUMBURL = "THUMBURL";
    final static String TAG_FULLSIZEURL = "FULLSIZEURL";
    final static String TAG_VIEWASGRID = "VIEWASGRID";
    final static String TAG_READY = "READY";
    final static String TAG_SEARCH_FOR = "SEARCH_FOR";
    final static String TAG_DATE_TO_VIEW = "DATE_VIEW";
    final static String TAG_PAGE_TO_VIEW = "PAGE_VIEW";
    final static String TAG_NUMBER_ON_PAGE = "NUMBER_ON_PAGE";


    static boolean isLandscape(Resources res){
        final int orientation = res.getConfiguration().orientation;
        return orientation == ORIENTATION_LANDSCAPE;
    }

    static int pxFromDp(Resources res, float dp) {
        return (int)(dp * res.getDisplayMetrics().density);
    }

    static String DateToStr_yyyy_mm_dd(Date date){
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
    }

    static String DateToStr_dd_mmmm_yyyy(Date date){
        return new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date);
    }

    private static Date __dec_inc_date(Date date, int amount){
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, amount);
        return cal.getTime();
    }

    static Date DecDate(Date date){
        return __dec_inc_date(date, -1);
    }

    static Date IncDate(Date date){
        return __dec_inc_date(date, 1);
    }

    static boolean IsEqualDay(Date date1, Date date2){
        /*final Calendar cal1 = Calendar.getInstance();
        final Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return  cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH); */
        return date1.compareTo(date2) == 0;
    }

    static boolean IsLowerDate(Date date1, Date date2, int HowMuchDays){
        final Calendar cal1 = Calendar.getInstance();
        final Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return  cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) < cal2.get(Calendar.DAY_OF_MONTH) - HowMuchDays;
    }

    static boolean IsToday(Date date){
        return IsEqualDay(date, new Date());
    }
}
