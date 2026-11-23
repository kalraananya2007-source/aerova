
package com.aerova.service;

import com.aerova.dao.FlightRepository;
import com.aerova.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FlightCsvService {

    private final FlightRepository repository = new FlightRepository();
    private final FlightManager flightManager = new FlightManager();

    // Import flights from CSV
    public int importFlights(String filePath)
            throws IOException, SQLException {

        ArrayList<String> lines =
                new ArrayList<>(Files.readAllLines(Path.of(filePath)));

        if (lines.size() <= 1) {
            return 0;
        }

        int imported = 0;

        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                continue;
            }

            try {
                String[] data = parseCsvLine(lines.get(i));

                if (data.length != 9) {
                    throw new IllegalArgumentException(
                            "Invalid number of CSV columns."
                    );
                }

                String flightNumber = data[0].trim();
                String airline = data[1].trim();
                int aircraftId = Integer.parseInt(data[2].trim());

                Aircraft aircraft =
                        repository.findAircraftById(aircraftId);

                if (aircraft == null) {
                    throw new IllegalArgumentException(
                            "Aircraft not found: " + aircraftId
                    );
                }

                FlightType type =
                        FlightType.valueOf(data[3].trim().toUpperCase());

                LocalDateTime scheduledTime =
                        LocalDateTime.parse(data[4].trim());

                boolean emergency =
                        Boolean.parseBoolean(data[5].trim());

                Flight flight = new Flight(
                        flightNumber, aircraft, type,
                        scheduledTime, emergency
                );

                flight.setAirline(airline);

                flight.setStatus(
                        FlightStatus.valueOf(data[6].trim().toUpperCase())
                );

                flight.setActualTime(
                        data[7].isBlank()
                                ? null
                                : LocalDateTime.parse(data[7].trim())
                );

                flight.setDelayMinutes(
                        Integer.parseInt(data[8].trim())
                );

                flightManager.addFlight(flight);
                imported++;

            } catch (RuntimeException | SQLException e) {
                throw new IllegalArgumentException(
                        "Error in CSV row " + (i + 1) + ": "
                                + e.getMessage(), e
                );
            }
        }

        return imported;
    }

    // Export all flights to CSV
    public void exportFlights(String filePath)
            throws IOException, SQLException {

        StringBuilder csv = new StringBuilder();

        csv.append(
                "flightNumber,airline,aircraftId,flightType,"
                + "scheduledTime,emergency,status,actualTime,delayMinutes\n"
        );

        for (Flight flight : flightManager.getAllFlights()) {

            csv.append(escape(flight.getFlightNumber())).append(",");
            csv.append(escape(flight.getAirline())).append(",");

            csv.append(
                    flight.getAircraft() == null
                            ? ""
                            : flight.getAircraft().getAircraftId()
            ).append(",");

            csv.append(flight.getFlightType()).append(",");
            csv.append(flight.getScheduledTime()).append(",");
            csv.append(flight.isEmergency()).append(",");
            csv.append(flight.getStatus()).append(",");

            csv.append(
                    flight.getActualTime() == null
                            ? ""
                            : flight.getActualTime()
            ).append(",");

            csv.append(flight.getDelayMinutes()).append("\n");
        }

        Files.writeString(Path.of(filePath), csv.toString());
    }

    // Parse CSV row, including quoted values and commas
    private String[] parseCsvLine(String line) {

        ArrayList<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                if (insideQuotes && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {
                    value.append('"');
                    i++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (ch == ',' && !insideQuotes) {
                values.add(value.toString());
                value.setLength(0);
            } else {
                value.append(ch);
            }
        }

        values.add(value.toString());
        return values.toArray(new String[0]);
    }

    // Escape values before writing to CSV
    private String escape(String value) {
        if (value == null) {
            return "";
        }

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
