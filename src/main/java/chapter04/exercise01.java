package chapter04;

public class exercise01 {
	public static void main(String... args) {
		System.out.println("Starting...");
		System.out.println(fibonacci(50));
		System.out.println(fibonacciTailTesursion(50, 1, 1));
	}

	static int fibonacci(int number) {
		if (number == 0 || number == 1)
			return 1;
		else
			return fibonacci(number - 1) + fibonacci(number - 2);
	}

	static int fibonacciTailTesursion(int number, int accumulatorA, int accumulatorB) {
		if (number == 0)
			return accumulatorB;
		else if (number == 1)
			return accumulatorA;
		else
			return fibonacciTailTesursion(number-1, accumulatorA + accumulatorB, accumulatorA);
	}
}
