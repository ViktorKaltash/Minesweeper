package UI;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class TextFieldLimit extends PlainDocument {
    int limit;

    public TextFieldLimit(int limit) {
        super();
        this.limit = limit;
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (getText().length() > index) {
//                    String tmp;
//                    try {
//                        tmp = getText(0, index);
//                    } catch (BadLocationException ex) {
//                        throw new RuntimeException(ex);
//                    }
//                    removeAll();
//                    setText(tmp);
//                }
//                String tmp = getText();
//                setText(tmp.replaceAll("[^\\d]", ""));
//            }
    }

    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null)
            return;

        if (!str.matches("[1-9]")) {
            str = str.replaceAll("[a-zA-Z]+", "");
        }

        if ((getLength() + str.length()) <= limit) {
            super.insertString(offset, str, attr);
        }
    }

}
