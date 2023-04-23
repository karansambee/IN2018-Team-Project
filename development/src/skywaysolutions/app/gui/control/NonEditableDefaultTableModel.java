package skywaysolutions.app.gui.control;

import javax.swing.table.DefaultTableModel;

/**
 * This class provides a JTableModel that is not editable.
 */
public class NonEditableDefaultTableModel extends DefaultTableModel {

    /**
     * Constructs a new instance of NonEditableDefaultTableModel with the specified column names and row count.
     *
     * @param columnNames The array of column names.
     * @param rowCount The number of rows.
     */
    public NonEditableDefaultTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
