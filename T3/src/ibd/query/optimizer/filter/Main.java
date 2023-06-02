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
import ibd.query.unaryop.PKSort;
import ibd.table.Table;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio
 */
public class Main {

    String path = ".";

    public void testPushDownOptimization(FilterQueryOptimizer opt, Operation query, boolean showTree, boolean runQuery)
            throws Exception {

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

        Table table1 = Directory.getTable(path, "t1", Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable(path, "t2", Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);

        Operation join1 = new Difference(scan1, scan2);

        Operation filter1 = new PKFilter(join1, null, ComparisonTypes.EQUAL, 20L);

        return filter1;
    }

    private Operation createQuery2() throws Exception {

        Table table1 = Directory.getTable(path, "t1", Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable(path, "t2", Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable(path, "t3", Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);
        Operation scan3 = new TableScan("t3", table3);

        Operation diff = new Difference(scan1, scan2);
        Operation join2 = new NestedLoopJoin(diff, scan3);

        Operation filter1 = new PKFilter(join2, null, ComparisonTypes.EQUAL, 20L);

        return filter1;
    }

    private Operation createQuery3() throws Exception {

        Table table1 = Directory.getTable(path, "t1", Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable(path, "t2", Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable(path, "t3", Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);
        Operation scan3 = new TableScan("t3", table3);

        Operation diff = new Difference(scan1, scan2);
        ;

        Operation filter1 = new PKFilter(diff, null, ComparisonTypes.GREATER_THAN, 5L);
        Operation filter2 = new PKFilter(filter1, null, ComparisonTypes.LOWER_THAN, 30L);

        return filter2;
    }

    private Operation createQuery4() throws Exception {

        Table table1 = Directory.getTable(path, "t1", Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable(path, "t2", Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable(path, "t3", Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);
        Operation scan3 = new TableScan("t3", table3);

        Operation diff = new Difference(scan1, scan2);
        ;

        Operation filter1 = new PKFilter(diff, null, ComparisonTypes.GREATER_THAN, 5L);
        Operation filter2 = new PKFilter(filter1, null, ComparisonTypes.LOWER_THAN, 30L);

        Operation join2 = new NestedLoopJoin(filter2, scan3);

        Operation filter3 = new PKFilter(join2, null, ComparisonTypes.EQUAL, 20L);

        return filter3;
    }

    public Operation createQuery5() throws Exception {
        Table table1 = Directory.getTable(path, "t1", Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable(path, "t2", Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable(path, "t3", Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);
        Operation scan3 = new TableScan("t3", table3);

        Operation join1 = new NestedLoopJoin(scan1, scan2);

        Operation diff = new Difference(join1, scan2);
        Operation filter0 = new PKFilter(diff, null, ComparisonTypes.GREATER_THAN, 10L);
        Operation sort = new PKSort(filter0, "t1");
        Operation filter = new PKFilter(sort, null, ComparisonTypes.GREATER_THAN, 75L);
        Operation diff2 = new Difference(filter, scan3);
        Operation filtera = new PKFilter(diff2, null, ComparisonTypes.GREATER_THAN, 85L);

        Operation filter1 = new PKFilter(filtera, null, ComparisonTypes.GREATER_THAN, 5L);
        Operation filter2 = new PKFilter(filter1, null, ComparisonTypes.LOWER_THAN, 30L);

        Operation join2 = new NestedLoopJoin(filter2, scan3);

        Operation filter3 = new PKFilter(join2, null, ComparisonTypes.EQUAL, 20L);

        return filter3;
    }

    private Operation createQuery6() throws Exception {
        Table table1 = Directory.getTable(path, "t1", Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable(path, "t2", Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);

        Operation join1 = new Difference(scan1, scan2);

        Operation filter1 = new PKFilter(join1, null, ComparisonTypes.EQUAL, 20L);

        Operation sort = new PKSort(filter1, null);

        return sort;
    }

    private Operation createQuery7() throws Exception {

        Table table1 = Directory.getTable(path,
                "t1", Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable(path,
                "t2", Table.DEFULT_PAGE_SIZE, false);
        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);

        Operation diff = new Difference(scan1, scan2);

        Operation filter1 = new PKFilter(diff, null, ComparisonTypes.EQUAL, 20L);
        Operation filter2 = new PKFilter(filter1, null, ComparisonTypes.EQUAL, 50L);

        return filter2;
    }

    public Operation createQuery102mod() throws Exception {
        Table table1 = Directory.getTable(path, "t1", Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable(path, "t2", Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable(path, "t3", Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);
        Operation scan3 = new TableScan("t3", table3);

        Operation diff1 = new Difference(scan1, scan2);
        Operation filter0 = new PKFilter(diff1, null, ComparisonTypes.GREATER_THAN, 20L);
        Operation join = new NestedLoopJoin(filter0, scan3);
        Operation diff2 = new Difference(join, scan1);
        Operation diff3 = new Difference(diff2, scan3);
        Operation filter1 = new PKFilter(diff3, null, ComparisonTypes.LOWER_THAN, 5L);
        Operation diff4 = new Difference(filter1, scan3);
        Operation filter2 = new PKFilter(diff4, null, ComparisonTypes.EQUAL, 10L);

        return filter2;
    }

    public Operation createQuery100() throws Exception {
        Table table1 = Directory.getTable(path, "t1", Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable(path, "t2", Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable(path, "t3", Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);
        Operation scan3 = new TableScan("t3", table3);
        Operation diff1 = new Difference(scan1, scan2);
        Operation diff2 = new Difference(diff1, scan3);
        Operation filter1 = new PKFilter(diff2, null, ComparisonTypes.LOWER_THAN, 5L);

        return filter1;
    }

    public Operation createQuery101() throws Exception {
        Table table1 = Directory.getTable(path, "t1", Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable(path, "t2", Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable(path, "t3", Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);
        Operation scan3 = new TableScan("t3", table3);

        Operation diff1 = new Difference(scan1, scan2);
        Operation diff2 = new Difference(diff1, scan3);
        Operation join = new NestedLoopJoin(diff1, diff2);
        Operation filter1 = new PKFilter(join, null, ComparisonTypes.LOWER_THAN, 5L);

        return filter1;
    }

    public Operation createQuery102() throws Exception {
        Table table1 = Directory.getTable(path, "t1", Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable(path, "t2", Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable(path, "t3", Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);
        Operation scan3 = new TableScan("t3", table3);

        Operation diff1 = new Difference(scan1, scan2);
        Operation filter0 = new PKFilter(diff1, null, ComparisonTypes.GREATER_THAN, 20L);
        Operation join = new NestedLoopJoin(filter0, scan3);
        Operation diff2 = new Difference(join, scan1);
        Operation sort1 = new PKSort(diff2, "t1");
        Operation diff3 = new Difference(sort1, scan3);
        Operation filter1 = new PKFilter(diff3, null, ComparisonTypes.LOWER_THAN, 5L);
        Operation diff4 = new Difference(filter1, scan3);
        Operation filter2 = new PKFilter(diff4, null, ComparisonTypes.EQUAL, 10L);

        return filter2;
    }

    public Operation createQuery103() throws Exception {
        Table table1 = Directory.getTable(path, "t1", Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable(path, "t2", Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable(path, "t3", Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);
        Operation scan3 = new TableScan("t3", table3);

        Operation join = new NestedLoopJoin(scan1, scan2);
        Operation sort1 = new PKSort(join, "t1");
        Operation filter1 = new PKFilter(sort1, null, ComparisonTypes.EQUAL, 1L);
        Operation join2 = new NestedLoopJoin(filter1, scan3);
        Operation sort2 = new PKFilter(join2, null, ComparisonTypes.EQUAL, 1L);

        return sort2;
    }

    public Operation createQuery104() throws Exception {
        Table table1 = Directory.getTable(path, "t1", Table.DEFULT_PAGE_SIZE, false);
        Table table2 = Directory.getTable(path, "t2", Table.DEFULT_PAGE_SIZE, false);
        Table table3 = Directory.getTable(path, "t3", Table.DEFULT_PAGE_SIZE, false);

        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);
        Operation scan3 = new TableScan("t3", table3);

        Operation sort1 = new PKSort(scan1, "t1");
        Operation diff = new Difference(sort1, scan3);
        Operation filter = new PKFilter(diff, null, ComparisonTypes.EQUAL, 20L);

        return filter;
    }

    public static void main(String[] args) {
        try {
            Main m = new Main();
            FilterQueryOptimizer opt = new JoaoMazzaroloQueryOptimizer();

            createTable(m.path, "t1", Table.DEFULT_PAGE_SIZE, 100, false, 2);
            createTable(m.path, "t2", Table.DEFULT_PAGE_SIZE, 100, false, 3);
            createTable(m.path, "t3", Table.DEFULT_PAGE_SIZE, 100, false, 4);

            // coloque o código aqui
            System.out.println("**********TESTE 1");
            m.testPushDownOptimization(opt, m.createQuery1(), true, true);

            System.out.println("**********TESTE 2");
            m.testPushDownOptimization(opt, m.createQuery2(), true, true);

            System.out.println("**********TESTE 3");
            m.testPushDownOptimization(opt, m.createQuery3(), true, true);

            System.out.println("**********TESTE 4");
            m.testPushDownOptimization(opt, m.createQuery4(), true, true);

            System.out.println("**********TESTE 5");
            m.testPushDownOptimization(opt, m.createQuery5(), true, true);

            System.out.println("**********TESTE 6");
            m.testPushDownOptimization(opt, m.createQuery6(), true, false);

            System.out.println("**********TESTE 7");
            m.testPushDownOptimization(opt, m.createQuery7(), true, true);

            System.out.println("**********TESTE 100");
            m.testPushDownOptimization(opt, m.createQuery100(), true, true);

            System.out.println("**********TESTE 101");
            m.testPushDownOptimization(opt, m.createQuery101(), true, true);

            System.out.println("**********TESTE 102");
            m.testPushDownOptimization(opt, m.createQuery102(), true, true);

            System.out.println("**********TESTE 103");
            m.testPushDownOptimization(opt, m.createQuery103(), true, true);

            System.out.println("**********TESTE 104");
            m.testPushDownOptimization(opt, m.createQuery104(), true, true);

            System.out.println("**********TESTE 102mod");
            m.testPushDownOptimization(opt, m.createQuery102mod(), true, true);

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
