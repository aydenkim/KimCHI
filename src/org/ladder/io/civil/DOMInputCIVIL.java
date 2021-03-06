/**
 * DOMInputFull.java
 * 
 * Revision History: <br>
 * (5/30/08) awolin - Finished the class, although with bugs <br>
 * (5/30/08) awolin - Fixed bugs, fixed color to input from an RGB integer value <br>
 * (6/2/08) awolin - converted the class to work with the IInput interface,
 * altered the functions to speed up load times by a small fraction <br>
 * 26 June 2008 : jbjohns : FullPoint <br>
 * Jul 31, 2008 awolin - Added throws to the parseDocument call so that any
 * errors with loading documents are thrown to the highest level
 * 
 * <p>
 * 
 * <pre>
 * This work is released under the BSD License:
 * (C) 2008 Sketch Recognition Lab, Texas A&amp;M University (hereafter SRL @ TAMU)
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sketch Recognition Lab, Texas A&amp;M University
 *       nor the names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior written
 *       permission.
 * THIS SOFTWARE IS PROVIDED BY SRL @ TAMU ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL SRL @ TAMU BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */

package org.ladder.io.civil;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ladder.core.sketch.Author;
import org.ladder.core.sketch.IPoint;
import org.ladder.core.sketch.ISegmentation;
import org.ladder.core.sketch.IShape;
import org.ladder.core.sketch.IStroke;
import org.ladder.core.sketch.Pen;
import org.ladder.core.sketch.Point;
import org.ladder.core.sketch.Segmentation;
import org.ladder.core.sketch.Shape;
import org.ladder.core.sketch.Sketch;
import org.ladder.core.sketch.Speech;
import org.ladder.core.sketch.Stroke;
import org.ladder.io.IInput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.tamu.core.sketch.Alias;

/**
 * DOMInputFull class that takes a file in the constructor and can return a
 * FullSketch object.
 * 
 * @author awolin
 * 
 */
public class DOMInputCIVIL implements IInput {

	/**
	 * Sketch object to add input data to
	 */
	private Sketch m_sketch;

	/**
	 * Map from an author's ID string to the author object
	 */
	private Map<String, Author> m_authorMap;

	/**
	 * Map from a pen's ID string to the pen object
	 */
	private Map<String, Pen> m_penMap;

	/**
	 * Map from a point ID string to the point object
	 */
	private Map<String, Point> m_pointMap;

	/**
	 * Map from a stroke's ID string to the stroke object
	 */
	private Map<String, Stroke> m_strokeMap;

	/**
	 * Map from a segmentation's ID string to the segmentation object
	 */
	private Map<String, Segmentation> m_segMap;

	/**
	 * Map from an shape's ID string to the shape object
	 */
	private Map<String, Shape> m_shapeMap;

	private Map<IStroke, String> m_parentsMap;

	/**
	 * Empty Constructor
	 */
	public DOMInputCIVIL() {
		// Do nothing
	}

	/**
	 * Takes in an XML file and parses the file into a Sketch object
	 * 
	 * @param file
	 *            Input file to parse
	 * @return Sketch created from the input file
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Sketch parseDocument(File file) throws ParserConfigurationException,
			SAXException, IOException {

		// Get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		// Using factory get an instance of document builder
		DocumentBuilder db = dbf.newDocumentBuilder();

		// Parse using builder to get DOM representation of the XML file
		Document dom = db.parse(file);

		// Read in the XML document and add the data to the sketch
		parseSketch(file, dom);

		return m_sketch;
	}

	public Sketch parseDocument(Element sketchRoot, File file) {
		parseSketch(sketchRoot, file);
		return m_sketch;
	}

	/**
	 * Parse the input document, starting with the sketch root element
	 */
	private void parseSketch(File file, Document dom) {

		// Create the root (sketch) element
		Element sketchRoot = dom.getDocumentElement();
		parseSketch(sketchRoot, file);
	}

