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
package uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.statsKeys;

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class StatsKey {

    private final Integer yLabel;
    private final Integer xLabel;
    private final Integer maxXLabel;
    private final double forNPeople;
    private final CompoundTimeUnit consideredTimePeriod;
    private final Date date;

    public StatsKey(Integer yLabel, Integer xLabel, Integer maxXLabel, double forNPeople, CompoundTimeUnit consideredTimePeriod, Date date) {
        this.yLabel = yLabel;
        this.xLabel = xLabel;
        this.maxXLabel = maxXLabel;
        this.forNPeople = forNPeople;
        this.consideredTimePeriod = consideredTimePeriod;
        this.date = date;
    }

    public StatsKey(Integer yLabel, Integer xLabel, double forNPeople, CompoundTimeUnit consideredTimePeriod, Date date) {
        this(yLabel, xLabel, null, forNPeople, consideredTimePeriod, date);
    }

    public StatsKey(Integer yLabel, double forNPeople, CompoundTimeUnit consideredTimePeriod, Date date) {
        this(yLabel, null, null, forNPeople, consideredTimePeriod, date);
    }

    public double getForNPeople() {
        return forNPeople;
    }

    public Integer getXLabel() {
        return xLabel;
    }

    public Integer getMaxXLabel() {
        return maxXLabel;
    }

    public Integer getYLabel() {
        return yLabel;
    }

    public CompoundTimeUnit getConsideredTimePeriod() {
        return consideredTimePeriod;
    }

    public Date getDate() {
        return date;
    }
}