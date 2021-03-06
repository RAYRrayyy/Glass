package talent.virtualtourskeleton;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vuforia.samples.VuforiaSamples.app.VuMark.VuMark;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import javax.net.ssl.HttpsURLConnection;

public class FeedbackActivity extends AppCompatActivity {
    Button like, dislike;
    TextView recommendation, locationString;
    String user, location, preference;
    EditText userSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        like = (Button)findViewById(R.id.like);
        dislike = (Button)findViewById(R.id.dislike);

        recommendation = (TextView)findViewById(R.id.recommendation); //Output/Next location recommendation
        Intent intent = getIntent();
        location = String.valueOf(intent.getIntExtra("location", 0));
        locationString = (TextView) findViewById(R.id.location_string);
        locationString.setText(LocationsClass.spotNames[Integer.parseInt(location)-1]);

        userSelect = (EditText)findViewById(R.id.userSelect);

        /*When the Like Button is Hit*/
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = userSelect.getText().toString();
                preference = "1"; //Like
                new SendRequest().execute();
            }

        }   );

        /*When the Dislike Button is Hit*/
        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = userSelect.getText().toString();
                preference = "-1"; //Dislike
                new SendRequest().execute();
            }

        }   );

    }

    private void backToMain(String recc) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("RECOMMENDATIONS", rec);
//        startActivity(intent);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("reccomendation", recc);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void delay(int Seconds){
        long Time = 0;
        Time = System.currentTimeMillis();
        while(System.currentTimeMillis() < Time+(Seconds*1000));
    }
    public class SendRequest extends AsyncTask<String, Void, String> {
        protected void onPreExecute(){}
        protected String doInBackground(String... arg0) {
            try{
                //Change your web app deployed URL or or u can use this for attributes (name, country)
                URL url = new URL("https://script.google.com/macros/s/AKfycbya-jg7flbxyY9xEnG91Nv4_wtNx_oob9RwwRHdg2WJoUX9Eps/exec");
                JSONObject postDataParams = new JSONObject();
                //int i;
                //for(i=1;i<=70;i++)
                //    String usn = Integer.toString(i);
                String id= "13ordFQXWTch9kvvulG08rtA76W-nKKcHwk887UiY59Q";
                postDataParams.put("X","0");
                postDataParams.put("user",user);
                postDataParams.put("location",location);
                postDataParams.put("preference",preference);
                postDataParams.put("id",id);

                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getApplicationContext(), result,
            //        Toast.LENGTH_LONG).show();
            recommendation.setText(result);
            delay(3);
            new GetRecommendationRequest().execute();
        }
    }

    public class GetRecommendationRequest extends AsyncTask<String, Void, String> {
        protected void onPreExecute(){}
        protected String doInBackground(String... arg0) {
            try{
                //Change your web app deployed URL or or u can use this for attributes (name, country)
                URL url = new URL("https://script.google.com/macros/s/AKfycbya-jg7flbxyY9xEnG91Nv4_wtNx_oob9RwwRHdg2WJoUX9Eps/exec");
                JSONObject postDataParams = new JSONObject();
                //int i;
                //for(i=1;i<=70;i++)
                //    String usn = Integer.toString(i);
                String id= "13ordFQXWTch9kvvulG08rtA76W-nKKcHwk887UiY59Q";
                postDataParams.put("X","1");
                postDataParams.put("user",user);
                postDataParams.put("id",id);

                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getApplicationContext(), result,
            //        Toast.LENGTH_LONG).show();
            recommendation.setText(result);
            backToMain(result.substring(2, result.length()-2));
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
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



    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            Toast.makeText(parent.getContext(),
                    "Current Location Set to : " + parent.getItemAtPosition(pos).toString(),
                    Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }
}
