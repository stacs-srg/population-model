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
package uk.ac.standrews.cs.basic_model.tools;

import uk.ac.standrews.cs.basic_model.model.IPopulation;
import uk.ac.standrews.cs.basic_model.model.in_memory.CompactPopulationAdapter;
import uk.ac.standrews.cs.utilities.ProgressIndicator;

/**
 * Generates a population in a series of independent batches, and exports to the database.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @see <a href="http://digitisingscotland.cs.st-andrews.ac.uk/population_model/scripts/generate_population.html">Launch script documentation</a>}
 */
public class GenerateCompactPopulationInDB extends AbstractPopulationToDB {

    public static void main(final String[] args) throws Exception {

        new GenerateCompactPopulationInDB().export(args);
    }

    public IPopulation getPopulation(final int batch_size, final ProgressIndicator indicator) throws Exception {

        return new CompactPopulationAdapter(batch_size, indicator);
    }
}