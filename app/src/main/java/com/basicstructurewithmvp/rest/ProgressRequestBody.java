package com.basicstructurewithmvp.rest;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by Darshna Desai
 */
public class ProgressRequestBody extends RequestBody {
    private File mFile;

    private UploadCallbacks mListener;
    private String id;
    private MediaType mediaType;

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage, String id);
    }

    public ProgressRequestBody(final File file, String id, MediaType mediaType, final UploadCallbacks listener) {
        mFile = file;
        mListener = listener;
        this.id = id;
        this.mediaType = mediaType;
    }

    @Override
    public MediaType contentType() {
        return mediaType;
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {

        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;

        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new ProgressUpdater(uploaded, fileLength, id));
            int num = 0;
            while ((read = in.read(buffer)) != -1) {
                int progress = (int) (100 * uploaded / fileLength);
                if (progress > num + 1) {
                    handler.post(new ProgressUpdater(uploaded, fileLength, id));
                    num = progress;
                }
                uploaded += read;
                sink.write(buffer, 0, read);
            }
        } finally {
            in.close();
        }
    }


    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;
        private String id;

        public ProgressUpdater(long uploaded, long total, String id) {
            mUploaded = uploaded;
            mTotal = total;
            this.id = id;
        }

        @Override
        public void run() {
            mListener.onProgressUpdate((int) (100 * mUploaded / mTotal), id);
        }
    }
}