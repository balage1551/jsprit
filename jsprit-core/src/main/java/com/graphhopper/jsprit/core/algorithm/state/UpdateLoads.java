/*
 * Licensed to GraphHopper GmbH under one or more contributor
 * license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * GraphHopper GmbH licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.graphhopper.jsprit.core.algorithm.state;

import java.util.Collection;

import com.graphhopper.jsprit.core.algorithm.recreate.listener.InsertionStartsListener;
import com.graphhopper.jsprit.core.algorithm.recreate.listener.JobInsertedListener;
import com.graphhopper.jsprit.core.problem.SizeDimension;
import com.graphhopper.jsprit.core.problem.job.DeliveryJob;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.PickupJob;
import com.graphhopper.jsprit.core.problem.job.ServiceJob;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.ActivityVisitor;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;


/**
 * Updates load at start and end of route as well as at each activity. And update is triggered when either
 * activityVisitor has been started, the insertion process has been started or a job has been inserted.
 * <p>
 * <p>Note that this only works properly if you register this class as ActivityVisitor AND InsertionStartsListener AND JobInsertedListener.
 * The reason behind is that activity states are dependent on route-level states and vice versa. If this is properly registered,
 * this dependency is solved automatically.
 *
 * @author stefan
 */
class UpdateLoads implements ActivityVisitor, StateUpdater, InsertionStartsListener, JobInsertedListener {

    private StateManager stateManager;

    /*
     * default has one dimension with a value of zero
     */
    private SizeDimension currentLoad;

    private SizeDimension defaultValue;


    public UpdateLoads(StateManager stateManager) {
        super();
        this.stateManager = stateManager;
        defaultValue = SizeDimension.Builder.newInstance().build();
    }

    @Override
    public void begin(VehicleRoute route) {
        currentLoad = stateManager.getRouteState(route, InternalStates.LOAD_AT_BEGINNING, SizeDimension.class);
        if (currentLoad == null) {
            currentLoad = defaultValue;
        }
    }

    @Override
    public void visit(TourActivity act) {
        currentLoad = currentLoad.add(act.getLoadChange());
        stateManager.putInternalTypedActivityState(act, InternalStates.LOAD, currentLoad);
        //		assert currentLoad.isLessOrEqual(route.getVehicle().getType().getCapacityDimensions()) : "currentLoad at activity must not be > vehicleCapacity";
        //		assert currentLoad.isGreaterOrEqual(SizeDimension.Builder.newInstance().build()) : "currentLoad at act must not be < 0 in one of the applied dimensions";
    }

    @Override
    public void finish() {
        currentLoad = SizeDimension.Builder.newInstance().build();
    }

    void insertionStarts(VehicleRoute route) {
        SizeDimension loadAtDepot = SizeDimension.Builder.newInstance().build();
        SizeDimension loadAtEnd = SizeDimension.Builder.newInstance().build();
        for (Job j : route.getTourActivities().getJobs()) {
            if (j instanceof DeliveryJob) {
                loadAtDepot = loadAtDepot.add(j.getSize());
            } else if (j instanceof PickupJob || j instanceof ServiceJob) {
                loadAtEnd = loadAtEnd.add(j.getSize());
            }
        }
        stateManager.putTypedInternalRouteState(route, InternalStates.LOAD_AT_BEGINNING, loadAtDepot);
        stateManager.putTypedInternalRouteState(route, InternalStates.LOAD_AT_END, loadAtEnd);
    }

    @Override
    public void informInsertionStarts(Collection<VehicleRoute> vehicleRoutes, Collection<Job> unassignedJobs) {
        for (VehicleRoute route : vehicleRoutes) {
            insertionStarts(route);
        }
    }

    @Override
    public void informJobInserted(Job job2insert, VehicleRoute inRoute, double additionalCosts, double additionalTime) {
        if (job2insert instanceof DeliveryJob) {
            SizeDimension loadAtDepot = stateManager.getRouteState(inRoute, InternalStates.LOAD_AT_BEGINNING, SizeDimension.class);
            if (loadAtDepot == null) {
                loadAtDepot = defaultValue;
            }
            stateManager.putTypedInternalRouteState(inRoute, InternalStates.LOAD_AT_BEGINNING,
                            loadAtDepot.add(job2insert.getSize()));
        } else if (job2insert instanceof PickupJob || job2insert instanceof ServiceJob) {
            SizeDimension loadAtEnd = stateManager.getRouteState(inRoute, InternalStates.LOAD_AT_END, SizeDimension.class);
            if (loadAtEnd == null) {
                loadAtEnd = defaultValue;
            }
            stateManager.putTypedInternalRouteState(inRoute, InternalStates.LOAD_AT_END,
                            loadAtEnd.add(job2insert.getSize()));
        }
    }

    public void informRouteChanged(VehicleRoute route) {
        insertionStarts(route);
    }


}
