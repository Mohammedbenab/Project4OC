package com.parkit.parkingsystem.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	private static final Logger logger = LogManager.getLogger("TicketDAO");
	public DataBaseConfig dataBaseConfig = new DataBaseConfig();
	public int nbrOfRecurency;

	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}
		LocalDateTime intHourV = ticket.getInTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime outHourV = ticket.getOutTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		// TODO: Some tests are failing here. Need to check if this logic is correct
		Long duration = Duration.between(intHourV, outHourV).toMinutes();

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = DBConstants.RECURENT_VEHICLE_REG_NUMBER;

		try {
			con = dataBaseConfig.getConnection();
			try {
				if (query != null) {
					ps = con.prepareStatement(query);
					ps.setString(1, ticket.getVehicleRegNumber());
					rs = ps.executeQuery();
				}
				if (rs.next()) {
					nbrOfRecurency = rs.getInt(1);
				}
			} catch (SQLException exp) {
				exp.printStackTrace();
			}
		} catch (Exception ex) {
			logger.error("Error fetching next available spot", ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		double value;

		if (duration > 30 && nbrOfRecurency <= 1) {
			value = 1;
		} else if (nbrOfRecurency >= 2) {
			value = 0.95;
		} else
			value = 0;

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			ticket.setPrice(Fare.CAR_RATE_PER_HOUR * duration * value);
			break;
		}
		case BIKE: {
			ticket.setPrice(Fare.BIKE_RATE_PER_HOUR * duration * value);
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
	}
}
