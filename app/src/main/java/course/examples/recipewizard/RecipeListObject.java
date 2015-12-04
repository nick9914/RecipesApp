package course.examples.recipewizard;

import android.content.Intent;
import android.graphics.Bitmap;


public class RecipeListObject {
    public static final int MY_WIDTH = 180;
    public static final int MY_HEIGHT = 180;
    private Bitmap recipePicture;
    private String recipeLabel;
    private String rating;
    private String pictureURL;

    public RecipeListObject() {
        this.recipePicture = null;
        this.recipeLabel = null;
        this.rating = null;
        this.pictureURL = null;
    }

    public RecipeListObject(Bitmap recipePicture, String recipeLabel, String rating) {
        this.recipePicture = recipePicture;
        this.recipeLabel = recipeLabel;
        this.rating = rating;
    }

    public Bitmap getRecipePicture() {
        return recipePicture;
    }

    public String getRecipeLabel() {
        return recipeLabel;
    }

    public String getRecipeDescription() {
        return rating;
    }

    public void setRecipePicture(Bitmap recipePicture) {
        Bitmap bitmapScaled = Bitmap.createScaledBitmap(recipePicture, MY_WIDTH, MY_HEIGHT, true);
        this.recipePicture = bitmapScaled;
    }

    public void setRecipeLabel(String recipeLabel) {
        this.recipeLabel = recipeLabel;
    }

    public Intent packToIntent(Intent i) {
        i.putExtra("recipePicture", recipePicture);
        i.putExtra("recipeLabel", recipeLabel);

        return i;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setPictureURL(String pictureURL) {
        pictureURL = pictureURL.replace("s90", "s180");
        this.pictureURL = pictureURL;
    }

    public String getPictureURL() {
        return pictureURL;
    }
}
