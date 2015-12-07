package course.examples.recipewizard;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Arrays;

import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import course.examples.recipewizard.OCR_Helper_Classes.MultipartUtility;


public class IngredientsActivity extends AppCompatActivity {

    ArrayAdapter<String> m_adapter;
    ArrayAdapter<String> m_suggestions;
    ArrayList<String> mUserIngredients = new ArrayList<>();
    ArrayList<String> allIngredientsSearchValues;
    AutoCompleteTextView userInput;
    /*For OCR functionality*/
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int FILTER_ACTIVITY_REQUEST_CODE = 837;

    private Uri fileUri;
    private String mParsedText;
    private List<String> mParsedResults;
    private ProgressBar mProgressBar;
    ArrayList<String> restoreSearchValues;
    ArrayList<String> restoreUserIngredients;
    private String filterstring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        //Load the user input field first so it can be restored if applicable
        userInput = (AutoCompleteTextView) findViewById(R.id.userInput);

        //Get the resources to reload from an orientation change if they exist
        if (savedInstanceState != null) {
            restoreUserIngredients = savedInstanceState.getStringArrayList("userIngredients");
            restoreSearchValues = savedInstanceState.getStringArrayList("searchValues");
            String restoreUserInput = savedInstanceState.getString("userInput");

            if (restoreUserIngredients != null) {
                mUserIngredients = restoreUserIngredients;
            }

            if (restoreUserInput != null) {
                userInput.setText(restoreUserInput);
            }
        }


