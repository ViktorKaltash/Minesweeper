import javax.swing.*;

public class Tile extends JButton {
    private boolean isExplosive;
    private boolean isChecked;
    private final Coordinates coordinates;

    public class Coordinates {
        private int x;
        private int y;

        public Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    Tile(int x, int y, boolean isExplosive) {
        this.isExplosive = isExplosive;
        isChecked = false;
        coordinates = new Coordinates(x, y);
    }
    public boolean isExplosive() {
        return isExplosive;
    }
    public void checkMinesNearby(GameBoard gameBoard) {
        //TODO Check up-left-down-right
    }

    public void reveal() {
        if (!isExplosive) {
            return;
        }
        this.setText("B");
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setMine() {
        isExplosive = true;
    }

    public void setInvisible() {
        this.setVisible(false);
    }

    public void setChecked() {
        isChecked = true;
    }

    public boolean isChecked() {
        return isChecked;
    }
}