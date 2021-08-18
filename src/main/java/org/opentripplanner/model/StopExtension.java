package org.opentripplanner.model;

import org.onebusaway.csv_entities.schema.annotations.CsvField;

public class StopExtension {

    @CsvField(
            name = "stop_area",
            optional = true
    )
    private String stopArea;

    public StopExtension() {}

    public StopExtension(String stopArea) {
        this.stopArea = stopArea;
    }

    public String getStopArea() {
        return stopArea;
    }

    public void setStopArea(String stopArea) {
        this.stopArea = stopArea;
    }
}
