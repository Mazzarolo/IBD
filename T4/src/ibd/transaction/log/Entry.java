/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ibd.transaction.log;

/**
 *
 * @author pccli
 */
public abstract class Entry {
    
    private int transactionId; 
    
    public int getTransactionId(){
    return transactionId;
    }
        
     /**
     * @param transactionId the transactionId to set
     */
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    
    
    public abstract String write();
    public abstract void read(String text) throws Exception;

   
    
}
