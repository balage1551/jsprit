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
package com.graphhopper.jsprit.core.problem.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.graphhopper.jsprit.core.problem.AbstractActivity;
import com.graphhopper.jsprit.core.problem.AbstractJob;
import com.graphhopper.jsprit.core.problem.Capacity;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.SelfJobActivityFactory;
import com.graphhopper.jsprit.core.problem.Skills;
import com.graphhopper.jsprit.core.problem.solution.route.activity.BackhaulReturnShipment;
import com.graphhopper.jsprit.core.problem.solution.route.activity.DeliverReturnShipment;
import com.graphhopper.jsprit.core.problem.solution.route.activity.PickupReturnShipment;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindowsImpl;


/**
 * TODO Shipment is an implementation of Job and consists of a pickup and a
 * delivery of something.
 * <p>
 * <p>
 * It distinguishes itself from {@link Service} as two locations are involved a
 * pickup where usually something is loaded to the transport unit and a delivery
 * where something is unloaded.
 * <p>
 * <p>
 * By default serviceTimes of both pickup and delivery is 0.0 and timeWindows of
 * both is [0.0, Double.MAX_VALUE],
 * <p>
 * <p>
 * A shipment can be built with a builder. You can get an instance of the
 * builder by coding <code>Shipment.Builder.newInstance(...)</code>. This way
 * you can specify the shipment. Once you build the shipment, it is immutable,
 * i.e. fields/attributes cannot be changed anymore and you can only 'get' the
 * specified values.
 * <p>
 * <p>
 * Note that two shipments are equal if they have the same id.
 *
 * @author balage
 */
public class ReturnShipment extends AbstractJob implements SelfJobActivityFactory {


    /**
     * Builder that builds the shipment.
     *
     * @author schroeder
     */
    public static class Builder {

        private String id;

        private double pickupServiceTime = 0.0;

        private double backhaulPickupServiceTime = 0.0;

        private double deliveryServiceTime = 0.0;

        private double backhaulDeliveryServiceTime = 0.0;

        private TimeWindow deliveryTimeWindow = TimeWindow.newInstance(0.0, Double.MAX_VALUE);

        private TimeWindow pickupTimeWindow = TimeWindow.newInstance(0.0, Double.MAX_VALUE);

        private TimeWindow backhaulTimeWindow = TimeWindow.newInstance(0.0, Double.MAX_VALUE);

        private Capacity.Builder capacityBuilder = Capacity.Builder.newInstance();

        private Capacity capacity;

        private Skills.Builder skillBuilder = Skills.Builder.newInstance();

        private Skills skills;

        private String name = "no-name";

        private Location pickupLocation_;

        private Location deliveryLocation_;

        private Location backhaulLocation_;

        private TimeWindowsImpl pickupTimeWindows;
        protected TimeWindowsImpl deliveryTimeWindows;
        private TimeWindowsImpl backhaulTimeWindows;

        private boolean pickupTimeWindowAdded = false;
        private boolean deliveryTimeWindowAdded = false;
        private boolean backhaulTimeWindowAdded = false;

        private int priority = 2;

        /**
         * Returns new instance of this builder.
         *
         * @param id
         *            the id of the shipment which must be a unique identifier
         *            among all jobs
         * @return the builder
         */
        public static Builder newInstance(String id) {
            return new Builder(id);
        }

        Builder(String id) {
            if (id == null) {
                throw new IllegalArgumentException("id must not be null");
            }
            this.id = id;
            pickupTimeWindows = new TimeWindowsImpl();
            pickupTimeWindows.add(pickupTimeWindow);
            deliveryTimeWindows = new TimeWindowsImpl();
            deliveryTimeWindows.add(deliveryTimeWindow);
            backhaulTimeWindows = new TimeWindowsImpl();
            backhaulTimeWindows.add(backhaulTimeWindow);
        }

        /**
         * Sets pickup location.
         *
         * <p>
         * If no <code>backhaulLocation</code> is specified, it will be same as
         * the pickupLocation.
         * </p>
         *
         * @param pickupLocation
         *            pickup location
         * @return builder
         */
        public Builder setPickupLocation(Location pickupLocation) {
            pickupLocation_ = pickupLocation;
            if (backhaulLocation_ == null) {
                backhaulLocation_ = pickupLocation;
            }
            return this;
        }

