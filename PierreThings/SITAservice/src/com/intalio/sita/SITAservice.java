package com.intalio.sita;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.axis2.AxisFault;

import java.io.FileInputStream;

public class SITAservice {

	private static final String TAMANAGEMENT_URI = "http://www.intalio.com/gi/forms/TAmanagement.gi";
	private static final String HHT_URI = "http://www.example.org/hht";

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

		StAXOMBuilder builder = new StAXOMBuilder(
				requestElement.getOMFactory(), requestElement
						.getXMLStreamReader());

		OMElement root = builder.getDocumentElement();

		try {

			String currentUser = root.getFirstChildWithName(new QName("user"))
					.getText();

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
							+ currentUser.substring(
									currentUser.indexOf('\\') + 1, currentUser
											.length()) + "'");

			String currentShift;

			if (s.next()) {
				currentShift = s.getString("shift");
			} else {
				currentShift = "A";
				System.out.println("WARNING: " + currentUser
						+ " is not a registered user of the RMA. Using shift "
						+ currentShift + " for prepopulation by default");
			}

			s.close();

			// get shift mechanics
			ResultSet m = statement
					.executeQuery("SELECT name, identifier, certified FROM rma_mechanic WHERE shift='"
							+ currentShift + "' AND NOT certified='AUX'");

			ArrayList<Mechanic> mechanics = new ArrayList<Mechanic>();

			while (m.next()) {
				mechanics.add(new Mechanic(m.getString("name"), m
						.getString("identifier"), m.getString("certified")));
			}

			m.close();

			// get shift avionics
			ResultSet a = statement
					.executeQuery("SELECT name, identifier, certified FROM rma_avionic WHERE shift='"
							+ currentShift + "'");

			ArrayList<Mechanic> avionics = new ArrayList<Mechanic>();

			while (a.next()) {
				avionics.add(new Mechanic(a.getString("name"), a
						.getString("identifier"), a.getString("certified")));
			}

			a.close();

			// get shift coordinators
			ResultSet c = statement
					.executeQuery("SELECT name, identifier, certified FROM rma_coordinator WHERE shift='"
							+ currentShift + "'");

			ArrayList<Mechanic> coordinators = new ArrayList<Mechanic>();

