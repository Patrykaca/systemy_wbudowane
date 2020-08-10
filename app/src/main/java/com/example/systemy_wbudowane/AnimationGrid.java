package com.example.systemy_wbudowane;

import java.util.ArrayList;

public class AnimationGrid {

    public final ArrayList<AnimationCell> globalAnimation = new ArrayList<>();
    private final ArrayList<AnimationCell>[][] field;
    private int activeAnimations = 0;
    private boolean oneMoreFrame = false;

    public AnimationGrid(int x, int y) {
        field = new ArrayList[x][y];

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                field[i][j] = new ArrayList<>();
            }
        }
    }

    public void startAnimation(int x, int y, int animationType, long length, long delay, int[] extras) {
        AnimationCell animationToAdd = new AnimationCell(x, y, animationType, length, delay, extras);
        if (x == -1 && y == -1) {
            globalAnimation.add(animationToAdd);
        } else {
            field[x][y].add(animationToAdd);
        }
        activeAnimations = activeAnimations + 1;
    }
}
