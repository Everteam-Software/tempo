/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */

package org.intalio.tempo.workflow.wds.core;

import static org.intalio.tempo.workflow.wds.core.WDSUtil.getSamplePipa;

import java.util.Arrays;

import org.intalio.tempo.workflow.task.PIPATask;
import org.junit.runner.RunWith;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.Specification;

@RunWith(InstinctRunner.class)
public class PipaTaskTest {

    static{
        System.setProperty("org.intalio.tempo.configDirectory",
                "src/test/resources/");
    }

    final static ExpectThat expect = new ExpectThatImpl();
	
	@Specification
    public void AValidPipaIsValid() throws Exception {
		PIPATask task1 = getSamplePipa();
        expect.that(task1.isValid()).isTrue();
	}

	@Specification
	public void ANewlyCreatedPipaIsNotValid() throws Exception {
		PIPATask task2 = new PIPATask();
        expect.that(task2.isValid()).isFalse();

	}
      
	@Specification
	public void UserNamesAreNormalized() throws Exception {
		PIPATask task1 = getSamplePipa();
        String[] unnormalizedUsers = {"abc/abc", "def\\def", "ghi.ghi"};
        String[] normalizedUsers = {"abc\\abc", "def\\def", "ghi.ghi"};
        task1.setUserOwners(unnormalizedUsers);
        expect.that(Arrays.asList(normalizedUsers).containsAll(task1.getUserOwners()));
	}
	
	@Specification 
	public void RoleNamesAreNormalized() throws Exception {
		PIPATask task1 = getSamplePipa();
        String[] unnormalizedRoles = {"jkl/jkl", "mno\\mno", "pqr.pqr"};
        String[] normalizedRoles = {"jkl\\jkl", "mno\\mno", "pqr.pqr"};
        
        task1.setRoleOwners(unnormalizedRoles);
        
        expect.that(Arrays.asList(normalizedRoles).containsAll(task1.getRoleOwners()));
    }
}
