/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.basic_model.analytic;

import uk.ac.standrews.cs.basic_model.model.IPopulation;
import uk.ac.standrews.cs.basic_model.model.in_memory.CompactPopulationAdapter;

/**
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * 
 * Tests of population operations.
 */
public class PopulationAnalyticsTestManual {

    public static void main(final String[] args) throws Exception {

        final int population_size = 1000;
        final IPopulation population = new CompactPopulationAdapter(population_size);

        new PopulationAnalytics(population).printAllAnalytics();
        new MarriageAnalytics(population).printAllAnalytics();
        new ChildrenAnalytics(population).printAllAnalytics();
        new DeathAnalytics(population).printAllAnalytics();
    }
}