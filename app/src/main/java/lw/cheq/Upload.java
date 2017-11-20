package lw.cheq;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Upload extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Volley";
    Bitmap bmp = null;
    ImageView cheque;
    HashMap<String, EditText> fields = new HashMap<>();
    CheckBox amt_match, chq_stale, sbiapi;
    View view;
    ProgressDialog progressDialog;
    EditText chqdate;
    FirebaseStorage storage;
    // Create a storage reference from our app
    StorageReference storageRef;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Button upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        view = findViewById(R.id.sample_main_layout);
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        cheque = findViewById(R.id.cheqimage);
        chqdate = findViewById(R.id.CHQ_DATE);

        chqdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(Upload.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String[] months = {
                        "Jan",
                        "Feb",
                        "Mar",
                        "Apr",
                        "May",
                        "Jun",
                        "Jul",
                        "Aug",
                        "Sept",
                        "Oct",
                        "Nov",
                        "Dec"
                };
                String date = dayOfMonth + "-" + months[month] +"-" + year;
                chqdate.setText(date);

            }
        };
        cheque.setImageBitmap(bmp);

        upload = findViewById(R.id.button);

        upload.setOnClickListener(this);

        sbiapi = findViewById(R.id.SBIAPI);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        amt_match = findViewById(R.id.AMT_MATCH);
        chq_stale = findViewById(R.id.CHQ_STALE);

        fields.put(getString(R.string.chq_num), (EditText) findViewById(R.id.CHQ_NUM));
        fields.put(getString(R.string.amount_words), (EditText) findViewById(R.id.AMOUNT_WORDS));
        fields.put(getString(R.string.amount_digit), (EditText) findViewById(R.id.AMOUNT_DIGIT));
        fields.put(getString(R.string.chq_date), (EditText) findViewById(R.id.CHQ_DATE));
        fields.put(getString(R.string.micr_code), (EditText) findViewById(R.id.MICR_CODE));
        fields.put(getString(R.string.act_type), (EditText) findViewById(R.id.ACT_TYPE));
        fields.put(getString(R.string.ben_name), (EditText) findViewById(R.id.BEN_NAME));
        fields.put(getString(R.string.payee_ac_no), (EditText) findViewById(R.id.PAYEE_AC_NO));
        fields.put(getString(R.string.san_no), (EditText) findViewById(R.id.SAN_NO));
    }

    @Override
    public void onClick(View view) {
        if (view == upload) {
            // TODO @Adish do form validation here, before calling these functions
            String chq_num = fields.get("CHQ_NUM").getText().toString();

            if (chq_num.length() > 6) {
                Toast.makeText(Upload.this, "Cheque Number too long",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            sendToFirebase();
            if (sbiapi.isChecked()) {
                sendToAPI();
            }
        }
    }

    private void sendToFirebase() {
        // Get cheque number from field
        String chq_num = fields.get("CHQ_NUM").getText().toString();
        // We create a StorageReference to <cheque_number.jpg>
        final StorageReference chequeRef = storageRef.child(chq_num + ".jpg");

        // Detect if file already exists
        chequeRef.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            // if file does not exist do this
            @Override
            public void onFailure(@NonNull Exception e) {
                // Convert bmp imag to byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
               int k = bmp.getByteCount();
                bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                int j= bmp.getByteCount();
                byte[] data = baos.toByteArray();
               int i= baos.size();


                // Upload
                UploadTask uploadTask = chequeRef.putBytes(data);
                // Upload listeners
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        /*Snackbar snackbar = Snackbar
                                .make(view, "Error Uploading to Firebase : " + exception.getMessage(), Snackbar.LENGTH_LONG);

                        snackbar.show();*/
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        /*Snackbar snackbar = Snackbar
                                .make(view, "Pushed Successfully!", Snackbar.LENGTH_LONG);

                        snackbar.show();*/
                    }
                });
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpg")
                        .setCustomMetadata("CHQ_NUM", fields.get("CHQ_NUM").getText().toString())
                        .build();
            }

        });
    }

    private void sendToAPI() {
        final String URL = "http://apiplatformcloudse-gseapicssbisecond-uqlpluu8.srv.ravcloud.com:8001/InsertChqDetails";

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<>();

        progressDialog = new ProgressDialog(Upload.this);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();

        //converting image to base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        final byte[] imageBytes = baos.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        //sending image to server
        StringRequest request = new StringRequest(Request.Method.POST, URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                if (s.equals("NOT A JSON or XML:")) {
                    Toast.makeText(Upload.this, "Uploaded Successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Upload.this, "Some error occurred!", Toast.LENGTH_LONG).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(Upload.this, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                return imageBytes;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();

                for (Map.Entry<String, EditText> entry : fields.entrySet()) {
                    headers.put(entry.getKey(), entry.getValue().getText().toString());
                }
                headers.put("api-key", getString(R.string.api_key));
                headers.put("TEAM_ID", getString(R.string.team_id));
                headers.put("MIME_TYPE", "image/jpeg");
                headers.put("ENCODING", "None");
                headers.put("AMT_MATCH", (amt_match.isChecked() ? "1" : "0"));
                headers.put("CHQ_STALE", (chq_stale.isChecked() ? "1" : "0"));

                // TODO Find out a way to calculate image size and put it over here
                //headers.put("IMG_SIZE", )
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String body;
                try {
                    body = new String(response.data, "UTF-8");
                    Log.d(TAG, body);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(Upload.this);
        rQueue.add(request);

        //upload.setVisibility(View.INVISIBLE);
    }
}
