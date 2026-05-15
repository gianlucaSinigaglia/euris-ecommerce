package it.euris.ecommerce.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "prodotti",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_prodotto_codice", columnNames = "codice")
        })
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Prodotto extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "codice", nullable = false, unique = true, length = 50)
    private String codice;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "stock", nullable = false)
    @Min(value = 0, message = "Lo stock non può essere negativo")
    private Integer stock;

}