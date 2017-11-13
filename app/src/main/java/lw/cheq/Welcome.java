package lw.cheq;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_welcome);

        login = findViewById(R.id.button);

        login.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == login) {
            finish();
            startActivity(new Intent(this,Login.class));
        }
    }
}
