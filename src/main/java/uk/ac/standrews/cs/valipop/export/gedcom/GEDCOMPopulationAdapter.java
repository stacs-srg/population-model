/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
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
package uk.ac.standrews.cs.valipop.export.gedcom;

import org.gedcom4j.model.Family;
import org.gedcom4j.model.Individual;
import org.gedcom4j.parser.GedcomParser;
import org.gedcom4j.parser.GedcomParserException;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.simulationEntities.IPopulation;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides abstract interface to a population represented in a GEDCOM file.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class GEDCOMPopulationAdapter implements IPopulation {

    private final GedcomParser parser;
    private String description;

    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    /**
     * Initialises the adapter for a given GEDCOM file.
     *
     * @param path the path of the GEDCOM file
     * @throws IOException           if the file cannot be accessed
     * @throws GedcomParserException if the GEDCOM file is not well formed
     */
    public GEDCOMPopulationAdapter(final Path path) throws IOException, GedcomParserException {

        parser = new GedcomParser();
        parser.load(path.toString());
    }

    @Override
    public Iterable<IPerson> getPeople() {

        List<IPerson> people = new ArrayList<>();

        for (Individual individual : parser.gedcom.individuals.values()) {
            people.add(new GEDCOMPerson(individual, this));
        }

        Collections.sort(people);

        return people;
    }

    @Override
    public Iterable<IPartnership> getPartnerships() {

        List<IPartnership> partnerships = new ArrayList<>();

        for (Family family : parser.gedcom.families.values()) {
            partnerships.add(new GEDCOMPartnership(family, this));
        }

        Collections.sort(partnerships);

        return partnerships;
    }

    @Override
    public IPerson findPerson(final int id) {

        for (final IPerson person : getPeople()) {
            if (person.getId() == id) {
                return person;
            }
        }
        return null;
    }

    @Override
    public IPartnership findPartnership(final int id) {

        for (final IPartnership partnership : getPartnerships()) {
            if (partnership.getId() == id) {
                return partnership;
            }
        }
        return null;
    }

    @Override
    public int getNumberOfPeople() {

        return parser.gedcom.individuals.values().size();
    }

    @Override
    public int getNumberOfPartnerships() {

        return parser.gedcom.families.values().size();
    }

    @Override
    public LocalDate getStartDate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LocalDate getEndDate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDescription(final String description) {

        this.description = description;
    }

    @Override
    public String toString() {

        return description;
    }
}