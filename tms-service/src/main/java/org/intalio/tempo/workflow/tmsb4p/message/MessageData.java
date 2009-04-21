package org.intalio.tempo.workflow.tmsb4p.message;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;

public class MessageData {
    private QName mMsgType;
    /** Holds on to the part data, the name of the part is its key. */
    private Map mPartData = new HashMap();

    public MessageData() {
    }

    /**
     * Constructor.
     * 
     * @param aQName
     * @param aPartData
     */
    public MessageData(QName aQName, Map aPartData) {
        this(aQName);
        mPartData.putAll(aPartData);
    }

    /**
     * Constructor which takes the QName of the message as input.
     * 
     * @param aMsgType
     *            the qualified name of the message this data element
     *            represents.
     */
    public MessageData(QName aMsgType) {
        mMsgType = aMsgType;
    }

    public MessageData(QName aQName, OMElement parts){
        this(aQName);
        Iterator<OMElement> it = parts.getChildElements();
        while(it.hasNext()){
            OMElement part = it.next();
            String partName = part.getAttributeValue(aQName);
            mPartData.put(partName, part.getText());
        }
    }

    /**
     * Returns the type of message this data is representing.
     */
    public QName getMessageType() {
        return mMsgType;
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
    
    public String toXML(){
        //TODO
        return "need to set the real xml";
    }

}
