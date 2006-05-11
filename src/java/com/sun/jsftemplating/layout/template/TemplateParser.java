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

import com.sun.jsftemplating.util.IncludeInputStream;
import com.sun.jsftemplating.util.LogUtil;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;


/**
 *  <p>	This class is responsible for the actual parsing of a template.</p>
 *
 *  <p>	This class is intended to read the template one time.  Often it may be
 *	useful to cache the result as it would be inefficient to reread a
 *	template multiple times.  Templates that are generated from this class
 *	are intended to be static and safe to share.  However, this class
 *	itself is not thread safe.</p>
 *
 *  @author Ken Paulsen	(ken.paulsen@sun.com)
 */
public class TemplateParser {

    /**
     *	<p> Constructor.</p>
     *
     *	@param	url		<code>URL</code> pointing to the template.
     */
    public TemplateParser(URL url) {
	_url = url;
    }

    /**
     *	<p> Accessor for the URL.</p>
     */
    public URL getURL() {
	return _url;
    }

    /**
     *	<p> The init method opens the given <code>URL</code> pointing to a
     *	    template and prepares to parses it.</p>
     *
     *	@throws	IOException
     */
    public void open() throws IOException {
	if (_reader != null) {
	    // Generally this should not happen, but just in case... start over
	    close();
	}

	// Open the URL
	_reader = new BufferedReader(
		new InputStreamReader(
		new IncludeInputStream(
		new BufferedInputStream(getURL().openStream()))));

	// Initialize the queue we will use to push values back
	_queue = new LinkedList<Character>();
    }

    /**
     *	<p> This method closes the stream if it is open.  It doesn't throw an
     *	    exception, instead it logs any exceptions at the CONFIG level.</p>
     */
    public void close() {
	try {
	    if (_reader != null) {
		_reader.close();
	    }
	} catch (Exception ex) {
	    if (LogUtil.configEnabled(this)) {
		LogUtil.config("Exception while closing stream for url: '"
		    + getURL() + "'.", ex);
	    }
	}
    }

    /**
     *	<p> This method returns the next character.</p>
     */
    public int nextChar() throws IOException {
	if (_queue.peek() != null) {
	    // We have values in the queue
	    return _queue.poll().charValue();
	}
	return _reader.read();
    }

    /**
     *	<p> This method pushes a character on the read queue so that it will
     *	    be read next.</p>
     */
    public void unread(int ch) {
	_queue.add(new Character((char) ch));
    }

    /**
     *	<p> This method reads a "Name Value Pair" from the stream.  For the
     *	    purposes of this method, a "Name Value Pair" may look like like one
     *	    of these formats:</p>
     *
     *	<code>
     *	<ul><li>keyName="keyValue"</li>
     *	    <li>keyName='keyValue'</li>
     *	    <li>keyName=&gt;$attribute{attributeKey}</li>
     *	    <li>keyName=&gt;$session{sessionKey}</li></ul>
     *	</code>
     *
     *	<p> In the first two formats, <code>keyName</code> must consist of
     *	    letters, numbers, or the underscore '_' character.
     *	    <code>keyValue</code> must be wrapped in single or double quotes.
     *	    The backslash '\' character may be used to escape characters, this
     *	    may be useful if a backslash, single, or double quote exists in
     *	    the string.</p>
     *
     *	<p> The last two formats are only used for mapping return values.
     *	    This is necessary when a handler returns a value so that the value
     *	    can be stored somewhere.  <code>keyName</code> in these cases is
     *	    the name of the return value to map.  The value after the dollar
     *	    '$' character (which is either attribute or session) specifies
     *	    the type of storage the value should be saved.  The value inside
     *	    the curly braces "{}" specifies the key that should be used when
     *	    saving the value as a request attribute or session attribute.</p>
     *
     *	<p> The return value is of type {@link NameValuePair}.  This object
     *	    contains the necessary information to interpret this NVP.</p>
     */
    public NameValuePair getNVP() throws IOException {

	// Read the name
	String name = readToken();

	// Skip White Space
	skipCommentsAndWhiteSpace(SIMPLE_WHITE_SPACE);

	// Ensure next character is '='
	int next = nextChar();
	if (next != '=') {
// FIXME: Improve error messages by providing some context
// FIXME: Define own exceptions
// FIXME: Localize exceptions
	    throw new IllegalArgumentException(
		"'=' missing for Name Value Pair named: '" + name + "'!");
	}

	// Check for '>' character (means we're mapping an output value)
	String target = null;
	int endingChar = -1;
	next = nextChar();
	if (next == '>') {
	    // We are mapping an output value, this should look like:
	    //	    keyName => $attribute{attKey}
	    //	    keyName => $session{sessionKey}

	    // First skip any whitespace after the '>'
	    skipCommentsAndWhiteSpace(SIMPLE_WHITE_SPACE);

	    // Next Make sure we have a '$' character
	    next = nextChar();
	    if (next != '$') {
		throw new IllegalArgumentException(
		    "'$' missing for Name Value Pair named: '" + name
		    + "=>'!  This NVP appears to be a mapping expression, and "
		    + "therefor requires one of these formats:\n\t" + name
		    + " => $attribute{attKey}\nor:\n\t" + name
		    + " => $session{sessionKey}");
	    }

	    // Next look for "attribute" or "session"
	    target = readToken();
	    if (!target.equals("attribute") && !target.equals("session")) {
		throw new IllegalArgumentException(
		    "'attribute' or 'session' type is missing for Name Value "
		    + "Pair named: '" + name + "=>$'!  This NVP appears to "
		    + "be a mapping expression, and therefor requires one of "
		    + "these formats:\n\t" + name
		    + " => $attribute{attKey}\nor:\n\t" + name
		    + " => $session{sessionKey}");
	    }

	    // Skip whitespace again...
	    skipCommentsAndWhiteSpace(SIMPLE_WHITE_SPACE);

	    // Now look for '{'
	    next = nextChar();
	    if (next != '{') {
		throw new IllegalArgumentException(
		    "'{' missing for Name Value Pair: '" + name
		    + "=>$" + target
		    + "'!  The format must resemble the following:\n\t"
		    + name + " => $" + target + "{key}");
	    }
	    endingChar = '}';
	} else {
	    // We read past the '=', backup before we skip whitespace
	    unread(next);

	    // Regular NVP, skip whitespace...
	    skipCommentsAndWhiteSpace(SIMPLE_WHITE_SPACE);

	    // Now look for starting quote...
	    next = nextChar();
	    if ((next != '"') && (next != '\'')) {
		throw new IllegalArgumentException("Name Value Pair named '"
		    + name + "' is missing single or double quotes enclosing "
		    + "its value.  It must follow one of these formats:\n\t"
		    + name + "=\"value\"\nor:\n\t" + name + "='value'");
	    }

	    // Set the ending character to the same type of quote
	    endingChar = next;
	}

	// Read the value
	String value = readUntil(endingChar);

	// Create the NVP object and return it
	return new NameValuePair(name, value, target);
    }

