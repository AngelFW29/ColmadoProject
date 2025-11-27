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

    /**
     * Método Genérico Maestro para cargar tablas.
     *
     * @param <T>            El tipo de objeto (Product, InventoryLog, etc.)
     * @param dataList       La lista de datos del controlador.
     * @param columns        Nombres de las columnas de la tabla.
     * @param rowMapper      Función que convierte un objeto T en una fila Object[].
     * @param entityName     Nombre de la entidad (ej: "Producto").
     * @param formLabels     Etiquetas para los formularios Agregar/Editar.
     * @param editMapper     Función que extrae de la fila visual (Object[]) los valores para el formulario de edición (String[]).
     * @param deleteAction   Función que ejecuta el borrado en el controlador (recibe ID).
     * @param reloadCallback Acción para recargar la vista.
     */
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
                e -> {
                    if (editMapper != null) {
                        int row = e.getID();
                        int id = (int) data[row][0];
                        String[] currentValues = editMapper.apply(data[row]);

                        context.openUpdateDialog(entityName, formLabels, currentValues, id, reloadCallback);
                    }
                },
                // Acción ELIMINAR
                e -> {
                    if (deleteAction != null) {
                        int row = e.getID();
                        int id = (int) data[row][0];
                        String itemName = entityName + " #" + id;
                        // Adaptador para BooleanSupplier
                        BooleanSupplier action = () -> deleteAction.apply(id);
                        context.confirmAndDelete(itemName, action, reloadCallback);
                    }
                }
        );

        // 3. Mostrar Tabla
        container.setViewportView(table.getTable());

        // 4. Configurar Botón Agregar (Estándar)
        configureAddButton(entityName, formLabels, reloadCallback);
    }

    /**
     * Sobrecarga para cuando el botón Agregar tiene una lógica especial (como Ventas).
     */
    public <T> void loadTableWithCustomAdd(
            List<T> dataList,
            String[] columns,
            Function<T, Object[]> rowMapper,
            ActionListener customAddAction,
            Function<Integer, Boolean> deleteAction, // Solo eliminar, sin editar
            Runnable reloadCallback
    ) {
        // 1. Matriz
        Object[][] data = new Object[dataList.size()][columns.length];
        for (int i = 0; i < dataList.size(); i++) data[i] = rowMapper.apply(dataList.get(i));

        // 2. Tabla
        CustomTableGenerator table = new CustomTableGenerator(
                columns, data,
                null, // Sin editar
                e -> { // Eliminar
                    int row = e.getID();
                    int id = (int) data[row][0];
                    context.confirmAndDelete("Registro #" + id, () -> deleteAction.apply(id), reloadCallback);
                }
        );
        container.setViewportView(table.getTable());

        // 3. Botón Custom
        for (ActionListener l : btnAdd.getActionListeners()) btnAdd.removeActionListener(l);
        btnAdd.addActionListener(customAddAction);
        btnAdd.setEnabled(true); // Asegurar que esté activo
    }

    // Auxiliar para configurar el botón Add estándar
    private void configureAddButton(String title, String[] labels, Runnable reloadCallback) {
        // Limpiamos listeners viejos
        for (ActionListener l : btnAdd.getActionListeners()) btnAdd.removeActionListener(l);

        // Usamos el método público de MainWindow
        context.openAddWindow(btnAdd, title, labels, reloadCallback);
    }
}