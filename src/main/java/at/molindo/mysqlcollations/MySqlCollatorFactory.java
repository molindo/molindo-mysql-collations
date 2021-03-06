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
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import at.molindo.mysqlcollations.xml.CharsetXmlDigester;
import at.molindo.mysqlcollations.xml.MySqlCharsetBean;
import at.molindo.mysqlcollations.xml.MySqlCharsetsBean;
import at.molindo.utils.properties.SystemProperty;

/**
 * 
 * 
 * 
 * @author stf@molindo.at
 */
public class MySqlCollatorFactory implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String PROPERTY_BASE = MySqlCollatorFactory.class.getPackage().getName();
	private static final String PROPERTY_MYSQL_DIR = PROPERTY_BASE + ".mySqlDir";
	private static final String PROPERTY_MYSQL_HOME_DIR = PROPERTY_BASE + ".mySqlHomeDir";

	public static final String CHARSET_ARMSCII8 = "armscii8";
	public static final String COLLATION_ARMSCII8_GENERAL_CI = "armscii8_general_ci";
	public static final String COLLATION_ARMSCII8_BIN = "armscii8_bin";

	public static final String CHARSET_ASCII = "ascii";
	public static final String COLLATION_ASCII_GENERAL_CI = "ascii_general_ci";
	public static final String COLLATION_ASCII_BIN = "ascii_bin";

	public static final String CHARSET_CP1250 = "cp1250";
	public static final String COLLATION_CP1250_CROATIAN_CI = "cp1250_croatian_ci";
	public static final String COLLATION_CP1250_CZECH_CI = "cp1250_czech_ci";
	public static final String COLLATION_CP1250_POLISH_CI = "cp1250_polish_ci";

	public static final String CHARSET_CP1251 = "cp1251";
	public static final String COLLATION_CP1251_BULGARIAN_CI = "cp1251_bulgarian_ci";
	public static final String COLLATION_CP1251_UKRAINIAN_CI = "cp1251_ukrainian_ci";
	public static final String COLLATION_CP1251_GENERAL_CS = "cp1251_general_cs";
	public static final String COLLATION_CP1251_GENERAL_CI = "cp1251_general_ci";
	public static final String COLLATION_CP1251_BIN = "cp1251_bin";

	public static final String CHARSET_CP1256 = "cp1256";
	public static final String COLLATION_CP1256_GENERAL_CI = "cp1256_general_ci";
	public static final String COLLATION_CP1256_BIN = "cp1256_bin";

	public static final String CHARSET_CP1257 = "cp1257";
	public static final String COLLATION_CP1257LTLV = "cp1257ltlv";
	public static final String COLLATION_CP1257_LITHUANIAN_CI = "cp1257_lithuanian_ci";
	public static final String COLLATION_CP1257_CS = "cp1257_cs";
	public static final String COLLATION_CP1257_CI = "cp1257_ci";
	public static final String COLLATION_CP1257_GENERAL_CI = "cp1257_general_ci";
	public static final String COLLATION_CP1257_BIN = "cp1257_bin";

	public static final String CHARSET_CP850 = "cp850";
	public static final String COLLATION_CP850_GENERAL_CI = "cp850_general_ci";
	public static final String COLLATION_CP850_BIN = "cp850_bin";

	public static final String CHARSET_CP852 = "cp852";
	public static final String COLLATION_CP852_GENERAL_CI = "cp852_general_ci";
	public static final String COLLATION_CP852_BIN = "cp852_bin";

	public static final String CHARSET_CP866 = "cp866";
	public static final String COLLATION_CP856_GENERAL_CI = "cp856_general_ci";
	public static final String COLLATION_CP856_BIN = "cp856_bin";

	public static final String CHARSET_DEC8 = "dec8";
	public static final String COLLATION_DEC8_SWEDISH_CI = "dec8_swedish_ci";
	public static final String COLLATION_DEC8_BIN = "dec8_bin";

	public static final String CHARSET_GEOSTD8 = "geostd8";
	public static final String COLLATION_GEOSTD8_GENERAL_CI = "geostd8_general_ci";
	public static final String COLLATION_GEOSTD8_BIN = "geostd8_bin";

	public static final String CHARSET_GREEK = "greek";
	public static final String COLLATION_GREEK_GENERAL_CI = "greek_general_ci";
	public static final String COLLATION_GREEK_BIN = "greek_bin";

	public static final String CHARSET_HEBREW = "hebrew";
	public static final String COLLATION_HEBREW_GENERAL_CI = "hebrew_general_ci";
	public static final String COLLATION_HEBREW_BIN = "hebrew_bin";

	public static final String CHARSET_HP8 = "hp8";
	public static final String COLLATION_HP8_ENGLISH_CI = "hp8_english_ci";
	public static final String COLLATION_HP8_BIN = "hp8_bin";

	public static final String CHARSET_KEYBCS2 = "keybcs2";
	public static final String COLLATION_KEYBCS2_GENERAL_CI = "keybcs2_general_ci";
	public static final String COLLATION_KEYBCS2_BIN = "keybcs2_bin";

	public static final String CHARSET_KOI8R = "koi8r";
	public static final String COLLATION_KOI8R_GENERAL_CI = "koi8u_general_ci";
	public static final String COLLATION_KOI8R_BIN = "koi8u_bin";

	public static final String CHARSET_KOI8U = "koi8u";
	public static final String COLLATION_KOI8U_GENERAL_CI = "koi8u_general_ci";
	public static final String COLLATION_KOI8U_BIN = "koi8u_bin";

	public static final String CHARSET_LATIN1 = "latin1";
	public static final String COLLATION_LATIN1_SPANISH_CI = "latin1_spanish_ci";
	public static final String COLLATION_LATIN1_DANISH_CI = "latin1_danish_ci";
	public static final String COLLATION_LATIN1_SWEDISH_CI = "latin1_swedish_ci";
	public static final String COLLATION_LATIN1_GERMAN1_CI = "latin1_german1_ci";
	public static final String COLLATION_LATIN1_GETMAN2_CI = "latin1_german2_ci";
	public static final String COLLATION_LATIN1_GENERAL_CI = "latin1_general_ci";
	public static final String COLLATION_LATIN1_GENERAL_CS = "latin1_general_cs";
	public static final String COLLATION_LATIN1_BIN = "latin1_bin";

	public static final String CHARSET_LATIN2 = "latin2";
	public static final String COLLATION_LATIN2_CZECH_CI = "latin2_czech_ci";
	public static final String COLLATION_LATIN2_HUNGARIAN_CI = "latin2_hungarian_ci";
	public static final String COLLATION_LATIN2_CROATIAN_CI = "latin2_croatian_ci";
	public static final String COLLATION_LATIN2_GENERAL_CI = "latin2_general_ci";
	public static final String COLLATION_LATIN2_BIN = "latin2_bin";

	public static final String CHARSET_LATIN5 = "latin5";
	public static final String COLLATION_LATIN5_GENERAL_CI = "latin5_turkish_ci";
	public static final String COLLATION_LATIN5_BIN = "latin5_bin";

	public static final String CHARSET_LATIN7 = "latin7";
	public static final String COLLATION_LATIN7_ESTONIAN_CS = "latin7_estonian_cs";
	public static final String COLLATION_LATIN7_GENERAL_CI = "latin7_general_ci";
	public static final String COLLATION_LATIN7_GENERAL_CS = "latin7_general_cs";
	public static final String COLLATION_LATIN7_BIN = "latin7_bin";

	public static final String CHARSET_MACCE = "macce";
	public static final String COLLATION_MACCE_CI_AI = "macce_ci_ai";
	public static final String COLLATION_MACCE_GENERAL_CI = "macce_general_ci";
	public static final String COLLATION_MACCE_CI = "macce_ci";
	public static final String COLLATION_MACCE_CS = "macce_cs";
	public static final String COLLATION_MACCE_BIN = "macce_bin";

	public static final String CHARSET_MACROMAN = "macroman";
	public static final String COLLATION_MACROMAN_GENERAL_CI = "macroman_general_ci";
	public static final String COLLATION_MACROMAN_CI_AI = "macroman_ci_ai";
	public static final String COLLATION_MACROMAN_CI = "macroman_ci";
	public static final String COLLATION_MACROMAN_CS = "macroman_cs";
	public static final String COLLATION_MACROMAN_BIN = "macroman_bin";

	public static final String CHARSET_SWE7 = "swe7";
	public static final String COLLATION_SWE7_SWEDISH_CI = "swe7_swedish_ci";
	public static final String COLLATION_SWE7_BIN = "swe7_bin";

	public static final String CHARSET_DEFAULT = CHARSET_LATIN1;
	public static final String COLLATION_DEFAULT = COLLATION_LATIN1_SWEDISH_CI;

	private static String DIRECTORY_WINDOWS = "C:\\Programme\\MySQL\\MySQL Server 5.0";
	private static final String DIRECTORY_MAC = "/usr/local/mysql/share/charsets";
	private static final String DIRECTORY_DEFAULT = "/usr/share/mysql/charsets";

	public static final String CHARSET_DIR = DIRECTORY_DEFAULT;

	private final Map<String, MySqlCharset> _charsets;

	public static MySqlCollatorFactory parse(final String path) throws IOException, SAXException {
		return parse(new File(path));
	}

	public static MySqlCollatorFactory parse(final File file) throws IOException, SAXException {
		if (file.isDirectory()) {
			return new MySqlCollatorFactory(parseDirectory(file));
		} else {
			return new MySqlCollatorFactory(parse(new FileInputStream(file)));
		}
	}

	public static MySqlCollatorFactory parse(final URL url) throws IOException, SAXException {
		return new MySqlCollatorFactory(parse(url.openStream()));
	}

	private static MySqlCharsetsBean parse(final InputStream in) throws IOException, SAXException {
		return parse(new InputSource(in));
	}

	/**
	 * @return {@link MySqlCollatorFactory} containing all charsets from given
	 *         directory considering all *.xml but Index.xml
	 */
	private static List<MySqlCharsetsBean> parseDirectory(final File dir) throws IOException, SAXException {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("not a directory = " + dir);
		}

		File[] files = dir.listFiles(new CharsetFileFilter());

		List<MySqlCharsetsBean> charsets = new ArrayList<MySqlCharsetsBean>(files.length);
		for (final File file : files) {
			charsets.add(parse(new FileInputStream(file)));
		}
		return charsets;
	}

	public static boolean isValidCharsetDirectory() {
		return getDirectory().isDirectory();
	}

	/**
	 * load MySQL charset files from defaul directory
	 * 
	 * <ul> <li>Windows: value of {@literal PROPERTY_MYSQL_HOME_DIR}
	 * \share\charsets or {@literal DIRECTORY_WINDOWS}\share\charsets</li>
	 * <li>Mac: value of {@literal PROPERTY_MYSQL_DIR} or
	 * {@literal DIRECTORY_MAC} <li>Others: value of
	 * {@literal PROPERTY_MYSQL_DIR} or {@literal DIRECTORY_DEFAULT}</li> </ul>
	 * 
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public static MySqlCollatorFactory parseDefaultDirectory() throws IOException, SAXException {
		final File dir = getDirectory();

		if (dir.isDirectory()) {
			return parse(dir);
		} else {
			if ("windows".equals(SystemProperty.OS_FAMILY.get())) {
				throw new RuntimeException(
						String.format(
								"%s ist not a directory, please check your %s system property (current value %s) for a valid MySQL charsets directory",
								dir, PROPERTY_MYSQL_HOME_DIR, System.getProperty(PROPERTY_MYSQL_HOME_DIR)));
			} else {
				throw new RuntimeException(
						String.format(
								"%s ist not a directory, please check your %s system property (current value %s) for a valid MySQL charsets directory",
								dir, PROPERTY_MYSQL_DIR, System.getProperty(PROPERTY_MYSQL_DIR)));
			}
		}

	}

	private static File getDirectory() {
		final String os = SystemProperty.OS_FAMILY.get();

		if ("windows".equals(os)) {
			return new File(System.getProperties().getProperty(PROPERTY_MYSQL_HOME_DIR, DIRECTORY_WINDOWS)
					+ "\\share\\charsets");
		} else {
			return new File(System.getProperties().getProperty(PROPERTY_MYSQL_DIR,
					"mac".equals(os) ? DIRECTORY_MAC : DIRECTORY_DEFAULT));
		}
	}

	private static MySqlCharsetsBean parse(final InputSource source) throws IOException, SAXException {
		return (MySqlCharsetsBean) new CharsetXmlDigester().parse(source);
	}

	private MySqlCollatorFactory(final MySqlCharsetsBean... charsetBeans) {
		this(Arrays.asList(charsetBeans));
	}

	public MySqlCollatorFactory(List<MySqlCharsetsBean> charsetBeans) {
		_charsets = new HashMap<String, MySqlCharset>();

		for (MySqlCharsetsBean charsets : charsetBeans) {
			addCharsets(charsets);
		}
	}

	/**
	 * @return {@link MySqlCharset} for given charset name
	 * @throws IllegalArgumentException
	 *             for unknown charset name
	 */
	public MySqlCharset getCharset(final String charset) {
		final MySqlCharset cset = _charsets.get(charset);
		if (cset == null) {
			throw new IllegalArgumentException("charset not available: " + charset);
		}
		return cset;
	}

	/**
	 * @return {@link MySqlCollation} for given charset and collation name
	 * @throws IllegalArgumentException
	 *             for unknown charset or collation name
	 * @see #getCharset(String)
	 * @see MySqlCharset#getCollation(String)
	 */
	public MySqlCollation getCollation(String charset, String collation) {
		return getCharset(charset).getCollation(collation);
	}

	/**
	 * @return {@link MySqlCollator} for given charset and collation name
	 * @throws IllegalArgumentException
	 *             for unknown charsets of charset/collation combinations
	 * 
	 * @see #getCollation(String, String)
	 * @see MySqlCollation#getCollator()
	 */
	public MySqlCollator getCollator(final String charset, final String collation) {
		return getCollation(charset, collation).getCollator();
	}

	/**
	 * @return charset {@value #CHARSET_DEFAULT}, collation
	 *         {@value #COLLATION_DEFAULT}
	 */
	public MySqlCollator getDefaultCollator() {
		return getCollator(CHARSET_DEFAULT, COLLATION_DEFAULT);
	}

	private void addCharsets(final MySqlCharsetsBean charsets) {
		for (Map.Entry<String, MySqlCharsetBean> e : charsets.getCharsets().entrySet()) {
			_charsets.put(e.getKey(), new MySqlCharset(e.getValue()));
		}
	}

	@Override
	public String toString() {
		return "MySqlCollatorFactory [charsets=" + _charsets + "]";
	}

	private static class CharsetFileFilter implements FilenameFilter {

		@Override
		public boolean accept(final File dir, final String name) {
			return name.endsWith(".xml") && !"Index.xml".equals(name);
		}

	}
}
