package course.examples.recipewizard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 11/15/2015.
 */



public class SingleRecipeActivity extends Activity  {

    private static String stringdata;

    ImageView recipe_picture;
    ArrayAdapter<String> ingredientsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_recipe);

        /*set recipe code for test use*/
        Intent i = getIntent();
        String lableTest = i.getStringExtra("recipeId");
        Log.i("SingleRecipe",lableTest);

        String recipeCode;

        recipeCode = lableTest;
        //recipeCode = "French-Onion-Soup-The-Pioneer-Woman-Cooks-_-Ree-Drummond-41364";

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
                if (mObject == null) {
                    onBackPressed();
                }

                //get image url from json
                JSONObject imageObject = getImageObject(mObject);
                if (imageObject == null) {
                    setDefaultImage();
                } else {
                    String bitmapUrl = getImageString(imageObject);
                    if (bitmapUrl == null) {
                        setDefaultImage();
                    } else {

                        Log.i("SingleRecipe", bitmapUrl);
                        String[] pass = new String[1];

                        //set testing image url
                        pass[0] = bitmapUrl;
                        //setDefaultImage();
                        new imageDownloaderTask().execute(pass);
                    }
                }

                //get name from the json object
                //get ingredient lines from the json object
                //get energy and serves from the ingredients
                SingleRecipeObject recipeObject = new SingleRecipeObject(mObject);
                //set title
                TextView titleView = (TextView) findViewById(R.id.recipe_name);
                titleView.setText(recipeObject.name);

                /*Log.i("SingleRecipe", "serves: " + recipeObject.serves + " energy: " + recipeObject.energy
                        + " fat: " + recipeObject.fat + " protein: " + recipeObject.protein +
                        " carb: " + recipeObject.carb);
*/
                //adding webpage and ingredients list
                TextView ingredients = (TextView) findViewById(R.id.ingredients_text);
                if (recipeObject.ingredients != null){
                    String temp = "Ingredients: ";


                    for (String str : recipeObject.ingredients){
                        temp += ("\n \u2022" + str );
                    }

                    ingredients.setText(temp);
                }else{
                    ingredients.setText("Ingredients: Not Available");
                }

                TextView webpage = (TextView) findViewById(R.id.webpage);
                if (recipeObject.webpage != null){
                    webpage.setText(Html.fromHtml("<a href='"+recipeObject.webpage +"'>Directions Webpage</a>"));
                    webpage.setMovementMethod(LinkMovementMethod.getInstance());
                }else{
                    webpage.setText("Directions not available for this recipe");
                }

                TextView serve = (TextView)findViewById(R.id.serves_data);
                TextView energy = (TextView)findViewById(R.id.energy_data);
                TextView fat = (TextView)findViewById(R.id.fat_data);
                TextView protein = (TextView)findViewById(R.id.protein_data);
                TextView carb = (TextView)findViewById(R.id.carbs_data);

                serve.setText(recipeObject.serves+"");

                if(recipeObject.energy==-1) {
                    energy.setText("not available");
                }else{
                    energy.setText(recipeObject.energy+" kcal");
                }

                if(recipeObject.fat==-1) {
                    fat.setText("not available");
                }else{
                    fat.setText(recipeObject.fat+" g");
                }

                if(recipeObject.protein==-1) {
                    protein.setText("not available");
                }else{
                    protein.setText(recipeObject.protein+" g");
                }
                if(recipeObject.carb==-1) {
                    carb.setText("not available");
                }else{
                    carb.setText(recipeObject.carb + " g");
                }

                /* debug info for ingredients list
                for (int i =0; i<recipeObject.ingredients.size();i++){
                    Log.i("SingleRecipe",recipeObject.ingredients.get(i));
                }*/

                /*
                *
                *
                *
                *
                * */
                LinearLayout ingredientsLinearLayout = (LinearLayout) findViewById(R.id.ingredientsLinearLayout);
                for (int i = 0; i<recipeObject.ingredients.size();i++){
                    TextView textView = new TextView(SingleRecipeActivity.this);
                    textView.setText(recipeObject.ingredients.get(i));
                    ingredientsLinearLayout.addView(textView);
                }

            }
            else{
                onBackPressed();
                Log.i("SingleRecipe","get null data string form server");
            }
        }

        private JSONObject stringToJSON(String data){
            try {
                JSONObject mObject = new JSONObject(data);
                return mObject;
            }catch (Exception e){
                e.printStackTrace();
                Log.i("SingleRecipe","unable to convert datastring to JSON object");
                return null;
            }
        }

        private JSONObject getImageObject(JSONObject mObject){
            try{
                JSONObject imageObject = (JSONObject)mObject.getJSONArray("images").get(0);
                //Log.i("SingleRecipe", imageObject.getClass().toString());

                return imageObject;
            }catch (Exception e){
                e.printStackTrace();
                Log.i("SingleRecipe", "No image object found");
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
                    Log.i("SingleRecipe","no suitable image found in image object");
                    return null;
                }

            }catch(Exception e){
                e.printStackTrace();
                Log.i("SingleRecipe", "no suitable image found in image object");
                return null;
            }
        }

        private void setDefaultImage(){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_image_available);
            setContentBitmap(bitmap);
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

    private class SingleRecipeObject{
        protected float energy;
        protected List<String> ingredients;
        protected String name;
        protected int serves;
        protected float carb,fat,protein;
        protected JSONArray nutritions;
        protected String webpage;


        SingleRecipeObject(JSONObject mObject){
            ingredients = new ArrayList<String>();
            //energy = -1;
            //name = null;
            //serves = -1;

            try{
                webpage = mObject.getJSONObject("source").getString("sourceRecipeUrl");
                //Log.i("SingleRecipe",name);
            }catch (Exception e){
                e.printStackTrace();
                webpage = null;
            }

            //get name
            try{
                name = mObject.getString("name");
                //Log.i("SingleRecipe",name);
            }catch (Exception e){
                e.printStackTrace();
                name = null;
            }

            //get energy

            //get serves
            try{
                serves = mObject.getInt("numberOfServings");
                //Log.i("SingleRecipe","serves: "+serves);
            }catch (Exception e){
                e.printStackTrace();
                serves = -1;
            }

            //get nutritional estimates
            try {
                //Log.i("SingleRecipe", mObject.get("nutritionEstimates").getClass().toString());
                nutritions = mObject.getJSONArray("nutritionEstimates");

            }catch (Exception e){
                e.printStackTrace();
                Log.i("SingleRecipe","fail to retrive nutrition array");
            }

            carb = -1;
            fat = -1;
            protein = -1;

            if (nutritions!=null){
                try{
                    //Log.i("SingleRecipe", nutritions.get(0).getClass().toString());
                    for (int i = 0;i<nutritions.length();i++){
                        JSONObject temp = nutritions.getJSONObject(i);
                        //fat
                        if (temp.getString("attribute").equals("FAT")){
                            fat = (float) temp.getDouble("value");
                        }
                        //protein
                        if (temp.getString("attribute").equals("PROCNT")){
                            protein = (float) temp.getDouble("value");
                        }
                        //carb
                        if (temp.getString("attribute").equals("CHOCDF")){
                            carb = (float) temp.getDouble("value");
                        }
                        //energy
                        if (temp.getString("attribute").equals("ENERC_KCAL")){
                            energy = (float) temp.getDouble("value");
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            try{
                //Log.i("SingleRecipe",mObject.get("ingredientLines").getClass().toString());
                JSONArray temp = mObject.getJSONArray("ingredientLines");
                for (int i = 0; i< temp.length();i++){
                    //Log.i("SingleRecipe",temp.getString(i));
                    ingredients.add(temp.getString(i));
                }

            }catch (Exception e){
                e.printStackTrace();
                Log.i("SingleRecipe","ingredients not found");
            }

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
