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
 * &lt;charsets&gt; from charset XML files, containing a map of
 * {@link MySqlCharset} mapped by name
 * 
 * @author stf@molindo.at
 */
public class MySqlCharsets implements Serializable {

	private static final long serialVersionUID = 1L;
	private String _copyright;
	private Map<String, MySqlCharset> _charsets;

	/**
	 * default constructor used by XML digester
	 */
	public MySqlCharsets() {
	}

	/**
	 * create a new charset from several others
	 * 
	 * @param charsets
	 */
	public MySqlCharsets(final MySqlCharsets... charsets) {
		_charsets = new HashMap<String, MySqlCharset>(charsets.length * 2);
		for (final MySqlCharsets c : charsets) {
			_charsets.putAll(c._charsets);
		}
	}

	public String getCopyright() {
		return _copyright;
	}

	public void setCopyright(final String copyright) {
		_copyright = copyright;
	}

	public Map<String, MySqlCharset> getCharsets() {
		return _charsets;
	}

	public void setCharsets(final Map<String, MySqlCharset> charsets) {
		_charsets = charsets;
	}

	public void add(final MySqlCharset charset) {
		if (_charsets == null) {
			_charsets = new HashMap<String, MySqlCharset>(4);
		}
		if (_charsets.put(charset.getName(), charset) != null) {
			throw new IllegalArgumentException("duplicate charset name: " + charset.getName());
		}
	}

	@Override
	public String toString() {
		return "Charsets [_charsets=" + _charsets + "]";
	}
}
