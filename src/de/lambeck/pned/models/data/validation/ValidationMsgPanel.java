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
public class ValidationMsgPanel extends JPanel implements IValidationMsgPanel {

    private final int ROWS = 0;
    private final int COLUMNS = 30; // Defines the width

    /**
     * This should be the canonical (unique) path name of the file.
     */
    private String modelName = "";

    /**
     * The text area
     */
    JTextArea textArea;

    /**
     * The standard background color (after reset)
     */
    private final Color startBackground = EValidationColor.EMPTY.getColor();

    // /**
    // * The background color for the current validation result
    // */
    // private Color currentBackground = Color.WHITE;

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
     *            name of the PNML file represented by this model.)
     * @param title
     *            The title of this message panel
     */
    @SuppressWarnings("hiding")
    public ValidationMsgPanel(String modelName, String title) {
        this.modelName = modelName;

        textArea = new JTextArea(ROWS, COLUMNS);
        textArea.setBackground(EValidationColor.EMPTY.getColor());
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createEtchedBorder());

        // styledDoc = textPane.getStyledDocument();
        // addStylesToDocument(doc);

        JScrollPane infoScrollPane = new JScrollPane(textArea);
        infoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // infoScrollPane.setBorder(BorderFactory.createEtchedBorder());
        infoScrollPane.setBorder(BorderFactory.createTitledBorder(title));

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
        this.setBackground(startBackground);
    }

    @Override
    public void addMessage(String s) {
        if (!content.equals(""))
            content = content + newline;
        content = content + s;
        this.textArea.setText(content);
    }

    @Override
    public void setBgColor(EValidationColor c) {
        Color color = c.getColor();
        this.textArea.setBackground(color);
        // this.currentBackground = color;
    }

}
