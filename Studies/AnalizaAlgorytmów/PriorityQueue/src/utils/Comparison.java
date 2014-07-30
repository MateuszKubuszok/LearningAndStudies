package utils;

import java.util.HashSet;
import java.util.Set;

public enum Comparison {
	EQUAL_TO {
		@Override
		public <Key extends Comparable<Key>> boolean compare(Key a, Key b) {
			return compareNullSafe(a, b) == 0;
		}
	},
	GREATER_THAN {
		@Override
		public <Key extends Comparable<Key>> boolean compare(Key a, Key b) {
			return compareNullSafe(a, b) > 0;
		}
	},
	GREATER_OR_EQUAL_TO {
		@Override
		public <Key extends Comparable<Key>> boolean compare(Key a, Key b) {
			return compareNullSafe(a, b) >= 0;
		}
	},
	LESSER_THAN {
		@Override
		public <Key extends Comparable<Key>> boolean compare(Key a, Key b) {
			return compareNullSafe(a, b) < 0;
		}
	},
	LESSER_OR_EQUAL_TO {
		@Override
		public <Key extends Comparable<Key>> boolean compare(Key a, Key b) {
			return compareNullSafe(a, b) <= 0;
		}
	};

	private static Set<ComparisonEvent> EVENT_HANDLERS = new HashSet<ComparisonEvent>();

	public abstract <Key extends Comparable<Key>> boolean compare(Key a, Key b);

	public static <Key extends Comparable<Key>> boolean is(Key a,
			Comparison comparison, Key b) {
		return comparison.compare(a, b);
	}

	public static void registerHandler(ComparisonEvent handler) {
		if (handler != null)
			EVENT_HANDLERS.add(handler);
	}

	private static <Key extends Comparable<Key>> int compareNullSafe(Key a,
			Key b) {
		for (ComparisonEvent handler : EVENT_HANDLERS)
			handler.onCompare(a, b);
		if (a == null) {
			if (b == null)
				return 0;
			return -1;
		}
		if (b == null)
			return 1;
		return a.compareTo(b);
	}
}