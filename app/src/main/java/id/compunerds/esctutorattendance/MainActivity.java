package id.compunerds.esctutorattendance;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static id.compunerds.esctutorattendance.MyApp.db;

public class MainActivity extends AppCompatActivity {

    String scannedData;
    Button scanBtn;
    Siswa siswa;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    List<Siswa> listSiswa = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Activity activity = this;

        recyclerView = findViewById(R.id.rvDatabaseScan);
        scanBtn = findViewById(R.id.scan_btn);

        fetchDataFromRoom();
        initRecyclerView();
        setAdapter();

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setBeepEnabled(false);
                integrator.setCameraId(0);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
    }

    private void setAdapter() {
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter =new RecyclerAdapter(this, listSiswa);
        recyclerAdapter.notifyDataSetChanged();
    }

    private void fetchDataFromRoom() {
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class,"siswa").allowMainThreadQueries().build();
        listSiswa = db.userDao().getAll();

        for (int i = 0 ;i <listSiswa.size();i++){
            Log.e("Aplikasi",listSiswa.get(i).getNama()+i);
            Log.e("Aplikasi",listSiswa.get(i).getTglMulai()+i);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            scannedData = result.getContents();
            if (scannedData != null) {
                // Here we need to handle scanned data...
                new SendRequest().execute();
                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);

                SimpleDateFormat df = new SimpleDateFormat("h:mm a dd MMMM yyyy");
                String formattedDate = df.format(c);

                scannedData = result.getContents();
                siswa = new Siswa();
                siswa.setNama(scannedData);
                siswa.setTglMulai(formattedDate);
                db.userDao().insertAll(siswa);
            } else {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();
        while (itr.hasNext()) {
            String key = itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    public class SendRequest extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {

            try {

                //Enter script URL Here
                URL url = new URL("https://script.google.com/macros/s/AKfycbyRkqkFxle5Dxsf9vKU1BT2FNt2tsjfNoxCakjbr9p1mH1aoBTh/exec");

                JSONObject postDataParams = new JSONObject();

                //Passing scanned code as parameter
                postDataParams.put("sdata", scannedData);

                Log.e("params", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer();
                    String line = "";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();
                } else {
                    return "false : " + responseCode;
                }

            } catch (Exception e) {
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this, "SCAN SUCCESS", Toast.LENGTH_SHORT).show();
            fetchDataFromRoom();
            initRecyclerView();
            setAdapter();

        }
    }
}
