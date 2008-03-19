package com.intalio.tempo.pluto;

import java.io.IOException;

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
import org.intalio.tempo.security.token.TokenService;

import edu.yale.its.tp.cas.client.CASReceipt;
import edu.yale.its.tp.cas.client.filter.CASFilter;
import edu.yale.its.tp.cas.proxy.ProxyTicketReceptor;

public class TempoPortletEnvironmentService implements PortletEnvironmentService {
  String _endpoint;
  
  public InternalActionRequest createActionRequest(PortletContainer container, HttpServletRequest request, HttpServletResponse response,
      InternalPortletWindow internalPortletWindow) {
    return new ActionRequestImpl(container, internalPortletWindow, request);
  }

  public InternalActionResponse createActionResponse(PortletContainer container, HttpServletRequest request, HttpServletResponse response,
      InternalPortletWindow internalPortletWindow) {
    return new ActionResponseImpl(container, internalPortletWindow, request, response);
  }

  public InternalRenderRequest createRenderRequest(PortletContainer container, HttpServletRequest request, HttpServletResponse response,
      InternalPortletWindow internalPortletWindow) {
    String uname = getCASUser(request);
    RenderRequestImpl rri = new RenderRequestImpl(container, internalPortletWindow, request);
    if (rri.getAttribute(TempoPlutoConstants.TEMPO_PLUTO_USER) == null) {
      rri.setAttribute(TempoPlutoConstants.TEMPO_PLUTO_USER, uname);
    }
    
    /*Get proxy ticket*/
    String pgtIou = null;
    CASReceipt CASreceipt = (CASReceipt) request.getSession().getAttribute(CASFilter.CAS_FILTER_RECEIPT);
    if (CASreceipt != null) {
      pgtIou = CASreceipt.getPgtIou();
    }

    String proxyTicket = null;
    if (pgtIou != null) {
      try {
        proxyTicket = ProxyTicketReceptor.getProxyTicket(pgtIou, _endpoint);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
    }
    if (rri.getAttribute(TokenService.CAS_PROXY_TICKET) == null) {
      rri.setAttribute(TokenService.CAS_PROXY_TICKET, proxyTicket);
    }
    return rri;
  }

  public InternalRenderResponse createRenderResponse(PortletContainer container, HttpServletRequest request, HttpServletResponse response,
      InternalPortletWindow internalPortletWindow) {
    return new RenderResponseImpl(container, internalPortletWindow, request, response);
  }

  private String getCASUser(HttpServletRequest request) {
    String uName = (String) request.getSession().getAttribute("edu.yale.its.tp.cas.client.filter.user");
    return uName;

  }

  public void set_endpoint(String _endpoint) {
    this._endpoint = _endpoint;
  }
}
