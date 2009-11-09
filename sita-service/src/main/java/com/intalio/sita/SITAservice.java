package com.intalio.sita;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.xmlbeans.GDate;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlDateTimeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intalio.gi.forms.tAmanagement.ActivityType;
import com.intalio.gi.forms.tAmanagement.ArrivalDepartureType;
import com.intalio.gi.forms.tAmanagement.DataDocument;
import com.intalio.gi.forms.tAmanagement.InspectionType;
import com.intalio.gi.forms.tAmanagement.UpdateInputDocument;
import com.intalio.gi.forms.tAmanagement.DataDocument.Data;
import com.intalio.gi.forms.tAmanagement.InspectionType.RTR;
import com.intalio.gi.forms.tAmanagement.InspectionType.RTR.RTRstatus;
import com.intalio.gi.forms.tAmanagement.UpdateInputDocument.UpdateInput;
import com.intalio.gi.forms.tAmanagement.UpdateInputDocument.UpdateInput.FMR;
import com.intalio.gi.forms.tAmanagement.UpdateInputDocument.UpdateInput.FMR.MaintTask;
import com.intalio.gi.forms.tAmanagement.impl.DataDocumentImpl;
import com.intalio.gi.forms.tAmanagement.impl.UpdateInputDocumentImpl;
import com.intalio.gi.forms.tAmanagement.impl.InspectionTypeImpl.RTRImpl;

public class SITAservice {

	private static final String TAMANAGEMENT_URI = "http://www.intalio.com/gi/forms/TAmanagement.gi";
	private static final String HHT_URI = "http://www.example.org/hht";
	static DateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Logger LOG = LoggerFactory.getLogger(SITAservice.class);
	private static OMFactory OM_Factory = OMAbstractFactory.getOMFactory();

	/**
	 * 
	 * @param requestElement
	 * @return
	 * @throws AxisFault
	 */
	public static OMElement UpdateHILandRTR(OMElement requestElement)
			throws AxisFault {

		StAXOMBuilder builder = new StAXOMBuilder(
				requestElement.getOMFactory(), requestElement
						.getXMLStreamReader());

		OMElement responseElement = builder.getDocumentElement()
				.getFirstChildWithName(new QName(TAMANAGEMENT_URI, "data"));

		Iterator RTRiterator = responseElement.getFirstChildWithName(
				new QName(TAMANAGEMENT_URI, "FormModel"))
				.getFirstChildWithName(
						new QName(TAMANAGEMENT_URI, "Inspection"))
				.getChildrenWithName(new QName(TAMANAGEMENT_URI, "RTR"));

		while (RTRiterator.hasNext()) {
			OMElement currentRTR = (OMElement) RTRiterator.next();
			Iterator updateIterator = builder.getDocumentElement()
					.getFirstChildWithName(
							new QName(TAMANAGEMENT_URI, "incidents"))
					.getChildrenWithName(new QName(HHT_URI, "rtr"));

			while (updateIterator.hasNext()) {
				OMElement currentUpdateID = (OMElement) updateIterator.next();
				if (currentRTR.getFirstChildWithName(
						new QName(TAMANAGEMENT_URI, "RTRid")).getText().equals(
						currentUpdateID.getAttributeValue(new QName("rtr-id")))) {
					currentRTR.getFirstChildWithName(
							new QName(TAMANAGEMENT_URI, "RTRstatus")).setText(
							"resolved");
				}
			}
		}

		return responseElement;
	}

