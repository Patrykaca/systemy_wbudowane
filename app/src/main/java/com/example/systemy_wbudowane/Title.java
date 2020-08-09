package com.example.systemy_wbudowane;

public class Title extends Cell {

    private final int value;
    private Title[] mergedForm = null;

    public Title(int x, int y, int value) {
        super(x, y);
        this.value = value;
    }

    public Title(Cell cell, int value) {
        super(cell.getX(),cell.getY());
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public Title[] getMergedForm() {
        return mergedForm;
    }

    public void setMergedForm(Title[] mergedForm) {
        this.mergedForm = mergedForm;
    }
}
