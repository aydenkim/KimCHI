/**
 * EllipsePainter.java
 * 
 * Revision History:<br>
 * Jul 1, 2008 bpaulson - File created
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
package edu.tamu.recognition.paleo;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import org.ladder.core.sketch.IBeautifiable;

import edu.tamu.core.sketch.IShapePainter;
import edu.tamu.core.sketch.Shape;

/**
 * Special painter for ellipses (allows ellipses to be painted in a rotated
 * manner)
 * 
 * @author bpaulson
 */
public class EllipsePainter implements IShapePainter {

	/**
	 * Shape to paint
	 */
	private Shape m_shape;

	/**
	 * Angle of the major axis (rotation angle)
	 */
	private double m_majorAxisAngle;

	/**
	 * Center point (of rotation)
	 */
	private Point2D m_center;

	/**
	 * Constructor for painter
	 * 
	 * @param shape
	 *            shape to paint (ellipse)
	 * @param majorAxisAngle
	 *            angle of the major axis (rotation angle)
	 * @param center
	 *            center point (of rotation)
	 */
	public EllipsePainter(Shape shape, double majorAxisAngle, Point2D center) {
		m_shape = shape;
		m_majorAxisAngle = majorAxisAngle;
		m_center = center;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.tamu.core.sketch.IShapePainter#paintSpecial(java.awt.Graphics)
	 */
	public void paintSpecial(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.rotate(m_majorAxisAngle, m_center.getX(), m_center.getY());
		g2.draw(((IBeautifiable) m_shape).getBeautifiedShape());
		g2.rotate(m_majorAxisAngle * -1, m_center.getX(), m_center.getY());
	}

}
