package course.examples.recipewizard;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Created by Nicolas on 11/16/2015.
 */
public class RecipeListObject {
    private Drawable recipePicture;
    private String recipeLabel;
    private String rating;

    public RecipeListObject() {
        this.recipePicture = null;
        this.recipeLabel = null;
        this.rating = null;
    }

    public RecipeListObject(Drawable recipePicture, String recipeLabel, String rating) {
        this.recipePicture = recipePicture;
        this.recipeLabel = recipeLabel;
        this.rating = rating;
    }

    public Drawable getRecipePicture() {
        return recipePicture;
    }

    public String getRecipeLabel() {
        return recipeLabel;
    }

    public String getRecipeDescription() {
        return rating;
    }

    public void setRecipePicture(Drawable recipePicture) {
        this.recipePicture = recipePicture;
    }

    public void setRecipeLabel(String recipeLabel) {
        this.recipeLabel = recipeLabel;
    }

    public Intent packToIntent(Intent i) {

        if (recipePicture != null) {
            //TODO: pack recipe picture into intent

        }
        i.putExtra("recipeLabel", recipeLabel);
        i.putExtra("recipeRating", rating);
        return i;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
