/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.optimizer.filter;

import ibd.table.Directory;
import ibd.query.sourceop.TableScan;
import ibd.query.binaryop.join.NestedLoopJoin;
import ibd.table.Params;
import static ibd.table.Utils.createTable;
import ibd.index.ComparisonTypes;
import ibd.query.Operation;
import ibd.query.Tuple;
import ibd.query.Utils;
import ibd.query.binaryop.Difference;
import ibd.query.unaryop.PKFilter;
import ibd.table.Table;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio
 */
public class Main {

    public void testPushDownOptimization(FilterQueryOptimizer opt, Operation query, boolean showTree, boolean runQuery) throws Exception {

        Params.BLOCKS_LOADED = 0;
        Params.BLOCKS_SAVED = 0;

        System.out.println("BEFORE");
        if (showTree) {
            Utils.toString(query, 0);
            System.out.println("");
        }

        if (runQuery) {
            Params.BLOCKS_LOADED = 0;
            Params.BLOCKS_SAVED = 0;
            query.open();
            while (query.hasNext()) {
                Tuple r = (Tuple) query.next();
                System.out.println(r);
            }
            query.close();

            System.out.println("blocks loaded during reorganization " + Params.BLOCKS_LOADED);
            System.out.println("blocks saved during reorganization " + Params.BLOCKS_SAVED);
        }

        query = opt.pushDownFilters(query);

        System.out.println("AFTER");
        if (showTree) {
            Utils.toString(query, 0);
            System.out.println("");
        }

        if (runQuery) {
            Params.BLOCKS_LOADED = 0;
            Params.BLOCKS_SAVED = 0;
            query.open();
            while (query.hasNext()) {
                Tuple r = (Tuple) query.next();
                System.out.println(r);
            }
            query.close();

            System.out.println("blocks loaded during reorganization " + Params.BLOCKS_LOADED);
            System.out.println("blocks saved during reorganization " + Params.BLOCKS_SAVED);
        }
    }

    private Operation createQuery1() throws Exception {

        Table table1 = Directory.getTable(".", "t1", Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable(".", "t2", Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);

        Operation join1 = new Difference(scan1, scan2);

        Operation filter1 = new PKFilter(join1, null, ComparisonTypes.EQUAL, 20L);

        return filter1;
    }

    private Operation createQuery2() throws Exception {

        Table table1 = Directory.getTable(".", "t1", Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable(".", "t2", Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable(".", "t3", Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);
        Operation scan3 = new TableScan("t3", table3);

        Operation diff = new Difference(scan1, scan2);
        Operation join2 = new NestedLoopJoin(diff, scan3);

        Operation filter1 = new PKFilter(join2, null, ComparisonTypes.EQUAL, 20L);

        return filter1;
    }

    public static void main(String[] args) {
        try {
            Main m = new Main();
            FilterQueryOptimizer opt = new JoaoMazzaroloQueryOptimizer();

            createTable(".", "t1", Table.DEFULT_PAGE_SIZE, 100, false, 2);
            createTable(".", "t2", Table.DEFULT_PAGE_SIZE, 100, false, 3);
            createTable(".", "t3", Table.DEFULT_PAGE_SIZE, 100, false, 4);

            //coloque o código aqui
            System.out.println("**********TESTE 1");
            m.testPushDownOptimization(opt, m.createQuery1(), true, true);

            System.out.println("**********TESTE 2");
            m.testPushDownOptimization(opt, m.createQuery2(), true, true);

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
