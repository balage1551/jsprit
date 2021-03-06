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
package com.graphhopper.jsprit.core.algorithm.ruin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.graphhopper.jsprit.core.algorithm.ruin.distance.CoordinateJobDistance;
import com.graphhopper.jsprit.core.algorithm.ruin.distance.JobDistance;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.ServiceJob;


public class JobNeighborhoodsImplTest {

    VehicleRoutingProblem vrp;

    JobDistance jobDistance;

    ServiceJob target;
    ServiceJob s2;
    ServiceJob s3;
    ServiceJob s4;
    ServiceJob s5;
    ServiceJob s6;
    ServiceJob s7;

    @Before
    public void doBefore() {
        VehicleRoutingProblem.Builder builder = VehicleRoutingProblem.Builder.newInstance();
        target = new ServiceJob.Builder("s1").addSizeDimension(0, 1).setLocation(Location.newInstance(0, 5)).build();
        s2 = new ServiceJob.Builder("s2").addSizeDimension(0, 1).setLocation(Location.newInstance(0, 4)).build();
        s3 = new ServiceJob.Builder("s3").addSizeDimension(0, 1).setLocation(Location.newInstance(0, 3)).build();
        s4 = new ServiceJob.Builder("s4").addSizeDimension(0, 1).setLocation(Location.newInstance(0, 2)).build();

        s5 = new ServiceJob.Builder("s5").addSizeDimension(0, 1).setLocation(Location.newInstance(0, 6)).build();
        s6 = new ServiceJob.Builder("s6").addSizeDimension(0, 1).setLocation(Location.newInstance(0, 7)).build();
        s7 = new ServiceJob.Builder("s7").addSizeDimension(0, 1).setLocation(Location.newInstance(0, 8)).build();

        vrp = builder.addJob(target).addJob(s2).addJob(s3).addJob(s4).addJob(s5).addJob(s6).addJob(s7).build();

        jobDistance = new CoordinateJobDistance();
    }

    @Test
    public void whenRequestingNeighborhoodOfTargetJob_nNeighborsShouldBeTwo() {
        JobNeighborhoodsImpl jn = new JobNeighborhoodsImpl(vrp, jobDistance);
        jn.initialise();
        Iterator<Job> iter = jn.getNearestNeighborsIterator(2, target);
        List<ServiceJob> services = new ArrayList<ServiceJob>();
        while (iter.hasNext()) {
            services.add((ServiceJob) iter.next());
        }
        assertEquals(2, services.size());
    }

    @Test
    public void whenRequestingNeighborhoodOfTargetJob_s2ShouldBeNeighbor() {
        JobNeighborhoodsImpl jn = new JobNeighborhoodsImpl(vrp, jobDistance);
        jn.initialise();
        Iterator<Job> iter = jn.getNearestNeighborsIterator(2, target);
        List<ServiceJob> services = new ArrayList<ServiceJob>();
        while (iter.hasNext()) {
            services.add((ServiceJob) iter.next());
        }
        assertTrue(services.contains(s2));
    }

    @Test
    public void whenRequestingNeighborhoodOfTargetJob_s4ShouldBeNeighbor() {
        JobNeighborhoodsImpl jn = new JobNeighborhoodsImpl(vrp, jobDistance);
        jn.initialise();
        Iterator<Job> iter = jn.getNearestNeighborsIterator(2, target);
        List<ServiceJob> services = new ArrayList<ServiceJob>();
        while (iter.hasNext()) {
            services.add((ServiceJob) iter.next());
        }
        assertTrue(services.contains(s5));
    }

    @Test
    public void whenRequestingNeighborhoodOfTargetJob_sizeShouldBe4() {
        JobNeighborhoodsImpl jn = new JobNeighborhoodsImpl(vrp, jobDistance);
        jn.initialise();
        Iterator<Job> iter = jn.getNearestNeighborsIterator(4, target);
        List<ServiceJob> services = new ArrayList<ServiceJob>();
        while (iter.hasNext()) {
            services.add((ServiceJob) iter.next());
        }
        assertEquals(4, services.size());
    }

    @Test
    public void whenRequestingMoreNeighborsThanExisting_itShouldReturnMaxNeighbors() {
        JobNeighborhoodsImpl jn = new JobNeighborhoodsImpl(vrp, jobDistance);
        jn.initialise();
        Iterator<Job> iter = jn.getNearestNeighborsIterator(100, target);
        List<ServiceJob> services = new ArrayList<ServiceJob>();
        while (iter.hasNext()) {
            services.add((ServiceJob) iter.next());
        }
        assertEquals(6, services.size());
    }

}
