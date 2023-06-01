package ibd.query.binaryop;

import ibd.query.Operation;
import ibd.query.Tuple;
import ibd.query.binaryop.join.NestedLoopJoin;
import ibd.query.binaryop.join.NestedLoopJoin1;
import ibd.query.sourceop.TableScan;
import ibd.query.unaryop.PKSort;
import ibd.table.Params;
import ibd.table.Table;
import ibd.table.Utils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainTest {

    public static List<Integer> getArrayDifference(List<Integer> list1, List<Integer> list2) {
        List<Integer> differenceList = new ArrayList<>(list1);
        // System.out.println("tamanho lista 1: " + list1.size());
        // System.out.println("tamanho lista 2: " + list2.size());
        differenceList.removeAll(list2);
        return differenceList;
    }

    public static List<Integer> generateSortedArray(int size, int increment) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < size; i += increment) {
            list.add(i);
        }
        return list;
    }

    public static void compareLists(List<Integer> list1, List<Integer> list2) throws InvalidParameterException {
        if (list1.size() != list2.size()) {
            throw new InvalidParameterException("Lists have different lengths");
        }

        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                throw new InvalidParameterException("Lists are not equal at index " + i);
            }
        }
        System.out.println("Test passed");
    }

    public static void printList(List<Integer> list) {
        for (int num : list) {
            System.out.println(num);
        }
    }

    /*
     * public static void main(String[] args) throws Exception {
     * int arr1Size = 27, arr2Size = 51, arr1Range = 2, arr2Range = 8;
     * Table table1 = Utils.createTable("./tables", "t1", 4096, arr1Size, false,
     * arr1Range);
     * Table table2 = Utils.createTable("./tables", "t2", 4096, arr2Size, false,
     * arr2Range);
     * Operation scan1 = new PKSort(new TableScan("t1", table1), "t1");
     * Operation scan2 = new PKSort(new TableScan("t2", table2), "t2");
     * Operation diff = new DeivisDifference(scan1, scan2);
     * Params.BLOCKS_LOADED = 0;
     * diff.open();
     * for (int i = 0; i < 2000000000; i++) {
     * diff.hasNext();
     * }
     * List<Integer> l1 = generateSortedArray(arr1Size, arr1Range);
     * List<Integer> l2 = generateSortedArray(arr2Size, arr2Range);
     * System.out.println("Tabela 1:");
     * printList(l1);
     * System.out.println("Tabela 2:");
     * printList(l2);
     * List<Integer> testedDiff = getArrayDifference(l1, l2);
     * // System.out.println("Diferença para teste: ");
     * // printList(testedDiff);
     * 
     * List<Integer> realDiff = new ArrayList<>();
     * while (diff.hasNext()) {
     * // System.out.println("Batata");
     * Tuple r = diff.next();
     * // System.out.println(r.sourceTuples[0].primaryKey + " - " +
     * // r.sourceTuples[0].content);
     * realDiff.add((int) (r.sourceTuples[0].record.getPrimaryKey() %
     * Integer.MAX_VALUE));
     * // System.out.println("-- FIM NEXT -- \n \n \n \n \n \n \n");
     * // System.out.println(r.sourceTuples[1].primaryKey + " - " +
     * // r.sourceTuples[1].content);
     * // System.out.println(Params.BLOCKS_LOADED);
     * }
     * System.out.println("Lista Esperada: ");
     * printList(testedDiff);
     * System.out.println("Lista Gerada: ");
     * printList(realDiff);
     * compareLists(testedDiff, realDiff);
     * diff.close();
     * System.out.println("blocks loaded " + Params.BLOCKS_LOADED);
     * }
     */

    public static void main(String[] args) throws Exception {

        int i = 1, errors = 0;
        while (i < 1000) {
            Random random = new Random();
            int arr1Size = random.nextInt(100) + 1;
            int arr1Range = random.nextInt(20) + 1;
            int arr2Range = random.nextInt(20) + 1;
            int arr2Size = random.nextInt(100) + 1;
            Table table1 = Utils.createTable(".", "t" + String.valueOf(i), 4096,
                    arr1Size, false, arr1Range);
            Table table2 = Utils.createTable(".", "t" + String.valueOf(i + 1), 4096,
                    arr1Size, false, arr2Range);
            Operation scan1 = new PKSort(new TableScan("t1", table1), "t1");
            Operation scan2 = new PKSort(new TableScan("t2", table2), "t2");
            Operation diff = new JoaoMazzaroloDifference(scan1, scan2);
            Params.BLOCKS_LOADED = 0;
            diff.open();
            for (int j = 0; j < 10000; j++) {
                diff.hasNext();
            }
            List<Integer> l1 = generateSortedArray(arr1Size, arr1Range);
            List<Integer> l2 = generateSortedArray(arr1Size, arr2Range);
            List<Integer> testedDiff = getArrayDifference(l1, l2);
            List<Integer> realDiff = new ArrayList<>();
            while (diff.hasNext()) {
                Tuple r = diff.next();
                // System.out.println(r);
                // System.out.println(r.sourceTuples[0].primaryKey + " - " +
                // r.sourceTuples[0].content);
                realDiff.add((int) (r.sourceTuples[0].record.getPrimaryKey() %
                        Integer.MAX_VALUE));
                // System.out.println(Params.BLOCKS_LOADED);
            }

            try {
                compareLists(testedDiff, realDiff);
            } catch (Exception e) {
                System.out.println("tamanho 1: " + arr1Size);
                System.out.println("tamanho 2: " + arr2Size);
                System.out.println("range 1: " + arr1Range);
                System.out.println("range 2: " + arr2Range);
                System.out.println(e);
                System.out.println("Lista para teste");
                printList(testedDiff);
                System.out.println("Lista Gerada");
                printList(realDiff);

                errors += 1;
            }
            diff.close();
            i += 2;
        }
        System.out.println("Erros: " + errors);

    }

}
