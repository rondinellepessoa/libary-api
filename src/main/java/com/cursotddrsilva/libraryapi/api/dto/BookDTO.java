package com.cursotddrsilva.libraryapi.api.dto;

import lombok.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private Long id;

    @NotEmpty
    //@NotNull
    private String title;

    @NotEmpty
    //@NotNull
    private String author;

    @NotEmpty
    //@NotNull
    private String isbn;

}
