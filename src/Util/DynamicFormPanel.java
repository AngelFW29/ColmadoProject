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

            // Detección de Fechas
            if (label.contains("Fecha") || label.contains("Date")) {
                JDateChooser dateChooser = new JDateChooser();
                dateChooser.setDateFormatString("yyyy-MM-dd");
                dateChooser.setDate(new Date());
                dateChooser.setPreferredSize(new Dimension(180, 25));
                inputComponent = dateChooser;
            }
            // Detección de Enums (ComboBox)
            else if (isEnumLabel(label)) {
                JComboBox<String> comboBox = new JComboBox<>();
                String[] options = getEnumOptions(label);

                for (String option : options) {
                    comboBox.addItem(option);
                }

                comboBox.setPreferredSize(new Dimension(180, 25));
                inputComponent = comboBox;
            }
            // Texto Normal
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
                label.equals("Estado") ||
                label.equals("Tipo Persona");
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

    public void setFormValues(String[] labels, String[] values) {
        if (labels == null || values == null || labels.length != values.length) {
            return;
        }

        for (int i = 0; i < labels.length; i++) {
            String key = labels[i];
            String value = values[i];

            JComponent comp = fields.get(key);

            if (comp != null) {
                if (comp instanceof JTextField) {
                    ((JTextField) comp).setText(value);
                }
                else if (comp instanceof JComboBox) {
                    ((JComboBox<?>) comp).setSelectedItem(value);
                }
                else if (comp instanceof JDateChooser) {
                    try {
                        if (value != null && !value.isEmpty() && !value.equals("N/A")) {
                            java.sql.Date date = java.sql.Date.valueOf(value);
                            ((JDateChooser) comp).setDate(date);
                        }
                    } catch (Exception e) {
                        System.err.println("Error al parsear fecha en setFormValues: " + value);
                    }
                }
            }
        }
    }
}