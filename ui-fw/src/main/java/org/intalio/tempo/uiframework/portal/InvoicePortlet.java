package org.intalio.tempo.uiframework.portal;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.sf.log.Log;

public class InvoicePortlet extends GenericPortlet {

	private static final String viewPage = "/WEB-INF/jsp/view.jsp";
	private static final String editPage = "/WEB-INF/jsp/edit.jsp";
	private static final String session_invoice = "org.intalio.tempo.uiframework.portal";
	private static final String invoice_pref = "uifw";
	private static final String test_user = "com.intalio.tempo.user";

	static {
		Log.setFile("/home/ark/tempo.log");
	}
	protected void doView(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		if (getPortletContext().getResourceAsStream(viewPage) != null) {
			try {
				// dispatch view request to view.jsp
				PortletRequestDispatcher dispatcher = getPortletContext()
						.getRequestDispatcher(viewPage);
				dispatcher.include(request, response);
			} catch (IOException e) {
				throw new PortletException("InvoicePortlet.doView exception", e);
			}
		} else {
			throw new PortletException("view.jsp missing.");
		}

	}

	protected void doEdit(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		if (getPortletContext().getResourceAsStream(editPage) != null) {
			try {
				// dispatch edit request to edit.jsp
				PortletRequestDispatcher dispatcher = getPortletContext()
						.getRequestDispatcher(editPage);
				dispatcher.include(request, response);
			} catch (IOException e) {
				throw new PortletException("InvoicePortlet.doEdit exception", e);
			}
		} else {
			throw new PortletException("edit.jsp missing.");
		}

	}

	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		// set response content-type to value in request
		response.setContentType(request.getResponseContentType());

		// if set, get invoice from preferences, else null
		String prefInvoice = request.getPreferences().getValue(invoice_pref,
				null);

		PortletSession ps = request.getPortletSession();
		// if set, get invoice from session, else null
		String sessionInvoice = (String) ps.getAttribute(session_invoice, PortletSession.APPLICATION_SCOPE);

		if (sessionInvoice == null && prefInvoice != null) {
			// if new session and pref is set, put pref in session
			request.getPortletSession().setAttribute(session_invoice,
					prefInvoice, PortletSession.APPLICATION_SCOPE);
		}


		
		String sessionUser = (String)ps.getAttribute(test_user,PortletSession.APPLICATION_SCOPE);
		Log.trace("InvoicePortlet session user:"+sessionUser);
		if(sessionUser == null){
			Log.trace("Get attribute in session is NULL");
			ps.setAttribute(test_user, "TESTUSER", PortletSession.APPLICATION_SCOPE);
		}
		sessionUser = (String)ps.getAttribute(test_user,PortletSession.APPLICATION_SCOPE);
		Log.trace("InvoicePortlet session user2:"+sessionUser);
		
		super.render(request, response);

	}

	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {

		String invoice = request.getParameter(invoice_pref);
		if (invoice != null) {
			PortletPreferences prefs = request.getPreferences();
			prefs.setValue(invoice_pref, invoice);
			prefs.store();

			// set new preference in session
			request.getPortletSession().setAttribute(session_invoice, invoice,
					PortletSession.APPLICATION_SCOPE);
			
		}
	}

}
