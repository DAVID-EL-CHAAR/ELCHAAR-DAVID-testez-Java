package com.parkit.parkingsystem.service;


import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.constants.ParkingType;

public class FareCalculatorService {

	/**
     * Calcule le tarif plein pour une voiture.
     *
     * @param duration La durée de stationnement en minutes.
     * @return Le tarif à appliquer.
     */
	public double calculateFullFareCar(int duration) {
	    // Tarif horaire pour une voiture
	    double fare = Fare.CAR_RATE_PER_HOUR;
	    // Durée minimale de facturation
	    int minDuration = 30;
	    // Si la durée est inférieure à la durée minimale, le tarif est 0
	    if (duration < minDuration) {
	        return 0;
	    }
	    // Sinon, on applique le tarif proportionnel à la durée
	    return fare * duration / 60;
	}

	public double calculateFullFareBike(int duration) {
	    // Tarif horaire pour une moto
	    double fare = Fare.BIKE_RATE_PER_HOUR;
	    // Durée minimale de facturation
	    int minDuration = 30;
	    // Si la durée est inférieure à la durée minimale, le tarif est 0
	    if (duration < minDuration) {
	        return 0;
	    }
	    // Sinon, on applique le tarif proportionnel à la durée
	    return fare * duration / 60;
	}

	

    /**
     * Calcule le tarif avec éventuelle réduction pour une voiture ou une moto.
     *
     * @param ticket Le ticket du véhicule.
     * @param discount Indique si une réduction doit être appliquée.
     * @return Le tarif à appliquer.
     * @throws IllegalArgumentException Si le ticket est null, si l'heure de sortie n'est pas définie ou si l'heure de sortie est antérieure à l'heure d'entrée.
     */
    public double calculateFare(Ticket ticket, boolean discount) {
        // Si le ticket est null, on lance une exception
        if (ticket == null) {
            throw new IllegalArgumentException("Le ticket est nul");
        }
        // Si le ticket n'a pas d'heure de sortie, on lance une exception
        if (ticket.getOutTime() == null) {
            throw new IllegalArgumentException("Le temps de sortie est nul");
        }
        // Si le ticket a une heure de sortie antérieure à l'heure d'entrée, on lance une exception
        if (ticket.getOutTime().before(ticket.getInTime())) {
            throw new IllegalArgumentException("Le temps est avant dans le temps");
        }
        
       
        
        // On calcule la durée du ticket en minutes
        int duration = (int) ((ticket.getOutTime().getTime() - ticket.getInTime().getTime()) / 1000 / 60);
        // On initialise le prix à zéro
        double price = 0;
       
        // On vérifie le type de véhicule du ticket
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                // On calcule le prix plein pour une voiture en appelant la méthode de la classe FareCalculatorService
                price = calculateFullFareCar(duration);
                break;
            }
            case BIKE: {
                // On calcule le prix plein pour une moto en appelant la méthode de la classe FareCalculatorService
                price = calculateFullFareBike(duration);
                break;
            }
            default: throw new IllegalArgumentException("Type de stationnement inconnu");
        }
        // Si le paramètre discount est vrai, on applique une réduction de 5%
        if (discount) {
            price = price * 0.95;
        }
        // On arrondit le prix à deux décimales
        price = Math.round(price * 100.0) / 100.0;
        // On retourne le prix calculé
        return price;
    }

    /**
     * Calcule le tarif sans réduction pour une voiture ou une moto.
     *
     * @param ticket Le ticket du véhicule.
     * @return Le tarif à appliquer.
     */
    public double calculateFare(Ticket ticket) {
        // Appel de la méthode calculateFare avec le paramètre discount à false
        return calculateFare(ticket, false);
    }
}