package course.examples.recipewizard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
    private static final Integer MAX_PAGINATION_RESULTS = 50;
    private Integer paginationFrom;
    private GridView mGridview;
    private Integer totalMatchCount;
    private List<RecipeListObject> mPaginationListOfRecipes;
    private List<RecipeListObject> mlistOfRecipes;
    private RecipeListAdapter mAdapter;
    private ProgressBar mProgressBar;
    private String mIngredientList;
    private String mFilterString;
    private boolean mIngredientListProvided;
    private String mIngredientListIncludes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Initialize global variables and listners*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipes_grid_view_layout);
        paginationFrom = 0;
        mGridview = (GridView) findViewById(R.id.gridview);
        mPaginationListOfRecipes = new ArrayList<>();
        mlistOfRecipes = new ArrayList<>();
        mAdapter = new RecipeListAdapter(this, mlistOfRecipes);
        mGridview.setAdapter(mAdapter);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mIngredientListProvided = false;
        mIngredientList = null;
        mFilterString = null;

        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RecipesActivity.this, SingleRecipeActivity.class);
                RecipeListObject recipe = (RecipeListObject) parent.getItemAtPosition(position);
                recipe.packToIntent(intent);
                startActivity(intent);

            }
        });

        mGridview.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
                customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        /*Check Network Connection*/
        if (!checkNetworkConnection()) {
            Toast.makeText(RecipesActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
            finish();
        }
        /*Check if statrted from Ingredients Activity*/
        Intent intent = getIntent();
        if(intent != null) {
            if (intent.hasExtra("filter")) {
                mFilterString = intent.getStringExtra("filter");
            }
            if (intent.hasExtra("ingredientList")) {
                mIngredientListProvided = true;
                mIngredientList = intent.getStringExtra("ingredientList");
                mIngredientListIncludes = intent.getStringExtra("ingredientListIncludes");
            }
        }

        /*Get Recipes from Yummly API*/
        new HttpGetRecipesTask().execute();


    }

    public void customLoadMoreDataFromApi(int totalItemsCount) {
        if(totalItemsCount < MAX_PAGINATION_RESULTS  && totalItemsCount < totalMatchCount) {
            new HttpGetRecipesTask().execute();
        }
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
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
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        private String buildURLExcludes(boolean ingredientListProvided, String ingredientList) {
            StringBuilder excludes = new StringBuilder();

            if(!ingredientListProvided) {
                try {

                /*Get ingredients for user_ingredient_file.JSON*/
                    JSONArray userIngredients = new JSONArray(loadUserIngredientsJSONFromAsset());
                    for (int i = 0; i < userIngredients.length(); i++) {
                        JSONObject ingredient = userIngredients.getJSONObject(i);
                        String searchIngredientName = ingredient.getString("searchValue");
                        excludes.append("&excludedIngredient[]=");
                        //Encode value
                        excludes.append(searchIngredientName.replaceAll(" ", "%20"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                for(String ingredient : ingredientList.split("\\r?\\n")) {
                    excludes.append("&excludedIngredient[]=");
                    excludes.append(ingredient.replaceAll(" ", "%20"));
                }

            }
            return excludes.toString();
        }

        private String buildURLIncludes(boolean ingredientListProvided, String ingredientListIncludes) {
            StringBuilder includes = new StringBuilder();
            if(!ingredientListProvided) {
                //No ingredients, thus no includes
                return "";
            } else {
                for(String ingredient : ingredientListIncludes.split("\\r?\\n")) {
                    includes.append("&allowedIngredient[]=");
                    includes.append(ingredient.replaceAll(" ", "%20"));
                }
                return includes.toString();
            }
        }

        @Override
        protected List<RecipeListObject> doInBackground(Void... params) {
            /*TODO: Remove Try Catch block. Not need anymore*/
            try {
                /*Construct GET URL*/
                StringBuilder urlWithParameters = new StringBuilder(BASE_URL);

                urlWithParameters.append(buildURLExcludes(mIngredientListProvided, mIngredientList));
                urlWithParameters.append(buildURLIncludes(mIngredientListProvided, mIngredientListIncludes));
                urlWithParameters.append("&start=" + paginationFrom);
                urlWithParameters.append("&maxResult=" + MAX_RESULT);
                urlWithParameters.append("&requirePictures=true");

                //Log.i(DEBUG_TAG, "appending filter string to query: " + mFilterString);
                // added for filter activity
                if(mFilterString!=null) {

                    Log.i(DEBUG_TAG, "appending filter string to query: " + mFilterString);
                    urlWithParameters.append(mFilterString);
                }

                return downloadUrl(urlWithParameters.toString());

            } catch (IOException e) {
                Log.i(DEBUG_TAG, "Could not load user_ingredient_file.JSON");
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                Log.i(DEBUG_TAG, "Unable to retrieve web page. URL may be invalid.");
                e.printStackTrace();
                return null;

            }
        }

        @Override
        protected void onPostExecute(List<RecipeListObject> recipeListObjects) {
            Log.i(DEBUG_TAG, "Finished Loading Recipe Objects");
            if(!mPaginationListOfRecipes.isEmpty()) {
                mPaginationListOfRecipes.clear();
            }
            //test if statement because of crash with nullpointer exception recipeListObjects
            if(recipeListObjects!= null) {
                mPaginationListOfRecipes.addAll(recipeListObjects);
                paginationFrom += MAX_RESULT;
                /*Get images for recipe objects*/
                getRecipeImages();
            }

        }

        private List<RecipeListObject> downloadUrl(String myurl) throws IOException {
            /*http://developer.android.com/training/basics/network-ops/connecting.html#connection*/
            InputStream is = null;
            String contentAsString;
            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(DEBUG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                contentAsString = readStream(is);

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
            return fromJSONToRecipeListObjects(contentAsString);
        }

        private List<RecipeListObject> fromJSONToRecipeListObjects(String contentAsString) {
            List<RecipeListObject> listOfRecipes = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(contentAsString);
                if(totalMatchCount == null){
                    totalMatchCount = jsonObject.getInt("totalMatchCount");
                }
                JSONArray matchesArray = (JSONArray) jsonObject.get("matches");
                for (int i = 0; i < matchesArray.length(); i++) {
                    JSONObject JSONRecipeObject = matchesArray.getJSONObject(i);
                    RecipeListObject recipeListObj = new RecipeListObject();
                    recipeListObj.setRecipeId(JSONRecipeObject.getString("id"));
                    recipeListObj.setRecipeLabel(JSONRecipeObject.getString("recipeName"));
                    JSONObject imageUrlJsonObj = JSONRecipeObject.getJSONObject("imageUrlsBySize");
                    recipeListObj.setPictureURL(imageUrlJsonObj.getString("90"));
                    listOfRecipes.add(recipeListObj);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return listOfRecipes;
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
    }

    private void getRecipeImages() {
        new HttpGetImages().execute();
    }

    private class HttpGetImages extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            for(RecipeListObject recipeObj : mPaginationListOfRecipes) {
                recipeObj.setRecipePicture(LoadImageFromWebOperations(recipeObj.getPictureURL()));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mlistOfRecipes.addAll(mPaginationListOfRecipes);
            mAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        private Bitmap LoadImageFromWebOperations(String url) {
            try {
                Bitmap x;
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();

                x = BitmapFactory.decodeStream(input);

                return x;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

    }

}
