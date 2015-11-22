package course.examples.recipewizard;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecipesActivity extends Activity {
    ListView mlistView;
    List<RecipeListObject> mlistOfRecipes;
    RecipeListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Initialize global variables and listners*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        mlistView = (ListView) findViewById(R.id.listview);
        mlistOfRecipes = new ArrayList<>();
        mAdapter = new RecipeListAdapter(this, mlistOfRecipes);
        mlistView.setAdapter(mAdapter);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*TODO: react to selections in the list*/
                Intent intent = new Intent(RecipesActivity.this,SingleRecipeActivity.class);
                RecipeListObject recipe = (RecipeListObject) parent.getItemAtPosition(position);
                recipe.packToIntent(intent);
                startActivity(intent);

            }
        });

        /*Check Network Connection*/
        if (!checkNetworkConnection()) {
            /*TODO: Go back to previous activity*/
            finish();
        }
        /*Get Recipes from Yummly API*/
        new HttpGetRecipesTask().execute();


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class HttpGetRecipesTask extends AsyncTask<Void, Void, List<RecipeListObject>> {
        private static final String DEBUG_TAG = "HttpGetRecipes";
        /*UserName*/
        private static final String APP_ID = "17a74454";
        private static final String APP_KEY = "3ee9a972a2fd829f69fb1f077189185e";

        private static final String BASE_URL = "http://api.yummly.com/v1/api/recipes?_app_id=" +
                APP_ID + "&_app_key=" + APP_KEY;

        private static final String User_Ingredients = "user_ingredient_file.JSON";

        private static final int MAX_RESULT = 10;


        @Override
        protected List<RecipeListObject> doInBackground(Void... params) {
            try {
                /*Get ingredients for user_ingredient_file.JSON*/
                JSONArray userIngredients = new JSONArray(loadUserIngredientsJSONFromAsset());
                /*Construct GET URL*/
                StringBuilder urlWithParameters = new StringBuilder(BASE_URL);

                for (int i = 0; i < userIngredients.length(); i++) {
                    JSONObject ingredient = userIngredients.getJSONObject(i);
                    String searchIngredientName = ingredient.getString("searchValue");
                    urlWithParameters.append("&excludedIngredient[]=");
                    urlWithParameters.append(searchIngredientName);
                }
                urlWithParameters.append("&maxResult=" + MAX_RESULT);
                urlWithParameters.append("&requirePictures=true");
                return downloadUrl(urlWithParameters.toString());

            } catch (JSONException e) {
                Log.i(DEBUG_TAG, "Could not load user_ingredient_file.JSON");
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                Log.i(DEBUG_TAG, "Unable to retrieve web page. URL may be invalid.");
                e.printStackTrace();
                return null;

            }

        }

        private List<RecipeListObject> downloadUrl(String myurl) throws IOException {
            /*TODO: Finish this Method*/
            /*http://developer.android.com/training/basics/network-ops/connecting.html#connection*/
            InputStream is = null;
            String contentAsString;
            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000  /*milliseconds*/);
                conn.setConnectTimeout(15000 /*milliseconds*/);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(DEBUG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                contentAsString = readStream(is);

                return fromJSONToRecipeListObjects(contentAsString);

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        private List<RecipeListObject> fromJSONToRecipeListObjects(String contentAsString) {
            try {
                JSONObject jsonObject = new JSONObject(contentAsString);
                JSONArray matchesArray = (JSONArray) jsonObject.get("matches");
                List<RecipeListObject> listOfRecipes = new ArrayList<>();
                for (int i = 0; i < matchesArray.length(); i++) {
                    JSONObject JSONRecipeObject = matchesArray.getJSONObject(i);
                    RecipeListObject recipeListObj = new RecipeListObject();
                    recipeListObj.setRating(JSONRecipeObject.getString("rating"));
                    recipeListObj.setRecipeLabel("recipeName");
                    /*TODO: Write Code to download image*/
                    recipeListObj.setRecipePicture(null);
                    listOfRecipes.add(recipeListObj);
                }
                return listOfRecipes;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer data = new StringBuffer("");
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    data.append(line);
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

        private String loadUserIngredientsJSONFromAsset() {
            String json = null;
            try {
                InputStream is = getAssets().open(User_Ingredients);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
            return json;
        }

        @Override
        protected void onPostExecute(List<RecipeListObject> recipeListObjects) {
            Log.i(DEBUG_TAG, "Finished Loading Recipe Objects");
            mlistOfRecipes = recipeListObjects;
        }
    }

}

/*TODO: Add support for pagination. */
