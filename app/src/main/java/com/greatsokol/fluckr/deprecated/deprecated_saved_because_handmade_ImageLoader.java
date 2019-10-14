package com.greatsokol.fluckr.deprecated;

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


public class deprecated_saved_because_handmade_ImageLoader {
    private static final String TAG = "deprecated_saved_because_handmade_ImageLoader";

    public static final int NORESIZE = -1;
    public static final int THUMB_SIZE = 320;


    deprecated_saved_because_handmade_ImageLoader(){
    }


    public static Bitmap loadPicture(final String url, final String cacheDir, final int resize) throws Exception {
        String cacheFileName
                = deprecated_saved_because_handmade_CacheFile.convertUrlToCacheFileName(url, cacheDir,
                                resize!=NORESIZE, String.valueOf(resize));
        Bitmap bitmap = loadPictureFromCache(cacheFileName, true);
        if (bitmap == null)
           bitmap = loadImageFromUrlAndCacheIt(url, cacheFileName, resize);
        return bitmap;
    }


    public static Bitmap loadPictureFromCache(final String path, boolean checkIsFileOverdue) {
        if (!checkIsFileOverdue || deprecated_saved_because_handmade_CacheFile.isFileNotOverdue(path)) {
            Bitmap bitmap = loadPictureFromFile(path);
            if (!checkIsFileOverdue && bitmap == null)
                bitmap = loadPictureFromFile(path + deprecated_saved_because_handmade_CacheFile.OLD);
            return bitmap;
        } else {
            //Log.i(TAG, "OVERDUTED FILE " + path);
            deprecated_saved_because_handmade_CacheFile.makeFileOverdue(path);
        }
        return null;
    }

    private static Bitmap loadPictureFromFile(final String path) {
        return (Bitmap) deprecated_saved_because_handmade_CacheFile.loadFileFromCache(path, new deprecated_saved_because_handmade_CacheFile.Callable<String, Object>() {
                                                        @Override
                                                        public Object call(String input) {
                                                            return BitmapFactory.decodeFile(path);
                                                        }
                                                    });
    }


    private static Bitmap loadImageFromUrlAndCacheIt(String urlPath, String cacheFileName, int resize) throws Exception {
        try {
            //Log.i(TAG, "LOADING URL BEGIN = " + urlPath);
            File cacheFile = deprecated_saved_because_handmade_CacheFile.prepareCacheFile(cacheFileName);
            if (cacheFile != null) {
                OutputStream os = new FileOutputStream(cacheFile);
                InputStream is = (new URL(urlPath)).openStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
                    CopyStream(is, bos);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.size());
                    if (resize!=NORESIZE)
                        bitmap = ThumbnailUtils.extractThumbnail(bitmap, resize, resize);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
                    deprecated_saved_because_handmade_CacheFile.deleteCacheFile(cacheFileName + deprecated_saved_because_handmade_CacheFile.OLD);
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
            e.printStackTrace();
            deprecated_saved_because_handmade_CacheFile.deleteCacheFile(cacheFileName);
            throw new Exception(e);
            //Log.i(TAG, "LOADING URL ERROR (" + e.getClass().getSimpleName() + ") = " + urlPath);
        }
        return null;
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
