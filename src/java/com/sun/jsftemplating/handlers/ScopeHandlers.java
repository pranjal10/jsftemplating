/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://jsftemplating.dev.java.net/cddl1.html or
 * jsftemplating/cddl1.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at jsftemplating/cddl1.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */
/*
 *  ScopeHandlers.java
 *
 *  Created on December 2, 2004, 3:06 AM
 */
package com.sun.jsftemplating.handlers;

import com.sun.jsftemplating.annotation.Handler;
import com.sun.jsftemplating.annotation.HandlerInput;
import com.sun.jsftemplating.annotation.HandlerOutput;
import com.sun.jsftemplating.el.PageSessionResolver;
import com.sun.jsftemplating.layout.descriptors.handler.HandlerContext;
import com.sun.jsftemplating.resource.ResourceBundleManager;
import com.sun.jsftemplating.util.Util;

import java.io.Serializable;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;



/**
 *  <p>	This class contains
 *	{@link com.sun.jsftemplating.layout.descriptors.handler.Handler}
 *	methods that perform common utility-type functions.</p>
 *
 *  @author  Ken Paulsen (ken.paulsen@sun.com)
 */
public class ScopeHandlers {

    /**
     *	<p> Default Constructor.</p>
     */
    public ScopeHandlers() {
    }

    /**
     *	<p> This handler gets a request attribute.  It requires "key" as an
     *	    input value.  It returns "value" as an output value.  Note this
     *	    can also be done via #{requestScope["attributeName"]}.</p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="getAttribute",
	input={
	    @HandlerInput(name="key", type=String.class, required=true)},
	output={
	    @HandlerOutput(name="value", type=Object.class)})
    public static void getAttribute(HandlerContext context) {
	String key = (String) context.getInputValue("key");
	Object value = context.getFacesContext().getExternalContext().
	    getRequestMap().get(key);
	context.setOutputValue("value", value);
    }

    /**
     *	<p> This handler sets a request attribute.  It requires "key" and
     *	    "value" input values to be passed in.</p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="setAttribute",
	input={
	    @HandlerInput(name="key", type=String.class, required=true),
	    @HandlerInput(name="value", required=true)})
    public static void setAttribute(HandlerContext context) {
	String key = (String) context.getInputValue("key");
	Object value = context.getInputValue("value");
	context.getFacesContext().getExternalContext().
	    getRequestMap().put(key, value);
    }

    /**
     *	<p> This handler produces a String consisting of all the request
     *	    attributes.  It outputs this via the "value" output value.</p>
     */
    @Handler(id="dumpAttributes",
	output={
	    @HandlerOutput(name="value", type=String.class)
	})
    public static void dumpAttributes(HandlerContext context) {
	Map<String, Object> map =
	    context.getFacesContext().getExternalContext().getRequestMap();
	context.setOutputValue("value", formatAttributes(map));
    }

    /**
     *	<p> This handler gets a "page" session attribute.  It requires
     *	    <code>key</code> as an input value.  It returns <code>value</code>
     *	    as an output value.  You may pass in the page
     *	    (<code>UIViewRoot</code>) as an input value via the
     *	    <code>page</code> input value, if you don't the current
     *	    <code>UIViewRoot</code> will be used.</p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="getPageSessionAttribute",
	input={
	    @HandlerInput(name="key", type=String.class, required=true),
	    @HandlerInput(name="page", type=UIViewRoot.class, required=false)
	},
	output={
	    @HandlerOutput(name="value", type=Serializable.class)
	})
    public static void getPageSessionAttribute(HandlerContext context) {
	// Get the Page Session Map
	Map<String, Serializable> map =
	    PageSessionResolver.getPageSession(
		context.getFacesContext(),
		(UIViewRoot) context.getInputValue("page"));

	// Get the value to return
	Serializable value = null;
	if (map != null) {
	    value = map.get((String) context.getInputValue("key"));
	}

	// Return the value
	context.setOutputValue("value", value);
    }

    /**
     *	<p> This handler sets a page session attribute.  It requires
     *	    <code>key</code> and <code>value</code> input values to be passed
     *	    in.  <code>page</code> may optionally be passed in to specify the
     *	    page (<code>UIViewRoot</code>) that should be used -- otherwise
     *	    the current <code>UIViewRoot</code> is used.</p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="setPageSessionAttribute",
	input={
	    @HandlerInput(name="key", type=String.class, required=true),
	    @HandlerInput(name="value", type=Serializable.class, required=true),
	    @HandlerInput(name="page", type=UIViewRoot.class, required=false)
	})
    public static void setPageSessionAttribute(HandlerContext context) {
	UIViewRoot root = (UIViewRoot) context.getInputValue("page");
	FacesContext ctx = context.getFacesContext();

	// Get the Page Session Map
	Map<String, Serializable> map =
	    PageSessionResolver.getPageSession(ctx, root);
	if (map == null) {
	    map = PageSessionResolver.createPageSession(ctx, root);
	}

	// Set the page session value
	map.put((String) context.getInputValue("key"),
	    (Serializable) context.getInputValue("value"));
    }

    /**
     *	<p> This handler produces a String consisting of all the page session
     *	    attributes.  It outputs this via the <code>value</code> output
     *	    value.  This handler optionally accepts the <code>page</code> input
     *	    value (<code>UIViewRoot</code>) -- if not supplied the current
     *	    <code>UIViewRoot</code> is used.</p>
     */
    @Handler(id="dumpPageSessionAttributes",
	input={
	    @HandlerInput(name="page", type=UIViewRoot.class, required=false)
	},
	output={
	    @HandlerOutput(name="value", type=String.class)
	})
    public static void dumpPageSessionAttributes(HandlerContext context) {
	// Get the Page Session Map
	Map<String, Serializable> map = PageSessionResolver.getPageSession(
	    context.getFacesContext(),
	    (UIViewRoot) context.getInputValue("page"));

	// Return the formatted result
	context.setOutputValue("value", formatAttributes(map));
    }

