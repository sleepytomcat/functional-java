package chapter04;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class exercise03 {
	public static void main(String... args) {
		List<Integer> list = list(1,3,5,7,9);
		System.out.println(foldLeft(0, list, x -> y -> y + x));

		TailCall<Integer> tc = foldLeftWithTailCall(0, list, x -> y -> y + x);
		while (tc.isSuspended())
			tc = tc.resume();
		System.out.println(tc.eval());
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

	public static <T,U> U foldLeft(U accumulator, List<T> list, final Function<U, Function<T, U>> foldingFunction) {
		if (list.size() == 0)
			return accumulator;
		else
			return foldLeft(foldingFunction.apply(accumulator).apply(head(list)), tail(list), foldingFunction);
	}

	public static <T,U> TailCall<U> foldLeftWithTailCall(U accumulator, List<T> list, final Function<U, Function<T, U>> foldingFunction) {
		if (list.size() == 0)
			return TailCall.ret(accumulator);
		else
			return foldLeftWithTailCall(foldingFunction.apply(accumulator).apply(head(list)), tail(list), foldingFunction);
	}

	interface TailCall<T> {
		boolean isSuspended();
		T eval();
		TailCall<T> resume();

		static <T> Return<T> ret(T s) { return new Return<>(s);}
		static <T> Suspend<T> sus(Supplier<TailCall<T>> s) { return new Suspend<>(s);}
	}

	static class Return<T> implements TailCall<T> {
		Return(T value) {this.value = value;}
		public boolean isSuspended() {return false;}
		public T eval() {return value;}
		public TailCall<T> resume() {throw new IllegalStateException("Return has no resume");}

		private T value;
	}

	static class Suspend<T> implements TailCall<T> {
		public Suspend(Supplier<TailCall<T>> supplier) {this.supplier = supplier;}
		public boolean isSuspended() {return true;}
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
