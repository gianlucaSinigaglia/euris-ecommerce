package it.euris.ecommerce.controller;

import it.euris.ecommerce.domain.dto.ProdottoDTO;
import it.euris.ecommerce.service.ProdottoService;
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
@RequestMapping("/api/v1/prodotti")
public class ProdottoController {

    private final ProdottoService prodottoService;

    public ProdottoController(ProdottoService prodottoService) {
        this.prodottoService = prodottoService;
    }

    @GetMapping
    public ResponseEntity<Page<ProdottoDTO>> getAllProdotti(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Richiesta lista prodotti con paginazione: {}", pageable);
        Page<ProdottoDTO> response = prodottoService.findAllProdotti(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProdottoDTO> aggiungiProdotto(@RequestBody ProdottoDTO prodottoDTO) {
        log.info("Richiesta di aggiunta prodotto: [{}]", prodottoDTO);
        ProdottoDTO response = prodottoService.aggiungiProdotto(prodottoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
