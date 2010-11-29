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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;

import org.apache.commons.digester.Digester;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import at.molindo.utils.properties.SystemProperty;

public class MySqlCollatorFactory implements Serializable {

	private static final long serialVersionUID = 1L;

	private static class CharsetFileFilter implements FilenameFilter {

		@Override
		public boolean accept(final File dir, final String name) {
			return name.endsWith(".xml") && !"Index.xml".equals(name);
		}

	}

	private static final String CHARSETS = "charsets";
	private static final String CHARSET = CHARSETS + "/charset";
	private static final String COLLATION = CHARSET + "/collation";

	private static final String PROPERTY_BASE = MySqlCollatorFactory.class.getPackage().getName();
	private static final String PROPERTY_MYSQL_DIR = PROPERTY_BASE + ".mySqlDir";
	private static final String PROPERTY_MYSQL_HOME_DIR = PROPERTY_BASE + ".mySqlHomeDir";

	public static final String CHARSET_LATIN1 = "latin1";
	public static final String COLLATION_LATIN1_SWEDISH_CI = "latin1_swedish_ci";

	public static final String CHARSET_DEFAULT = "latin1";
	public static final String COLLATION_DEFAULT = "latin1_swedish_ci";

	public static final String CHARSET_DIR = "/usr/share/mysql/charsets";

	private MySqlCharsets _charsets;

	public static MySqlCollatorFactory parse(final String path) throws IOException, SAXException {
		return parse(new File(path));
	}

	public static MySqlCollatorFactory parse(final File collation) throws IOException, SAXException {
		return parse(new FileInputStream(collation));
	}

	public static MySqlCollatorFactory parse(final URL collation) throws IOException, SAXException {
		return parse(collation.openStream());
	}

	public static MySqlCollatorFactory parse(final InputStream in) throws IOException, SAXException {
		return parse(new InputSource(in));
	}

	public static MySqlCollatorFactory parse(final Reader reader) throws IOException, SAXException {
		return parse(new InputSource(reader));
	}

	public static MySqlCollatorFactory parseDirectory(final String path) throws IOException, SAXException {
		return parseDirectory(new File(path));
	}

	public static MySqlCollatorFactory parseDirectory(final File dir) throws IOException, SAXException {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("not a directory = " + dir);
		}

		MySqlCollatorFactory factory = null;
		for (final File file : dir.listFiles(new CharsetFileFilter())) {
			final MySqlCollatorFactory current = parse(file);
			if (factory == null) {
				factory = current;
			} else {
				factory.addCharsets(current);
			}
		}
		return factory;
	}

	public static boolean isValidCharsetDirectory() {
		return getDirectory().isDirectory();
	}

	public static MySqlCollatorFactory parseDefaultDirectory() throws IOException, SAXException {
		final File dir = getDirectory();

		if (dir.isDirectory()) {
			return parseDirectory(dir);
		} else {
			if ("windows".equals(SystemProperty.OS_FAMILY.get())) {
				throw new RuntimeException(String.format("%s ist not a directory, please check your %s system property (current value %s) for a valid MySQL charsets directory", dir, PROPERTY_MYSQL_HOME_DIR, System
						.getProperty(PROPERTY_MYSQL_HOME_DIR)));
			} else {
				throw new RuntimeException(String.format("%s ist not a directory, please check your %s system property (current value %s) for a valid MySQL charsets directory", dir, PROPERTY_MYSQL_DIR, System
						.getProperty(PROPERTY_MYSQL_DIR)));
			}
		}

	}

	private static File getDirectory() {
		final String os = SystemProperty.OS_FAMILY.get();

		if ("windows".equals(os)) {
			return new File(System.getProperties()
					.getProperty(PROPERTY_MYSQL_HOME_DIR, "C:\\Programme\\MySQL\\MySQL Server 5.0")
					+ "\\share\\charsets");
		} else {
			return new File(System
					.getProperties()
					.getProperty(PROPERTY_MYSQL_DIR, "mac".equals(os) ? "/usr/local/mysql/share/charsets" : "/usr/share/mysql/charsets"));
		}
	}

	public static MySqlCollatorFactory parse(final InputSource source) throws IOException, SAXException {
		final Digester digester = newDigester();
		final MySqlCharsets charsets = (MySqlCharsets) digester.parse(source);
		return new MySqlCollatorFactory(charsets);
	}

	private static Digester newDigester() {
		final Digester digester = new Digester();
		digester.setValidating(false);
		digester.setNamespaceAware(false);

		digester.addObjectCreate(CHARSETS, MySqlCharsets.class);
		digester.addBeanPropertySetter(CHARSETS + "/copyright");

		digester.addObjectCreate(CHARSET, MySqlCharset.class);
		digester.addSetProperties(CHARSET);
		digester.addSetNext(CHARSET, "add");

		for (final String map : new String[] { "ctype", "lower", "upper", "unicode" }) {
			final String element = CHARSET + "/" + map;
			digester.addObjectCreate(element, MySqlCharacterMap.class);
			digester.addBeanPropertySetter(element + "/map");
			digester.addSetNext(element, "set" + map.substring(0, 1).toUpperCase()
					+ map.substring(1));
		}

		digester.addObjectCreate(COLLATION, MySqlCollation.class);
		digester.addSetProperties(COLLATION);
		digester.addBeanPropertySetter(COLLATION + "/map");
		digester.addSetNext(COLLATION, "add");
		return digester;
	}

	private MySqlCollatorFactory(final MySqlCharsets charsets) {
		_charsets = charsets;
	}

	public MySqlCollator getCollator(final String charset, final String collation) {
		final MySqlCharset cset = _charsets.getCharsets().get(charset);
		if (cset == null) {
			throw new IllegalArgumentException("charset not available: " + charset);
		}
		final MySqlCollation coll = cset.getCollations().get(collation);
		if (coll == null) {
			throw new IllegalArgumentException("collation not available for charset '" + charset
					+ "': " + collation);
		}
		return coll.getCollatorInstance();
	}

	public MySqlCollator getDefaultCollator() {
		return getCollator(CHARSET_DEFAULT, COLLATION_DEFAULT);
	}

	private void addCharsets(final MySqlCollatorFactory current) {
		_charsets = new MySqlCharsets(_charsets, current._charsets);
	}
}
