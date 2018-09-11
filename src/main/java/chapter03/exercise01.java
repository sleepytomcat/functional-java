package chapter03;
import java.util.function.Function;
import java.util.regex.Pattern;

public class exercise01 {
	static final Pattern emailPattern = Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$");

	static Function<String, Result> emailChecker = s -> {
		if (s == null) {
			return Result.failure("email must not be null");
		} else if (s.length() == 0) {
			return Result.failure("email must not be empty");
		} else if (emailPattern.matcher(s).matches()) {
			return Result.success(s);
		} else {
			return Result.failure("email " + s + " is invalid.");
		}
	};

	public static void main(String... args) {
		Effect<String> success = exercise01::sendVerificationMail;
		Effect<String> failure = exercise01::logError;
		emailChecker.apply("this.is@my.email").bind(success, failure);
		emailChecker.apply(null).bind(success, failure);
		emailChecker.apply("").bind(success, failure);
		emailChecker.apply("john.doe@acme.com").bind(success, failure);
	}

	private static void logError(String s) {
		System.err.println("Error message logged: " + s);
	}

	private static void sendVerificationMail(String s) {
		System.out.println("Mail sent to " + s);
	}
}

interface Result<T> {
	void bind(Effect<T> success, Effect<T> failure);

	static Result<String> failure(String message) {return new Failure(message);}
	static Result<String> success(String email) {return new Success(email);}

	class Success implements Result<String>{
		public Success(String email) {this.email = email;}
		public void bind(Effect<String> success, Effect<String> failure) {success.apply(email);}
		String email;
	};

	class Failure implements Result<String> {
		private final String errorMessage;
		public Failure(String errorMessage) {this.errorMessage = errorMessage;}
		public void bind(Effect<String> success, Effect<String> failure) {failure.apply(errorMessage);
		}
	}
}

interface Effect<T> {
	void apply(T t);
}
