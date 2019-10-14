package com.greatsokol.fluckr.deprecated;

import android.util.Log;

import java.io.File;
import java.util.Date;

public abstract class deprecated_saved_because_handmade_CacheFile {
    static final String OLD = "old";
    private static final long CACHE_TIME = 24 *  60 * 60; // 1 days * 24 hours * 60 minutes * 60 sec
    private static final String TAG = "deprecated_saved_because_handmade_CacheFile";

    public interface Callable<I, O> {
        O call(I input);
    }

    public static void cleanCache(final String cacheDir){
        File directory = new File(cacheDir);
        File[] files = directory.listFiles();
        assert files != null;
        for(File file : files){
            final String filePath = file.getPath();
            if (!isFileNotOverdue(filePath))
                if (deleteCacheFile(filePath))
                    Log.d(TAG, String.format("Cleaned cache file %s",filePath));
        }
    }

    static String convertUrlToCacheFileName(final String url, final String cacheDir,
                                                   boolean addpostfix, String postfix){
        String fileName = cacheDir + "/" +
                url.replace(':', '_').
                        replace('?','_').
                        replace('&','_').
                        replace('/','_').
                        replace('.','_');
        if (addpostfix)
            fileName = fileName+"_"+postfix;
        return fileName;
    }

    static boolean isFileNotOverdue(String path) {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                final long currentTime = new Date().getTime() / 1000;
                final long lastModifed = file.lastModified() / 1000;
                return lastModifed + CACHE_TIME > currentTime;
            }
        }
        return false;
    }

    static void makeFileOverdue(String path) {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                boolean proceed = true;
                File file_old = new File(path + OLD);
                if (file_old.exists())
                    proceed = file_old.delete();
                if (proceed)
                    if (!file.renameTo(new File(path + OLD)))
                        Log.i(TAG, String.format("Cant rename %s", path));
            }
        }
    }

    static boolean deleteCacheFile(String cacheFileName) {
        File cacheFile = new File(cacheFileName);
        return !cacheFile.exists() || cacheFile.delete();
    }


    static File prepareCacheFile(String cacheFileName) {
        if (!deleteCacheFile(cacheFileName)) return null;
        return new File(cacheFileName);
    }

    static Object loadFileFromCache(String path, deprecated_saved_because_handmade_CacheFile.Callable<String, Object> func) {
        if (path != null && !path.trim().isEmpty()) {
            //Log.i(TAG, "LOADING FILE BEGIN = " + path);
            File file = new File(path);
            if (file.isFile() && file.exists()) {
                return func.call(path);// BitmapFactory.decodeFile(path);
                //Log.i(TAG, "LOADING FILE FINISHED = " + path);
            }
        }
        return null;
    }
}
