package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.DateForParkingApp;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class ParkingService {

    private static final Logger logger = LogManager.getLogger("ParkingService");

    private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

    private InputReaderUtil inputReaderUtil;
    private ParkingSpotDAO parkingSpotDAO;
    private  TicketDAO ticketDAO;
    private final DateForParkingApp dateForParkingApp ;

    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO){
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
		this.dateForParkingApp = new DateForParkingApp();
    }

    public ParkingService(ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
		this.dateForParkingApp = new DateForParkingApp();
    }
    
 

    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO, DateForParkingApp dateForParkingApp){
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
        this.dateForParkingApp = dateForParkingApp;
    }

    /**
     * Cette méthode gère le processus d'entrée d'un véhicule dans le parking.
     * Elle trouve une place de parking disponible, crée un ticket pour le véhicule et enregistre le ticket.
     * 
     */
    public void processIncomingVehicle() {
        try{
            String vehicleRegNumber = getVehichleRegNumber();

            
            if (ticketDAO.doesRegNumberExist(vehicleRegNumber)) {
                System.out.println("Ce numéro d'immatriculation du véhicule existe déjà !");
                return;
            }

            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            if(parkingSpot !=null && parkingSpot.getId() > 0){
                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot);
                Date inTime = new Date();
                Ticket ticket = new Ticket();
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(0);
                ticket.setInTime(inTime);
                ticket.setOutTime(null);
                ticketDAO.saveTicket(ticket);
                System.out.println("Bienvenue sur notre parking !");
                System.out.println("Veuillez garer votre véhicule avec le numéro de place  " + parkingSpot.getId());
                System.out.println("Heure d'entrée enregistrée pour le numéro de véhicule : " + vehicleRegNumber + " is: " + inTime);
            }
        }catch(Exception e){
            logger.error("Unable to process incoming vehicle",e);
        }
    }

    public String getVehichleRegNumber() throws Exception {
        System.out.println("Veuillez saisir le numéro d'immatriculation du véhicule et appuyer sur la touche Entrée.");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    
    public ParkingSpot getNextParkingNumberIfAvailable(){
        int parkingNumber=0;
        ParkingSpot parkingSpot = null;
        try{
            ParkingType parkingType = getVehichleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            if(parkingNumber > 0){
                parkingSpot = new ParkingSpot(parkingNumber,parkingType, true);
            }else{
                throw new Exception("Pas de place disponible dans le parking");
            }
        }catch(IllegalArgumentException ie){
            logger.error("Erreur lors de l'analyse de l'entrée utilisateur pour le type de véhicule", ie);
        }catch(Exception e){
            logger.error("Erreur lors de la récupération de la prochaine place de parking disponible", e);
        }
        return parkingSpot;
    }
   
   
    
  /*  public ParkingSpot getNextParkingNumberIfAvailable(){
        int parkingNumber=0;
        ParkingSpot parkingSpot = null;
        
            ParkingType parkingType = getVehichleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            if(parkingNumber > 0){
                parkingSpot = new ParkingSpot(parkingNumber,parkingType, true);
            }else{
            	System.out.println("Pas de place disponible dans le parking");
            }
       
        return parkingSpot;
    }
    
    */
    
    private ParkingType getVehichleType(){
        System.out.println("Please select vehicle type from menu");
        System.out.println("1 CAR");
        System.out.println("2 BIKE");
        int input = inputReaderUtil.readSelection();
        switch(input){
            case 1: {
                return ParkingType.CAR;
            }
            case 2: {
                return ParkingType.BIKE;
            }
            default: {
                System.out.println("Entrée incorrecte fournie");
                throw new IllegalArgumentException("L'entrée saisie n'est pas valide");
            }
        }
    }
    
    /**
     * Cette méthode gère le processus de sortie d'un véhicule du parking.
     * Elle récupère le ticket du véhicule, calcule le tarif, et met à jour les informations du parking et du ticket.
     * 
     * @throws Exception si une opération de base de données échoue
     */

    public void processExitingVehicle() throws Exception {
        String vehicleRegNumber = getVehichleRegNumber();
        Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);

        if(ticket == null) {
            System.out.println("Erreur : Impossible de trouver un ticket pour le véhicule " + vehicleRegNumber);
            return;
        }

        Date outTime = new Date();
        ticket.setOutTime(outTime);
        fareCalculatorService.calculateFare(ticket, ticketDAO.getNbTicket(vehicleRegNumber) > 0);

       
        ParkingSpot parkingSpot = ticket.getParkingSpot();
        parkingSpot.setAvailable(true);
        parkingSpotDAO.updateParking(parkingSpot); 

        ticketDAO.updateTicket(ticket);
        System.out.println("Veuillez payer le tarif de stationnement :" + ticket.getPrice());
        System.out.println("Heure de sortie enregistrée pour le numéro de véhicule : " + ticket.getVehicleRegNumber() + " is :" + outTime);
    }

}

