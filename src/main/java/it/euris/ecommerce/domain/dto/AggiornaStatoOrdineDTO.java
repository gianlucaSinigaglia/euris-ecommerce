package it.euris.ecommerce.domain.dto;

import it.euris.ecommerce.domain.entity.StatoOrdine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggiornaStatoOrdineDTO {

    private StatoOrdine statoOrdine;
    
}
