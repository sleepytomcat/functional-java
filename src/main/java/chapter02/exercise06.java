package chapter02;

import java.util.function.Function;

public class exercise06 {
	public static void main(String[] args) {
		Function<Integer, Character> triple = x -> 'a';
		Function<Character, Boolean> square = x -> true;

		Function<Integer, Boolean> composed = exercise06.<Integer, Character, Boolean>andThen().apply(triple).apply(square);
		System.out.println(composed.apply(3)); // expected: true
	}

	// in Java there's no way to declare 'polymorphic' field, so we're using a trick with static method
	static <A, B, C> Function<Function<A, B>, Function<Function<B, C>, Function<A, C>>> andThen() {
		return f -> g -> x -> g.apply(f.apply(x));
	}
}