    /**
     *	<p> This handler gets a session attribute.  It requires "key" as an
     *	    input value.  It returns "value" as an output value.  Note this
     *	    can also be done via #{sessionScope["attributeName"]}.</p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="getSessionAttribute",
	input={
	    @HandlerInput(name="key", type=String.class, required=true)},
	output={
	    @HandlerOutput(name="value", type=Object.class)})
    public static void getSessionAttribute(HandlerContext context) {
	String key = (String) context.getInputValue("key");
	Object value = context.getFacesContext().getExternalContext().
	    getSessionMap().get(key);
	context.setOutputValue("value", value);
    }

    /**
     *	<p> This handler sets a session attribute.  It requires "key" and
     *	    "value" input values to be passed in.</p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="setSessionAttribute",
	input={
	    @HandlerInput(name="key", type=String.class, required=true),
	    @HandlerInput(name="value", required=true)})
    public static void setSessionAttribute(HandlerContext context) {
	String key = (String) context.getInputValue("key");
	Object value = context.getInputValue("value");
	context.getFacesContext().getExternalContext().
	    getSessionMap().put(key, value);
    }

    /**
     *	<p> This handler produces a String consisting of all the request
     *	    attributes.  It outputs this via the "value" output value.</p>
     */
    @Handler(id="dumpSessionAttributes",
	output={
	    @HandlerOutput(name="value", type=String.class)
	})
    public static void dumpSessionAttributes(HandlerContext context) {
	Map<String, Object> map =
	    context.getFacesContext().getExternalContext().getSessionMap();
	context.setOutputValue("value", formatAttributes(map));
    }

    /**
     *	<p> This handler gets a application attribute.  It requires "key" as an
     *	    input value.  It returns "value" as an output value.  Note this
     *	    can also be done via #{applicationScope["attributeName"]}.</p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="getApplicationAttribute",
	input={
	    @HandlerInput(name="key", type=String.class, required=true)},
	output={
	    @HandlerOutput(name="value", type=Object.class)})
    public static void getApplicationAttribute(HandlerContext context) {
	String key = (String) context.getInputValue("key");
	Object value = context.getFacesContext().getExternalContext().
	    getApplicationMap().get(key);
	context.setOutputValue("value", value);
    }

    /**
     *	<p> This handler sets a application attribute.  It requires "key" and
     *	    "value" input values to be passed in.</p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="setApplicationAttribute",
	input={
	    @HandlerInput(name="key", type=String.class, required=true),
	    @HandlerInput(name="value", required=true)})
    public static void setApplicationAttribute(HandlerContext context) {
	String key = (String) context.getInputValue("key");
	Object value = context.getInputValue("value");
	context.getFacesContext().getExternalContext().
	    getApplicationMap().put(key, value);
    }

    /**
     *	<p> This handler produces a String consisting of all the request
     *	    attributes.  It outputs this via the "value" output value.</p>
     */
    @Handler(id="dumpApplicationAttributes",
	output={
	    @HandlerOutput(name="value", type=String.class)
	})
    public static void dumpApplicationAttributes(HandlerContext context) {
	Map<String, Object> map =
	    context.getFacesContext().getExternalContext().getApplicationMap();
	context.setOutputValue("value", formatAttributes(map));
    }

    /**
     *	<p> This method formats attributes from a <code>Map</code>.  This is
     *	    used with the dump handlers.</p>
     */
    private static <T> String formatAttributes(Map<String, T> map) {
	Formatter printf = new Formatter();
	if (map == null) {
	    printf.format("Map null.");
	}
	Iterator<Map.Entry<String, T>> it = map.entrySet().iterator();
	Map.Entry<String, T> entry = null;
	while (it.hasNext()) {
	    entry = it.next();
	    printf.format("%-20s = %s\n", entry.getKey(), entry.getValue());
	}
	return printf.toString();
    }

    /**
     *	<p> This handler sets a <code>ResourceBundle</code> in desired request
     *	    attribute.  It requires "key" as an input value, which is the
     *	    request attribute key used to store the bundle.  "bundle" is also
     *	    required as an input value, this is the fully qualified name of the
     *	    bundle.  Optionally the "locale" can be specified, if not the web
     *	    user's locale will be used.  It returns "bundle" as an output
     *	    value.</p>
     *
     *	@param	context	The {@link HandlerContext}.
     */
    @Handler(id="setResourceBundle",
	input={
	    @HandlerInput(name="key", type=String.class, required=true),
	    @HandlerInput(name="bundle", type=String.class, required=true),
	    @HandlerInput(name="locale", type=Locale.class, required=false)},
	output={
	    @HandlerOutput(name="bundle", type=ResourceBundle.class)})
    public static void setResourceBundle(HandlerContext context) {
	// Get the input
	String key = (String) context.getInputValue("key");
	String name = (String) context.getInputValue("bundle");
	Locale locale = (Locale) context.getInputValue("locale");
	if (locale == null) {
	    locale = Util.getLocale(FacesContext.getCurrentInstance());
	}

	// Get the ResourceBundle
	ResourceBundle bundle =
	    ResourceBundleManager.getInstance().getBundle(name, locale);

	// Store it in the Request Map
	context.getFacesContext().getExternalContext().
	    getRequestMap().put(key, bundle);

	// Return it
	context.setOutputValue("bundle", bundle);
    }
}
