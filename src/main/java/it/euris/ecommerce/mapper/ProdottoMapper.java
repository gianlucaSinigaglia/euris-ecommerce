package it.euris.ecommerce.mapper;

import it.euris.ecommerce.domain.dto.ProdottoDTO;
import it.euris.ecommerce.domain.entity.Prodotto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProdottoMapper {

    ProdottoDTO toDTO(Prodotto prodotto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Prodotto toEntity(ProdottoDTO prodottoDTO);

}