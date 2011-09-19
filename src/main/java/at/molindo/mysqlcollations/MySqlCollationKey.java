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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.CollationKey;
import java.util.Arrays;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

public class MySqlCollationKey extends CollationKey {

	private final byte[] _weights;
	private final int _hash;

	private MySqlCollationKey(final String source, final byte[] weights, final int hash) {
		super(source);
		_weights = weights;
		_hash = hash;
	}

	public MySqlCollationKey(final String source, final MySqlCollation collation) {
		super(source);
		_weights = new byte[source.length()];
		int hash = 1;

		final int prime = 31;
		for (int i = 0; i < source.length(); i++) {
			_weights[i] = collation.getWeight(source.charAt(i));
			hash = prime * hash + _weights[i];
		}
		_hash = hash;
	}

	@Override
	public int hashCode() {
		return _hash;
	}

	@SuppressWarnings(value = "ES_COMPARING_STRINGS_WITH_EQ", justification = "performance optimization only")
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof MySqlCollationKey)) {
			return false;
		}
		final MySqlCollationKey other = (MySqlCollationKey) obj;
		if (other.getSourceString() == getSourceString()) {
			return true;
		}
		final byte[] oWeights = other._weights;

		if (oWeights.length != _weights.length) {
			return false;
		}

		for (int i = 0; i < _weights.length; i++) {
			final byte a = _weights[i];
			final byte b = oWeights[i];

			if (a != b) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int compareTo(final CollationKey o) {
		final byte[] oWeights = ((MySqlCollationKey) o)._weights;

		final int min = _weights.length <= oWeights.length ? _weights.length : oWeights.length;

		for (int i = 0; i < min; i++) {
			final byte a = _weights[i];
			final byte b = oWeights[i];

			if (a != b) {
				return a - b;
			}
		}
		// shorter is first
		return _weights.length - oWeights.length;
	}

	@Override
	public byte[] toByteArray() {
		return Arrays.copyOf(_weights, _weights.length);
	}

	@Override
	public String toString() {
		return "MySqlCollationKey [source=" + getSourceString() + "]";
	}

	public final Object writeReplace() throws ObjectStreamException {
		return new SerializedKey(getSourceString(), _weights, _hash);
	}

	private static final class SerializedKey implements Serializable {
		private static final long serialVersionUID = 1L;

		private final String _source;
		private final byte[] _weights;
		private final int _hash;

		public SerializedKey(final String source, final byte[] weights, final int hash) {
			_source = source;
			_weights = weights;
			_hash = hash;
		}

		public final Object readResolve() throws ObjectStreamException {
			return new MySqlCollationKey(_source, _weights, _hash);
		}
	}
}
