package com.fooddelivery.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileUtil — Central utility class for all file read/write operations.
 *
 * This class handles reading and writing to .txt data files using
 * BufferedReader and BufferedWriter — as required by the SE1020 assignment.
 *
 * Data files used:
 *   - users.txt               (customers and admin users)
 *   - delivery_persons.txt    (delivery staff)
 *   - menu_items.txt          (food items and beverages)
 *   - orders.txt              (customer orders)
 *   - order_items.txt         (items within each order)
 *
 * File format: pipe-delimited lines, one record per line.
 * Example: 1|John|john@email.com|password123|0771234567|customer|No 5, Galle Road|3
 *
 * Member 1 is responsible for this class.
 */
@Component
public class FileUtil {

    // Injected from application.properties — the path to the data folder
    @Value("${data.files.path}")
    private String dataFilesPath;

    // ============================================================
    //  CORE READ OPERATION
    // ============================================================

    /**
     * Reads all lines from a given file.
     * Uses BufferedReader for efficient reading line by line.
     *
     * @param fileName name of the file (e.g., "users.txt")
     * @return list of non-empty lines from the file
     */
    public List<String> readAllLines(String fileName) {
        List<String> lines = new ArrayList<>();
        String filePath = dataFilesPath + fileName;

        // Ensure the file exists before reading
        ensureFileExists(filePath);

        // BufferedReader reads file line by line efficiently
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // readLine() returns null when there are no more lines
            while ((line = reader.readLine()) != null) {
                // Skip blank lines and comment lines starting with #
                if (!line.trim().isEmpty() && !line.startsWith("#")) {
                    lines.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("[FileUtil] Error reading " + fileName + ": " + e.getMessage());
        }

        return lines;
    }

    // ============================================================
    //  CORE WRITE OPERATION — Overwrite entire file
    // ============================================================

    /**
     * Writes a list of lines to a file, completely replacing its contents.
     * Used for UPDATE and DELETE operations (rewrite the whole file).
     *
     * Uses BufferedWriter for efficient writing.
     *
     * @param fileName name of the file (e.g., "users.txt")
     * @param lines    the complete list of lines to write
     */
    public void writeAllLines(String fileName, List<String> lines) {
        String filePath = dataFilesPath + fileName;
        ensureFileExists(filePath);

        // BufferedWriter writes to file efficiently
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine(); // writes system-appropriate line ending
            }
        } catch (IOException e) {
            System.err.println("[FileUtil] Error writing " + fileName + ": " + e.getMessage());
        }
    }

    // ============================================================
    //  APPEND OPERATION — Add one new record
    // ============================================================

    /**
     * Appends a single new line to the end of a file.
     * Used for CREATE operations — adds a new record without reading the whole file.
     *
     * @param fileName name of the file
     * @param line     the pipe-delimited record to append
     */
    public void appendLine(String fileName, String line) {
        String filePath = dataFilesPath + fileName;
        ensureFileExists(filePath);

        // FileWriter with append=true adds to the end of the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("[FileUtil] Error appending to " + fileName + ": " + e.getMessage());
        }
    }

    // ============================================================
    //  ID GENERATION — Auto-increment from last record
    // ============================================================

    /**
     * Generates the next available ID for a file by finding the highest current ID.
     * Reads all lines, extracts the first field (ID), and returns max + 1.
     *
     * @param fileName the file to check
     * @return next available integer ID
     */
    public int getNextId(String fileName) {
        List<String> lines = readAllLines(fileName);
        int maxId = 0;
        for (String line : lines) {
            try {
                // First field before the pipe is the ID
                int id = Integer.parseInt(line.split("\\|")[0]);
                if (id > maxId) maxId = id;
            } catch (NumberFormatException e) {
                // Skip lines that don't start with a number
            }
        }
        return maxId + 1; // next ID = highest existing ID + 1
    }

    // ============================================================
    //  HELPER — Ensure file exists
    // ============================================================

    /**
     * Creates the file (and any parent directories) if it doesn't already exist.
     * This prevents FileNotFoundException when the app first starts.
     *
     * @param filePath full path to the file
     */
    private void ensureFileExists(String filePath) {
        File file = new File(filePath);
        try {
            // Create parent directories if they don't exist
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            // Create the file itself if it doesn't exist
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("[FileUtil] Created new data file: " + filePath);
            }
        } catch (IOException e) {
            System.err.println("[FileUtil] Could not create file " + filePath + ": " + e.getMessage());
        }
    }
}
