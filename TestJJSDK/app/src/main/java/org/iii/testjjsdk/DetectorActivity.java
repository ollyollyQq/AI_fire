/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.iii.testjjsdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ImageReader.OnImageAvailableListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.params.HttpConnectionParams;
import org.iii.testjjsdk.customview.OverlayView;
import org.iii.testjjsdk.env.BorderedText;
import org.iii.testjjsdk.env.ImageUtils;
import org.iii.testjjsdk.env.Logger;
import org.iii.testjjsdk.tflite.Classifier;
import org.iii.testjjsdk.tracking.MultiBoxTracker;
import org.json.JSONException;
import org.json.JSONObject;
import org.iii.testjjsdk.customview.OverlayView;
import org.iii.testjjsdk.customview.OverlayView.DrawCallback;
import org.iii.testjjsdk.env.BorderedText;
import org.iii.testjjsdk.env.ImageUtils;
import org.iii.testjjsdk.env.Logger;
import org.iii.testjjsdk.tflite.Classifier;
import org.iii.testjjsdk.tflite.YoloV4Classifier;
import org.iii.testjjsdk.tracking.MultiBoxTracker;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {
    Button button;
    private static final Logger LOGGER = new Logger();

    private static final int TF_OD_API_INPUT_SIZE = 416;
    private static final boolean TF_OD_API_IS_QUANTIZED = false;
    private static final String TF_OD_API_MODEL_FILE = "yolov4-416-int8.tflite";

    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco.txt";
    public static final int CAMERA_PERMISSION = 100;
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    private static final boolean MAINTAIN_ASPECT = false;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10;
    OverlayView trackingOverlay;
    private Integer sensorOrientation;

    private Classifier detector;

    private long lastProcessingTimeMs;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;

    private boolean computingDetection = false;

    private long timestamp = 0;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    private MultiBoxTracker tracker;

    private BorderedText borderedText;
    NotificationManager notificationManager;
    NotificationChannel channel;
    Button noti_but;
    Context context = this;
    String result;
    String response_result = "";
    String temp;
    TextView textView;
    String upLoadServerUri = "http://59.126.170.89/DEX.php/";
    URL url = null;
    private int serverResponseCode = 0;
    String key = "file";
    String mPath = "";
    String id = "";
    String time = "";
    int lo=0;
    String address = "";
    String dateString;
    Geocoder gc;
    private TextView txtViewLatGPS;
    private TextView txtViewLongGPS;
    private TextView txtViewAltGPS;

    private TextView txtViewLatNetwork;
    private TextView txtViewLongNetwork;
    private TextView txtViewAltNetwork;

    private LocationManager mLocationManagerGPS;
    private LocationListener mLocationListenerGPS;

    private LocationManager mLocationManagerNetwork;
    private LocationListener mLocationListenerNetwork;

    private Button btnGPS;
    private Button btnNetwork;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    String latitude;
    String longitude;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        getPositionNetwork();
        gc = new Geocoder(this, Locale.TRADITIONAL_CHINESE);
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            // We don't have permission so prompt the user
            requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        notificationManager = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);

        if (checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION);

        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        tracker = new MultiBoxTracker(this);

        int cropSize = TF_OD_API_INPUT_SIZE;

        try {
            detector =
                    YoloV4Classifier.create(
                            getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_IS_QUANTIZED);
//            detector = TFLiteObjectDetectionAPIModel.create(
//                    getAssets(),
//                    TF_OD_API_MODEL_FILE,
//                    TF_OD_API_LABELS_FILE,
//                    TF_OD_API_INPUT_SIZE,
//                    TF_OD_API_IS_QUANTIZED);
            cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.e(e, "Exception initializing classifier!");
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
        trackingOverlay.addCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        tracker.draw(canvas);
                        if (isDebug()) {
                            tracker.drawDebug(canvas);
                        }
                    }
                });

        tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void processImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {

        }
        long startTime1=System.currentTimeMillis();
        ++timestamp;
        final long currTimestamp = timestamp;
        trackingOverlay.postInvalidate();

        // No mutex needed as this method is not reentrant.
        if (computingDetection) {
            readyForNextImage();
            return;
        }
        computingDetection = true;
        LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

        readyForNextImage();

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);


        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.i("Running detection on image " + currTimestamp);
                        final long startTime = SystemClock.uptimeMillis();
                        final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

                        Log.e("CHECK", "run: " + results.size());

                        cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                        final Canvas canvas = new Canvas(cropCopyBitmap);
                        final Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        paint.setStyle(Style.STROKE);
                        paint.setStrokeWidth(2.0f);

                        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                        switch (MODE) {
                            case TF_OD_API:
                                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                                break;
                        }

                        final List<Classifier.Recognition> mappedRecognitions =
                                new LinkedList<Classifier.Recognition>();

                        for (final Classifier.Recognition result : results) {
                            final RectF location = result.getLocation();
                            temp = result.getTitle();
                            if (location != null && result.getConfidence() >= minimumConfidence) {
                                canvas.drawRect(location, paint);

                                cropToFrameTransform.mapRect(location);

                                result.setLocation(location);
                                mappedRecognitions.add(result);
                            }
                        }

                        tracker.trackResults(mappedRecognitions, currTimestamp);
                        trackingOverlay.postInvalidate();

                        computingDetection = false;

                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showFrameInfo(previewWidth + "x" + previewHeight);
                                        showCropInfo(cropCopyBitmap.getWidth() + "x" + cropCopyBitmap.getHeight());
                                        showInference(lastProcessingTimeMs + "ms");
                                    }
                                });
                        if(temp!=null)
                        {
                            getPositionNetwork();

                            if(lo == 1)
                            {


                                Log.e("cccccc","asfjsaklfjsaljfl");
                                long endTime=System.currentTimeMillis(); //獲取結束時間

                                Date date = new Date();
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
                                dateString = dateFormat.format(date);
                                Log.e("1231",dateString);
                                Log.e("577",Long.toString(lastProcessingTimeMs)+"   "+Long.toString(endTime-startTime1));
                                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                    // We don't have permission so prompt the user
                                    requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                                Intent intent = getIntent();
                                id=intent.getStringExtra("member_id");
                                Thread thread = new Thread(uploadtexttoserver);
                                thread.start(); // 開始執行
                                Thread thread1 = new Thread(uploadtexttofirebase);
                                thread1.start();

                                time = "IMG-"+System.currentTimeMillis();
                                File imgFile = new  File(Environment.getExternalStorageDirectory()+"/DCIM/Camera/",time  +".png");
                                mPath = Environment.getExternalStorageDirectory()+"/DCIM/Camera/" + time +".png";
                                try {
                                    OutputStream os = new FileOutputStream(imgFile);
                                    croppedBitmap.compress(Bitmap.CompressFormat.PNG, 99, os);

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }

                                Thread thread2 = new Thread(uploadimgFile);
                                thread2.start(); // 開始執行
                                Log.e("123","0"+temp+"0");
                                temp = null;
                            }

                        }
                        button = findViewById(R.id.button);
                        textView = findViewById(R.id.textView);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                    getPositionNetwork();

                                    if(lo == 1)
                                    {


                                        Log.e("cccccc","asfjsaklfjsaljfl");
                                        long endTime=System.currentTimeMillis(); //獲取結束時間

                                        Date date = new Date();
                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                                        dateString = dateFormat.format(date);
                                        Log.e("1231",dateString);
                                        Log.e("577",Long.toString(lastProcessingTimeMs)+"   "+Long.toString(endTime-startTime1));
                                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                            // We don't have permission so prompt the user
                                            requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                                        Intent intent = getIntent();
                                        id=intent.getStringExtra("member_id");
                                        Thread thread = new Thread(uploadtexttoserver);
                                        thread.start(); // 開始執行
                                        Thread thread1 = new Thread(uploadtexttofirebase);
                                        thread1.start();

                                        time = "IMG-"+System.currentTimeMillis();
                                        File imgFile = new  File(Environment.getExternalStorageDirectory()+"/DCIM/Camera/",time  +".png");
                                        mPath = Environment.getExternalStorageDirectory()+"/DCIM/Camera/" + time +".png";
                                        try {
                                            OutputStream os = new FileOutputStream(imgFile);
                                            croppedBitmap.compress(Bitmap.CompressFormat.PNG, 99, os);

                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }

                                        Thread thread2 = new Thread(uploadimgFile);
                                        thread2.start(); // 開始執行
                                        Log.e("123","0"+temp+"0");
                                        temp = null;
                                    }



                            }
                        });
                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.tfe_od_camera_connection_fragment_tracking;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    // Which detection model to use: by default uses Tensorflow Object Detection API frozen
    // checkpoints.
    private enum DetectorMode {
        TF_OD_API;
    }

    @Override
    protected void setUseNNAPI(final boolean isChecked) {
        runInBackground(() -> detector.setUseNNAPI(isChecked));
    }

    @Override
    protected void setNumThreads(final int numThreads) {
        runInBackground(() -> detector.setNumThreads(numThreads));

    }
    private Runnable uploadtexttoserver = new Runnable(){
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run()
        {
            try {
                String comfilepath = "D:\\xmapp\\htdocs\\uploads\\"+time+".png";
                String param = "path="+comfilepath+"&member="+id+"&result="+temp+"&latitude="+latitude.substring(0,8)+"&longitude="+longitude.substring(0,9)
                        +"&dateString="+dateString+"&address="+address;
                URL url = new URL("http://59.126.170.89/DEX2.php/");
                // 開始宣告 HTTP 連線需要的物件，這邊通常都是一綑的
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // 建立 Google 比較挺的 HttpURLConnection 物件
                connection.setRequestMethod("POST");
                // 設定連線方式為 POST
                connection.setDoOutput(true); // 允許輸出
                connection.setDoInput(true); // 允許讀入
                connection.setUseCaches(false); // 不使用快取
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                connection.setRequestProperty("Charset", "UTF-8");
                connection.connect(); // 開始連線
                OutputStream out = connection.getOutputStream();
                out.write(param.getBytes());
                out.flush();
                out.close();
                int responseCode =
                        connection.getResponseCode();
                // 建立取得回應的物件
                if(responseCode == HttpURLConnection.HTTP_OK){
                    // 如果 HTTP 回傳狀態是 OK ，而不是 Error
                    InputStream inputStream = connection.getInputStream();
                    // 取得輸入串流
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                    // 讀取輸入串流的資料
                    String box = ""; // 宣告存放用字串
                    String line = null; // 宣告讀取用的字串
                    while((line = bufReader.readLine()) != null) {
                        Log.e("123","777"+line);
                        response_result += line + "\n";
                        // 每當讀取出一列，就加到存放字串後面
                    }
                    Log.e("123",response_result);
                    inputStream.close(); // 關閉輸入串流
                    JSONObject j = new JSONObject(response_result);
                    String ron = j.getString("success");

                }
                // 讀取輸入串流並存到字串的部分
                // 取得資料後想用不同的格式
                // 例如 Json 等等，都是在這一段做處理

            } catch(Exception e) {
//                response_result = e.toString(); // 如果出事，回傳錯誤訊息
            }

            // 當這個執行緒完全跑完後執行
            runOnUiThread(new Runnable() {
                public void run() {
//                    textView.setText(response_result); // 更改顯示文字
                }
            });

        }
    };
    private Runnable uploadimgFile = new Runnable(){
        @SuppressLint("LongLogTag")
        @Override


        public void run() {


            String fileName = mPath;

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(mPath);

            if (!sourceFile.isFile()) {




                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(DetectorActivity.this, "Source File not exist", Toast.LENGTH_SHORT).show();
                    }
                });



            }
            else
            {
                try {

                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    url = new URL(upLoadServerUri);
                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);
                    conn.setRequestProperty("upfile", key);
                    conn.connect();
                    OutputStream gg = conn.getOutputStream();
                    dos = new DataOutputStream(gg);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + fileName + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);



                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    Log.e("uploadFile", "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);
                    if(serverResponseCode == 200){

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(DetectorActivity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                } catch (MalformedURLException ex) {


                    ex.printStackTrace();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(DetectorActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {


                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(DetectorActivity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e);
                }



            } // End else block
        }
    };
    private Runnable uploadtexttofirebase = new Runnable(){
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run()
        {
            try {

//                String comfilepath = "D:\\xmapp\\htdocs\\notify.php";
                String param = "&member="+id+"&result="+temp;
                URL url = new URL("http://59.126.170.89/notify.php/");
                // 開始宣告 HTTP 連線需要的物件，這邊通常都是一綑的
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // 建立 Google 比較挺的 HttpURLConnection 物件
                connection.setRequestMethod("POST");
                // 設定連線方式為 POST
                connection.setDoOutput(true); // 允許輸出
                connection.setDoInput(true); // 允許讀入
                connection.setUseCaches(false); // 不使用快取
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                connection.setRequestProperty("Charset", "UTF-8");
                connection.connect(); // 開始連線
                OutputStream out = connection.getOutputStream();
                out.write(param.getBytes());
                out.flush();
                out.close();
                int responseCode =
                        connection.getResponseCode();
                // 建立取得回應的物件
                if(responseCode == HttpURLConnection.HTTP_OK){
                    // 如果 HTTP 回傳狀態是 OK ，而不是 Error
                    InputStream inputStream = connection.getInputStream();
                    // 取得輸入串流
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                    // 讀取輸入串流的資料
                    String box = ""; // 宣告存放用字串
                    String line = null; // 宣告讀取用的字串
                    while((line = bufReader.readLine()) != null) {
                        Log.e("123","777"+line);
                        response_result += line + "\n";
                        // 每當讀取出一列，就加到存放字串後面
                    }
                    Log.e("123",response_result);
                    inputStream.close(); // 關閉輸入串流
                    Toast.makeText(DetectorActivity.this, "success firebase ", Toast.LENGTH_SHORT).show();
                }
                // 讀取輸入串流並存到字串的部分
                // 取得資料後想用不同的格式
                // 例如 Json 等等，都是在這一段做處理

            } catch(Exception e) {
//                response_result = e.toString(); // 如果出事，回傳錯誤訊息
            }

            // 當這個執行緒完全跑完後執行
            runOnUiThread(new Runnable() {
                public void run() {
//                    textView.setText(response_result); // 更改顯示文字
                }
            });

        }
    };
    private void getPositionGPS() {
        mLocationManagerGPS = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Log.e("485456","123");
        mLocationListenerGPS = new LocationListener() {
            public void onLocationChanged(Location location) {

                Log.e("485456",Double.toString(location.getLatitude())+"sss"+Double.toString(location.getLongitude()));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {

            }
        };

    }
    private void getPositionNetwork() {
        mLocationManagerNetwork = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListenerNetwork = new LocationListener() {
            public void onLocationChanged(Location location) {
                latitude=Double.toString(location.getLatitude());
                longitude=Double.toString(location.getLongitude());
                address = GPS2City(gc,location.getLatitude(),location.getLongitude());
             
                Log.e("485456",latitude+longitude);
                lo = 1;

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {

            }
        };

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {
                mLocationManagerNetwork.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 0, mLocationListenerNetwork);
            }
        }
    }

    private void requestLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.GPS_permissions).setCancelable(false).setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(DetectorActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }).show();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.GPS_permissions).setCancelable(false).setPositiveButton(R.string.btn_watch_permissions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName())));
                }
            }).setNegativeButton(R.string.btn_close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).show();
        }
    }
    public static String GPS2City(Geocoder gc, double latitude, double longitude){
        String city = "";
        String temp = "";
        try{

            List<Address> lstAddress = gc.getFromLocation(latitude, longitude, 1);
            String returnAddress = lstAddress.get(0).getAddressLine(0);
//            temp = returnAddress.replace(lstAddress.get(0).getPostalCode()+lstAddress.get(0).getCountryName(),"");
//            temp = temp.replace(lstAddress.get(0).getFeatureName()+"號","");
            city = returnAddress;
//                    lstAddress.get(0).getCountryName();  //台灣省
//                    lstAddress.get(0).getAdminArea();  //台北市
//                    lstAddress.get(0).getLocality();  //中正區
//                    lstAddress.get(0).getThoroughfare();  //信陽街(包含路巷弄)
//                    lstAddress.get(0).getFeatureName();  //會得到33(號)
//                    lstAddress.get(0).getPostalCode();  //會得到100(郵遞區號)
            Log.e("123","GPS2City: " + city);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return city;
    }
}
