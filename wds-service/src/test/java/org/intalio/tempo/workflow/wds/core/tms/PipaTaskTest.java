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

package org.intalio.tempo.workflow.wds.core.tms;

import static org.intalio.tempo.workflow.wds.core.tms.WDSUtil.*;
import org.junit.runner.RunWith;

import com.googlecode.instinct.expect.ExpectThat;
import com.googlecode.instinct.expect.ExpectThatImpl;
import com.googlecode.instinct.integrate.junit4.InstinctRunner;
import com.googlecode.instinct.marker.annotate.Specification;

@RunWith(InstinctRunner.class)
public class PipaTaskTest {

    final static ExpectThat expect = new ExpectThatImpl();
	
	@Specification
    public void AValidPipaIsValid() throws Exception {
        PipaTask task1 = getSamplePipa();
        expect.that(task1.isValid()).isTrue();
	}

	@Specification
	public void ANewlyCreatedPipaIsNotValid() throws Exception {
		PipaTask task2 = new PipaTask();
        expect.that(task2.isValid()).isFalse();

	}
      
	@Specification
	public void UserNamesAreNormalized() throws Exception {
		PipaTask task1 = getSamplePipa();
        String[] unnormalizedUsers = {"abc/abc", "def\\def", "ghi.ghi"};
        String[] normalizedUsers = {"abc\\abc", "def\\def", "ghi\\ghi"};
        task1.setUserOwners(unnormalizedUsers);
        
        for (int i = 0; i < normalizedUsers.length; ++i) 
            expect.that(normalizedUsers[i]).isEqualTo(task1.getUserOwners()[i]);

	}
	
	@Specification 
	public void RoleNamesAreNormalized() throws Exception {
		PipaTask task1 = getSamplePipa();
        String[] unnormalizedRoles = {"jkl/jkl", "mno\\mno", "pqr.pqr"};
        String[] normalizedRoles = {"jkl\\jkl", "mno\\mno", "pqr\\pqr"};
        task1.setRoleOwners(unnormalizedRoles);

        for (int i = 0; i < normalizedRoles.length; ++i) 
            expect.that(normalizedRoles[i]).isEqualTo(task1.getRoleOwners()[i]);
        
    }
}
