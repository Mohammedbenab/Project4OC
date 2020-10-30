package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAO {

	private static final Logger logger = LogManager.getLogger("TicketDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	public boolean saveTicket(Ticket ticket) {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.SAVE_TICKET);
			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			// ps.setInt(1,ticket.getId());
			try {
				ps.setInt(1, ticket.getParkingSpot().getId());
				ps.setString(2, ticket.getVehicleRegNumber());
				ps.setDouble(3, ticket.getPrice());
				ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
				ps.setTimestamp(5,
						(ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
				ps.execute();
			} catch (SQLException exp) {
				exp.printStackTrace();
			} finally {
				if (ps != null) {
					ps.close();
				}
			}
		} catch (Exception ex) {
			logger.error("Error fetching next available spot", ex);
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException exp) {
				exp.printStackTrace();
			}
		}
		return false;
	}

	public Ticket getTicket(String vehicleRegNumber) {
		Connection con = null;
		Ticket ticket = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = DBConstants.GET_TICKET;
		try {
			con = dataBaseConfig.getConnection();
			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			try {
				if (query != null) {
					ps = con.prepareStatement(query);
					ps.setString(1, vehicleRegNumber);
					rs = ps.executeQuery();
				}
				if (rs.next()) {
					ticket = new Ticket();
					ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),
							false);
					ticket.setParkingSpot(parkingSpot);
					ticket.setId(rs.getInt(2));
					ticket.setVehicleRegNumber(vehicleRegNumber);
					ticket.setPrice(rs.getDouble(3));
					ticket.setInTime(rs.getTimestamp(4));
					ticket.setOutTime(rs.getTimestamp(5));
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
			}
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception e) {
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
			}
		}
		return ticket;
	}

	public boolean updateTicket(Ticket ticket) {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
			try {
				ps.setDouble(1, ticket.getPrice());
				ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
				ps.setInt(3, ticket.getId());
				ps.execute();
				return true;
			} catch (SQLException exp) {
				exp.printStackTrace();
			} finally {
				if (ps != null) {
					ps.close();
				}
			}
		} catch (Exception ex) {
			logger.error("Error saving ticket info", ex);
		} finally {
			try {
				if (con != null) {
					con.close();
				}

			} catch (SQLException exp) {
				exp.printStackTrace();
			}
		}
		return false;
	}
}