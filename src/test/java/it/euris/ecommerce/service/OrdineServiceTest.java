package it.euris.ecommerce.service;

import it.euris.ecommerce.BaseIntegrationTest;
import it.euris.ecommerce.domain.dto.OrdineDTO;
import it.euris.ecommerce.domain.entity.*;
import it.euris.ecommerce.repository.ClienteRepository;
import it.euris.ecommerce.repository.OrdineRepository;
import it.euris.ecommerce.repository.ProdottoRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@DisplayName("Test OrdineService")
public class OrdineServiceTest extends BaseIntegrationTest {

    @Autowired
    private OrdineService ordineService;

    @Autowired
    private OrdineRepository ordineRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdottoRepository prodottoRepository;

    @AfterEach
    void tearDown() {
        ordineRepository.deleteAll();
        prodottoRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    @Nested
    @DisplayName("Test findAll ordini")
    class FindAllOrdiniTest {

        @Test
        @DisplayName("Deve restituire gli ordini paginati")
        void ritornaOrdiniPaginati() {
            // Arrange - creo un cliente, un prodotto e due ordini
            Cliente cliente = Cliente.builder()
                    .nome("Gianluca")
                    .cognome("Sinigaglia")
                    .dataNascita(LocalDate.of(1997, 6, 7))
                    .codiceFiscale("SNGGLC97A01H501U")
                    .email("gianluca@mail.com")
                    .build();
            cliente = clienteRepository.save(cliente);

            Prodotto prodotto = Prodotto.builder()
                    .codice("P001")
                    .nome("Laptop")
                    .stock(100)
                    .build();
            prodotto = prodottoRepository.save(prodotto);

            DettaglioOrdine dettaglio1 = DettaglioOrdine.builder()
                    .prodotto(prodotto)
                    .quantita(1)
                    .build();

            DettaglioOrdine dettaglio2 = DettaglioOrdine.builder()
                    .prodotto(prodotto)
                    .quantita(2)
                    .build();

            Ordine ordine1 = Ordine.builder()
                    .cliente(cliente)
                    .dettagli(List.of(dettaglio1))
                    .statoOrdine(StatoOrdine.ORDINATO)
                    .build();
            dettaglio1.setOrdine(ordine1);

            Ordine ordine2 = Ordine.builder()
                    .cliente(cliente)
                    .dettagli(List.of(dettaglio2))
                    .statoOrdine(StatoOrdine.CONSEGNATO)
                    .build();
            dettaglio2.setOrdine(ordine2);

            ordineRepository.saveAll(List.of(ordine1, ordine2));

            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<OrdineDTO> result = ordineService.findAllOrdini(pageable);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Test aggiungiOrdine")
    class AggiungiOrdineTest {

        @Test
        @DisplayName("Deve aggiungere un ordine con dati validi e scalare lo stock")
        void aggiungiOrdineConDatiValidi() {
            // Arrange - creo un cliente e un prodotto con stock sufficiente
            Cliente cliente = Cliente.builder()
                    .nome("Gianluca")
                    .cognome("Sinigaglia")
                    .dataNascita(LocalDate.of(1997, 6, 7))
                    .codiceFiscale("SNGGLC97A01H501U")
                    .email("gianluca@mail.com")
                    .build();
            cliente = clienteRepository.save(cliente);

            Prodotto prodotto = Prodotto.builder()
                    .codice("P001")
                    .nome("Laptop")
                    .stock(10)
                    .build();
            prodotto = prodottoRepository.save(prodotto);

            // Creo il DTO dell'ordine
            OrdineDTO.DettaglioOrdineDTO dettaglioDTO = OrdineDTO.DettaglioOrdineDTO.builder()
                    .prodottoId(prodotto.getId())
                    .quantita(3)
                    .build();

            OrdineDTO ordineDTO = OrdineDTO.builder()
                    .clienteId(cliente.getId())
                    .dettagli(List.of(dettaglioDTO))
                    .statoOrdine(StatoOrdine.ORDINATO)
                    .build();

            // Act - eseguo il metodo
            OrdineDTO risultato = ordineService.aggiungiOrdine(ordineDTO);

            // Assert - verifico che l'ordine sia stato creato e lo stock scalato
            assertThat(risultato).isNotNull();
            assertThat(risultato.getId()).isNotNull();
            assertThat(risultato.getStatoOrdine()).isEqualTo(StatoOrdine.ORDINATO);
            assertThat(risultato.getDettagli()).hasSize(1);
            assertThat(risultato.getDettagli().getFirst().getQuantita()).isEqualTo(3);

            // Verifico che lo stock sia stato aggiornato correttamente
            Prodotto prodottoAggiornato = prodottoRepository.findById(prodotto.getId()).get();
            assertThat(prodottoAggiornato.getStock()).isEqualTo(7); // 10 - 3
        }

        @Test
        @DisplayName("Deve lanciare eccezione quando lo stock è insufficiente")
        void lanciaEccezioneQuandoStockInsufficiente() {
            // Arrange - creo un cliente, un prodotto con stock insufficiente
            Cliente cliente = Cliente.builder()
                    .nome("Gianluca")
                    .cognome("Sinigaglia")
                    .dataNascita(LocalDate.of(1997, 6, 7))
                    .codiceFiscale("SNGGLC97A01H501U")
                    .email("gianluca@mail.com")
                    .build();
            cliente = clienteRepository.save(cliente);

            Prodotto prodotto = Prodotto.builder()
                    .codice("P001")
                    .nome("Laptop")
                    .stock(2)  // Stock insufficiente per la richiesta
                    .build();
            prodotto = prodottoRepository.save(prodotto);

            // Creo il DTO dell'ordine con quantità maggiore dello stock
            OrdineDTO.DettaglioOrdineDTO dettaglioDTO = OrdineDTO.DettaglioOrdineDTO.builder()
                    .prodottoId(prodotto.getId())
                    .quantita(5)  // Richiedo 5, ma lo stock è 2
                    .build();

            OrdineDTO ordineDTO = OrdineDTO.builder()
                    .clienteId(cliente.getId())
                    .dettagli(List.of(dettaglioDTO))
                    .statoOrdine(StatoOrdine.ORDINATO)
                    .build();

            // Act & Assert - verifico che venga lanciata l'eccezione
            assertThrows(IllegalArgumentException.class, () -> {
                ordineService.aggiungiOrdine(ordineDTO);
            });

            // Verifico che lo stock non sia cambiato
            Prodotto prodottoNonModificato = prodottoRepository.findById(prodotto.getId()).get();
            assertThat(prodottoNonModificato.getStock()).isEqualTo(2);

            // Verifico che nessun ordine sia stato salvato
            assertThat(ordineRepository.findAll()).isEmpty();
        }

    }

    @Nested
    @DisplayName("Test aggiornaOrdine")
    class AggiornaOrdineTest {

        @Test
        @DisplayName("Deve aggiornare lo stato di un ordine esistente")
        void aggiornaStatoOrdine() {
            // Arrange - creo un cliente, un prodotto e un ordine
            Cliente cliente = Cliente.builder()
                    .nome("Gianluca")
                    .cognome("Sinigaglia")
                    .dataNascita(LocalDate.of(1997, 6, 7))
                    .codiceFiscale("SNGGLC97A01H501U")
                    .email("gianluca@mail.com")
                    .build();
            cliente = clienteRepository.save(cliente);

            Prodotto prodotto = Prodotto.builder()
                    .codice("P001")
                    .nome("Laptop")
                    .stock(100)
                    .build();
            prodotto = prodottoRepository.save(prodotto);

            DettaglioOrdine dettaglio = DettaglioOrdine.builder()
                    .prodotto(prodotto)
                    .quantita(1)
                    .build();

            Ordine ordine = Ordine.builder()
                    .cliente(cliente)
                    .dettagli(List.of(dettaglio))
                    .statoOrdine(StatoOrdine.ORDINATO)
                    .build();
            dettaglio.setOrdine(ordine);
            ordine = ordineRepository.save(ordine);

            // Act
            OrdineDTO risultato = ordineService.aggiornaStato(ordine.getId(), StatoOrdine.CONSEGNATO);

            // Assert
            assertThat(risultato).isNotNull();
            assertThat(risultato.getStatoOrdine()).isEqualTo(StatoOrdine.CONSEGNATO);
        }

    }

    @Nested
    @DisplayName("Test cancellaOrdine")
    class CancellaOrdineTest {

        @Test
        @DisplayName("Deve cancellare un ordine non consegnato e ripristinare lo stock")
        void cancellaOrdineNonConsegnato() {
            // Arrange - creo un cliente, un prodotto con stock e un ordine
            Cliente cliente = Cliente.builder()
                    .nome("Gianluca")
                    .cognome("Sinigaglia")
                    .dataNascita(LocalDate.of(1997, 6, 7))
                    .codiceFiscale("SNGGLC97A01H501U")
                    .email("gianluca@mail.com")
                    .build();
            cliente = clienteRepository.save(cliente);

            Prodotto prodotto = Prodotto.builder()
                    .codice("P001")
                    .nome("Laptop")
                    .stock(10)
                    .build();
            prodotto = prodottoRepository.save(prodotto);

            DettaglioOrdine dettaglio = DettaglioOrdine.builder()
                    .prodotto(prodotto)
                    .quantita(3)
                    .build();

            Ordine ordine = Ordine.builder()
                    .cliente(cliente)
                    .dettagli(List.of(dettaglio))
                    .statoOrdine(StatoOrdine.ORDINATO)
                    .build();
            dettaglio.setOrdine(ordine);
            ordine = ordineRepository.save(ordine);

            // Act - Esecuzione del metodo
            ordineService.cancellaOrdine(ordine.getId());

            // Assert - Verifico che l'ordine sia stato cancellato e lo stock ripristinato
            assertThat(ordineRepository.findById(ordine.getId())).isEmpty();

            Prodotto prodottoAggiornato = prodottoRepository.findById(prodotto.getId()).get();
            assertThat(prodottoAggiornato.getStock()).isEqualTo(13); // 10 + 3 dell'ordine cancellato
        }

        @Test
        @DisplayName("Deve lanciare eccezione quando si tenta di cancellare un ordine consegnato")
        void lanciareEccezionePerOrdineConsegnato() {
            // Arrange - creo un ordine CONSEGNATO
            Cliente cliente = Cliente.builder()
                    .nome("Gianluca")
                    .cognome("Sinigaglia")
                    .dataNascita(LocalDate.of(1997, 6, 7))
                    .codiceFiscale("SNGGLC97A01H501U")
                    .email("gianluca@mail.com")
                    .build();
            cliente = clienteRepository.save(cliente);

            Prodotto prodotto = Prodotto.builder()
                    .codice("P001")
                    .nome("Laptop")
                    .stock(10)
                    .build();
            prodotto = prodottoRepository.save(prodotto);

            DettaglioOrdine dettaglio = DettaglioOrdine.builder()
                    .prodotto(prodotto)
                    .quantita(2)
                    .build();

            Ordine ordine = Ordine.builder()
                    .cliente(cliente)
                    .dettagli(List.of(dettaglio))
                    .statoOrdine(StatoOrdine.CONSEGNATO)
                    .build();
            dettaglio.setOrdine(ordine);
            ordine = ordineRepository.save(ordine);

            final UUID ordineId = ordine.getId();

            // Act & Assert - Verifico che venga lanciata l'eccezione
            assertThrows(IllegalArgumentException.class, () -> {
                ordineService.cancellaOrdine(ordineId);
            });

            // Verifico che l'ordine esista ancora
            assertThat(ordineRepository.findById(ordineId)).isPresent();
        }

    }

}
