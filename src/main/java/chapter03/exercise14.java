package chapter03;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class exercise14 {
	public static void main(String... args) {
		List<Integer> list = range(3,15);
		System.out.println(list);
	}

	@SafeVarargs
	public static <T> List<T> list(T... elements) {
		return Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(elements, elements.length)));
	}

	public static <T> List<T> append(T element, List<T> list) {
		List<T> result = copy(list);
		result.add(element);
		return result;
	}

	private static <T> List<T> copy(List<T> src) {
		return new ArrayList<>(src);
	}

	public static <T,U> U foldLeft(U identityElement, List<T> list, final Function<U, Function<T, U>> foldingFunction) {
		U result = identityElement;
		for (T value: list) {
			result = foldingFunction.apply(result).apply(value);
		}

		return result;
	}

	public static <T> List<T> prepend(T element, List<T> list) {
		return foldLeft(list(element), list, a -> b -> append(b, a));
	}

	public static List<Integer> range(int start, int end) {
		return end > start
				? prepend(start, range(start+1, end))
				: list();
	}
}