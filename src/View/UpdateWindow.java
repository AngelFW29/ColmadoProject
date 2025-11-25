package View;

import Util.DynamicFormPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UpdateWindow extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel inputPanel;

    private DynamicFormPanel formPanel;

    public UpdateWindow(String[] fields) {
        setContentPane(contentPane);
        setModal(true);
        setTitle("Actualizar Registro");

        if (!(inputPanel.getLayout() instanceof BorderLayout)) {
            inputPanel.setLayout(new BorderLayout());
        }

        formPanel = new DynamicFormPanel(fields);
        inputPanel.add(formPanel, BorderLayout.CENTER);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setSize(500, 400);
        setLocationRelativeTo(null);
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
