package com.cursotddrsilva.libraryapi.service;

import com.cursotddrsilva.libraryapi.exception.BusinessException;
import com.cursotddrsilva.libraryapi.model.entity.Book;
import com.cursotddrsilva.libraryapi.model.repository.BookRepository;
import com.cursotddrsilva.libraryapi.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.function.BooleanSupplier;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest implements BookService {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl( repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        //cenario
        Book book = createValidBook(Book.builder());
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(book)).thenReturn(
                createValidBook(Book.builder()
                        .id(1l)));

        //execucao
        Book savedBook = service.save(book);

        //verificacao
        Assertions.assertNotNull(savedBook.getId());
        Assertions.assertEquals(savedBook.getIsbn(),"123");
        Assertions.assertEquals(savedBook.getTitle(),"As aventuras");
        Assertions.assertEquals(savedBook.getAuthor(),"Fulano");
    }

    @Test
    @DisplayName("Deve lancar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookWithDuplicatedISBN(){
        //cenario
        Book book = createValidBook(Book.builder());
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execucao
        Throwable exception = org.assertj.core.api.Assertions.catchThrowable( () -> service.save(book));

        //verificacoes
        org.assertj.core.api.Assertions.assertThat(exception)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Isbn já cadastrado");

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Override
    public Book save(Book any) {
        return null;
    }

    private Book createValidBook(Book.BookBuilder builder) {
        return builder.isbn("123").author("Fulano").title("As aventuras").build();
    }
}
