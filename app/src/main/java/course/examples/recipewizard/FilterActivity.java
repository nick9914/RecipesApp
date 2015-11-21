package course.examples.recipewizard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by Andrew on 11/15/2015.
 */
public class FilterActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);



        // Get a reference to the Press Me Button
        final Button done = (Button) findViewById(R.id.done);
        // Set an OnClickListener on this Button
        // Called each time the user clicks the Button
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // add a query phrase
                // add excluded ingredients
                // add allergies
                // add diets
                // add require pictures
                // add cuisine
                // restrict cuisine
                // add course
                // restrict course
                // add holiday
                // restrict holiday
                // add max total time (seconds)
                // nutrition limits

                Intent intent = new Intent();
                // return selections based on database fields
                //intent.putExtra("field","value");
                setResult(RESULT_OK , intent);
                finish();

            }
        });

    }

    /*
    Supported Allergies
    Dairy, Egg, Gluten, Peanut, Seafood, Sesame, Soy, Sulfite, Tree Nut, Wheat

    Supported Diets
    Lacto vegetarian, Ovo vegetarian, Pescetarian, Vegan, Vegetarian

    Supported Cuisines
    American, Italian, Asian, Mexican, Southern & Soul Food, French, Southwestern, Barbecue,
    Indian, Chinese, Cajun & Creole, English, Mediterranean, Greek, Spanish, German, Thai,
    Moroccan, Irish, Japanese, Cuban, Hawaiin, Swedish, Hungarian, Portugese

    Supported Courses
    Main Dishes, Desserts, Side Dishes, Lunch and Snacks, Appetizers, Salads, Breads,
    Breakfast and Brunch, Soups, Beverages, Condiments and Sauces, Cocktails

    Supported Holidays
    Christmas, Summer, Thanksgiving, New Year, Super Bowl / Game Day, Halloween, Hanukkah, 4th of July


    Supported Nutrition Attributes (ATTR_NAME):

    value       Description 	                Implied Units
    K 	        Potassium, K 	                gram
    NA 	        Sodium, Na 	                    gram
    CHOLE 	    Cholesterol 	                gram
    FATRN 	    Fatty acids, total trans 	    gram
    FASAT 	    Fatty acids, total saturated 	gram
    CHOCDF 	    Carbohydrate, by difference 	gram
    FIBTG 	    Fiber, total dietary 	        gram
    PROCNT 	    Protein 	                    gram
    VITC 	    Vitamin C, total ascorbic acid 	gram
    CA 	        Calcium, Ca 	                gram
    FE 	        Iron, Fe 	                    gram
    SUGAR 	    Sugars, total 	                gram
    ENERC_KCAL 	Energy 	                        kcal
    FAT 	    Total lipid (fat) 	            gram
    VITA_IU 	Vitamin A, IU 	                IU

    */

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

