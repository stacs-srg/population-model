package uk.ac.standrews.cs.valipop.utils.sourceEventRecords.tdFormat;

import org.apache.commons.math3.random.JDKRandomGenerator;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.IPopulation;
import uk.ac.standrews.cs.valipop.simulationEntities.PopulationNavigation;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TableStructure.PersonCharacteristicsIdentifier;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.SexOption;
import uk.ac.standrews.cs.valipop.utils.sourceEventRecords.oldDSformat.MarriageSourceRecord;

import java.time.LocalDate;
import java.util.List;

import static uk.ac.standrews.cs.valipop.simulationEntities.PopulationNavigation.getLastPartnership;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class TDMarriageSourceRecord extends MarriageSourceRecord {

    private String BRIDE_IMMIGRATION_GENERATION;
    private String GROOM_IMMIGRATION_GENERATION;

    protected LocalDate marriageDate;
    protected int groomID;
    protected int brideID;
    protected String marriageLocation;

    private String GROOM_IDENTITY = "";
    private String BRIDE_IDENTITY = "";
    private String GROOM_MOTHER_IDENTITY = "";
    private String GROOM_FATHER_IDENTITY = "";
    private String BRIDE_MOTHER_IDENTITY = "";
    private String BRIDE_FATHER_IDENTITY = "";
    private String GROOM_BIRTH_RECORD_IDENTITY = "";
    private String BRIDE_BIRTH_RECORD_IDENTITY = "";
    private String GROOM_FATHER_BIRTH_RECORD_IDENTITY = "";
    private String GROOM_MOTHER_BIRTH_RECORD_IDENTITY = "";
    private String BRIDE_FATHER_BIRTH_RECORD_IDENTITY = "";
    private String BRIDE_MOTHER_BIRTH_RECORD_IDENTITY = "";

    public TDMarriageSourceRecord(IPartnership partnership, IPopulation population) {
        super(partnership, population);

        marriageDate = partnership.getPartnershipDate();
        groomID = partnership.getMalePartner().getId();
        brideID = partnership.getFemalePartner().getId();

        groom_marital_status = identifyMaritalStatus(partnership.getMalePartner());
        bride_marital_status = identifyMaritalStatus(partnership.getFemalePartner());

        marriageLocation = partnership.getMarriagePlace();

        if(partnership.getFemalePartner().getParents() != null) {
            IPartnership bParents = partnership.getFemalePartner().getParents();
            setBrideFathersSurname(bParents.getMalePartner().getSurname());

            BRIDE_MOTHER_IDENTITY = String.valueOf(bParents.getFemalePartner().getId());
            BRIDE_MOTHER_BIRTH_RECORD_IDENTITY = BRIDE_MOTHER_IDENTITY;

            BRIDE_FATHER_IDENTITY = String.valueOf(bParents.getMalePartner().getId());
            BRIDE_FATHER_BIRTH_RECORD_IDENTITY = BRIDE_FATHER_IDENTITY ;
        }

        if(partnership.getMalePartner().getParents() != null) {
            IPartnership gParents = partnership.getMalePartner().getParents();
            setGroomFathersSurname(gParents.getMalePartner().getSurname());

            GROOM_MOTHER_IDENTITY = String.valueOf(gParents.getFemalePartner().getId());
            GROOM_MOTHER_BIRTH_RECORD_IDENTITY = GROOM_MOTHER_IDENTITY;

            GROOM_FATHER_IDENTITY = String.valueOf(gParents.getMalePartner().getId());
            GROOM_FATHER_BIRTH_RECORD_IDENTITY = GROOM_FATHER_IDENTITY;


        }

        GROOM_IDENTITY = String.valueOf(groomID);
        BRIDE_IDENTITY = String.valueOf(brideID);
        GROOM_BIRTH_RECORD_IDENTITY = String.valueOf(groomID);
        BRIDE_BIRTH_RECORD_IDENTITY = String.valueOf(brideID);

        int brideImmigantGen = PersonCharacteristicsIdentifier.getImmigrantGeneration(partnership.getFemalePartner());

        if(brideImmigantGen == -1)
            BRIDE_IMMIGRATION_GENERATION = "NA";
        else
            BRIDE_IMMIGRATION_GENERATION = String.valueOf(brideImmigantGen);

        int groomImmigantGen = PersonCharacteristicsIdentifier.getImmigrantGeneration(partnership.getMalePartner());

        if(groomImmigantGen == -1)
            GROOM_IMMIGRATION_GENERATION = "NA";
        else
            GROOM_IMMIGRATION_GENERATION = String.valueOf(brideImmigantGen);

    }

    public String identifyMaritalStatus(IPerson deceased) {

        List<IPartnership> partnerships = deceased.getPartnerships();

        if (partnerships.size() == 0) {
            if (deceased.getSex() == SexOption.MALE) {
                return "B"; // bachelor
            } else {
                return "S"; // single/spinster
            }
        } else {
            IPartnership lastPartnership = PopulationNavigation.getLastPartnershipBeforeDate(deceased, marriageDate);
            if (lastPartnership == null) {
                if (deceased.getSex() == SexOption.MALE) {
                    return "B"; // bachelor
                } else {
                    return "S"; // single/spinster
                }
            } else if(lastPartnership.getSeparationDate(new JDKRandomGenerator()) == null) {
                // not separated from last partner

                IPerson lastPartner = PopulationNavigation.getLastPartnershipBeforeDate(deceased, marriageDate).getPartnerOf(deceased);
                if (PopulationNavigation.aliveOnDate(lastPartner, marriageDate)) {

                    // last spouse alive on death date of deceased
                    return "?"; // married

                } else {
                    // last spouse dead on death date of deceased
                    return "W"; // widow/er
                }
            } else {
                // separated from last partner
                return "D"; // divorced
            }
        }
    }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();

        append(builder, uid, marriageDate.getDayOfMonth(), marriageDate.getMonth(), marriageDate.getYear(),
                marriageLocation, getGroomForename(), getGroomSurname(),
                getGroomOccupation(), getGroomMaritalStatus(), getGroomAgeOrDateOfBirth(), getGroomAddress(),
                getBrideForename(), getBrideSurname(), getBrideOccupation(),
                getBrideMaritalStatus(), getBrideAgeOrDateOfBirth(), getBrideAddress(),
                getGroomFathersForename(), getGroomFathersSurname(), getGroomFathersOccupation(),
                getGroomFatherDeceased(), getGroomMothersForename(), getGroomMothersMaidenSurname(),
                getGroomMotherDeceased(), getBrideFathersForename(), getBrideFathersSurname(),
                getBrideFatherOccupation(), getBrideFatherDeceased(), getBrideMothersForename(),
                getBrideMothersMaidenSurname(), getBrideMotherDeceased(),
                "SYNTHETIC DATA PRODUCED USING VALIPOP", groomID, brideID, GROOM_IDENTITY, BRIDE_IDENTITY,
                GROOM_MOTHER_IDENTITY, GROOM_FATHER_IDENTITY, BRIDE_MOTHER_IDENTITY, BRIDE_FATHER_IDENTITY,
                GROOM_BIRTH_RECORD_IDENTITY, BRIDE_BIRTH_RECORD_IDENTITY, GROOM_FATHER_BIRTH_RECORD_IDENTITY,
                GROOM_MOTHER_BIRTH_RECORD_IDENTITY, BRIDE_FATHER_BIRTH_RECORD_IDENTITY,
                BRIDE_MOTHER_BIRTH_RECORD_IDENTITY, BRIDE_IMMIGRATION_GENERATION, GROOM_IMMIGRATION_GENERATION);

        return builder.toString();
    }

    @Override
    public String getHeaders() {

        final StringBuilder builder = new StringBuilder();

        append(builder, "ID", "day", "month", "year",
                "place of marriage", "forename of groom", "surname of groom",
                "occupation of groom", "marital status of groom", "age of groom", "address of groom",
                "forename of bride", "surname of bride", "occupation of bride",
                "marital status of bride", "age of bride", "address of bride",
                "groom's father's forename", "groom's father's surname", "groom's father's occupation",
                "if groom's father deceased", "groom's mother's forename", "groom's mother's maiden surname",
                "if groom's mother deceased", "bride's father's forename", "bride's father's surname",
                "bride's father's occupation", "if bride's father deceased", "bride's mother's forename",
                "bride's mother's maiden surname", "if bride's mother deceased",
                "notes1", "gdeath", "bdeath", "GROOM_IDENTITY", "BRIDE_IDENTITY", "GROOM_MOTHER_IDENTITY",
                "GROOM_FATHER_IDENTITY", "BRIDE_MOTHER_IDENTITY", "BRIDE_FATHER_IDENTITY",
                "GROOM_BIRTH_RECORD_IDENTITY", "BRIDE_BIRTH_RECORD_IDENTITY", "GROOM_FATHER_BIRTH_RECORD_IDENTITY",
                "GROOM_MOTHER_BIRTH_RECORD_IDENTITY", "BRIDE_FATHER_BIRTH_RECORD_IDENTITY",
                "BRIDE_MOTHER_BIRTH_RECORD_IDENTITY", "BRIDE_IMMIGRANT_GENERATION", "GROOM_IMMIGRANT_GENERATION");

        return builder.toString();
    }
}