package Util;

import View.MainWindow;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class ViewManager {

    private final MainWindow context;
    private final JScrollPane container;
    private final JButton btnAdd;

    public ViewManager(MainWindow context, JScrollPane container, JButton btnAdd) {
        this.context = context;
        this.container = container;
        this.btnAdd = btnAdd;
    }

    public <T> void loadTable(
            List<T> dataList,
            String[] columns,
            Function<T, Object[]> rowMapper,
            String entityName,
            String[] formLabels,
            Function<Object[], String[]> editMapper,
            Function<Integer, Boolean> deleteAction,
            Runnable reloadCallback
    ) {
        Object[][] data = new Object[dataList.size()][columns.length];
        for (int i = 0; i < dataList.size(); i++) {
            data[i] = rowMapper.apply(dataList.get(i));
        }

        CustomTableGenerator table = new CustomTableGenerator(
                columns, data,
                null,
                e -> {
                    if (editMapper != null) {
                        int row = e.getID();
                        int id = (int) data[row][0];
                        String[] currentValues = editMapper.apply(data[row]);

                        context.openUpdateDialog(entityName, formLabels, currentValues, id, reloadCallback);
                    }
                },
                e -> {
                    if (deleteAction != null) {
                        int row = e.getID();
                        int id = (int) data[row][0];
                        String itemName = entityName + " #" + id;
                        BooleanSupplier action = () -> deleteAction.apply(id);
                        context.confirmAndDelete(itemName, action, reloadCallback);
                    }
                }
        );

        container.setViewportView(table.getTable());

        configureAddButton(entityName, formLabels, reloadCallback);
    }

    public <T> void loadTableWithCustomActions(
            List<T> dataList,
            String[] columns,
            Function<T, Object[]> rowMapper,
            ActionListener customAddAction,
            ActionListener viewAction,
            Function<Integer, Boolean> deleteAction,
            Runnable reloadCallback
    ) {
        Object[][] data = new Object[dataList.size()][columns.length];
        for (int i = 0; i < dataList.size(); i++) data[i] = rowMapper.apply(dataList.get(i));

        CustomTableGenerator table = new CustomTableGenerator(
                columns, data,
                viewAction,
                null,
                e -> {
                    int row = e.getID();
                    int id = (int) data[row][0];
                    context.confirmAndDelete("Registro #" + id, () -> deleteAction.apply(id), reloadCallback);
                }
        );
        container.setViewportView(table.getTable());

        for (ActionListener l : btnAdd.getActionListeners()) btnAdd.removeActionListener(l);
        if (customAddAction != null) {
            btnAdd.addActionListener(customAddAction);
            btnAdd.setEnabled(true);
        }
    }

    private void configureAddButton(String title, String[] labels, Runnable reloadCallback) {
        for (ActionListener l : btnAdd.getActionListeners()) btnAdd.removeActionListener(l);

        context.openAddWindow(btnAdd, title, labels, reloadCallback);
    }
}