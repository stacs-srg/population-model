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
package uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.DoubleNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.timeSteps.CompoundTimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.determinedCounts.MultipleDeterminedCount;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.populationStatistics.statsKeys.PartneringStatsKey;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.ControlSelfNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.DoubleNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.statistics.analysis.validation.contingencyTables.TableStructure.CTRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.ArrayList;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NewPartnerAgeNodeDouble extends DoubleNode<IntegerRange, String> implements ControlSelfNode {

    public NewPartnerAgeNodeDouble(IntegerRange option, SeparationNodeDouble parentNode, Double initCount, boolean init) {
        super(option, parentNode, initCount);

        if(!init) {
            calcCount();
        }

    }

    @Override
    public Node<String, ?, Double, ?> makeChildInstance(String childOption, Double initCount) {
        return null;
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {
        incCountByOne();
    }

    @Override
    public void advanceCount() {

    }

    @Override
    public void calcCount() {

        if(getOption().getValue() == null) {
            setCount(getParent().getCount());
        } else {
            YearDate yob = ((YOBNodeDouble) getAncestor(new YOBNodeDouble())).getOption();
            Integer age = ((AgeNodeDouble) getAncestor(new AgeNodeDouble())).getOption().getValue();

            Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

            double numberOfFemales = getParent().getCount();
            CompoundTimeUnit timePeriod = new CompoundTimeUnit(1, TimeUnit.YEAR);

            MultipleDeterminedCount mDC = (MultipleDeterminedCount) getInputStats()
                    .getDeterminedCount(new PartneringStatsKey(age, numberOfFemales, timePeriod, currentDate), null);

            if(getOption().getValue() == null) {
                setCount(getParent().getCount());
            } else {
                setCount(mDC.getRawUncorrectedCount().get(getOption()));
            }
        }

    }

    public ArrayList<String> toStringAL() {
        ArrayList<String> s = getParent().toStringAL();
        if(getOption() == null) {
            s.add("na");
        } else {
            s.add(getOption().toString());
        }
        s.add(getCount().toString());
        return s;
    }

    public CTRow<Double> toCTRow() {
        CTRow r = getParent().toCTRow();

        if(r != null) {
            if (getOption() == null) {
                r.setVariable(getVariableName(), "na");
            } else {
                r.setVariable(getVariableName(), getOption().toString());
            }

            r.setCount(getCount());
        }

        return r;
    }

    @Override
    public String getVariableName() {
        return "NPA";
    }

}