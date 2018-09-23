package chapter03;

import java.util.*;

public class exercise04 {
	public static void main(String... args) {
		List<Integer> list = list(1,2,3,4,5);
		Integer head = head(list);
		List<Integer> tail = tail(list);

		List<Integer> emptyList = list();

		try {
			Integer headOfEmptyList = head(emptyList);
		}
		catch (Throwable ex) {
			System.out.println(ex.getMessage());
		}

		try {
			List<Integer> tailOfEmptyList = tail(emptyList);
		}
		catch (Throwable ex) {
			System.out.println(ex.getMessage());
		}
	}

	public static <T> List<T> list() {
		return Collections.emptyList();
	}

	public static <T> List<T> list(T value) {
		return Collections.singletonList(value);
	}

	public static <T> List<T> list(Collection<T> elements) {
		return Collections.unmodifiableList(new ArrayList<>(elements));
	}

	@SafeVarargs
	public static <T> List<T> list(T... elements) {
		return Collections.unmodifiableList(
				Arrays.asList(
						Arrays.copyOf(
								elements, elements.length
						)
				)
		);
	}

	public static <T> T head(List<T> list) {
		if (list.size() >= 1)
			return list.get(0);
		else
			throw new IllegalStateException("head of empty list");
	}

	public static <T> List<T> tail(List<T> src) {
		if (src.size() == 0)
			throw new IllegalStateException("tail of empty list");
		else
			return exercise04.copy(src.subList(1, src.size()));
	}

	private static <T> List<T> copy(List<T> src) {
		return new ArrayList<T>(src);
	}
}