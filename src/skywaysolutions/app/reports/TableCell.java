package skywaysolutions.app.reports;

/**
 * This class provides a spannable table cell.
 *
 * @author Alfred Manville
 */
public final class TableCell {
    private final int _span;
    private final String _contents;

    /**
     * Constructs a new TableCell with the specified contents spanning 1 column.
     *
     * @param contents The contents of the cell.
     */
    public TableCell(String contents) {
        this(contents, 1);
    }

    /**
     * Constructs a new TableCell with the specified contents
     * spanning the specified number of columns.
     *
     * @param contents The contents of the cell.
     * @param columnSpan The number of columns spanned.
     */
    public TableCell(String contents, int columnSpan) {
        _contents = contents;
        _span = columnSpan;
    }

    /**
     * Gets the number of columns spanned by this cell.
     *
     * @return The number of spanned columns.
     */
    public int getColumnSpan() {
        return _span;
    }

    /**
     * Gets the contents of this cell.
     *
     * @return The contents of this cell.
     */
    public String getContents() {
        return _contents;
    }

    /**
     * Gets a string representation of this cell.
     *
     * @return The cell contents.
     */
    @Override
    public String toString() {
        return _contents;
    }
}
