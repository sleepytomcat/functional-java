package chapter02;

import java.util.function.Function;

public class exercise02 {
	public static void main(String[] args) {
		Function<Integer, Integer> triple = x -> x * 3;
		Function<Integer, Integer> square = x -> x * x;

		Function<Integer, Integer> composed = compose(square, triple);
		System.out.println(composed.apply(3)); // expected: 81
	}

	static Function<Integer, Integer> compose(Function<Integer, Integer> f, Function<Integer, Integer> g) {
		return x -> f.apply(g.apply(x));
	}
}
