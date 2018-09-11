package chapter02;

import java.util.function.Function;

public class exercise08 {
	public static void main(String[] args) {
		Function<Integer, Function<Double, String>> partial = x -> y -> x.toString() + y.toString();
		Function<Integer, String> partiallyApplied = partialApply(-101.0101, partial);

		System.out.println(partiallyApplied.apply(3)); // expected: "3-101.0101"
	}

	// in Java there's no way to declare 'polymorphic' field, so we're using a trick with static method
	static <A, B, C> Function<A, C> partialApply(B b, Function<A, Function <B, C>> f) {
		return a -> f.apply(a).apply(b);
	}
}
