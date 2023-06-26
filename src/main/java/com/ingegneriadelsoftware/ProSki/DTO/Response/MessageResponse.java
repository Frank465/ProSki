package com.ingegneriadelsoftware.ProSki.DTO.Response;

import com.ingegneriadelsoftware.ProSki.DTO.Utils.MessageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {

    //Nei tre casi questo può essere: emailIstruttore, nome località, email riforniore
    private String username;

    private List<MessageDTO> listMessage;

    private String error;
}
