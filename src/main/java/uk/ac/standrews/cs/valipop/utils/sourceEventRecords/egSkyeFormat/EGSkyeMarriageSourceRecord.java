package uk.ac.standrews.cs.valipop.utils.sourceEventRecords.egSkyeFormat;

import org.apache.commons.math3.random.JDKRandomGenerator;
import uk.ac.standrews.cs.basic_model.model.IPartnership;
import uk.ac.standrews.cs.basic_model.model.IPopulation;
import uk.ac.standrews.cs.valipop.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.valipop.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.oldDSformat.MarriageSourceRecord;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dateModel.dateImplementations.ExactDate;

import java.util.List;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class EGSkyeMarriageSourceRecord extends MarriageSourceRecord {

    protected ExactDate marriageDate;
    protected int groomID;
    protected int brideID;

    public EGSkyeMarriageSourceRecord(IPartnershipExtended partnership, IPopulation population) {
        super(partnership, population);

        marriageDate = new ExactDate(partnership.getPartnershipDate());
        groomID = partnership.getMalePartnerId();
        brideID = partnership.getFemalePartnerId();

    }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();

        append(builder, "", "", uid, "", "", "",
                "", "", "", "",
                "", "", "", marriageDate.toString(), getGroomAgeOrDateOfBirth(), getBrideAgeOrDateOfBirth(), "",
                "", "", "", "", "", "", "", marriageDate.toString(), marriageDate.getDay(), marriageDate.getMonth(), marriageDate.getYear(),
                "", "", "", getGroomForename(), getGroomSurname(),
                getGroomOccupation(), getGroomMaritalStatus(), getGroomAgeOrDateOfBirth(), getGroomAddress(),
                "", getBrideForename(), getBrideSurname(), getBrideOccupation(),
                getBrideMaritalStatus(), getBrideAgeOrDateOfBirth(), getBrideAddress(), "",
                getGroomFathersForename(), getGroomFathersSurname(), getGroomFathersOccupation(),
                getGroomFatherDeceased(), getGroomMothersForename(), getGroomMothersMaidenSurname(),
                getGroomMotherDeceased(), getBrideFathersForename(), getBrideFathersSurname(),
                getBrideFatherOccupation(), getBrideFatherDeceased(), getBrideMothersForename(),
                getBrideMothersMaidenSurname(), getBrideMotherDeceased(), "", "",
                "", "", "", "", "", "", "", "", "",
                "", "", "", groomID, brideID);

        return builder.toString();
    }

    @Override
    public String getHeaders() {

        final StringBuilder builder = new StringBuilder();

        append(builder, "IOS_identifier", "corrected", "ID", "source", "line no", "rd identifier",
                "IOS_Rdindentifier", "IOS_RSDidentifier", "register identifier", "IOS_registeridentifier",
                "entry number", "IOS_entrynumber", "IOS_YrofReg", "date of marriage", "groomagey", "brideagey", "gsn",
                "gxn", "bsn", "bxn", "gfxn", "gmxn", "bfxn", "bmxn", "clean date of marriage", "day", "month", "year",
                "place of marriage 1", "place of marriage 2", "denomination", "forename of groom", "surname of groom",
                "occupation of groom", "marital status of groom", "age of groom", "address of groom 1",
                "address of groom 2", "forename of bride", "surname of bride", "occupation of bride",
                "marital status of bride", "age of bride", "address of bride 1", "address of bride 2",
                "groom's father's forename", "groom's father's surname", "groom's father's occupation",
                "if groom's father deceased", "groom's mother's forename", "groom's mother's maiden surname",
                "if groom's mother deceased", "bride's father's forename", "bride's father's surname",
                "bride's father's occupation", "if bride's father deceased", "bride's mother's forename",
                "bride's mother's maiden surname", "if bride's mother deceased", "did groom sign?", "did bride sign?",
                "notes1", "notes2", "notes3", "repeats", "gearlypid", "gearlysch", "bearlypid", "bearlysch", "glatepid",
                "glatesch", "blatepid", "blatesch", "gdeath", "bdeath");

        return builder.toString();
    }
}