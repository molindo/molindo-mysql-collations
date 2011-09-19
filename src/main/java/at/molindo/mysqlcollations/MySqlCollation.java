/**
 * Copyright 2010 Molindo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.molindo.mysqlcollations;

import java.io.Serializable;

import at.molindo.mysqlcollations.xml.MySqlCollationBean;

public class MySqlCollation implements Serializable {

	private static final long serialVersionUID = 1L;

	private final MySqlCharset _charset;

	private final String _name;
	private final byte[] _weights;

	private final MySqlCollator _collator;

	/**
	 * contains the lowest character mapping for any weight - used for
	 * normalization of
	 */
	private final char[] _normalize;

	public MySqlCollation(MySqlCharset charset, MySqlCollationBean collation) {
		if (charset == null) {
			throw new NullPointerException("charset");
		}
		_charset = charset;
		_name = collation.getName();
		_weights = MySqlCharsetUtils.toByteArray(collation.getMap());

		_collator = new MySqlCollator(this);

		_normalize = new char[MySqlCharset.MAX_CHARACTERS];

		for (char c : _charset.getCharacters()) {
			final int index = getWeight(c) & 0xFF;
			final char current = _normalize[index];
			if (current == 0x0 || current > c) {
				_normalize[index] = c;
			}
		}
	}

	public MySqlCollator getCollator() {
		return _collator;
	}

	public byte getWeight(final char character) {
		return _weights == null ? (byte) character : _weights[getCharset().toIndex(character)];
	}

	public String normalize(final String string) {
		if (string == null) {
			return null;
		}
		final StringBuilder buf = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			buf.append(_normalize[getWeight(string.charAt(i)) & 0xFF]);
		}
		return buf.toString();
	}

	public MySqlCharset getCharset() {
		return _charset;
	}

	public String getName() {
		return _name;
	}

	@Override
	public String toString() {
		return "MySqlCollation [name=" + _name + ", charset=" + _charset + "]";
	}

}
