package it.euris.ecommerce.mapper;

import it.euris.ecommerce.domain.dto.ClienteDTO;
import it.euris.ecommerce.domain.entity.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteDTO toDTO(Cliente cliente);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ordini", ignore = true)
    Cliente toEntity(ClienteDTO clienteDTO);

}
