package chapter03;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class exercise06 {
	public static void main(String... args) {
		List<Integer> list = list(1,2,3,4,5);
		System.out.println(exercise06.foldLeft("0", list, x -> y -> addSI(x, y)));
	}

	@SafeVarargs
	public static <T> List<T> list(T... elements) {
		return Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(elements, elements.length)));
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
			return exercise06.copy(src.subList(1, src.size()));
	}

	private static <T> List<T> copy(List<T> src) {
		return new ArrayList<T>(src);
	}

	static String addSI(String s, Integer i) {
		return "(" + s + " + " + i + ")";
	}

	public static <T,U> U foldLeft(U identityElement, List<T> list, final Function<U, Function<T, U>> foldingFunction) {
		U result = identityElement;
		for (T value: list) {
			result = foldingFunction.apply(result).apply(value);
		}

		return result;
	}
}