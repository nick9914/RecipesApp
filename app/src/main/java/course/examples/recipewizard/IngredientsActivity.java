package course.examples.recipewizard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import android.view.View;
import android.widget.ListView;
import android.widget.Toast;


public class IngredientsActivity extends AppCompatActivity {

    ArrayAdapter<String> m_adapter;
    ArrayAdapter<String> m_suggestions;
    ArrayList<String> mUserIngredients = new ArrayList<>();
    ArrayList<String> allIngredientsSearchValues;
    AutoCompleteTextView userInput;
    ArrayList<String> restoreSearchValues;
    ArrayList<String> restoreUserIngredients;

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


        //Restore the search value list or load it from scratch
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

        //Set the auto complete suggestions
        m_suggestions = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
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
                ArrayList<String> retArrList = allIngredientsSearchValues;
                for (String s : mUserIngredients) {
                    retArrList.remove(s);
                }

                //Create the return string;
                String retString = "";
                for (String s : retArrList) {
                    retString += s + "\n";
                }

                //Package the string in an intent and return it
                Intent i = new Intent();
                i.putExtra("ingredientList", retString);
                setResult(144, i);
                finish();
            }
        });

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
}
