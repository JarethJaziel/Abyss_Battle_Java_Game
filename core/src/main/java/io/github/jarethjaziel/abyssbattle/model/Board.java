package io.github.jarethjaziel.abyssbattle.model;

public class Board {

    private final int width;
    private final int height;
    private Entity[][] grid;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Entity[width][height];
    }

    public boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public void placeEntity(Entity e) {
        int x = (int) e.getX();
        int y = (int) e.getY();

        if (isInside(x, y)) {
            grid[x][y] = e;
        }
    }

    public Entity getEntityAt(int x, int y) {
        if (!isInside(x, y)) return null;
        return grid[x][y];
    }

    public void removeEntity(int x, int y) {
        if (isInside(x, y)) {
            grid[x][y] = null;
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
