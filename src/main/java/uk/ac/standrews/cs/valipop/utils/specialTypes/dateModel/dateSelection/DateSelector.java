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
package uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateSelection;

import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.DateUtils;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;

import java.util.Random;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DateSelector {

    private Random random = new Random();

    public ExactDate selectDate(Date earliestDate, Date latestDate) {

        int daysInWindow = DateUtils.differenceInDays(earliestDate, latestDate);
        int chosenDay = random.nextInt(Math.abs(daysInWindow));
        return DateUtils.calculateExactDate(earliestDate, chosenDay);

    }

    public ExactDate selectDate(Date earliestDate, CompoundTimeUnit timePeriod) {

        int daysInWindow = DateUtils.getDaysInTimePeriod(earliestDate, timePeriod);

        int chosenDay = random.nextInt(Math.abs(daysInWindow));
        return DateUtils.calculateExactDate(earliestDate, chosenDay);

    }

}