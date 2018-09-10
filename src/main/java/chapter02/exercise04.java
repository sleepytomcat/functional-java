package chapter02;

import java.util.function.Function;

public class exercise04 {
	public static void main(String[] args) {
		Function<Integer, Integer> triple = x -> x * 3;
		Function<Integer, Integer> square = x -> x * x;

		Function<Function<Integer, Integer>,
				Function<Function<Integer, Integer>, Function<Integer, Integer>>>
				compose = f -> g -> x -> f.apply(g.apply(x));

		Function<Integer, Integer> composed = compose.apply(square).apply(triple);
		System.out.println(composed.apply(3)); // expected: 81
	}
}