	private void parseSketch(Element sketchRoot, File file) {
		parseSketchElement(sketchRoot);

		// Add the list of Authors to the sketch
		NodeList authorList = sketchRoot.getElementsByTagName("author");
		parseAuthorList(authorList);

		// Add the list of Pens to the sketch
		NodeList penList = sketchRoot.getElementsByTagName("pen");
		parsePenList(penList);

		// Add speech to the sketch
		NodeList speechList = sketchRoot.getElementsByTagName("speech");
		parseSpeechElement((Element) speechList.item(0), file);

		// Parse all of the sketch's point into a HashMap to use when building
		// the sketch
		NodeList pointList = sketchRoot.getElementsByTagName("point");
		parsePointList(pointList);

		// Parse the strokes
		NodeList strokeList = sketchRoot.getElementsByTagName("stroke");
		parseStrokeList(strokeList);

		// Parse the segmentations
		NodeList segList = sketchRoot.getElementsByTagName("segmentation");
		parseSegmentationList(segList);

		// Parse the shapes
		NodeList shapeList = sketchRoot.getElementsByTagName("shape");
		parseShapeList(shapeList);

		Element sketchShapes = (Element) sketchRoot.getElementsByTagName(
				"sketchElements").item(0);
		parseSketchDirectElements(sketchShapes);
	}

