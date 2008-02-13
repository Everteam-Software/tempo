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

def convertBundleToProperties(ResourceBundle rb) {
	props = new Properties()
	for (Enumeration<String> keys = rb.getKeys(); keys.hasMoreElements();) {
		String key = (String) keys.nextElement()
		props.put(key, rb.getString(key))
	}
	return props
}
def getSampleItem() {
	return new Item("http://www.hellonico.net", "meta", "hello".getBytes())
}
scenario "storing items using jpa", {
	given "a JPA Item connection ", {
		map = convertBundleToProperties(ResourceBundle.getBundle("jpa"))
		factory = new JPAItemDaoConnectionFactory(map)
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

scenario "storing pipa using jpa", {
	given "a JPA Pipa connection", {
		map = convertBundleToProperties(ResourceBundle.getBundle("jpa"))
		factory = new TMSJPAConnectionFactory(map)
		jpac = (TMSConnectionInterface)factory.getTMSConnection()
		formUrl = "http://localhost/"
	} 
	when "a PIPA is created", {
		task1 = new PIPATask("abc",formUrl)
		task1.formNamespace = "urn:ns"
		task1.processEndpoint = "http://localhost/process"
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
scenario "testing list of pipa tasks", {
	given "a JPA Pipa connection", {
		map = convertBundleToProperties(ResourceBundle.getBundle("jpa"))
		factory = new TMSJPAConnectionFactory(map)
		jpac = (TMSJPAConnection) factory.getTMSConnection()
		formUrl = "http://localhost/"
	}
	when "a PIPA is stored for the user niko with role engineer", {
		task1 = new PIPATask("abc",formUrl)
		task1.formNamespace = "urn:ns"
		task1.processEndpoint = "http://localhost/process"
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
}