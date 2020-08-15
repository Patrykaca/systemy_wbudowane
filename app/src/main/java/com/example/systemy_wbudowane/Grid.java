package com.example.systemy_wbudowane;

import java.util.ArrayList;

public class Grid {

    public final Tile[][] field;
    public final Tile[][] undoField;
    public final Tile[][] bufferField;

    public Grid(int sizeX, int sizeY) {
        field = new Tile[sizeX][sizeY];
        undoField = new Tile[sizeX][sizeY];
        bufferField = new Tile[sizeX][sizeY];
        clearGrid();
        clearUndoGrid();
    }
///////////

    public Cell randomAvailableCell() {
        ArrayList<Cell> availableCells = getAvailableCells();
        if (availableCells.size() >= 1) {
            return availableCells.get((int) Math.floor(Math.random() * availableCells.size()));
        }
        return null;
    }

    private ArrayList<Cell> getAvailableCells() {
        ArrayList<Cell> availableCells = new ArrayList<>();
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                if (field[i][j] == null) {
                    availableCells.add(new Cell(i, j));
                }
            }
        }
        return availableCells;
    }
    ///////////

    public boolean isCellsAvailable() {
        return (getAvailableCells().size() >= 1);
    }

    public boolean isCellAvailable(Cell cell) {
        return !isCellOccupied(cell);
    }

    public boolean isCellOccupied(Cell cell) {
        return (getCellContent(cell) != null);
    }

    public Tile getCellContent(Cell cell) {
        if (cell != null && isCellWithinBounds(cell)) {
            return field[cell.getX()][cell.getY()];
        } else {
            return null;
        }
    }

    public Tile getCellContent(int x, int y) {
        if (isCellWithinBounds(x, y)) {
            return field[x][y];
        } else {
            return null;
        }
    }

    public boolean isCellWithinBounds(Cell cell) {
        return 0 <= cell.getX() && cell.getX() < field.length
                && 0 <= cell.getY() && cell.getY() < field[0].length;
    }


    public boolean isCellWithinBounds(int x, int y) {
        return 0 <= x && x < field.length
                && 0 <= y && y < field[0].length;
    }

    //////////

    public void insertTile(Tile tile) {
        field[tile.getX()][tile.getY()] = tile;
    }

    public void removeTile(Tile tile) {
        field[tile.getX()][tile.getY()] = null;
    }

    public void saveTiles() {
        for (int i = 0; i < bufferField.length; i++) {
            for (int j = 0; j < bufferField[0].length; j++) {
                if (bufferField[i][j] == null) {
                    undoField[i][j] = null;
                } else {
                    undoField[i][j] = new Tile(i, j, bufferField[i][j].getValue());
                }
            }
        }
    }

    public void prepareSaveTiles() {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                if (field[i][j] == null) {
                    bufferField[i][j] = null;
                } else {
                    bufferField[i][j] = new Tile(i, j, field[i][j].getValue());
                }
            }
        }
    }

    //////////

    public void revertTiles() {
        for (int i = 0; i < undoField.length; i++) {
            for (int j = 0; j < undoField[0].length; j++) {
                if (undoField[i][j] == null) {
                    field[i][j] = null;
                } else {
                    field[i][j] = new Tile(i, j, undoField[i][j].getValue());
                }
            }
        }
    }

    public void clearGrid() {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                field[i][j] = null;
            }
        }
    }

    private void clearUndoGrid() {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                undoField[i][j] = null;
            }
        }
    }

}
