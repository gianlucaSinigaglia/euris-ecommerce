package it.euris.ecommerce.controller;

import it.euris.ecommerce.domain.dto.AggiornaStatoOrdineDTO;
import it.euris.ecommerce.domain.dto.OrdineDTO;
import it.euris.ecommerce.service.OrdineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/ordini")
public class OrdineController {

    private final OrdineService ordineService;

    public OrdineController(OrdineService ordineService) {
        this.ordineService = ordineService;
    }

    @GetMapping
    public ResponseEntity<Page<OrdineDTO>> getAllOrdini(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Richiesta lista ordini con paginazione: [{}]", pageable);
        Page<OrdineDTO> response = ordineService.findAllOrdini(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<OrdineDTO> creaOrdine(@RequestBody OrdineDTO ordineDTO) {
        log.info("Richiesta di aggiunta ordine: [{}]", ordineDTO);
        OrdineDTO response = ordineService.aggiungiOrdine(ordineDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{ordineId}/stato")
    public ResponseEntity<OrdineDTO> aggiornaStato(
            @PathVariable UUID ordineId,
            @RequestBody AggiornaStatoOrdineDTO aggiornaStatoDTO) {
        log.info("Richiesta di aggiornamento ordineId: [{}], statoOrdine: [{}]", ordineId, aggiornaStatoDTO);
        OrdineDTO response = ordineService.aggiornaStato(ordineId, aggiornaStatoDTO.getStatoOrdine());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{ordineId}")
    public ResponseEntity<Void> cancellaOrdine(@PathVariable UUID ordineId) {
        log.info("Richiesta di cancellazione ordineId: [{}]", ordineId);
        ordineService.cancellaOrdine(ordineId);
        return ResponseEntity.noContent().build();
    }

}
