package it.euris.ecommerce.service;

import it.euris.ecommerce.domain.dto.ProdottoDTO;
import it.euris.ecommerce.domain.entity.Prodotto;
import it.euris.ecommerce.mapper.ProdottoMapper;
import it.euris.ecommerce.repository.ProdottoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProdottoService {

    private final ProdottoRepository prodottoRepository;
    private final ProdottoMapper prodottoMapper;

    public ProdottoService(ProdottoRepository prodottoRepository, ProdottoMapper prodottoMapper) {
        this.prodottoRepository = prodottoRepository;
        this.prodottoMapper = prodottoMapper;
    }

    @Transactional(readOnly = true)
    public Page<ProdottoDTO> findAllProdotti(Pageable pageable) {
        log.info("Ricerca prodotti con paginazione: [{}]", pageable);
        Page<Prodotto> prodotti = prodottoRepository.findAll(pageable);
        return prodotti.map(prodottoMapper::toDTO);
    }

    @Transactional
    public ProdottoDTO aggiungiProdotto(ProdottoDTO prodottoDTO) {
        log.info("Aggiunta nuovo prodotto: [{}]", prodottoDTO);

        Prodotto prodotto = prodottoMapper.toEntity(prodottoDTO);
        Prodotto prodottoSalvato = prodottoRepository.save(prodotto);

        ProdottoDTO prodottoSalvatoDTO = prodottoMapper.toDTO(prodottoSalvato);
        log.info("Prodotto aggiunto: [{}]", prodottoSalvatoDTO);
        return prodottoSalvatoDTO;
    }

}
