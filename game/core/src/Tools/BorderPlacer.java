package com.mygdx.game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class BorderPlacer {
    public static void main(String[] args) {
        String csvFile = "/Users/pavelpavliv/IdeaProjects/iti0301-2024-meeskond-suva/game/core/src/com/mygdx/game/csv.csv";
        String outputFile = "/Users/pavelpavliv/IdeaProjects/iti0301-2024-meeskond-suva/game/core/src/com/mygdx/game/border.txt";
        String line = "";
        String cvsSplitBy = ",";
        Random random = new Random();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile));
             PrintWriter writer = new PrintWriter(outputFile)) {

            while ((line = br.readLine()) != null) {
                String[] values = line.split(cvsSplitBy);

                for (int i = 0; i < values.length; i++) {
                    if (values[i].equals("158") || values[i].equals("159")) {
                        int randomNum = 360 + random.nextInt(4); // Random number between 361 and 364
                        writer.print(randomNum);
                    } else {
                        writer.print(0);
                    }
                    writer.print(",");
                }

                // Add new line after each row
                writer.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}