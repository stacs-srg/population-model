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

import uk.ac.standrews.cs.basic_model.model.IPerson;
import uk.ac.standrews.cs.basic_model.model.IPopulation;
import uk.ac.standrews.cs.utilities.ArrayManipulation;

import java.util.List;

/**
 * An analytic class to analyse the distribution of marriages.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class MarriageAnalytics {

    private static final int MAX_MARRIAGES = 10;
    private static final int ONE_HUNDRED = 100;

    private final int[] count_marriages = new int[MAX_MARRIAGES]; // tracks marriage size
    private final IPopulation population;

    /**
     * Creates an analytic instance to analyse marriages in a population.
     *
     * @param population the population to analyse
     */
    public MarriageAnalytics(final IPopulation population) {

        this.population = population;
        analyseMarriages();
    }

    /**
     * Prints out all analyses.
     */
    public void printAllAnalytics() {

        final int sum = ArrayManipulation.sum(count_marriages);

        System.out.println("Male mariage sizes:");
        System.out.println("\t unmarried: " + count_marriages[0]);

        for (int i = 1; i < count_marriages.length; i++) {
            if (count_marriages[i] != 0) {
                System.out.println("\t Married " + i + " times: " + count_marriages[i] + " = " + String.format("%.1f", count_marriages[i] / (double) sum * ONE_HUNDRED) + '%');
            }
        }
    }

    /**
     * Analyses marriages for the population.
     */
    public void analyseMarriages() {

        for (final IPerson person : population.getPeople()) {

            if (person.getSex() == IPerson.MALE) { // only look at Males to avoid counting marriages twice.

                final List<Integer> partnership_ids = person.getPartnerships();
                if (partnership_ids == null) {
                    count_marriages[0]++;
                } else {
                    count_marriages[partnership_ids.size()]++;
                }
            }
        }
    }
}