/**
 * Copyright (c) 2005-2014 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.workflow.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

public class VacationTest extends TestCase {

    public void testBuildVacationQueryWithNoParams() {
        String expcetedQuery = "select vacation from Vacation vacation where vacation._is_active = 1";
        String query = Vacation
                .buildVacationQuery(null, null, null, null, null);
        Assert.assertTrue(expcetedQuery.equals(query.trim()));
    }

    public void testBuildVacationQueryWithEmptyUsersParam() {
        String expcetedQuery = "select vacation from Vacation vacation where vacation._is_active = 1";
        String query = Vacation.buildVacationQuery(new ArrayList<String>(),
                null, null, null, null);
        Assert.assertTrue(expcetedQuery.equals(query.trim()));
    }

    public void testBuildVacationQueryWithUsersParam() {
        String expcetedQuery = "select vacation from Vacation vacation where vacation._user in (:users)"
                + " and vacation._is_active = 1";

        List<String> users = new ArrayList<String>();
        users.add("user");

        String query = Vacation.buildVacationQuery(users, null, null, null,
                null);
        Assert.assertTrue(expcetedQuery.equals(query.trim()));
    }

    public void testBuildVacationQueryWithStartSinceParam() {
        String expcetedQuery = "select vacation from Vacation vacation where vacation._fromDate >=(:startSince)"
                + " and vacation._is_active = 1";

        String query = Vacation.buildVacationQuery(null, new Date(), null,
                null, null);
        Assert.assertTrue(expcetedQuery.equals(query.trim()));
    }

    public void testBuildVacationQueryWithStartUntilParam() {
        String expcetedQuery = "select vacation from Vacation vacation where vacation._fromDate <=(:startUntil)"
                + " and vacation._is_active = 1";
        String query = Vacation.buildVacationQuery(null, null, new Date(),
                null, null);
        Assert.assertTrue(expcetedQuery.equals(query.trim()));
    }

    public void testBuildVacationQueryWithEndSinceParam() {
        String expcetedQuery = "select vacation from Vacation vacation where vacation._toDate >=(:endSince)"
                + " and vacation._is_active = 1";
        String query = Vacation.buildVacationQuery(null, null, null,
                new Date(), null);
        Assert.assertTrue(expcetedQuery.equals(query.trim()));
    }

    public void testBuildVacationQueryWithEndUntilParam() {
        String expcetedQuery = "select vacation from Vacation vacation where vacation._toDate <=(:endUntil)"
                + " and vacation._is_active = 1";
        String query = Vacation.buildVacationQuery(null, null, null, null,
                new Date());
        Assert.assertTrue(expcetedQuery.equals(query.trim()));
    }

    public void testBuildVacationQueryWithStartParams() {
        String expcetedQuery = "select vacation from Vacation vacation where vacation._fromDate >=(:startSince)"
                + " and vacation._fromDate <=(:startUntil) and vacation._is_active = 1";
        String query = Vacation.buildVacationQuery(null, new Date(),
                new Date(), null, null);
        Assert.assertTrue(expcetedQuery.equals(query.trim()));
    }

    public void testBuildVacationQueryWithEndParams() {
        String expcetedQuery = "select vacation from Vacation vacation where vacation._toDate >=(:endSince)"
                + " and vacation._toDate <=(:endUntil) and vacation._is_active = 1";
        String query = Vacation.buildVacationQuery(null, null, null,
                new Date(), new Date());
        Assert.assertTrue(expcetedQuery.equals(query.trim()));
    }

    public void testBuildVacationQueryWithUsersStartParams() {
        String expcetedQuery = "select vacation from Vacation vacation where vacation._user in (:users)"
                + " and vacation._fromDate >=(:startSince) and vacation._fromDate <=(:startUntil)"
                + " and vacation._is_active = 1";

        List<String> users = new ArrayList<String>();
        users.add("user");

        String query = Vacation.buildVacationQuery(users, new Date(),
                new Date(), null, null);
        Assert.assertTrue(expcetedQuery.equals(query.trim()));
    }

    public void testBuildVacationQueryWithUsersEndParams() {
        String expcetedQuery = "select vacation from Vacation vacation where vacation._user in (:users)"
                + " and vacation._toDate >=(:endSince) and vacation._toDate <=(:endUntil)"
                + " and vacation._is_active = 1";

        List<String> users = new ArrayList<String>();
        users.add("user");

        String query = Vacation.buildVacationQuery(users, null, null,
                new Date(), new Date());
        Assert.assertTrue(expcetedQuery.equals(query.trim()));
    }

    public void testBuildVacationQueryWithAllParams() {
        String expcetedQuery = "select vacation from Vacation vacation where vacation._user in (:users)"
                + " and vacation._fromDate >=(:startSince) and vacation._fromDate <=(:startUntil)"
                + " and vacation._toDate >=(:endSince) and vacation._toDate <=(:endUntil)"
                + " and vacation._is_active = 1";

        List<String> users = new ArrayList<String>();
        users.add("user");

        String query = Vacation.buildVacationQuery(users, new Date(),
                new Date(), new Date(), new Date());
        Assert.assertTrue(expcetedQuery.equals(query.trim()));
    }
}
