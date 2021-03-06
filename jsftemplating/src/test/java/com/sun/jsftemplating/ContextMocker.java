package com.sun.jsftemplating;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;

/**
 * Provide a dummy FacesContext for unit tests to pass.
 * 
 * @author Romain Grecourt
 */
public class ContextMocker extends FacesContext {
  public ExternalContext _extCtx = new ExternalContextMocker();
  public UIViewRoot _viewRoot = new UIViewRoot();
  
  public ContextMocker() {
  }

  static ContextMocker _ctx = new ContextMocker();
  public static void init(){
    setCurrentInstance(_ctx);
  }

  @Override
  public ExternalContext getExternalContext() {
    return _extCtx;
  }

  @Override
  public Application getApplication() {
    throw new UnsupportedOperationException("Not supported."); 
  }

  @Override
  public Iterator<String> getClientIdsWithMessages() {
    throw new UnsupportedOperationException("Not supported."); 
  }

  @Override
  public FacesMessage.Severity getMaximumSeverity() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public Iterator<FacesMessage> getMessages() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public Iterator<FacesMessage> getMessages(String clientId) {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public RenderKit getRenderKit() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public boolean getRenderResponse() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public boolean getResponseComplete() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public ResponseStream getResponseStream() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public void setResponseStream(ResponseStream responseStream) {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public ResponseWriter getResponseWriter() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public void setResponseWriter(ResponseWriter responseWriter) {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public UIViewRoot getViewRoot() {
    return _viewRoot;
  }

  @Override
  public void setViewRoot(UIViewRoot root) {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public void addMessage(String clientId, FacesMessage message) {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public void release() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public void renderResponse() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public void responseComplete() {
    throw new UnsupportedOperationException("Not supported.");
  }
  
  public static class ExternalContextMocker extends ExternalContext {

    public ExternalContextMocker() {
    }
    
    public Map<String,Object> _appMap = new HashMap<String,Object>();
    public Map _initParamMap = new HashMap();
    public Map<String, Object> _requestMap = new HashMap<String,Object>();
    
    @Override
    public Map<String, Object> getApplicationMap() {
      return _appMap;
    }
    @Override
    public void dispatch(String path) throws IOException {
      throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String encodeActionURL(String url) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public String encodeNamespace(String name) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public String encodeResourceURL(String url) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public String getAuthType() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Object getContext() {
      return this;
    }

    @Override
    public String getInitParameter(String name) {
      return (String) _initParamMap.get(name);
    }

    @Override
    public Map getInitParameterMap() {
      return _initParamMap;
    }

    @Override
    public String getRemoteUser() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Object getRequest() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public String getRequestContextPath() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Map<String, Object> getRequestCookieMap() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Map<String, String> getRequestHeaderMap() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Map<String, String[]> getRequestHeaderValuesMap() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Locale getRequestLocale() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Iterator<Locale> getRequestLocales() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Map<String, Object> getRequestMap() {
      return _requestMap;
    }

    @Override
    public Map<String, String> getRequestParameterMap() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Iterator<String> getRequestParameterNames() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Map<String, String[]> getRequestParameterValuesMap() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public String getRequestPathInfo() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public String getRequestServletPath() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public URL getResource(String path) {
      return this.getClass().getClassLoader().getResource(path);
    }

    @Override
    public InputStream getResourceAsStream(String path) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Set<String> getResourcePaths(String path) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Object getResponse() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Object getSession(boolean create) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Map<String, Object> getSessionMap() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public Principal getUserPrincipal() {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public boolean isUserInRole(String role) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public void log(String message) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public void log(String message, Throwable exception) {
      throw new UnsupportedOperationException("Not supported."); 
    }

    @Override
    public void redirect(String url) throws IOException {
      throw new UnsupportedOperationException("Not supported."); 
    }
    
  }
}