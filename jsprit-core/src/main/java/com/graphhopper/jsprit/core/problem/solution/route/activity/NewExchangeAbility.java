package com.graphhopper.jsprit.core.problem.solution.route.activity;

import com.graphhopper.jsprit.core.problem.AbstractJob;
import com.graphhopper.jsprit.core.problem.Capacity;
import com.graphhopper.jsprit.core.problem.Location;

public class NewExchangeAbility<T extends AbstractJob> extends JobActivityBase<T> implements DeliveryActivity {

    public NewExchangeAbility(NewExchangeAbility<T> sourceActivity) {
        super(sourceActivity);
    }

    public NewExchangeAbility(T job, String name, Location location, double operationTime, Capacity capacity) {
        super(job, name, location, operationTime, capacity);
    }

}
