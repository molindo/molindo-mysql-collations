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

/**
 * 
 */
package at.molindo.mysqlcollations.xml;

import java.io.Serializable;

import at.molindo.mysqlcollations.MySqlCollator;

/**
 * &lt;collation&gt; from charset XML files, basically a named
 * {@link MySqlCharacterMap}
 * 
 * @author stf@molindo.at
 */
public class MySqlCollation extends MySqlCharacterMap implements Serializable {
	private static final long serialVersionUID = 1L;

	private String _name;
	private String _flag;
	private MySqlCharset _charset;
	private MySqlCollator _collator;

	/**
	 * contains the lowest character mapping for any weight - used for
	 * normalization of
	 */
	volatile private char[] _normalize;

	public String getName() {
		return _name;
	}

	public void setName(final String name) {
		_name = name;
	}

	public String getFlag() {
		return _flag;
	}

	public void setFlag(final String flag) {
		_flag = flag;
	}

	public MySqlCharset getCharset() {
		return _charset;
	}

	public void setCharset(final MySqlCharset charset) {
		_charset = charset;
	}

	@Override
	public String toString() {
		return "Collation [_name=" + _name + ", _flag=" + _flag + "]";
	}

	public MySqlCollator getCollatorInstance() {
		if (_collator == null) {
			_collator = new MySqlCollator(this);
		}
		return _collator;
	}

	public byte getWeight(final char character) {
		return (byte) getValue(getCharset().toIndex(character));
	}

	private char[] getNormalize() {
		if (_normalize == null) {
			synchronized (this) {
				if (_normalize == null) {
					_normalize = new char[MySqlCharset.MAX_CHARACTERS];

					for (char c : getCharset().getCharacters()) {
						final int index = getWeight(c) & 0xFF;
						final char current = _normalize[index];
						if (current == 0x0 || current > c) {
							_normalize[index] = c;
						}
					}
				}
			}
		}
		return _normalize;
	}

	public String normalize(final String string) {
		if (string == null) {
			return null;
		}
		final char[] normalize = getNormalize();
		final StringBuilder buf = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			buf.append(normalize[getWeight(string.charAt(i)) & 0xFF]);
		}
		return buf.toString();
	}
}
