/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.table.Utils;
import ibd.index.ComparisonTypes;
import ibd.query.Operation;
import ibd.query.Tuple;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio
 */
public class PkRangeFilter extends PKFilter{

    
    Long value2 = -1L;
    int comparisonType2 = -1;
    
    
    
    public PkRangeFilter(Operation op, String sourceName, int comparisonType, long value, int comparisonType2, long value2) throws Exception{
        super(op, sourceName, comparisonType, value);
        this.comparisonType2 = comparisonType2;
        this.value2 = value2;
    }
    
    public Long getValue2(){
        return value2;
    }
    
    public int getComparisonType2(){
        return comparisonType2;
    }

    

     public boolean match(Tuple tp){
    boolean ok = super.match(tp);
    if (!ok) return false;
    return Utils.match(tp.sourceTuples[tupleIndex].record.getPrimaryKey(),value2, comparisonType2);
    
    
    }
     
     
    @Override
     public String toString(){
        if (sourceName==null)
            return "PK Filter "+ComparisonTypes.getComparisonType(comparisonType)+" "+ value;
        else return "PK Filter ["+sourceName+"]"+ComparisonTypes.getComparisonType(comparisonType)+" "+ value;
     }
    
}
