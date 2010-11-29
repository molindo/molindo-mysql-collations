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
package at.molindo.mysqlcollations;

import java.io.Serializable;

public class MySqlCharacterMap implements Serializable {
	private static final long serialVersionUID = 1L;

	private String _map;
	private transient int[] _values;

	public String getMap() {
		return _map;
	}

	public void setMap(final String map) {
		_map = map;
		_values = null;
	}

	protected int[] getValues() {
		if (_values == null && _map != null) {
			_values = parseMapValues(_map);
		}
		return _values;
	}

	public int getValue(final int index) {
		if (_map == null) {
			throw new IllegalStateException("no map set");
		}

		final int values[] = getValues();
		if (index < 0 || index >= values.length) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		return values[index];
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
