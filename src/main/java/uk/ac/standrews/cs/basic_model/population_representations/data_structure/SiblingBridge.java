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
package uk.ac.standrews.cs.basic_model.population_representations.data_structure;

import uk.ac.standrews.cs.basic_model.population_representations.types.SiblingType;

/**
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 *
 */
public class SiblingBridge extends IntermediaryLinkObject {

	private SiblingType siblingType;

	public SiblingBridge(int id, String ref) {
		this.id = id;
		this.ref = ref;
		siblingType = SiblingType.FULL_SIBLINGS;
	}

	public Link[] getSibling1PotentialLinks() {
		return person1;
	}

	public Link[] getSibling2PotentialLinks() {
		return person2;
	}

	public SiblingType getSiblingType() {
		return siblingType;
	}

}