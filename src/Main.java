import skywaysolutions.app.database.DB_Connector;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.utils.CheckedException;

/**
 * Main class of the system.
 *
 * @author Alfred Manville
 */
public class Main {
    /**
     * Main entry point.
     *
     * @param args The arguments passed to the program.
     */
    public static void main(String[] args) {
        if (args.length != 5) System.exit(-5);
        IDB_Connector conn = new DB_Connector();
        try {
            conn.connect(args[0]+":"+args[1], args[2], args[3], args[4]);
            skywaysolutions.app.gui.Main form = new skywaysolutions.app.gui.Main("AirVia ATS", conn);
        } catch (CheckedException e) {
            throw new RuntimeException(e);
        }
    }
}