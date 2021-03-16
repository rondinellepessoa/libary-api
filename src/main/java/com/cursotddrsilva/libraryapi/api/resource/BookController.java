package com.cursotddrsilva.libraryapi.api.resource;

import com.cursotddrsilva.libraryapi.api.dto.BookDTO;
import com.cursotddrsilva.libraryapi.api.dto.LoanDto;
import com.cursotddrsilva.libraryapi.model.entity.Book;
import com.cursotddrsilva.libraryapi.model.entity.Loan;
import com.cursotddrsilva.libraryapi.service.BookService;
import com.cursotddrsilva.libraryapi.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Api("Book API")
@Slf4j
public class BookController {

    private final BookService service;
    private final ModelMapper modelMapper;
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creates a book")
    public BookDTO create(@RequestBody @Valid BookDTO dto){
        log.info("creating a book for isbn:{}", dto.getIsbn());

        Book entity = modelMapper.map(dto,Book.class);

        entity = this.service.save(entity);

        entity = entity == null ? new Book() : entity;

        return modelMapper.map(entity, BookDTO.class);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Obtains a book details by id")
    public BookDTO get(@PathVariable Long id){
        return service
                .getById(id)
                .map(book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Deletes a book by id")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Book succesfully deleted"),
            @ApiResponse(code = 401, message = "Forbidden to remove the book")
    })
    public void delete(@PathVariable Long id){
        Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(book);
    }

    @PutMapping("{id}")
    @ApiOperation("Updates a book")
    public BookDTO update(@PathVariable Long id,@RequestBody @Valid BookDTO dto){
        return service.getById(id).map( book -> {

            book.setAuthor(dto.getAuthor());
            book.setTitle(dto.getTitle());
            book = service.update(book);
            return modelMapper.map(book, BookDTO.class);

        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @GetMapping
    @ApiOperation("Find book by params")
    public Page<BookDTO> find(BookDTO dto, Pageable pageRequest){
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pageRequest);
        List<BookDTO> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());
        return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
    }

    @GetMapping("{id}/loans")
    public Page<LoanDto> loansByBook( @PathVariable Long id, Pageable pageable){
        Book book = this.service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<Loan> result = this.loanService.getLoansByBook(book, pageable);
        List<LoanDto> list = result.getContent().stream()
                .map( loan -> {
                    Book loanBook = loan.getBook();
                    BookDTO bookDTO = modelMapper.map(loanBook, BookDTO.class);
                    LoanDto loanDto = modelMapper.map(loan, LoanDto.class);
                    loanDto.setBookDTO(bookDTO);
                    return loanDto;
                }).collect(Collectors.toList());
        return new PageImpl<LoanDto>(list, pageable, result.getTotalElements());
    }
}
