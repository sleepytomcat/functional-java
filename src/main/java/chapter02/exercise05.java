package chapter02;

import java.util.function.Function;

public class exercise05 {
	public static void main(String[] args) {
		Function<Integer, Character> triple = x -> 'a';
		Function<Character, Boolean> square = x -> true;

		Function<Integer, Boolean> composed = exercise05.<Integer, Character, Boolean>compose().apply(square).apply(triple);
		System.out.println(composed.apply(3)); // expected: true
	}

	static <U, V, R> Function<Function<V, R>, Function<Function<U, V>, Function<U, R>>> compose() {
		return f -> g -> x -> f.apply(g.apply(x));
	}
}
