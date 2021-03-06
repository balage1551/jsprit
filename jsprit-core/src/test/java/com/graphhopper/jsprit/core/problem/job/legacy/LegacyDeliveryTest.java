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
package com.graphhopper.jsprit.core.problem.job.legacy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.job.Delivery;

public class LegacyDeliveryTest {

    @Test(expected = IllegalArgumentException.class)
    public void whenNeitherLocationIdNorCoordIsSet_itThrowsException() {
        Delivery.Builder.newInstance("p").build();
    }

    @Test
    public void whenAddingTwoCapDimension_nuOfDimsShouldBeTwo() {
        Delivery one = Delivery.Builder.newInstance("s").setLocation(Location.newInstance("foofoo"))
                .addSizeDimension(0, 2)
                .addSizeDimension(1, 4)
                .build();
        assertEquals(2, one.getSize().getNuOfDimensions());
        assertEquals(2, one.getSize().get(0));
        assertEquals(4, one.getSize().get(1));

    }

    @Test
    public void whenPickupIsBuiltWithoutSpecifyingCapacity_itShouldHvCapWithOneDimAndDimValOfZero() {
        Delivery one = Delivery.Builder.newInstance("s").setLocation(Location.newInstance("foofoo"))
                .build();
        assertEquals(1, one.getSize().getNuOfDimensions());
        assertEquals(0, one.getSize().get(0));
    }

    @Test
    public void whenPickupIsBuiltWithConstructorWhereSizeIsSpecified_capacityShouldBeSetCorrectly() {
        Delivery one = Delivery.Builder.newInstance("s").addSizeDimension(0, 1).setLocation(Location.newInstance("foofoo"))
                .build();
        assertEquals(1, one.getSize().getNuOfDimensions());
        assertEquals(1, one.getSize().get(0));
    }

    @Test
    public void whenAddingSkills_theyShouldBeAddedCorrectly() {
        Delivery s = Delivery.Builder.newInstance("s").setLocation(Location.newInstance("loc"))
                .addRequiredSkill("drill").addRequiredSkill("screwdriver").build();
        assertTrue(s.getRequiredSkills().containsSkill("drill"));
        assertTrue(s.getRequiredSkills().containsSkill("ScrewDriver"));
    }

    @Test
    public void whenAddingSkillsCaseSens_theyShouldBeAddedCorrectly() {
        Delivery s = Delivery.Builder.newInstance("s").setLocation(Location.newInstance("loc"))
                .addRequiredSkill("DriLl").addRequiredSkill("screwDriver").build();
        assertTrue(s.getRequiredSkills().containsSkill("drill"));
        assertTrue(s.getRequiredSkills().containsSkill("drilL"));
    }

    @Test
    public void whenAddingSkillsCaseSensV2_theyShouldBeAddedCorrectly() {
        Delivery s = Delivery.Builder.newInstance("s").setLocation(Location.newInstance("loc"))
                .addRequiredSkill("screwDriver").build();
        assertFalse(s.getRequiredSkills().containsSkill("drill"));
        assertFalse(s.getRequiredSkills().containsSkill("drilL"));
    }

    @Test
    public void nameShouldBeAssigned() {
        Delivery s = Delivery.Builder.newInstance("s").setLocation(Location.newInstance("loc"))
                .setName("name").build();
        assertEquals("name", s.getName());
    }

    @Test
    public void whenSettingPriorities_itShouldBeSetCorrectly(){
        Delivery s = Delivery.Builder.newInstance("s").setLocation(Location.newInstance("loc"))
                .setPriority(3).build();
        Assert.assertEquals(3, s.getPriority());
    }

    @Test
    public void whenNotSettingPriorities_defaultShouldBe(){
        Delivery s = Delivery.Builder.newInstance("s").setLocation(Location.newInstance("loc"))
                .build();
        Assert.assertEquals(2, s.getPriority());
    }


    @Test
    public void whenSettingUserData_itIsAssociatedWithTheJob() {
        Delivery one = Delivery.Builder.newInstance("s").setLocation(Location.newInstance("loc"))
                .setUserData(new HashMap<String, Object>()).build();
        Delivery two = Delivery.Builder.newInstance("s2").setLocation(Location.newInstance("loc")).setUserData(42)
                .build();
        Delivery three = Delivery.Builder.newInstance("s3").setLocation(Location.newInstance("loc")).build();

        assertTrue(one.getUserData() instanceof Map);
        assertEquals(42, two.getUserData());
        assertNull(three.getUserData());
    }
}
