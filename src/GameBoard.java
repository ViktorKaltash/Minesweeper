import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class GameBoard {
    private Tile[][] board;
    private int numRows = 4;
    private int numCols = numRows;
    private int BOMB_COUNT;
    private int TILES_TO_WIN;
    private String APP_NAME;
    private int ROWS;
    private int COLUMNS;
    private int TILE_SIZE;

    private int tilesRevealed;
    private JFrame frame = new JFrame(App.lang.APP_NAME);
    private JLabel textLabel = new JLabel();
    private JPanel textPanel = new JPanel();
    private JPanel tilePanel = new JPanel();
    boolean gameOver = false;
    private boolean isRunning;

    //Non-Custom
    public GameBoard(String mode) throws IOException {
        try {
            readProperties(mode);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(-1);
        }
        isRunning = true;
        createClearBoard();
        setMines();
        createFrame();
        tilesRevealed = 0;
    }
    //Custom
    public GameBoard(int Rows, int Columns, int Bombs) {
        try {
            readProperties();
        } catch (IOException e) {
            System.out.println(e);
            System.exit(-1);
        }
        isRunning = true;
        ROWS = Rows;
        COLUMNS = Columns;
        BOMB_COUNT = Bombs;
        TILES_TO_WIN = ROWS*COLUMNS - BOMB_COUNT;
        createClearBoard();
        setMines();
        createFrame();
        tilesRevealed = 0;
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void readProperties(String mode) throws IOException {
        Properties properties = new Properties();
        FileInputStream fis;

        properties.load(new FileInputStream(new File(App.PROP_PATH)));
        String tmpAreaSize = "BOMB_AREA_SIZE_" + mode.toUpperCase();
        String tmpBombCount = "BOMB_COUNTS_" + mode.toUpperCase();

        ROWS = Integer.parseInt(properties.getProperty(tmpAreaSize));
        COLUMNS = Integer.parseInt(properties.getProperty(tmpAreaSize));
        BOMB_COUNT = Integer.parseInt(properties.getProperty(tmpBombCount));
        APP_NAME = properties.getProperty(App.lang.APP_NAME);
        TILE_SIZE = Integer.parseInt(properties.getProperty("TILE_SIZE"));
        TILES_TO_WIN = ROWS*COLUMNS - BOMB_COUNT;
    }

    private void readProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(App.PROP_PATH)));
        APP_NAME = properties.getProperty("APP_NAME");
        TILE_SIZE = Integer.parseInt(properties.getProperty("TILE_SIZE"));
    }

    private void createClearBoard() {
        board = new Tile[ROWS][COLUMNS];

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                board[i][j] = new Tile(i, j, false);
                board[i][j].setFocusable(false);
                board[i][j].setMargin(new Insets(0, 0, 0, 0));
                board[i][j].setFont(new Font("Arial Unicode MS", Font.PLAIN, 32));
                board[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }
                        Tile tile = (Tile) e.getSource();
                        //left click
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText() == "") {
                                if (tile.isExplosive()) {
                                    gameOver();
                                    return;
                                }
                                checkNearby(tile.getCoordinates().getX(),tile.getCoordinates().getY());
                            }
                        }
                        //right click
                        else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getText() == "" && tile.isEnabled()) {
                                tile.setText("F");
                            }
                            else if (tile.getText() == "F") {
                                tile.setText("");
                            }
                        }
                    }
                });
            }
        }
    }

    public void createFrame() {
        int boardHeight = ROWS * TILE_SIZE;
        int boardWidth = COLUMNS * TILE_SIZE;

        if (boardHeight < 128) {
            boardHeight = 128;
        }
        if (boardWidth < 128) {
            boardWidth = 128;
        }

        frame.setSize(boardWidth, boardHeight+100);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JButton jButton = new JButton(App.lang.BACK);
        jButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isRunning = false;
                frame.setVisible(false);
            }
        });
        textLabel.setFont(new Font("Arial", Font.BOLD, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText(App.lang.BOMBS + ": " + Integer.toString(BOMB_COUNT));
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);
        frame.add(jButton, BorderLayout.NORTH);
        tilePanel.setLayout(new GridLayout(ROWS, COLUMNS));

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                tilePanel.add(board[i][j]);
            }
        }

        frame.add(jButton, BorderLayout.SOUTH);
        frame.add(tilePanel, BorderLayout.CENTER);
        frame.add(textLabel, BorderLayout.NORTH);

        frame.setVisible(true);
    }
    public void setMines() {
        int minesLeft = BOMB_COUNT;
        Random random = new Random();

        while (minesLeft > 0) {
            int r = random.nextInt(ROWS); //0-7
            int c = random.nextInt(COLUMNS);

            if (!board[r][c].isExplosive()) {
                board[r][c].setMine();
                minesLeft -= 1;
            }
        }
    }
    public void revealAllMines() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j].reveal();
            }
        }
    }
    public void gameOver() {
        gameOver = true;
        revealAllMines();
        textLabel.setText("You lost!");
        textLabel.setBackground(Color.red);
    }
    public int checkMine(int x, int y) {
        if ((x < 0 || x >= ROWS) || (y < 0 || y >= COLUMNS)) {
            return 0;
        }
        if (board[x][y].isExplosive()) {
            return 1;
        }
        return 0;
    }
    public void checkNearby(int x, int y) {
        if ((x < 0 || x >= ROWS) || (y < 0 || y >= COLUMNS)) {
            return;
        }
        if (board[x][y].isChecked()) {
            return;
        }

        int minesFound = 0;

        //Check TOP
        minesFound += checkMine(x-1, y-1);
        minesFound += checkMine(x, y-1);
        minesFound += checkMine(x+1, y-1);

        //Check Left and Right
        minesFound += checkMine(x-1, y);
        minesFound += checkMine(x+1, y);

        //Check Bottom
        minesFound += checkMine(x-1, y+1);
        minesFound += checkMine(x, y+1);
        minesFound += checkMine(x+1, y+1);

        if (minesFound > 0) {
            Color tmp = setNumberColor(minesFound);
            board[x][y].setText(Integer.toString(minesFound));
            board[x][y].setUI(new MetalButtonUI() {
                protected Color getDisabledTextColor() {
                    return tmp;
                }
            });
            board[x][y].setEnabled(false);
            board[x][y].setChecked();
            tilesRevealed++;
            checkWin();
            return;
        } else {
            board[x][y].setText("");
            tilesRevealed++;
            board[x][y].setEnabled(false);
            board[x][y].setChecked();

            checkNearby(x - 1, y - 1);
            checkNearby(x - 1, y + 1);
            checkNearby(x, y - 1);
            //LEFT and RIGHT
            checkNearby(x-1,y);
            checkNearby(x+1,y);
            //BOTTOM
            checkNearby(x,y+1);
            checkNearby(x+1,y+1);
            checkNearby(x + 1, y - 1);
            checkWin();
        }
    }
    public Color setNumberColor(int number) {
        switch (number) {
            case 1: return Color.blue;
            case 2: return Color.green;
            case 3: return Color.red;
            case 4: return new Color(80,0,130);
            case 5: return new Color(100, 0,0);
            case 6: return new Color(255,0,150);
            case 7: return new Color(255,213,0);
        }
        return Color.BLUE;
    }
    public void checkWin() {
        if (TILES_TO_WIN == tilesRevealed) {
            revealAllMines();
            textLabel.setText("You win!");
            textLabel.setBackground(Color.green);
        }
    }
}