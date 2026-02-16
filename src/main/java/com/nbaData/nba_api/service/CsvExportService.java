package com.nbaData.nba_api.service;

import com.nbaData.nba_api.response.PlayerIndexResponse;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CsvExportService {

    public Path exportPlayersToCsv(PlayerIndexResponse response, String filePath) throws IOException {
        Path path = Path.of(filePath);
        Files.createDirectories(path.getParent());

        PlayerIndexResponse.ResultSet resultSet = response.getResultSets().get(0);
        List<String> headers = resultSet.getHeaders();
        List<List<Object>> rows = resultSet.getRowSet();

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(headers.stream().map(this::escapeCsvField).collect(Collectors.joining(",")));
            writer.newLine();

            for (List<Object> row : rows) {
                String line = row.stream()
                        .map(val -> val == null ? "" : val.toString())
                        .map(this::escapeCsvField)
                        .collect(Collectors.joining(","));
                writer.write(line);
                writer.newLine();
            }
        }

        return path;
    }

    private String escapeCsvField(String field) {
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}
