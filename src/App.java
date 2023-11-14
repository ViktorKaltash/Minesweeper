import Language.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class App {
    public static Language lang;
    public static String[] lang_arr = {"english", "russian"};
    public static String PROP_PATH = "src\\GameRules\\GameRules.properties";
    public static String LANGUAGE;
    public static String CUR_DIF;

    public static void main(String[] args) throws IOException {
        try {
            readProperties();
        } catch (IOException e) {
            System.out.println("File not found!");
            System.exit(-1);
        }
        TitleBoard titleBoard = new TitleBoard();
    }

    private static void readProperties() throws IOException {
        Properties properties = new Properties();
        FileInputStream tmp = new FileInputStream(new File(App.PROP_PATH));
        properties.load(tmp);
        LANGUAGE = properties.getProperty("CURRENT_LANGUAGE");
        switch (LANGUAGE) {
            case "english" -> lang = new English();
            case "russian" -> lang = new Russian();
        }
        CUR_DIF = properties.getProperty("CURRENT_MODE");
        tmp.close();
    }
}
