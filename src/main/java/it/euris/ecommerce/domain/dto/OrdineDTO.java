package it.euris.ecommerce.domain.dto;

import it.euris.ecommerce.domain.entity.StatoOrdine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdineDTO {

    private UUID id;
    private UUID clienteId;
    private StatoOrdine statoOrdine;
    private List<DettaglioOrdineDTO> dettagli;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DettaglioOrdineDTO {
        private UUID prodottoId;
        private Integer quantita;
    }

}
