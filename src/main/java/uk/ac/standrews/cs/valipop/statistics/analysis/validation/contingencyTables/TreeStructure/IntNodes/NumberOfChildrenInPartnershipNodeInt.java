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
package uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.IntNodes;

import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.enumerations.SeparationOption;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.valipop.utils.specialTypes.integerRange.IntegerRange;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.IntNode;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.PersonCharacteristicsIdentifier;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NumberOfChildrenInPartnershipNodeInt extends IntNode<IntegerRange, SeparationOption> {

    public NumberOfChildrenInPartnershipNodeInt(IntegerRange option, NumberOfChildrenInYearNodeInt parentNode, Integer initCount) {
        super(option, parentNode, initCount);
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {
        incCountByOne();

        IPartnershipExtended activePartnership = PersonCharacteristicsIdentifier.getActivePartnership(person, currentDate);

        SeparationOption option = PersonCharacteristicsIdentifier.toSeparate(activePartnership, currentDate.getYearDate());

        try {
            getChild(option).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(option).processPerson(person, currentDate);
        }
    }

    @Override
    public String getVariableName() {
        return "NCIP";
    }

    @Override
    public Node<SeparationOption, ?, Integer, ?> makeChildInstance(SeparationOption childOption, Integer initCount) {
        return new SeparationNodeInt(childOption, this, initCount);
    }
}