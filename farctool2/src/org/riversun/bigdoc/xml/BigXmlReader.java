/*  bigdoc Java lib for easy to read/search from a big document
 *
 *  Copyright (c) 2006-2016 Tom Misawa, riversun.org@gmail.com
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.riversun.bigdoc.xml.BasicSAXHandler.BasicSAXHandlerException;
import org.riversun.bigdoc.xml.BasicSAXHandler.TagEventListener;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * 
 * Read Big(Giga-Bytes order) XML using SAX
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 */
public class BigXmlReader {

	public static interface TagListener {
		/**
		 * Called when tag starts
		 * 
		 * @param obj
		 *            ref of BigXmlReader,when you want to stop reading a
		 *            xml,you can call <code>obj.stop()</code>
		 * @param fullTagName
		 *            full tag name like "root.element.child"
		 * @param tagName
		 *            local tag name like "child"
		 */
		public void onTagStarted(BigXmlReader obj, String fullTagName, String tagName);

		/**
		 * Called when tag ends
		 * 
		 * @param obj
		 *            ref of BigXmlReader,when you want to stop reading a
		 *            xml,you can call <code>obj.stop()</code>
		 * @param fullTagName
		 *            full tag name like "root.element.child"
		 * @param tagName
		 *            local tag name like "child"
		 * @param value
		 *            value of this tag
		 * @param atts
		 *            attributes of this tag
		 */
		public void onTagFinished(BigXmlReader obj, String fullTagName, String tagName, String value, Attributes atts);

	}

	private final BasicSAXHandler mSaxHandler = new BasicSAXHandler();

	/**
	 * Read big XML file from specific stream
	 * 
	 * @param is
	 *            stream of xml like fileinputstream
	 * @param encoding
	 * @param listener
	 *            listener callbacks when each xml element starts and ends
	 */
	public void read(InputStream is, String encoding, final TagListener listener) {

		mSaxHandler.initialize();

		Reader reader = null;

		try {

			reader = new InputStreamReader(is, encoding);

			SAXParserFactory spf = SAXParserFactory.newInstance();

			// To correspond to a big entities
			spf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);

			XMLReader xmlReader;

			xmlReader = spf.newSAXParser().getXMLReader();

			mSaxHandler.setTagEventListener(new TagEventListener() {

				@Override
				public void onTagStarted(String fullTagName, String tagName) {
					if (listener != null) {
						listener.onTagStarted(BigXmlReader.this, fullTagName, tagName);
					}
				}

				@Override
				public void onTagFinished(String fullTagName, String tagName, String value, Attributes atts) {
					if (listener != null) {
						listener.onTagFinished(BigXmlReader.this, fullTagName, tagName, value, atts);
					}
				}
			});

			xmlReader.setContentHandler(mSaxHandler);
			InputSource iso = new InputSource(reader);

			xmlReader.parse(iso);

		} catch (BasicSAXHandlerException e) {
			// Force stopped
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Stop reading a XML
	 */
	public void stop() {
		mSaxHandler.forceStop();
	}

}
