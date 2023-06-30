/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency;

import ibd.table.record.AbstractRecord;
import ibd.transaction.log.EmptyLogger;
import ibd.transaction.instruction.Instruction;
import ibd.transaction.log.Logger;
import ibd.transaction.SimulatedIterations;
import ibd.transaction.Transaction;
import static ibd.transaction.instruction.Instruction.WRITE;
import ibd.transaction.concurrency.locktable.LockTables;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pccli
 */
public class LockBasedConcurrencyManager extends ConcurrencyManager {

    //LockTable lockTable = new HashLockTable();
    //LockTable lockTable = new LockTable();
    LockTables lockTables = new LockTables();

    public Logger logger;

    protected ArrayList<Transaction> activeTransactions = new ArrayList<>();

    /**
     *
     * @throws Exception
     */
    public LockBasedConcurrencyManager() throws Exception {
        //logger = new Logger0("c:\\teste\\ibd", "log.txt");
        logger = new EmptyLogger();
    }

    /**
     * this method need to be called to restore the database to a valid state if
     * transactions where interrupted during the previous database usage
     *
     * @throws Exception
     */
    @Override
    public void recoverFromLog() throws Exception {
        logger.recover();
    }

    /**
     *
     * @throws Exception
     */
    @Override
    public void clearLog() throws Exception {
        logger.clear();
    }

    /**
     * Logs the start of the transaction in the recovery log.
     *
     * @param t
     * @return
     * @throws Exception
     */
    private void logTansactionStart(Transaction t) throws Exception {
        if (!activeTransactions.contains(t)) {
            activeTransactions.add(t);
            logger.transactionStart(t);
        }
    }

    /**
     * Logs the abort of the transaction in the recovery log.
     *
     * @param t
     * @return
     * @throws Exception
     */
    private void logTansactionAbort(Transaction t) throws Exception {
        logger.transactionAbort(t);
        activeTransactions.remove(t);
    }

    /**
     * Logs the commit of the transaction in the recovery log.
     *
     * @param t
     * @return
     * @throws Exception
     */
    private void logTansactionCommit(Transaction t) throws Exception {
        logger.transactionCommit(t);
        activeTransactions.remove(t);
    }

    /**
     * Tries to process the current instrucion of a transaction
     *
     * @param t the transaction from which the current instruction is to be
     * processed
     * @return the records affected by the instruction. If the instruction was
     * not executed by some reason, the return is null
     * @throws Exception
     */
    @Override
    public List<AbstractRecord> processInstruction(Transaction t) throws Exception {

        //puts the transaction in the recovery log if its not already there
        logTansactionStart(t);

        //verifies if the transaction is currently blocked
        if (!t.waitingLockRelease()) {

            //if not, adds its current instruction in the lock table
            Transaction toAbort = queueTransaction(t.getCurrentInstruction());
            //a deadlock prevention strategy decides if a transaction should be chosen to abort
            if (toAbort != null) {
                //aborts the chosen transaction and returns, without executing any instruction
                abort(toAbort);
                return null;
            }
        }

        //at this point, the transaction is definitely in the lock table, either recently or previsoulsy added
        //checks if it can can execute its current instruction
        if (canExecuteCurrentInstruction(t)) {
            //executes the current instruction and returns the affected records
            return processCurrentInstruction(t);

        } //the transaction cannot lock is current instruction
        //it means the transaction is blocked and could not execute
        else {
            //a deadlock prevention strategy decides whether the current transaction should be aborted
            boolean abort = shouldAbort(t);
            //aborts the current transaction
            if (abort) {
                abort(t);
            }

            //returns without executing any instruction
            return null;
        }
    }

    /**
     * Verifies if a transaction can execute its current instruction. All itens
     * where the instruction is queued are checked. The instruction needs to be
     * able to lock all those items in order to be allowed to execute its next
     * instruction
     *
     * @param t the transaction from which the current instruction is to be
     * verified
     * @return true if the instruction can be executed.
     * @throws Exception
     */
    protected boolean canExecuteCurrentInstruction(Transaction t) {
        Instruction i = t.getCurrentInstruction();

        Iterable<Item> itens = lockTables.getItems(i);
        for (Item item : itens) {
            if (alreadyInQueue(item, i) && !item.canBeLockedBy(t)) {
                t.waitingLockRelease = true;
                return false;
            }
        }
        return true;

    }

