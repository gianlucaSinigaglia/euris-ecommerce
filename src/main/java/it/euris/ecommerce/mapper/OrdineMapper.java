package it.euris.ecommerce.mapper;

import it.euris.ecommerce.domain.dto.OrdineDTO;
import it.euris.ecommerce.domain.entity.DettaglioOrdine;
import it.euris.ecommerce.domain.entity.Ordine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrdineMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "dettagli", target = "dettagli", qualifiedByName = "toDettaglioDTOList")
    OrdineDTO toDTO(Ordine ordine);

    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "dettagli", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Ordine toEntity(OrdineDTO ordineDTO);

    @Named("toDettaglioDTOList")
    default List<OrdineDTO.DettaglioOrdineDTO> toDettaglioDTOList(List<DettaglioOrdine> dettagli) {
        if (dettagli == null) {
            return null;
        }
        return dettagli.stream()
                .map(d -> OrdineDTO.DettaglioOrdineDTO.builder()
                        .prodottoId(d.getProdotto().getId())
                        .quantita(d.getQuantita())
                        .build())
                .toList();
    }
}
