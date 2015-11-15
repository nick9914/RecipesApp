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
        final Button button = (Button) findViewById(R.id.button);
        // Set an OnClickListener on this Button
        // Called each time the user clicks the Button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start activity: Find Recipes
                Intent intent = new Intent(MainActivity.this, RecipesActivity.class);
                startActivity(intent);



            }
        });
        // Get a reference to the Press Me Button
        final Button button2 = (Button) findViewById(R.id.button2);
        // Set an OnClickListener on this Button
        // Called each time the user clicks the Button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start activity: Add New Recipe
                Intent intent = new Intent(MainActivity.this, AddRecipeActivity.class);
                startActivity(intent);


            }
        });
        // Get a reference to the Press Me Button
        final Button button3 = (Button) findViewById(R.id.button3);
        // Set an OnClickListener on this Button
        // Called each time the user clicks the Button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start activity: Add Ingredients
                Intent intent = new Intent(MainActivity.this, IngredientsActivity.class);
                startActivity(intent);


            }
        });
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
