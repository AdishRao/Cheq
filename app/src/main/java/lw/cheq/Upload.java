package lw.cheq;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.util.HashMap;

public class Upload extends AppCompatActivity implements View.OnClickListener {
    Bitmap bmp = null;
    ImageView cheque;
    private Button login;
    HashMap<String, EditText> fields = new HashMap<>();
    ApplicationController applicationController = new ApplicationController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        cheque = findViewById(R.id.cheqimage);
        cheque.setImageBitmap(bmp);

        login = findViewById(R.id.button);

        login.setOnClickListener(this);

        fields.put("@string/chq_num", (EditText) findViewById(R.id.CHQ_NUM));
        fields.put("@string/amount_words", (EditText) findViewById(R.id.AMOUNT_WORDS));
        fields.put("@string/amount_digit", (EditText) findViewById(R.id.AMOUNT_DIGIT));
        fields.put("@string/chq_date", (EditText) findViewById(R.id.CHQ_DATE));
        fields.put("@string/micr_code", (EditText) findViewById(R.id.MICR_CODE));
        fields.put("@string/act_type", (EditText) findViewById(R.id.ACT_TYPE));
        fields.put("@string/ben_name", (EditText) findViewById(R.id.BEN_NAME));
        fields.put("@string/payee_ac_no", (EditText) findViewById(R.id.PAYEE_AC_NO));
        fields.put("@string/amt_match", (EditText) findViewById(R.id.AMT_MATCH));
        fields.put("@string/chq_stale", (EditText) findViewById(R.id.CHQ_STALE));
    }

    @Override
    public void onClick(View view) {
        if (view == login) {
            sendToAPI();
        }
    }

    private void sendToAPI() {
        final String URL = "https://private-anon-f1ac857084-chequeinsertrecord.apiary-mock.com/InsertChqDetails/";
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<>();
        params.put("token", "AbCdEfGh123456");

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                    }

                });

        // add the request object to the queue to be executed
        try {
            applicationController.addToRequestQueue(req);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }


        login.setVisibility(View.INVISIBLE);

    }
}
