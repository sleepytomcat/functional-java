package chapter02;

import java.util.function.Function;

public class exercise12 {
	static final Function<Integer, Integer> factorialFunction
			= x -> x == 0 ? 1 : exercise12.factorialFunction.apply(x - 1) * x;
	public static void main(String[] args) {
		System.out.println(factorial(3));
		System.out.println(factorialFunction.apply(3));
	}

	static int factorial(int x) {
		return x == 0? 1 : factorial(x - 1) * x;
	}
}


