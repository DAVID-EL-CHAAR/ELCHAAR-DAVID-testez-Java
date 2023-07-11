package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public boolean saveTicket(Ticket ticket){
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
            ps.setInt(1,ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
            ps.setTimestamp(5, (ticket.getOutTime() == null)?null: (new Timestamp(ticket.getOutTime().getTime())) );
            ps.execute();
            return true; // Return true when ticket save is successful
        } catch (Exception ex){
            logger.error("Erreur lors de la sauvegarde du ticket",ex);
            return false; // Return false if an error occurred
        } finally {
            dataBaseConfig.closeConnection(con);
        }
    }


    @SuppressWarnings("finally")
	public Ticket getTicket(String vehicleRegNumber) {
        Connection con = null;
        Ticket ticket = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1,vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(rs.getTimestamp(4));
                ticket.setOutTime(rs.getTimestamp(5));
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            logger.error("Erreur lors de la récupération du prochain emplacement disponible",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
            return ticket;
        }
    }

    public boolean updateTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
            ps.setInt(3,ticket.getId());
            ps.execute();
            return true;
        } catch (Exception ex){
            logger.error("Erreur lors de l'enregistrement des informations sur le billet", ex);
            ex.printStackTrace();
        } finally {
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }
    
    public int getNbTicket(String vehicleRegNumber) {
        Connection con = null;
        int nbTicket = 0;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_NB_TICKET);
            // SELECT COUNT(*) AS nbTicket FROM ticket WHERE VEHICLE_REG_NUMBER = ?
            ps.setString(1, vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nbTicket = rs.getInt("nbTicket");
            }
        } catch (Exception ex) {
            logger.error("Erreur de comptage des tickets", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
        }
        return nbTicket;
    }
    
    public int getNumberOfTickets(String vehicleRegNumber){


        int result = -1;
        try(Connection con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_NB_TICKET)) {

            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1, vehicleRegNumber);
            ResultSet rs = ps.executeQuery();//rien lu
            if (rs.next()) {
                result = rs.getInt(1);
            }
            dataBaseConfig.closeResultSet(rs);
        } catch (RuntimeException e) {
            throw e;

        } catch (Exception ex){
            logger.error("Erreur lors de l'obtention du nombre de billets",ex);
        }
        return result;
    }
    
    public boolean doesRegNumberExist(String vehicleRegNumber) {
        return getNumberOfTickets(vehicleRegNumber) > 0;
    }
}

