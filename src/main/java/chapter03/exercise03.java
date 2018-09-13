package chapter03;

import java.util.*;

public class exercise03 {
	public static void main(String... args) {
		List<Integer> l1 = list();
		List<Integer> l2 = list(123);
		List<Integer> l3 = list(Arrays.asList(11,22,33,44,55));
		List<Integer> l4 = list(1,2,3,4,5);
	}

	static <T> List<T> list() {
		return Collections.emptyList();
	}

	static <T> List<T> list(T value) {
		return Collections.singletonList(value);
	}

	static <T> List<T> list(Collection<T> elements) {
		return Collections.unmodifiableList(new ArrayList<>(elements));
	}

	@SafeVarargs
	static <T> List<T> list(T... elements) {
		return Collections.unmodifiableList(
				Arrays.asList(
						Arrays.copyOf(
								elements, elements.length
						)
				)
		);
	}
}