/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency;

import ibd.table.record.AbstractRecord;
import ibd.transaction.Transaction;
import java.util.List;

/**
 *
 * @author Sergio
 */
public abstract class ConcurrencyManager {
    
    public abstract void recoverFromLog() throws Exception;
    public abstract void clearLog() throws Exception;
    public abstract void flushLog() throws Exception ;
    
    public abstract List<AbstractRecord> processInstruction(Transaction t) throws Exception;
    public abstract boolean commit(Transaction t) throws Exception;
    protected abstract void abort(Transaction t) throws Exception;

}
