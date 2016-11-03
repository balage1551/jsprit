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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.graphhopper.jsprit.core.problem.AbstractJob;
import com.graphhopper.jsprit.core.problem.Capacity;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity.JobActivity;

public abstract class JobActivityBase implements JobActivity {

    private int index;

    private AbstractJob job;

    private Capacity capacity;

    private double arrTime;

    private double endTime;

    private double theoreticalEarliest = 0;

    private double theoreticalLatest = Double.MAX_VALUE;

    private String name;
    private double operationTime;
    private Location location;

    public JobActivityBase(AbstractJob job, String name, Location location, double operationTime, Capacity capacity) {
        super();
        this.job = job;
        this.name = name;
        this.location = location;
        this.operationTime = operationTime;
        this.capacity = capacity;
    }

    protected JobActivityBase(JobActivityBase sourceActivity) {
        job = sourceActivity.getJob();
        arrTime = sourceActivity.getArrTime();
        endTime = sourceActivity.getEndTime();
        capacity = sourceActivity.getSize();
        setIndex(sourceActivity.getIndex());
        theoreticalEarliest = sourceActivity.getTheoreticalEarliestOperationStartTime();
        theoreticalLatest = sourceActivity.getTheoreticalLatestOperationStartTime();
        name = sourceActivity.name;
        location = sourceActivity.location;
        operationTime = sourceActivity.getOperationTime();
    }

    @Override
    public int getIndex() {
        return index;
    }

    protected void setIndex(int index) {
        this.index = index;
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
    public AbstractJob getJob() {
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getOperationTime() {
        return operationTime;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public TourActivity duplicate() {
        // TODO - Balage1551 - It uses safe reflection. But this is reflection which is expensive, so
        // in case it is a bottlenect, this should be refactored
        try {
            @SuppressWarnings("unchecked")
            Constructor<? extends JobActivityBase> constructor = getClass().getConstructor(getClass());
            return constructor.newInstance(this);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

}
