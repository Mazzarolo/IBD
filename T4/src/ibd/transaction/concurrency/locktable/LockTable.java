/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.locktable;

import ibd.transaction.instruction.Instruction;
import ibd.transaction.instruction.MultiReadInstruction;
import ibd.transaction.Transaction;
import ibd.transaction.concurrency.Item;
import ibd.transaction.concurrency.locktable.items.HashItem;
import ibd.transaction.concurrency.locktable.items.ItemCollection;
import ibd.transaction.concurrency.locktable.items.JoaoMazzaroloItemCollection;

/**
 *
 * @author pccli
 */
public class LockTable {

    ItemCollection items = new JoaoMazzaroloItemCollection();

    /*
    * Adds an item related to the records accessed by the instruction. 
    * the recors are identified by their primary keys
    * If an item already exists, just returns it. 
     */
    public Item addItem(Instruction i) {
        Long firstPK = i.getPk();
        Long lastPK = firstPK;

        if (i instanceof MultiReadInstruction) {
            MultiReadInstruction m = (MultiReadInstruction) i;
            lastPK = m.getLastPK();
        }

        Item item = items.getItem(firstPK, lastPK);
        if (item != null) {
            return item;
        }

        item = items.addItem(firstPK, lastPK);

        return item;

    }

    /*
    * returns all items affected by the records acessed by the instruction. 
     */
    public Iterable<Item> getItems(Instruction i) {
        Long firstPK = i.getPk();
        Long lastPK = firstPK;

        if (i instanceof MultiReadInstruction) {
            MultiReadInstruction m = (MultiReadInstruction) i;
            lastPK = m.getLastPK();
        }

        return items.getOverlappedItems(firstPK, lastPK);

    }

    /*
    * removes all references to a transaction (its instructions) from the items of the lock table 
     */
    public void removeTransaction(Transaction t) {
        for (Item item : items.getAllItems()) {
            item.removeTransaction(t);
        }
    }

}
