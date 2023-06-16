package com.ingegneriadelsoftware.ProSki.DTO.Utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {

    private Integer id;

    private String user;

    private String comment;
}
