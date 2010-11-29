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
package at.molindo.mysqlcollations;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MySqlCharset implements Serializable {
	static final int MAX_CHARACTERS = 256;

	private static final long serialVersionUID = 1L;

	private String _name;
	private MySqlCharacterMap _ctype;
	private MySqlCharacterMap _lower;
	private MySqlCharacterMap _upper;
	private MySqlCharacterMap _unicode;
	private Map<String, MySqlCollation> _collations;

	private transient Charset _charset;
	private transient char[] _charTable;
	private transient int[] _indexTable;

	private static int toBytes(final int index, final byte[] bytes) {
		int first = -1;
		for (int i = 0; i < bytes.length; i++) {
			final int b = index >> (3 - i) * 8;
			bytes[i] = (byte) (b & 0xFF);
			if (first < 0 && bytes[i] != 0x00) {
				first = i;
			}
		}
		return first;
	}

	public String getName() {
		return _name;
	}

	public void setName(final String name) {
		_name = name;
	}

	public MySqlCharacterMap getCtype() {
		return _ctype;
	}

	public void setCtype(final MySqlCharacterMap ctype) {
		_ctype = ctype;
	}

	public MySqlCharacterMap getLower() {
		return _lower;
	}

	public void setLower(final MySqlCharacterMap lower) {
		_lower = lower;
	}

	public MySqlCharacterMap getUpper() {
		return _upper;
	}

	public void setUpper(final MySqlCharacterMap upper) {
		_upper = upper;
	}

	public MySqlCharacterMap getUnicode() {
		return _unicode;
	}

	public void setUnicode(final MySqlCharacterMap unicode) {
		_unicode = unicode;
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
		// ctype has a leading 00
		return getCtype().getValue(toIndex(character) + 1);
	}

	public char toLower(final char character) {
		return toChar(getLower().getValue(toIndex(character)));
	}

	public char toUpper(final char character) {
		return toChar(getUpper().getValue(toIndex(character)));
	}

	public int toIndex(final char character) {
		if (_indexTable != null) {
			final int i = 0x00 | character;
			if (i > 0 && i < _indexTable.length) {
				return _indexTable[i];
			}
		}

		final ByteBuffer buf = getCharset().encode(new String(new char[] { character }));

		int index = 0x0;
		int pos = 1;
		while (buf.hasRemaining()) {
			int current = 0xFF;
			current &= buf.get();
			current <<= (buf.capacity() - pos++) * 8;
			index |= current;
		}

		return index;
	}

	public char toChar(final int index) {
		if (_charTable != null && index >= 0 && index < _charTable.length) {
			return _charTable[index];
		}

		final byte[] bytes = new byte[4];
		final int first = toBytes(index, bytes);
		if (first < 0) {
			return 0x00;
		}
		return getCharset().decode(ByteBuffer.wrap(bytes, first, bytes.length - first)).get();
	}

	public char toUnicode(final char character) {
		return (char) getUnicode().getValue(toIndex(character));
	}

	private Charset getCharset() {
		initCharset();
		return _charset;
	}

	private void initCharset() {
		if (_charset == null && getName() != null) {
			_charset = Charset.forName(getName());

			final char[] charTable = new char[MAX_CHARACTERS];
			final int[] indexTable = new int[MAX_CHARACTERS];
			for (int i = 0; i < MAX_CHARACTERS; i++) {
				charTable[i] = toChar(i);
				indexTable[i] = toIndex((char) i);
			}
			_charTable = charTable;
			_indexTable = indexTable;
		}
	}

	public char[] getCharacters() {
		initCharset();
		return Arrays.copyOf(_charTable, _charTable.length);
	}

	@Override
	public String toString() {
		return "Charset [_name=" + _name + ", _collations=" + _collations + "]";
	}

}
