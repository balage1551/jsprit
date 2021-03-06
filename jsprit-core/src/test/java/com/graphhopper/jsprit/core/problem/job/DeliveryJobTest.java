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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.SizeDimension;

public class DeliveryJobTest {

    @Test(expected = IllegalArgumentException.class)
    public void whenNeitherLocationIdNorCoordIsSet_itThrowsException() {
        new DeliveryJob.Builder("p").build();
    }

    @Test
    public void whenAddingTwoCapDimension_nuOfDimsShouldBeTwo() {
        DeliveryJob one = new DeliveryJob.Builder("s").setLocation(Location.newInstance("foofoo"))
                        .addSizeDimension(0, 2)
                        .addSizeDimension(1, 4)
                        .build();
        SizeDimension size = one.getActivity().getLoadChange().abs();
        assertEquals(2, size.getNuOfDimensions());
        assertEquals(2, size.get(0));
        assertEquals(4, size.get(1));

    }

    @Test
    public void sizeAtStartAndEndShouldBeCorrect() {
        DeliveryJob one = new DeliveryJob.Builder("s").setLocation(Location.newInstance("foofoo"))
                        .addSizeDimension(0, 2)
                        .addSizeDimension(1, 4)
                        .build();
        SizeDimension size = one.getActivity().getLoadChange().abs();
        assertEquals(size, one.getSizeAtStart());
        assertEquals(SizeDimension.Builder.newInstance().addDimension(0, 0).addDimension(1, 0)
                        .build(), one.getSizeAtEnd());
    }

    @Test
    public void whenPickupIsBuiltWithoutSpecifyingCapacity_itShouldHvCapWithOneDimAndDimValOfZero() {
        DeliveryJob one = new DeliveryJob.Builder("s").setLocation(Location.newInstance("foofoo"))
                        .build();
        SizeDimension size = one.getActivity().getLoadChange();
        assertEquals(1, size.getNuOfDimensions());
        assertEquals(0, size.get(0));
    }

    @Test
    public void whenPickupIsBuiltWithConstructorWhereSizeIsSpecified_capacityShouldBeSetCorrectly() {
        DeliveryJob one = new DeliveryJob.Builder("s").addSizeDimension(0, 1).setLocation(Location.newInstance("foofoo"))
                        .build();
        SizeDimension size = one.getActivity().getLoadChange().abs();
        assertEquals(1, size.getNuOfDimensions());
        assertEquals(1, size.get(0));
    }

    @Test
    public void whenAddingSkills_theyShouldBeAddedCorrectly() {
        DeliveryJob s = new DeliveryJob.Builder("s").setLocation(Location.newInstance("loc"))
                        .addRequiredSkill("drill").addRequiredSkill("screwdriver").build();
        assertTrue(s.getRequiredSkills().containsSkill("drill"));
        assertTrue(s.getRequiredSkills().containsSkill("ScrewDriver"));
    }

    @Test
    public void whenAddingSkillsCaseSens_theyShouldBeAddedCorrectly() {
        DeliveryJob s = new DeliveryJob.Builder("s").setLocation(Location.newInstance("loc"))
                        .addRequiredSkill("DriLl").addRequiredSkill("screwDriver").build();
        assertTrue(s.getRequiredSkills().containsSkill("drill"));
        assertTrue(s.getRequiredSkills().containsSkill("drilL"));
    }

    @Test
    public void whenAddingSkillsCaseSensV2_theyShouldBeAddedCorrectly() {
        DeliveryJob s = new DeliveryJob.Builder("s").setLocation(Location.newInstance("loc"))
                        .addRequiredSkill("screwDriver").build();
        assertFalse(s.getRequiredSkills().containsSkill("drill"));
        assertFalse(s.getRequiredSkills().containsSkill("drilL"));
    }

    @Test
    public void nameShouldBeAssigned() {
        DeliveryJob s = new DeliveryJob.Builder("s").setLocation(Location.newInstance("loc"))
                        .setName("name").build();
        assertEquals("name", s.getName());
    }

    @Test
    public void whenSettingPriorities_itShouldBeSetCorrectly() {
        DeliveryJob s = new DeliveryJob.Builder("s").setLocation(Location.newInstance("loc"))
                        .setPriority(3).build();
        assertEquals(3, s.getPriority());
    }

    @Test
    public void whenNotSettingPriorities_defaultShouldBe() {
        DeliveryJob s = new DeliveryJob.Builder("s").setLocation(Location.newInstance("loc"))
                        .build();
        assertEquals(2, s.getPriority());
    }


}
