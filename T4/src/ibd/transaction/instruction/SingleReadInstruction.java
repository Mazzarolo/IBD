/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ibd.transaction.instruction;

import ibd.table.record.AbstractRecord;
import ibd.table.Table;
import ibd.transaction.SimulatedIterations;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pccli
 */
public class SingleReadInstruction extends Instruction{
    
    
    
public SingleReadInstruction(Table table, long pk) throws Exception{
        super(table, pk);
        mode = Instruction.READ;
    }
    
    public SingleReadInstruction(Table table, String pk) throws Exception{
        this(table, SimulatedIterations.getValue(pk));
    }
    
    
    
    @Override
    public List<AbstractRecord> process() throws Exception{
        List l = new ArrayList();
        l.add(getTable().getRecord(pk));
        endProcessing = true;
        return l;
    }
        
        
    
    @Override
    public int getMode(){
    return Instruction.READ;
    }


    
}
