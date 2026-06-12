package org.acme.book.tools;

import java.util.List;

import org.acme.assistant.guard.HumanApproval;
import org.acme.book.model.Book;
import org.jboss.logging.Logger;

import dev.langchain4j.agent.tool.Tool;
import io.quarkiverse.langchain4j.guardrails.ToolInputGuardrails;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class BookTools {

    private static final Logger LOG = Logger.getLogger(BookTools.class);

    @Tool("List all books in the catalog")
    public List<Book> listBooks() {
        LOG.info("TOOL listBooks()");
        return Book.listAll();
    }

    @Tool("Find a book by its exact title")
    public Book findByTitle(String title) {
        LOG.infof("TOOL findByTitle(title=%s)", title);
        return Book.find("title", title).firstResult();
    }

    @Tool("Find all books written by the given author")
    public List<Book> findByAuthor(String author) {
        LOG.infof("TOOL findByAuthor(author=%s)", author);
        return Book.list("author", author);
    }

    @Tool("Add a new book to the catalog")
    @ToolInputGuardrails(HumanApproval.class)
    @Transactional
    public Book addBook(String title, String description, String author) {
        LOG.infof("TOOL addBook(title=%s, author=%s)", title, author);
        Book book = new Book();
        book.title = title;
        book.description = description;
        book.author = author;
        book.persist();
        return book;
    }

    @Tool("""
            Update an existing book identified by its id.
            NEVER guess or invent the id: if you only know the title or author,
            first call findByTitle or findByAuthor to get the book's real id.""")
    @ToolInputGuardrails(HumanApproval.class)
    @Transactional
    public Book updateBook(Long id, String title, String description, String author) {
        LOG.infof("TOOL updateBook(id=%d, title=%s)", id, title);
        Book book = Book.findById(id);
        if (book == null) {
            return null;
        }
        book.title = title;
        book.description = description;
        book.author = author;
        return book;
    }

    @Tool("""
            Delete a book by its id, returns true if deleted.
            NEVER guess or invent the id: if you only know the title or author,
            first call findByTitle or findByAuthor to get the book's real id.""")
    @ToolInputGuardrails(HumanApproval.class)
    @Transactional
    public boolean deleteBook(Long id) {
        LOG.infof("TOOL deleteBook(id=%d)", id);
        return Book.deleteById(id);
    }
}
