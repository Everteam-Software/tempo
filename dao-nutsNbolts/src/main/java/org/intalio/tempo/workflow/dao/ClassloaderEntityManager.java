/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */

package org.intalio.tempo.workflow.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;

public class ClassloaderEntityManager implements EntityManager {

	private EntityManager em;
	private ClassLoader cl;

	public static EntityManager createEntityManager(EntityManagerFactory f,
			ClassLoader cl) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return new ClassloaderEntityManager(f.createEntityManager(), cl);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public static class ClassloaderQuery implements Query {
		private Query q;
		private ClassLoader cl;

		public static Query create(Query q, ClassLoader cl) {
			if (q == null) return null;
			return new ClassloaderQuery(q, cl);
		}
		
		public ClassloaderQuery(Query q, ClassLoader cl) {
			super();
			this.q = q;
			this.cl = cl;
		}

		public int executeUpdate() {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.executeUpdate();
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public int getFirstResult() {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.getFirstResult();
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public FlushModeType getFlushMode() {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.getFlushMode();
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Map<String, Object> getHints() {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.getHints();
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public LockModeType getLockMode() {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.getLockMode();
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public int getMaxResults() {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.getMaxResults();
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public <T> Parameter<T> getParameter(int arg0, Class<T> arg1) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.getParameter(arg0, arg1);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Parameter<?> getParameter(int arg0) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.getParameter(arg0);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public <T> Parameter<T> getParameter(String arg0, Class<T> arg1) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.getParameter(arg0, arg1);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Parameter<?> getParameter(String arg0) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.getParameter(arg0);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Object getParameterValue(int arg0) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.getParameterValue(arg0);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public <T> T getParameterValue(Parameter<T> arg0) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.getParameterValue(arg0);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Object getParameterValue(String arg0) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.getParameterValue(arg0);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Set<Parameter<?>> getParameters() {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.getParameters();
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public List getResultList() {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.getResultList();
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Object getSingleResult() {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.getSingleResult();
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public boolean isBound(Parameter<?> arg0) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.isBound(arg0);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Query setFirstResult(int arg0) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return create(q.setFirstResult(arg0), cl);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Query setFlushMode(FlushModeType arg0) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return create(q.setFlushMode(arg0), cl);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Query setHint(String arg0, Object arg1) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return create(q.setHint(arg0, arg1), cl);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Query setLockMode(LockModeType arg0) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return create(q.setLockMode(arg0), cl);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Query setMaxResults(int arg0) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return create(q.setMaxResults(arg0), cl);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Query setParameter(int arg0, Calendar arg1, TemporalType arg2) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return create(q.setParameter(arg0, arg1, arg2), cl);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Query setParameter(int arg0, Date arg1, TemporalType arg2) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return create(q.setParameter(arg0, arg1, arg2), cl);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Query setParameter(int arg0, Object arg1) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return create(q.setParameter(arg0, arg1), cl);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Query setParameter(Parameter<Calendar> arg0, Calendar arg1,
				TemporalType arg2) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return create(q.setParameter(arg0, arg1, arg2), cl);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Query setParameter(Parameter<Date> arg0, Date arg1,
				TemporalType arg2) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return create(q.setParameter(arg0, arg1, arg2), cl);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public <T> Query setParameter(Parameter<T> arg0, T arg1) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return create(q.setParameter(arg0, arg1), cl);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Query setParameter(String arg0, Calendar arg1, TemporalType arg2) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return create(q.setParameter(arg0, arg1, arg2), cl);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Query setParameter(String arg0, Date arg1, TemporalType arg2) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return create(q.setParameter(arg0, arg1, arg2), cl);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public Query setParameter(String arg0, Object arg1) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return create(q.setParameter(arg0, arg1), cl);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

		public <T> T unwrap(Class<T> arg0) {
			ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(cl);
				return q.unwrap(arg0);
			} finally {
				Thread.currentThread().setContextClassLoader(oldCL);
			}
		}

	}

	public ClassloaderEntityManager(EntityManager em, ClassLoader cl) {
		super();
		this.em = em;
		this.cl = cl;
	}

	public void clear() {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			em.clear();
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public void close() {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			em.close();
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public boolean contains(Object arg0) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.contains(arg0);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public <T> TypedQuery<T> createNamedQuery(String arg0, Class<T> arg1) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.createNamedQuery(arg0, arg1);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public Query createNamedQuery(String arg0) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return ClassloaderQuery.create(em.createNamedQuery(arg0), cl);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public Query createNativeQuery(String arg0, Class arg1) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return ClassloaderQuery.create(em.createNativeQuery(arg0, arg1), cl);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public Query createNativeQuery(String arg0, String arg1) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return ClassloaderQuery.create(em.createNativeQuery(arg0, arg1), cl);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public Query createNativeQuery(String arg0) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return ClassloaderQuery.create(em.createNativeQuery(arg0), cl);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> arg0) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.createQuery(arg0);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public <T> TypedQuery<T> createQuery(String arg0, Class<T> arg1) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.createQuery(arg0, arg1);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public Query createQuery(String arg0) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return ClassloaderQuery.create(em.createQuery(arg0), cl);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public void detach(Object arg0) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			em.detach(arg0);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public <T> T find(Class<T> arg0, Object arg1, LockModeType arg2,
			Map<String, Object> arg3) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.find(arg0, arg1, arg2, arg3);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public <T> T find(Class<T> arg0, Object arg1, LockModeType arg2) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.find(arg0, arg1, arg2);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public <T> T find(Class<T> arg0, Object arg1, Map<String, Object> arg2) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.find(arg0, arg1, arg2);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public <T> T find(Class<T> arg0, Object arg1) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.find(arg0, arg1);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public void flush() {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			em.flush();
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public CriteriaBuilder getCriteriaBuilder() {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.getCriteriaBuilder();
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public Object getDelegate() {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.getDelegate();
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public EntityManagerFactory getEntityManagerFactory() {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.getEntityManagerFactory();
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public FlushModeType getFlushMode() {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.getFlushMode();
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public LockModeType getLockMode(Object arg0) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.getLockMode(arg0);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public Metamodel getMetamodel() {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.getMetamodel();
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public Map<String, Object> getProperties() {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.getProperties();
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public <T> T getReference(Class<T> arg0, Object arg1) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.getReference(arg0, arg1);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public EntityTransaction getTransaction() {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.getTransaction();
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public boolean isOpen() {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.isOpen();
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public void joinTransaction() {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			em.joinTransaction();
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public void lock(Object arg0, LockModeType arg1, Map<String, Object> arg2) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			em.lock(arg0, arg1, arg2);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public void lock(Object arg0, LockModeType arg1) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			em.lock(arg0, arg1);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public <T> T merge(T arg0) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.merge(arg0);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public void persist(Object arg0) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			em.persist(arg0);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public void refresh(Object arg0, LockModeType arg1, Map<String, Object> arg2) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			em.refresh(arg0, arg1, arg2);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public void refresh(Object arg0, LockModeType arg1) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			em.refresh(arg0, arg1);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public void refresh(Object arg0, Map<String, Object> arg1) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			em.refresh(arg0, arg1);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public void refresh(Object arg0) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			em.refresh(arg0);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public void remove(Object arg0) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			em.remove(arg0);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public void setFlushMode(FlushModeType arg0) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			em.setFlushMode(arg0);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public void setProperty(String arg0, Object arg1) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			em.setProperty(arg0, arg1);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}

	public <T> T unwrap(Class<T> arg0) {
		ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(cl);
			return em.unwrap(arg0);
		} finally {
			Thread.currentThread().setContextClassLoader(oldCL);
		}
	}
}
