package com.cursotddrsilva.libraryapi.model.repository;

import com.cursotddrsilva.libraryapi.model.entity.Book;
import com.cursotddrsilva.libraryapi.service.BookService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado")
    public void returnTrueWhenIsbnExistis(){
        //cenario
        String isbn = "123";
        Book book = createNewBook(isbn);
        entityManager.persist(book);

        //execucao
        boolean exists = repository.existsByIsbn(isbn);

        //verificacao
        Assertions.assertThat(exists).isTrue();
    }

    private Book createNewBook(String isbn) {
        return Book.builder().title("As aventuras").author("Fulano").isbn(isbn).build();
    }

    @Test
    @DisplayName("Deve retornar falso quando nao existir um livro na base com o isbn informado")
    public void returnFalseWhenIsbnDoesntExist(){
        //cenario
        String isbn = "123";

        //execucao
        boolean exists = repository.existsByIsbn(isbn);

        //verificacao
        Assertions.assertThat(exists).isFalse();

    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void findByIdTest(){
        //cenario
        Book book = this.createNewBook("123");
        this.entityManager.persist(book);

        //execucao
        Optional<Book> foundBook = this.repository.findById(book.getId());

        //verificacoes
        Assertions.assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        //cenario
        Book book = this.createNewBook("123");

        //execucao
        Book savedBook = this.repository.save(book);

        //verficacoes
        Assertions.assertThat(this.repository.findById(savedBook.getId())).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){
        //cenario
        Book book = this.createNewBook("123");
        this.entityManager.persist(book);

        //execucao
        this.repository.delete(book);
        Book deletedBook = this.entityManager.find(Book.class, book.getId());

        //verificacao
        Assertions.assertThat(deletedBook).isNull();


    }

}
