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
package com.graphhopper.jsprit.core.problem.solution.route.activity;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.job.ReturnShipment;

public final class PickupReturnShipment extends AbstractPickupActivity<ReturnShipment> {


    public PickupReturnShipment(ReturnShipment shipment) {
        super(shipment);
    }

    private PickupReturnShipment(PickupReturnShipment pickupShipmentActivity) {
        super(pickupShipmentActivity);
    }

    @Override
    public String getName() {
        return "pickupReturnShipment";
    }

    @Override
    public Location getLocation() {
        return getJob().getPickupLocation();
    }

    @Override
    public double getOperationTime() {
        return getJob().getPickupServiceTime();
    }

    @Override
    public TourActivity duplicate() {
        return new PickupReturnShipment(this);
    }

}
