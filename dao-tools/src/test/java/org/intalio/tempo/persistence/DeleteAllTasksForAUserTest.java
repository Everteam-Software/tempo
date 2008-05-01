package org.intalio.tempo.persistence;

import junit.framework.TestCase;

import org.intalio.tempo.persistence.delete.DeleteTasksHelper;
import org.junit.Test;

public class DeleteAllTasksForAUserTest extends TestCase {

    @SuppressWarnings("unchecked")
    @Test
    public void testDeleteAllTask() throws Exception {
        DeleteTasksHelper helper = new DeleteTasksHelper();
        helper.run();
    }
}
