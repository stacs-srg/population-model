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
package uk.ac.standrews.cs.valipop.simulationEntities.dataStructure;

import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.IPopulation;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.SexOption;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PeopleCollection extends PersonCollection implements IPopulation, Cloneable {

    private final MaleCollection males;
    private final FemaleCollection females;

    private final Map<Integer, IPartnership> partnershipIndex = new HashMap<>();

    /**
     * Instantiates a new PersonCollection. The dates specify the earliest and latest expected birth dates of
     * individuals in the PersonCollection. There is no hard enforcement of this as the bounds are intended to serve
     * mainly as a guide for when other things make use of the PersonCollection - e.g. producing plots, applying
     * validation statistics.
     *
     * @param start the start
     * @param end   the end
     */
    public PeopleCollection(final LocalDate start, final LocalDate end, final Period divisionSize, final String description) {

        super(start, end, divisionSize, description);

        males = new MaleCollection(start, end, divisionSize, description);
        females = new FemaleCollection(start, end, divisionSize, description);
    }

    @Override
    public PeopleCollection clone() {

        final PeopleCollection clone = new PeopleCollection(getStartDate(), getEndDate(), getDivisionSize(), description);

        for (IPerson person : males) {
            clone.add(person);
        }

        for (IPerson person : females) {
            clone.add(person);
        }

        for (IPartnership partnership : partnershipIndex.values()) {
            clone.add(partnership);
        }

        return clone;
    }

    public MaleCollection getMales() {
        return males;
    }

    public FemaleCollection getFemales() {
        return females;
    }

    public void add(final IPartnership partnership) {

        partnershipIndex.put(partnership.getId(), partnership);
    }

    public void removeMales(final int numberToRemove, final LocalDate firstDate, final Period timePeriod, final boolean bestAttempt) throws InsufficientNumberOfPeopleException {

        removePeople(males, numberToRemove, firstDate, timePeriod, bestAttempt);
    }

    public void removeFemales(final int numberToRemove, final LocalDate firstDate, final Period timePeriod, final boolean bestAttempt) throws InsufficientNumberOfPeopleException {

        removePeople(females, numberToRemove, firstDate, timePeriod, bestAttempt);
    }

    /*
    -------------------- PersonCollection abstract methods --------------------
     */

    @Override
    public Collection<IPerson> getPeople() {

        final Collection<IPerson> people = females.getPeople();
        people.addAll(males.getPeople());

        return people;
    }

    @Override
    public Collection<IPerson> getPeopleBornInTimePeriod(final LocalDate firstDate, final Period timePeriod) {

        final Collection<IPerson> people = females.getPeopleBornInTimePeriod(firstDate, timePeriod);
        people.addAll(males.getPeopleBornInTimePeriod(firstDate, timePeriod));

        return people;
    }

    @Override
    public Collection<IPerson> getPeopleAliveInTimePeriod(final LocalDate firstDate, final Period timePeriod, final Period maxAge) {

        Collection<IPerson> people = females.getPeopleAliveInTimePeriod(firstDate, timePeriod, maxAge);
        people.addAll(males.getPeopleAliveInTimePeriod(firstDate, timePeriod, maxAge));

        return people;
    }

    @Override
    public void add(final IPerson person) {

        if (person.getSex() == SexOption.MALE) {
            males.add(person);

        } else {
            females.add(person);
        }
    }

    @Override
    public void remove(final IPerson person) {

        if (person.getSex() == SexOption.MALE) {
            males.remove(person);

        } else {
            females.remove(person);
        }
    }

    @Override
    public int getNumberOfPeople() {

        return females.getNumberOfPeople() + males.getNumberOfPeople();
    }

    @Override
    public int getNumberOfPeople(final LocalDate firstDate, final Period timePeriod) {

        return females.getNumberOfPeople(firstDate, timePeriod) + males.getNumberOfPeople(firstDate, timePeriod);
    }

    @Override
    public Set<LocalDate> getDivisionDates() {
        return females.getDivisionDates();
    }


    @Override
    public Iterable<IPartnership> getPartnerships() {

        return partnershipIndex.values();
    }

    @Override
    public IPerson findPerson(final int id) {

        for (IPerson person : males) {
            if (person.getId() == id) return person;
        }

        for (IPerson person : females) {
            if (person.getId() == id) return person;
        }
        return null;
    }

    @Override
    public IPartnership findPartnership(int id) {
        return partnershipIndex.get(id);
    }

    @Override
    public int getNumberOfPartnerships() {
        return partnershipIndex.size();
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    public String toString() {
        return description;
    }

    private void removePeople(final PersonCollection collection, final int numberToRemove, final LocalDate firstDate, final Period timePeriod, final boolean bestAttempt) throws InsufficientNumberOfPeopleException {

        final Collection<IPerson> removed = collection.removeNPersons(numberToRemove, firstDate, timePeriod, true);
        for (IPerson person : removed) {
            removeChildFromParentsPartnership(person);
        }
    }

    private void removeChildFromParentsPartnership(final IPerson person) {

        final IPartnership parents = person.getParents();

        if (parents != null) {
            final IPerson mother = parents.getFemalePartner();
            remove(mother);
            parents.getChildren().remove(person);

            if(parents.getChildren().size() == 0) {
                cancelPartnership(parents);
            }

            add(mother);
        }
    }

    private void cancelPartnership(IPartnership partnership) {

        // remove from parents partnership history
        partnership.getMalePartner().getPartnerships().remove(partnership);
        partnership.getFemalePartner().getPartnerships().remove(partnership);

        // remove partnership from index
        partnershipIndex.remove(partnership.getId());

    }
}