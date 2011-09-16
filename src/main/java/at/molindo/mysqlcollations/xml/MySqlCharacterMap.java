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

public class MySqlCharacterMap implements Serializable {
	private static final long serialVersionUID = 1L;

	private int[] _values;

	public void setMap(final String map) {
		_values = parseMapValues(map);
	}

	protected int[] getValues() {
		return _values;
	}

	public int getValue(final int index) {
		if (_values == null) {
			throw new IllegalStateException("no map set");
		}

		if (index < 0 || index >= _values.length) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		return _values[index];
	}

	private int[] parseMapValues(final String map) {
		if (map == null) {
			return null;
		}

		final String[] str = map.split("\\s+");
		final int[] values = new int[str.length];

		for (int i = 0; i < values.length; i++) {
			values[i] = Integer.parseInt(str[i], 16);
		}
		return values;
	}
}
