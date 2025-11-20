package View;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DynamicFormPanel extends JPanel {

    private Map<String, JTextField> fields;

    public DynamicFormPanel(String[] labels) {
        this.fields = new HashMap<>();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        for (String label : labels) {
            JLabel jLabel = new JLabel(label);
            JTextField jTextField = new JTextField(15);
            fields.put(label, jTextField);

            gbc.gridx = 0;
            add(jLabel, gbc);
            gbc.gridx = 1;
            add(jTextField, gbc);

            gbc.gridy++;
        }
    }

    public Map<String, String> getFormData() {
        Map<String, String> data = new HashMap<>();
        for (String key : fields.keySet()) {
            data.put(key, fields.get(key).getText());
        }
        return data;
    }
}