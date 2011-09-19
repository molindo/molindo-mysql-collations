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

class MySqlCharsetUtils {
	private MySqlCharsetUtils() {
	}

	public static byte[] toByteArray(String map) {
		int[] values = toIntArray(map);
		if (values == null) {
			return null;
		}
		byte[] bytes = new byte[values.length];
		for (int i = 0; i < values.length; i++) {
			bytes[i] = (byte) values[i];
		}
		return bytes;
	}

	public static char[] toCharArray(String map) {
		int[] values = toIntArray(map);
		if (values == null) {
			return null;
		}
		char[] chars = new char[values.length];
		for (int i = 0; i < values.length; i++) {
			chars[i] = (char) values[i];
		}
		return chars;
	}

	public static int[] toIntArray(String map) {
		if (map == null) {
			return null;
		}

		String[] str = map.split("\\s+");

		final int[] values = new int[str.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = Integer.parseInt(str[i], 16);
		}
		return values;
	}

}
