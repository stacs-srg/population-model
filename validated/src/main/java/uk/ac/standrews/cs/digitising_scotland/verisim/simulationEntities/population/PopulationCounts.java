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
package uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.population;

import uk.ac.standrews.cs.digitising_scotland.verisim.simulationEntities.person.IPersonExtended;

/**
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationCounts {

    private int createdMales = 0;
    private int createdFemales = 0;

    private int livingMales = 0;
    private int livingFemales = 0;

    private int createdPartnerships = 0;
    private int currentPartnerships = 0;

    private int maxPopulation = 0;

    private int illegitimateBirths = 0;

    public void newMale(int numberOf) {
        createdMales += numberOf;
        livingMales += numberOf;
    }

    public void newMale() {
        newMale(1);
    }

    public void newFemale(int numberOf) {
        createdFemales += numberOf;
        livingFemales += numberOf;
    }

    public void newFemale() {
        newFemale(1);
    }

    public void newPartnership(int numberOf) {
        createdPartnerships += numberOf;
        currentPartnerships += numberOf;
    }

    public void newPartnership() {
        newPartnership(1);
    }

    public void maleDeath(int numberOf) {
        livingMales -= numberOf;
    }

    public void maleDeath() {
        maleDeath(1);
    }

    public void femaleDeath(int numberOf) {
        livingFemales -= numberOf;
    }

    public void femaleDeath() {
        femaleDeath(1);
    }

    public void death(IPersonExtended deceased) {
        if(Character.toLowerCase(deceased.getSex()) == 'm') {
            maleDeath();
        } else {
            femaleDeath();
        }
    }

    public void newIllegitimateBirth(int numberOf) {
        illegitimateBirths += numberOf;
    }

    public void newIllegitimateBirth() {
        newIllegitimateBirth(1);
    }

    public void partnershipEnd(int numberOf) {
        currentPartnerships -= numberOf;
    }

    public void partnershipEnd() {
        partnershipEnd(1);
    }

    public int getLivingMales() {
        return livingMales;
    }

    public int getLivingFemales() {
        return livingFemales;
    }

    public int getCurrentPartnerships() {
        return currentPartnerships;
    }

    public double getAllTimeSexRatio() {
        return createdMales / ((double) (createdFemales + createdMales));
    }

    public double getLivingSexRatio() {
        return livingMales / ((double) (livingFemales + livingMales));
    }

    public void updateMaxPopulation(int populationSize) {
        if(populationSize > maxPopulation) {
            maxPopulation = populationSize;
        }
    }

    public int getPeakPopulationSize() {
        return maxPopulation;
    }

    public int getCreatedPeople() {
        return createdFemales + createdMales;
    }

    public int getIllegitimateBirths() {
        return illegitimateBirths;
    }
}