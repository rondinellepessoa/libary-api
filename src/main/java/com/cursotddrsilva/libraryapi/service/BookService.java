package com.cursotddrsilva.libraryapi.service;

import com.cursotddrsilva.libraryapi.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


public interface BookService {
    Book save(Book any);

    Optional<Book> getById(Long id);

    void delete(Book book);

    Book update(Book book);

    Page find(Book filter, Pageable pageRequest);

    Page find(Book filter, java.awt.print.Pageable pageRequest);

    Optional<Book> getBookByIsbn(String isbn);
}
