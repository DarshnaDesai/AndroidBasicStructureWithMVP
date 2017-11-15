package com.basicstructurewithmvp.rest;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by Darshna Desai
 */
public class ProgressResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private ProgressRequestBody.UploadCallbacks listener;
    private BufferedSource bufferedSource;
    private String id;

    public ProgressResponseBody(ResponseBody responseBody, String id, final ProgressRequestBody.UploadCallbacks listener) {
        this.responseBody = responseBody;
        this.listener = listener;
        this.id = id;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);

                totalBytesRead += bytesRead != -1 ? bytesRead : 0;

                float percent = bytesRead == -1 ? 100f : (((float) totalBytesRead / (float) responseBody.contentLength()) * 100);

        /*AppUtils.logd("Progress Download", "Download:" + percent);*/

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new ProgressUpdater((int) percent, totalBytesRead, id));

                return bytesRead;
            }
        };
    }

    private class ProgressUpdater implements Runnable {
        private int mUploaded;
        private long mTotal;
        private String id;

        public ProgressUpdater(int uploaded, long total, String id) {
            mUploaded = uploaded;
            mTotal = total;
            this.id = id;
        }

        @Override
        public void run() {
            listener.onProgressUpdate(mUploaded, id);
        }
    }
}