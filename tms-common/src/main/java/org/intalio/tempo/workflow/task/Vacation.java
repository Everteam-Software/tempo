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
/**
 *  This class is a Persistence Class for  vacation management of table vacation.
 */
package org.intalio.tempo.workflow.task;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.Persistent;
import org.intalio.tempo.workflow.util.RequiredArgumentException;

@Entity
@Table(name = "vacation")
@TableGenerator(name = "tab", initialValue = 0, allocationSize = 50)
@NamedQueries({
        @NamedQuery(name = Vacation.GET_VACATION_DETAILS, query = "select vacation from Vacation vacation where vacation._user=(:user) and vacation._is_active = 1"),
        @NamedQuery(name = Vacation.FIND_VAC_BY_ID, query = "select vacation from Vacation vacation where vacation._id = ?1 and vacation._is_active = 1"),
        @NamedQuery(name = Vacation.FETCH_VACATION_SUMMARY, query = "select vacation from Vacation vacation where vacation._is_active = 1"),
        @NamedQuery(name = Vacation.FETCH_MATCHED_VACATION, query = "select vacation from Vacation vacation where vacation._fromDate <=(:toDate) AND vacation._toDate >= (:fromDate) and vacation._is_active = 1")
        })
public class Vacation {

    private Query find_id;
    public static final String FIND_VAC_BY_ID = "find_vac_by_id";
    private EntityManager _entityManager;
    public static final String GET_VACATION_DETAILS = "get_vacation_details";
    public static final String FETCH_VACATION_SUMMARY = "fetch_vacation_summary";
    public static final String FETCH_MATCHED_VACATION = "fetch_matched_vacation";

    // @GeneratedValue(strategy=GenerationType.AUTO)
    @GeneratedValue
    @Id
    @Column(name = "id")
    @Persistent
    private int _id;

    @Column(name = "from_date")
    @Persistent
    private Date _fromDate;

    @Column(name = "to_date")
    @Persistent
    private Date _toDate;

    @Column(name = "description")
    @Persistent
    private String _description;

    @Column(name = "user_name")
    @Persistent
    private String _user;
    
    @Column(name = "substitute")
    @Persistent
    private String _substitute;
    
    @Column(name = "is_active")
    @Persistent
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

    public Vacation fetchVacationByID(int id) {
        Query q = find_id.setParameter(1, id);
        List<Vacation> resultList = q.getResultList();
        return resultList.get(0);
    }
}
