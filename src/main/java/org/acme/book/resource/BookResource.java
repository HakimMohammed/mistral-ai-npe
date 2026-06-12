package org.acme.book.resource;

import java.util.List;

import org.acme.book.model.Book;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/book")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    @GET
    public List<Book> list() {
        return Book.listAll();
    }

    @GET
    @Path("/{id}")
    public Book get(@PathParam("id") Long id) {
        Book book = Book.findById(id);
        if (book == null) {
            throw new WebApplicationException("Book " + id + " not found", Response.Status.NOT_FOUND);
        }
        return book;
    }

    @POST
    @Transactional
    public Response create(Book book) {
        book.id = null;
        book.persist();
        return Response.status(Response.Status.CREATED).entity(book).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Book update(@PathParam("id") Long id, Book book) {
        Book entity = Book.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Book " + id + " not found", Response.Status.NOT_FOUND);
        }
        entity.title = book.title;
        entity.description = book.description;
        entity.author = book.author;
        return entity;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = Book.deleteById(id);
        if (!deleted) {
            throw new WebApplicationException("Book " + id + " not found", Response.Status.NOT_FOUND);
        }
        return Response.noContent().build();
    }
}
