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

import org.gedcom4j.model.*;
import uk.ac.standrews.cs.valipop.simulationEntities.IPartnership;
import uk.ac.standrews.cs.valipop.simulationEntities.IPerson;
import uk.ac.standrews.cs.valipop.statistics.analysis.validation.contingencyTables.TreeStructure.SexOption;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Person implementation for a population represented in a GEDCOM file.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk>
 */
public class GEDCOMPerson implements IPerson {

    private final GEDCOMPopulationAdapter adapter;
    protected int id;
    private String first_name;
    protected String surname;
    protected SexOption sex;
    private LocalDate birth_date;
    private String birth_place;
    private LocalDate death_date;
    private String death_place;
    protected String death_cause;
    protected String occupation;
    private List<Integer> partnership_ids;
    private int parent_id;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getFirstName() {
        return first_name;
    }

    @Override
    public String getSurname() {
        return surname;
    }

    @Override
    public SexOption getSex() {
        return sex;
    }

    @Override
    public String getBirthPlace() {
        return birth_place;
    }

    @Override
    public String getDeathPlace() {
        return death_place;
    }

    @Override
    public String getDeathCause() {
        return death_cause;
    }

    @Override
    public void setDeathCause(final String deathCause) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getOccupation() {
        return occupation;
    }

    @SuppressWarnings("NonFinalFieldReferenceInEquals")
    @Override
    public boolean equals(final Object other) {
        return other instanceof IPerson && ((IPerson) other).getId() == id;
    }

    @SuppressWarnings("NonFinalFieldReferencedInHashCode")
    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "GEDCOM person";
    }

    private static final String MALE_STRING = SexOption.MALE.toString();

    GEDCOMPerson(final Individual individual, GEDCOMPopulationAdapter adapter) {

        this.adapter = adapter;

        setId(individual);
        setSex(individual);
        setNames(individual);
        setParents(individual);
        setEvents(individual);
        setOccupation(individual);
        setPartnerships(individual);
    }

    private void setId(final Individual individual) {

        id = GEDCOMPopulationWriter.idToInt(individual.xref);
    }

    private void setSex(final Individual individual) {

        sex = individual.sex.toString().equals(MALE_STRING) ? SexOption.MALE : SexOption.FEMALE;
    }

    private void setNames(final Individual individual) {

        final List<PersonalName> names = individual.names;

        first_name = findFirstNames(names);
        surname = findSurname(names);
    }

    private void setParents(final Individual individual) {

        final List<FamilyChild> families = individual.familiesWhereChild;
        parent_id = !families.isEmpty() ? GEDCOMPopulationWriter.idToInt(families.get(0).family.xref) : -1;
    }

    private void setPartnerships(final Individual individual) {

        partnership_ids = new ArrayList<>();

        List<FamilySpouse> familiesWhereSpouse = individual.familiesWhereSpouse;

        for (FamilySpouse family : familiesWhereSpouse) {
            partnership_ids.add(GEDCOMPopulationWriter.idToInt(family.family.xref));
        }
    }

    private void setEvents(final Individual individual) {

        for (final IndividualEvent event : individual.events) {

            switch (event.type) {

                case BIRTH:
                    birth_date = LocalDate.parse(event.date.toString(), GEDCOMPopulationAdapter.FORMATTER);
                    if (event.place != null) {
                        birth_place = event.place.placeName;
                    }
                    break;

                case DEATH:
                    death_date = LocalDate.parse(event.date.toString(), GEDCOMPopulationAdapter.FORMATTER);
                    if (event.place != null) {
                        death_place = event.place.placeName;
                    }
                    if (event.cause != null) {
                        death_cause = event.cause.toString();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private void setOccupation(final Individual individual) {

        final List<IndividualAttribute> occupation_attributes = individual.getAttributesOfType(IndividualAttributeType.OCCUPATION);
        if (!occupation_attributes.isEmpty()) {
            occupation = occupation_attributes.get(0).description.toString();
        }
    }

    private static String findSurname(final List<PersonalName> names) {

        for (final PersonalName gedcom_name : names) {

            final String name = gedcom_name.toString();
            if (name.contains("/")) { // Slashes denote surname
                final int start = name.indexOf('/');
                final int end = name.lastIndexOf('/');
                if (end > start) {
                    return name.substring(start + 1, end);
                }
            }
        }
        return null;
    }

    private static String findFirstNames(final List<PersonalName> names) {

        final StringBuilder builder = new StringBuilder();

        for (final PersonalName gedcom_name : names) {

            if (builder.length() > 0) {
                builder.append(' ');
            }

            String name = gedcom_name.toString();
            if (name.contains("/")) { // Slashes denote surname
                final int start = name.indexOf('/');
                final int end = name.lastIndexOf('/');
                if (end > start) {
                    name = name.substring(0, start).trim() + name.substring(end + 1).trim();
                }
            }
            builder.append(name);
        }
        return builder.toString();
    }

    @Override
    public LocalDate getBirthDate() {
        return birth_date;
    }

    @Override
    public LocalDate getDeathDate() {
        return death_date;
    }

    @Override
    public void setDeathDate(final LocalDate deathDate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<IPartnership> getPartnerships() {

        List<IPartnership> partnerships = new ArrayList<>();
        for (int id : partnership_ids) {
            partnerships.add(adapter.findPartnership(id));
        }
        return partnerships;
    }

    @Override
    public IPartnership getParents() {
        return adapter.findPartnership(parent_id);
    }

    @Override
    public boolean isIllegitimate() {
        return false;
    }

    @Override
    public void recordPartnership(IPartnership partnership) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(IPerson o) {
        return Integer.compare(id, o.getId());
    }
}