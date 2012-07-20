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
        @NamedQuery(name = Vacation.GET_VACATION_DETAILS, query = "select vacation._id,vacation._from_Date,vacation._to_Date,vacation._description from Vacation vacation where vacation._user=(:user)"),
        @NamedQuery(name = Vacation.FIND_VAC_BY_ID, query = "select vacation from Vacation vacation where vacation._id = ?1") })
public class Vacation {

    private Query find_id;
    public static final String FIND_VAC_BY_ID = "find_vac_by_id";
    private EntityManager _entityManager;
    public static final String GET_VACATION_DETAILS = "get_vacation_details";

    // @GeneratedValue(strategy=GenerationType.AUTO)
    @GeneratedValue
    @Id
    @Column(name = "id")
    @Persistent
    private int _id;

    @Column(name = "from_date")
    @Persistent
    private Date _from_Date;

    @Column(name = "to_date")
    @Persistent
    private Date _to_Date;

    @Column(name = "description")
    @Persistent
    private String _description;

    @Column(name = "userName")
    @Persistent
    private String _user;

    public Vacation() {
    }

    public Vacation(int id) {
        this._id = id;
    }

    public Vacation(EntityManager em) {
        _entityManager = em;
        find_id = _entityManager.createNamedQuery(Vacation.FIND_VAC_BY_ID);
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public Date get_from_Date() {
        return _from_Date;
    }

    public void set_from_Date(Date _from_Date) {
        if (_from_Date == null) {
            throw new RequiredArgumentException("_from_Date");
        }
        this._from_Date = _from_Date;
    }

    public Date get_to_Date() {
        return _to_Date;
    }

    public void set_to_Date(Date _to_Date) {
        if (_to_Date == null) {
            throw new RequiredArgumentException("_to_Date");
        }
        this._to_Date = _to_Date;
    }

    public String get_description() {
        return _description;
    }

    public void set_description(String _description) {
        if (_description == null) {
            throw new RequiredArgumentException("_description");
        }
        this._description = _description;
    }

    public String get_user() {
        return _user;
    }

    public void set_user(String _user) {
        this._user = _user;
    }

    public Vacation fetchVacationByID(int id) {
        Query q = find_id.setParameter(1, id);
        List<Vacation> resultList = q.getResultList();
        return resultList.get(0);
    }
}
