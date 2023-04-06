package skywaysolutions.app.reports;

import skywaysolutions.app.utils.CheckedException;

class  Main {
    public static void main(String args[]) {
        TableCell[][][] report = new TableCell[20][20][20];
        TableCell[][] reportTable = new TableCell[20][20];

        TicketStockTurnoverReport testReport = new TicketStockTurnoverReport();
        try {
            report = testReport.generateTables();
        } catch (CheckedException e) {
            e.printStackTrace();
            System.out.println("error");
        }

        for (int tableNumber = 0; tableNumber < 3; tableNumber++) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 16; j++) {
                    reportTable[i][j] = report[tableNumber][i][j];
                    if (reportTable[i][j] == null) {
                        reportTable[i][j] = new TableCell("");
                    }
                    System.out.print(" " + reportTable[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println("");
        }
    }
}
