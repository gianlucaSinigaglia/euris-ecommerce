package it.euris.ecommerce.service;

import it.euris.ecommerce.domain.dto.OrdineDTO;
import it.euris.ecommerce.domain.entity.*;
import it.euris.ecommerce.mapper.OrdineMapper;
import it.euris.ecommerce.repository.ClienteRepository;
import it.euris.ecommerce.repository.OrdineRepository;
import it.euris.ecommerce.repository.ProdottoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class OrdineService {

    private final OrdineRepository ordineRepository;
    private final ClienteRepository clienteRepository;
    private final ProdottoRepository prodottoRepository;
    private final OrdineMapper ordineMapper;

    public OrdineService(OrdineRepository ordineRepository, ClienteRepository clienteRepository, ProdottoRepository prodottoRepository, OrdineMapper ordineMapper) {
        this.ordineRepository = ordineRepository;
        this.clienteRepository = clienteRepository;
        this.prodottoRepository = prodottoRepository;
        this.ordineMapper = ordineMapper;
    }

    @Transactional(readOnly = true)
    public Page<OrdineDTO> findAllOrdini(Pageable pageable) {
        log.info("Ricerca ordini con paginazione: [{}]", pageable);
        Page<Ordine> ordini = ordineRepository.findAll(pageable);
        return ordini.map(ordineMapper::toDTO);
    }

    @Transactional
    public OrdineDTO aggiungiOrdine(OrdineDTO ordineDTO) {
        log.info("Aggiunta nuovo ordine: [{}]", ordineDTO);

        // Recupero il cliente
        Cliente cliente = clienteRepository.findById(ordineDTO.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente non trovato: " + ordineDTO.getClienteId()));

        // Converto DTO in Entity
        Ordine ordine = ordineMapper.toEntity(ordineDTO);
        ordine.setCliente(cliente);

        // Gestisce dettagli e stock
        ordine.setDettagli(ordineDTO.getDettagli().stream().map(dettaglioOrdineDTO -> {
            Prodotto prodotto = prodottoRepository.findByIdWithLock(dettaglioOrdineDTO.getProdottoId())
                    .orElseThrow(() -> new EntityNotFoundException("Prodotto non trovato: " + dettaglioOrdineDTO.getProdottoId()));

            // Controllo disponibilità stock
            if (prodotto.getStock() < dettaglioOrdineDTO.getQuantita()) {
                throw new IllegalArgumentException("Stock insufficiente per il prodotto: " + prodotto.getId());
            }

            // Scalo lo stock
            prodotto.setStock(prodotto.getStock() - dettaglioOrdineDTO.getQuantita());
            prodottoRepository.save(prodotto);

            return DettaglioOrdine.builder()
                    .ordine(ordine)
                    .prodotto(prodotto)
                    .quantita(dettaglioOrdineDTO.getQuantita())
                    .build();
        }).toList());

        // Salvo l'ordine (CascadeType.ALL salva anche i dettagli)
        OrdineDTO ordineSalvatoDTO = ordineMapper.toDTO(ordineRepository.save(ordine));
        log.info("Ordine aggiunto: [{}]", ordineSalvatoDTO);
        return ordineSalvatoDTO;
    }

    @Transactional
    public OrdineDTO aggiornaStato(UUID ordineId, StatoOrdine statoOrdine) {
        log.info("Aggiornamento stato ordineId: [{}], statoOrdine: [{}]", ordineId, statoOrdine);
        Ordine ordine = ordineRepository.findById(ordineId)
                .orElseThrow(() -> new EntityNotFoundException("Ordine non trovato"));
        ordine.setStatoOrdine(statoOrdine);

        OrdineDTO ordineAggiornatoDTO = ordineMapper.toDTO(ordineRepository.save(ordine));
        log.info("Ordine aggiornato: [{}]", ordineAggiornatoDTO);
        return ordineAggiornatoDTO;
    }

    @Transactional
    public void cancellaOrdine(UUID ordineId) {
        log.info("Cancellazione ordineId: [{}]", ordineId);

        Ordine ordine = ordineRepository.findById(ordineId)
                .orElseThrow(() -> new EntityNotFoundException("Ordine non trovato"));

        if (ordine.getStatoOrdine() == StatoOrdine.CONSEGNATO) {
            throw new IllegalArgumentException("Impossibile cancellare un ordine già consegnato");
        }

        // Ripristina lo stock dei prodotti
        for (DettaglioOrdine dettaglio : ordine.getDettagli()) {
            Prodotto prodotto = dettaglio.getProdotto();
            prodotto.setStock(prodotto.getStock() + dettaglio.getQuantita());
            prodottoRepository.save(prodotto);
        }

        ordineRepository.delete(ordine);
        log.info("Ordine cancellato: [{}]", ordineId);
    }

}