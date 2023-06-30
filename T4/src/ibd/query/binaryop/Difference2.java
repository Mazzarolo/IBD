package ibd.query.binaryop;

import ibd.query.Operation;
import ibd.query.Tuple;

public class Difference2 extends BinaryOperation {

    private Tuple rightTuple;
    private Tuple nextTuple;

    public Difference2(Operation op1, Operation op2) throws Exception {
        super(op1, op2);
    }

    public Difference2(Operation op1, String sourceName1, Operation op2, String sourceName2) throws Exception {
        super(op1, sourceName1, op2, sourceName2);
    }

    @Override
    public void open() throws Exception {
        super.open();
        rightTuple = null;
        nextTuple = null;
    }

    @Override
    public Tuple next() throws Exception {
        if (nextTuple != null) {
            Tuple tuple = nextTuple;
            nextTuple = null;
            return tuple;
        }
        return diff();
    }

    @Override
    public boolean hasNext() throws Exception {
        if (nextTuple == null) {
            nextTuple = diff();
        }
        return nextTuple != null;
    }

    /**
     * Retorna a próxima tupla dessa diferença
     */
    private Tuple diff() throws Exception {
        while (op1.hasNext()) {
            Tuple leftTuple = op1.next();
            if (!hasEqual(leftTuple)) {
                Tuple tuple = new Tuple();
                tuple.addSource(leftTuple);
                return tuple;
            }
        }
        return null;
    }

    /**
     * Retorna se a tupla de entrada possui uma correspondente na operação
     * direita
     */
    private boolean hasEqual(Tuple leftTuple) throws Exception {
        while (rightTuple != null || op2.hasNext()) {
            if (rightTuple == null) {
                rightTuple = op2.next();
            }
            int compare = Long.compare(leftTuple.sourceTuples[tupleIndex1].record.getPrimaryKey(), rightTuple.sourceTuples[tupleIndex2].record.getPrimaryKey());
            if (compare < 0) {
                return false;
            } else if (compare == 0) {
                rightTuple = null;
                return true;
            }
            rightTuple = null;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Difference";
    }
}
