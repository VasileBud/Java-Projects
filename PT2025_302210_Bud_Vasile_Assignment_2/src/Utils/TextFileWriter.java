package Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TextFileWriter {
    private static final String FILE_NAME = "log.txt";
    private BufferedWriter writer;

    public TextFileWriter() {
        try {
            writer = new BufferedWriter(new FileWriter(FILE_NAME, false));
        } catch (IOException e) {
            System.err.println("Error creating the log file: " + e.getMessage());
        }
    }

    public void write(String text) {
        try {
            writer.write(text);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error writing to the log file: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing the log file: " + e.getMessage());
        }
    }
}
