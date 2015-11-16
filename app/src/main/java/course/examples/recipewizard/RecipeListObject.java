package course.examples.recipewizard;

import android.graphics.drawable.Drawable;

/**
 * Created by Nicolas on 11/16/2015.
 */
public class RecipeListObject {
    private Drawable recipePicture;
    private String recipeLabel;
    private String recipeDescription;

    public RecipeListObject(Drawable recipePicture, String recipeLabel, String recipeDescription) {
        this.recipePicture = recipePicture;
        this.recipeLabel = recipeLabel;
        this.recipeDescription = recipeDescription;
    }

    public Drawable getRecipePicture() {
        return recipePicture;
    }

    public String getRecipeLabel() {
        return recipeLabel;
    }

    public String getRecipeDescription() {
        return recipeDescription;
    }

    public void setRecipePicture(Drawable recipePicture) {
        this.recipePicture = recipePicture;
    }

    public void setRecipeLabel(String recipeLabel) {
        this.recipeLabel = recipeLabel;
    }

    public void setRecipeDescription(String recipeDescription) {
        this.recipeDescription = recipeDescription;
    }

}
