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
import java.util.HashMap;
import java.util.Map;

/**
 * &lt;charset&gt; from charset XML files, nested inside &lt;charsets&gt; (
 * {@link MySqlCharsets}) containing a map of {@link MySqlCollation} mapped by
 * name. Additionally, all character maps (lower, upper, unicode and ctype)
 * 
 * 
 * @author stf@molindo.at
 */
public class MySqlCharset implements Serializable {
	static final int MAX_CHARACTERS = 256;

	private static final long serialVersionUID = 1L;

	private String _name;
	private MySqlCharacterMap _ctype;
	private MySqlCharacterMap _lower;
	private MySqlCharacterMap _upper;
	private MySqlCharacterMap _unicode;
	private Map<String, MySqlCollation> _collations;

	/**
	 * index translates to char
	 */
	private transient char[] _indexToChar;

	/**
	 * char translates to index
	 */
	private transient Map<Character, Integer> _charToIndex;

	public String getName() {
		return _name;
	}

	public void setName(final String name) {
		_name = name;
	}

	/**
	 * @return character type map (contains leading 0x00, hence
	 *         {@link #MAX_CHARACTERS} + 1)
	 */
	public MySqlCharacterMap getCtype() {
		return _ctype;
	}

	public void setCtype(final MySqlCharacterMap ctype) {
		_ctype = ctype;
	}

	/**
	 * @return maps upper case to lower case characters
	 */
	public MySqlCharacterMap getLower() {
		return _lower;
	}

	public void setLower(final MySqlCharacterMap lower) {
		_lower = lower;
	}

	/**
	 * @return maps lower case to upper case characters
	 */
	public MySqlCharacterMap getUpper() {
		return _upper;
	}

	public void setUpper(final MySqlCharacterMap upper) {
		_upper = upper;
	}

	/**
	 * @return maps characters to unicode representation
	 */
	public MySqlCharacterMap getUnicode() {
		return _unicode;
	}

	public void setUnicode(final MySqlCharacterMap unicode) {
		_unicode = unicode;
		initCharLookup();
	}

	private void initCharLookup() {
		if (_unicode == null) {
			_indexToChar = null;
			_charToIndex = null;
		} else {
			_indexToChar = new char[MAX_CHARACTERS];
			_charToIndex = new HashMap<Character, Integer>(MAX_CHARACTERS * 2);

			for (int i = 0; i < MAX_CHARACTERS; i++) {
				_charToIndex.put(_indexToChar[i] = (char) _unicode.getValue(i), i);
			}
		}
	}

	public Map<String, MySqlCollation> getCollations() {
		return _collations;
	}

	public void setCollations(final Map<String, MySqlCollation> collations) {
		_collations = collations;
	}

	public void add(final MySqlCollation collation) {
		if (_collations == null) {
			_collations = new HashMap<String, MySqlCollation>();
		}
		if (_collations.put(collation.getName(), collation) != null) {
			throw new IllegalArgumentException("duplicate collation name: " + collation.getName());
		}
		collation.setCharset(this);
	}

	/*
	 * 0x01 Upper-case word character 0x02 Lower-case word character 0x04
	 * Decimal digit 0x08 Printer control (Space/TAB/VT/FF/CR) 0x10 Not-white,
	 * not a word 0x20 Control-char (0x00 - 0x1F) 0x40 Space 0x80 Hex digit
	 * (0-9, a-f, A-F)
	 */
	boolean isUpperCaseWordChar(final char character) {
		return (getCtypeValue(character) & 0x01) != 0x0;
	}

	boolean isLowerCaseWordChar(final char character) {
		return (getCtypeValue(character) & 0x02) != 0x0;
	}

	boolean isDecimalDigit(final char character) {
		return (getCtypeValue(character) & 0x04) != 0x0;
	}

	boolean isPrinterControl(final char character) {
		return (getCtypeValue(character) & 0x08) != 0x0;
	}

	boolean isNotWhiteNotWord(final char character) {
		return (getCtypeValue(character) & 0x10) != 0x0;
	}

	boolean isControlChar(final char character) {
		return (getCtypeValue(character) & 0x20) != 0x0;
	}

	boolean isSpace(final char character) {
		return (getCtypeValue(character) & 0x40) != 0x0;
	}

	boolean isHexDigit(final char character) {
		return (getCtypeValue(character) & 0x80) != 0x0;
	}

	private int getCtypeValue(final char character) {
		// ctype has a leading 00, hence +1
		return getCtype().getValue(toIndex(character) + 1);
	}

	public char toLower(final char character) {
		return toChar(getLower().getValue(toIndex(character)));
	}

	public char toUpper(final char character) {
		return toChar(getUpper().getValue(toIndex(character)));
	}

	/**
	 * @return -1 for unmappable character
	 */
	public int toIndex(final char character) {
		Integer i = _charToIndex.get(character);
		return i == null ? -1 : i;
	}

	public char toChar(final int index) {
		return _indexToChar[index];
	}

	public char toUnicode(final char character) {
		return (char) getUnicode().getValue(toIndex(character));
	}

	char[] getCharacters() {
		return _indexToChar;
	}

	@Override
	public String toString() {
		return "Charset [_name=" + _name + ", _collations=" + _collations + "]";
	}

}
