package demo.prorotypeinvocemaker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class InvoiceIdGenerator {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static String lastTimestamp = "";
    private static final AtomicInteger sequence = new AtomicInteger(0);

    public static synchronized String generateId(String customerType) {
        String prefix = "Company".equals(customerType) ? "PO-" : "FO-";

        String currentTimestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);

        // If we are in the same second, increment sequence
        if (currentTimestamp.equals(lastTimestamp)) {
            sequence.incrementAndGet();
        } else {
            // New second, reset sequence
            lastTimestamp = currentTimestamp;
            sequence.set(0);
        }

        // Format: PO-20231027-143001-001
        return String.format("%s%s-%03d", prefix, currentTimestamp, sequence.get());
    }
}
