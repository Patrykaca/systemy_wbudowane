package com.example.systemy_wbudowane;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

public class MainView extends View {
    //Internal Constants
    static final int BASE_ANIMATION_TIME = 100000000;
    private static final String TAG = MainView.class.getSimpleName();
    private static final float MERGING_ACCELERATION = (float) -0.5;
    private static final float INITIAL_VELOCITY = (1 - MERGING_ACCELERATION) / 4;
    public final int numCellTypes = 21;
    private final BitmapDrawable[] bitmapCell = new BitmapDrawable[numCellTypes];
    public final MainGame game;
    //Internal variables
    private final Paint paint = new Paint();
    public boolean hasSaveState = false;
    public boolean continueButtonEnabled = false;
    public int startingX;
    public int startingY;
    public int endingX;
    public int endingY;
    //Icon variables
    public int sYIcons;
    public int sXNewGame;
    public int sXUndo;
    public int iconSize;
    //Misc
    boolean refreshLastTime = true;
    boolean showHelp;
    //Timing
    private long lastFPSTime = System.nanoTime();
    //Text
    private float titleTextSize;
    private float bodyTextSize;
    private float headerTextSize;
    private float instructionsTextSize;
    private float gameOverTextSize;
    //Layout variables
    private int cellSize = 0;
    private float textSize = 0;
    private float cellTextSize = 0;
    private int gridWidth = 0;
    private int textPaddingSize;
    private int iconPaddingSize;
    /*Assets
    private Drawable backgroundRectangle;
    private Drawable lightUpRectangle;
    private Drawable fadeRectangle;
    private Bitmap background = null;
    private BitmapDrawable loseGameOverlay;
    private BitmapDrawable winGameContinueOverlay;
    private BitmapDrawable winGameFinalOverlay;*/
    //Text variables
    private int sYAll;
    private int titleStartYAll;
    private int bodyStartYAll;
    private int eYAll;
    private int titleWidthHighScore;
    private int titleWidthScore;

    public MainView(Context context) {
        super(context);
        Resources resources = context.getResources();
        //Loading resources
        game = new MainGame(context, this);
        try {
            //Getting assets
            /*
            backgroundRectangle = resources.getDrawable(R.drawable.background_rectangle);
            lightUpRectangle = resources.getDrawable(R.drawable.light_up_rectangle);
            fadeRectangle = resources.getDrawable(R.drawable.fade_rectangle);
            this.setBackgroundColor(resources.getColor(R.color.background));
            Typeface font = Typeface.createFromAsset(resources.getAssets(), "ClearSans-Bold.ttf");
            paint.setTypeface(font);
            paint.setAntiAlias(true);
        } catch (Exception e) {
            Log.e(TAG, "Error getting assets?", e);
        }*/
        setOnTouchListener(new InputListener(this));
        game.newGame();
    }
}
