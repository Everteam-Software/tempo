import org.intalio.tempo.workflow.wds.core.Item
import java.util.Properties
import java.util.ResourceBundle
import org.intalio.tempo.workflow.wds.core.Item
import org.intalio.tempo.workflow.wds.core.ItemDaoConnection
import org.intalio.tempo.workflow.wds.core.JPAItemDaoConnectionFactory
import org.intalio.tempo.workflow.wds.core.JPAItemDaoConnection

import org.intalio.tempo.workflow.task.PIPATask
import org.intalio.tempo.workflow.wds.core.tms.TMSJPAConnectionFactory
import org.intalio.tempo.workflow.wds.core.tms.TMSJPAConnection
import org.intalio.tempo.workflow.wds.core.tms.TMSConnectionInterface
import org.intalio.tempo.workflow.auth.UserRoles
import java.net.URI

def getJPAProperties() {
	props = new Properties()
	props.put("openjpa.ConnectionDriverName","org.apache.derby.jdbc.EmbeddedDriver")
	props.put("openjpa.ConnectionURL","jdbc:derby:wds-service/target/JPADB;create=true")
	props.put("openjpa.jdbc.SynchronizeMappings","buildSchema")
	return props
}
def getSampleItem() {
	return new Item("http://www.hellonico.net", "meta", "hello".getBytes())
}

scenario "storing items using jpa", {
	given "a JPA Item connection ", {
		factory = new JPAItemDaoConnectionFactory(getJPAProperties())
		jpac = (JPAItemDaoConnection)factory.getItemDaoConnection()  	
	}
	when "an item is stored", {
		item = getSampleItem()
		jpac.storeItem(item)
	}
	then "I can retrieve it", {
		item2 = jpac.retrieveItem(item.URI)
	}
	then "the retrieved item is the same as the original", {
		ensure(item) {
			isEqualTo item2
		}
	}
	and 
	when "I delete the item", {
		jpac.deleteItem(item.URI)
	}
	then "trying to retrieve it should fail", {
		ensureThrows(Exception.class){
			jpac.retrieveItem(item.URI)
		}
	}
}

scenario "testing list of pipa tasks", {
	given "a JPA Pipa connection", {
		factory = new TMSJPAConnectionFactory(getJPAProperties())
		jpac = (TMSJPAConnection) factory.getTMSConnection()
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
		jpac = (TMSJPAConnection) factory.getTMSConnection()
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
		factory = new TMSJPAConnectionFactory(getJPAProperties())
		jpac = (TMSConnectionInterface)factory.getTMSConnection()
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