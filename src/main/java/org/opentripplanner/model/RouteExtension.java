package org.opentripplanner.model;

import org.onebusaway.csv_entities.schema.annotations.CsvField;

public class RouteExtension {


    public RouteExtension() {
    }

    public RouteExtension(String competentAuthority) {
        this.competentAuthority = competentAuthority;
    }

    @CsvField(
            name = "competent_authority",
            optional = true
    )
    private String competentAuthority;

    @CsvField(
            name = "route_desc",
            optional = true
    )
    private String routeDesc;

    public String getCompetentAuthority() {
        return competentAuthority;
    }

    public void setCompetentAuthority(String competentAuthority) {
        this.competentAuthority = competentAuthority;
    }

    public String getRouteDesc() {
        return routeDesc;
    }

    public void setRouteDesc(String routeDesc) {
        this.routeDesc = routeDesc;
    }
}