	/**
	 * 
	 * @param requestElement
	 * @return
	 * @throws AxisFault
	 */
	public static OMElement AssignTAtoShift(OMElement requestElement)
			throws AxisFault {
		try {
			StAXOMBuilder builder = new StAXOMBuilder(requestElement
					.getOMFactory(), requestElement.getXMLStreamReader());

			OMElement root = builder.getDocumentElement();

			try {

				String currentUser = root.getFirstChildWithName(
						new QName("user")).getText();

				// get shift start and end dateTimes from input
				Calendar start = convertToCalendar(root.getFirstChildWithName(
						new QName("startDate")).getText()
						+ " "
						+ root.getFirstChildWithName(new QName("startTime"))
								.getText());

				Calendar end = (Calendar) start.clone();
				end.add(Calendar.HOUR_OF_DAY, 8);
				end.add(Calendar.MINUTE, 30);

				// create connection to DB
				Connection connection = getMySQL("../conf/sita.properties",
						"dburl", "dbuser", "dbpassword");

				Statement statement = connection.createStatement();

				// Get shift from user
				ResultSet s = statement
						.executeQuery("SELECT shift FROM rma_coordinator WHERE identifier='"
								+ currentUser.substring(currentUser
										.indexOf('\\') + 1, currentUser
										.length()) + "'");

				String currentShift;

				if (s.next()) {
					currentShift = s.getString("shift");
				} else {
					currentShift = "A";
					System.out
							.println("WARNING: "
									+ currentUser
									+ " is not a registered user of the RMA. Using shift "
									+ currentShift
									+ " for prepopulation by default");
				}

				s.close();

				// get shift mechanics
				ResultSet m = statement
						.executeQuery("SELECT name, identifier, certified FROM rma_mechanic WHERE shift='"
								+ currentShift
								+ "' AND NOT certified='AUX' AND available=1");

				ArrayList<Mechanic> mechanics = new ArrayList<Mechanic>();

				while (m.next()) {
					mechanics
							.add(new Mechanic(m.getString("name"), m
									.getString("identifier"), m
									.getString("certified")));
				}

				m.close();

				// get shift avionics
				ResultSet a = statement
						.executeQuery("SELECT name, identifier, certified FROM rma_avionic WHERE shift='"
								+ currentShift + "' AND available=1");

				ArrayList<Mechanic> avionics = new ArrayList<Mechanic>();

				while (a.next()) {
					avionics
							.add(new Mechanic(a.getString("name"), a
									.getString("identifier"), a
									.getString("certified")));
				}

				a.close();

				// get shift coordinators
				ResultSet c = statement
						.executeQuery("SELECT name, identifier, certified FROM rma_coordinator WHERE shift='"
								+ currentShift + "' AND available=1");

				ArrayList<Mechanic> coordinators = new ArrayList<Mechanic>();

				while (c.next()) {
					coordinators
							.add(new Mechanic(c.getString("name"), c
									.getString("identifier"), c
									.getString("certified")));
				}

				c.close();

				statement.close();
				connection.close();

				connection = getMySQL("../conf/resources.properties",
						"resource.ds2.driverProperties.url",
						"resource.ds2.driverProperties.user",
						"resource.ds2.driverProperties.password");

				statement = connection.createStatement();

				// get non-released TAs
				ResultSet TAs = statement
						.executeQuery("SELECT id, output_xml FROM tempo_pa WHERE output_xml NOT LIKE '%InspectionStatus_released%' and not state=1");

				// get the TAs of this shift
				ArrayList<TAobject> ShiftTAs = getShiftTAs(TAs, start, end,
						currentShift);

				TAs.close();

				// if there are no TAs, then stop
				if (ShiftTAs.isEmpty()) {
					statement.close();
					connection.close();
					return root;
				}

				// TODO sort them in a more complex way
				// sort them by from earliest to latest
				fusion(0, ShiftTAs.size() - 1, ShiftTAs);

				int mechCursor = 0;
				int aviCursor = 0;
				int coordCursor = 0;

				ArrayList<Mechanic> skippedMechs = new ArrayList<Mechanic>();

				for (int i = 0; i < ShiftTAs.size(); i++) {

					try {
						OMElement currentTAdata = ShiftTAs.get(i)
								.getFormModel();

						// stupid namespace problem
						String namespace = "";
						if (currentTAdata.getFirstChildWithName(new QName(
								TAMANAGEMENT_URI, "Inspection")) != null) {
							namespace = TAMANAGEMENT_URI;
						}

						// First, we assign a coordinator. Simple round-robin
						// behavior

						// create assignedCoordinatorElement
						OMElement coordElement = createAssignedCoordElement(
								currentTAdata, coordinators.get(coordCursor)
										.getName(), coordinators.get(
										coordCursor).getId(), coordinators.get(
										coordCursor).getCertified(), namespace);

						currentTAdata.getFirstChildWithName(
								new QName(namespace, "Inspection")).addChild(
								coordElement);

						// increment cursor
						coordCursor++;
						if (coordCursor == coordinators.size())
							coordCursor = 0;

						// Now we need to assign mechanics

						// The first thing to do is check if the aircraft is a
						// narrow-body and we have an NB-certified mechanic in
						// the
						// skipped list
						if (!currentTAdata.getFirstChildWithName(
								new QName(namespace, "Activity"))
								.getFirstChildWithName(
										new QName(namespace, "AircraftID"))
								.getText().startsWith("TO")
								&& NBcertifiedMechanicIndex(skippedMechs) != -1) {

							int cursor = NBcertifiedMechanicIndex(skippedMechs);

							// create assignedMechanicElement
							OMElement mechElement = createAssignedMechElement(
									currentTAdata, skippedMechs.get(cursor)
											.getName(), skippedMechs
											.get(cursor).getId(), skippedMechs
											.get(cursor).getCertified(), 1,
									namespace);

							currentTAdata.getFirstChildWithName(
									new QName(namespace, "Inspection"))
									.addChild(mechElement);

							// remove the element at index "cursor" from the
							// skipped
							// list
							skippedMechs.remove(cursor);

						} else {

							boolean firstMechDetermined = false;

							while (!firstMechDetermined) {

								// If the current mechanic is BC-certified OR
								// [current
								// mechanic is NB-certified AND aircraft is
								// narrow-body], we can assign him without
								// checking
								// anything

								// Note that a BC-certified mechanic will never
								// be
								// skipped

								if (mechanics.get(mechCursor).getCertified()
										.equals("BC")
										|| (mechanics.get(mechCursor)
												.getCertified().equals("NB") && !currentTAdata
												.getFirstChildWithName(
														new QName(namespace,
																"Activity"))
												.getFirstChildWithName(
														new QName(namespace,
																"AircraftID"))
												.getText().startsWith("TO"))) {

									// create assignedMechanicElement
									OMElement mechElement = createAssignedMechElement(
											currentTAdata, mechanics.get(
													mechCursor).getName(),
											mechanics.get(mechCursor).getId(),
											mechanics.get(mechCursor)
													.getCertified(), 1,
											namespace);

									currentTAdata.getFirstChildWithName(
											new QName(namespace, "Inspection"))
											.addChild(mechElement);

									// increment cursor
									mechCursor++;
									if (mechCursor == mechanics.size())
										mechCursor = 0;

									// set "created" flag to true
									firstMechDetermined = true;

								} else {
									// Otherwise, we skip the current mechanic
									// and
									// store
									// him into the "skipped" list

									skippedMechs.add(mechanics.get(mechCursor));

									// increment cursor
									mechCursor++;
									if (mechCursor == mechanics.size())
										mechCursor = 0;
								}
							}
						}

						// Now we need to determine the second mechanic. The
						// first
						// thing
						// to do is check the AircraftID. If it is a wide-body,
						// we
						// need
						// a BC-certified mechanic. Otherwise, we look at the
						// skipped
						// list and take the first one there. If the skipped
						// list is
						// empty, we take the next one in the list
						if (!currentTAdata.getFirstChildWithName(
								new QName(namespace, "Activity"))
								.getFirstChildWithName(
										new QName(namespace, "AircraftID"))
								.getText().startsWith("TO")) {

							if (!skippedMechs.isEmpty()) {

								// create assignedMechanicElement
								OMElement mechElement = createAssignedMechElement(
										currentTAdata, skippedMechs.get(0)
												.getName(), skippedMechs.get(0)
												.getId(), skippedMechs.get(0)
												.getCertified(), 0, namespace);

								currentTAdata.getFirstChildWithName(
										new QName(namespace, "Inspection"))
										.addChild(mechElement);

								// remove the first element from the skipped
								// list
								skippedMechs.remove(0);

							} else {

								// create assignedMechanicElement
								OMElement mechElement = createAssignedMechElement(
										currentTAdata, mechanics
												.get(mechCursor).getName(),
										mechanics.get(mechCursor).getId(),
										mechanics.get(mechCursor)
												.getCertified(), 0, namespace);

								currentTAdata.getFirstChildWithName(
										new QName(namespace, "Inspection"))
										.addChild(mechElement);

								// increment cursor
								mechCursor++;
								if (mechCursor == mechanics.size())
									mechCursor = 0;
							}
						} else {

							// wide-body airplane. In that case we need another
							// BC-certified mechanic

							boolean secondMechDetermined = false;

							while (!secondMechDetermined) {

								if (mechanics.get(mechCursor).getCertified()
										.equals("BC")) {

									// create assignedMechanicElement
									OMElement mechElement = createAssignedMechElement(
											currentTAdata, mechanics.get(
													mechCursor).getName(),
											mechanics.get(mechCursor).getId(),
											mechanics.get(mechCursor)
													.getCertified(), 0,
											namespace);

									currentTAdata.getFirstChildWithName(
											new QName(namespace, "Inspection"))
											.addChild(mechElement);

									// increment cursor
									mechCursor++;
									if (mechCursor == mechanics.size())
										mechCursor = 0;

									// set "created" flag to true
									secondMechDetermined = true;

								} else {
									// Otherwise, we skip the current mechanic
									// and
									// store
									// him into the "skipped" list

									skippedMechs.add(mechanics.get(mechCursor));

									// increment cursor
									mechCursor++;
									if (mechCursor == mechanics.size())
										mechCursor = 0;
								}
							}
						}

						// Now we need to assign one avionic. That's a simple
						// round-robin behavior

						// create assignedMechanicElement
						OMElement aviElement = createAssignedAviElement(
								currentTAdata, avionics.get(aviCursor)
										.getName(), avionics.get(aviCursor)
										.getId(), avionics.get(aviCursor)
										.getCertified(), namespace);

						currentTAdata.getFirstChildWithName(
								new QName(namespace, "Inspection")).addChild(
								aviElement);

						// increment cursor
						aviCursor++;
						if (aviCursor == avionics.size())
							aviCursor = 0;

						// This TA is now assigned for the current shift, let's
						// mark
						// it
						// as such
						currentTAdata.getFirstChildWithName(
								new QName(namespace, "Inspection"))
								.getFirstChildWithName(
										new QName(namespace, "assigned"))
								.setText(currentShift);

						// All that's left is update the TA in the DB

						statement
								.executeUpdate("UPDATE tempo_pa SET output_xml='"
										+ currentTAdata.toString()
										+ "' WHERE id="
										+ ShiftTAs.get(i).getId());
					} catch (Exception e) {
						// If something goes wrong for some strage odd reason,
						// let's
						// not jeopardize the others
						System.out
								.println("[SITA]: something went wrong to prepopulate TA "
										+ ShiftTAs.get(i).getId()
										+ ". See stacktrace below");
						e.printStackTrace();
					}
				}
				statement.close();
				connection.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

			return root;
		} catch (Exception e) {
			e.printStackTrace();

			throw prepareAxisFault(e, new QName("", "AssignTaToShift"));
		}
	}

	/**
	 * 
	 * @param requestElement
	 * @return
	 * @throws AxisFault
	 */
	public static OMElement ImportFMR(OMElement requestElement)
			throws AxisFault {

		OMElement responseElement = createEmptyFMRelement();

		String filepath = requestElement.getText();

		try {

			OMElement myElement = AXIOMUtil.stringToOM(filepath);

			while (!myElement.getLocalName().equals("FMR")) {
				myElement = myElement.getFirstElement();
			}

			Iterator<OMAttribute> attIterator = myElement.getAllAttributes();

			while (attIterator.hasNext()) {

				OMAttribute current = attIterator.next();

				if (current.getLocalName().equals("Aircraft")) {
					setElementValue(myElement, responseElement, "Aircraft",
							"aircraft");
				} else if (current.getLocalName().equals("STA")) {
					setElementValue(myElement, responseElement, "STA",
							"ScheduledArrivalDate");
					setElementValue(myElement, responseElement, "STA", "STA");
				} else if (current.getLocalName().equals("ArrivalFlightNumber")) {
					setElementValue(myElement, responseElement,
							"ArrivalFlightNumber", "ArrivalFlightNumber");
				} else if (current.getLocalName().equals("ATA")) {
					setElementValue(myElement, responseElement, "ATA",
							"ActualArrivalDate");
					setElementValue(myElement, responseElement, "ATA", "ATA");
				} else if (current.getLocalName().equals("STD")) {
					setElementValue(myElement, responseElement, "STD",
							"ScheduledDepartureDate");
					setElementValue(myElement, responseElement, "STD", "STD");
				} else if (current.getLocalName().equals("ATD")) {
					setElementValue(myElement, responseElement, "ATD",
							"ActualDepartureDate");
					setElementValue(myElement, responseElement, "ATD", "ATD");
				} else if (current.getLocalName().equals("Stand")) {
					setElementValue(myElement, responseElement, "Stand",
							"Stand");
				} else if (current.getLocalName().equals("InspectionType")) {
					setElementValue(myElement, responseElement,
							"InspectionType", "InspectionType");
				} else if (current.getLocalName().equals(
						"DepartureFlightNumber")) {
					setElementValue(myElement, responseElement,
							"DepartureFlightNumber", "DepartureFlightNumber");
				} else if (current.getLocalName().equals("Rtr-id")) {
					setElementValue(myElement, responseElement, "Rtr-id",
							"RTRid");
				}
			}

			responseElement.getFirstChildWithName(new QName("filepath"))
					.setText("");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseElement;
	}

	/**
	 * 
	 * @param requestElement
	 * @return
	 * @throws AxisFault
	 */
	public static OMElement readProperty(OMElement requestElement)
			throws AxisFault {

		OMElement responseElement = requestElement;

		String propertyName = requestElement.getText();

		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream("../conf/sita.properties"));
			responseElement.setText(properties.getProperty(propertyName));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseElement;
	}

	public static OMElement updateTA(OMElement requestElement)
			throws AxisFault, XmlException {
		try {

			UpdateInputDocumentImpl updateRequest = (UpdateInputDocumentImpl) UpdateInputDocument.Factory
					.parse("<xml-fragment>" + requestElement
							+ "</xml-fragment>");

			UpdateInput input = updateRequest.getUpdateInput();

			Data data = input.getData();

			ArrivalDepartureType arrivalDeparture = data.getFormModel()
					.getArrivalDeparture();
			ActivityType activity = data.getFormModel().getActivity();

			FMR FMRelement = input.getFMR();
			boolean update = false;

			if (FMRelement.xgetAircraft() != null) {
				String aircraft = FMRelement.getAircraft();
				String oldAircraft = activity.getAircraftID();
				activity.setAircraftID(aircraft.substring(
						aircraft.length() - 3, aircraft.length()));
				if (activity.xgetAircraftID() != null
						&& !activity.getAircraftID().equals(oldAircraft)) {
					update = true;
				}

			}

			if (FMRelement.xgetATA() != null && FMRelement.xgetATA().validate()) {
				Calendar FMR_ATA = FMRelement.getATA();
				FMR_ATA = removeTimezone(FMR_ATA);
				if (arrivalDeparture.xgetActualArrivalDate() != null
						&& arrivalDeparture.xgetActualArrivalDate().validate()
						&& arrivalDeparture.xgetATA() != null
						&& arrivalDeparture.xgetATA().validate()) {
					Calendar dateElement = arrivalDeparture
							.getActualArrivalDate();
					Calendar timeElement = arrivalDeparture.getATA();

					if (!FMR_ATA.equals(add(dateElement, timeElement))) {
						update = true;
					}

				}
				arrivalDeparture.setActualArrivalDate(FMR_ATA);
				arrivalDeparture.setATA(FMR_ATA);
			}

			if (FMRelement.xgetSTA() != null && FMRelement.xgetSTA().validate()) {
				Calendar FMR_STA = FMRelement.getSTA();
				FMR_STA = removeTimezone(FMR_STA);
				if (arrivalDeparture.xgetScheduledArrivalDate() != null
						&& arrivalDeparture.xgetScheduledArrivalDate()
								.validate()
						&& arrivalDeparture.xgetSTA() != null
						&& arrivalDeparture.xgetSTA().validate()) {
					Calendar dateElement = arrivalDeparture
							.getScheduledArrivalDate();
					Calendar timeElement = arrivalDeparture.getSTA();

					if (!FMR_STA.equals(add(dateElement, timeElement))) {
						update = true;
					}

				}
				arrivalDeparture.setScheduledArrivalDate(FMR_STA);
				arrivalDeparture.setSTA(FMR_STA);
			}

			if (FMRelement.xgetSTD() != null && FMRelement.xgetSTD().validate()) {
				Calendar FMR_STD = FMRelement.getSTD();
				FMR_STD = removeTimezone(FMR_STD);
				if (arrivalDeparture.xgetScheduledDepartureDate() != null
						&& arrivalDeparture.xgetScheduledDepartureDate()
								.validate()
						&& arrivalDeparture.xgetSTD() != null
						&& arrivalDeparture.xgetSTD().validate()) {
					Calendar dateElement = arrivalDeparture
							.getScheduledDepartureDate();
					Calendar timeElement = arrivalDeparture.getSTD();

					if (!FMR_STD.equals(add(dateElement, timeElement))) {
						update = true;
					}

				}
				arrivalDeparture.setScheduledDepartureDate(FMR_STD);
				arrivalDeparture.setSTD(FMR_STD);
			}

			if (FMRelement.xgetATD() != null && FMRelement.xgetATD().validate()) {
				Calendar FMR_ATD = FMRelement.getATD();
				FMR_ATD = removeTimezone(FMR_ATD);
				if (arrivalDeparture.xgetActualArrivalDate() != null
						&& arrivalDeparture.xgetActualArrivalDate().validate()
						&& arrivalDeparture.xgetATD() != null
						&& arrivalDeparture.xgetATD().validate()) {
					Calendar dateElement = arrivalDeparture
							.getActualArrivalDate();
					Calendar timeElement = arrivalDeparture.getATD();

					if (!FMR_ATD.equals(add(dateElement, timeElement))) {
						update = true;
					}

				}
				arrivalDeparture.setActualArrivalDate(FMR_ATD);
				arrivalDeparture.setATD(FMR_ATD);
			}

			if (FMRelement.xgetETD() != null && FMRelement.xgetETD().validate()) {
				Calendar FMR_ETD = FMRelement.getETD();
				FMR_ETD = removeTimezone(FMR_ETD);
				if (arrivalDeparture.xgetEstimatedDepartureDate() != null
						&& arrivalDeparture.xgetEstimatedDepartureDate()
								.validate()
						&& arrivalDeparture.xgetETD() != null
						&& arrivalDeparture.xgetETD().validate()) {
					Calendar dateElement = arrivalDeparture
							.getEstimatedDepartureDate();
					Calendar timeElement = arrivalDeparture.getETD();

					if (!FMR_ETD.equals(add(dateElement, timeElement))) {
						update = true;
					}

				}
				arrivalDeparture.setEstimatedDepartureDate(FMR_ETD);
				arrivalDeparture.setETD(FMR_ETD);
			}

			if (FMRelement.xgetETA() != null && FMRelement.xgetETA().validate()) {
				Calendar FMR_ETA = FMRelement.getETA();
				FMR_ETA = removeTimezone(FMR_ETA);
				if (arrivalDeparture.xgetEstimatedArrivalDate() != null
						&& arrivalDeparture.xgetEstimatedArrivalDate()
								.validate()
						&& arrivalDeparture.xgetETA() != null
						&& arrivalDeparture.xgetETA().validate()) {
					Calendar dateElement = arrivalDeparture
							.getEstimatedArrivalDate();
					Calendar timeElement = arrivalDeparture.getETA();

					if (!FMR_ETA.equals(add(dateElement, timeElement))) {
						update = true;
					}

				}
				arrivalDeparture.setEstimatedArrivalDate(FMR_ETA);
				arrivalDeparture.setETA(FMR_ETA);
			}

			if (FMRelement.xgetDepartureFlightNumber() != null) {
				String departure = FMRelement.getDepartureFlightNumber();
				String oldDeparture = arrivalDeparture
						.getDepartureFlightNumber();
				arrivalDeparture.setDepartureFlightNumber(departure);
				if (arrivalDeparture.xgetDepartureFlightNumber() != null
						&& !arrivalDeparture.getDepartureFlightNumber().equals(
								oldDeparture)) {
					update = true;
				}

			}

			InspectionType inspection = input.getData().getFormModel()
					.getInspection();
			if (FMRelement.xgetStand() != null) {
				String Stand = FMRelement.getStand();
				String oldStand;
				if (inspection.getStand() != null) {
					oldStand = inspection.getStand();
				} else {
					oldStand = null;
				}
				inspection.setStand(Stand);
				if (inspection.xgetStand() != null
						&& !inspection.getStand().equals(oldStand)) {
					update = true;
				}

			}

			if (FMRelement.xgetFlightStatus() != null) {
				String flightStatus = FMRelement.getFlightStatus();
				inspection.setFlightStatus(flightStatus);
			}

			if (FMRelement.getMaintTaskArray() != null) {
				MaintTask[] maintTasks = FMRelement.getMaintTaskArray();
				ArrayList<InspectionType.TaTasks> tasks = new ArrayList<InspectionType.TaTasks>();
				InspectionType.RTR[] oldRTRs = inspection.getRTRArray();
				oldRTRs = clone(oldRTRs);
				ArrayList<RTR> newRTRsArray = new ArrayList<RTR>();
				for (MaintTask maintTask : maintTasks) {
					InspectionType.TaTasks task = InspectionType.TaTasks.Factory
							.newInstance();
					task.setInspectionType(maintTask.getInspectionType());
					task.setMTstartDate(maintTask.getMaintStartDate());
					task.setMTstartTime(maintTask.getMaintStartDate());
					task.setMTendDate(maintTask.getMaintEndtDate());
					task.setMTendTime(maintTask.getMaintEndtDate());
					if (maintTask.getRemarks() != null) {
						task.setRemarks(maintTask.getRemarks());
					}
					tasks.add(task);

					String[] FMRrtrs = maintTask.getRTRIDArray();
					for (String RTRid : FMRrtrs) {
						// check if that RTR existed before
						int contain = contains(oldRTRs, RTRid);
						if (contain != -1) {
							newRTRsArray.add(oldRTRs[contain]);
						} else {
							// new RTR
							RTR newRTR = RTR.Factory.newInstance();
							newRTR.setRTRid(RTRid);
							newRTR.setRTRad(false);
							newRTR.setRTRstatus(RTRstatus.OPEN);
							newRTRsArray.add(newRTR);
						}
					}
				}
				inspection.setTaTasksArray(tasks
						.toArray(new InspectionType.TaTasks[0]));
				inspection.setRTRArray(newRTRsArray.toArray(new RTR[0]));
			}

			// if (FMRelement.xgetInspectionType() != null) {
			// String inspectionType = FMRelement.getInspectionType();
			// inspectionType = inspectionType.substring(inspectionType
			// .lastIndexOf(";") + 1);
			// inspectionType = inspectionType.replace("+", "");
			// inspection.setInspectionType(inspectionType);
			// if (inspection.xgetInspectionType() != null
			// && !inspection.getInspectionType().equals(
			// inspectionType)) {
			// update = true;
			// }
			//
			// }

			// String newRTR_ids = "";
			// if (FMRelement.xgetRtrId() != null) {
			// newRTR_ids = FMRelement.getRtrId();
			//
			// } else {
			// // No RTRs, so need to delete the old ones
			// newRTR_ids = "";
			// }
			// RTR[] oldRTRs = inspection.getRTRArray();
			//
			// oldRTRs = clone(oldRTRs);
			//
			// String oldRTR_ids = "";
			// for (RTR oldRTR : oldRTRs) {
			// oldRTR_ids += oldRTR.getRTRid() + ";";
			// }
			// if (oldRTR_ids.endsWith(";")) {
			// oldRTR_ids = oldRTR_ids.substring(0, oldRTR_ids.length() - 1);
			// }
			//
			// if (!newRTR_ids.equals(oldRTR_ids)) {
			// // remove all old RTRs
			//
			// inspection.setRTRArray(new RTR[0]);
			//
			// // check that we still have all the old RTRs, to be removed
			//
			// StringTokenizer tok = new StringTokenizer(newRTR_ids, ";");
			//
			// ArrayList<RTR> newRTRsArray = new ArrayList<RTR>();
			//
			// while (tok.hasMoreTokens()) {
			// String currentID = tok.nextToken();
			//
			// int contains = contains(oldRTRs, currentID);
			//
			// if (contains != -1) {
			//
			// newRTRsArray.add(oldRTRs[contains]);
			// } else {
			//
			// RTR newRTR = RTR.Factory.newInstance();
			//
			// newRTR.setRTRid(currentID);
			//
			// newRTR.setRTRad(false);
			// newRTR.setRTRstatus(RTRstatus.OPEN);
			// newRTRsArray.add(newRTR);
			// }
			// }
			//
			// inspection.setRTRArray(newRTRsArray.toArray(new RTR[0]));
			//
			// update = true;
			// }

			if (update) {

				activity.setUpdate("1");

			}

			// return responseElement;

			// /////ADDED by Ihab//
			// OMAttribute current = FMRelement.getAttribute(new
			// QName("Rtr-id"));
			// String currentValue;
			// if(current==null){
			// currentValue="";
			// }
			// else{
			// currentValue=current.getAttributeValue();
			// }
			//
			//		
			//
			// Iterator<OMElement> iter = responseElement
			// .getFirstChildWithName(
			// new QName(TAMANAGEMENT_URI, "FormModel"))
			// .getFirstChildWithName(
			// new QName(TAMANAGEMENT_URI, "Inspection"))
			// .getChildrenWithName(new QName(TAMANAGEMENT_URI, "RTR"));
			//
			// String RTRstring = "";
			//
			// while (iter.hasNext()) {
			// OMElement currentRTR = iter.next();
			// RTRstring += currentRTR.getFirstChildWithName(
			// new QName(TAMANAGEMENT_URI, "RTRid")).getText()
			// + ";";
			// }
			//
			// if (RTRstring.endsWith(";")) {
			// RTRstring = RTRstring.substring(0, RTRstring.length() - 1);
			// }
			//
			// if (!RTRstring.equals(currentValue)) {
			// iter = responseElement.getFirstChildWithName(
			// new QName(TAMANAGEMENT_URI, "FormModel"))
			// .getFirstChildWithName(
			// new QName(TAMANAGEMENT_URI, "Inspection"))
			// .getChildrenWithName(
			// new QName(TAMANAGEMENT_URI, "RTR"));
			//
			//				
			//
			// ArrayList<OMElement> OldRTRlist = new ArrayList<OMElement>();
			//
			// while (iter.hasNext()) {
			// OldRTRlist.add(iter.next());
			// iter.remove();
			// }
			// StringTokenizer tok = new StringTokenizer(currentValue, ";");
			//
			// ArrayList<OMElement> NewRTRlist = new ArrayList<OMElement>();
			//
			// while (tok.hasMoreTokens()) {
			// String currentID = tok.nextToken();
			// int index = contains(OldRTRlist, currentID);
			//
			// if (index != -1) {
			// NewRTRlist.add(OldRTRlist.get(index));
			// } else {
			// NewRTRlist.add(createRTR(currentID));
			// }
			// }
			//
			// for (int i = NewRTRlist.size() - 1; i >= 0; i--) {
			// responseElement.getFirstChildWithName(
			// new QName(TAMANAGEMENT_URI, "FormModel"))
			// .getFirstChildWithName(
			// new QName(TAMANAGEMENT_URI,
			// "Inspection"))
			// .getFirstChildWithName(
			// new QName(TAMANAGEMENT_URI,
			// "coordinator"))
			// .insertSiblingAfter(NewRTRlist.get(i));
			// }
			//
			// update = true;
			// }
			// /////////////END ADDED BY IHAB
			// if (update)
			// responseElement.getFirstChildWithName(
			// new QName(TAMANAGEMENT_URI, "FormModel"))
			// .getFirstChildWithName(
			// new QName(TAMANAGEMENT_URI, "Activity"))
			// .getFirstChildWithName(
			// new QName(TAMANAGEMENT_URI, "update")).setText("1");
			//
			// return responseElement;
			// }

			DataDocumentImpl response = (DataDocumentImpl) DataDocument.Factory
					.newInstance();
			response.setData(data);

			OMElement doc = toOM(response);
			OMElement docWrapper = doc.getOMFactory().createOMElement(
					new QName(TAMANAGEMENT_URI, "data"));
			docWrapper.setFirstChild(doc);

			return docWrapper;
		} catch (Exception e) {
			e.printStackTrace();

			throw prepareAxisFault(e, new QName("", "UpdateTA_Fault"));
		}
	}

	private static AxisFault prepareAxisFault(Exception e, QName qname) {
		AxisFault fault = new AxisFault(e.getMessage(), e);
		OMElement response = OM_Factory.createOMElement(qname);
		Throwable throwable = e.fillInStackTrace();
		final Writer message = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(message);
		throwable.printStackTrace(printWriter);
		response.setText(message.toString());
		fault.setDetail(response);
		return fault;
	}

	private static RTR[] clone(RTR[] list) {
		Collection<RTR> response = new ArrayList<RTR>();
		for (RTR rtr : list) {
			RTRImpl rtrClone = (RTRImpl) RTR.Factory.newInstance();
			rtrClone.set(rtr.copy());
			response.add(rtrClone);
		}
		return response.toArray(new RTR[0]);
	}

	private static Calendar add(Calendar dateElement, Calendar timeElement) {
		Calendar response = (Calendar) dateElement.clone();
		response.setTime(dateElement.getTime());
		response.add(Calendar.HOUR, timeElement.getTime().getHours());
		response.add(Calendar.MINUTE, timeElement.getTime().getMinutes());
		response.add(Calendar.SECOND, timeElement.getTime().getSeconds());
		return response;

	}

	/**
	 * 
	 * @param requestElement
	 * @return
	 * @throws AxisFault
	 */
	public static OMElement getRoles(OMElement requestElement) throws AxisFault {

		OMFactory fac = OMAbstractFactory.getOMFactory();

		OMElement responseElement = fac.createOMElement("roles", requestElement
				.getNamespace());

		String arrivalDate = requestElement.getFirstChildWithName(
				new QName(TAMANAGEMENT_URI, "startDateTime")).getText()
				.replace("T", " ").replace("Z", "");

		String departureDate = requestElement.getFirstChildWithName(
				new QName(TAMANAGEMENT_URI, "endDateTime")).getText().replace(
				"T", " ").replace("Z", "");

		try {
			// get DB configuration from sita.properties
			Connection connection = getMySQL("../conf/sita.properties",
					"dburl", "dbuser", "dbpassword");

			Statement statement = connection.createStatement();

			ResultSet s = statement
					.executeQuery("SELECT DISTINCT shift_name FROM rma_shift WHERE (start_time < '"
							+ arrivalDate
							+ "' AND end_time > '"
							+ arrivalDate
							+ "') OR (start_time < '"
							+ departureDate
							+ "' AND end_time > '"
							+ departureDate
							+ "') OR (start_time > '"
							+ arrivalDate
							+ "' AND end_time < '" + departureDate + "')");

			String roles = "";

			while (s.next()) {
				roles += "intalio\\coordinator" + s.getString("shift_name")
						+ ",";
			}

			s.close();
			connection.close();

			if (roles.endsWith(",")) {
				roles = roles.substring(0, roles.length() - 1);
			}

			OMElement rolesElement = fac.createOMElement(new QName("roles"));
			rolesElement.setText(roles);

			responseElement.addChild(rolesElement);

		} catch (Exception e) {
			e.printStackTrace();
			throw new AxisFault("DB connection is not available");
		}

		return responseElement;
	}

	/**
	 * 
	 * @param filepath
	 * @param urlProp
	 * @param userProp
	 * @param pwdProp
	 * @return
	 */
	private static Connection getMySQL(String filepath, String urlProp,
			String userProp, String pwdProp) {

		Connection connection = null;

		try {

			// get DB configuration from resources.properties
			Properties properties = new Properties();
			properties.load(new FileInputStream(filepath));
			Class.forName("com.mysql.jdbc.Driver");
			String url = properties.getProperty(urlProp);
			String username = properties.getProperty(userProp);
			String password = properties.getProperty(pwdProp);

			// create connection to DB
			connection = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return connection;

	}

	// /** NO longer needed after ihab's updateTA refactoring. Now contructed
	// with XML Beans
	// *
	// * @param RTRid
	// * @return
	// */
	// private static OMElement createRTR(String RTRid) {
	//
	// OMFactory fac = OMAbstractFactory.getOMFactory();
	// OMNamespaceImpl ns = new OMNamespaceImpl(TAMANAGEMENT_URI, "gi");
	// OMElement RTRelement = fac.createOMElement("RTR", ns);
	//
	// ArrayList<OMElement> childList = new ArrayList<OMElement>();
	//
	// OMElement id = fac.createOMElement("RTRid", ns);
	// id.setText(RTRid);
	// childList.add(id);
	// // childList.add(fac.createOMElement(new QName("RTRdate")));
	// childList.add(fac.createOMElement("RTRdescription", ns));
	// OMElement status = fac.createOMElement("RTRstatus", ns);
	// status.setText("open");
	// childList.add(status);
	// OMElement ad = fac.createOMElement("RTRad", ns);
	// ad.setText("false");
	// childList.add(ad);
	//
	// for (int i = 0; i < childList.size(); i++) {
	// RTRelement.addChild(childList.get(i));
	// }
	//
	// return RTRelement;
	// }

	/**
	 * 
	 * @return
	 */
	private static OMElement createEmptyFMRelement() {

		OMFactory fac = OMAbstractFactory.getOMFactory();

		OMElement FMRelement = fac.createOMElement("FormModel", null);

		ArrayList<OMElement> childList = new ArrayList<OMElement>();

		childList.add(fac.createOMElement(new QName("aircraft")));
		childList.add(fac.createOMElement(new QName("ScheduledArrivalDate")));
		childList.add(fac.createOMElement(new QName("STA")));
		childList.add(fac.createOMElement(new QName("ArrivalFlightNumber")));
		childList.add(fac.createOMElement(new QName("ActualArrivalDate")));
		childList.add(fac.createOMElement(new QName("ATA")));
		childList.add(fac.createOMElement(new QName("ScheduledDepartureDate")));
		childList.add(fac.createOMElement(new QName("STD")));
		childList.add(fac.createOMElement(new QName("ActualDepartureDate")));
		childList.add(fac.createOMElement(new QName("ATD")));
		childList.add(fac.createOMElement(new QName("Stand")));
		childList.add(fac.createOMElement(new QName("InspectionType")));
		childList.add(fac.createOMElement(new QName("DepartureFlightNumber")));
		childList.add(fac.createOMElement(new QName("RTRid")));
		childList.add(fac.createOMElement(new QName("filepath")));

		for (int i = 0; i < childList.size(); i++) {
			FMRelement.addChild(childList.get(i));
		}

		return FMRelement;
	}

	/**
	 * 
	 * @param source
	 * @param target
	 * @param sourceAttributeName
	 * @param targetElementName
	 */
	private static void setElementValue(OMElement source, OMElement target,
			String sourceAttributeName, String targetElementName) {

		if (targetElementName.endsWith("Date")) {
			target.getFirstChildWithName(new QName(targetElementName)).setText(
					source.getAttributeValue(new QName(sourceAttributeName))
							.substring(0, 10));
		} else if (targetElementName.startsWith("ST")
				|| targetElementName.startsWith("AT")) {
			target.getFirstChildWithName(new QName(targetElementName)).setText(
					source.getAttributeValue(new QName(sourceAttributeName))
							.substring(11, 19));
		} else {
			target.getFirstChildWithName(new QName(targetElementName)).setText(
					source.getAttributeValue(new QName(sourceAttributeName)));
		}
	}

	/**
	 * 
	 * @param input
	 * @return
	 * @throws ParseException
	 */
	private static Calendar convertToCalendar(String input)
			throws ParseException {
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar output = Calendar.getInstance();
		output.setTime(s.parse(input));
		return output;
	}

	/**
	 * 
	 * @param TAs
	 * @param start
	 * @param end
	 * @param currentShift
	 * @return
	 * @throws Exception
	 */
	private static ArrayList<TAobject> getShiftTAs(ResultSet TAs,
			Calendar start, Calendar end, String currentShift)
			throws SQLException {

		ArrayList<TAobject> ShiftTAs = new ArrayList<TAobject>();

		while (TAs.next()) {

			try {

				OMElement myElement = AXIOMUtil.stringToOM(TAs
						.getString("output_xml"));

				StAXOMBuilder builder = new StAXOMBuilder(myElement
						.getOMFactory(), myElement.getXMLStreamReader());

				// stupid namespace problem
				String namespace = "";
				if (builder.getDocumentElement().getFirstChildWithName(
						new QName(TAMANAGEMENT_URI, "Inspection")) != null) {
					namespace = TAMANAGEMENT_URI;
				}

				// Consider only TAP flights, whose aircraft start with "T"
				if (builder.getDocumentElement().getFirstChildWithName(
						new QName(namespace, "Activity"))
						.getFirstChildWithName(
								new QName(namespace, "AircraftID")).getText()
						.startsWith("T")) {

					OMElement AD = builder.getDocumentElement()
							.getFirstChildWithName(
									new QName(namespace, "ArrivalDeparture"));

					String dateTime;

					if (AD.getFirstChildWithName(new QName(namespace,
							"ActualArrivalDate")) != null) {
						dateTime = AD.getFirstChildWithName(
								new QName(namespace, "ActualArrivalDate"))
								.getText()
								+ " "
								+ AD.getFirstChildWithName(
										new QName(namespace, "ATA")).getText();
					} else if (AD.getFirstChildWithName(new QName(namespace,
							"EstimatedArrivalDate")) != null) {
						dateTime = AD.getFirstChildWithName(
								new QName(namespace, "EstimatedArrivalDate"))
								.getText()
								+ " "
								+ AD.getFirstChildWithName(
										new QName(namespace, "ETA")).getText();
					} else if (AD.getFirstChildWithName(new QName(namespace,
							"ScheduledArrivalDate")) != null) {
						dateTime = AD.getFirstChildWithName(
								new QName(namespace, "ScheduledArrivalDate"))
								.getText()
								+ " "
								+ AD.getFirstChildWithName(
										new QName(namespace, "STA")).getText();
					} else {
						dateTime = "2008-01-01 01:00:00";
					}

					Calendar TAstartDateTime = convertToCalendar(dateTime);

					if (AD.getFirstChildWithName(new QName(namespace,
							"ActualDepartureDate")) != null) {
						dateTime = AD.getFirstChildWithName(
								new QName(namespace, "ActualDepartureDate"))
								.getText()
								+ " "
								+ AD.getFirstChildWithName(
										new QName(namespace, "ATD")).getText();
					} else if (AD.getFirstChildWithName(new QName(namespace,
							"EstimatedDepartureDate")) != null) {
						dateTime = AD.getFirstChildWithName(
								new QName(namespace, "EstimatedDepartureDate"))
								.getText()
								+ " "
								+ AD.getFirstChildWithName(
										new QName(namespace, "ETD")).getText();
					} else if (AD.getFirstChildWithName(new QName(namespace,
							"ScheduledDepartureDate")) != null) {
						dateTime = AD.getFirstChildWithName(
								new QName(namespace, "ScheduledDepartureDate"))
								.getText()
								+ " "
								+ AD.getFirstChildWithName(
										new QName(namespace, "STD")).getText();
					} else {
						// if we have no STD, take a far-away date in the
						// future
						dateTime = "2100-12-12 23:00:00";
					}

					Calendar TAendDateTime = convertToCalendar(dateTime);

					// NOTE: the "if" statement below takes into
					// account whether a TA has been assigned already during a
					// given shift, as opposed to one right after who doesn't.
					// Only one of those two statements should be active,
					// depending on the behavior that we want to follow. In
					// other words, IF there are new TAs in the
					// middle of a shift and two or more pre-populations are
					// required, then we only pre-populate the new ones. But
					// this doesn't comply with the pre-population algorithm

					if (((TAstartDateTime.after(start) && TAstartDateTime
							.before(end))
							|| (TAendDateTime.after(start) && TAendDateTime
									.before(end)) || (TAstartDateTime
							.before(start) && TAendDateTime.after(end)))
							&& !builder.getDocumentElement()
									.getFirstChildWithName(
											new QName(namespace, "Inspection"))
									.getFirstChildWithName(
											new QName(namespace, "assigned"))
									.getText().equals(currentShift)) {

						// Now that we have the dateTime to consider, we need to
						// check that it corresponds to the shift we're working
						// on

						// if ((TAstartDateTime.after(start) && TAstartDateTime
						// .before(end))
						// || (TAendDateTime.after(start) && TAendDateTime
						// .before(end))
						// || (TAstartDateTime.before(start) && TAendDateTime
						// .after(end))) {

						// scratch assigned mechanics/avionics/coordinators
						OMElement ins = builder.getDocumentElement()
								.getFirstChildWithName(
										new QName(namespace, "Inspection"));

						clear(ins, "assignedMechanics");

						clear(ins, "assignedAvionics");

						clear(ins, "assignedCoord");

						ShiftTAs.add(new TAobject(builder.getDocumentElement(),
								TAendDateTime, TAs.getInt("id")));
					}
				}
			} catch (Exception e) {
				// If that also fails, then let's not jeopardize the other
				// TAs
				System.out.println("[SITA]: Checking task " + TAs.getInt("id")
						+ " failed, see stackTrace below");
				e.printStackTrace();
			}
		}

		return ShiftTAs;
	}

	private static void clear(OMElement ins, String name) {

		// for security, let's do it with both potential namespaces. Anyway, the
		// useless one will be empty so there's not much more resources involved
		Iterator iter = ins.getChildrenWithName((new QName("", name)));
		Iterator iter2 = ins.getChildrenWithName((new QName(TAMANAGEMENT_URI,
				name)));

		while (iter.hasNext()) {
			iter.next();
			iter.remove();
		}

		while (iter2.hasNext()) {
			iter2.next();
			iter2.remove();
		}
	}

	/**
	 * 
	 * @param tab
	 * @param debut
	 * @param fin
	 */
	private static void Interclassement(ArrayList<TAobject> tab, int debut,
			int fin) {
		TAobject[] temp = new TAobject[tab.size()];
		for (int i = 0; i < tab.size(); i++) {
			temp[i] = tab.get(i);
		}
		int middle = (debut + fin) / 2;
		int counter1 = debut;
		int counter2 = middle + 1;
		int i = debut;
		while (i <= fin) {
			if (counter1 <= middle && counter2 <= fin) {
				if (temp[counter1].getDate().before(temp[counter2].getDate())) {
					tab.set(i, temp[counter1]);
					counter1++;
				} else if (temp[counter2].getDate().before(
						temp[counter1].getDate())) {
					tab.set(i, temp[counter2]);
					counter2++;
				} else {
					tab.set(i, temp[counter1]);
					tab.set(i + 1, temp[counter2]);
					counter1++;
					counter2++;
					i++;
				}
			} else if (counter1 <= middle) {
				tab.set(i, temp[counter1]);
				counter1++;
			} else if (counter2 <= fin) {
				tab.set(i, temp[counter2]);
				counter2++;
			}
			i++;
		}
	}

	/**
	 * 
	 * @param debut
	 * @param fin
	 * @param tab
	 */
	private static void fusion(int debut, int fin, ArrayList<TAobject> tab) {
		if (fin - debut > 1) {
			int middle = (debut + fin) / 2;
			fusion(debut, middle, tab);
			fusion(middle + 1, fin, tab);
			Interclassement(tab, debut, fin);
		} else if (tab.get(debut).getDate().after(tab.get(fin).getDate())) {
			TAobject temp = tab.get(debut);
			tab.set(debut, tab.get(fin));
			tab.set(fin, temp);
		}
	}

	/**
	 * 
	 * @param TAdata
	 * @param name
	 * @param id
	 * @param certified
	 * @param release
	 * @return
	 */
	private static OMElement createAssignedMechElement(OMElement TAdata,
			String name, String id, String certified, int release,
			String namespace) {

		OMElementImpl assignedMech = new OMElementImpl(new QName(namespace,
				"assignedMechanics"), TAdata, TAdata.getOMFactory());

		OMElementImpl mechName = new OMElementImpl(new QName(namespace,
				"assignedMechanicName"), assignedMech, TAdata.getOMFactory());

		mechName.setText(name);

		OMElementImpl mechID = new OMElementImpl(new QName(namespace,
				"assignedMechanicID"), assignedMech, TAdata.getOMFactory());

		mechID.setText(id);

		OMElementImpl mechCertified = new OMElementImpl(new QName(namespace,
				"assignedMechanicCert"), assignedMech, TAdata.getOMFactory());

		mechCertified.setText(certified);

		OMElementImpl mechRelease = new OMElementImpl(new QName(namespace,
				"entitledToRelease"), assignedMech, TAdata.getOMFactory());

		mechRelease.setText(String.valueOf(release));

		assignedMech.addChild(mechName);
		assignedMech.addChild(mechID);
		assignedMech.addChild(mechCertified);
		assignedMech.addChild(mechRelease);

		return assignedMech;
	}

	/**
	 * 
	 * @param TAdata
	 * @param name
	 * @param id
	 * @param certified
	 * @return
	 */
	private static OMElement createAssignedAviElement(OMElement TAdata,
			String name, String id, String certified, String namespace) {

		OMElementImpl assignedAvi = new OMElementImpl(new QName(namespace,
				"assignedAvionics"), TAdata, TAdata.getOMFactory());

		OMElementImpl aviName = new OMElementImpl(new QName(namespace,
				"assignedAvionicName"), assignedAvi, TAdata.getOMFactory());

		aviName.setText(name);

		OMElementImpl aviID = new OMElementImpl(new QName(namespace,
				"assignedAvionicID"), assignedAvi, TAdata.getOMFactory());

		aviID.setText(id);

		OMElementImpl aviCertified = new OMElementImpl(new QName(namespace,
				"assignedAvionicCert"), assignedAvi, TAdata.getOMFactory());

		aviCertified.setText(certified);

		assignedAvi.addChild(aviName);
		assignedAvi.addChild(aviID);
		assignedAvi.addChild(aviCertified);

		return assignedAvi;
	}

	/**
	 * 
	 * @param TAdata
	 * @param name
	 * @param id
	 * @param certified
	 * @return
	 */
	private static OMElement createAssignedCoordElement(OMElement TAdata,
			String name, String id, String certified, String namespace) {

		OMElementImpl assignedCoord = new OMElementImpl(new QName(namespace,
				"assignedCoord"), TAdata, TAdata.getOMFactory());

		OMElementImpl coordName = new OMElementImpl(new QName(namespace,
				"assignedCoordName"), assignedCoord, TAdata.getOMFactory());

		coordName.setText(name);

		OMElementImpl coordID = new OMElementImpl(new QName(namespace,
				"assignedCoordID"), assignedCoord, TAdata.getOMFactory());

		coordID.setText(id);

		OMElementImpl coordCertified = new OMElementImpl(new QName(namespace,
				"assignedCoordCert"), assignedCoord, TAdata.getOMFactory());

		coordCertified.setText(certified);

		assignedCoord.addChild(coordName);
		assignedCoord.addChild(coordID);
		assignedCoord.addChild(coordCertified);

		return assignedCoord;
	}

	/**
	 * 
	 * @param list
	 * @return
	 */
	private static int NBcertifiedMechanicIndex(ArrayList<Mechanic> list) {

		int result = -1;

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getCertified().equals("NB")) {
				result = i;
				break;
			}
		}
		return result;
	}

	/**
	 * 
	 * @param list
	 * @param string
	 * @return
	 */
	private static int contains(RTR[] list, String string) {

		int position = -1;
		for (RTR rtr : list) {
			position++;
			if (rtr.getRTRid().equals(string)) {
				return position;
			}
		}
		return -1;
	}

	public static OMElement toOM(XmlObject xmlObj) {
		XMLStreamReader stremR = xmlObj.newXMLStreamReader();
		StAXOMBuilder builder = new StAXOMBuilder(OMAbstractFactory
				.getOMFactory(), stremR);

		return builder.getDocumentElement();
	}

	public static Calendar removeTimezone(Calendar calendar) {
		// /The +1 in the month is because January in the Date object is month 0
		// and in the GDate object is 1.
		XmlDateTimeImpl date = new XmlDateTimeImpl();
		date.set(new GDate(calendar.get(Calendar.YEAR), calendar
				.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.HOUR_OF_DAY), calendar
						.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
				BigDecimal.ZERO));

		return date.getCalendarValue();

	}
}
