/**
 * Copyright (c) 2005-2006 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 *
 * $Id: Vacation.java 5440 2006-06-09 08:58:15Z imemruk $
 * $Log:$
 */
package org.intalio.tempo.workflow.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.intalio.tempo.workflow.util.RequiredArgumentException;

/**
 *  This class is a Persistence Class for vacation management
 *  of table vacation.
 */
@Entity
@Table(name = "vacation")
@NamedQueries({
        @NamedQuery(name = Vacation.GET_VACATION_DETAILS,
        query = "select vacation from Vacation vacation where "
                + "vacation._user=(:user) "
                + "AND vacation._toDate >=(:toDate)  "
                + "AND vacation._is_active = 1"),
        @NamedQuery(name = Vacation.GET_VACATION_DETAILS_BY_USERS_TIME,
        query = "select vacation from Vacation vacation where "
                + "vacation._user in (:users) "
                + "AND vacation._fromDate >=(:fromDate)  "
                + "AND vacation._toDate >=(:toDate)  "
                + "AND vacation._is_active = 1"),
        @NamedQuery(name = Vacation.GET_VACATION_DETAILS_BY_TIME,
        query = "select vacation from Vacation vacation where "
                + "vacation._fromDate >=(:fromDate)  "
                + "AND vacation._toDate >=(:toDate)  "
                + "AND vacation._is_active = 1"),
        @NamedQuery(name = Vacation.FIND_VAC_BY_ID,
        query = "select vacation from Vacation vacation where "
                + "vacation._id = ?1 "
                + "AND vacation._is_active = 1"),
        @NamedQuery(name = Vacation.FETCH_VACATION_SUMMARY,
        query = "select vacation from Vacation vacation where "
                + "vacation._toDate >=(:toDate)  "
                + "AND vacation._is_active = 1"),

        @NamedQuery(name = Vacation.FETCH_USER_MATCHED_VACATION,
        query = "select vacation from Vacation vacation where "
                + "vacation._fromDate <=(:toDate) "
                + "AND vacation._toDate >= (:fromDate) "
                + "AND vacation._user=(:user) "
                + "AND vacation._is_active = 1"),

        @NamedQuery(name = Vacation.FETCH_MATCHED_VACATION,
        query = "select vacation from Vacation vacation where "
                + "vacation._fromDate <=(:toDate) "
                + "AND vacation._toDate >= (:fromDate) "
                + "AND vacation._is_active = 1"),

        @NamedQuery(name = Vacation.FETCH_START_VACATION,
        query = "select vacation from Vacation vacation where "
                + "vacation._fromDate <= (:fromDate) "
                + "AND vacation._toDate >= (:fromDate) "
                + "AND vacation._is_active = 1"),

        @NamedQuery(name = Vacation.FETCH_END_VACATION,
        query = "select vacation from Vacation vacation where "
                + "vacation._toDate = (:toDate) "
                + "AND vacation._is_active = 1")
        })
public class Vacation {

    @Transient
    private Query find_id;
    public static final String FIND_VAC_BY_ID = "find_vac_by_id";
    @Transient
    private EntityManager _entityManager;
    public static final String GET_VACATION_DETAILS = "get_vacation_details";
    public static final String GET_VACATION_DETAILS_BY_TIME = "get_vacation_details_by_time";
    public static final String GET_VACATION_DETAILS_BY_USERS_TIME = "get_vacation_details_by_users_time";
    public static final String FETCH_VACATION_SUMMARY = "fetch_vacation_summary";
    public static final String FETCH_MATCHED_VACATION = "fetch_matched_vacation";

    /**
     * Query building constants
     */
    public static final String SELECT = "select vacation from Vacation vacation";
    public static final String WHERE = "where";
    public static final String USERS = "vacation._user in (:users)";
    public static final String START_DATE_LE = "vacation._fromDate <=(:startUntil)";
    public static final String START_DATE_GE = "vacation._fromDate >=(:startSince)";
    public static final String END_DATE_LE = "vacation._toDate <=(:endUntil)";
    public static final String END_DATE_GE = "vacation._toDate >=(:endSince)";
    public static final String IS_ACTIVE = "vacation._is_active = 1";
    public static final String AND = "and";
    public static final String SEPARATOR = " ";

    /**
     * query to fetch vacations matched to given dates for user.
     */
    public static final String FETCH_USER_MATCHED_VACATION
                            = "fetch_user_matched_vacation";

    /**
     * query to fetch vacations start with given date.
     */
    public static final String FETCH_START_VACATION = "fetch_start_vacation";

    /**
     * query to fetch vacations end with given date.
     */
    public static final String FETCH_END_VACATION = "fetch_end_vacation";

