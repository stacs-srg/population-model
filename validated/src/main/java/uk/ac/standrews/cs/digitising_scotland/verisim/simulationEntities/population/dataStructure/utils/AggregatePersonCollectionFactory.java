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
package uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.utils;

import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.dateImplementations.AdvancableDate;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.partnership.IPartnershipExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.PeopleCollection;
import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population.dataStructure.PersonCollection;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.Date;
import uk.ac.standrews.cs.digitising_scotland.verisim.utils.specialTypes.dateModel.DateUtils;

import java.util.Collection;

/**
 * Provides a set of methods to create aggregates of two PersonCollections
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AggregatePersonCollectionFactory {

    /**
     * Aggregates two PersonCollections into a single collection of IPersonExtended.
     *
     * @param col1 The first PersonCollection
     * @param col2 The second PersonCollection
     * @return The aggregated Collection of people
     */
    public static Collection<IPersonExtended> makeCollectionOfPersons(PersonCollection col1, PersonCollection col2) {

        Collection<IPersonExtended> people = col1.getAll();
        people.addAll(col2.getAll());

        return people;
    }

    /**
     * Aggregates two PersonCollections into a single PersonCollection.
     *
     * @param col1 The first PersonCollection
     * @param col2 The second PersonCollection
     * @return The aggregated PersonCollection
     */
    public static PeopleCollection makePeopleCollection(PeopleCollection col1, PeopleCollection col2) {

        AdvancableDate start = DateUtils.getEarliestDate(col1.getStartDate(), col2.getStartDate());
        Date end = DateUtils.getLatestDate(col1.getStartDate(), col2.getStartDate());

        PeopleCollection cloneCol1 = col1.clone();
        PeopleCollection cloneCol2 = col2.clone();

        cloneCol1.setStartDate(start);
        cloneCol1.setEndDate(end);

        for(IPersonExtended p : cloneCol2.getPeople_ex()) {
            cloneCol1.addPerson(p);
        }

        for(IPartnershipExtended p : cloneCol2.getPartnerships_ex()) {
            cloneCol1.addPartnershipToIndex(p);
        }

        return cloneCol1;
    }


}