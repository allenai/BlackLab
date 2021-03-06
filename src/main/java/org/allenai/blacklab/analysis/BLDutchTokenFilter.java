/*******************************************************************************
 * Copyright (c) 2010, 2012 Institute for Dutch Lexicology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.allenai.blacklab.analysis;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * The token filter to accompany BLDutchTokenizer. Will get rid of some
 * unwanted tokens or characters in tokens:
 * * tokens containing no letters are eliminated (e.g. "-")
 * * periods, parens and brackets are removed (e.g. "a.u.b." -> "aub", "bel(len)" -> "bellen")
 * * apostrophes at the beginning or end of a token are removed (e.g. multiple words in single quotes)
 */
public class BLDutchTokenFilter extends TokenFilter {
	final static Pattern removePattern = Pattern.compile("[\\.\\(\\)\\[\\]]|^'|'$");

	final static Pattern anyLetterPattern = Pattern.compile("[\\p{L}\\d]");

	/**
	 * Perform filtering on the input string
	 * @param input the string
	 * @return same string with periods, parens, brackets and apostrophes at beginning/end removed
	 */
	public static String process(String input) {
		return removePattern.matcher(input).replaceAll("");
	}

	private CharTermAttribute termAtt;

	/**
	 * @param input the token stream to remove punctuation from
	 */
	public BLDutchTokenFilter(TokenStream input) {
		super(input);
		termAtt = addAttribute(CharTermAttribute.class);
	}

	@Override
	public boolean incrementToken() throws IOException {
		while (input.incrementToken()) {
			String t = new String(termAtt.buffer(), 0, termAtt.length());

			// Filter out some characters
			t = process(t);

			// Output if there's any letters in it
			if (anyLetterPattern.matcher(t).find()) {
				termAtt.copyBuffer(t.toCharArray(), 0, t.length());
				return true;
			}
		}
		return false;
	}

}
