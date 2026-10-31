package com.finals.cinema.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SeatDAO - Manages seat availability queries for projections
 */
@Component
public class SeatDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Get all reserved seat numbers for a specific projection
     */
    public Set<Integer> getReservedSeats(int projectionId) {
        Set<Integer> reservedSeats = new HashSet<>();
        String sql = "SELECT seat FROM tickets WHERE projection_id = ?";
        
        try {
            List<Integer> seats = jdbcTemplate.queryForList(sql, Integer.class, projectionId);
            reservedSeats.addAll(seats);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return reservedSeats;
    }

    /**
     * Check if a specific seat is available for a projection
     */
    public boolean isSeatAvailable(int projectionId, int seatNumber) {
        String sql = "SELECT COUNT(*) as cnt FROM tickets WHERE projection_id = ? AND seat = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, projectionId, seatNumber);
        return count == null || count == 0;}
    

    /**
     * Get the number of available seats for a projection
     */
    public int getAvailableSeatsCount(int projectionId, int hallCapacity) {
        Set<Integer> reserved = getReservedSeats(projectionId);
        return hallCapacity - reserved.size();
    }

    /**
     * Verify if multiple seats are available (for multi-seat booking)
     */
    public boolean areSeatAvailable(int projectionId, Set<Integer> seatNumbers) {
        for (Integer seatNumber : seatNumbers) {
            if (!isSeatAvailable(projectionId, seatNumber)) {
                return false;
            }
        }
        return true;
    }
}