        if (restoreSearchValues != null) {
            allIngredientsSearchValues = restoreSearchValues;
        } else {
            try {
                allIngredientsSearchValues = readJSON();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //This restore the user input list of ingredients based on a list
        //that has been pushed back into the activity via a string in an intent
        //that has ingredients separated by newline characters
        Intent intent = getIntent();
        String restoreString = intent.getStringExtra("ingredientsList");
        if (restoreString != null) {
            ArrayList<String> previousList = new ArrayList<>(Arrays.asList(restoreString.split("\n")));
            ArrayList<String> restore = new ArrayList<>();
            for (String s : allIngredientsSearchValues) {
                if (!(previousList.contains(s))) {
                    restore.add(s);
                }
            }

            //Restore the previous list if applicable
            if (restoreUserIngredients == null) {
                mUserIngredients = restore;
            }
        }

        //Restore the user input on orientation change if applicable
        if (restoreUserIngredients != null) {
            mUserIngredients = restoreUserIngredients;
        }

        //Load the list view output
        m_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mUserIngredients);
        ListView ingredientList = (ListView) findViewById(R.id.ingredientList);
        ingredientList.setAdapter(m_adapter);
        //Clicking an item sets the user input text to that item
        ingredientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String data = (String) arg0.getItemAtPosition(arg2);
                userInput.setText(data);
            }
        });

        //Set the auto complete suggestions
        m_suggestions = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                allIngredientsSearchValues);
        userInput.setAdapter(m_suggestions);

        //Button to add the user input ingredients into the display of
        //currently added ingredients
        final Button addIngredients = (Button) findViewById(R.id.addIngredients);
        addIngredients.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String input = userInput.getText().toString();
                if (input != null && input.length() > 0) {
                    String[] splitInput = input.split(",");

                    for (String s : splitInput) {
                        addIngredientsHelper(s.trim());
                    }
                }
                userInput.setText("");
            }
        });

        //Button to remove an ingredient from the display list
        Button removeIngredients = (Button) findViewById(R.id.removeIngredients);
        removeIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = userInput.getText().toString();
                if (input != null && input.length() > 0) {
                    String[] splitInput = input.split(",");

                    for (String s : splitInput) {
                        removeIngredientsHelper(s.trim());
                    }
                }
                userInput.setText("");
            }
        });

        //Button to return the ingredients to the calling activity
        Button returnIngredients = (Button) findViewById(R.id.returnIngredients);
        returnIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove all the user input from the ingredients search values
                ArrayList<String> retArrList = new ArrayList<>();
                for (String s : allIngredientsSearchValues) {
                    retArrList.add(s);
                }

                String retStringIncludes = "";
                for (String s : mUserIngredients) {
                    retStringIncludes += s + "\n";
                    retArrList.remove(s);
                }

                //Create the return string;
                String retString = "";
                for (String s : retArrList) {
                    retString += s + "\n";
                }


                //Package the string in an intent and return it
                Intent i = new Intent(IngredientsActivity.this, RecipesActivity.class);
                if(filterstring!=null) {
                    i.putExtra("filter", filterstring);
                }
                i.putExtra("ingredientListIncludes", retStringIncludes);
                i.putExtra("ingredientList", retString);
                setResult(144, i);
                startActivity(i);
            }
        });

        Button takePictureOfReceipt = (Button) findViewById(R.id.pictureOfReceipt);
        takePictureOfReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.progressBarIngredients);
        mProgressBar.setVisibility(View.INVISIBLE);
        mParsedResults = new ArrayList<>();
}

    //Add the individual ingredients into the list and update the list accordingly
    private void addIngredientsHelper(String in) {
        String sanitized_input;
        if (null != in && in.length() > 0) {
            sanitized_input = in.toLowerCase();
            if (mUserIngredients.contains(sanitized_input)) {
                Toast.makeText(getApplicationContext(), "You have already entered this ingredient!", Toast.LENGTH_LONG).show();
            } else if (!(allIngredientsSearchValues.contains(sanitized_input))) {
                Toast.makeText(getApplicationContext(), "That ingredient is not recognized!", Toast.LENGTH_LONG).show();
            } else {
                mUserIngredients.add(sanitized_input);
                m_adapter.notifyDataSetChanged();
            }
        }
    }


    //Remove the individual ingredients from the list and update the list accordingly
    private void removeIngredientsHelper(String in) {
        String sanitized_input;
        if (null != in && in.length() > 0) {
            sanitized_input = in.toLowerCase();
            if (!(mUserIngredients.contains(sanitized_input))) {
                Toast.makeText(getApplicationContext(), "This ingredient is not in the list!", Toast.LENGTH_LONG).show();
            } else {
                mUserIngredients.remove(sanitized_input);
                m_adapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ingredients, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clearList) {
            mUserIngredients.clear();
            m_adapter.notifyDataSetChanged();
        } else if (id == R.id.clearInput) {
            userInput.setText("");
        }else if (id == R.id.filters) {
            Intent intent = new Intent(this, FilterActivity.class);
            this.startActivityForResult(intent, FILTER_ACTIVITY_REQUEST_CODE);
        }

        return super.onOptionsItemSelected(item);
    }


    //The following three methods read the user ingredients JSON file and parse the search values
    public ArrayList<String> readJSON() throws IOException {
        InputStream is = getAssets().open("user_ingredient_file.JSON");
        JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
        try {
            return readIngredientsArray(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            reader.close();
        }

        return null;
    }


    public ArrayList<String> readIngredientsArray(JsonReader reader) throws IOException {
        ArrayList<String> ingredients = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            ingredients.add(readIngredients(reader));
        }
        reader.endArray();
        return ingredients;
    }


    public String readIngredients(JsonReader reader) throws IOException {
        String retValue = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("searchValue")) {
                retValue = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return retValue;
    }

    //Save the state of the app so it can be restore on rotation
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);

        savedState.putStringArrayList("userIngredients", mUserIngredients);
        savedState.putStringArrayList("searchValues", allIngredientsSearchValues);
        savedState.putString("userInput", userInput.getText().toString());
    }

    public void onRestoreInstanceState (Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mUserIngredients = savedInstanceState.getStringArrayList("userIngredients");
        allIngredientsSearchValues = savedInstanceState.getStringArrayList("searchValues");
        userInput.setText(savedInstanceState.getString("userInput"));
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    /*---------------------------------------------------------------------------------*/

    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        /*Context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) */



        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        /*File mediaStorageDir = new File(getApplicationContext().getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");*/
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                new HttpTask().execute();

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }

        if(requestCode==FILTER_ACTIVITY_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK) {
                filterstring = data.getStringExtra("filter");
            }else if (resultCode == RESULT_CANCELED) {
                // User cancelled the filter act
            }
        }


    }

    class HttpTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String charset = "UTF-8";
            String requestURL = "https://ocr.space/api/Parse/Image";

            /*for testing purposes get uri from test picture.*/
            /*if(fileUri == null) {
                fileUri = resIdToUri(getApplicationContext(), R.raw.recipts_grocery);
            }*/


            try {
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addFormField("apikey", "helloworld");
                multipart.addFormField("language", "eng");
                multipart.addFilePart("file", new File(getRealPathFromURI(fileUri)));
                List<String> response = multipart.finish(); // response from server.
                JSONObject jsonObject = new JSONObject(response.get(0));
                JSONArray parsedResultsJsonArray = (JSONArray) jsonObject.get("ParsedResults");
                JSONObject innerJsonObject = parsedResultsJsonArray.getJSONObject(0);
                String parsedText = (String) innerJsonObject.get("ParsedText");

                return parsedText;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "error";
        }
        @Override
        protected void onPostExecute(String s) {
            mParsedText = s;

            //Find matched ingredients.
            for(String ingredient : mParsedText.split("\\s+")) {
                if(allIngredientsSearchValues.contains(ingredient)){
                    mParsedResults.add(ingredient.toLowerCase());
                }
            }
            for(String ingredient : mParsedResults){
                addIngredientsHelper(ingredient);
            }

            /*TODO: Delete Temporary picture*/
            /*getContentResolver().delete(fileUri, null, null);*/

            mProgressBar.setVisibility(View.INVISIBLE);
            String ingredientsRecognized = "" + mParsedResults.size() + " Ingredients Recognized";
            showToast(ingredientsRecognized);
            addRecognizedIngredients(mParsedResults);
        }

    }

    private void addRecognizedIngredients(List<String> recognizedIngredients) {
        for(String ingr : recognizedIngredients) {
            addIngredientsHelper(ingr.trim());
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = this.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String path = cursor.getString(idx);
            cursor.close();
            return path;
        }
    }


    private void showToast(String input) {
        Toast.makeText(getApplicationContext(), input, Toast.LENGTH_SHORT).show();
    }

}
