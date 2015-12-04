package course.examples.recipewizard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Nicolas on 11/16/2015.
 */
public class RecipeListAdapter extends ArrayAdapter<RecipeListObject> {
    private final Context context;
    private final List<RecipeListObject> values;

    public RecipeListAdapter(Context context, List<RecipeListObject> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.recipe_grid_view_cell_layout, parent, false);
        }
        TextView recipeLabel = (TextView) convertView.findViewById(R.id.recipeLabel);
        ImageView recipePicture = (ImageView) convertView.findViewById(R.id.recipePicture);

        recipePicture.setImageBitmap(values.get(position).getRecipePicture());
        recipeLabel.setText(values.get(position).getRecipeLabel());


        return convertView;

    }
}
