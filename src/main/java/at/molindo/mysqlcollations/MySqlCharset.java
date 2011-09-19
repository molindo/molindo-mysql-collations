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
import java.util.HashMap;
import java.util.Map;

import at.molindo.mysqlcollations.xml.MySqlCharsetBean;
import at.molindo.mysqlcollations.xml.MySqlCollationBean;

public class MySqlCharset implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int MAX_CHARACTERS = 256;

	private final String _name;
	private final byte[] _ctypes;
	private final char[] _chars;
	private final int[] _upper;
	private final int[] _lower;

	private final HashMap<String, MySqlCollation> _collations = new HashMap<String, MySqlCollation>();

	/**
	 * char translates to index
	 */
	private final Map<Character, Integer> _charIndexes;

	public MySqlCharset(MySqlCharsetBean charset) {
		_name = charset.getName();
		_ctypes = MySqlCharsetUtils.toByteArray(charset.getCtype().getMap());
		_chars = MySqlCharsetUtils.toCharArray(charset.getUnicode().getMap());
		_upper = MySqlCharsetUtils.toIntArray(charset.getUpper().getMap());
		_lower = MySqlCharsetUtils.toIntArray(charset.getUpper().getMap());

		_charIndexes = new HashMap<Character, Integer>(MAX_CHARACTERS * 2);
		for (int i = 0; i < MAX_CHARACTERS; i++) {
			_charIndexes.put(_chars[i], i);
		}

		for (Map.Entry<String, MySqlCollationBean> e : charset.getCollations().entrySet()) {
			_collations.put(e.getKey(), new MySqlCollation(this, e.getValue()));
		}

	}

	/*
	 * 0x01 Upper-case word character 0x02 Lower-case word character 0x04
	 * Decimal digit 0x08 Printer control (Space/TAB/VT/FF/CR) 0x10 Not-white,
	 * not a word 0x20 Control-char (0x00 - 0x1F) 0x40 Space 0x80 Hex digit
	 * (0-9, a-f, A-F)
	 */
	public boolean isUpperCaseWordChar(final char character) {
		return (getCtypeValue(character) & 0x01) != 0x0;
	}

	public boolean isLowerCaseWordChar(final char character) {
		return (getCtypeValue(character) & 0x02) != 0x0;
	}

	public boolean isDecimalDigit(final char character) {
		return (getCtypeValue(character) & 0x04) != 0x0;
	}

	public boolean isPrinterControl(final char character) {
		return (getCtypeValue(character) & 0x08) != 0x0;
	}

	public boolean isNotWhiteNotWord(final char character) {
		return (getCtypeValue(character) & 0x10) != 0x0;
	}

	public boolean isControlChar(final char character) {
		return (getCtypeValue(character) & 0x20) != 0x0;
	}

	public boolean isSpace(final char character) {
		return (getCtypeValue(character) & 0x40) != 0x0;
	}

	public boolean isHexDigit(final char character) {
		return (getCtypeValue(character) & 0x80) != 0x0;
	}

	public int getCtypeValue(final char character) {
		// ctype has a leading 00, hence +1
		return _ctypes[toIndex(character) + 1];
	}

	public char toLower(final char character) {
		return toChar(_lower[toIndex(character)]);
	}

	public char toUpper(final char character) {
		return toChar(_upper[toIndex(character)]);
	}

	/**
	 * @return character index for this charset, always >= 0 and <
	 *         {@link #MAX_CHARACTERS}
	 * @throws UnmappableCharacterException
	 *             for unmappable character
	 */
	public int toIndex(final char character) {
		Integer i = _charIndexes.get(character);
		if (i == null) {
			throw new UnmappableCharacterException(this, character);
		}
		return i;
	}

	public boolean isMappable(char character) {
		return _charIndexes.containsKey(character);
	}

	public boolean isMappable(String string) {
		int l = string.length();
		for (int i = 0; i < l; i++) {
			if (!isMappable(string.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public char toChar(final int index) {
		return _chars[index];
	}

	public char toUnicode(final char character) {
		// supposed to return the same character but checking mappability
		return _chars[toIndex(character)];
	}

	public String getName() {
		return _name;
	}

	public MySqlCollation getCollation(String collation) {
		MySqlCollation coll = _collations.get(collation);
		if (coll == null) {
			throw new IllegalArgumentException("collation not available for charset '" + _name + "': " + collation);
		}
		return coll;
	}

	char[] getCharacters() {
		return _chars;
	}

	@Override
	public String toString() {
		return "MySqlCharset [name=" + _name + ", collations=" + _collations + "]";
	}

}
