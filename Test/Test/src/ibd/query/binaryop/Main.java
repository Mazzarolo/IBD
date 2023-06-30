/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop;


import ibd.query.sourceop.TableScan;
import ibd.table.Params;
import static ibd.table.Utils.createTable;
import ibd.query.Operation;
import ibd.query.Tuple;
import ibd.table.Table;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio
 */
public class Main {

    
    
    public Operation testDifference1(boolean display) throws Exception {
        
        Table table1 = createTable("c:\\teste\\ibd", "t1", Table.DEFULT_PAGE_SIZE, 1000, false, 2);
        Table table2 = createTable("c:\\teste\\ibd", "t2", Table.DEFULT_PAGE_SIZE, 1000, false, 3);
        
        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);

        Operation dif1 = diff(scan1, scan2);

        return dif1;

    }
    
    
    public Operation testDifference2(boolean display) throws Exception {
        
        Table table1 = createTable("c:\\teste\\ibd", "t3", Table.DEFULT_PAGE_SIZE, 1000, false, 2);
        Table table2 = createTable("c:\\teste\\ibd", "t4", Table.DEFULT_PAGE_SIZE, 1000, false, 2);
        
        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);

        Operation dif1 = diff(scan1, scan2);

        return dif1;

    }
    
    public Operation testDifference3(boolean display) throws Exception {
        
        Table table1 = createTable("c:\\teste\\ibd", "t5", Table.DEFULT_PAGE_SIZE, 1000, false, 2);
        Table table2 = createTable("c:\\teste\\ibd", "t6", Table.DEFULT_PAGE_SIZE, 1000, false, 3);
        Table table3 = createTable("c:\\teste\\ibd", "t7", Table.DEFULT_PAGE_SIZE, 1000, false, 4);
        Table table4 = createTable("c:\\teste\\ibd", "t8", Table.DEFULT_PAGE_SIZE, 1000, false, 5);
        
        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);
        Operation scan3 = new TableScan("t3", table3);
        Operation scan4 = new TableScan("t4", table4);

        Operation dif1 = diff(scan1, scan2);
        Operation dif2 = diff(scan3, scan4);
        Operation dif3 = diff(dif1, dif2);
        
        return dif3;

    }
    
    
    public Operation testDifference4(boolean display) throws Exception {
        
        Table table1 = createTable("c:\\teste\\ibd", "t9", Table.DEFULT_PAGE_SIZE, 1000, false, 2);
        Table table2 = createTable("c:\\teste\\ibd", "t10", Table.DEFULT_PAGE_SIZE, 1000, false, 2);
        Table table3 = createTable("c:\\teste\\ibd", "t11", Table.DEFULT_PAGE_SIZE, 1000, false, 3);
        
        Operation scan1 = new TableScan("t1", table1);
        Operation scan2 = new TableScan("t2", table2);
        Operation scan3 = new TableScan("t3", table3);

        Operation dif1 = diff(scan1, scan2);
        Operation dif2 = diff(scan3, dif1);
        
        return dif2;

    }

    
    public void run(Operation op) throws Exception{
    Params.BLOCKS_LOADED = 0;
            Params.BLOCKS_SAVED = 0;
            boolean first = true;
            String f = "";
            String l = "";
            int count = 0;
            op.open();
            while (op.hasNext()) {
                Tuple r = op.next();
                if (first){
                    f = r.toString();
                    first = false;
                }
                l = r.toString();
                if (count<20)
                    System.out.println(count+"=>"+r);
                count++;
                
            }
            
            //System.out.println("first:"+f);
            //System.out.println("last:"+l);

            System.out.println("blocks loaded during reorganization " + Params.BLOCKS_LOADED);
            System.out.println("blocks saved during reorganization " + Params.BLOCKS_SAVED);
            
    }
    
     public BinaryOperation diff(Operation op1, Operation op2) throws Exception{
        //return new AlexandreChagasBritesDifference(op1, op2);
        return new ibd.query.binaryop.Difference(op1, op2);
     }
    
    public static void main(String[] args) {
        try {
            Main m = new Main();

            //Operation op = m.testUnion1(true);
            Operation op = null;
            System.out.println("teste 1");
            m.run(m.testDifference1(true));
            System.out.println("teste 2");
            m.run(m.testDifference2(true));
            System.out.println("teste 3");
            m.run(m.testDifference3(true));
            System.out.println("teste 4");
            m.run(m.testDifference4(true));
            
            

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
