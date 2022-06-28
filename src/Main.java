import java.io.FileNotFoundException;

public class Main {
	/* Entry point of the application */
    public static void main(String[] args) throws FileNotFoundException {
        LexicalAnalyzer surly = new LexicalAnalyzer();
        surly.run("args[0]");
    }
}