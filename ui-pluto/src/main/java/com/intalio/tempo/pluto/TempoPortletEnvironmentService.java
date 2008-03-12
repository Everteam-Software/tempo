package com.intalio.tempo.pluto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pluto.PortletContainer;
import org.apache.pluto.internal.InternalActionRequest;
import org.apache.pluto.internal.InternalActionResponse;
import org.apache.pluto.internal.InternalPortletWindow;
import org.apache.pluto.internal.InternalRenderRequest;
import org.apache.pluto.internal.InternalRenderResponse;
import org.apache.pluto.internal.impl.ActionRequestImpl;
import org.apache.pluto.internal.impl.ActionResponseImpl;
import org.apache.pluto.internal.impl.RenderRequestImpl;
import org.apache.pluto.internal.impl.RenderResponseImpl;
import org.apache.pluto.spi.optional.PortletEnvironmentService;

public class TempoPortletEnvironmentService implements
		PortletEnvironmentService {
	public InternalActionRequest createActionRequest(
			PortletContainer container, HttpServletRequest request,
			HttpServletResponse response,
			InternalPortletWindow internalPortletWindow) {
		return new ActionRequestImpl(container, internalPortletWindow, request);
	}

	public InternalActionResponse createActionResponse(
			PortletContainer container, HttpServletRequest request,
			HttpServletResponse response,
			InternalPortletWindow internalPortletWindow) {
		return new ActionResponseImpl(container, internalPortletWindow,
				request, response);
	}

	public InternalRenderRequest createRenderRequest(
			PortletContainer container, HttpServletRequest request,
			HttpServletResponse response,
			InternalPortletWindow internalPortletWindow) {
		String uname = getCASUser(request);
		RenderRequestImpl rri = new RenderRequestImpl(container, internalPortletWindow, request);
		if (rri.getAttribute(TempoPlutoConstants.TEMPO_PLUTO_USER) == null){
			rri.setAttribute(TempoPlutoConstants.TEMPO_PLUTO_USER,uname);
		}
		return rri;
	}

	public InternalRenderResponse createRenderResponse(
			PortletContainer container, HttpServletRequest request,
			HttpServletResponse response,
			InternalPortletWindow internalPortletWindow) {
		return new RenderResponseImpl(container, internalPortletWindow,
				request, response);
	}

	private String getCASUser(HttpServletRequest request){
		String uName = (String)request.getSession().getAttribute("edu.yale.its.tp.cas.client.filter.user");
		return uName;
		
	}
}
