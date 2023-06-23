package com.ingegneriadelsoftware.ProSki.DTO.Utils;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {

    private Integer idMessage;

    private Integer user;

    private String message;

    private List<CommentDTO> comments;

}
