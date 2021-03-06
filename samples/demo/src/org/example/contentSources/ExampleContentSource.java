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
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 */
package org.example.contentSources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import com.sun.jsftemplating.util.fileStreamer.ContentSource;
import com.sun.jsftemplating.util.fileStreamer.Context;


/**
 *  <p>	This class implements <code>ContentSource</code>.  It generates
 *	simple content to serve as a basic example for the
 *	<code>FileStreamer</code> functionality.</p>
 */
public class ExampleContentSource implements ContentSource {

    /**
     *	<p> This method returns a unique string used to identify this
     *	    <code>ContentSource</code>.  This string must be specified in
     *	    order to select the appropriate {@link ContentSource} when using
     *	    <code>FileStreamer</code>.</p>
     */
    public String getId() {
	return ID;
    }

    /**
     *  <p> This method is responsible for generating the content and returning
     *	    an <code>InputStream</code> for that content.  It is also
     *	    responsible for setting any attribute values in the
     *	    <code>Context</code>, such as <code>Context#EXTENSION</code> or
     *	    <code>Context#CONTENT_TYPE</code>.</p>
     */
    public InputStream getInputStream(Context ctx) throws IOException {
	// See if we already have it.
	InputStream in = (InputStream) ctx.getAttribute("inputStream");
	if (in == null) {
	    // Create some content...
	    in = new ByteArrayInputStream(("<b>Hello!  You requested: '"
		 + ctx.getAttribute(Context.FILE_PATH) + "'</b>").getBytes());

	    // Set the extension so it can be mapped to a MIME type
	    ctx.setAttribute(Context.CONTENT_TYPE, "text/plain");

	    // Save in case method is called multiple times
	    ctx.setAttribute("inputStream", in);
	}

	// Return the InputStream
	return in;
    }

    /**
     *	<p> This method may be used to clean up any temporary resources.  It
     *	    will be invoked after the <code>InputStream</code> has been
     *	    completely read.</p>
     */
    public void cleanUp(Context ctx) {
	InputStream is = (InputStream) ctx.getAttribute("inputStream");

	// Close the InputStream
	if (is != null) {
	    try {
		is.close();
	    } catch (Exception ex) {
		// Ignore...
	    }
	}
	ctx.removeAttribute("inputStream");
    }

    /**
     *	<p> This method is responsible for returning the last modified date of
     *	    the content, or -1 if not applicable.  This information will be
     *	    used for caching.  (See <code>ResourceContentSource</code> for a
     *	    better example.)</p>
     */
    public long getLastModified(Context context) {
	// This will enable caching on all requests
	return -1;
    }

    /**
     *	<p>This is the ID for this {@link ContentSource}.</p>
     */
    public static final String ID = "example";
}
