/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.optimizer.filter;

import ibd.query.Operation;

/**
 *
 * @author Sergio
 */
public interface FilterQueryOptimizer {
    public Operation pushDownFilters(Operation query);
}
