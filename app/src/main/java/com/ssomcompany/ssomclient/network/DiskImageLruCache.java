package com.ssomcompany.ssomclient.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;
import com.ssomcompany.ssomclient.BuildConfig;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class DiskImageLruCache {
    private static final String TAG = "DiskLruImageCache";
    private final Object mDiskCacheLock = new Object();

    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final String DISK_CACHE_SUBDIR = "thumbnails";
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 100; // 10MB

    private DiskLruCache mDiskCache;
    private CompressFormat mCompressFormat = CompressFormat.JPEG;
    private boolean mDiskCacheStarting = true;
    private int mCompressQuality = 100;
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;

    DiskImageLruCache(Context context) {
        final File diskCacheDir = getDiskCacheDir(context, DISK_CACHE_SUBDIR );
        new InitDiskCacheTask().execute(diskCacheDir);
    }

    private class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            synchronized (mDiskCacheLock) {
                try {
                    File cacheDir = params[0];
                    mDiskCache = DiskLruCache.open( cacheDir, APP_VERSION, VALUE_COUNT, DISK_CACHE_SIZE );
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mDiskCacheStarting = false; // Finished initialization
                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }
    }

    private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor )
            throws IOException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream( editor.newOutputStream( 0 ), IO_BUFFER_SIZE );
            return bitmap.compress( mCompressFormat, mCompressQuality, out );
        } finally {
            if ( out != null ) {
                out.close();
            }
        }
    }

    private File getDiskCacheDir(Context context, String uniqueName) {

        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !NetworkUtil.isExternalStorageRemovable() ?
                        NetworkUtil.getExternalCacheDir(context).getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    public void put( String key, Bitmap data ) {

        synchronized (mDiskCacheLock) {
            if (mDiskCache != null && !containsKey(key)) {
                DiskLruCache.Editor editor = null;
                try {
                    editor = mDiskCache.edit( hashKeyForDisk(key) );
                    if ( editor == null ) {
                        return;
                    }

                    if( writeBitmapToFile( data, editor ) ) {
                        mDiskCache.flush();
                        editor.commit();
                        if ( BuildConfig.DEBUG ) {
                            Log.d( "cache_test_DISK_", "image put on disk cache " + key );
                        }
                    } else {
                        editor.abort();
                        if ( BuildConfig.DEBUG ) {
                            Log.d( "cache_test_DISK_", "ERROR on: image put on disk cache " + key );
                        }
                    }
                } catch (IOException e) {
                    if ( BuildConfig.DEBUG ) {
                        Log.d( "cache_test_DISK_", "ERROR on: image put on disk cache " + key );
                    }
                    try {
                        if ( editor != null ) {
                            editor.abort();
                        }
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    Bitmap getBitmap( String key ) {
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;

        synchronized (mDiskCacheLock) {
            // Wait while disk cache is started from background thread
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (mDiskCache != null) {
                try {
                    snapshot = mDiskCache.get( hashKeyForDisk(key) );
                    if ( snapshot == null ) {
                        return null;
                    }
                    final InputStream in = snapshot.getInputStream( 0 );
                    if ( in != null ) {
                        final BufferedInputStream buffIn =
                                new BufferedInputStream( in, IO_BUFFER_SIZE );
                        bitmap = BitmapFactory.decodeStream( buffIn );
                    }
                } catch ( IOException e ) {
                    e.printStackTrace();
                } finally {
                    if ( snapshot != null ) {
                        snapshot.close();
                    }
                }

                if ( BuildConfig.DEBUG ) {
                    Log.d( "cache_test_DISK_", bitmap == null ? "" : "image read from disk " + key);
                }
            }
        }

        return bitmap;

    }

    public boolean containsKey( String key ) {

        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskCache.get( hashKeyForDisk(key) );
            contained = snapshot != null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( snapshot != null ) {
                snapshot.close();
            }
        }

        return contained;

    }

    public void clearCache() {
        if ( BuildConfig.DEBUG ) {
            Log.d( "cache_test_DISK_", "disk cache CLEARED");
        }
        try {
            mDiskCache.delete();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public File getCacheFolder() {
        return mDiskCache.getDirectory();
    }

    private static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
