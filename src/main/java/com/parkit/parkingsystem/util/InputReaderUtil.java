package com.parkit.parkingsystem.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class InputReaderUtil {

    private static Scanner scan = new Scanner(System.in);
    private static final Logger logger = LogManager.getLogger("InputReaderUtil");

    public int readSelection() {
        try {
            String input = scan.nextLine();
            if (input == null || input.trim().isEmpty()) {
                System.out.println("Aucune entrée détectée. Veuillez entrer un nombre valide.");
                return -1;
            }
            int number = Integer.parseInt(input);
            return number;
        } catch(Exception e) {
            logger.error("Error while reading user input from Shell", e);
            System.out.println("Error reading input. Please enter valid number for proceeding further");
            return -1;
        }
    }


    public String readVehicleRegistrationNumber() throws Exception {
        try {
            String vehicleRegNumber= scan.nextLine();
            if(vehicleRegNumber == null || vehicleRegNumber.trim().length()==0) {
                throw new IllegalArgumentException("Invalid input provided");
            }
            return vehicleRegNumber;
        }catch(Exception e){
            logger.error("Error while reading user input from Shell", e);
            System.out.println("Error reading input. Please enter a valid string for vehicle registration number");
            throw e;
        }
    }


}
