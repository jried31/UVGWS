/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navigation.server;

import au.com.bytecode.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import navigation.util.Constants;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author user510 This class parses the file received from the NSF site and a
 * CIE file.
 */
public class NSFParser {

    // Returns a map that contains wavelengths as keys and its correspondig CIE values.
    public Map<String, Double> weightMap() throws FileNotFoundException, IOException {
        File f = new File(Constants.PATH + Constants.CIE);
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line;
        Map<String, Double> map = new HashMap<String, Double>();
        while ((line = br.readLine()) != null) {
            if (line.matches("^[0-9]+(\\.[0-9])*\t[0-9](\\.[0-9]+)*")) {
                String wavelength = line.substring(0, line.indexOf("\t"));
                String weight = line.substring(line.indexOf("\t") + 1);
                map.put(wavelength, Double.parseDouble(weight));
            }
        }

        return map;
    }

    // Proof of concept. Do not use in application.
    // Returns a set of times where Vitamin D consumption is possible.
    public Set<String> getVitaminD() throws Exception {
        CSVReader reader = new CSVReader(new FileReader(Constants.PATH + Constants.UV_IRRADIANCE));
        String[] nextLine;

        //Reading row by Row (therefore 1st row are the columns of interest)
        // First mark interested column numbers, which may not be the same
        // between excel files because of custom field selections.
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        int timeIndex = 1;
        nextLine = reader.readNext();
        for (int i = 0; i < nextLine.length; i++) {
            // For Vitamin D to be effective, we want values within the 290 to
            // 300 wavelength. Therefore, we mark column indexes that are within those wavelengths and the time
            String line = nextLine[i];
            if (line.startsWith("E29") || line.equals("E300")) {
                indexes.add(i);
            }
            if (line.equals("TimeB")) {
                timeIndex = i;
            }
        }

        //Now we read the next line which contains teh data we care about ONLY in the column indexes of interest
        Set<String> vitaminDSet = new HashSet<String>();
        while ((nextLine = reader.readNext()) != null) {
            for (int j = 0; j < indexes.size(); j++) {
                // Only accept readings that are greater than 0.
                String line = nextLine[indexes.get(j)];
                if (Double.parseDouble(line) > 0) {
                    // Vitamin D is consummed at this time so store it.
                    vitaminDSet.add(nextLine[timeIndex]);
                }
            }
        }

        return vitaminDSet;
    }

    // Proof of concept. Do not use in application.
    // Iterates the excel file and calculates the UV index using the summation
    // of the different wavelength strength and cie value products.
    public Map<String, Double> getUVIndex() throws Exception {
        CSVReader reader = new CSVReader(new FileReader(Constants.PATH + Constants.UV_IRRADIANCE));
        String[] nextLine;

        // First mark interested column numbers, which may not be the same
        // between excel files because of custom field selections.
        Map<String, Integer> wavelengths = new HashMap<String, Integer>();
        int timeIndex = 1;
        nextLine = reader.readNext();
        for (int i = 0; i < nextLine.length; i++) {
            // Stores the wavelengths.
            String line = nextLine[i];
            if (line.matches("^E[2-4][0-9]{2}(_[0-9])*")) {
                if (line.contains("_")) {
                    wavelengths.put(line.substring(1, line.indexOf("_")), i);
                } else {
                    wavelengths.put(line.substring(1), i);
                }
            }
            if (line.equals("TimeB")) {
                timeIndex = i;
            }
        }
        //This is a map correlating Wavelength to file Column index
        System.out.println(wavelengths);

        // Stores the interested times and corresponding UV irradiances in map.
        // HashMap that stores time as key and UV irradiance as value.
        Map<String, Double> uvIndex = new HashMap<String, Double>();
        Map<String, Double> weightMap = weightMap();
        while ((nextLine = reader.readNext()) != null) {
            for (String wavelength : wavelengths.keySet()) {
                int index = wavelengths.get(wavelength);
                // Reading values greater than 0.
                Double strength = Double.parseDouble(nextLine[index]);
                if(strength < 0)strength = 0.0;
                // Calculate value of UV index based on the summation
                // of predetermined strength * weight measurements.
                double weight = weightMap.get(wavelength);
                double irradiance = strength * weight;
                if (!uvIndex.containsKey(nextLine[timeIndex])) {
                    uvIndex.put(nextLine[timeIndex], irradiance);
                } else {
                    uvIndex.put(nextLine[timeIndex], uvIndex.get(nextLine[timeIndex]) + irradiance);
                }
            }
        }

        for (String s : uvIndex.keySet()) {
            uvIndex.put(s, uvIndex.get(s) / 25);
            System.out.println(s + " : " + uvIndex.get(s));
        }
        System.out.println(uvIndex.size());

        return uvIndex;
    }
}
