package course.examples.recipewizard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Andrew on 11/15/2015.
 *
 * Start this activity for result to display a ui allowing the choosing of filters
 * it will return the intent with the string extra named "filter", it is the query string to append to the
 * end of the search query
 */
public class FilterActivity extends Activity {

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

    String[] allergies = { "Dairy", "Egg", "Gluten", "Peanut", "Seafood", "Sesame", "Soy", "Sulfite",
            "Tree Nut", "Wheat", "" };

    String[] allergiesquery = {"396^Dairy-Free","397^Egg-Free","393^Gluten-Free","394^Peanut-Free",
            "398^Seafood-Free","399^Sesame-Free","400^Soy-Free","401^Sulfite-Free",
            "395^Tree Nut-Free","392^Wheat-Free"};

    String[] diets = { "Lacto vegetarian", "Ovo vegetarian", "Pescetarian", "Vegan", "Vegetarian", "" };

    String[] dietsquery = {"388^Lacto vegetarian",
            "389^Ovo vegetarian",
            "390^Pescetarian",
            "386^Vegan",
            "387^Lacto-ovo",
            "403^Paleo"};


    String[] cuisines = {"American", "Kid Friendly" ,"Italian", "Asian", "Mexican", "Southern & Soul Food", "French",
            "Southwestern", "Barbecue", "Indian", "Chinese", "Cajun & Creole", "English",
            "Mediterranean", "Greek", "Spanish", "German", "Thai", "Moroccan", "Irish", "Japanese",
            "Cuban", "Hawaiin", "Swedish", "Hungarian", "Portugese", ""};

    String[] cuisinesquery = {"cuisine^cuisine-american","cuisine^cuisine-kid-friendly",
            "cuisine^cuisine-italian","cuisine^cuisine-asian","cuisine^cuisine-mexican",
            "cuisine^cuisine-southern","cuisine^cuisine-french","cuisine^cuisine-southwestern",
            "cuisine^cuisine-barbecue-bbq","cuisine^cuisine-indian","cuisine^cuisine-chinese",
            "cuisine^cuisine-cajun","cuisine^cuisine-english","cuisine^cuisine-mediterranean",
            "cuisine^cuisine-greek","cuisine^cuisine-spanish","cuisine^cuisine-german",
            "cuisine^cuisine-thai","cuisine^cuisine-moroccan","cuisine^cuisine-irish",
            "cuisine^cuisine-japanese","cuisine^cuisine-cuban","cuisine^cuisine-hawaiian",
            "cuisine^cuisine-swedish","cuisine^cuisine-hungarian","cuisine^cuisine-portuguese"};


    String[] courses = { "Main Dishes", "Desserts", "Side Dishes", "Lunch and Snacks", "Appetizers",
            "Salads", "Breads", "Breakfast and Brunch", "Soups", "Beverages",
            "Condiments and Sauces", "Cocktails", ""};

    String[] coursesquery = { "course^course-Main Dishes"
            ,"course^course-Desserts"
            ,"course^course-Side Dishes"
            ,"course^course-Lunch and Snacks"
            ,"course^course-Appetizers"
            ,"course^course-Salads"
            ,"course^course-Breads"
            ,"course^course-Breakfast and Brunch"
            ,"course^course-Soups"
            ,"course^course-Beverages"
            ,"course^course-Condiments and Sauces"
            ,"course^course-Cocktails"};

    String[] holidays = { "Christmas", "Summer", "Thanksgiving", "Fall", "New Year", "Super Bowl / Game Day",
            "Winter", "Spring", "Halloween", "Hanukkah","Valentine's Day","Passover" ,"Easter","St. Patrick's Day",
            "Chinese New Year", "4th of July", ""};

    String[] holidaysquery = { "holiday^holiday-christmas",
            "holiday^holiday-summer",
            "holiday^holiday-thanksgiving",
            "holiday^holiday-fall",
            "holiday^holiday-new-year",
            "holiday^holiday-super-bowl",
            "holiday^holiday-winter",
            "holiday^holiday-spring",
            "holiday^holiday-halloween",
            "holiday^holiday-hanukkah",
            "holiday^holiday-valentines-day",
            "holiday^holiday-passover",
            "holiday^holiday-easter",
            "holiday^holiday-st-patricks-day",
            "holiday^holiday-chinese-new-year",
            "holiday^holiday-4th-of-july"};

    HashMap<String,String> allergiesMap;
    HashMap<String,String> dietsMap;
    HashMap<String,String> cuisinesMap;
    HashMap<String,String> coursesMap;
    HashMap<String,String> holidaysMap;

