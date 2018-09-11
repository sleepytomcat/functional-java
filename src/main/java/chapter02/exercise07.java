package chapter02;

import java.util.function.Function;

public class exercise07 {
	public static void main(String[] args) {
		Function<Integer, Function<Double, String>> partial = x -> y -> x.toString() + y.toString();
		Function<Double, String> partiallyApplied = partialApply(101, partial);

		System.out.println(partiallyApplied.apply(-3.1415926)); // expected: "101-3.1415926"
	}

	// in Java there's no way to declare 'polymorphic' field, so we're using a trick with static method
	static <A, B, C> Function<B, C> partialApply(A a, Function<A, Function <B, C>> f) {
		return f.apply(a);
	}
}
