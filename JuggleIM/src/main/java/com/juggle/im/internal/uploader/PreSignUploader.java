package com.juggle.im.internal.uploader;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.juggle.im.internal.model.upload.UploadPreSignCred;
import com.juggle.im.internal.util.JLogger;
import com.juggle.im.internal.util.JThreadPoolExecutor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * @author Ye_Guli
 * @create 2024-05-29 9:12
 */
public class PreSignUploader extends BaseUploader {
    private static final int BUFFER_SIZE = 4096; //缓冲区大小
    private static final int CONNECT_TIMEOUT = 20 * 1000; //连接超时时间
    private static final int READ_TIMEOUT = 20 * 1000; //读取超时时间
    private static final int WRITE_TIMEOUT = 20 * 1000; //读取超时时间

    private final UploadPreSignCred mPreSignCred;
    private volatile boolean mIsCancelled = false;
    private Call currentCall;

    public PreSignUploader(String localPath, UploaderCallback uploaderCallback, UploadPreSignCred preSignCred) {
        super(localPath, uploaderCallback);
        this.mPreSignCred = preSignCred;
    }

    @Override
    public void start() {
        //判空文件地址
        if (TextUtils.isEmpty(mLocalPath)) {
            JLogger.e("J-Uploader", "PreSignUploader error, mLocalPath is empty");
            notifyFail();
            return;
        }
        //判空mQiNiuCred
        if (mPreSignCred == null || TextUtils.isEmpty(mPreSignCred.getUrl())) {
            JLogger.e("J-Uploader", "PreSignUploader error, mPreSignCred is null or empty");

            notifyFail();
            return;
        }
        //获取文件名
        String fileName = FileUtil.getFileName(mLocalPath);
        //判空文件名
        if (TextUtils.isEmpty(fileName)) {
            JLogger.e("J-Uploader", "PreSignUploader error, fileName is empty");
            notifyFail();
            return;
        }
        //开始上传
        JThreadPoolExecutor.runInBackground(() -> {
            //构造OkHttpClient
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                    .build();
            //构造RequestBody
            File file = new File(mLocalPath);
            RequestBody requestBody = new RequestBody() {
                @Nullable
                @Override
                public MediaType contentType() {
                    //不要返回ContentType，不然阿里云上传会403
                    return null;
                }

                @Override
                public long contentLength() throws IOException {
                    return file.length();
                }

                @Override
                public void writeTo(@NonNull BufferedSink sink) throws IOException {
                    long fileLength = file.length();
                    long uploaded = 0;
                    try (Source source = Okio.source(file)) {
                        long read;
                        while ((read = source.read(sink.getBuffer(), BUFFER_SIZE)) != -1) {
                            if (mIsCancelled) {
                                return;
                            }
                            uploaded += read;
                            sink.flush();
                            //计算上传进度
                            if (fileLength == -1) {
                                notifyProgress(0);
                            } else {
                                double progress = (double) uploaded / fileLength * 100;
                                notifyProgress((int) progress);
                            }
                        }
                    }
                }
            };
            //构造request
            Request request = new Request.Builder()
                    .url(mPreSignCred.getUrl())
                    .put(requestBody)
                    .build();
            //发起网络请求
            try {
                currentCall = client.newCall(request);
                Response response = currentCall.execute();
                if (response.isSuccessful()) {
                    //获取预签名上传地址去除query部分
                    String modifiedUrl = removeQueryFromUrl(mPreSignCred.getUrl());
                    //回调上传成功
                    notifySuccess(modifiedUrl);
                } else {
                    JLogger.e("J-Uploader, PreSignUploader error, responseCode is " + response.code() + ", responseMessage is " + response.message());
                    //回调上传失败
                    notifyFail();
                }
            } catch (Exception e) {
                JLogger.e("J-Uploader, PreSignUploader error, exception is " + e.getMessage());
                e.printStackTrace();
                notifyFail();
            }
        });
    }

    @Override
    public void cancel() {
        if (currentCall != null) {
            currentCall.cancel();
        }
        mIsCancelled = true;
        JLogger.i("J-Uploader", "PreSignUploader canceled");
        notifyCancel();
    }

    //移除URL的query部分
    private String removeQueryFromUrl(String url) {
        int index = url.indexOf("?");
        if (index != -1) {
            return url.substring(0, index);
        }
        return url;
    }
}