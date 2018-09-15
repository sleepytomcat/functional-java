package chapter03;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class exercise11 {
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

	static List<Integer> range(int start, int end) {
		List<Integer> result = list();
		int current = start;
		while (current < end) {
			result = append(current, result);
			current++;
		}
		return result;
	}
}