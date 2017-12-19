package de.lambeck.pned.models.data.validation;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Implements a text area for validation messages. It should show the results of
 * the validation for the current Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
@SuppressWarnings("serial")
public class ValidationMessagesPanel extends JPanel implements IValidationMessagesPanel {

    private final int ROWS = 0;
    private final int COLUMNS = 20; // Defines the width

    /**
     * This should be the canonical (unique) path name of the file.
     */
    private String modelName = "";

    /**
     * This should be the name of the tab. (file name only)
     */
    private String displayName = "";

    /**
     * The text area
     */
    JTextArea textArea;

    /**
     * The standard background color (after reset)
     */
    private final Color standardBackground = Color.WHITE;

    /**
     * The background color for the current validation result
     */
    private Color currentBackground = Color.WHITE;

    /**
     * The text content of the text area
     */
    // private List<String> content = new ArrayList<String>();
    private String content = "";

    // StyledDocument styledDoc;

    String newline = "\n";

    /**
     * Constructs the validation message area for the specified file.
     * 
     * @param modelName
     *            The name of the model (This is intended to be the full path
     *            name of the pnml file represented by this model.)
     * @param displayName
     *            The name of the tab (the file name only)
     */
    @SuppressWarnings("hiding")
    public ValidationMessagesPanel(String modelName, String displayName) {
        this.modelName = modelName;
        this.displayName = displayName;

        textArea = new JTextArea(ROWS, COLUMNS);
        textArea.setBackground(ValidationColor.EMPTY.getColor());
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createEtchedBorder());

        // styledDoc = textPane.getStyledDocument();
        // addStylesToDocument(doc);

        JScrollPane infoScrollPane = new JScrollPane(textArea);
        infoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // infoScrollPane.setBorder(BorderFactory.createEtchedBorder());
        infoScrollPane.setBorder(BorderFactory.createTitledBorder("Validator"));

        /*
         * Add the components.
         */
        setLayout(new BorderLayout());
        add(infoScrollPane, BorderLayout.CENTER);
    }

    /*
     * Setter and Getter
     */

    @Override
    public String getModelName() {
        return this.modelName;
    }

    @Override
    public void setModelName(String s) {
        this.modelName = s;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(String s) {
        this.displayName = s;
    }

    // protected void addStylesToDocument(StyledDocument doc) {
    // // Initialize some styles.
    // Style def =
    // StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
    //
    // Style regular = doc.addStyle("regular", def);
    // StyleConstants.setFontFamily(def, "SansSerif");
    //
    // Style s = doc.addStyle("italic", regular);
    // StyleConstants.setItalic(s, true);
    //
    // s = doc.addStyle("bold", regular);
    // StyleConstants.setBold(s, true);
    //
    // s = doc.addStyle("small", regular);
    // StyleConstants.setFontSize(s, 10);
    //
    // s = doc.addStyle("large", regular);
    // StyleConstants.setFontSize(s, 16);
    // }

    @Override
    public void reset() {
        // this.content.clear();
        this.content = "";
        this.setBackground(standardBackground);
    }

    @Override
    public void addMessage(String s) {
        if (!content.equals(""))
            content = content + newline;
        content = content + s;
        this.textArea.setText(content);
    }

    @Override
    public void setBgColor(ValidationColor c) {
        Color color = c.getColor();
        this.textArea.setBackground(color);
        this.currentBackground = color;
    }

}
