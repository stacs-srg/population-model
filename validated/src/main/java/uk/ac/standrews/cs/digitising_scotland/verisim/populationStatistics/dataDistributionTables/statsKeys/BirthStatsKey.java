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
package uk.ac.standrews.cs.digitising_scotland.verisim.populationStatistics.dataDistributionTables.statsKeys;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.CompoundTimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class BirthStatsKey extends StatsKey {


    public BirthStatsKey(Integer age, Integer order, int forNPeople, CompoundTimeUnit consideredTimePeriod, Date currentDate) {
        this(order, age, forNPeople, consideredTimePeriod, currentDate, true);
    }

    public BirthStatsKey(Integer age, Integer order, int forNPeople, CompoundTimeUnit consideredTimePeriod, Date currentDate, boolean selfCorrection) {
        super(order, age, null, forNPeople, consideredTimePeriod, currentDate, selfCorrection);
    }

    public Integer getAge() {
        return getXLabel();
    }

    public Integer getOrder() {
        return getYLabel();
    }
}
