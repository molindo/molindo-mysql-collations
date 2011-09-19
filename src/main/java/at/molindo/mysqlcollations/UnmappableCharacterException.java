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

public class UnmappableCharacterException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final MySqlCharset _charset;
	private final char _character;

	public UnmappableCharacterException(MySqlCharset charset, char character) {
		super("unmappable character '" + character + "' for charset " + charset.getName());
		_charset = charset;
		_character = character;
	}

	public MySqlCharset getCharset() {
		return _charset;
	}

	public char getCharacter() {
		return _character;
	}

}
