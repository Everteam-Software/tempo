package org.intalio.tempo.workflow.tms.server.dao;

import java.util.Map;

import org.intalio.tempo.workflow.dao.AbstractJPAConnectionFactory;

public class JPAVacationDAOConnectionFactory extends AbstractJPAConnectionFactory implements
		VacationDAOConnectionFactory {
	public JPAVacationDAOConnectionFactory() {
		super("org.intalio.tempo.tms");
	}

	public JPAVacationDAOConnectionFactory(Map<String, Object> properties) {
		this("org.intalio.tempo.tms", properties);
	}

	public JPAVacationDAOConnectionFactory(String tms, Map<String, Object> properties) {
		super(tms, properties);
	}

	public VacationDAOConnection openConnection() {
		return new JPAVacationDAOConnection(factory.createEntityManager());
	}
}