    //later
    String[] nutrition = {};


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        //map user fields to api calls
        setMaps();

        //get EditText query
        final EditText querytext = (EditText) findViewById(R.id.title);


        //Allergy spinner
        //
        //
        // Get a reference to the Spinner
        final Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        final int  size1 = allergies.length - 1;

        final HashSet<String> hs1 = new HashSet();


        final TextView tv1 = (TextView) findViewById(R.id.selectedspinner1);

        //Button to clear selection
        final Button button1 = (Button) findViewById(R.id.spinnerbutton1);
        // Set an OnClickListener on this Button
        // Called each time the user clicks the Button
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear set and text
                tv1.setText("Selected:");
                hs1.clear();
                spinner1.setSelection(size1);
            }
        });

        ArrayAdapter<CharSequence> adapter1 =
                new ArrayAdapter<CharSequence>(this,R.layout.filter_list_item, allergies ){
                    @Override
                    public int getCount() {
                        return(size1); // Truncate the list
                    }
                };

        // Set the Adapter for the spinner
        spinner1.setAdapter(adapter1);
        spinner1.setSelection(size1); // Hidden item to appear in the spinner
        // Set an setOnItemSelectedListener on the spinner
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if(pos != size1) {
                    String s = parent.getItemAtPosition(pos).toString();
                    if(hs1.size()<5) {
                        if (hs1.add(s)) {
                            tv1.append(" " + s);
                        }
                    }else{
                        makeToast("Too many selected");
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });









        //diets spinner
        //
        //
        // Get a reference to the Spinner
        final Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        final int  size2 = diets.length - 1;

        final HashSet<String> hs2 = new HashSet();


        final TextView tv2 = (TextView) findViewById(R.id.selectedspinner2);

        //Button to clear selection
        final Button button2 = (Button) findViewById(R.id.spinnerbutton2);
        // Set an OnClickListener on this Button
        // Called each time the user clicks the Button
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear set and text
                tv2.setText("Selected:");
                hs2.clear();
                spinner2.setSelection(size2);
            }
        });

        ArrayAdapter<CharSequence> adapter2 =
                new ArrayAdapter<CharSequence>(this,R.layout.filter_list_item, diets ){
                    @Override
                    public int getCount() {
                        return(size2); // Truncate the list
                    }
                };

        // Set the Adapter for the spinner
        spinner2.setAdapter(adapter2);
        spinner2.setSelection(size2); // Hidden item to appear in the spinner
        // Set an setOnItemSelectedListener on the spinner
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if(pos != size2) {
                    String s = parent.getItemAtPosition(pos).toString();
                    if(hs2.size()<2) {
                        if (hs2.add(s)) {
                            tv2.append(" " + s);
                        }
                    }else{
                        makeToast("Too many selected");
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        //cuisines spinner
        //
        //
        // Get a reference to the Spinner
        final Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);
        final int  size3 = cuisines.length - 1;

        final HashSet<String> hs3 = new HashSet();


        final TextView tv3 = (TextView) findViewById(R.id.selectedspinner3);

        //Button to clear selection
        final Button button3 = (Button) findViewById(R.id.spinnerbutton3);
        // Set an OnClickListener on this Button
        // Called each time the user clicks the Button
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear set and text
                tv3.setText("Selected:");
                hs3.clear();
                spinner3.setSelection(size3);
            }
        });

        ArrayAdapter<CharSequence> adapter3 =
                new ArrayAdapter<CharSequence>(this,R.layout.filter_list_item, cuisines ){
                    @Override
                    public int getCount() {
                        return(size3); // Truncate the list
                    }
                };

        // Set the Adapter for the spinner
        spinner3.setAdapter(adapter3);
        spinner3.setSelection(size3); // Hidden item to appear in the spinner
        // Set an setOnItemSelectedListener on the spinner
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if(pos != size3) {
                    String s = parent.getItemAtPosition(pos).toString();
                    if(hs3.size()<5) {
                        if (hs3.add(s)) {
                            tv3.append(" " + s);
                        }
                    }else{
                        makeToast("Too many selected");
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //courses spinner
        //
        //
        // Get a reference to the Spinner
        final Spinner spinner4 = (Spinner) findViewById(R.id.spinner4);
        final int  size4 = courses.length - 1;

        final HashSet<String> hs4 = new HashSet();


        final TextView tv4 = (TextView) findViewById(R.id.selectedspinner4);

        //Button to clear selection
        final Button button4 = (Button) findViewById(R.id.spinnerbutton4);
        // Set an OnClickListener on this Button
        // Called each time the user clicks the Button
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear set and text
                tv4.setText("Selected:");
                hs4.clear();
                spinner4.setSelection(size4);
            }
        });

        ArrayAdapter<CharSequence> adapter4 =
                new ArrayAdapter<CharSequence>(this,R.layout.filter_list_item, courses ){
                    @Override
                    public int getCount() {
                        return(size4); // Truncate the list
                    }
                };

        // Set the Adapter for the spinner
        spinner4.setAdapter(adapter4);
        spinner4.setSelection(size4); // Hidden item to appear in the spinner
        // Set an setOnItemSelectedListener on the spinner
        spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if(pos != size4) {
                    String s = parent.getItemAtPosition(pos).toString();
                    if(hs4.size()<4) {
                        if (hs4.add(s)) {
                            tv4.append(" " + s);
                        }
                    }else{
                        makeToast("Too many selected");
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        //holidays spinner
        //
        //
        // Get a reference to the Spinner
        final Spinner spinner5 = (Spinner) findViewById(R.id.spinner5);
        final int  size5 = holidays.length - 1;

        final HashSet<String> hs5 = new HashSet();


        final TextView tv5 = (TextView) findViewById(R.id.selectedspinner5);

        //Button to clear selection
        final Button button5 = (Button) findViewById(R.id.spinnerbutton5);
        // Set an OnClickListener on this Button
        // Called each time the user clicks the Button
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear set and text
                tv5.setText("Selected:");
                hs5.clear();
                spinner5.setSelection(size5);
            }
        });

        ArrayAdapter<CharSequence> adapter5 =
                new ArrayAdapter<CharSequence>(this,R.layout.filter_list_item, holidays ){
                    @Override
                    public int getCount() {
                        return(size5); // Truncate the list
                    }
                };

        // Set the Adapter for the spinner
        spinner5.setAdapter(adapter5);
        spinner5.setSelection(size5); // Hidden item to appear in the spinner
        // Set an setOnItemSelectedListener on the spinner
        spinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if(pos != size5) {
                    String s = parent.getItemAtPosition(pos).toString();
                    if(hs5.size()<3) {
                        if (hs5.add(s)) {
                            tv5.append(" " + s);
                        }
                    }else{
                        makeToast("Too many selected");
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




        // Get a reference to the submitButton Button
        final Button done = (Button) findViewById(R.id.submitButton);
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

                // make query string to return
                StringBuilder str = new StringBuilder();

                String temp = querytext.getText().toString();
                if (!temp.isEmpty()) {
                    temp = temp.trim();
                    String query = temp.replaceAll("\\s+", "+");
                    str.append("&q=" + query);
                }

                for(String s : hs1) {
                    str.append("&allowedAllergy[]=");
                    str.append(allergiesMap.get(s));
                }
                for(String s : hs2) {
                    str.append("&allowedDiet[]=");
                    str.append(dietsMap.get(s));
                }
                for(String s : hs3) {
                    str.append("&allowedCuisine[]=");
                    str.append(cuisinesMap.get(s));
                }
                for(String s : hs4) {
                    str.append("&allowedCourse[]=");
                    str.append(coursesMap.get(s));
                }
                for(String s : hs5) {
                    str.append("&allowedHoliday[]=");
                    str.append(holidaysMap.get(s));
                }

                Log.d("Test Return from Filter", str.toString());

                //make intent to return
                Intent intent = new Intent();
                // return selections based on database fields
                intent.putExtra("filter",str.toString());
                setResult(RESULT_OK , intent);
                finish();

            }
        });

    }

    private void setMaps() {
        allergiesMap = new HashMap<String,String>();
        for(int i = 0 ; i < allergiesquery.length ; i ++){
            allergiesMap.put(allergies[i],allergiesquery[i]);
        }
        dietsMap = new HashMap<String,String>();
        for(int i = 0 ; i < dietsquery.length ; i ++){
            dietsMap.put(diets[i],dietsquery[i]);
        }
        cuisinesMap = new HashMap<String,String>();
        for(int i = 0 ; i < cuisinesquery.length ; i ++){
            cuisinesMap.put(cuisines[i],cuisinesquery[i]);
        }
        coursesMap = new HashMap<String,String>();
        for(int i = 0 ; i < coursesquery.length ; i ++){
            coursesMap.put(courses[i],coursesquery[i]);
        }
        holidaysMap = new HashMap<String,String>();
        for(int i = 0 ; i < holidaysquery.length ; i ++){
            holidaysMap.put(holidays[i],holidaysquery[i]);
        }
    }

    private void makeToast(String s) {
        Context context = getApplicationContext();
        CharSequence text = s;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
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

