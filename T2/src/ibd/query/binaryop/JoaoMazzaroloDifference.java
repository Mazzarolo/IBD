package ibd.query.binaryop;

import ibd.query.Operation;
import ibd.query.Tuple;
import ibd.query.binaryop.BinaryOperation;
import java.util.Objects;

public class JoaoMazzaroloDifference extends BinaryOperation {

    Tuple nextTuple;
    Tuple lTuple;
    Tuple rTuple;

    public JoaoMazzaroloDifference(Operation op1, Operation op2) throws Exception
    {
        super(op1, op2);
    }

    @Override
    public void open() throws Exception
    {
        super.open();
        nextTuple = null;
        lTuple = op1.next();
        rTuple = op2.next();
    }

    @Override
    public Tuple next() throws Exception
    {
        if (nextTuple != null) {
            Tuple next_ = nextTuple;
            nextTuple = null;
            return next_;
        }
        return diff();
    }
    
    @Override
    public boolean hasNext() throws Exception
    {
        if (nextTuple != null) {
            return true;
        }

        nextTuple = diff();
        
        return (nextTuple != null);
    }

    private Tuple verifyLastLeftTuple() throws Exception
    {
        return null;
    }

    private Tuple diff() throws Exception
    {
        if (lTuple == null) {
            return null;
        }

        if (rTuple == null) {
            Tuple temp = lTuple;
            if(op1.hasNext())
                lTuple = op1.next();
            else
                lTuple = null;
            return temp;
        }

        while (lTuple != null && rTuple != null) {
            int compare = Long.compare(lTuple.sourceTuples[tupleIndex1].record.getPrimaryKey(), rTuple.sourceTuples[tupleIndex2].record.getPrimaryKey());

            // System.out.println(lTuple.sourceTuples[tupleIndex1].record.getPrimaryKey());
            // System.out.println(rTuple.sourceTuples[tupleIndex1].record.getPrimaryKey());

            if (compare < 0) {
                Tuple temp = lTuple;
                if(op1.hasNext())
                    lTuple = op1.next();
                else
                    lTuple = null;
                return temp;
            } else if (compare > 0) {
                if(op2.hasNext())
                    rTuple = op2.next();
                else
                {
                    rTuple = null;
                    Tuple temp = lTuple;
                    if(op1.hasNext())
                        lTuple = op1.next();
                    else
                        lTuple = null;
                    return temp;
                }
            } else {
                if(op1.hasNext())
                    lTuple = op1.next();
                else
                {
                    lTuple = null;
                }
                if(op2.hasNext())
                    rTuple = op2.next();
                else
                {
                    rTuple = null;
                    Tuple temp = lTuple;
                    if(op1.hasNext())
                        lTuple = op1.next();
                    else
                        lTuple = null;
                    return temp;
                }
            }
        }

        return null;
    }

    @Override
    public String toString(){
        return "Joao Mazzarolo Difference";
    }
}
