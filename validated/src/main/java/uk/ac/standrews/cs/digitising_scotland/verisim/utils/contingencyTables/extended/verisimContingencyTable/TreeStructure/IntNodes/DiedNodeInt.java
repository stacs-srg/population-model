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
package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.dateImplementations.YearDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.timeSteps.TimeUnit;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.Interfaces.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.CTRow;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TableStructure.PersonCharacteristicsIdentifier;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.TreeStructure.enumerations.DiedOption;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.integerRange.IntegerRange;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class DiedNodeInt extends IntNode<DiedOption, IntegerRange> {

    public DiedNodeInt(DiedOption option, AgeNodeInt parentNode, Integer initCount) {
        super(option, parentNode, initCount);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {

        incCountByOne();

        if(Character.toUpperCase(person.getSex()) == 'F') {

            IPartnershipExtended partnership = PersonCharacteristicsIdentifier.getActivePartnership(person, currentDate);
            int numberOfChildren;

            if(partnership == null) {
                numberOfChildren = 0;
            } else {
                numberOfChildren = PersonCharacteristicsIdentifier.getChildrenBirthedBeforeDate(partnership, currentDate);
            }

            IntegerRange range = resolveToChildRange(numberOfChildren);

            try {
                getChild(range).processPerson(person, currentDate);
            } catch (ChildNotFoundException e) {
                addChild(range).processPerson(person, currentDate);
            }

        }

    }

    @Override
    public Node<IntegerRange, ?, Integer, ?> makeChildInstance(IntegerRange childOption, Integer initCount) {
        return new PreviousNumberOfChildrenInPartnershipNodeInt(childOption, this, initCount);
    }

    private IntegerRange resolveToChildRange(Integer pncip) {

        for(Node<IntegerRange, ?, ?, ?> aN : getChildren()) {
            if(aN.getOption().contains(pncip)) {
                return aN.getOption();
            }
        }

        YearDate yob = ((YOBNodeInt) getAncestor(new YOBNodeInt())).getOption();
        Integer age = ((AgeNodeInt) getAncestor(new AgeNodeInt())).getOption().getValue();

        Date currentDate = yob.advanceTime(age, TimeUnit.YEAR);

        Collection<IntegerRange> sepRanges = getInputStats().getSeparationByChildCountRates(currentDate).getLabels();

        for(IntegerRange o : sepRanges) {
            if(o.contains(pncip)) {
                return o;
            }
        }

        if(pncip == 0) {
            return new IntegerRange(0);
        }

        throw new Error("Did not resolve any permissable ranges");
    }

    public ArrayList<String> toStringAL() {
        ArrayList<String> s = getParent().toStringAL();
        s.add(getOption().toString());
        s.add(getCount().toString());
        return s;
    }

    public CTRow<Integer> toCTRow() {
        CTRow r = getParent().toCTRow();
        r.setVariable(getVariableName(), getOption().toString());
        r.setCount(getCount());
        return r;
    }

    @Override
    public String getVariableName() {
        return "Died";
    }


}