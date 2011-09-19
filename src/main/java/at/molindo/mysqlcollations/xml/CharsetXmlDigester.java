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

package at.molindo.mysqlcollations.xml;

import org.apache.commons.digester.Digester;

public class CharsetXmlDigester extends Digester {

	private static final String CHARSETS = "charsets";
	private static final String CHARSET = CHARSETS + "/charset";
	private static final String COLLATION = CHARSET + "/collation";

	public CharsetXmlDigester() {
		setValidating(false);
		setNamespaceAware(false);

		addObjectCreate(CHARSETS, MySqlCharsetsBean.class);
		addBeanPropertySetter(CHARSETS + "/copyright");

		addObjectCreate(CHARSET, MySqlCharsetBean.class);
		addSetProperties(CHARSET);
		addSetNext(CHARSET, "add");

		for (final String map : new String[] { "ctype", "lower", "upper", "unicode" }) {
			final String element = CHARSET + "/" + map;
			addObjectCreate(element, MySqlCharacterMapBean.class);
			addBeanPropertySetter(element + "/map");
			addSetNext(element, "set" + map.substring(0, 1).toUpperCase() + map.substring(1));
		}

		addObjectCreate(COLLATION, MySqlCollationBean.class);
		addSetProperties(COLLATION);
		addBeanPropertySetter(COLLATION + "/map");
		addSetNext(COLLATION, "add");
	}
}
