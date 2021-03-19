package org.opentripplanner.updater.bike_rental;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.StringEntity;
import org.opentripplanner.routing.bike_rental.BikeRentalStation;
import org.opentripplanner.util.HttpUtils;
import org.opentripplanner.util.NonLocalizedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class TartuBicycleRentDataSource extends GenericJsonBikeRentalDataSource {
    private static final Logger log = LoggerFactory.getLogger(TartuBicycleRentDataSource.class);

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

    @Override
    public boolean update() {
        try {
            InputStream data = null;

            URL url2 = new URL(super.getUrl());

            String proto = url2.getProtocol();
            if (proto.equals("http") || proto.equals("https")) {
                data = HttpUtils.getPostData(super.getUrl(), "Content-Type", "application/json", new StringEntity("{}"));
            } else {
                // Local file probably, try standard java
                data = url2.openStream();
            }
            // TODO handle optional GBFS files, where it's not warning-worthy that they don't exist.
            if (data == null) {
                log.warn("Failed to get data from url " + super.getUrl());
                return false;
            }
            parseJSON(data);
            data.close();
        } catch (IllegalArgumentException e) {
            log.warn("Error parsing bike rental feed from " + super.getUrl(), e);
            return false;
        } catch (JsonProcessingException e) {
            log.warn("Error parsing bike rental feed from " + super.getUrl() + "(bad JSON of some sort)", e);
            return false;
        } catch (IOException e) {
            log.warn("Error reading bike rental feed from " + super.getUrl(), e);
            return false;
        }
        return true;
    }

    private void parseJSON(InputStream dataStream) throws IllegalArgumentException, IOException {

        ArrayList<BikeRentalStation> out = new ArrayList<>();

        String rentalString = convertStreamToString(dataStream);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(rentalString);

        for (int i = 0; i < rootNode.size(); i++) {
            // TODO can we use foreach? for (JsonNode node : rootNode) ...
            JsonNode node = rootNode.get(i);
            if (node == null) {
                continue;
            }
            BikeRentalStation brstation = makeStation(node);
            if (brstation != null)
                out.add(brstation);
        }
        synchronized(this) {
            stations = out;
        }
    }

    private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner scanner = null;
        String result="";
        try {

            scanner = new java.util.Scanner(is).useDelimiter("\\A");
            result = scanner.hasNext() ? scanner.next() : "";
            scanner.close();
        }
        finally
        {
            if(scanner!=null)
                scanner.close();
        }
        return result;

    }
}
