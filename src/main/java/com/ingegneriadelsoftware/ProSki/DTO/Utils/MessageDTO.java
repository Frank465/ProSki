package com.ingegneriadelsoftware.ProSki.DTO.Utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
