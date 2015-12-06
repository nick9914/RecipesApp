package course.examples.recipewizard;

import android.content.Intent;
import android.graphics.Bitmap;


public class RecipeListObject {
    public static final int MY_WIDTH = 180;
    public static final int MY_HEIGHT = 180;
    private Bitmap recipePicture;
    private String recipeLabel;
    private String id;
    private String pictureURL;

    public RecipeListObject() {
        this.recipePicture = null;
        this.recipeLabel = null;
        this.id = null;
        this.pictureURL = null;
    }

    public RecipeListObject(Bitmap recipePicture, String recipeLabel, String id) {
        this.recipePicture = recipePicture;
        this.recipeLabel = recipeLabel;
        this.id = id;
    }

    public Bitmap getRecipePicture() {
        return recipePicture;
    }

    public String getRecipeLabel() {
        return recipeLabel;
    }

    public String getRecipeId() {
        return id;
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
        i.putExtra("recipeId", id);

        return i;
    }

    public void setRecipeId(String id) {
        this.id = id;
    }

    public void setPictureURL(String pictureURL) {
        pictureURL = pictureURL.replace("s90", "s180");
        this.pictureURL = pictureURL;
    }

    public String getPictureURL() {
        return pictureURL;
    }
}
