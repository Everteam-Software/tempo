package org.intalio.tempo.workflow.tms.server.dao;

public interface VacationDAOConnectionFactory {
	VacationDAOConnection openConnection();

	/**
	 * Clear the cache by accessing the cache that is associated with the entity
	 * manager factory.
	 */
	void clearCache();
}
