package lw.cheq;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.opencv.android.OpenCVLoader;

public class Welcome extends AppCompatActivity implements View.OnClickListener  {

    private static final String TAG= "WelcomeActivity";
    private Button login;

    static {
        if(OpenCVLoader.initDebug())
        {
            Log.d(TAG,"Working");
        }
        else
        {
            Log.d(TAG,"Not");

        }
    }

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        login = (Button) findViewById(R.id.button);

        login.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == login) {
            finish();
            startActivity(new Intent(this,Capture.class));
        }
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
}
