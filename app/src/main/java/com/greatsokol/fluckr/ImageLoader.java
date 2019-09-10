package com.greatsokol.fluckr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;


class ImageLoader {
    private static final String OLD = "old";
    private static final int THUMB_SIZE = 350;
    private static final long CACHE_TIME = 2 * 24 *  60 * 60; // 2 days * 24 hours * 60 minutes * 60 sec


    ImageLoader(){
    }


    static Bitmap loadPicture(final String url, String cacheFileName) throws Exception {
        Bitmap bitmap = __loadPictureLocal(true, cacheFileName);
        if (bitmap == null)
           bitmap = loadImageFromUrlAndCacheIt(url, cacheFileName);
        return bitmap;
    }

    private static Bitmap __loadPictureLocal(boolean checkIsFileOverdue, String cacheFileName) {
        return loadPictureFromCache(cacheFileName, checkIsFileOverdue);
    }

    static Bitmap loadPictureFromCache(final String path, boolean checkIsFileOverdue) {
        if (!checkIsFileOverdue || __isFileNotOverdue(path)) {
            Bitmap bitmap = loadPictureFromFile(path);
            if (!checkIsFileOverdue && bitmap == null) {
                bitmap = loadPictureFromFile(path + OLD);
            }
            return bitmap;
        } else {
            //Log.i(TAG, "OVERDUTED FILE " + path);
            __makeFileOverdue(path);
        }
        return null;
    }

    private static Bitmap loadPictureFromFile(String path) {
        if (path != null && !path.trim().isEmpty()) {
            //Log.i(TAG, "LOADING FILE BEGIN = " + path);
            File file = new File(path);
            if (file.isFile() && file.exists()) {
                return BitmapFactory.decodeFile(path);
                //Log.i(TAG, "LOADING FILE FINISHED = " + path);
            }
        }
        return null;
    }


    private static Bitmap loadImageFromUrlAndCacheIt(String urlPath, String cacheFileName) throws Exception {
        try {
            //Log.i(TAG, "LOADING URL BEGIN = " + urlPath);
            File cacheFile = __prepareCacheFile(cacheFileName);
            if (cacheFile != null) {
                OutputStream os = new FileOutputStream(cacheFile);
                InputStream is = (new URL(urlPath)).openStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
                    CopyStream(is, bos);
                    Bitmap bitmap = ThumbnailUtils.extractThumbnail(
                            BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.size()),
                            THUMB_SIZE, THUMB_SIZE);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
                    __deleteCacheFile(cacheFileName + OLD);
                    //Log.i(TAG, "LOADING URL FINISHED = " + urlPath);
                    return bitmap;
                } finally {
                    try {
                        bos.close();
                        os.close();
                        is.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            __deleteCacheFile(cacheFileName);
            throw new Exception(e);
            //Log.i(TAG, "LOADING URL ERROR (" + e.getClass().getSimpleName() + ") = " + urlPath);
        }
        return null;
    }


    private static boolean __deleteCacheFile(String cacheFileName) {
        File cacheFile = new File(cacheFileName);
        return !cacheFile.exists() || cacheFile.delete();
    }


     private static File __prepareCacheFile(String cacheFileName) {
        if (!__deleteCacheFile(cacheFileName)) return null;
        return new File(cacheFileName);
    }

    private static boolean __isFileNotOverdue(String path) {
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

    private static void __makeFileOverdue(String path) {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                boolean proceed = true;
                File file_old = new File(path + OLD);
                if (file_old.exists())
                    proceed = file_old.delete();
                if (proceed)
                    file.renameTo(new File(path + OLD));
            }
        }
    }

    private static void CopyStream(InputStream is, OutputStream os) throws IOException {
        final int buffer_size=512;
        byte[] bytes=new byte[buffer_size];
        for(;;)
        {
            int count=is.read(bytes, 0, buffer_size);
            if(count==-1)
                break;
            os.write(bytes, 0, count);
        }
    }


}
