package Tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class CrystalPlacer {
    public static void main(String[] args) {
        String csvFile = " ";
        String outputFile = " ";
        String line = "";
        String cvsSplitBy = ",";
        int lineCount = 0;
        int id = 1404;
        Random random = new Random();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile));
             PrintWriter writer = new PrintWriter(outputFile)) {

            while ((line = br.readLine()) != null) {
                String[] values = line.split(cvsSplitBy);

                for (int i = 0; i < values.length; i++) {
                    if (values[i].equals("1752")) {
                        int randomNum = random.nextInt(100);
                        if (randomNum == 77) {
                            writer.println("<object id=\"" + id + "\" x=\"" + i * 32  + "\" y=\"" + lineCount * 32 + "\">\n <point/>\n</object>");
                            id++;
                        }
                    }
                }

                lineCount++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
