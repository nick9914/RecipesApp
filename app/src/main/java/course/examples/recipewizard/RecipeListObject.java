package course.examples.recipewizard;

import android.content.Intent;
import android.graphics.Bitmap;


public class RecipeListObject {
    public static final int MY_WIDTH = 180;
    public static final int MY_HEIGHT = 180;
    private Bitmap recipePicture;
    private String recipeTitle;
    private String id;
    private String pictureURL;
    private Integer usedIngredientCount;
    private Integer missedIngredientCount;
    private Integer likes;

    public RecipeListObject() {
        this.recipePicture = null;
        this.recipeTitle = null;
        this.id = null;
        this.pictureURL = null;
        this.usedIngredientCount = 0;
        this.missedIngredientCount = 0;
        this.likes = 0;
    }

    public RecipeListObject(Bitmap recipePicture, String recipeTitle, String id) {
        this.recipePicture = recipePicture;
        this.recipeTitle = recipeTitle;
        this.id = id;
    }

    public Bitmap getRecipePicture() {
        return recipePicture;
    }

    public String getRecipeLabel() {
        return recipeTitle;
    }

    public String getRecipeId() {
        return id;
    }

    public void setRecipePicture(Bitmap recipePicture) {
        Bitmap bitmapScaled = Bitmap.createScaledBitmap(recipePicture, MY_WIDTH, MY_HEIGHT, true);
        this.recipePicture = bitmapScaled;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
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
        this.pictureURL = pictureURL;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setUsedIngredientCount(Integer usedIngredientCount) {
        this.usedIngredientCount = usedIngredientCount;
    }

    public void setMissedIngredientCount(Integer missedIngredientCount) {
        this.missedIngredientCount = missedIngredientCount;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }
}
