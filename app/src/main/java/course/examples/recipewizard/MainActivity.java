package course.examples.recipewizard;
//  Authors : Andrew Cleary,
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*TEST1*/
        /*some change */
        ArrayList<Integer> test = new ArrayList<>();

        // Get a reference to the Press Me Button
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
        // Get a reference to the Press Me Button
        final Button addNewRecipe = (Button) findViewById(R.id.AddNewRecipe);
        // Set an OnClickListener on this Button
        // Called each time the user clicks the Button
        addNewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start activity: Add New Recipe
                Intent intent = new Intent(MainActivity.this, AddRecipeActivity.class);
                MainActivity.this.startActivity(intent);


            }
        });
        // Get a reference to the Press Me Button
        final Button addIngredients = (Button) findViewById(R.id.AddIngredients);
        // Set an OnClickListener on this Button
        // Called each time the user clicks the Button
        addIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start activity: Add Ingredients
                Intent intent = new Intent(MainActivity.this, IngredientsActivity.class);
                MainActivity.this.startActivityForResult(intent,0);


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
