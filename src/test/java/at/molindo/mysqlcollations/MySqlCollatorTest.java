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

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.CollationKey;
import java.text.Collator;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

public class MySqlCollatorTest {

	private static MySqlCollator DEFAULT, GERMAN;

	@BeforeClass
	public static void load() throws IOException, SAXException {
		final MySqlCollatorFactory factory = MySqlCollatorFactory.parseDefaultDirectory();
		DEFAULT = factory.getDefaultCollator();
		GERMAN = factory.getCollator("latin1", "latin1_german1_ci");
	}

	@Test
	public void testCompare() {
		assertTrue(e("foobar", "Foobar"));
		assertFalse(e("foobar", "foo bar"));
	}

	private boolean e(final String source, final String target) {
		return DEFAULT.equals(source, target);
	}

	private CollationKey k(final String source) {
		return DEFAULT.getCollationKey(source);
	}

	@Test
	public void testGetCollationKey() {
		final CollationKey foobar = k("foobar");
		final CollationKey fooBar = k("foo bar");
		final CollationKey foobar2 = k("Foobar");

		assertEquals(foobar, foobar2);
		assertFalse(fooBar.equals(foobar));
	}

	@Test
	public void testNormalize() {
		assertEquals("FOOBAR", GERMAN.normalize("foobar"));
		assertEquals("FOOBAR", GERMAN.normalize("Foobar"));
		assertEquals("FOOBAR", GERMAN.normalize("fo\u00F6bar"));
		assertEquals("FOOBAR", GERMAN.normalize("fo\u00F3bar"));
	}

	// @Test
	public void testCompareSpeed() {

		final Collator collator = Collator.getInstance();
		collator.setStrength(Collator.PRIMARY);

		final String s1 = "foobarbazquxbar";
		final String s2 = "foobarbazquxb\u00E1r";

		{
			collator.compare(s1, s2); // warmup

			final long start = System.currentTimeMillis();

			for (int i = 0; i < 1000000; i++) {
				collator.compare(s1, s2);
			}

			System.out.println("java.text.Collator: "
					+ (System.currentTimeMillis() - start) + "ms");
		}
		{
			DEFAULT.compare(s1, s2); // warmup

			final long start = System.currentTimeMillis();

			for (int i = 0; i < 1000000; i++) {
				DEFAULT.compare(s1, s2);
			}

			System.out.println("MySqlCollator: "
					+ (System.currentTimeMillis() - start) + "ms");
		}
	}

	// @Test
	public void testKeyCompareSpeed() {

		final Collator collator = Collator.getInstance();
		collator.setStrength(Collator.PRIMARY);

		final String s1 = "foobarbazquxbar";
		final String s2 = "foobarbazquxb\u00E1r";

		{
			final CollationKey k1 = collator.getCollationKey(s1);
			final CollationKey k2 = collator.getCollationKey(s2);

			k1.compareTo(k2); // warmup

			long value = 0;

			final long start = System.currentTimeMillis();

			for (int i = 0; i < 100000000; i++) {
				value += k1.compareTo(k2);
			}

			System.out.println("java.text.CollationKey: "
					+ (System.currentTimeMillis() - start) + "ms (value = " + value
					+ ")");
		}
		{
			final CollationKey k1 = DEFAULT.getCollationKey(s1);
			final CollationKey k2 = DEFAULT.getCollationKey(s2);

			k1.compareTo(k2); // warmup

			long value = 0;

			final long start = System.currentTimeMillis();

			for (int i = 0; i < 100000000; i++) {
				value += k1.compareTo(k2);
			}

			System.out.println("MySqlCollationKey: "
					+ (System.currentTimeMillis() - start) + "ms (value = " + value
					+ ")");
		}
	}
}
