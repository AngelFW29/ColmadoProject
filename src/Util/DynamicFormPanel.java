package Util;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DynamicFormPanel extends JPanel {

    private Map<String, JComponent> fields;

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
            jLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

            JComponent inputComponent;

            if (label.contains("Fecha") || label.contains("Date")) {
                JDateChooser dateChooser = new JDateChooser();
                dateChooser.setDateFormatString("yyyy-MM-dd");
                dateChooser.setDate(new Date());
                dateChooser.setPreferredSize(new Dimension(180, 25));
                inputComponent = dateChooser;
            }
            else if (isEnumLabel(label)) {
                JComboBox<String> comboBox = new JComboBox<>();
                String[] options = getEnumOptions(label);

                for (String option : options) {
                    comboBox.addItem(option);
                }

                comboBox.setPreferredSize(new Dimension(180, 25));
                inputComponent = comboBox;
            }
            else {
                inputComponent = new JTextField(15);
                inputComponent.setPreferredSize(new Dimension(180, 25));
            }

            fields.put(label, inputComponent);

            gbc.gridx = 0;
            add(jLabel, gbc);
            gbc.gridx = 1;
            add(inputComponent, gbc);

            gbc.gridy++;
        }
    }

    private boolean isEnumLabel(String label) {
        return label.equals("Tipo de movimiento") ||
                label.equals("Método Pago") ||
                label.equals("Estado");
    }

    private String[] getEnumOptions(String label) {
        switch (label) {
            case "Tipo de movimiento":
                return new String[]{"Venta", "Compra", "Ajuste", "Perdida"};

            case "Método Pago":
                return new String[]{"Efectivo", "Tarjeta de credito", "Tarjeta de debito"};

            case "Estado":
                return new String[]{"Pendiente", "Recibido", "Cancelado"};

            case "Tipo Persona":
                return new String[]{"Cliente", "Proveedor"};

            default:
                return new String[]{};
        }
    }

    public Map<String, String> getFormData() {
        Map<String, String> data = new HashMap<>();

        for (String key : fields.keySet()) {
            JComponent comp = fields.get(key);
            String value = "";

            if (comp instanceof JTextField) {
                value = ((JTextField) comp).getText();
            }
            else if (comp instanceof JDateChooser) {
                Date date = ((JDateChooser) comp).getDate();
                if (date != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    value = sdf.format(date);
                }
            }
            else if (comp instanceof JComboBox) {
                Object selected = ((JComboBox<?>) comp).getSelectedItem();
                if (selected != null) {
                    value = selected.toString();
                }
            }

            data.put(key, value);
        }
        return data;
    }
}