        /**
         * Sets pickupServiceTime.
         * <p>
         * <p>
         * ServiceTime is intended to be the time the implied activity takes at
         * the pickup-location.
         *
         * @param serviceTime
         *            the service time / duration the pickup of the associated
         *            shipment takes
         * @return builder
         * @throws IllegalArgumentException
         *             if servicTime < 0.0
         */
        public Builder setPickupServiceTime(double serviceTime) {
            if (serviceTime < 0.0) {
                throw new IllegalArgumentException("serviceTime must not be < 0.0");
            }
            pickupServiceTime = serviceTime;
            return this;
        }

        /**
         * Sets the timeWindow for the pickup, i.e. the time-period in which a
         * pickup operation is allowed to START.
         * <p>
         * <p>
         * By default timeWindow is [0.0, Double.MAX_VALUE}
         *
         * @param timeWindow
         *            the time window within the pickup operation/activity can
         *            START
         * @return builder
         * @throws IllegalArgumentException
         *             if timeWindow is null
         */
        public Builder setPickupTimeWindow(TimeWindow timeWindow) {
            if (timeWindow == null) {
                throw new IllegalArgumentException("delivery time-window must not be null");
            }
            pickupTimeWindow = timeWindow;
            pickupTimeWindows = new TimeWindowsImpl();
            pickupTimeWindows.add(timeWindow);
            return this;
        }



        /**
         * Sets delivery location.
         *
         * @param deliveryLocation
         *            delivery location
         * @return builder
         */
        public Builder setDeliveryLocation(Location deliveryLocation) {
            deliveryLocation_ = deliveryLocation;
            return this;
        }

        /**
         * Sets the delivery service-time.
         * <p>
         * <p>
         * ServiceTime is intended to be the time the implied activity takes at
         * the delivery-location.
         *
         * @param deliveryServiceTime
         *            the service time / duration of shipment's delivery
         * @return builder
         * @throws IllegalArgumentException
         *             if serviceTime < 0.0
         */
        public Builder setDeliveryServiceTime(double deliveryServiceTime) {
            if (deliveryServiceTime < 0.0) {
                throw new IllegalArgumentException("deliveryServiceTime must not be < 0.0");
            }
            this.deliveryServiceTime = deliveryServiceTime;
            return this;
        }

        /**
         * Sets the timeWindow for the delivery, i.e. the time-period in which a
         * delivery operation is allowed to start.
         * <p>
         * <p>
         * By default timeWindow is [0.0, Double.MAX_VALUE}
         *
         * @param timeWindow
         *            the time window within the associated delivery is allowed
         *            to START
         * @return builder
         * @throws IllegalArgumentException
         *             if timeWindow is null
         */
        public Builder setDeliveryTimeWindow(TimeWindow timeWindow) {
            if (timeWindow == null) {
                throw new IllegalArgumentException("delivery time-window must not be null");
            }
            deliveryTimeWindow = timeWindow;
            deliveryTimeWindows = new TimeWindowsImpl();
            deliveryTimeWindows.add(timeWindow);
            return this;
        }

        /**
         * Sets backhaul location.
         *
         * <p>
         * If no <code>backhaulLocation</code> is specified, it will be same as
         * the pickupLocation.
         * </p>
         *
         * @param backhaulLocation
         *            backhaul location
         * @return builder
         */
        public Builder setBackhaulLocation(Location backhaulLocation) {
            backhaulLocation_ = backhaulLocation;
            return this;
        }

        /**
         * Sets backhaulPickupServiceTime.
         * <p>
         * ServiceTime is intended to be the time the implied activity takes at
         * the backhaul-location.
         *
         * @param serviceTime
         *            the service time / duration of picking up the backhaul
         *            cargo of the associated shipment takes
         * @return builder
         * @throws IllegalArgumentException
         *             if servicTime < 0.0
         */
        public Builder setBackhaulPickupServiceTime(double serviceTime) {
            if (serviceTime < 0.0) {
                throw new IllegalArgumentException("serviceTime must not be < 0.0");
            }
            backhaulPickupServiceTime = serviceTime;
            return this;
        }

        /**
         * Sets backhaulDeliveryServiceTime.
         * <p>
         * ServiceTime is intended to be the time the implied activity takes at
         * the backhaul-location.
         *
         * @param serviceTime
         *            the service time / duration of picking up the backhaul
         *            cargo of the associated shipment takes
         * @return builder
         * @throws IllegalArgumentException
         *             if servicTime < 0.0
         */
        public Builder setBackhaulDeliveryServiceTime(double serviceTime) {
            if (serviceTime < 0.0) {
                throw new IllegalArgumentException("serviceTime must not be < 0.0");
            }
            backhaulDeliveryServiceTime = serviceTime;
            return this;
        }


