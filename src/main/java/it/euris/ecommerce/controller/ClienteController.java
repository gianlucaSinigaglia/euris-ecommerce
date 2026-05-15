package it.euris.ecommerce.controller;

import it.euris.ecommerce.domain.dto.ClienteDTO;
import it.euris.ecommerce.service.ClienteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/clienti")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<Page<ClienteDTO>> getAllClienti(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Richiesta lista clienti con paginazione: [{}]", pageable);
        Page<ClienteDTO> response = clienteService.findAllClienti(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> aggiungiCliente(@RequestBody ClienteDTO clienteDTO) {
        log.info("Richiesta di aggiunta cliente: [{}]", clienteDTO);
        ClienteDTO response = clienteService.aggiungiCliente(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
