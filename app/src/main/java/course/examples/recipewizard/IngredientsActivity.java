package course.examples.recipewizard;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import java.util.ArrayList;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


/**
 Look up auto-search completion things
 */

public class IngredientsActivity extends AppCompatActivity {

    ArrayAdapter<String> m_adapter;
    ArrayList<String> mIngredients = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        Button addIngredients = (Button) findViewById(R.id.addIngredients);
        final TextView userInput = (EditText) findViewById(R.id.userInput);
        ListView ingredientList = (ListView) findViewById(R.id.ingredientList);
        m_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mIngredients);
        ingredientList.setAdapter(m_adapter);

        addIngredients.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String input = userInput.getText().toString();
                if (null != input && input.length() > 0) {

                    mIngredients.add(input);

                    m_adapter.notifyDataSetChanged();

                }
            }
        });

        //Button to clear all ingredients in the current text input and
        //display list
        Button clearIngredients = (Button) findViewById(R.id.clearList);
        clearIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIngredients.clear();
                m_adapter.notifyDataSetChanged();

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
