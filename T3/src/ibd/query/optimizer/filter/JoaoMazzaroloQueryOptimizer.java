package ibd.query.optimizer.filter;

import java.util.ArrayList;

import ibd.query.Operation;
import ibd.query.binaryop.BinaryOperation;
import ibd.query.sourceop.SourceOperation;
import ibd.query.unaryop.UnaryOperation;
import ibd.query.unaryop.PKFilter;
import ibd.query.binaryop.Difference;
import ibd.query.binaryop.Difference2;

public class JoaoMazzaroloQueryOptimizer implements FilterQueryOptimizer {

    Operation root;
    ArrayList<PKFilter> filters;

    public Operation pushDownFilters(Operation op) throws Exception {

        root = op;

        filters = new ArrayList<PKFilter>();

        pushDownFiltersRec(op);
        
        return root;
    }

    private void pushDownFiltersRec(Operation op) throws Exception {
        if (op instanceof UnaryOperation)
        {
            UnaryOperation uop = (UnaryOperation) op;
            if(uop instanceof PKFilter)
            {
                filters.add((PKFilter) uop);
            }
            pushDownFiltersRec(uop.getOperation());
        }
        else if (op instanceof BinaryOperation){
            BinaryOperation bop = (BinaryOperation) op;
            if(bop instanceof Difference || bop instanceof Difference2)
            {
                moveFilters(bop);
            }
            else
            {
                filters.clear();
            }
            pushDownFiltersRec(bop.getLeftOperation());
            pushDownFiltersRec(bop.getRigthOperation());
        }
        else if (op instanceof SourceOperation){
            return;
        }
    }

    private void moveFilters(BinaryOperation diff) throws Exception {
        for (PKFilter filter : filters) {
            if(root.equals(filter))
            {
                root = filter.getOperation();
                root.setParentOperation(null);
            }
            else
            {
                if(filter.getParentOperation() instanceof BinaryOperation)
                {
                    BinaryOperation parent = (BinaryOperation) filter.getParentOperation();
                    if(parent.getLeftOperation().equals(filter))
                    {
                        parent.setLeftOperation(filter.getOperation());
                    }
                    else
                    {
                        parent.setRightOperation(filter.getOperation());
                    }
                }
                else
                {
                    UnaryOperation parent = (UnaryOperation) filter.getParentOperation();
                    parent.setOperation(filter.getOperation());
                }
                filter.getOperation().setParentOperation(filter.getParentOperation());
            }

            PKFilter leftFilter = filter;
            leftFilter.setOperation(diff.getLeftOperation());
            leftFilter.setParentOperation(diff);
            diff.setLeftOperation(leftFilter);
            PKFilter rightFilter = new PKFilter(diff.getRigthOperation(), null, filter.getComparisonType(), filter.getValue());
            rightFilter.setParentOperation(diff);
            diff.setRightOperation(rightFilter);
        }
        filters.clear();
    }
}
