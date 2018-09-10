package chapter02;

import java.util.function.Function;

public class exercise01 {
	public static void main(String[] args) {
		Function<Integer, Integer> triple = new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer x) {
				return x * 3;
			}
		};

		Function<Integer, Integer> square = new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer x) {
				return x * x;
			}
		};

		Function<Integer, Integer> composed = compose(square, triple);
		System.out.println(composed.apply(3)); // expected: 81
	}

	static Function<Integer, Integer> compose(Function<Integer, Integer> f, Function<Integer, Integer> g) {
		return new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer x) {
				return f.apply(g.apply(x));
			}
		};
	}

}
