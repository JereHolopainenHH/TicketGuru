package bugivelhot.ticketguru.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "maksutavat") // Määrittää, että tämä entiteetti vastaa tietokantataulua "maksutavat"
public class Maksutapa {

    // tietokantataulun kentät
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Määrittää, että maksutapaId on pääavain ja se generoidaan automaattisesti
    private Long maksutapaId;
    private String maksutapaNimi;

    @OneToMany(mappedBy = "maksutapa", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Myyntitapahtuma> myyntitapahtumat;

    // konstruktorit
    public Maksutapa(String maksutapaNimi) {
        this.maksutapaNimi = maksutapaNimi;
    }

    public Maksutapa(){}

    public Long getMaksutapaId() {
        return maksutapaId;
    }

    public void setMaksutapaId(Long maksutapaId) {
        this.maksutapaId = maksutapaId;
    }

    public String getMaksutapaNimi() {
        return maksutapaNimi;
    }

    public void setMaksutapaNimi(String maksutapaNimi) {
        this.maksutapaNimi = maksutapaNimi;
    }

    public List<Myyntitapahtuma> getMyyntitapahtumat() {
        return myyntitapahtumat;
    }

    public void setMyyntitapahtumat(List<Myyntitapahtuma> myyntitapahtumat) {
        this.myyntitapahtumat = myyntitapahtumat;
    }

    @Override
    public String toString() {
        return "Maksutapa [maksutapaId=" + maksutapaId + ", maksutapaNimi=" + maksutapaNimi + ", myyntitapahtumat="
                + myyntitapahtumat + "]";
    }
}