        /**
         * Sets the timeWindow for the pickup, i.e. the time-period in which a
         * pickup operation is allowed to START.
         * <p>
         * <p>
         * By default timeWindow is [0.0, Double.MAX_VALUE}
         *
         * @param timeWindow
         *            the service time / duration of delivering the backhaul
         *            cargo of the associated shipment takes
         * @return builder
         * @throws IllegalArgumentException
         *             if timeWindow is null
         */
        public Builder setBackhaulTimeWindow(TimeWindow timeWindow) {
            if (timeWindow == null) {
                throw new IllegalArgumentException("delivery time-window must not be null");
            }
            backhaulTimeWindow = timeWindow;
            backhaulTimeWindows = new TimeWindowsImpl();
            backhaulTimeWindows.add(timeWindow);
            return this;
        }



        /**
         * Adds capacity dimension.
         *
         * @param dimensionIndex
         *            the dimension index of the corresponding capacity value
         * @param dimensionValue
         *            the capacity value
         * @return builder
         * @throws IllegalArgumentException
         *             if dimVal < 0
         */
        public Builder addSizeDimension(int dimensionIndex, int dimensionValue) {
            if (dimensionValue < 0) {
                throw new IllegalArgumentException("capacity value cannot be negative");
            }
            capacityBuilder.addDimension(dimensionIndex, dimensionValue);
            return this;
        }

        public Builder addRequiredSkill(String skill) {
            skillBuilder.addSkill(skill);
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder addDeliveryTimeWindow(TimeWindow timeWindow) {
            if (timeWindow == null) {
                throw new IllegalArgumentException("time-window arg must not be null");
            }
            if (!deliveryTimeWindowAdded) {
                deliveryTimeWindows = new TimeWindowsImpl();
                deliveryTimeWindowAdded = true;
            }
            deliveryTimeWindows.add(timeWindow);
            return this;
        }

        public Builder addDeliveryTimeWindow(double earliest, double latest) {
            addDeliveryTimeWindow(TimeWindow.newInstance(earliest, latest));
            return this;
        }

        public Builder addPickupTimeWindow(TimeWindow timeWindow) {
            if (timeWindow == null) {
                throw new IllegalArgumentException("time-window arg must not be null");
            }
            if (!pickupTimeWindowAdded) {
                pickupTimeWindows = new TimeWindowsImpl();
                pickupTimeWindowAdded = true;
            }
            pickupTimeWindows.add(timeWindow);
            return this;
        }

        public Builder addPickupTimeWindow(double earliest, double latest) {
            return addPickupTimeWindow(TimeWindow.newInstance(earliest, latest));
        }


        public Builder addBackhaulTimeWindow(TimeWindow timeWindow) {
            if (timeWindow == null) {
                throw new IllegalArgumentException("time-window arg must not be null");
            }
            if (!backhaulTimeWindowAdded) {
                backhaulTimeWindows = new TimeWindowsImpl();
                backhaulTimeWindowAdded = true;
            }
            backhaulTimeWindows.add(timeWindow);
            return this;
        }

        public Builder addBackhaulTimeWindow(double earliest, double latest) {
            addBackhaulTimeWindow(TimeWindow.newInstance(earliest, latest));
            return this;
        }


        /**
         * Set priority to shipment. Only 1 = high priority, 2 = medium and 3 =
         * low are allowed.
         * <p>
         * Default is 2 = medium.
         *
         * @param priority
         * @return builder
         */
        public Builder setPriority(int priority) {
            if (priority < 1 || priority > 3) {
                throw new IllegalArgumentException("incorrect priority. only 1 = high, 2 = medium and 3 = low is allowed");
            }
            this.priority = priority;
            return this;
        }

        /**
         * Builds the shipment.
         *
         * @return shipment
         * @throws IllegalArgumentException
         *             if neither pickup-location nor pickup-coord is set or if
         *             neither delivery-location nor delivery-coord is set
         */
        public ReturnShipment build() {
            if (pickupLocation_ == null) {
                throw new IllegalArgumentException("pickup location is missing");
            }
            if (deliveryLocation_ == null) {
                throw new IllegalArgumentException("delivery location is missing");
            }
            capacity = capacityBuilder.build();
            skills = skillBuilder.build();
            return new ReturnShipment(this);
        }

    }

    private final String id;

    private final double pickupServiceTime;

    private final double deliveryServiceTime;

    private final double backhaulPickupServiceTime;

