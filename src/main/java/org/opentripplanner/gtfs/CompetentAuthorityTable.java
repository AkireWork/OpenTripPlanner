package org.opentripplanner.gtfs;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

public class CompetentAuthorityTable {
    private static final Logger LOG = LoggerFactory.getLogger(CompetentAuthorityTable.class);

    private Map<String, String> authorities = Maps.newHashMap();

    public CompetentAuthorityTable () {
        String fileLocation = "/org/opentripplanner/competent_authority.txt";
        InputStream inputStream = getClass().getResourceAsStream(fileLocation);

        try (Scanner scanner = new Scanner(inputStream)) {
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                final String[] parts = line.split("\\s*,\\s*");
                authorities.put(parts[0], parts[1]);
            }
        }

    }

    public Map<String, String> getAuthorities() {
        return authorities;
    }
}
