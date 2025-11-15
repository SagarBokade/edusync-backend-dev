package com.project.edusync.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

/**
 * A Spring component service responsible for validating the structural integrity of
 * uploaded CSV files.
 *
 * This validator specifically checks if the CSV header row matches a predefined
 * set of columns in the exact expected order. This is a critical data integrity
 * gateway to prevent data corruption and parsing errors during import processes.
 *
 * It uses Apache Commons CSV for robust parsing, handling edge cases like
 * quoted fields.
 */
@Component
@Slf4j
public class CsvHeaderValidator {


    /**
     * Defines the single source of truth for the UIS CSV header structure.
     * The order of elements in this list is strictly enforced.
     */
    private static final List<String> EXPECTED_HEADER = Arrays.asList(
            "Table Name",
            "Column Name (Attribute)",
            "Data Type",
            "Constraints / Keys",
            "Description"
    );

    /**
     * Pre-configured, immutable CSVFormat instance.
     * This is optimized for performance by being a static final field.
     *
     * Configuration:
     * - withHeader(): Specifies that the first record is the header.
     * - withSkipHeaderRecord(false): We explicitly want to *read* the header
     * record, not skip it, so we can validate its contents.
     */
    private static final CSVFormat CSV_FILE_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(false)
            .build();

    /**
     * Validates the header of a provided CSV file InputStream against the
     * expected format.
     *
     * <p><b>Important:</b> This method will consume the InputStream. The caller is
     * responsible for closing the stream. If the caller needs to read the
     * file again (e.g., for data processing after successful validation),
     * they MUST provide a new, fresh InputStream.</p>
     *
     * @param inputStream The InputStream of the CSV file to validate. The stream
     * is *not* closed by this method.
     * @return {@code true} if the header is valid (matches EXPECTED_HEADER
     * exactly), {@code false} otherwise.
     */
    public boolean isCsvHeaderValid(InputStream inputStream) {
        if (inputStream == null) {
            log.warn("CSV validation failed: Input stream was null.");
            return false;
        }

        // We use a try-with-resources block to ensure the Reader and Parser
        // are auto-closed, even if exceptions occur.
        // The underlying inputStream is *not* closed, as per the Javadoc.
        try (
                Reader reader = new InputStreamReader(inputStream);
                CSVParser csvParser = new CSVParser(reader, CSV_FILE_FORMAT)
        ) {

            // Get the header names that were actually parsed from the first line.
            // getHeaderNames() is the correct method provided by Apache Commons CSV.
            List<String> actualHeader = csvParser.getHeaderNames();

            // This is the core integrity check. List.equals() verifies
            // both content and strict order.
            if (EXPECTED_HEADER.equals(actualHeader)) {
                log.debug("CSV header validation successful."); // DEBUG level for success
                return true;
            } else {
                // Log the validation failure at a WARN level. This is not a
                // system error (ERROR), but a data validation failure.
                // We use parameterized logging for performance.
                log.warn("CSV header validation failed. Mismatch detected.");
                log.warn("Expected: {}", EXPECTED_HEADER);
                log.warn("Found:    {}", actualHeader);
                return false;
            }

        } catch (IOException e) {
            // Log at ERROR level, as this indicates a system-level failure
            // (e.g., file unreadable, network stream broken), not a data error.
            log.error("IO Exception occurred while parsing CSV header.", e);
            return false;
        } catch (Exception e) {
            // Catch-all for other potential parsing errors (e.g., malformed CSV)
            log.error("An unexpected error occurred during CSV header validation.", e);
            return false;
        }
    }
}