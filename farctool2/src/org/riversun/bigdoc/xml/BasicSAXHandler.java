/*  bigdoc Java lib for easy to read/search from a big document
 *
 *  Copyright (c) 2006-2009 Tom Misawa, riversun.org@gmail.com
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 *  DEALINGS IN THE SOFTWARE.
 *  
 */
package org.riversun.bigdoc.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX Handler for BIG XML
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class BasicSAXHandler extends DefaultHandler {

	public static interface TagEventListener {

		public void onTagStarted(String fullTagName, String tagName);

		public void onTagFinished(String fullTagName, String tagName, String value, Attributes atts);
	}

	@SuppressWarnings("serial")
	public static class BasicSAXHandlerException extends SAXException {
		public BasicSAXHandlerException(String message) {
			super(message);
		}
	}

	private TagEventListener mTagEventListener;
	private String mCurrentTagKey;
	private boolean mIsForceStop = false;
	private Stack<String> mTagStack = new Stack<String>();
	private Map<String, StringBuilder> mTagValueCache = new HashMap<String, StringBuilder>();
	private Map<String, Attributes> mTagAttrCache = new HashMap<String, Attributes>();

	/**
	 * Set listener for tag start/end event callback
	 * 
	 * @param listener
	 */
	public void setTagEventListener(TagEventListener listener) {
		mTagEventListener = listener;
	}

	/**
	 * Initialize internal variables
	 */
	public void initialize() {
		mIsForceStop = false;
		mTagStack.clear();
		mTagValueCache.clear();
		mTagAttrCache.clear();
		mCurrentTagKey = null;
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

		if (mIsForceStop) {
			mIsForceStop = false;
			throw new BasicSAXHandlerException("Force stopped.");
		}

		mCurrentTagKey = getTagKey(namespaceURI, localName, qName);

		if (!mTagValueCache.containsKey(mCurrentTagKey)) {
			mTagValueCache.put(mCurrentTagKey, new StringBuilder());
			mTagAttrCache.put(mCurrentTagKey, atts);
		}

		pushTag(qName);
		final String currentTag = getCurrentTag();

		if (mTagEventListener != null) {
			mTagEventListener.onTagStarted(currentTag, qName);
		}

	}

	@Override
	public void characters(char[] ch, int start, int length) {
		if (mCurrentTagKey == null) {
			return;
		}

		final StringBuilder sb = mTagValueCache.get(mCurrentTagKey);
		final String str = new String(ch, start, length);
		sb.append(str);

	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) {

		final String key = getTagKey(namespaceURI, localName, qName);
		final StringBuilder sb = mTagValueCache.get(key);
		final Attributes atts = mTagAttrCache.get(key);

		String value = null;

		if (sb != null) {
			value = sb.toString();
		}

		mTagValueCache.remove(key);
		mTagAttrCache.remove(key);
		mCurrentTagKey = null;

		final String currentTag = getCurrentTag();
		popTag();

		if (mTagEventListener != null) {
			mTagEventListener.onTagFinished(currentTag, qName, value, atts);
		}

	}

	public void forceStop() {
		mIsForceStop = true;
	}

	/**
	 * Get current scanning full tag name like "root.element.child"
	 * 
	 * @return
	 */
	private String getCurrentTag() {
		if (!mTagStack.empty()) {
			return mTagStack.peek();
		} else {
			return "";
		}
	}

	/**
	 * Make current scanning full tag name like "root.element.child"
	 * 
	 * @param qName
	 */
	private void pushTag(String qName) {
		final String currentTag = getCurrentTag();
		if ("".equals(currentTag)) {
			mTagStack.push(qName);
		} else {
			mTagStack.push(getCurrentTag() + "." + qName);
		}
	}

	/**
	 * Remove last value from current scanning full tag name.<br>
	 * Before: "root.element.child"<br>
	 * After: "root.element"<br>
	 * 
	 * @return
	 */
	private void popTag() {
		mTagStack.pop();
	}

	private String getTagKey(String namespaceURI, String localName, String qName) {
		return namespaceURI + "_" + localName + "_" + qName;
	}

}
