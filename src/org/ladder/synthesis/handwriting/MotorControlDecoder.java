/*******************************************************************************
 *  Revision History:<br>
 *  SRL Member - File created
 *
 *  <p>
 *  <pre>
 *  This work is released under the BSD License:
 *  (C) 2011 Sketch Recognition Lab, Texas A&M University (hereafter SRL @ TAMU)
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Sketch Recognition Lab, Texas A&M University 
 *        nor the names of its contributors may be used to endorse or promote 
 *        products derived from this software without specific prior written 
 *        permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY SRL @ TAMU ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SRL @ TAMU BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  </pre>
 *  
 *******************************************************************************/
package org.ladder.synthesis.handwriting;

import org.ladder.core.sketch.IPoint;
import org.ladder.core.sketch.IStroke;

/**
 * Singer93
 * 
 * @author awolin
 */
public class MotorControlDecoder {

	/**
	 * Stroke to decode
	 */
	private IStroke m_stroke = null;

	/**
	 * Velocity of x for each time step
	 */
	private double[] m_vx = null;

	/**
	 * Velocity of y for each time step
	 */
	private double[] m_vy = null;

	/**
	 * Time steps
	 */
	private long[] m_dt = null;

	/**
	 * Constructor
	 */
	public MotorControlDecoder() {

	}

	/**
	 * Decodes the given stroke into corresponding x,y velocities over time.
	 * 
	 * @param stroke
	 *            the stroke to decode.
	 */
	public void decodeStroke(IStroke stroke) {
		setStroke(stroke);

		if (m_stroke != null) {
			m_vx = new double[m_stroke.getNumPoints()];
			m_vy = new double[m_stroke.getNumPoints()];
			m_dt = new long[m_stroke.getNumPoints()];

			IPoint pt0 = m_stroke.getPoint(0);
			m_vx[0] = 0.0;
			m_vy[0] = 0.0;
			m_dt[0] = 0;

			long startTime = pt0.getTime();

			// Update the velocities and time changes
			for (int i = 1; i < m_stroke.getNumPoints(); i++) {

				IPoint pt1 = m_stroke.getPoint(i);
				m_vx[i] = pt1.getX() - pt0.getX();
				m_vy[i] = pt1.getY() - pt0.getY();
				m_dt[i] = pt1.getTime() - startTime;

				pt0 = pt1;
			}
		}

	}

	public double[] getVelocityX() {
		return m_vx;
	}

	public double[] getVelocityY() {
		return m_vy;
	}

	public long[] getDeltaTimes() {
		return m_dt;
	}

	/**
	 * Sets the stroke to decode.
	 * 
	 * @param stroke
	 *            the stroke to decode.
	 */
	public void setStroke(IStroke stroke) {
		m_stroke = stroke;
	}
}
