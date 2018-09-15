package chapter03;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class exercise05 {
	public static void main(String... args) {
		List<Integer> list = list(1,2,3,4,5);
		System.out.println(foldRight(0, list, x -> y -> x + y));
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
			return exercise05.<T>copy(src.subList(1, src.size()));
	}

	private static <T> List<T> copy(List<T> src) {
		return new ArrayList<T>(src);
	}

	public static Integer foldRight(Integer identityElement, List<Integer> list, final Function<Integer, Function<Integer, Integer>> foldingFunction) {
		if (list.size() == 0)
			return identityElement;
		else
			return foldingFunction.apply(head(list)).apply(foldRight(identityElement, tail(list), foldingFunction));
	}
}