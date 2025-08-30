package Presentation;

import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Arrays;

/**
 * The TableUtils class provides utility methods for creating table models
 * compatible with Swing's JTable. It allows for generating a DefaultTableModel
 * dynamically based on the fields of objects within a given list.
 *
 * @author Bud Vasile-Stefan
 * @version 1.0
 */
public class TableUtils {
    /**
     * Creates a DefaultTableModel dynamically based on the fields of objects
     * in the provided list. Each object's fields are used as columns, and their
     * values as row data in the table model. If the list is null or empty,
     * an empty DefaultTableModel is returned.
     *
     * @param <T> the type of objects in the list
     * @param objects a list of objects to be used for generating the table model;
     *                each object's fields are used as column names and row values
     * @return a DefaultTableModel representing the data in the provided list,
     *         or an empty model if the list is null or empty
     */
    public static <T> DefaultTableModel createTableModel(List<T> objects) {
        if (objects == null || objects.isEmpty()) {
            return new DefaultTableModel();
        }

        Field[] fields = objects.get(0).getClass().getDeclaredFields();
        String[] columnNames = Arrays.stream(fields)
                .peek(field -> field.setAccessible(true))
                .map(Field::getName)
                .toArray(String[]::new);

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        objects.stream()
                .map(obj -> Arrays.stream(fields)
                        .map(field -> {
                            try {
                                return field.get(obj);
                            } catch (IllegalAccessException e) {
                                return "N/A";
                            }
                        })
                        .toArray())
                .forEach(model::addRow);

        return model;
    }
}