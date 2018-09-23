package chapter04;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class exercise05 {
	public static void main(String... args) {
		List<String> characters = list("1", "2", "3");
		String combined = foldRight("0", characters, x -> y -> addIS(x, y));
		System.out.println(combined);

		combined = foldRightStackSafe("0", reverseWithFoldLeft(characters), x -> y -> addIS(x, y)).eval();
		System.out.println(combined);
	}

	public static String addIS(String i, String s) {
		return "(" + i + " + " + s + ")";
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
			return copy(src.subList(1, src.size()));
	}

	private static <T> List<T> copy(List<T> src) {
		return new ArrayList<>(src);
	}

	public static <T> List<T> append(T element, List<T> list) {
		List<T> result = copy(list);
		result.add(element);
		return result;
	}

	public static <T,U> U foldRight(U identityElement, List<T> list, final Function<T, Function<U, U>> foldingFunction) {
		return list.isEmpty()
				? identityElement
				: foldingFunction.apply(head(list)).apply(foldRight(identityElement, tail(list), foldingFunction));
	}

	public static <T,U> TailCall<U> foldRightStackSafe(U accumulator, List<T> list, final Function<T, Function<U, U>> foldingFunction) {
		return list.isEmpty()
				? TailCall.ret(accumulator)
				: TailCall.sus(() -> foldRightStackSafe(foldingFunction.apply(head(list)).apply(accumulator), tail(list), foldingFunction));
	}

	public static <T> List<T> reverseWithFoldLeft(List<T> list) {
		return foldLeftStackSafe(list(), list, (List <T> x) -> y -> exercise05.prepend(y, x)).eval();
	}

	public static <T> List<T> prepend(T element, List<T> list) {
		return foldLeftStackSafe(list(element), list, a -> b -> append(b, a)).eval();
	}

	public static <T,U> TailCall<U> foldLeftStackSafe(U accumulator, List<T> list, final Function<U, Function<T, U>> foldingFunction) {
		if (list.size() == 0)
			return TailCall.ret(accumulator);
		else
			return TailCall.sus(() -> foldLeftStackSafe(foldingFunction.apply(accumulator).apply(head(list)), tail(list), foldingFunction));
	}

	interface TailCall<T> {
		boolean isSuspended();

		T eval();

		TailCall<T> resume();

		static <T> Return<T> ret(T s) {
			return new Return<>(s);
		}

		static <T> Suspend<T> sus(Supplier<TailCall<T>> s) {
			return new Suspend<>(s);
		}
	}

	static class Return<T> implements TailCall<T> {
		Return(T value) {
			this.value = value;
		}

		public boolean isSuspended() {
			return false;
		}

		public T eval() {
			return value;
		}

		public TailCall<T> resume() {
			throw new IllegalStateException("Return has no resume");
		}

		private T value;
	}

	static class Suspend<T> implements TailCall<T> {
		public Suspend(Supplier<TailCall<T>> supplier) {
			this.supplier = supplier;
		}

		public boolean isSuspended() {
			return true;
		}

		public T eval() {
			TailCall<T> tc = supplier.get();
			while (tc.isSuspended()) {
				tc = tc.resume();
			}
			return tc.eval();
		}

		public TailCall<T> resume() {
			return supplier.get();
		}

		private Supplier<TailCall<T>> supplier;
	}
}