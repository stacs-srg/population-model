package uk.ac.standrews.cs.valipop.implementations;

import org.apache.commons.math3.random.RandomGenerator;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.Person;
import uk.ac.standrews.cs.valipop.simulationEntities.PopulationCounts;
import uk.ac.standrews.cs.valipop.simulationEntities.dataStructure.Population;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.SexOption;
import uk.ac.standrews.cs.valipop.statistics.populationStatistics.PopulationStatistics;
import uk.ac.standrews.cs.valipop.utils.specialTypes.dates.DateSelector;

import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.time.temporal.ChronoUnit;

/**
 * Source for creating persons in simulation.
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PersonFactory {

    private final Population population;
    private final PopulationStatistics desired;
    private final Period simulationTimeStep;
    private final DateSelector birthDateSelector;

    public PersonFactory(Population population, PopulationStatistics desired, Period simulationTimeStep, RandomGenerator randomNumberGenerator) {
        this.population = population;
        this.desired = desired;
        this.simulationTimeStep = simulationTimeStep;
        birthDateSelector = new DateSelector(randomNumberGenerator);
    }

    public IPerson makePerson(final LocalDate birthDate, final IPartnership parents, final boolean adulterous) {
        return makePerson(birthDate, parents, adulterous, false);
    }

    public IPerson makePerson(final LocalDate birthDate, final IPartnership parents, final boolean adulterous, final boolean immigrant) {

        SexOption sex = getSex(population.getPopulationCounts(), desired, birthDate);
        return new Person(sex, birthDate, parents, desired, adulterous, immigrant);
    }

    public IPerson makePerson(final LocalDate birthDate, final IPartnership parents, final boolean adulterous, final boolean immigrant, SexOption sex) {

        if(sex == SexOption.MALE)
            population.getPopulationCounts().newMale();
        else
            population.getPopulationCounts().newFemale();

        return new Person(sex, birthDate, parents, desired, adulterous, immigrant);
    }

    public IPerson makePersonWithRandomBirthDate(final LocalDate currentDate, final IPartnership parents, final boolean adulterous) {

        LocalDate immigrationDateFather = parents == null ? null : parents.getMalePartner().getImmigrationDate();

        if(immigrationDateFather != null) {
            if(immigrationDateFather.plus(desired.getMinGestationPeriod()).isAfter(currentDate))
                return makePerson(
                        birthDateSelector.selectRandomDate(
                            immigrationDateFather.plus(desired.getMinGestationPeriod()),
                            currentDate.plus(1, ChronoUnit.YEARS)).minus(1, ChronoUnit.DAYS),
                        parents, adulterous);
        }

        return makePerson(birthDateSelector.selectRandomDate(currentDate, simulationTimeStep), parents, adulterous);
    }

    private SexOption getSex(final PopulationCounts counts, final PopulationStatistics statistics, final LocalDate currentDate) {

        final double sexBalance = counts.getAllTimeSexRatio();

        if (sexBalance < statistics.getMaleProportionOfBirths(Year.of(currentDate.getYear()))) {

            counts.newMale();
            return SexOption.MALE;

        } else {

            counts.newFemale();
            return SexOption.FEMALE;
        }
    }

}
