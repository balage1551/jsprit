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
package com.graphhopper.jsprit.core.algorithm.recreate;

import com.graphhopper.jsprit.core.problem.driver.Driver;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data object that collects insertion information. It collects insertionCosts, insertionIndeces, vehicle and driver to be employed
 * and departureTime of vehicle at vehicle's start location (e.g. depot).
 *
 * @author stefan
 */
public class InsertionData {

    public static class NoInsertionFound extends InsertionData {

        public NoInsertionFound() {
            super(Double.MAX_VALUE, NO_INDEX, NO_INDEX, null, null);
        }

    }

    private static InsertionData noInsertion = new NoInsertionFound();

    /**
     * Returns an instance of InsertionData that represents an EmptyInsertionData (which might indicate
     * that no insertion has been found). It is internally instantiated as follows:<br>
     * <code>new InsertionData(Double.MAX_VALUE, NO_INDEX, NO_INDEX, null, null);</code><br>
     * where NO_INDEX=-1.
     *
     * @return
     */
    public static InsertionData createEmptyInsertionData() {
        return noInsertion;
    }

    static int NO_INDEX = -1;

    private final double insertionCost;

    private int pickupInsertionIndex;

    private int deliveryInsertionIndex;

    private final Vehicle selectedVehicle;

    private final Driver selectedDriver;

    private double departureTime;

    private double additionalTime;

    private List<Event> events = new ArrayList<>();

    List<Event> getEvents() {
        return events;
    }

    public List<Event> getUnmodifiableEvents() {
        return Collections.unmodifiableList(events);
    }

    public <T extends Event> List<T> getUnmodifiableEventsByType(Class<T> eventType) {
        List<T> events = new ArrayList<>();
        for (Event e : this.events) {
            if (e.getClass().equals(eventType)) {
                events.add(eventType.cast(e));
            }
        }
        return events;
    }

    /**
     * @return the additionalTime
     */
    public double getAdditionalTime() {
        return additionalTime;
    }

    /**
     * @param additionalTime the additionalTime to set
     */
    @Deprecated
    public void setAdditionalTime(double additionalTime) {
        this.additionalTime = additionalTime;
    }

    @Deprecated
    public InsertionData(double insertionCost, int pickupInsertionIndex, int deliveryInsertionIndex, Vehicle vehicle, Driver driver) {
        this.insertionCost = insertionCost;
        this.pickupInsertionIndex = pickupInsertionIndex;
        this.deliveryInsertionIndex = deliveryInsertionIndex;
        this.selectedVehicle = vehicle;
        this.selectedDriver = driver;
    }

    public InsertionData(double insertionCost, double vehicleDepartureTime, Vehicle selectedVehicle, Driver selectedDriver) {
        this.insertionCost = insertionCost;
        this.selectedVehicle = selectedVehicle;
        this.selectedDriver = selectedDriver;
        this.departureTime = vehicleDepartureTime;
    }

    @Override
    public String toString() {
        return "[iCost=" + insertionCost + "][pickupIndex=" + pickupInsertionIndex + "][deliveryIndex=" + deliveryInsertionIndex + "][depTime=" + departureTime + "][vehicle=" + selectedVehicle + "][driver=" + selectedDriver + "]";
    }

    /**
     * Returns insertionIndex of deliveryActivity. If no insertionPosition is found, it returns NO_INDEX (=-1).
     *
     * @return
     */
    @Deprecated
    public int getDeliveryInsertionIndex() {
        return deliveryInsertionIndex;
    }

    /**
     * Returns insertionIndex of pickkupActivity. If no insertionPosition is found, it returns NO_INDEX (=-1).
     *
     * @return
     */
    @Deprecated
    public int getPickupInsertionIndex() {
        return pickupInsertionIndex;
    }

    /**
     * Returns insertion costs (which might be the additional costs of inserting the corresponding job).
     *
     * @return
     */
    public double getInsertionCost() {
        return insertionCost;
    }

    /**
     * Returns the vehicle to be employed.
     *
     * @return
     */
    public Vehicle getSelectedVehicle() {
        return selectedVehicle;
    }

    /**
     * Returns the vehicle to be employed.
     *
     * @return
     */
    public Driver getSelectedDriver() {
        return selectedDriver;
    }

    /**
     * @return the departureTime
     */
    public double getVehicleDepartureTime() {
        return departureTime;
    }

    /**
     * @param departureTime the departureTime to set
     */
    @Deprecated
    public void setVehicleDepartureTime(double departureTime) {
        this.departureTime = departureTime;
    }


}
