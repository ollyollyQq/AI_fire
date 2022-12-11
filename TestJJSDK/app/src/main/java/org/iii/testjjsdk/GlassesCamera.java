package org.iii.testjjsdk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.http.multipart.MultipartEntity;
import com.jorjin.jjsdk.camera.CameraManager;
import com.jorjin.jjsdk.camera.FrameListener;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GlassesCamera extends AppCompatActivity {
    String inf = "";
    String kaa = "";
    int a = 1;
    SurfaceView surfaceView;
    Context context = this;
    TextView textView8;
    FrameListener frameListener = (buffer, width, height, format) -> {

    };
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private Handler handler = new Handler();
    private Runnable rateRunnable;
    CameraManager cameraManager = new CameraManager(context);;
    String result = "";
    String upLoadServerUri = "http://59.126.170.89/DEX.php/";
    URL url = null;
    private int serverResponseCode = 0;
    String id;
    String fire_id;
    String key = "file";
    String mPath = "";
    LinearLayout mainLinerLayout;
    TextView textview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glassescamera);
        Intent intent = getIntent();
        id=intent.getStringExtra("member_id");
        fire_id=intent.getStringExtra("fire_id");
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            // We don't have permission so prompt the user
            requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        mainLinerLayout = (LinearLayout) this.findViewById(R.id.mytable);
        textView8 = findViewById(R.id.textView8);
        textview=new TextView(this);
        try {
            initCamera();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(getinformation);
        thread.start(); //1 開始執行
        addmessage(kaa);
        try {
            initTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }





    }

    private void initCamera() {

        surfaceView = findViewById(R.id.surface_camera);
        cameraManager = new CameraManager(context);
        cameraManager.setCameraFrameListener(frameListener);
        cameraManager.setResolutionIndex(0);
        cameraManager.addSurfaceHolder(surfaceView.getHolder());
        if(cameraManager.isPreviewing())
        {
            textView8.setText("鏡頭已開啟");

        }
        else {
            textView8.setText("鏡頭關閉");
            cameraManager.stopCamera();
        }
        Button btnCameraOpen = findViewById(R.id.button9);
        Button btnCameraClose = findViewById(R.id.button10);
        btnCameraOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(!cameraManager.isPreviewing())
                    {
                        cameraManager.startCamera(CameraManager.COLOR_FORMAT_RGBA);
                        textView8.setText("鏡頭已開啟");
                    }
                    else
                    {
                        Toast.makeText(GlassesCamera.this,"鏡頭已開啟", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e)
                {
                    textView8.setText("未偵測到鏡頭");
                }


            }
        });

        btnCameraClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cameraManager.isPreviewing())
                {
                    textView8.setText("鏡頭關閉");
                    cameraManager.stopCamera();
                }
            }
        });



        Button button5 = findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraManager.takePicture();
                File f = new File(Environment.getExternalStorageDirectory()+"/JJSDK/");
                File [] imgnamelist = f.listFiles();
                String temp = "";
                String lastimgph = "";
                long time = 0;
                long lastt = 0;
                for(int i = 0;i < f.listFiles().length;i++)
                {
                    temp = imgnamelist[i].getName();
                    time = imgnamelist[i].lastModified();
                    if(lastt <= time)
                    {
                        lastt = time;
                        lastimgph = temp;
                    }
                }

                mPath = Environment.getExternalStorageDirectory()+"/JJSDK/" + temp;
                Thread thread1 = new Thread(uploadimgFile);
                thread1.start(); // 開始執行
            }
        });


    }

    private Runnable uploadimgFile = new Runnable(){
        @SuppressLint("LongLogTag")
        @Override


        public void run() {


            String fileName = mPath;
            Log.e("12346546",fileName);
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
                        Toast.makeText(GlassesCamera.this, "Source File not exist"+"\n"+mPath, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(GlassesCamera.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(GlassesCamera.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {


                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(GlassesCamera.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e);
                }



            } // End else block
        }
    };




    private void initTimer() {
        rateRunnable = () -> {
            mainLinerLayout.removeAllViews();
            if(cameraManager.isPreviewing())
                textView8.setText("鏡頭已開啟");
            Thread thread = new Thread(getinformation);
            thread.start(); //1 開始執行
            addmessage(kaa);

            handler.postDelayed(rateRunnable, 2000);
        };
        handler.postDelayed(rateRunnable, 2000);
    }

    private Runnable getinformation = new Runnable(){
        public void run()
        {
            try {
                String param = "&fire_id="+fire_id;
                URL url = new URL("http://59.126.170.89/getinformation.php/");
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
                        Log.e("123","777");
                        result += line + "\n";
                        // 每當讀取出一列，就加到存放字串後面
                    }
                    inputStream.close(); // 關閉輸入串流
                    JSONObject j = new JSONObject(result);
                    String key = j.getString("success");

                    if(key.equals("1"))
                    {
                        inf = j.getString("in");
                        inf = inf.replace("[","");
                        inf = inf.replace("]","");
                        String[] a = inf.split(",");
                        //String kaa = "";


                        for(int i = a.length-1;i >= 0; i--)
                        {
                            kaa += a[i] + "\n";

                        }


                        result = "回報成功";
                    }
                }
                // 讀取輸入串流並存到字串的部分
                // 取得資料後想用不同的格式
                // 例如 Json 等等，都是在這一段做處理

            } catch(Exception e) {
                result = e.toString(); // 如果出事，回傳錯誤訊息
            }

            // 當這個執行緒完全跑完後執行
            runOnUiThread(new Runnable() {
                public void run() {
                    result = "";
                }
            });
        }
    };
    public void addmessage(String s){
        if (a == 1)
        {
            kaa = "";
            textview.setText("");
            textview.setText(s);
            textview.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
            mainLinerLayout.addView(textview);
        }

    }

}
//    String currentPhotoPath;
//
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        currentPhotoPath = image.getAbsolutePath();
//        return image;
//    }
//    static final int REQUEST_TAKE_PHOTO = 1;
//
//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//            }
//        }
//    }
//    private void galleryAddPic() {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(currentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
//    }

