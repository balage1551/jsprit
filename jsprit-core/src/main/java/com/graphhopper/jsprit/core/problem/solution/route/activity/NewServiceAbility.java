package com.graphhopper.jsprit.core.problem.solution.route.activity;

import com.graphhopper.jsprit.core.problem.AbstractJob;
import com.graphhopper.jsprit.core.problem.Capacity;
import com.graphhopper.jsprit.core.problem.Location;

public class NewServiceAbility extends JobActivityBase implements DeliveryActivity {

    public NewServiceAbility(NewServiceAbility sourceActivity) {
        super(sourceActivity);
    }

    public NewServiceAbility(AbstractJob job, String name, Location location, double operationTime, Capacity capacity) {
        super(job, name, location, operationTime, capacity);
    }

}