			while (c.next()) {
				coordinators.add(new Mechanic(c.getString("name"), c
						.getString("identifier"), c.getString("certified")));
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

			// sort them by from earliest to latest
			fusion(0, ShiftTAs.size() - 1, ShiftTAs);

			int mechCursor = 0;
			int aviCursor = 0;
			int coordCursor = 0;

			ArrayList<Mechanic> skippedMechs = new ArrayList<Mechanic>();

			for (int i = 0; i < ShiftTAs.size(); i++) {

				try {
					OMElement currentTAdata = ShiftTAs.get(i).getFormModel();

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
									.getName(), coordinators.get(coordCursor)
									.getId(), coordinators.get(coordCursor)
									.getCertified(), namespace);

					currentTAdata.getFirstChildWithName(
							new QName(namespace, "Inspection")).addChild(
							coordElement);

					// increment cursor
					coordCursor++;
					if (coordCursor == coordinators.size())
						coordCursor = 0;

					// Now we need to assign mechanics

					// The first thing to do is check if the aircraft is a
					// narrow-body and we have an NB-certified mechanic in the
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
										.getName(), skippedMechs.get(cursor)
										.getId(), skippedMechs.get(cursor)
										.getCertified(), 0, namespace);

						currentTAdata.getFirstChildWithName(
								new QName(namespace, "Inspection")).addChild(
								mechElement);

						// remove the element at index "cursor" from the skipped
						// list
						skippedMechs.remove(cursor);

					} else {

						boolean firstMechDetermined = false;

						while (!firstMechDetermined) {

							// If the current mechanic is BC-certified OR
							// [current
							// mechanic is NB-certified AND aircraft is
							// narrow-body], we can assign him without checking
							// anything

							// Note that a BC-certified mechanic will never be
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
										currentTAdata, mechanics
												.get(mechCursor).getName(),
										mechanics.get(mechCursor).getId(),
										mechanics.get(mechCursor)
												.getCertified(), 1, namespace);

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
								// Otherwise, we skip the current mechanic and
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

					// Now we need to determine the second mechanic. The first
					// thing
					// to do is check the AircraftID. If it is a wide-body, we
					// need
					// a BC-certified mechanic. Otherwise, we look at the
					// skipped
					// list and take the first one there. If the skipped list is
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

							// remove the first element from the skipped list
							skippedMechs.remove(0);

						} else {

							// create assignedMechanicElement
							OMElement mechElement = createAssignedMechElement(
									currentTAdata, mechanics.get(mechCursor)
											.getName(), mechanics.get(
											mechCursor).getId(), mechanics.get(
											mechCursor).getCertified(), 0,
									namespace);

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
										currentTAdata, mechanics
												.get(mechCursor).getName(),
										mechanics.get(mechCursor).getId(),
										mechanics.get(mechCursor)
												.getCertified(), 1, namespace);

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
								// Otherwise, we skip the current mechanic and
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
							currentTAdata, avionics.get(aviCursor).getName(),
							avionics.get(aviCursor).getId(), avionics.get(
									aviCursor).getCertified(), namespace);

					currentTAdata.getFirstChildWithName(
							new QName(namespace, "Inspection")).addChild(
							aviElement);

					// increment cursor
					aviCursor++;
					if (aviCursor == avionics.size())
						aviCursor = 0;

					// This TA is now assigned for the current shift, let's mark
					// it
					// as such
					currentTAdata.getFirstChildWithName(
							new QName(namespace, "Inspection"))
							.getFirstChildWithName(
									new QName(namespace, "assigned")).setText(
									currentShift);

					// All that's left is update the TA in the DB

					statement.executeUpdate("UPDATE tempo_pa SET output_xml='"
							+ currentTAdata.toString() + "' WHERE id="
							+ ShiftTAs.get(i).getId());
				} catch (Exception e) {
					// If something goes wrong for some strage odd reason, let's
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

	public static OMElement updateTA(OMElement requestElement) throws AxisFault {

		OMElement responseElement = requestElement
				.getFirstChildWithName(new QName(TAMANAGEMENT_URI, "data"));

		OMElement ADelement = responseElement.getFirstChildWithName(
				new QName(TAMANAGEMENT_URI, "FormModel"))
				.getFirstChildWithName(
						new QName(TAMANAGEMENT_URI, "ArrivalDeparture"));

		OMElement FMRelement = requestElement.getFirstChildWithName(new QName(
				"FMR"));

		Iterator<OMAttribute> attIterator = FMRelement.getAllAttributes();

		boolean update = false;

		while (attIterator.hasNext()) {

			OMAttribute current = attIterator.next();

			if (current.getLocalName().equals("Aircraft")) {

				OMElement element = responseElement.getFirstChildWithName(
						new QName(TAMANAGEMENT_URI, "FormModel"))
						.getFirstChildWithName(
								new QName(TAMANAGEMENT_URI, "Activity"))
						.getFirstChildWithName(
								new QName(TAMANAGEMENT_URI, "AircraftID"));

				// Take only the 3 last chars of the attribute
				String AID = current.getAttributeValue().substring(
						current.getAttributeValue().length() - 3,
						current.getAttributeValue().length());

				if (!element.getText().equals(AID)) {
					element.setText(AID);
					// TODO trash the TA data
					update = true;
				}
			} else if (current.getLocalName().equals("ATA")) {

				OMElement dateElement = ADelement
						.getFirstChildWithName(new QName(TAMANAGEMENT_URI,
								"ActualArrivalDate"));
				OMElement timeElement = ADelement
						.getFirstChildWithName(new QName(TAMANAGEMENT_URI,
								"ATA"));

				if (current.getAttributeValue().isEmpty()) {
					dateElement.setText("1970-01-01");
					timeElement.setText("");
					update = true;
				} else {
					if (!dateElement.getText().equals(
							current.getAttributeValue().substring(0, 10))
							|| !timeElement.getText().equals(
									current.getAttributeValue().substring(11,
											19))) {
						dateElement.setText(current.getAttributeValue()
								.substring(0, 10));
						timeElement.setText(current.getAttributeValue()
								.substring(11, 19));
						update = true;
					}
				}
			} else if (current.getLocalName().equals("STD")) {

				OMElement dateElement = ADelement
						.getFirstChildWithName(new QName(TAMANAGEMENT_URI,
								"ScheduledDepartureDate"));
				OMElement timeElement = ADelement
						.getFirstChildWithName(new QName(TAMANAGEMENT_URI,
								"STD"));
				if (current.getAttributeValue().isEmpty()) {
					dateElement.setText("1970-01-01");
					timeElement.setText("");
					update = true;
				} else {
					if (!dateElement.getText().equals(
							current.getAttributeValue().substring(0, 10))
							|| !timeElement.getText().equals(
									current.getAttributeValue().substring(11,
											19))) {
						dateElement.setText(current.getAttributeValue()
								.substring(0, 10));
						timeElement.setText(current.getAttributeValue()
								.substring(11, 19));
						update = true;
					}
				}
			} else if (current.getLocalName().equals("ATD")) {

				OMElement dateElement = ADelement
						.getFirstChildWithName(new QName(TAMANAGEMENT_URI,
								"ActualDepartureDate"));
				OMElement timeElement = ADelement
						.getFirstChildWithName(new QName(TAMANAGEMENT_URI,
								"ATD"));

				if (current.getAttributeValue().isEmpty()) {
					dateElement.setText("1970-01-01");
					timeElement.setText("");
					update = true;
				} else {
					if (!dateElement.getText().equals(
							current.getAttributeValue().substring(0, 10))
							|| !timeElement.getText().equals(
									current.getAttributeValue().substring(11,
											19))) {
						dateElement.setText(current.getAttributeValue()
								.substring(0, 10));
						timeElement.setText(current.getAttributeValue()
								.substring(11, 19));
						update = true;
					}
				}
			} else if (current.getLocalName().equals("Stand")) {

				OMElement element = responseElement.getFirstChildWithName(
						new QName(TAMANAGEMENT_URI, "FormModel"))
						.getFirstChildWithName(
								new QName(TAMANAGEMENT_URI, "Inspection"))
						.getFirstChildWithName(
								new QName(TAMANAGEMENT_URI, "Stand"));

				if (!element.getText().equals(current.getAttributeValue())) {
					element.setText(current.getAttributeValue());
					update = true;
				}
			} else if (current.getLocalName().equals("InspectionType")) {

				// InspectionType is a little tricky: have to take out the '+'
				String InspectionType = current.getAttributeValue().replace(
						"+", "");

				OMElement element = responseElement.getFirstChildWithName(
						new QName(TAMANAGEMENT_URI, "FormModel"))
						.getFirstChildWithName(
								new QName(TAMANAGEMENT_URI, "Inspection"))
						.getFirstChildWithName(
								new QName(TAMANAGEMENT_URI, "InspectionType"));

				if (!element.getText().equals(InspectionType)) {
					element.setText(InspectionType);
					update = true;
				}
			} else if (current.getLocalName().equals("DepartureFlightNumber")) {

				OMElement element = ADelement.getFirstChildWithName(new QName(
						TAMANAGEMENT_URI, "DepartureFlightNumber"));

				if (!element.getText().equals(current.getAttributeValue())) {
					element.setText(current.getAttributeValue());
					update = true;
				}
			} else if (current.getLocalName().equals("Rtr-id")
					&& !current.getAttributeValue().isEmpty()) {

				Iterator<OMElement> iter = responseElement
						.getFirstChildWithName(
								new QName(TAMANAGEMENT_URI, "FormModel"))
						.getFirstChildWithName(
								new QName(TAMANAGEMENT_URI, "Inspection"))
						.getChildrenWithName(new QName(TAMANAGEMENT_URI, "RTR"));

				String RTRstring = "";

				while (iter.hasNext()) {
					OMElement currentRTR = iter.next();
					RTRstring += currentRTR.getFirstChildWithName(
							new QName(TAMANAGEMENT_URI, "RTRid")).getText()
							+ ";";
				}

				if (RTRstring.endsWith(";")) {
					RTRstring = RTRstring.substring(0, RTRstring.length() - 1);
				}

				if (!RTRstring.equals(current.getAttributeValue())) {
					iter = responseElement.getFirstChildWithName(
							new QName(TAMANAGEMENT_URI, "FormModel"))
							.getFirstChildWithName(
									new QName(TAMANAGEMENT_URI, "Inspection"))
							.getChildrenWithName(
									new QName(TAMANAGEMENT_URI, "RTR"));

					ArrayList<OMElement> OldRTRlist = new ArrayList<OMElement>();

					while (iter.hasNext()) {
						OldRTRlist.add(iter.next());
						iter.remove();
					}

					StringTokenizer tok = new StringTokenizer(current
							.getAttributeValue(), ";");

					ArrayList<OMElement> NewRTRlist = new ArrayList<OMElement>();

					while (tok.hasMoreTokens()) {

						String currentID = tok.nextToken();
						int index = contains(OldRTRlist, currentID);

						if (index != -1) {
							NewRTRlist.add(OldRTRlist.get(index));
						} else {
							NewRTRlist.add(createRTR(currentID));
						}
					}

					for (int i = NewRTRlist.size() - 1; i >= 0; i--) {
						responseElement.getFirstChildWithName(
								new QName(TAMANAGEMENT_URI, "FormModel"))
								.getFirstChildWithName(
										new QName(TAMANAGEMENT_URI,
												"Inspection"))
								.getFirstChildWithName(
										new QName(TAMANAGEMENT_URI,
												"coordinator"))
								.insertSiblingAfter(NewRTRlist.get(i));
					}

					update = true;
				}
			}
		}

		if (update)
			responseElement.getFirstChildWithName(
					new QName(TAMANAGEMENT_URI, "FormModel"))
					.getFirstChildWithName(
							new QName(TAMANAGEMENT_URI, "Activity"))
					.getFirstChildWithName(
							new QName(TAMANAGEMENT_URI, "update")).setText("1");

		return responseElement;
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

	/**
	 * 
	 * @param RTRid
	 * @return
	 */
	private static OMElement createRTR(String RTRid) {

		OMFactory fac = OMAbstractFactory.getOMFactory();

		OMElement RTRelement = fac.createOMElement("RTR", null);

		ArrayList<OMElement> childList = new ArrayList<OMElement>();

		OMElement id = fac.createOMElement(new QName("RTRid"));
		id.setText(RTRid);
		childList.add(id);
		// childList.add(fac.createOMElement(new QName("RTRdate")));
		childList.add(fac.createOMElement(new QName("RTRdescription")));
		OMElement status = fac.createOMElement(new QName("RTRstatus"));
		status.setText("open");
		childList.add(status);
		OMElement ad = fac.createOMElement(new QName("RTRad"));
		ad.setText("false");
		childList.add(ad);

		for (int i = 0; i < childList.size(); i++) {
			RTRelement.addChild(childList.get(i));
		}

		return RTRelement;
	}

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

				OMElement AD = builder.getDocumentElement()
						.getFirstChildWithName(
								new QName(namespace, "ArrivalDeparture"));

				String dateTime;

				if (AD.getFirstChildWithName(
						new QName(namespace, "ActualArrivalDate")).getText()
						.startsWith("1970")) {
					dateTime = AD.getFirstChildWithName(
							new QName(namespace, "ScheduledArrivalDate"))
							.getText()
							+ " "
							+ AD.getFirstChildWithName(
									new QName(namespace, "STA")).getText();
				} else {
					dateTime = AD.getFirstChildWithName(
							new QName(namespace, "ActualArrivalDate"))
							.getText()
							+ " "
							+ AD.getFirstChildWithName(
									new QName(namespace, "ATA")).getText();
				}

				Calendar TAstartDateTime = convertToCalendar(dateTime);

				if (AD.getFirstChildWithName(
						new QName(namespace, "ActualDepartureDate")).getText()
						.startsWith("1970")) {
					dateTime = AD.getFirstChildWithName(
							new QName(namespace, "ScheduledDepartureDate"))
							.getText()
							+ " "
							+ AD.getFirstChildWithName(
									new QName(namespace, "STD")).getText();
					// If the STD wasn't populated in the TA, add time (the date
					// has to be there
					if (dateTime.length() < 19) {
						dateTime += "00:00:00";
					}
				} else {
					dateTime = AD.getFirstChildWithName(
							new QName(namespace, "ActualDepartureDate"))
							.getText()
							+ " "
							+ AD.getFirstChildWithName(
									new QName(namespace, "ATD")).getText();
				}

				Calendar TAendDateTime = convertToCalendar(dateTime);

				// Now that we have the dateTime to consider, we need to check
				// that
				// it corresponds to the shift we're working on, and that the
				// assignment hasn't been done already

				if (((TAstartDateTime.after(start) && TAstartDateTime
						.before(end))
						|| (TAendDateTime.after(start) && TAendDateTime
								.before(end)) || (TAstartDateTime.before(start) && TAendDateTime
						.after(end)))
						&& !builder.getDocumentElement().getFirstChildWithName(
								new QName(namespace, "Inspection"))
								.getFirstChildWithName(
										new QName(namespace, "assigned"))
								.getText().equals(currentShift)) {

					// scratch assigned mechanics/avionics/coordinators
					OMElement ins = builder.getDocumentElement()
							.getFirstChildWithName(
									new QName(namespace, "Inspection"));

					Iterator mechIter = ins.getChildrenWithName(new QName(
							namespace, "assignedMechanics"));

					while (mechIter.hasNext()) {
						mechIter.next();
						mechIter.remove();
					}

					Iterator aviIter = ins.getChildrenWithName(new QName(
							namespace, "assignedAvionics"));

					while (aviIter.hasNext()) {
						aviIter.next();
						aviIter.remove();
					}

					Iterator coordIter = ins.getChildrenWithName(new QName(
							namespace, "assignedCoord"));

					while (coordIter.hasNext()) {
						coordIter.next();
						coordIter.remove();
					}

					ShiftTAs.add(new TAobject(myElement, TAendDateTime, TAs
							.getInt("id")));
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
	private static int contains(ArrayList<OMElement> list, String string) {

		int result = -1;

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getFirstChildWithName(
					new QName(TAMANAGEMENT_URI, "RTRid")).getText().equals(
					string)) {
				result = i;
				break;
			}
		}
		return result;
	}
}
