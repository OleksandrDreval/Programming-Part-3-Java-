package ua.nure.ice.bookcatalog.practical4.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nure.ice.bookcatalog.practical4.model.Book;
import ua.nure.ice.bookcatalog.practical4.service.CatalogService;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final CatalogService catalogService;

    public BookController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(catalogService.findAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable long id) {
        return catalogService.findAllBooks().stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book bookRequest) {
        try {
            Book created = catalogService.addBook(
                    bookRequest.getTitle(),
                    bookRequest.getAuthor(),
                    bookRequest.getPublicationYear(),
                    bookRequest.getGenre()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable long id, @RequestBody Book bookRequest) {
        try {
            Book updated = catalogService.updateBook(
                    id,
                    bookRequest.getTitle(),
                    bookRequest.getAuthor(),
                    bookRequest.getPublicationYear(),
                    bookRequest.getGenre()
            );
            return ResponseEntity.ok(updated);
        } catch (ua.nure.ice.bookcatalog.practical4.exception.BookNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable long id) {
        try {
            catalogService.removeBook(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
