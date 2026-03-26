package main;

import model.Book;
import model.BasicBook;
import decorator.RatingDecorator;
import decorator.ReviewDecorator;
import decorator.TagDecorator;
import service.FileScanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FileScanner fileScanner = new FileScanner();

        System.out.println("=== eBook Organizer CLI ===");
        System.out.print("Enter folder path to scan (default 'test_books'): ");
        String path = scanner.nextLine();
        if (path.trim().isEmpty()) {
            path = "test_books";
        }

        List<String> filenames = fileScanner.scanFiles(path);

        if (filenames.isEmpty()) {
            System.out.println("No .pdf or .epub files found in: " + path);
            return;
        }

        System.out.println("\nFound " + filenames.size() + " books:");
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < filenames.size(); i++) {
            System.out.println((i + 1) + ". " + filenames.get(i));
            books.add(new BasicBook(filenames.get(i)));
        }

        System.out.println("\n--- Demonstrating Decorator Pattern ---");
        System.out.println("Enriching the first book with metadata...");

        Book book = books.get(0);
        
        // Dynamic decoration
        book = new RatingDecorator(book, 5);
        book = new ReviewDecorator(book, "Excellent resource for Java developers!");
        book = new TagDecorator(book, Arrays.asList("Java", "Programming", "Best Practices"));

        System.out.println("\nFinal Enriched Book Detail:");
        System.out.println(book.getDescription());

        System.out.println("\nEnriching the second book (if exists) with different metadata...");
        if (books.size() > 1) {
            Book book2 = books.get(1);
            book2 = new RatingDecorator(book2, 4);
            book2 = new TagDecorator(book2, Arrays.asList("Clean Code", "Software Engineering"));
            System.out.println(book2.getDescription());
        }

        System.out.println("\nDone.");
    }
}
