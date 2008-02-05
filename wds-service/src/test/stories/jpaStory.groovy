import org.intalio.tempo.workflow.wds.core.Item;
import java.util.Properties;
import java.util.ResourceBundle;
import org.intalio.tempo.workflow.wds.core.Item;
import org.intalio.tempo.workflow.wds.core.ItemDaoConnection;
import org.intalio.tempo.workflow.wds.core.JPAItemDaoConnectionFactory;
import org.intalio.tempo.workflow.wds.core.JPAItemDaoConnection;

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