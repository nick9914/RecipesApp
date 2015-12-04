package course.examples.recipewizard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Andrew on 11/15/2015.
 */



public class SingleRecipeActivity extends Activity  {

    private static String stringdata;

    ImageView recipe_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_recipe);

        /*set recipe code for test use*/
        String recipeCode = "French-Onion-Soup-The-Pioneer-Woman-Cooks-_-Ree-Drummond-41364";
        String[] pass = new String[1];
        pass[0] = recipeCode;
        /*
        * test url for onion soup
        * http://api.yummly.com/v1/api/recipes?_app_id=17a74454&_app_key=3ee9a972a2fd829f69fb1f077189185e&q=onion+soup
        * */

        if (!checkNetworkConnection()){
            finish();
        }

        new HttpGetSingleRecipeTask().execute(recipeCode);

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
        private static final String BASE_URL = "http://api.yummly.com/v1/api/recipe/";
        /*
                +"French-Onion-Soup-The-Pioneer-Woman-Cooks-_-Ree-Drummond-41364" +"?_app_id="
                + APP_ID +"&_app_key=" + APP_KEY;
        */
        @Override
        protected String doInBackground(String... pass){

            String mUrl = BASE_URL+pass[0]+"?_app_id="+APP_ID +"&_app_key=" + APP_KEY;
            Log.i("SingleRecipe", pass[0]);

            try {
                return downloadURL(mUrl);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        private String downloadURL(String mUrl) throws IOException{

            InputStream is = null;

            try{
                URL url = new URL(mUrl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000  /*milliseconds*/);
                conn.setConnectTimeout(15000 /*milliseconds*/);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();

                int response = conn.getResponseCode();

                is = conn.getInputStream();


                String contentAsString = readStream(is);


                return contentAsString;
            }
            finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        private String readStream(InputStream in){
            BufferedReader reader = null;
            StringBuffer data = new StringBuffer("");
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    data.append(line);
                    //Log.i("SingleRecipe",line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return data.toString();
        }

        @Override
        protected void onPostExecute(String s){
            if (s!=null) {
                //Log.i("SingleRecipe", s);
                stringdata = s;
                logString();
                JSONObject mObject = stringToJSON(s);
                if (mObject==null){
                    onBackPressed();
                }

                //get image url from json
                JSONObject imageObject = getImageObject(mObject);

                if (imageObject==null){
                    onBackPressed();
                }
                String bitmapUrl = getImageString(imageObject);
                if (bitmapUrl==null){
                    onBackPressed();
                }
                Log.i("SingleRecipe",bitmapUrl);
                String[] pass = new String[1];

                //set testing image url
                pass[0] = bitmapUrl;
                new imageDownloaderTask().execute(pass);
            }
            else{
                Log.i("SingleRecipe","get null String");
            }
        }

        private JSONObject stringToJSON(String data){
            try {
                JSONObject mObject = new JSONObject(data);

                return mObject;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        private JSONObject getImageObject(JSONObject mObject){
            try{

                JSONObject imageObject = (JSONObject)mObject.getJSONArray("images").get(0);
                Log.i("SingleRecipe", imageObject.getClass().toString());



                return imageObject;
            }catch (Exception e){
                e.printStackTrace();
                Log.i("SingleRecipe", "No image url found");
                return null;
            }

        }

        private String getImageString(JSONObject imageObject){
            try{
                if (imageObject.has("hostedLargeUrl")) {
                    return imageObject.getString("hostedLargeUrl");

                }else if (imageObject.has("hostedMediumUrl")){
                    return imageObject.getString("hostedMediumUrl");
                }else if (imageObject.has("hostedSmallUrl")){
                    return imageObject.getString("hostedSmallUrl");
                }else{
                    return null;
                }

            }catch(Exception e){
                e.printStackTrace();

                return null;
            }

        }

    }

    /*
    * downloader task for recipe image in case can not pass image through the intent
    *
    *
    */

    private class imageDownloaderTask extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... pass){
            try {
                String murl = pass[0];
                URL url = new URL(murl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000  /*milliseconds*/);
                conn.setConnectTimeout(15000 /*milliseconds*/);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);

                Bitmap bitmap = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
                return bitmap;
            }catch (Exception e){
                e.printStackTrace();
                Log.i("SingleRecipe","error downloading bitmap");
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap){
            setContentBitmap(bitmap);

        }

    }


    private void logString(){
        Log.i("SingleRecipe",stringdata);
    }

    public void setContentBitmap(Bitmap bitmap){
        recipe_picture = (ImageView) findViewById(R.id.recipe_image);
        recipe_picture.setImageBitmap(bitmap);
    }

}
