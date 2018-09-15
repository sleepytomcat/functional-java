package chapter03;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class exercise13 {
	public static void main(String... args) {
		List<Integer> list = range(3,7);
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

	public static <T> List<T> unfold(T seed, Function<T,T> next, Function<T, Boolean> continueCondition) {
		List<T> result = list();
		T current = seed;
		while (continueCondition.apply(current)) {
			result = append(current, result);
			current = next.apply(current);
		}
		return result;
	}

	public static List<Integer> range(int start, int end) {
		return unfold(start, x -> x + 1, x -> x < end);
	}
}