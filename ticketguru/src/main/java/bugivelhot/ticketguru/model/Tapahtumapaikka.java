package bugivelhot.ticketguru.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;


@Entity
@Table(name = "tapahtumaPaikat")
public class Tapahtumapaikka {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TP_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tapahtuma_id", nullable = false)
    private Tapahtuma tapahtuma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "osoite_id", nullable = false)
    private Osoite osoite;

    public Tapahtumapaikka() {
    }

    public Tapahtumapaikka(Tapahtuma tapahtuma, Osoite osoite) {
        this.tapahtuma = tapahtuma;
        this.osoite = osoite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tapahtuma getTapahtuma() {
        return tapahtuma;
    }

    public void setTapahtuma(Tapahtuma tapahtuma) {
        this.tapahtuma = tapahtuma;
    }

    public Osoite getOsoite() {
        return osoite;
    }

    public void setOsoite(Osoite osoite) {
        this.osoite = osoite;
    }
}
