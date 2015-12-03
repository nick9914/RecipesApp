package course.examples.recipewizard;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Andrew on 11/15/2015.
 */
public class SingleRecipeActivity extends Activity  {

    private static String recipeCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_recipe);

        /*set recipe code for test use*/
        recipeCode = "onion+soup";

        /*
        * test url for onion soup
        * http://api.yummly.com/v1/api/recipes?_app_id=17a74454&_app_key=3ee9a972a2fd829f69fb1f077189185e&q=onion+soup
        * */

        if (!checkNetworkConnection()){
            finish();
        }



    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(this.getApplicationContext(), "No Network Connection", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    private class HttpGetSingleRecipeTask extends AsyncTask<String,Void,String>{

        private static final String APP_ID = "17a74454";
        private static final String APP_KEY = "3ee9a972a2fd829f69fb1f077189185e";
        private static final String BASE_URL = "http://api.yummly.com/v1/api/recipes?_app_id="
                + APP_ID +"&_app_key=" + APP_KEY + "&q=";

        @Override
        protected String doInBackground(String... recipeCode){
            String mUrl = BASE_URL + recipeCode;


            try {
                return downloadURL(mUrl);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        private String downloadURL(String mUrl) throws IOException{

            URL url = new URL(mUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000  /*milliseconds*/);
            conn.setConnectTimeout(15000 /*milliseconds*/);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();

            int response = conn.getResponseCode();

            InputStream is = conn.getInputStream();

            /*following line does not compile*/
            //String contentAsString = readStream(is);


            return null;
        }


    }




}
