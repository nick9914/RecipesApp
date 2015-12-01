package course.examples.recipewizard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class IngredientsActivity extends AppCompatActivity {

    ArrayAdapter<String> m_adapter;
    ArrayList<String> mUserIngredients = new ArrayList<>();
    ArrayList<String> allIngredientsSearchValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        //Load the user input and list view output
        final TextView userInput = (EditText) findViewById(R.id.userInput);
        ListView ingredientList = (ListView) findViewById(R.id.ingredientList);
        m_adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, mUserIngredients);
        ingredientList.setAdapter(m_adapter);

        //Load the User Ingredients file
        try {
            allIngredientsSearchValues = readJSON();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Button to add the user input ingredients into the display of
        //currently added ingredients
        Button addIngredients = (Button) findViewById(R.id.addIngredients);
        addIngredients.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String input = userInput.getText().toString();
                if (null != input && input.length() > 0) {
                    if (mUserIngredients.contains(input)) {
                        Toast.makeText(getApplicationContext(), "You have already entered this ingredient!", Toast.LENGTH_LONG).show();
                    } else {
                        //TODO: Logic here to split user input on commas or spaces if they add multiple ones
                        mUserIngredients.add(input);
                        m_adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        //Button to remove an ingredient from the display list
        Button removeIngredients = (Button) findViewById(R.id.removeIngredients);
        removeIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = userInput.getText().toString();
                mUserIngredients.remove(input);
                m_adapter.notifyDataSetChanged();
            }
        });

        Button returnIngredients = (Button) findViewById(R.id.returnIngredients);
        returnIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                IngredientsParcel ip = new IngredientsParcel();
                ip.setmData(mUserIngredients);

                Bundle retBundle = new Bundle();
                retBundle.putParcelable("claw", ip);

                Intent i = new Intent();
                i.putExtras(retBundle);

                setResult(144, i);
                finish();
                */

                //TODO
                //This is extremely inefficient and just here to have something
                //for others to work with. Will need to be revisisted.
                String retString = "";
                for (String s : mUserIngredients) {
                    retString += s + "\n";
                }
                Intent i = new Intent();
                i.putExtra("ingredientList", retString);
                setResult(144, i);
                finish();
            }
        });

        //TODO -- test buttons -- remove when done
        Button testButton = (Button) findViewById(R.id.test);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String s : allIngredientsSearchValues) {
                    mUserIngredients.add(s);
                    m_adapter.notifyDataSetChanged();
                }
            }
        });
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
        } else if (id == R.id.saveList) {
            //// TODO: 12/1/15  
        } else if (id == R.id.loadList) {
            //// TODO: 12/1/15
        }

        return super.onOptionsItemSelected(item);
    }

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

}
