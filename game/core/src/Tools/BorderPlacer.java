package Tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class BorderPlacer {
    public static void main(String[] args) {
        String csvFile = " ";
        String outputFile = " ";
        String line = "";
        String cvsSplitBy = ",";
        Random random = new Random();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile));
             PrintWriter writer = new PrintWriter(outputFile)) {

            while ((line = br.readLine()) != null) {
                String[] values = line.split(cvsSplitBy);

                for (int i = 0; i < values.length; i++) {
                    if (values[i].equals("158") || values[i].equals("159")) {
                        int randomNum = 360 + random.nextInt(4);
                        writer.print(randomNum);
                    } else {
                        writer.print(0);
                    }
                    writer.print(",");
                }
                writer.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}