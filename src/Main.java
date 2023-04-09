import skywaysolutions.app.database.DB_Connector;
import skywaysolutions.app.database.IDB_Connector;
import skywaysolutions.app.gui.Prompt;
import skywaysolutions.app.utils.AccessorManager;
import skywaysolutions.app.utils.CheckedException;

import java.util.concurrent.CountDownLatch;

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
        //Check if all the arguments have been specified
        if (args.length < 5) {
            System.out.println("Required Arguments:");
            System.out.println("<database host> <database port> <database name> <database username> <database password> [UNLOCK|PURGE]");
            System.out.println("Please contact the administrator to set this up.");
            System.exit(-5);
        }
        //Initialize the database connection...
        IDB_Connector conn = new DB_Connector();
        try {
            conn.connect(args[0]+":"+args[1], args[2], args[3], args[4]);
            //Create accessor manager
            AccessorManager manager = new AccessorManager(conn);
            //Check for special 6th argument
            if (args.length > 5) {
                if (args[5].equals("UNLOCK")) {
                    manager.forceUnlock(null);
                } else if (args[5].equals("PURGE")) {
                    manager.forcePurge(null);
                }
            }
            //Run the main form, use a latch to wait for the form to close
            CountDownLatch latch = new CountDownLatch(1);
            new skywaysolutions.app.gui.Main("AirVia ATS", manager, latch);
            latch.await();
        } catch (CheckedException | InterruptedException e) {
            //Show any unhandled exceptions generated by the program
            Prompt prompt = new Prompt(null, "Exception: " + e.getClass().getName(), "Exception: " + e.getClass().getName() + "\n\n" +
                    e.getMessage() + ((e instanceof CheckedException cex) ? "\n\nStack Trace:\n\n" + cex.getStackTraceAsString() : ""), new String[] {"Exit"},
                    0, false);
            prompt.showDialog();
            try {
                conn.close();
            } catch (CheckedException ex) {
            }
            System.exit(-1);
        } finally {
            //Close the database connection when done
            try {
                conn.close();
            } catch (CheckedException e) {
            }
        }
        System.exit(0);
    }
}