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
package uk.ac.standrews.cs.basic_model.model.in_memory;

import uk.ac.standrews.cs.basic_model.model.IPerson;
import uk.ac.standrews.cs.utilities.BitManipulation;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A compact representation of a person, designed for minimal space overhead.
 * Encodes multiple attributes into a field wherever possible.
 * Dates are encoded as integers.
 * <p/>
 * This class is not thread-safe.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
@NotThreadSafe
class CompactPerson {

    private static final int POSITION_OF_MALE_BIT = 0;
    private static final int POSITION_OF_PARENTS_BIT = 1;
    private static final int POSITION_OF_INCOMERS_BIT = 2;
    private static final int POSITION_OF_MARKED_BIT = 3;

    protected int id;
    protected int birth_date = -1;
    protected int death_date = -1;

    private List<CompactPartnership> partnership_list;
    private byte bits = 0; // Used to store various boolean properties.

    /**
     * Creates a person.
     *
     * @param birth_date the date of birth represented in days elapsed from the start of the simulation
     * @param male       true if the person is male
     */
    protected CompactPerson(final int birth_date, final boolean male) {

        this.birth_date = birth_date;
        setMale(male);
    }

    protected CompactPerson(final int birth_date, final boolean male, final int id) {

        this(birth_date, male);
        this.id = id;
    }

    /**
     * Gets an id for the person.
     *
     * @return the id of this person
     */
    protected int getId() {

        return id;
    }

    protected CompactPartnership mostRecentPartnership() {

        return partnership_list != null ? partnership_list.get(getPartnerships().size() - 1) : null;
    }

    protected char getSex() {

        return isMale() ? IPerson.MALE : IPerson.FEMALE;
    }

    /**
     * Tests whether this person is male.
     *
     * @return true if this person is male
     */
    protected boolean isMale() {

        return BitManipulation.readBit(bits, POSITION_OF_MALE_BIT);
    }

    /**
     * Sets the sex of this person.
     *
     * @param male true if this person is male
     */
    protected void setMale(final boolean male) {

        bits = BitManipulation.writeBit(bits, male, POSITION_OF_MALE_BIT);
    }

    protected boolean isMarked() {

        return BitManipulation.readBit(bits, POSITION_OF_MARKED_BIT);
    }

    /**
     * Records if a record has been visited.
     *
     * @param marked true if a record has been visited
     */
    protected void setMarked(final boolean marked) {

        bits = BitManipulation.writeBit(bits, marked, POSITION_OF_MARKED_BIT);
    }

    protected boolean hasNoParents() {

        return !BitManipulation.readBit(bits, POSITION_OF_PARENTS_BIT);
    }

    /**
     * Records this person as having parents.
     */
    protected void setHasParents() {

        bits = BitManipulation.writeBit(bits, true, POSITION_OF_PARENTS_BIT);
    }

    protected boolean isIncomer() {

        return BitManipulation.readBit(bits, POSITION_OF_INCOMERS_BIT);
    }

    /**
     * Records this person as being an incomer.
     */
    protected void setIsIncomer() {

        bits = BitManipulation.writeBit(bits, true, POSITION_OF_INCOMERS_BIT);
    }

    /**
     * Get the date of birth of this person.
     *
     * @return the date of birth.
     */
    protected int getBirthDate() {

        return birth_date;
    }

    protected int getDeathDate() {

        return death_date;
    }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();

        builder.append(getClass().getSimpleName());
        builder.append('{');
        builder.append(getSex());
        builder.append('-');
        builder.append(getBirthDate());
        builder.append('-');
        if (getDeathDate() != -1) {
            builder.append(getDeathDate());
        }
        if (getPartnerships() != null) {
            builder.append(", p:");
            builder.append(getPartnerships().size());
        }
        builder.append('}');

        return builder.toString();
    }

    /**
     * Get the list of partnerships in which this person has been a member.
     *
     * @return the partnerships
     */
    protected List<CompactPartnership> getPartnerships() {

        return partnership_list;
    }

    /**
     * Setter for partnership_list.
     *
     * @param partnership_list list to set
     */
    protected void setPartnerships(final List<CompactPartnership> partnership_list) {

        this.partnership_list = partnership_list;
    }

    protected void addPartnership(final CompactPartnership partnership) {

        if (partnership_list == null) {
            partnership_list = new ArrayList<>();
        }
        partnership_list.add(partnership);
        Collections.sort(partnership_list);
    }
}