    /**
     * Effectivelly processes the current instrucion of a transaction.
     *
     * @param t
     * @return
     * @throws Exception
     */
    protected List<AbstractRecord> processCurrentInstruction(Transaction t) throws Exception {
        Instruction i = t.getCurrentInstruction();

        List<AbstractRecord> recs = i.process();
        if (i.endProcessing()) {
            t.advanceInstruction();
        }

        return recs;
    }

    /**
     * Verifies if a transaction should be aborted. The decision can be based on
     * a deadlock prevention strategy that verifies blocked transactions The
     * current implementation returns null, meaning no strategy is used.
     *
     * @param t
     * @return
     */
    protected boolean shouldAbort(Transaction t) {
        return false;
    }

    /**
     * Writes the log to disk
     *
     * @throws Exception
     */
    @Override
    public void flushLog() throws Exception {
        logger.writeLog();
    }

    /**
     * Commits the transaction.
     *
     * @param t the transaction to be committed
     * @throws Exception
     */
    @Override
    public boolean commit(Transaction t) throws Exception {
        t.commit();
        lockTables.removeTransaction(t);
        logTansactionCommit(t);
        return true;
    }

    /**
     * Aborts a transaction
     *
     * @param t
     * @throws Exception
     */
    @Override
    protected void abort(Transaction t) throws Exception {
        //System.out.println("Aborting "+t);
        System.out.println(SimulatedIterations.getTab(t.getId() - 1) + t.getId() + " Abort");
        t.abort();
        lockTables.removeTransaction(t);
        logTansactionAbort(t);

    }

    /**
     * Add the instruction in the lock table, if necessary. To prevent deadlock
     * situtations, a transaction may be chosen to be aborted.
     *
     * @param instruction
     * @return the transation chosen to be aborted
     */
    protected Transaction queueTransaction(Instruction instruction) {

        //a new item is added based on the records needed by the instruction.
        //if the item already exists, it is retrieved
        //Note: an item may refer to an interval of records
        Item itemX = lockTables.addItem(instruction);

        //checks if the trnsaction is already in the queue of the item. 
        //it happens when the transaction issues more than one instruction that affect the same item. 
        //the lock mode required by a instruction may already have been granted by a preceeding instruction of the same trnsaction 
        if (alreadyInQueue(itemX, instruction)) {
            return null;
        }

        //returns all items affected by the instrution
        //the instruction is added to the queue of all of them
        Iterable<Item> itens = lockTables.getItems(instruction);
        Transaction toAbort = null;
        for (Item item : itens) {
            //if (!alreadyInQueue(item, instruction)) 
            {
                //some extensions of the locking queue may implement a deadlock prevention strategy that choses a transaction to be aborted
                Transaction t = addToQueue(item, instruction);
                if (toAbort == null) {
                    toAbort = t;
                }

            }
        }
        return toAbort;

    }

    /**
     * Effectivelly adds the instruction in the lock table The function can
     * return a transaction that should be aborted. The decision to abort a
     * transaction is one way to implement a deadlock prevention strategy. The
     * transaction selection may take into account the dependencies in the lock
     * table. The current implementation returns null, which means no deadlock
     * prevention strategy is used.
     *
     * @param item
     * @param instruction
     * @return
     */
    protected Transaction addToQueue(Item item, Instruction instruction) {

        Transaction t = instruction.getTransaction();
        Lock l = new Lock(t, instruction.getMode());
        item.locks.add(l);
        //instruction.setItem(item);

        return null;
    }

    /**
     * Verifies if a required lock mode of an instruction is already in the
     * queue of a database item. Returns true if the required lock mode is
     * already in the queue or the transaction already has a write lock (which
     * subsumes read locks)
     */
    private boolean alreadyInQueue(Item item, Instruction instruction) {
        Transaction t = instruction.getTransaction();
        int requiredMode = instruction.getMode();
        for (int i = 0; i < item.locks.size(); i++) {
            Lock l = item.locks.get(i);
            if (l.transaction.equals(t)) {
                if (requiredMode == l.mode || l.mode >= WRITE) {
                    //instruction.setItem(item);
                    return true;
                }
            }
        }
        return false;
    }

}
