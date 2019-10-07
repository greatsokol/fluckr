package com.greatsokol.fluckr.etc;

import android.content.res.Resources;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public abstract class ConstsAndUtils {
    public final static String TAG_ARGS = "ARGS";
    public final static String TAG_TR_POSITION = "TR_POSITION";
    public final static String TAG_TR_NAME = "TR_NAME";
    public final static String TAG_TITLE = "TITLE";
    public final static String TAG_DETAILS = "DETAILS";
    public final static String TAG_THUMBURL = "THUMBURL";
    public final static String TAG_FULLSIZEURL = "FULLSIZEURL";
    public final static String TAG_VIEWASGRID = "VIEWASGRID";
    public final static String TAG_READY = "READY";
    public final static String TAG_SEARCH_FOR = "SEARCH_FOR";
    public final static String TAG_DATE_TO_VIEW = "DATE_VIEW";
    public final static String TAG_PAGE_TO_VIEW = "PAGE_VIEW";
    public final static String TAG_NUMBER_ON_PAGE = "NUMBER_ON_PAGE";


    public static boolean isLandscape(Resources res){
        final int orientation = res.getConfiguration().orientation;
        return orientation == ORIENTATION_LANDSCAPE;
    }

    static int pxFromDp(Resources res, float dp) {
        return (int)(dp * res.getDisplayMetrics().density);
    }

    public static String DateToStr_yyyy_mm_dd(Date date){
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
    }

    public static String DateToStr_dd_mmmm_yyyy(Date date){
        return new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date);
    }

    private static Date __dec_inc_date(Date date, int amount){
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, amount);
        return cal.getTime();
    }

    public static Date DecDate(Date date){
        return __dec_inc_date(date, -1);
    }

    public static Date IncDate(Date date){
        return __dec_inc_date(date, 1);
    }

    static boolean IsEqualDay(Date date1, Date date2){
        final Calendar cal1 = Calendar.getInstance();
        final Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return  cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
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

    public static boolean IsToday(Date date){
        return IsEqualDay(date, CurrentGMTDate());
    }

    public static Date CurrentGMTDate(){
        //Date date = new Date();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        return cal.getTime();
    }
}
