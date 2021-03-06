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
package com.sun.jsftemplating.layout.template;

import com.sun.jsftemplating.layout.descriptors.LayoutElement;


/**
 *  <p> This class hold environmental information needed while a parsing.  This
 *	information is specific to the nested level that is currently being
 *	processed.  This is unlike the {@link ProcessingContext}
 *	which is related to the "type" of element that is being processed.  Or
 *	said another way, the {@link ProcessingContext}
 *	specifies how / what sub-elements are to be processed based on the
 *	context; this class provides the "where" information for that
 *	processing.  Another difference is this class is stateful, the
 *	{@link ProcessingContext} has stateless methods that
 *	parse specific portions of the document.</p>
 *
 *  @see    TemplateReader
 *  @see    ProcessingContext
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class ProcessingContextEnvironment {

    /**
     *	<p> Constructor.</p>
     */
    public ProcessingContextEnvironment(TemplateReader reader, LayoutElement parent, boolean nested) {
	_reader = reader;
	_parent = parent;
	_nested = nested;
    }

    /**
     *  @return The <code>TemplateReader</code> instance.
     */
    public TemplateReader getReader() {
	return _reader;
    }

    /**
     *  @return <code>true</code> if nested in a LayoutComponent.
     */
    public boolean isNested() {
	return _nested;
    }

    /**
     *  @return The parent {@link LayoutElement}.
     */
    public LayoutElement getParent() {
	return _parent;
    }

    /**
     *  <p> This method marks the current {@link ProcessingContext} as
     *	    complete.</p>
     */
    public void setFinished(boolean finished) {
	_finished = finished;
    }

    /**
     *  <p> This method indicates if the current {@link ProcessingContext}
     *	    is still valid.</p>
     */
    public boolean isFinished() {
	return _finished;
    }

    boolean		_finished   = false;
    boolean		_special    = false;
    boolean		_nested	    = false;
    LayoutElement	_parent	    = null;
    TemplateReader	_reader	    = null;
}
