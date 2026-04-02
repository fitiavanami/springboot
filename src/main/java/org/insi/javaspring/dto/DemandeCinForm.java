package org.insi.javaspring.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.insi.javaspring.model.Etat;
import org.insi.javaspring.model.Type;

@Data
public class DemandeCinForm {

    @NotNull(message = "Le type de demande est obligatoire")
    private Type demande;

    @NotNull(message = "Le citoyen est obligatoire")
    private Long citoyenId;
}