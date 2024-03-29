/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop.join;

import ibd.query.sourceop.TableScan;
import ibd.table.Params;
import static ibd.table.Utils.createTable;
import ibd.query.Operation;
import ibd.query.Tuple;
import ibd.table.Table;
import java.util.logging.Level;
import java.util.logging.Logger;
import ibd.query.binaryop.JoaoMazzaroloDifference;

/**
 *
 * @author Sergio
 */
public class Main {


    public Operation testDiff() throws Exception {

        Table table1 = createTable(".", "t1", 4096,20, false, 10);
        Table table2 = createTable(".", "t2", 8192, 5, false, 2);
        Table table3 = createTable(".", "t3", 2048, 20, false, 1);
        Table table4 = createTable(".", "t4", 4096,30, false, 3);
        Table table5 = createTable(".", "t5", 1024, 10, false, 2);
        Table table6 = createTable(".", "t6", 8192, 50, false, 3);
        Table table7 = createTable(".", "t7", 4096, 200, false, 5);
        Table table8 = createTable(".", "t8", 2048, 30, false, 2);
        Table table9 = createTable(".", "t9", 1024, 10, false, 1);
        Table table10 = createTable(".", "t10", 2048, 50, false, 4);
        Table table11 = createTable(".", "t11", 4096, 80, false, 2);
        Table table12 = createTable(".", "t12", 1024, 15, false, 3);

        Operation scan = new TableScan("t1", table1);
        //PKFilter scan1 = new PKFilter(scan, "t1", ComparisonTypes.LOWER_THAN, 2L);
        Operation scan2 = new TableScan("t2", table2);
        Operation scan3 = new TableScan("t3", table3);
        Operation scan4 = new TableScan("t4", table4);
        Operation scan5 = new TableScan("t5", table5);
        Operation scan6 = new TableScan("t6", table6);
        Operation scan7 = new TableScan("t7", table7);
        Operation scan8 = new TableScan("t8", table8);
        Operation scan9 = new TableScan("t9", table9);
        Operation scan10 = new TableScan("t10", table10);
        Operation scan11 = new TableScan("t11", table11);
        Operation scan12 = new TableScan("t12", table12);


        Operation diff = new JoaoMazzaroloDifference(scan, scan2);

        //construa aqui as junções 

        return diff;

    }


    public static void main(String[] args) {
        try {
            Main m = new Main();

            Operation op = m.testDiff();

            Params.BLOCKS_LOADED = 0;
            Params.BLOCKS_SAVED = 0;

            op.open();
            while (op.hasNext()) {
                Tuple r = op.next();
                System.out.println(r);
            }

            System.out.println("blocks loaded during reorganization " + Params.BLOCKS_LOADED);
            System.out.println("blocks saved during reorganization " + Params.BLOCKS_SAVED);

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
