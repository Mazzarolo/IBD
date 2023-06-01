package ibd.query.optimizer.filter;

import ibd.query.Operation;

public class JoaoMazzaroloQueryOptimizer implements FilterQueryOptimizer {
    public Operation pushDownFilters(Operation op) {
        return op;
    }
}
