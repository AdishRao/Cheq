package lw.cheq;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Capture extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    JavaCameraView javaCameraView;
    private static final String TAG= "CaptureActivity";
    Mat mRgba, imgGray, imgCanny;
    Bitmap imgCannyb;
    Button captureb;
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

        OpenCVLoader.initDebug();
        //Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
       // startActivity(intent);

            javaCameraView =(JavaCameraView) findViewById(R.id.java_cam);
            captureb = findViewById(R.id.button2);
            javaCameraView.setVisibility(SurfaceView.VISIBLE);
            javaCameraView.setCvCameraViewListener(this);

            captureb.setOnClickListener((View.OnClickListener) this);
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

        Imgproc.cvtColor(mRgba,imgGray,Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(imgGray,imgCanny,30,200);
        Utils.matToBitmap(imgCanny,imgCannyb);
        return imgCanny;
    }
}