	/**
	 * Parse the sketch root element. Creates a new sketch member element for
	 * this class and sets the attributes from those found in the input file.
	 * 
	 * @param sketchElement
	 *            Sketch XML element to parse
	 */
	private void parseSketchElement(Element sketchElement) {

		m_sketch = new Sketch();

		// Get every attribute in the element
		NamedNodeMap attributes = sketchElement.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			String name = attributes.item(i).getNodeName();
			String value = attributes.item(i).getNodeValue();

			if (name.equals("id")) {
				m_sketch.setID(UUID.fromString(value));
			} else if (name.equals("study")) {
				m_sketch.setStudy(value);
			} else if (name.equals("domain")) {
				m_sketch.setDomain(value);
			} else if (name.equals("units")) {
				m_sketch.setUnits(Enum.valueOf(Sketch.SpaceUnits.class, value));
			} else if (name.equals("type")) {
				// Do nothing; we don't need to store the type here since we
				// check for the sketch type in DOMInput
			} else {
				m_sketch.setAttribute(name, value);
			}
		}
	}

	/**
	 * Parse the list of author elements.
	 * 
	 * @param authorList
	 *            The list of author elements in the sketch
	 */
	private void parseAuthorList(NodeList authorList) {

		m_authorMap = new HashMap<String, Author>();

		// Get the individual authors from the list
		if (authorList != null && authorList.getLength() > 0) {
			for (int i = 0; i < authorList.getLength(); i++) {
				Element authorElement = (Element) authorList.item(i);
				parseAuthorElement(authorElement);
			}
		}
	}

	/**
	 * Parse an individual author element. Adds the authors to the main sketch
	 * object as well as a global map from author IDs to author objects.
	 * 
	 * @param authorElement
	 *            An author element in the sketch
	 */
	private void parseAuthorElement(Element authorElement) {

		Author author = new Author();

		author.setID(UUID.fromString(authorElement.getAttribute("id")));
		String x = authorElement.getAttribute("dpi_x");
		if (x != null && x != "")
			author.setDpiX(Double.valueOf(x));
		String y = authorElement.getAttribute("dpi_y");
		if (y != null && y != "")
			author.setDpiY(Double.valueOf(y));
		String desc = authorElement.getAttribute("desc");
		if (desc != null && desc != "")
			author.setDescription(desc);

		// Add the author to this class's HashMap
		m_authorMap.put(author.getID().toString(), author);

		// Add the author to the sketch
		m_sketch.addAuthor(author);
	}

	/**
	 * Parse the list of pen elements.
	 * 
	 * @param penList
	 *            The list of pen elements in the sketch
	 */
	private void parsePenList(NodeList penList) {

		m_penMap = new HashMap<String, Pen>();

		// Get the individual authors from the list
		if (penList != null && penList.getLength() > 0) {
			for (int i = 0; i < penList.getLength(); i++) {
				Element penElement = (Element) penList.item(i);
				parsePenElement(penElement);
			}
		}
	}

	/**
	 * Parse an individual pen element. Adds the pens to the main sketch object
	 * as well as a global map from pen IDs to pen objects.
	 * 
	 * @param penElement
	 *            A pen element in the sketch
	 */
	private void parsePenElement(Element penElement) {

		Pen pen = new Pen();

		pen.setID(UUID.fromString(penElement.getAttribute("id")));
		pen.setPenID(penElement.getAttribute("penID"));
		pen.setBrand(penElement.getAttribute("brand"));
		pen.setDescription(penElement.getAttribute("desc"));

		// Add the pen to this class's HashMap
		m_penMap.put(pen.getID().toString(), pen);

		// Add the pen to the sketch
		m_sketch.addPen(pen);
	}

	/**
	 * Parse an individual speech element. Adds the speech element to the
	 * sketch.
	 * 
	 * @param speechElement
	 *            Speech element in the sketch
	 * @param file
	 *            File we're parsing
	 */
	private void parseSpeechElement(Element speechElement, File file) {
		if (speechElement != null) {
			Speech s = new Speech();
			s.setID(UUID.fromString(speechElement.getAttribute("id")));

			if (speechElement.getAttribute("desc") != null) {
				s.setDescription(speechElement.getAttribute("desc"));
			}
			if (speechElement.getAttribute("path") != null) {
				if (new File(speechElement.getAttribute("path")).exists()) {
					s.setPath(speechElement.getAttribute("path"));
				} else if (new File(file.getParent() + File.separator
						+ speechElement.getAttribute("path")).exists()) {
					s.setPath(file.getParent() + File.separator
							+ speechElement.getAttribute("path"));
				}
			}
			if (speechElement.getAttribute("startTime") != null) {
				s.setStartTime(Long.valueOf(
						speechElement.getAttribute("startTime")).longValue());
			}
			if (speechElement.getAttribute("stopTime") != null) {
				s.setStopTime(Long.valueOf(
						speechElement.getAttribute("stopTime")).longValue());
			}

			// Add the speech to the sketch
			m_sketch.setSpeech(s);
		}
	}

	/**
	 * Create a map containing mappings from point string IDs to point objects.
	 * The points in these maps contain all of the point information read from
	 * the input.
	 * 
	 * @param pointList
	 *            List of point elements from the input
	 */
	private void parsePointList(NodeList pointList) {

		m_pointMap = new HashMap<String, Point>();

		// Get the individual authors from the list
		if (pointList != null && pointList.getLength() > 0) {
			for (int i = 0; i < pointList.getLength(); i++) {
				Element pointElement = (Element) pointList.item(i);
				parsePointElement(pointElement);
			}
		}
	}

	/**
	 * Parse an individual point element. Adds the points to the main sketch
	 * object as well as a global map from point IDs to point objects.
	 * 
	 * @param pointElement
	 *            A point element in the sketch
	 */
	private void parsePointElement(Element pointElement) {

		double x = 0;
		double y = 0;
		long time = 0;
		double pressure = 0;
		double tiltX = 0;
		double tiltY = 0;
		UUID id = null;
		Map<String, String> ptAttributes = new HashMap<String, String>();

		// Get every attribute in the element
		NamedNodeMap attributes = pointElement.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			String name = attributes.item(i).getNodeName();
			String value = attributes.item(i).getNodeValue();

			if (name.equals("id")) {
				id = UUID.fromString(value);
			} else if (name.equals("x")) {
				x = Double.valueOf(value);
			} else if (name.equals("y")) {
				y = Double.valueOf(value);
			} else if (name.equals("time")) {
				time = Long.valueOf(value);
			} else if (name.equals("pressure")) {
				pressure = Double.valueOf(value);
			} else if (name.equals("tilt_x")) {
				tiltX = Double.valueOf(value);
			} else if (name.equals("tilt_y")) {
				tiltY = Double.valueOf(value);
			} else {
				ptAttributes.put(name, value);
			}
		}

		Point point = new Point(x, y, time);
		point.setID(id);
		point.setPressure(pressure);
		point.setTiltX(tiltX);
		point.setTiltY(tiltY);

		for (String key : ptAttributes.keySet()) {
			point.setAttribute(key, ptAttributes.get(key));
		}

		// We only add the points to the temporary the HashMap since ISketch
		// objects only store points within IStrokes
		m_pointMap.put(point.getID().toString(), point);
	}

	/**
	 * Parse the list of stroke elements.
	 * 
	 * @param strokeList
	 *            The list of stroke elements in the sketch
	 */
	private void parseStrokeList(NodeList strokeList) {

		m_strokeMap = new HashMap<String, Stroke>();
		m_parentsMap = new HashMap<IStroke, String>();

		if (strokeList != null && strokeList.getLength() > 0) {

			// Parse all of the strokes with a full list of attributes
			for (int i = 0; i < strokeList.getLength(); i++) {
				Element strokeElement = (Element) strokeList.item(i);
				parseStrokeElement(strokeElement);
			}
		}

		for (IStroke stroke : m_parentsMap.keySet())
			stroke.setParent(m_strokeMap.get(m_parentsMap.get(stroke)));
	}

	/**
	 * Parse an individual stroke element. Adds the stroke to the main sketch
	 * object. Also updates the stroke in the map from stroke IDs to stroke
	 * objects.
	 * 
	 * @param strokeElement
	 *            A stroke element in the sketch
	 */
	private void parseStrokeElement(Element strokeElement) {

		// Initialize the stroke in the Map
		String id = strokeElement.getAttribute("id");
		Stroke stroke = new Stroke();
		m_strokeMap.put(id, stroke);

		// Get every attribute in the element
		NamedNodeMap attributes = strokeElement.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			String name = attributes.item(i).getNodeName();
			String value = attributes.item(i).getNodeValue();
			if (name.equals("id")) {
				// Should already be set
				stroke.setID(UUID.fromString(id));
			} else if (name.equals("label")) {
				stroke.setLabel(value);
			} else if (name.equals("parent")) {
				m_parentsMap.put(stroke, value);
			} else if (name.equals("author")) {
				stroke.setAuthor(m_authorMap.get(value));
			} else if (name.equals("pen")) {
				stroke.setPen(m_penMap.get(value));
			} else if (name.equals("color")) {
				stroke.setColor(new Color(Integer.valueOf(value)));
			} else if (name.equals("visible")) {
				stroke.setVisible(Boolean.parseBoolean(value));
			} else {
				stroke.setAttribute(name, value);
			}
		}

		// Add the components (points and ?segmentations) to the stroke
		NodeList strokeArgs = strokeElement.getElementsByTagName("arg");
		parseStrokeComponents(stroke, strokeArgs);

		// Don't Add the stroke to the sketch
		// m_sketch.addStroke(stroke);
	}

	/**
	 * Parse the children arguments of a stroke, which include points and
	 * segmentations.
	 * 
	 * @param stroke
	 *            Stroke to parse the children for
	 * @param strokeArgs
	 *            Arguments of the stroke
	 */
	private void parseStrokeComponents(Stroke stroke, NodeList strokeArgs) {

		List<IPoint> strokePoints = new ArrayList<IPoint>();
		List<ISegmentation> strokeSegmentations = new ArrayList<ISegmentation>();

		// Get all of the children arguments
		for (int i = 0; i < strokeArgs.getLength(); i++) {
			Element arg = (Element) strokeArgs.item(i);

			String type = arg.getAttribute("type");
			String id = arg.getTextContent();

			if (type.equals("point")) {
				Point pt = m_pointMap.get(id);

				// TODO - find out why points are being found null -
				// DG022_F_P_1_X_X_X_Armor_AlternateForVision_128-0.xml
				if (pt != null) {
					strokePoints.add(pt);
				}
			} else if (type.equals("segmentation")) {

				Segmentation seg = m_segMap.get(id);

				// If we haven't initialized the segmentation yet, do so
				if (seg == null) {
					seg = new Segmentation();
					seg.setID(UUID.fromString(id));
					m_segMap.put(id, seg);
				}

				strokeSegmentations.add(seg);
			}
		}

		// Set the stroke members
		stroke.setPoints(strokePoints);
		stroke.setSegmentations(strokeSegmentations);
	}

	/**
	 * Parse the list of segmentation elements.
	 * 
	 * @param segList
	 *            The list of segmentation elements in the sketch
	 */
	private void parseSegmentationList(NodeList segList) {

		m_segMap = new HashMap<String, Segmentation>();

		if (segList != null && segList.getLength() > 0) {

			// Parse all of the shapes with a full list of attributes
			for (int i = 0; i < segList.getLength(); i++) {
				Element shapeElement = (Element) segList.item(i);
				parseSegmentationElement(shapeElement);
			}
		}
	}

	/**
	 * Parse an individual segmentation element. Updates the segmentation in the
	 * map from stroke IDs to stroke objects.
	 * 
	 * @param segElement
	 *            A stroke element in the sketch
	 */
	private void parseSegmentationElement(Element segElement) {

		// Pull the segmentation from the map
		String id = segElement.getAttribute("id");
		Segmentation seg = m_segMap.get(id);

		// If we haven't initialized the segmentation yet, do so
		if (seg == null) {
			seg = new Segmentation();
			seg.setID(UUID.fromString(id));
			m_segMap.put(id, seg);
		}

		// Get every attribute in the element
		NamedNodeMap attributes = segElement.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			String name = attributes.item(i).getNodeName();
			String value = attributes.item(i).getNodeValue();

			if (name.equals("id")) {
				// Should already be set
			} else if (name.equals("label")) {
				seg.setLabel(value);
			} else if (name.equals("confidence")) {
				seg.setConfidence(Double.parseDouble(value));
			} else if (name.equals("segmenterName")) {
				seg.setSegmenterName(value);
			} else {
				seg.setAttribute(name, value);
			}
		}

		// Add the components (segmentable strokes) to the shape
		NodeList segArgs = segElement.getElementsByTagName("arg");
		parseSegmentationComponents(seg, segArgs);

		// TODO - Do we need to go through the segmentations and add all of the
		// strokes to the sketch?! If so, we need to do some serious rethinking
	}

	/**
	 * Parse the children arguments of a segmentation, which include strokes
	 * 
	 * @param seg
	 *            Segmentation to parse the children for
	 * @param segArgs
	 *            Arguments of the segmentation
	 */
	private void parseSegmentationComponents(Segmentation seg, NodeList segArgs) {

		List<IStroke> segStrokes = new ArrayList<IStroke>();

		// Get all of the children arguments
		for (int i = 0; i < segArgs.getLength(); i++) {
			Element arg = (Element) segArgs.item(i);

			String type = arg.getAttribute("type");
			String id = arg.getTextContent();

			// All strokes should be initialized by this point
			if (type.equals("stroke")) {
				segStrokes.add(m_strokeMap.get(id));
			}
		}

		// Set the segmentation members
		seg.setSegmentedStrokes(segStrokes);
	}

	/**
	 * Parse the list of shape elements.
	 * 
	 * @param shapeList
	 *            The list of shape elements in the sketch
	 */
	private void parseShapeList(NodeList shapeList) {

		m_shapeMap = new HashMap<String, Shape>();

		if (shapeList != null && shapeList.getLength() > 0) {

			// Parse all of the shapes with a full list of attributes
			for (int i = 0; i < shapeList.getLength(); i++) {
				Element shapeElement = (Element) shapeList.item(i);
				parseShapeElement(shapeElement);
			}
		}
	}

	/**
	 * Parse an individual shape element. Adds the shape to the main sketch
	 * object. Also updates the shape in the map from shape IDs to shape
	 * objects.
	 * 
	 * @param shapeElement
	 *            A shape element in the sketch
	 */
	private void parseShapeElement(Element shapeElement) {

		// Pull the shape from the map if available
		String id = shapeElement.getAttribute("id");
		Shape shape = m_shapeMap.get(id);

		// If we haven't initialized the shape yet, do so
		if (shape == null) {
			shape = new Shape();
			shape.setID(UUID.fromString(id));
			m_shapeMap.put(id, shape);
		}

		// Get every attribute in the element
		NamedNodeMap attributes = shapeElement.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			String name = attributes.item(i).getNodeName();
			String value = attributes.item(i).getNodeValue();

			if (name.equals("id")) {
				// Should already be set
			} else if (name.equals("label")) {
				shape.setLabel(value);
			} else if (name.equals("description")) {
				shape.setDescription(value);
			} else if (name.equals("confidence")) {
				shape.setConfidence(Double.parseDouble(value));
			} else if (name.equals("recognizer")) {
				shape.setRecognizer(value);
			} else if (name.equals("orientation")) {
				shape.setOrientation(Double.parseDouble(value));
			} else if (name.equals("color")) {
				shape.setColor(new Color(Integer.valueOf(value)));
			} else if (name.equals("visible")) {
				shape.setVisible(Boolean.parseBoolean(value));
			} else {
				shape.setAttribute(name, value);
			}
		}

		// Add the components (strokes and subshapes) to the shape
		NodeList shapeArgs = shapeElement.getElementsByTagName("arg");
		parseShapeComponents(shape, shapeArgs);

		// Don't Add the shape to the sketch
		// m_sketch.addShape(shape);
	}

	/**
	 * Parse the children arguments of a shape, which include strokes and
	 * subshapes
	 * 
	 * @param shape
	 *            Shape to parse the children for
	 * @param shapeArgs
	 *            Arguments of the shape
	 */
	private void parseShapeComponents(Shape shape, NodeList shapeArgs) {

		List<IStroke> shapeStrokes = new ArrayList<IStroke>();
		List<IShape> shapeSubShapes = new ArrayList<IShape>();

		// Get all of the children arguments
		for (int i = 0; i < shapeArgs.getLength(); i++) {

			Element arg = (Element) shapeArgs.item(i);

			if (!arg.getAttribute("type").isEmpty()) {

				// Parse the type arguments: strokes and subshapes
				String type = arg.getAttribute("type");
				String id = arg.getTextContent();

				if (type.equals("stroke")) {
					shapeStrokes.add(m_strokeMap.get(id));
				} else if (type.equals("shape")) {

					Shape subshape = m_shapeMap.get(id);

					// If we haven't initialized the subshape yet, do so
					if (subshape == null) {
						subshape = new Shape();
						subshape.setID(UUID.fromString(id));
						m_shapeMap.put(id, subshape);
					}

					shapeSubShapes.add(subshape);
				}
			}

			else if (!arg.getAttribute("alias").isEmpty()) {

				// Parse the alias arguments
				String alias = arg.getAttribute("alias");
				String id = arg.getTextContent();

				shape.addAlias(new Alias(alias, new edu.tamu.core.sketch.Point(
						m_pointMap.get(id))));
			}
		}

		// Set the shape members
		shape.setStrokes(shapeStrokes);
		shape.setSubShapes(shapeSubShapes);
	}

	private void parseSketchDirectElements(Element sketchStrokes) {
		NodeList args = sketchStrokes.getElementsByTagName("arg");

		for (int i = 0; i < args.getLength(); ++i) {
			Element arg = (Element) args.item(i);
			String type = arg.getAttribute("type");
			if ("shape".equalsIgnoreCase(type)) {
				m_sketch.addShape(m_shapeMap.get(arg.getTextContent()));
			} else if ("stroke".equalsIgnoreCase(type)) {
				m_sketch.addStroke(m_strokeMap.get(arg.getTextContent()));
			}
		}
	}
}
