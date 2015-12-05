package course.examples.recipewizard;
/** Authors :
 *  Andrew Cleary
 *  Chris Lawrence
 *  Daniel Lins
 *  Jose Valdez
 *  Zhengyang Tang
 **/
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends Activity {

    ArrayList<String> ingredientsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Button to go into the findRecipes activity
        final Button findRecipes = (Button) findViewById(R.id.FindRecipes);
        // Set an OnClickListener on this Button
        // Called each time the user clicks the Button
        findRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start activity: Find Recipes
                Intent intent = new Intent(MainActivity.this, RecipesActivity.class);
                MainActivity.this.startActivity(intent);



            }
        });

        //Button to go into the addNewRecipe activity
        final Button addNewRecipe = (Button) findViewById(R.id.AddNewRecipe);
        // Set an OnClickListener on this Button
        // Called each time the user clicks the Button
        addNewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start activity: Add New Recipe
                Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                MainActivity.this.startActivity(intent);


            }
        });

        //Button to launch the ingredients activity
        final Button addIngredients = (Button) findViewById(R.id.AddIngredients);
        // Set an OnClickListener on this Button
        // Called each time the user clicks the Button
        addIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start activity: Add Ingredients
                Intent intent = new Intent(MainActivity.this, IngredientsActivity.class);

                //This will load all of the ingredients back into a string
                //with the ingredients separated by newline characters then put
                //it back into the ingredients activity so the list can be re-edited
                //if you go back into it
                if (ingredientsList != null) {
                    String sendString = "";
                    for (String s : ingredientsList) {
                        sendString += s + "\n";
                    }
                    intent.putExtra("ingredientsList", sendString);
                }

                //Start the fun!
                startActivityForResult(intent, 144);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }

        //This is the return from the Ingredients activity
        if (requestCode == 144) {
            if (resultCode == 144) {
                String ingredients = data.getStringExtra("ingredientList");
                ingredientsList = new ArrayList<>(Arrays.asList(ingredients.split("\n")));
                //Log.i("FOO", new Integer(ingredientsList.size()).toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
