package com.example.systemy_wbudowane;

public class Tile extends Cell {

    private final int value;
    private Tile[] mergedForm = null;

    public Tile(int x, int y, int value) {
        super(x, y);
        this.value = value;
    }

    public Tile(Cell cell, int value) {
        super(cell.getX(),cell.getY());
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public Tile[] getMergedForm() {
        return mergedForm;
    }

    public void setMergedForm(Tile[] mergedForm) {
        this.mergedForm = mergedForm;
    }
}