    private final double backhaulDeliveryServiceTime;

    private final TimeWindow deliveryTimeWindow;

    private final TimeWindow pickupTimeWindow;

    private final TimeWindow backhaulTimeWindow;

    private final Capacity capacity;

    private final Skills skills;

    private final String name;

    private final Location pickupLocation_;

    private final Location deliveryLocation_;

    private final Location backhaulLocation_;

    private final TimeWindowsImpl deliveryTimeWindows;

    private final TimeWindowsImpl pickupTimeWindows;

    private final TimeWindowsImpl backhaulTimeWindows;

    private final int priority;

    ReturnShipment(Builder builder) {
        id = builder.id;
        pickupServiceTime = builder.pickupServiceTime;
        pickupTimeWindow = builder.pickupTimeWindow;
        deliveryServiceTime = builder.deliveryServiceTime;
        deliveryTimeWindow = builder.deliveryTimeWindow;
        backhaulPickupServiceTime = builder.backhaulPickupServiceTime;
        backhaulDeliveryServiceTime = builder.backhaulDeliveryServiceTime;
        backhaulTimeWindow = builder.backhaulTimeWindow;

        capacity = builder.capacity;
        skills = builder.skills;
        name = builder.name;
        pickupLocation_ = builder.pickupLocation_;
        deliveryLocation_ = builder.deliveryLocation_;
        backhaulLocation_ = builder.backhaulLocation_;

        deliveryTimeWindows = builder.deliveryTimeWindows;
        pickupTimeWindows = builder.pickupTimeWindows;
        backhaulTimeWindows = builder.backhaulTimeWindows;

        priority = builder.priority;

        addLocation(pickupLocation_);
        addLocation(deliveryLocation_);
        addLocation(backhaulLocation_);
    }

    @Override
    public String getId() {
        return id;
    }

    public Location getPickupLocation() {
        return pickupLocation_;
    }

    /**
     * Returns the pickup service-time.
     * <p>
     * <p>
     * By default service-time is 0.0.
     *
     * @return service-time
     */
    public double getPickupServiceTime() {
        return pickupServiceTime;
    }

    public Location getDeliveryLocation() {
        return deliveryLocation_;
    }

    /**
     * Returns service-time of delivery.
     *
     * @return service-time of delivery
     */
    public double getDeliveryServiceTime() {
        return deliveryServiceTime;
    }

    /**
     * Returns the time-window of delivery.
     *
     * @return time-window of delivery
     */
    public TimeWindow getDeliveryTimeWindow() {
        return deliveryTimeWindows.getTimeWindows().iterator().next();
    }

    public Collection<TimeWindow> getDeliveryTimeWindows() {
        return deliveryTimeWindows.getTimeWindows();
    }

    public Location getBackhaulLocation() {
        return backhaulLocation_;
    }

    /**
     * Returns the time-window of backhaul delivery.
     *
     * @return time-window of bachaul delivery
     */
    public TimeWindow getBackhaulDeliveryTimeWindow() {
        return backhaulTimeWindows.getTimeWindows().iterator().next();
    }

    public Collection<TimeWindow> getBackhaulDeliveryTimeWindows() {
        return backhaulTimeWindows.getTimeWindows();
    }

    public double getBackhaulPickupServiceTime() {
        return backhaulPickupServiceTime;
    }

    public double getBackhaulDeliveryServiceTime() {
        return backhaulDeliveryServiceTime;
    }

    /**
     * Returns the time-window of pickup.
     *
     * @return time-window of pickup
     */
    public TimeWindow getPickupTimeWindow() {
        return pickupTimeWindows.getTimeWindows().iterator().next();
    }

    public Collection<TimeWindow> getPickupTimeWindows() {
        return pickupTimeWindows.getTimeWindows();
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * Two shipments are equal if they have the same id.
     *
     * @return true if shipments are equal (have the same id)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ReturnShipment other = (ReturnShipment) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public Capacity getSize() {
        return capacity;
    }

    @Override
    public Skills getRequiredSkills() {
        return skills;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Get priority of shipment. Only 1 = high priority, 2 = medium and 3 = low
     * are allowed.
     * <p>
     * Default is 2 = medium.
     *
     * @return priority
     */
    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public List<AbstractActivity> createActivities() {
        List<AbstractActivity> acts = new ArrayList<AbstractActivity>();
        acts.add(new PickupReturnShipment(this));
        acts.add(new DeliverReturnShipment(this));
        acts.add(new BackhaulReturnShipment(this));
        return acts;
    }

}
