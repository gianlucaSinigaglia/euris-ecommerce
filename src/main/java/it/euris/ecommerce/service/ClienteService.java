package it.euris.ecommerce.service;

import it.euris.ecommerce.domain.dto.ClienteDTO;
import it.euris.ecommerce.domain.entity.Cliente;
import it.euris.ecommerce.mapper.ClienteMapper;
import it.euris.ecommerce.repository.ClienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    @Transactional(readOnly = true)
    public Page<ClienteDTO> findAllClienti(Pageable pageable) {
        log.info("Ricerca clienti con paginazione: [{}]", pageable);
        Page<Cliente> clienti = clienteRepository.findAll(pageable);
        return clienti.map(clienteMapper::toDTO);
    }

    @Transactional
    public ClienteDTO aggiungiCliente(ClienteDTO clienteDTO) {
        log.info("Aggiunta nuovo cliente: [{}]", clienteDTO);

        Cliente cliente = clienteMapper.toEntity(clienteDTO);
        Cliente clienteSalvato = clienteRepository.save(cliente);

        ClienteDTO clienteSalvatoDTO = clienteMapper.toDTO(clienteSalvato);
        log.info("Cliente aggiunto: [{}]", clienteSalvatoDTO);
        return clienteSalvatoDTO;
    }

}
