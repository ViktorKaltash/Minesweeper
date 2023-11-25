import UI.TextFieldLimit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public class TitleBoard {
    private JFrame frame;
    private JFrame optionsFrame;
    private JFrame customGameFrame;
    private GameBoard gameBoard;
    private HashMap<String, String> diffMap;
    private JButton btnNewGame, btnCustomGame, btnOptions, btnExit;
    private JTextField jColumnsText, jRowsText, jBombText;
    private JLabel jReason;
    private JButton optBTN_Back, optBTN_Save;
    private JComboBox<String> optCMB_difficulty, optCMB_language;
    private JButton cusBTN_Back, cusBTN_Start;
    TitleBoard() throws IOException {
        readProperties();
        prepareTitleFrame();
        prepareCustomFrame();
        prepareOptionFrame();
    }
    private void prepareTitleFrame() {
        frame = new JFrame(App.lang.APP_NAME);
        frame.setSize(260, 200);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setForeground(new Color(155,155,155));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        try {
            readProperties();
        } catch (IOException e) {
            return;
        }

        int rows = 4;
        int columns = 1;
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(rows,columns));
        gridPanel.setForeground(new Color(155,155,155));

        JPanel[][] panelHolder = new JPanel[rows][columns];
        for (int m = 0; m < rows; m++) {
            for (int n = 0; n < columns; n++) {
                panelHolder[m][n] = new JPanel();
            }
        }

        btnNewGame.setMargin(new Insets(5, 8, 5, 8));
        btnNewGame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setVisible(false);
                try {
                    FileInputStream tmp = new FileInputStream(new File(App.PROP_PATH));
                    Properties properties = new Properties();
                    properties.load(tmp);
                    gameBoard = new GameBoard(properties.getProperty("CURRENT_MODE"));
                    Thread th = new Thread() {
                        @Override
                        public void run() {
                            while (gameBoard.isRunning()) {
                                try {
                                    sleep(200);
                                } catch (InterruptedException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                            frame.setVisible(true);
                        }
                    };
                    th.start();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        );

        btnExit.setMargin(new Insets(5, 10, 5, 10));
        btnExit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });

        btnOptions.setMargin(new Insets(5, 10, 5, 10));
        btnOptions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
                optionsFrame.setVisible(true);
            }
        });

        btnCustomGame.setMargin(new Insets(5, 10, 5, 10));
        btnCustomGame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                frame.dispose();
                customGameFrame.setVisible(true);
            }
        }
        );

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                gridPanel.add(panelHolder[i][j]);
            }
        }
        frame.add(gridPanel);

        setButtonsEqualSizeMenu();

        panelHolder[0][0].add(btnNewGame);
        panelHolder[1][0].add(btnCustomGame);
        panelHolder[2][0].add(btnOptions);
        panelHolder[3][0].add(btnExit);
        frame.setVisible(true);
    }
    private void prepareOptionFrame() {
        optionsFrame = new JFrame(App.lang.OPTIONS);
        optionsFrame.setSize(360, 360);
        optionsFrame.setVisible(false);
        optionsFrame.setLocationRelativeTo(null);
        optionsFrame.setResizable(false);
        optionsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        optBTN_Back = new JButton(App.lang.BACK);
        optBTN_Back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                optionsFrame.dispose();
                frame.setVisible(true);
                return;
            }
        });
        optBTN_Save = new JButton(App.lang.SAVE);
        optBTN_Save.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Properties properties = new Properties();
                FileInputStream tmp;
                try {
                    tmp = new FileInputStream(App.PROP_PATH);
                    properties.load(tmp);
                    if (optCMB_difficulty.getSelectedItem() != null) {
                        String difficult = diffMap.get(optCMB_difficulty.getSelectedItem());
                        System.out.println(optCMB_difficulty.getSelectedItem());
                        properties.setProperty("CURRENT_MODE", difficult);
                    }
                    if (optCMB_language.getSelectedItem() != null) {
                        System.out.println(optCMB_language.getSelectedItem());
                        String STmp = properties.getProperty("CURRENT_LANGUAGE");
                        if (!STmp.equals(optCMB_language.getSelectedItem())) {
                            properties.setProperty("CURRENT_LANGUAGE", optCMB_language.getSelectedItem().toString());
                            jReason.setVisible(true);
                        }
                    }
                    tmp.close();
                    FileOutputStream fos = new FileOutputStream(App.PROP_PATH);
                    properties.store(fos, "");
                    fos.close();
                } catch (IOException ex) {
                    return;
                }
            }
        });

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JPanel jOpt_panelCMB = new JPanel(new GridLayout(2,2));
        JPanel[][] tmpPanel = new JPanel[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                tmpPanel[i][j] = new JPanel();
            }
        }

        tmpPanel[0][0].add(new JLabel(App.lang.DIFFICULTY));
        tmpPanel[1][0].add(new JLabel(App.lang.LANGUAGE));
        tmpPanel[0][1].add(optCMB_difficulty);
        tmpPanel[1][1].add(optCMB_language);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                jOpt_panelCMB.add(tmpPanel[i][j]);
            }
        }

        JPanel jReason_panel = new JPanel();
        jReason_panel.add(jReason, BorderLayout.CENTER);

        JPanel jOpt_panel = new JPanel();
        jOpt_panel.add(optBTN_Save, BorderLayout.SOUTH);
        jOpt_panel.add(optBTN_Back, BorderLayout.SOUTH);
        container.add(jOpt_panelCMB);
        container.add(new JPanel());
        container.add(jReason_panel);
        container.add(new JPanel());
        container.add(jOpt_panel, BorderLayout.SOUTH);
        optionsFrame.add(container);
    }
    private void prepareCustomFrame() {
        customGameFrame = new JFrame();
        customGameFrame.setSize(360, 360);
        customGameFrame.setVisible(false);
        customGameFrame.setLocationRelativeTo(null);
        customGameFrame.setResizable(false);
        customGameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cusBTN_Back = new JButton(App.lang.BACK);
        cusBTN_Back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                customGameFrame.dispose();
                frame.setVisible(true);
                return;
            }
        });
        cusBTN_Start = new JButton(App.lang.START_GAME);
        cusBTN_Start.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (jRowsText.getText().equals("") || jColumnsText.getText().equals("") || jBombText.getText().equals("")) {
                    return;
                }
                if (Integer.parseInt(jRowsText.getText()) < 1 || Integer.parseInt(jColumnsText.getText()) < 1)
                    return;

                int bombs = Integer.parseInt(jBombText.getText());
                int tiles = Integer.parseInt(jRowsText.getText()) * Integer.parseInt(jColumnsText.getText());

                if (bombs > tiles) {
                    return;
                }

                int rowsTmp = Integer.parseInt(jRowsText.getText());
                int columnsTmp = Integer.parseInt(jColumnsText.getText());
                int bombTmp = Integer.parseInt(jBombText.getText());
                System.out.println(rowsTmp);
                System.out.println(columnsTmp);
                System.out.println(bombTmp);
                customGameFrame.dispose();
                gameBoard = new GameBoard(rowsTmp, columnsTmp, bombTmp);
                Thread th = new Thread() {
                    @Override
                    public void run() {
                        while (gameBoard.isRunning()) {
                            try {
                                sleep(200);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        frame.setVisible(true);
                    }
                };
                th.start();
            }
        });

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JPanel jCus_panelCMB = new JPanel(new GridLayout(3,3));
        JPanel[][] tmpPanel = new JPanel[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tmpPanel[i][j] = new JPanel();
            }
        }

        jRowsText = new JTextField(4);
        jRowsText.setDocument(new TextFieldLimit(2));
        jColumnsText = new JTextField(4);
        jColumnsText.setDocument(new TextFieldLimit(2));
        jBombText = new JTextField(4);
        jBombText.setDocument(new TextFieldLimit(2));
        tmpPanel[0][0].add(new JLabel(App.lang.ROWS + ": "));
        tmpPanel[1][0].add(new JLabel(App.lang.COLUMNS + ": "));
        tmpPanel[2][0].add(new JLabel(App.lang.BOMBS + ": "));
        tmpPanel[0][1].add(jRowsText);
        tmpPanel[1][1].add(jColumnsText);
        tmpPanel[2][1].add(jBombText);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                jCus_panelCMB.add(tmpPanel[i][j]);
            }
        }

        JPanel jCus_panel = new JPanel();
        jCus_panel.add(cusBTN_Back, BorderLayout.SOUTH);
        jCus_panel.add(cusBTN_Start, BorderLayout.SOUTH);
        container.add(jCus_panelCMB);
        container.add(new JPanel());
        container.add(new JPanel());
        container.add(jCus_panel, BorderLayout.SOUTH);
        customGameFrame.add(container);
    }
    private void readProperties() throws IOException {
        diffMap = new HashMap<>();
        diffMap.put(App.lang.GAME_DIFFICULTY_EASY, "Easy");
        diffMap.put(App.lang.GAME_DIFFICULTY_MEDIUM, "Medium");
        diffMap.put(App.lang.GAME_DIFFICULTY_HARD, "Hard");

        btnNewGame = new JButton(App.lang.NEW_GAME);
        btnCustomGame = new JButton(App.lang.CUSTOM);
        btnOptions = new JButton(App.lang.OPTIONS);
        btnExit = new JButton(App.lang.EXIT);

        jReason = new JLabel(App.lang.REASON);
        jReason.setVisible(false);

        optCMB_difficulty = new JComboBox<>();
        optCMB_difficulty.addItem(App.lang.GAME_DIFFICULTY_EASY);
        optCMB_difficulty.addItem(App.lang.GAME_DIFFICULTY_MEDIUM);
        optCMB_difficulty.addItem(App.lang.GAME_DIFFICULTY_HARD);

        optCMB_language = new JComboBox<>();
        optCMB_language.addItem(App.lang_arr[0]);
        optCMB_language.addItem(App.lang_arr[1]);
    }
    private void setButtonsEqualSizeMenu() {
        int maxWidth = 0;
        int maxHeight = 0;

        maxWidth = getMaxBtnWidthSize(btnNewGame, maxWidth);
        maxWidth = getMaxBtnWidthSize(btnCustomGame, maxWidth);
        maxWidth = getMaxBtnWidthSize(btnOptions, maxWidth);
        maxWidth = getMaxBtnWidthSize(btnExit, maxWidth);

        maxHeight = getMaxBtnHeightSize(btnNewGame, maxHeight);
        maxHeight = getMaxBtnHeightSize(btnCustomGame, maxHeight);
        maxHeight = getMaxBtnHeightSize(btnOptions, maxHeight);
        maxHeight = getMaxBtnHeightSize(btnExit, maxHeight);

        btnNewGame.setPreferredSize(new Dimension(maxWidth, maxHeight));
        btnCustomGame.setPreferredSize(new Dimension(maxWidth, maxHeight));
        btnOptions.setPreferredSize(new Dimension(maxWidth, maxHeight));
        btnExit.setPreferredSize(new Dimension(maxWidth, maxHeight));
    }
    private int getMaxBtnWidthSize(JButton btn, int compareTo) {
        return Math.max(compareTo, btn.getPreferredSize().width);
    }
    private int getMaxBtnHeightSize(JButton btn, int compareTo) {
        return Math.max(compareTo, btn.getPreferredSize().height);
    }
}