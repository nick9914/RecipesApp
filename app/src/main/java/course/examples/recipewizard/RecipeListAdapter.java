package course.examples.recipewizard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nicolas on 11/16/2015.
 */
public class RecipeListAdapter extends ArrayAdapter<RecipeListObject> {
    private final Context context;
    private final ArrayList<RecipeListObject> values;

    public RecipeListAdapter(Context context,ArrayList<RecipeListObject> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.recipe_list_view_layout, parent, false);
        ImageView recipePicture =(ImageView) rowView.findViewById(R.id.recipePicture);
        TextView recipeLabel = (TextView) rowView.findViewById(R.id.recipeLabel);
        TextView recipeDescription = (TextView) rowView.findViewById(R.id.recipeDescription);

        recipePicture.setImageDrawable(values.get(position).getRecipePicture());
        recipeLabel.setText(values.get(position).getRecipeLabel());
        recipeDescription.setText(values.get(position).getRecipeDescription());

        return rowView;

    }
}
