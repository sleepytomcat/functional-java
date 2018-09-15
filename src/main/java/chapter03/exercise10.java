package chapter03;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class exercise10 {
	public static void main(String... args) {
		List<Integer> list = list(1,2,3,4,5);
		System.out.println(list);
		System.out.println(mapWithFoldLeft(list, x -> "'" + x.toString() + "'"));
		System.out.println(mapWithFoldRight(list, x -> "'" + x.toString() + "'"));
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

	public static <T> List<T> prepend(T element, List<T> list) {
		return foldLeft(list(element), list, a -> b -> append(b, a));
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
			return copy(src.subList(1, src.size()));
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

	public static <T,U> List<U> mapWithFoldLeft(List<T> list, Function<T,U> mapping) {
		return foldLeft(list(), list, x -> y -> append(mapping.apply(y), x));
	}

	public static <T,U> U foldRight(U identityElement, List<T> list, final Function<T, Function<U, U>> foldingFunction) {
		return list.isEmpty()
				? identityElement
				: foldingFunction.apply(head(list)).apply(foldRight(identityElement, tail(list), foldingFunction));
	}

	public static <T,U> List<U> mapWithFoldRight(List<T> list, Function<T,U> mapping) {
		return foldRight(list(), list, x -> y -> prepend(mapping.apply(x), y));
	}
}