    // @GeneratedValue(strategy=GenerationType.AUTO)
    @Id
    @Column(name = "id")
    @TableGenerator(name="vac" , table="OPENJPA_SEQUENCE_TABLE", pkColumnName="ID" , valueColumnName="SEQUENCE_VALUE" , pkColumnValue = "0", allocationSize=10)
    @GeneratedValue(strategy=GenerationType.TABLE , generator="vac")
    private int _id;

    @Column(name = "from_date")
    @Temporal( TemporalType.DATE )
    private Date _fromDate;

    @Column(name = "to_date")
    @Temporal( TemporalType.DATE )
    private Date _toDate;

    @Column(name = "description")
    private String _description;

    @Column(name = "user_name")
    private String _user;
    
    @Column(name = "substitute")
    private String _substitute;
    
    @Column(name = "is_active")
    private int _is_active;

    public Vacation() {
    }

    public Vacation(int id) {
        this._id = id;
    }

    public Vacation(EntityManager em) {
        _entityManager = em;
        find_id = _entityManager.createNamedQuery(Vacation.FIND_VAC_BY_ID);
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public Date getFromDate() {
        return _fromDate;
    }

    public void setFromDate(Date fromDate) {
        if (fromDate == null) {
            throw new RequiredArgumentException("_from_Date");
        }
        this._fromDate = fromDate;
    }

    public Date getToDate() {
        return _toDate;
    }

    public void setToDate(Date toDate) {
        if (toDate == null) {
            throw new RequiredArgumentException("_to_Date");
        }
        this._toDate = toDate;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        if (description == null) {
            throw new RequiredArgumentException("description");
        }
        this._description = description;
    }

    public String getUser() {
        return _user;
    }

    public void setUser(String user) {
        this._user = user;
    }
    
    public String getSubstitute() {
		return _substitute;
	}

	public void setSubstitute(String substitute) {
		this._substitute = substitute;
	}
	
	public int getIs_active() {
		return _is_active;
	}

	public void setIs_active(int _is_active) {
		this._is_active = _is_active;
	}

    /**
     * user  display name.
     */
    @Transient
    private String userName;

    /**
     * substitute display name.
     */
    @Transient
    private String substituteName;

    /**
     * get User Name.
     * @return userName String
     */
    public final String getUserName() {
        return userName;
    }

    /**
     * set user name.
     * @param name String
     */
    public final void setUserName(final String name) {
        this.userName = name;
    }

    /**
     * get substitute name.
     * @return substituteName String
     */
    public final String getSubstituteName() {
        return substituteName;
    }

    /**
     * set substitute name.
     * @param name String
     */
    public final void setSubstituteName(final String name) {
        this.substituteName = name;
    }

    /**
     * fetch vacation by ID.
     * @param id int
     * @return vacation Vacation
     */
    public final Vacation fetchVacationByID(final int id) {
        Query q = find_id.setParameter(1, id);
        List<Vacation> resultList = q.getResultList();
        Vacation vacation = null;
        if (resultList != null && resultList.size() > 0) {
            vacation = (Vacation) resultList.get(0);
        }
        return vacation;
    }

    /**
     * Creates a select query for Vacation based on provided inputs.
     * 
     * @param users
     *            list of users
     * @param startSince
     *            fromDate >= startSince
     * @param startUntil
     *            fromDate <= startUntil
     * @param endSince
     *            toDate >= endSince
     * @param endUntil
     *            toDate <= endUntil
     * @return select query
     */
    public static final String buildVacationQuery(List<String> users,
            Date startSince, Date startUntil, Date endSince, Date endUntil) {

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(SELECT).append(SEPARATOR);

        List<String> whereClauses = new ArrayList<String>();

        if (users != null && users.size() > 0) {
            whereClauses.add(USERS + SEPARATOR);
        }

        if (startSince != null) {
            whereClauses.add(START_DATE_GE + SEPARATOR);
        }

        if (startUntil != null) {
            whereClauses.add(START_DATE_LE + SEPARATOR);
        }

        if (endSince != null) {
            whereClauses.add(END_DATE_GE + SEPARATOR);
        }

        if (endUntil != null) {
            whereClauses.add(END_DATE_LE + SEPARATOR);
        }

        whereClauses.add(IS_ACTIVE + SEPARATOR);

        if (whereClauses.size() > 0) {
            queryBuilder.append(WHERE).append(SEPARATOR);

            for (int i = 0; i < whereClauses.size(); i++) {
                queryBuilder.append(whereClauses.get(i));

                if (i < whereClauses.size() - 1) {
                    queryBuilder.append(AND).append(SEPARATOR);
                }
            }
        }

        return queryBuilder.toString();
    }
}
