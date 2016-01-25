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
    private GridView mGridview;
    private List<RecipeListObject> mlistOfRecipesWithoutPics;
    private List<RecipeListObject> mlistOfRecipes;
    private RecipeListAdapter mAdapter;
    private ProgressBar mProgressBar;
    private String mIngredientList;
    private String mFilterString;
    private boolean mIngredientListProvided;
    private String mIngredientListIncludes;

    /*Pagination Variables*/
    private static final Integer MAX_PAGINATION_RESULTS = 50;
    private boolean loadMoreResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Initialize global variables and listners*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipes_grid_view_layout);
        mGridview = (GridView) findViewById(R.id.gridview);
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

        loadMoreResults = true;



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
                Log.d("Debug", "The included ingredients is/are: " + mIngredientListIncludes);

            }
        }

        /*Get Recipes from Yummly API*/
        new HttpGetRecipesTask().execute();


    }


    public void customLoadMoreDataFromApi(int totalItemsCount) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
        if(totalItemsCount < MAX_PAGINATION_RESULTS && loadMoreResults) {
            new HttpGetRecipesTask().execute();
        } else {
            showToast("No more Results");
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
        private final String API_KEY = getString(R.string.spoonacular_key);


        private final String API_ENDPOINT = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/findByIngredients?";

        private static final String User_Ingredients = "user_ingredient_file.JSON";

        private static final int NUMBER_OF_RESULTS = 16;

        private static final int RANKING = 2;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<RecipeListObject> doInBackground(Void... params) {

            StringBuilder getCall = new StringBuilder(API_ENDPOINT);

            /*Construct GET URL*/

            getCall.append(appendIngredientsToGetRequest(mIngredientListProvided, mIngredientListIncludes));
            getCall.append("&limitLicense=" + "false");
            getCall.append("&number=" + paginationNumber());
            getCall.append("&ranking=" + RANKING);

            try {
                return downloadUrl(getCall.toString());

            } catch (IOException e) {
                Log.i(DEBUG_TAG, "GET call Error");
                e.printStackTrace();
                return null;
            }
        }

        private int paginationNumber() {
            return mlistOfRecipes.size() + NUMBER_OF_RESULTS;

        }

        private String appendIngredientsToGetRequest(boolean ingredientListProvided, String ingredientListIncludes) {
            StringBuilder ingredients = new StringBuilder();
            ingredients.append("ingredients=");
            if(!ingredientListProvided) {
                /*TODO: Handel No Ingredients Provided*/
                //No ingredients, thus no includes
                return "";
            } else {
                for(String ingredient : ingredientListIncludes.split("\\r?\\n")) {
                    ingredients.append(ingredient.replaceAll(" ", "%20"));
                    ingredients.append("%2C");
                }
                return ingredients.toString();
            }
        }

        private List<RecipeListObject> downloadUrl(String myurl) throws IOException {
            /*http://developer.android.com/training/basics/network-ops/connecting.html#connection*/
            InputStream is = null;
            String contentAsString;
            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("X-Mashape-Key", API_KEY);
                conn.setRequestProperty("Accept", "application/json");
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
                JSONArray matchesArray = new JSONArray(contentAsString);
                /*Check if more results are found (pagination support)*/
                if(mlistOfRecipes.size() == matchesArray.length()) {
                    //Empty List
                    loadMoreResults = false;
                    return listOfRecipes;
                }
                for (int i = mlistOfRecipes.size() == 0 ? 0 : mlistOfRecipes.size() - 1; i < matchesArray.length(); i++) {
                    JSONObject JSONRecipeObject = matchesArray.getJSONObject(i);
                    RecipeListObject recipeListObj = new RecipeListObject();

                    recipeListObj.setRecipeId(JSONRecipeObject.getString("id"));
                    recipeListObj.setRecipeTitle(JSONRecipeObject.getString("title"));
                    recipeListObj.setPictureURL(JSONRecipeObject.getString("image"));
                    recipeListObj.setUsedIngredientCount(Integer.valueOf(JSONRecipeObject.getString("usedIngredientCount")));
                    recipeListObj.setMissedIngredientCount(Integer.valueOf(JSONRecipeObject.getString("missedIngredientCount")));
                    recipeListObj.setLikes(Integer.valueOf(JSONRecipeObject.getString("likes")));

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

        @Override
        protected void onPostExecute(List<RecipeListObject> recipeListObjects) {
            if(recipeListObjects.size() == 0) {
                showToast("No more results");

            }
            mlistOfRecipesWithoutPics = recipeListObjects;
            getRecipeImages();

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
            for(RecipeListObject recipeObj : mlistOfRecipesWithoutPics) {
                recipeObj.setRecipePicture(LoadImageFromWebOperations(recipeObj.getPictureURL()));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mlistOfRecipes.addAll(mlistOfRecipesWithoutPics);
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

    public void showToast(String toast) {
        Toast.makeText(this.getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
    }

}
