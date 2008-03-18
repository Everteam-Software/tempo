import java.util.Properties
import java.util.ResourceBundle

import org.intalio.tempo.workflow.tms.server.dao.JPATaskDaoConnection
import org.intalio.tempo.workflow.tms.server.dao.JPATaskDaoConnectionFactory
import org.intalio.tempo.workflow.task.PIPATask
import org.intalio.tempo.workflow.auth.UserRoles
import java.net.URI

def getJPAProperties() {
	props = new Properties()
	props.put("openjpa.ConnectionDriverName","org.apache.derby.jdbc.EmbeddedDriver")
	props.put("openjpa.ConnectionURL","jdbc:derby:tms-dao/target/JPADB;create=true")
	props.put("openjpa.jdbc.SynchronizeMappings","buildSchema")
	return props
}

scenario "testing list of pipa tasks", {
	given "a JPA Pipa connection", {
		factory = new JPATaskDaoConnectionFactory(getJPAProperties())
		jpac = (JPATaskDaoConnection) factory.openConnection()
		formUrl = "http://localhost/2"
	}
	when "a PIPA is stored for the user niko with role engineer", {
		task1 = new PIPATask("abc1",formUrl)
		task1.processEndpoint = URI.create("http://localhost/process")
		task1.initOperationSOAPAction = "initProcess"
		user ="niko"
		role ="engineer"
		urs = new UserRoles(user, role);
		task1.userOwners.add(user);
		task1.roleOwners.add(role);
		jpac.storePipaTask(task1)
		jpac.commit()
	}
	then "The task stored can be retrieved through a list", {
		jpac = (JPATaskDaoConnection) factory.openConnection()
		list = jpac.fetchAllAvailableTasks(urs)
		ensure(list.size() >= 1)
		ensure(list[0] instanceof PIPATask)
	}
	then "we delete the pipa task properly", {
		jpac.deletePipaTask(formUrl)
	}
}

scenario "storing pipa using jpa", {
	given "a JPA Pipa connection", {
		factory = new JPATaskDaoConnectionFactory(getJPAProperties())
		jpac = factory.openConnection()
		formUrl = "http://localhost/1"
	} 
	when "a PIPA is created", {
		task1 = new PIPATask("abc",formUrl)
		task1.processEndpoint = URI.create("http://localhost/process")
		task1.initOperationSOAPAction = "initProcess"
	}
	then "it can be stored", {
		jpac.storePipaTask(task1)
	}
	then "it can be deleted", {
		jpac.deletePipaTask(formUrl)
	}
	and
	then "trying to delete the same task again throws an exception", {
		ensureThrows(Exception.class) {
			jpac.deletePipaTask(formUrl)
		}
	}
}