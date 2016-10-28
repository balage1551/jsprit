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

import com.graphhopper.jsprit.core.problem.AbstractActivity;
import com.graphhopper.jsprit.core.problem.AbstractJob;
import com.graphhopper.jsprit.core.problem.Capacity;

public abstract class AbstractDeliveryActivity<T extends AbstractJob> extends AbstractActivity implements DeliveryActivity {

    private T job;

    private Capacity capacity;

    private double arrTime;

    private double endTime;

    private double theoreticalEarliest = 0;

    private double theoreticalLatest = Double.MAX_VALUE;

    public AbstractDeliveryActivity(T job) {
        super();
        this.job = job;
        capacity = Capacity.invert(job.getSize());
    }

    protected AbstractDeliveryActivity(AbstractDeliveryActivity<T> deliveryActivity) {
        this.job = deliveryActivity.getJob();
        this.arrTime = deliveryActivity.getArrTime();
        this.endTime = deliveryActivity.getEndTime();
        capacity = deliveryActivity.getSize();
        setIndex(deliveryActivity.getIndex());
        this.theoreticalEarliest = deliveryActivity.getTheoreticalEarliestOperationStartTime();
        this.theoreticalLatest = deliveryActivity.getTheoreticalLatestOperationStartTime();
    }


    @Override
    public void setTheoreticalEarliestOperationStartTime(double earliest) {
        theoreticalEarliest = earliest;
    }

    @Override
    public void setTheoreticalLatestOperationStartTime(double latest) {
        theoreticalLatest = latest;
    }


    @Override
    public double getTheoreticalEarliestOperationStartTime() {
        return theoreticalEarliest;
    }

    @Override
    public double getTheoreticalLatestOperationStartTime() {
        return theoreticalLatest;
    }

    @Override
    public double getArrTime() {
        return arrTime;
    }

    @Override
    public double getEndTime() {
        return endTime;
    }

    @Override
    public void setArrTime(double arrTime) {
        this.arrTime = arrTime;
    }

    @Override
    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    @Override
    public T getJob() {
        return job;
    }

    @Override
    public String toString() {
        return "[type=" + getName() + "][locationId=" + getLocation().getId()
                + "][size=" + getSize().toString()
                + "][twStart=" + Activities.round(getTheoreticalEarliestOperationStartTime())
                + "][twEnd=" + Activities.round(getTheoreticalLatestOperationStartTime()) + "]";
    }

    @Override
    public Capacity getSize() {
        return capacity;
    }
}
