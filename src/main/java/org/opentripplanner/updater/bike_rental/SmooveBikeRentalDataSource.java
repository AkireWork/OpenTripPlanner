/* This program is free software: you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package org.opentripplanner.updater.bike_rental;

import java.util.HashSet;

import org.opentripplanner.routing.bike_rental.BikeRentalStation;
import org.opentripplanner.util.NonLocalizedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Implementation of a BikeRentalDataSource for the Smoove GIR SabiWeb used in Helsinki.
 * @see BikeRentalDataSource
 */
public class SmooveBikeRentalDataSource extends GenericJsonBikeRentalDataSource {

    private static final Logger log = LoggerFactory.getLogger(SmooveBikeRentalDataSource.class);

    private String networkName;
    private boolean allowOverloading = false;

    public SmooveBikeRentalDataSource(String networkName, boolean allowOverloading) {
        super("result");
        this.networkName = defaultIfEmpty(networkName, "smoove");
	this.allowOverloading = allowOverloading;
    }

    private String defaultIfEmpty(String value, String defaultValue) {
        if (value == null || value.isEmpty())
            return defaultValue;

        return value;
    }

    /**
     * <pre>
     * {
     *    "result" : [
     *       {
     *          "name" : "004 Hamn",
     *          "operative" : true,
     *          "coordinates" : "60.167913,24.952269",
     *          "style" : "",
     *          "avl_bikes" : 1,
     *          "free_slots" : 11,
     *          "total_slots" : 12,
     *       },
     *       ...
     *    ]
     * }
     * </pre>
     */
    public BikeRentalStation makeStation(JsonNode node) {
        BikeRentalStation station = new BikeRentalStation();
        station.id = node.path("name").asText().split("\\s", 2)[0];
        station.name = new NonLocalizedString(node.path("name").asText().split("\\s", 2)[1]);
        station.state = node.path("style").asText();
        station.networks = new HashSet<String>();
        station.networks.add(this.networkName);
	station.allowOverloading = this.allowOverloading;
        try {
            station.y = Double.parseDouble(node.path("coordinates").asText().split(",")[0].trim());
            station.x = Double.parseDouble(node.path("coordinates").asText().split(",")[1].trim());
            if (station.state.equals("Station on")) {
                station.bikesAvailable = node.path("avl_bikes").asInt();
                station.spacesAvailable = node.path("free_slots").asInt();
                station.capacity = node.path("total_slots").asInt();
            } else {
                station.bikesAvailable = 0;
                station.spacesAvailable = 0;
                station.capacity = node.path("total_slots").asInt();
            }
            return station;
        } catch (NumberFormatException e) {
            // E.g. coordinates is empty
            log.info("Error parsing bike rental station " + station.id, e);
            return null;
        }
    }
}
