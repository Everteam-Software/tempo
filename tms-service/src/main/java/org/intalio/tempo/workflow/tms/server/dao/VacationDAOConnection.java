package org.intalio.tempo.workflow.tms.server.dao;

import java.util.Date;
import java.util.List;

import org.intalio.tempo.workflow.task.Vacation;
import org.intalio.tempo.workflow.task.audit.VacationAudit;

public interface VacationDAOConnection {

	/**
	 * Commit After transaction
	 * 
	 * @param
	 * 
	 * @return
	 */
	public void commit();

	/**
	 * Inserts Vacation Details
	 * 
	 * @param Vacation
	 *            Object
	 * 
	 * @return
	 */
	public void insertVacationDetails(Vacation vacation);
	
	/**
	 * Updates Vacation Details
	 * 
	 * @param Vacation
	 *            Object
	 * 
	 * @return
	 */
	public void updateVacationDetails(Vacation vacation);
	
	/**
	 * get Matched or intersected vacations list
	 * 
	 * @param fromDate
	 * @param toDate
	 * 
	 * @return
	 */
	public List<Vacation> getMatchedVacations(Date fromDate, Date toDate);

    /**
     * get Matched or intersected vacations list for user.
     *
     * @param fromDate
     *            Date
     * @param toDate
     *            Date
     * @param user
     *            String
     *
     * @return vacations List<Vacation>
     */
    List<Vacation> getUserMatchedVacations(String user,
            Date fromDate, Date toDate);

	/**
	 * Gets the vacation details of a particular user
	 * 
	 * @param logged
	 *            in user
	 * 
	 * @return Object Vacation of logged in user
	 */
	public List<Vacation> getVacationDetails(String user);
	
	/**
     * Gets the vacation details of all users
     *      * 
     * @return Object Vacation 
     */
    public List<Vacation> getVacationDetails();

    /**
     * Gets the vacation details of all users
     * 
     * @return Object Vacation
     */
    public List<Vacation> getVacationDetails(Date since, Date until,
            List<String> users);

	/**
	 * end vacation details
     * @param id  int vacation id
     * @return result Boolean
	 */
	public Boolean endVacationDetails(String[] id);

	/**
     *closes the connection
     */
	public void close();

    /**
     * Gets vacation details of given start date.
     * @param fromDate Date
     * @return vacations list List<Vacation>
     */
    List<Vacation> getVacationsByStartDate(Date fromDate);

    /**
     * Gets vacation details of given end date.
     * @param toDate Date
     * @return vacations list List<Vacation>
     */
    List<Vacation> getVacationsByEndDate(Date toDate);

    /**
     * persist vacation audit details.
     * @param vacationAudit VacationAudit
     */
    void auditVacation(VacationAudit vacationAudit);

    /**
     * Gets vacation details of given id.
     * @param id int
     * @return vacation Vacation
     */
    Vacation getVacationsByID(int id);
}
