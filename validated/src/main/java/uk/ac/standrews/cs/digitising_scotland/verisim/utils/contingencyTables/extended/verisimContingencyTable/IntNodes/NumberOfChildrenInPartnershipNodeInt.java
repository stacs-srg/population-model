package uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.IntNodes;

import uk.ac.standrews.cs.digitising_scotland.verisim.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.ChildNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.IntNode;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.Node;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.PersonCharacteristicsIdentifier;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.contingencyTables.extended.verisimContingencyTable.enumerations.SeparationOption;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class NumberOfChildrenInPartnershipNodeInt extends IntNode<Integer, SeparationOption> {

    public NumberOfChildrenInPartnershipNodeInt(Integer option, NumberOfChildrenInYearNodeInt parentNode, Integer initCount) {
        super(option, parentNode, initCount);
    }

    @Override
    public void processPerson(IPersonExtended person, Date currentDate) {
        incCountByOne();

        IPartnershipExtended activePartnership = PersonCharacteristicsIdentifier.getActivePartnership(person, currentDate);

        Boolean toSeparate = PersonCharacteristicsIdentifier.toSeparate(activePartnership, currentDate.getYearDate());

        SeparationOption option;

        if(toSeparate == null) {
            option = SeparationOption.NA;
        } else if(toSeparate) {
            option = SeparationOption.YES;
        } else {
            option = SeparationOption.NO;
        }

        try {
            getChild(option).processPerson(person, currentDate);
        } catch (ChildNotFoundException e) {
            addChild(option).processPerson(person, currentDate);
        }
    }

    @Override
    public Node<SeparationOption, ?, Integer, ?> makeChildInstance(SeparationOption childOption, Integer initCount) {
        return new SeparationNodeInt(childOption, this, initCount);
    }
}