    /**
     *	<p> This method reads while the stream contains letters, numbers, or
     *	    the underscore '_' character, and returns the result.</p>
     */
    public String readToken() throws IOException {
	StringBuffer buf = new StringBuffer();
	int next = nextChar();
	while (Character.isLetterOrDigit(next) || (next == '_')) {
	    buf.append((char) next);
	    next = nextChar();
	}
	unread(next);

	// Return the result
	return buf.toString();
    }

    /**
     *	<p> This method returns a <code>String</code> of characters from the
     *	    current position in the file until the given character (or end of
     *	    file) is encountered.  It will not leave the given character in the
     *	    buffer, so the next character to be read will be the character
     *	    following the given character.</p>
     */
    public String readUntil(int endingChar) throws IOException {
	StringBuffer buf = new StringBuffer();
	int next = nextChar();
	while ((next != endingChar) && (next != -1)) {
	    buf.append((char) next);
	    next = nextChar();
	    if (next == '\\') {
		// Escape Character...
		next = nextChar();
		if ((next == '\n') || (next == '\r')) {
		    // Special case... skip the return
		    next = nextChar();
		    if ((next == '\n') || (next == '\r')) {
			// Do it one more time for windoze
			next = nextChar();
		    }
		} else if (next == endingChar) {
		    // We need to add this char so the loop doesn't stop
		    buf.append((char) next);
		    next = nextChar();
		}
	    }
	}

	// Return the result
	return buf.toString();
    }

    /**
     *	<p> This method returns a <code>String</code> of characters from the
     *	    current position in the file until the given String (or end of
     *	    file) is encountered.  It will not leave the given String in the
     *	    buffer, so the next character to be read will be the character
     *	    following the given character.</p>
     *
     * @param	endingStr   The terminating <code>String</code>.
     */
    public String readUntil(String endingStr) throws IOException {
	// Sanity Check
	if ((endingStr == null) || (endingStr.length() == 0)) {
	    return "";
	}

	// Break String into characters
	char arr[] = endingStr.toCharArray();

	StringBuffer buf = new StringBuffer("");
	int ch = nextChar();  // Read a char to unread
	int idx = 1;
	do {
	    // We didn't find the end, push read values back on queue
	    for (int cnt = 1; cnt < idx; cnt++) {
		unread(arr[cnt]);
	    }
	    unread(ch);

	    // Read until the beginning of the end (maybe)
	    buf.append(readUntil(arr[0]));
	    buf.append(arr[0]); // readUntil reads but doesn't return this char

	    // Check to see if we are at the end
	    for (idx = 1; idx < arr.length; idx++) {
		ch = nextChar();
		if (ch != arr[idx]) {
		    // This is not the end!
		    break;
		}
	    }
	} while ((ch != -1) && (idx < arr.length));

	// Append the remaining characters (use idx in case we hit eof)...
	for (int cnt = 1; cnt < idx; cnt++) {
	    buf.append(arr[cnt]);
	}

	// Return the result
	return buf.toString();
    }

