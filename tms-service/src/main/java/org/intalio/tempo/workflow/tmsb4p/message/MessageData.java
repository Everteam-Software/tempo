package org.intalio.tempo.workflow.tmsb4p.message;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

public class MessageData {
	/** Holds on to the part data, the name of the part is its key. */
	private Map mPartData = new HashMap();

	private OMFactory mFactory = OMAbstractFactory.getOMFactory();

	public MessageData() {
	}

	public MessageData(OMElement parts) {
		Iterator<OMElement> it = parts.getChildElements();
		while (it.hasNext()) {
			OMElement part = it.next();
			String partName = part.getAttributeValue(new QName("name"));
			mPartData.put(partName, part.getText());
		}
	}

	/**
	 * Returns an iterator over the part names for which we are storing data.
	 */
	public Iterator getPartNames() {
		return mPartData.keySet().iterator();
	}

	/**
	 * Returns the data associated with a passed part. Null if none.
	 * 
	 * @param aPartName
	 *            The part name to get data for.
	 * @return The data associated with the passed part name. Can be null.
	 */
	public Object getData(String aPartName) {
		return mPartData.get(aPartName);
	}

	/**
	 * Sets the data associated with a passed part. Data can be null.
	 * 
	 * @param aPartName
	 *            The part to set the data for.
	 * @param aData
	 *            The data to which the part is set.
	 */
	public void setData(String aPartName, Object aData) {
		mPartData.put(aPartName, aData);
	}

	/**
	 * @see org.activebpel.rt.message.IAeMessageData#getPartCount()
	 */
	public int getPartCount() {
		return mPartData.size();
	}
	
	public Map getMsgData(){
		return mPartData;
	}

	public String toXML() throws Exception {
		OMElement data = mFactory.createOMElement(new QName("data"));

		Iterator<String> it = mPartData.keySet().iterator();
		while (it.hasNext()) {
			String partName = it.next();
			OMElement part = mFactory.createOMElement(new QName("part"));
			data.addChild(part);
			part.addAttribute("name", partName, null);
			part.setText(mPartData.get(partName).toString());
		}
		return data.toString();
	}
}
