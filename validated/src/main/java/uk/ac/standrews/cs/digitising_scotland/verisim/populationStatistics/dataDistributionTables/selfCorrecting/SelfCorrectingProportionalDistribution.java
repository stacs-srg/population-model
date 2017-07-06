/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
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
package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.selfCorrecting;


import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.DataDistribution;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.DeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys.StatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.IntegerRangeToDoubleSet;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.IntegerRangeToIntegerSet;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.LabeledValueSet;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.InvalidRangeException;


import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class SelfCorrectingProportionalDistribution implements DataDistribution {

    // The integer range here represents the row labels (i.e. the age ranges on the ordered birth table)
    private Map<IntegerRange, LabeledValueSet<IntegerRange, Double>> targetProportions;
    private Map<IntegerRange, LabeledValueSet<IntegerRange, Integer>> achievedCounts;

    private YearDate year;
    private String sourcePopulation;
    private String sourceOrganisation;

    public SelfCorrectingProportionalDistribution(YearDate year, String sourcePopulation, String sourceOrganisation, Map<IntegerRange, LabeledValueSet<IntegerRange, Double>> targetProportions) {
        this.year = year;
        this.sourceOrganisation = sourceOrganisation;
        this.sourcePopulation = sourcePopulation;
        this.targetProportions = targetProportions;

        this.achievedCounts = new HashMap<>();

        for(IntegerRange iR : targetProportions.keySet()) {
            achievedCounts.put(iR, new IntegerRangeToIntegerSet(targetProportions.get(iR).getLabels(), 0));
        }
    }

    public MultipleDeterminedCount determineCount(StatsKey key) {

        int age = key.getYLabel();

        LabeledValueSet<IntegerRange, Integer> achievedCountsForAge;
        try {
            achievedCountsForAge = achievedCounts.get(resolveRowValue(age));
        } catch (InvalidRangeException e) {
            // If no stats in distribution for the given key then return a zero count object
            return new MultipleDeterminedCount(key,
                    new IntegerRangeToIntegerSet(Collections.singleton(new IntegerRange(1)), 0),
                    new IntegerRangeToDoubleSet(Collections.singleton(new IntegerRange(1)), 0.0));
        }

        LabeledValueSet<IntegerRange, Double> rawValues;

        if(key.performSelfCorrection()) {

            Integer sumOfAC = achievedCountsForAge.getSumOfValues();
            Double totalCount = sumOfAC + key.getForNPeople();

            rawValues =
                    targetProportions.get(resolveRowValue(age))
                            .productOfValuesAndN(totalCount)
                            .valuesSubtractValues(achievedCountsForAge);

        } else {
            rawValues =
                    targetProportions.get(resolveRowValue(age))
                            .productOfValuesAndN(key.getForNPeople());
        }

        LabeledValueSet<IntegerRange, Integer> retValues = rawValues.controlledRoundingMaintainingSum();

        return new MultipleDeterminedCount(key, retValues, rawValues);
    }

    public void returnAchievedCount(DeterminedCount<LabeledValueSet<IntegerRange, Integer>, LabeledValueSet<IntegerRange, Double>> achievedCount) {

        int age = achievedCount.getKey().getYLabel();
        LabeledValueSet<IntegerRange, Integer> previousAchievedCountsForAge;

        try {
            previousAchievedCountsForAge = achievedCounts.get(resolveRowValue(age));
        } catch (InvalidRangeException e) {
            return;
        }

        LabeledValueSet<IntegerRange, Integer> newAchievedCountsForAge = achievedCount.getFufilledCount();

        LabeledValueSet<IntegerRange, Integer> summedAchievedCountsForAge = previousAchievedCountsForAge
                .valuesPlusValues(newAchievedCountsForAge).floorValues();

        achievedCounts.replace(resolveRowValue(age), previousAchievedCountsForAge, summedAchievedCountsForAge);

    }


    @Override
    public YearDate getYear() {
        return year;
    }

    @Override
    public String getSourcePopulation() {
        return sourcePopulation;
    }

    @Override
    public String getSourceOrganisation() {
        return sourceOrganisation;
    }

    @Override
    public int getSmallestLabel() {
        int min = Integer.MAX_VALUE;
        for (IntegerRange iR : targetProportions.keySet()) {
            int v = iR.getMin();
            if (v < min) {
                min = v;
            }
        }
        return min;
    }

    @Override
    public IntegerRange getLargestLabel() {
        IntegerRange max = null;
        int maxV = Integer.MIN_VALUE;
        for (IntegerRange iR : targetProportions.keySet()) {
            int v = iR.getMax();
            if (v > maxV) {
                max = iR;
                maxV = v;
            }
        }
        return max;

    }

    private IntegerRange resolveRowValue(Integer rowValue) {

        for (IntegerRange iR : targetProportions.keySet()) {
            if (iR.contains(rowValue)) {
                return iR;
            }
        }

        throw new InvalidRangeException("Given value not covered by rows - value " + rowValue);
    }

}
