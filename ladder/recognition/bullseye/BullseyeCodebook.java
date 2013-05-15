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
package org.ladder.recognition.bullseye;

import java.util.ArrayList;
import java.util.List;

import libsvm.svm_node;

/**
 * The codebook for the BullseyeRecognizer, based on Oltman's thesis.  Basically, a list of Bullseyes that are codewords
 * and the look up function
 * @author pcorey
 *
 */
public class BullseyeCodebook {

	/**
	 * The Bullseyes used as code words in the code book
	 */
	private List<Bullseye> codeWords;
	
	/**
	 * Create an empty codebook
	 */
	public BullseyeCodebook(){
		codeWords = new ArrayList<Bullseye>();
	}
	
	/**
	 * Create a codebook from a list of Bulleyes
	 * @param words List of Bullseyes to use as codewords
	 */
	public BullseyeCodebook(List<Bullseye> words){
		codeWords = new ArrayList<Bullseye>(words);
	}

	/**
	 * Determines how closely the input Bullseyes match the codewords
	 * @param input List of Bullseyes to lookup
	 * @return List of svm_nodes to pass to the svm prediction model
	 */
	public svm_node[] lookUpVector(List<Bullseye> input){
		svm_node[] vector = new svm_node[codeWords.size()];
		int index=0;
		for(Bullseye codeWord : codeWords){
			double value=Double.POSITIVE_INFINITY;
			for(Bullseye in: input)
				if(codeWord.compareTo(in)<value)
					value=codeWord.compareTo(in);
			vector[index]=new svm_node();
			vector[index].index=index;
			vector[index].value=value;
			//System.out.print(value+" ");
			index++;
		}
		//System.out.println();
		return vector;
	}

	@Override
	public String toString(){
		String s = codeWords.size()+"\n";
		for(Bullseye b : codeWords){
			for(double d : b.getHistogram()){
				s+=d+" ";
			}
			s=s.trim()+"\n";
		}
		//System.out.println(s);
		return s;
	}
}
