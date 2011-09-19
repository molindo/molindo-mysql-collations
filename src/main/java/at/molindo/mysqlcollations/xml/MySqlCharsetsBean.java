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
 * {@link MySqlCharsetBean} mapped by name
 * 
 * @author stf@molindo.at
 */
public class MySqlCharsetsBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String _copyright;
	private Map<String, MySqlCharsetBean> _charsets;

	/**
	 * default constructor used by XML digester
	 */
	public MySqlCharsetsBean() {
	}

	/**
	 * create a new charset from several others
	 * 
	 * @param charsets
	 */
	public MySqlCharsetsBean(final MySqlCharsetsBean... charsets) {
		_charsets = new HashMap<String, MySqlCharsetBean>(charsets.length * 2);
		for (final MySqlCharsetsBean c : charsets) {
			_charsets.putAll(c._charsets);
		}
	}

	public String getCopyright() {
		return _copyright;
	}

	public void setCopyright(final String copyright) {
		_copyright = copyright;
	}

	public Map<String, MySqlCharsetBean> getCharsets() {
		return _charsets;
	}

	public void setCharsets(final Map<String, MySqlCharsetBean> charsets) {
		_charsets = charsets;
	}

	public void add(final MySqlCharsetBean charset) {
		if (_charsets == null) {
			_charsets = new HashMap<String, MySqlCharsetBean>(4);
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
