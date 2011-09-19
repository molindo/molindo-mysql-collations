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
 * {@link MySqlCharsetsBean}) containing a map of {@link MySqlCollationBean}
 * mapped by name. Additionally, all character maps (lower, upper, unicode and
 * ctype)
 * 
 * 
 * @author stf@molindo.at
 */
public class MySqlCharsetBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String _name;
	private MySqlCharacterMapBean _ctype;
	private MySqlCharacterMapBean _lower;
	private MySqlCharacterMapBean _upper;
	private MySqlCharacterMapBean _unicode;
	private Map<String, MySqlCollationBean> _collations;

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
	public MySqlCharacterMapBean getCtype() {
		return _ctype;
	}

	public void setCtype(final MySqlCharacterMapBean ctype) {
		_ctype = ctype;
	}

	/**
	 * @return maps upper case to lower case characters
	 */
	public MySqlCharacterMapBean getLower() {
		return _lower;
	}

	public void setLower(final MySqlCharacterMapBean lower) {
		_lower = lower;
	}

	/**
	 * @return maps lower case to upper case characters
	 */
	public MySqlCharacterMapBean getUpper() {
		return _upper;
	}

	public void setUpper(final MySqlCharacterMapBean upper) {
		_upper = upper;
	}

	/**
	 * @return maps characters to unicode representation
	 */
	public MySqlCharacterMapBean getUnicode() {
		return _unicode;
	}

	public void setUnicode(final MySqlCharacterMapBean unicode) {
		_unicode = unicode;
	}

	public Map<String, MySqlCollationBean> getCollations() {
		return _collations;
	}

	public void setCollations(final Map<String, MySqlCollationBean> collations) {
		_collations = collations;
	}

	public void add(final MySqlCollationBean collation) {
		if (_collations == null) {
			_collations = new HashMap<String, MySqlCollationBean>();
		}
		if (_collations.put(collation.getName(), collation) != null) {
			throw new IllegalArgumentException("duplicate collation name: " + collation.getName());
		}
		collation.setCharset(this);
	}

	@Override
	public String toString() {
		return "Charset [_name=" + _name + ", _collations=" + _collations + "]";
	}

}
