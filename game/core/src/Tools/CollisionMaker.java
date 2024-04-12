package Tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class CollisionMaker {
    public static void main(String[] args) {
        String csvFile = " ";
        String outputFile = " ";
        String line = "";
        String cvsSplitBy = ",";
        int lineCount = 0;
        int id = 46;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile));
             PrintWriter writer = new PrintWriter(outputFile)) {

            while ((line = br.readLine()) != null) {
                String[] values = line.split(cvsSplitBy);

                for (int i = 0; i < values.length; i++) {
                    if (values[i].equals("158") || values[i].equals("159")) {
                        writer.println("<object id=\"" + id + "\" x=\"" + i * 32  + "\" y=\"" + lineCount * 32 + "\" width=\"32\" height=\"32\"/>");
                        id++;
                    }
                }

                lineCount++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
