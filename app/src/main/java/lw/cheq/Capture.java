package lw.cheq;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import android.Manifest;
import java.io.FileOutputStream;

public class Capture extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    JavaCameraView javaCameraView;
    private View mLayout;
    private static final String TAG= "CaptureActivity";
    Mat mRgba, imgGray, imgCanny;
    Bitmap bmp = null;

    /**
     * Id to identify a camera permission request.
     */
    private static final int REQUEST_CAMERA = 0;

    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case BaseLoaderCallback.SUCCESS:{
                    javaCameraView.enableView();
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        mLayout = findViewById(R.id.sample_main_layout);
        OpenCVLoader.initDebug();
        //Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // startActivity(intent);
        // Here, thisActivity is the current activity
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            requestCameraPermission();

        } else {

            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "CAMERA permission has already been granted. Displaying camera preview.");
            javaCameraView = findViewById(R.id.java_cam);
            javaCameraView.setVisibility(SurfaceView.VISIBLE);
            javaCameraView.setCvCameraViewListener(this);
        }
    }
    /**
     * Requests the Camera permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestCameraPermission() {
        Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying camera permission rationale to provide additional context.");
            Snackbar.make(mLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(Capture.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                        }
                    })
                    .show();
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
    }

    public void ButtonOnClick(View v) {
        switch (v.getId()) {
            case R.id.button2:
                finish();
                try {
                    //Write file
                    String filename = "bitmap.png";
                    FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    //Cleanup
                    stream.close();
                    bmp.recycle();

                    //Pop intent
                    Intent in1 = new Intent(this, Upload.class);
                    in1.putExtra("image", filename);
                    startActivity(in1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                    break;
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        if(javaCameraView!=null){
            javaCameraView.disableView();

        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(javaCameraView!=null){
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(OpenCVLoader.initDebug())
        {
            Log.d(TAG,"Working");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        }
        else
        {
            Log.d(TAG,"Not");

        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        imgGray = new Mat(height, width, CvType.CV_8UC1);
        imgCanny = new Mat(height, width, CvType.CV_8UC1);

    }

    @Override
    public void onCameraViewStopped() {

        mRgba.release();

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        try {
        Imgproc.cvtColor(mRgba,imgGray,Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(imgGray,imgCanny,30,200);
        bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mRgba, bmp);
        }
        catch (CvException e){Log.d("Exception",e.getMessage());}
        return imgCanny;
    }
}
