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
package com.sun.jsftemplating.layout.descriptors.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *  <p>	A HandlerDefinition defines a "handler" that may be invoked in the
 *	process of executing an event.  A HandlerDefinition has an
 *	<strong>id</strong>, <strong>java method</strong>, <strong>input
 *	definitions</strong>, <strong>output definitions</strong>, and
 *	<strong>child handlers</strong>.</p>
 *
 *  <p>	The <strong>java method</strong> to be invoked must have the
 *	following method signature:</p>
 *
 *  <p> <BLOCKQUOTE></CODE>
 *	    public void beginDisplay(HandlerContext handlerCtx)
 *	</CODE></BLOCKQUOTE></p>
 *
 *  <p>	<code>void</code> above can return a value.  Depending on the type of
 *	event, return values may be handled differently.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class HandlerDefinition implements java.io.Serializable {

    /**
     *	Constructor
     */
    public HandlerDefinition(String id) {
	_id = id;
    }

    /**
     *	This method returns the id for this handler.
     */
    public String getId() {
	return _id;
    }

    /**
     *	For future tool support
     */
    public String getDescription() {
	return _description;
    }

    /**
     *	For future tool support
     */
    public void setDescription(String desc) {
	_description = desc;
    }

    /**
     *	<p> This method sets the event handler (method) to be invoked.  The
     *	    method should be public and accept a prameter of type
     *	    "HandlerContext"  Example:</p>
     *
     *	<p> <BLOCKQUOTE>
     *		public void beginDisplay(HandlerContext handlerCtx)
     *	    </BLOCKQUOTE></p>
     *
     *	@param cls	    The full class name containing method
     *	@param methodName   The method name of the handler within class
     */
    public void setHandlerMethod(String cls, String methodName) {
	if ((cls == null) || (methodName == null)) {
	    throw new IllegalArgumentException(
		"Class name and method name must be non-null!");
	}
	_methodClass = cls;
	_methodName = methodName;
    }

    /**
     *
     */
    public void setHandlerMethod(Method method) {
	if (method != null) {
	    _methodName = method.getName();
	    _methodClass = method.getDeclaringClass().getName();
	} else {
	    _methodName = null;
	    _methodClass = null;
	}
	_method = method;
    }

    /**
     *	<p> This method determines if the handler is static.</p>
     */
    public boolean isStatic() {
	if (_static == null) {
	    _static = Boolean.valueOf(
		    Modifier.isStatic(getHandlerMethod().getModifiers()));
	}
	return _static.booleanValue();
    }

    /**
     *
     */
    public Method getHandlerMethod() {
	if (_method != null) {
	    // return cached Method
	    return _method;
	}

	// See if we have the info to find it
	if ((_methodClass != null) && (_methodName != null)) {
	    // Find the class
	    Class clzz = null;
	    try {
		clzz = Class.forName(_methodClass);
	    } catch (ClassNotFoundException ex) {
		throw new RuntimeException("'"
			+ _methodClass + "' not found for method '"
			+ _methodName + "'!", ex);
	    }

	    // Find the method on the class
	    Method method = null;
	    try {
		method = clzz.getMethod(_methodName, EVENT_ARGS);
	    } catch (NoSuchMethodException ex) {
		throw new RuntimeException(
			"Method '" + _methodName + "' not found!", ex);
	    }

	    // Cache the _method
	    _method = method;
	}

	// Return the Method if there is one
	return _method;
    }

    /**
     *	This method adds an IODescriptor to the list of input descriptors.
     *	These descriptors define the input parameters to this handler.
     *
     *	@param desc	The input IODescriptor to add
     */
    public void addInputDef(IODescriptor desc) {
	_inputDefs.put(desc.getName(), desc);
    }

    /**
     *	This method sets the input IODescriptors for this handler.
     *
     *	@param inputDefs	The Map of IODescriptors
     */
    public void setInputDefs(Map<String, IODescriptor> inputDefs) {
	if (inputDefs == null) {
	    throw new IllegalArgumentException(
		"inputDefs cannot be null!");
	}
	_inputDefs = inputDefs;
    }

    /**
     *	This method retrieves the Map of input IODescriptors.
     *
     *	@return The Map of IODescriptors
     */
    public Map<String, IODescriptor> getInputDefs() {
	return _inputDefs;
    }

    /**
     *	This method returns the requested IODescriptor, null if not found.
     */
    public IODescriptor getInputDef(String name) {
	return _inputDefs.get(name);
    }

    /**
     *	This method adds an IODescriptor to the list of output descriptors.
     *	These descriptors define the output parameters to this handler.
     *
     *	@param desc	The IODescriptor to add
     */
    public void addOutputDef(IODescriptor desc) {
	_outputDefs.put(desc.getName(), desc);
    }

    /**
     *	This method sets the output IODescriptors for this handler.
     *
     *	@param outputDefs    The Map of output IODescriptors
     */
    public void setOutputDefs(Map<String, IODescriptor> outputDefs) {
	if (outputDefs == null) {
	    throw new IllegalArgumentException(
		"outputDefs cannot be null!");
	}
	_outputDefs = outputDefs;
    }

    /**
     *	This method retrieves the Map of output IODescriptors.
     *
     *	@return The Map of output IODescriptors
     */
    public Map<String, IODescriptor> getOutputDefs() {
	return _outputDefs;
    }

    /**
     *	This method returns the requested IODescriptor, null if not found.
     */
    public IODescriptor getOutputDef(String name) {
	return _outputDefs.get(name);
    }

    /**
     *	This method adds a Handler to the list of child handlers.  Child
     *	Handlers are executed PRIOR to this handler executing.
     *
     *	@param desc	The Handler to add
     */
    public void addChildHandler(Handler desc) {
	_childHandlers.add(desc);
    }

    /**
     *	This method sets the List of child Handlers for this HandlerDefinition.
     *
     *	@param childHandlers	The List of child Handler objects
     */
    public void setChildHandlers(List<Handler> childHandlers) {
	if (childHandlers == null) {
	    throw new IllegalArgumentException(
		"childHandlers cannot be null!");
	}
	_childHandlers = childHandlers;
    }

    /**
     *	This method retrieves the List of child Handler.
     *
     *	@return The List of child Handler for this handler.
     */
    public List<Handler> getChildHandlers() {
	return _childHandlers;
    }

    /**
     *	<p> This toString() provides detailed information about this
     *	    <code>HandlerDefinition</code>.</p>
     */
    public String toString() {
	// Print the basic info...
	Formatter printf = new Formatter();
	printf.format("%-40s  %s.%s\n", _id, _methodClass, _methodName);

	// Print the description
	if (_description != null) {
	    printf.format("%s\n", _description);
	}

	// Print the Inputs
	Iterator<IODescriptor> it = _inputDefs.values().iterator();
	while (it.hasNext()) {
	    printf.format("    INPUT>  %s\n", it.next().toString());
	}

	// Print the Outputs
	it = _outputDefs.values().iterator();
	while (it.hasNext()) {
	    printf.format("    OUTPUT> %s\n", it.next().toString());
	}

	// Print the Child Handlers (TBD...)

	// Return the result
	return printf.toString();
    }


    public static final Class[] EVENT_ARGS = new Class[] {HandlerContext.class};


    private String		_id		    = null;
    private String		_description	    = null;
    private String		_methodClass	    = null;
    private String		_methodName	    = null;
    private transient Method	_method		    = null;
    private Map<String, IODescriptor>	_inputDefs  = new HashMap<String, IODescriptor>(5);
    private Map<String, IODescriptor>	_outputDefs = new HashMap<String, IODescriptor>(5);
    private List<Handler>	_childHandlers	    = new ArrayList<Handler>(5);
    private transient Boolean	_static		     = null;

    private static final long serialVersionUID = 0xA8B7C6D5E4F30211L;
}