    /**
     *	<p> This method skips the given String of characters (usually used to
     *	    skip white space.  The contents of the String that is skipped is
     *	    lost.  Often you may wish to skip comments as well, use
     *	    {@link TemplateParser#skipCommentsAndWhiteSpace(String)} in this
     *	    case.</p>
     *
     *	@param	skipChars   The white space characters to skip.
     *
     *	@see	TemplateParser#skipCommentsAndWhiteSpace(String)
     */
    public void skipWhiteSpace(String skipChars) throws IOException {
	int next = nextChar();
	while ((next != -1) && (skipChars.indexOf(next) != -1)) {
	    // Skip...
	    next = nextChar();
	}

	// This will skip one too many
	unread(next);
    }

    /**
     *	<p> Normally you don't just want to skip white space, you also want to
     *	    skip comments.  This method allows you to do that.  It skips
     *	    comments of the following types:</p>
     *
     *	<code>
     *	    <ul><li>//	    -   Comment extends to the rest of the line.</li>
     *		<li>#	    -   Comment extends to the rest of the line.</li>
     *		<li>/*	    -   Comment extends until closing '*' and '/'.</li>
     *		<li>&lt;!-- -   Comment extends until closing --&gt;.</li></ul>
     *	</code>
     *
     *	@param	skipChars   The white space characters to skip; passed to
     *			    {@link TemplateParser#skipWhiteSpace(String)}.
     *
     *	@see	TemplateParser#skipWhiteSpace(String)
     */
    public void skipCommentsAndWhiteSpace(String skipChars) throws IOException {
	int ch = 0;
	while (ch != -1) {
	    ch = nextChar();
	    switch (ch) {
		case '#' :
		    // Skip rest of line
		    readLine();
		    break;
		case '/' :
		    ch = nextChar();
		    if (ch == '/') {
			// Skip rest of line
			readLine();
		    } else if (ch == '*') {
			// Throw away everything until '*' & '/'.
			readUntil("*/");
		    } else {
			// Not a comment, don't read
			unread('/');
			unread(ch);
			ch = -1; // Exit loop
		    }
		    break;
		case '<' :
		    ch = nextChar();  // !
		    if (ch == '!') {
			ch = nextChar();  // -
			if (ch == '-') {
			    ch = nextChar();  // -
			    if (ch == '-') {
				// Ignore HTML-style comment
				readUntil("-->");
			    } else {
				// Not a comment, probably a mistake... lets
				// throw an exception
				unread('<');
				unread('!');
				unread('-');
				unread(ch);
				throw new IllegalArgumentException("Invalid "
				    + "comment!  Expected comment to begin "
				    + "with \"<!--\", but found: "
				    + readLine());
			    }
			} else {
			    // Not a comment, probably a mistake... lets
			    // throw an exception
			    unread('<');
			    unread('!');
			    unread(ch);
			    throw new IllegalArgumentException("Invalid "
				+ "comment!  Expected comment to begin "
				+ "with \"<!--\", but found: "
				+ readLine());
			}
		    } else {
			// '!' not found, not a comment... we shouldn't be
			// skipping this back out
			unread('<');
			unread(ch);
			ch = -1;  // Cause loop to end
		    }
		    break;
		case -1:    // Ignore this case
		    break;
		default:
		    // See if this is white space...
		    if (skipChars.indexOf(ch) == -1) {
			// Nope... we're done skipping (undo last read)
			unread(ch);
			ch = -1;  // Exit loop
		    }
		    break;
	    }
	}
    }

    /**
     *	<p> This method reads the rest of the line.  This can be used to read
     *	    entire lines (obviously), or as a means of skipping the remainder
     *	    of a line (i.e. to ignore line comments).</p>
     */
    public String readLine() throws IOException {
	StringBuffer buf = new StringBuffer();
	int ch = -1;
	while (_queue.peek() != null) {
	    // We have values in the queue
	    ch = _queue.getFirst().charValue();
	    if ((ch == '\r') || (ch == '\n')) {
		// We hit the EOL...
		// Check to see if there are 2...
		if (_queue.peek() != null) {
		    ch = _queue.peek().charValue();
		    if ((ch == '\r') || (ch == '\n')) {
			// Remove this one too...
			_queue.getFirst().charValue();
		    }
		}
		return buf.toString();
	    }
	    buf.append((char) ch);
	}

	// Normal case...
	return buf.toString() + _reader.readLine();
    }


    //////////////////////////////////////////////////////////////////////
    //	Constants
    //////////////////////////////////////////////////////////////////////

    /**
     *	<p> This String constant defines the characters that are interpretted
     *	    to be basic white space characters.  The value of this is:</p>
     *
     *	<p><ul><li><code>" \t\r\n"</code></li></ul></p>
     */
    public static final String	SIMPLE_WHITE_SPACE	= " \t\r\n";

    private URL				    _url	= null;
    private transient BufferedReader	    _reader	= null;
    private transient LinkedList<Character> _queue	= null;
}
