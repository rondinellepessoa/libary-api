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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.awt.print.Pageable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(book)).thenReturn(
                createValidBook());

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
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execucao
        Throwable exception = org.assertj.core.api.Assertions.catchThrowable( () -> service.save(book));

        //verificacoes
        org.assertj.core.api.Assertions.assertThat(exception)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Isbn já cadastrado");

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro por Id")
    public void getByIdTest(){
        //cenario
        Long id = 1l;
        Book book = createValidBook();
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        //execucao
        Optional<Book> foundBook = service.getById(id);

        //verificacoes
        org.assertj.core.api.Assertions.assertThat(foundBook.isPresent()).isTrue();
        org.assertj.core.api.Assertions.assertThat(foundBook.get().getId()).isEqualTo(id);
        org.assertj.core.api.Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        org.assertj.core.api.Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        org.assertj.core.api.Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por Id quando ele nao existe na base")
    public void bookNotFoundByIdTest(){
        //cenario
        Long id = 1l;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //execucao
        Optional<Book> foundBook = service.getById(id);

        //verificacoes
        org.assertj.core.api.Assertions.assertThat(foundBook.isPresent()).isFalse();

    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){
        //cenario
        Book book = Book.builder().id(1l).build();

        //execucao
        Assertions.assertDoesNotThrow(() ->this.service.delete(book));

        //verificacoes
        Mockito.verify(this.repository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente")
    public void deleteInvalidBookTest(){
        //cenario
        Book book = new Book();

        //execucao
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.delete(book)) ;

        //verificacoes
        Mockito.verify(this.repository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest(){
        //cenario
        long id = 1l;

        //livro a atualizar
        Book updatingBook = Book.builder().id(id).build();

        //simulacao
        Book updateBook = this.createValidBook();
        updateBook.setId(id);

        Mockito.when(this.repository.save(updatingBook)).thenReturn(updateBook);

        //execucao
        Book book = this.service.update(updatingBook);

        //verificacoes
        org.assertj.core.api.Assertions.assertThat(book.getId()).isEqualTo(updateBook.getId());
        org.assertj.core.api.Assertions.assertThat(book.getIsbn()).isEqualTo(updateBook.getIsbn());
        org.assertj.core.api.Assertions.assertThat(book.getTitle()).isEqualTo(updateBook.getTitle());
        org.assertj.core.api.Assertions.assertThat(book.getAuthor()).isEqualTo(updateBook.getAuthor());
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente")
    public void updateInvalidBookTest(){
        //cenario
        Book book = new Book();

        //execucao
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.service.update(book)) ;

        //verificacoes
        Mockito.verify(this.repository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest(){
        //cenario
        Book book = this.createValidBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> lista = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(lista, pageRequest, 1);
        Mockito.when(this.repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execucao
        Page<Book> result = this.service.find(book, pageRequest);

        //verificacoes
        org.assertj.core.api.Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(result.getContent()).isEqualTo(lista);
        org.assertj.core.api.Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Override
    public Book save(Book any) {
        return null;
    }

    @Override
    public Optional<Book> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public void delete(Book book) {

    }

    @Override
    public Book update(Book book) {
        return null;
    }

    @Override
    public Page find(Book filter, org.springframework.data.domain.Pageable pageRequest) {
        return null;
    }

    @Override
    public Page find(Book filter, Pageable pageRequest) {
        return null;
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        return Optional.empty();
    }

    private Book createValidBook() {
        return Book.builder().id(1l).isbn("123").author("Fulano").title("As aventuras").build();
    }
}
