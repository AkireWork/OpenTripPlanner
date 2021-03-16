package org.opentripplanner.updater.bike_rental;

import com.fasterxml.jackson.databind.JsonNode;
import org.opentripplanner.routing.bike_rental.BikeRentalStation;
import org.opentripplanner.util.NonLocalizedString;

public class TartuBicycleRentDataSource extends GenericJsonBikeRentalDataSource {
    @Override
    public BikeRentalStation makeStation(JsonNode rentalStationNode) {
        BikeRentalStation bikeRentalStation = new BikeRentalStation();
        bikeRentalStation.id = rentalStationNode.get("id").textValue();
        bikeRentalStation.x = rentalStationNode.get("area").get("latitude").doubleValue() / 1000000.0;
        bikeRentalStation.y = rentalStationNode.get("area").get("longitude").doubleValue() / 1000000.0;
        bikeRentalStation.name = new NonLocalizedString(rentalStationNode.get("name").textValue());
        bikeRentalStation.bikesAvailable =
                rentalStationNode.get("overFullCycleStockingCount").intValue() - rentalStationNode.get("freeSpacesCount").intValue();
        bikeRentalStation.spacesAvailable =
                rentalStationNode.get("fullCycleStockingCount").intValue() - rentalStationNode.get("freeDocksCount").intValue();
        return bikeRentalStation;
    }
}
