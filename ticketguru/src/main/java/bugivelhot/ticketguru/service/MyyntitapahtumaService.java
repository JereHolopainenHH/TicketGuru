package bugivelhot.ticketguru.service;

// DTOt
import bugivelhot.ticketguru.dto.LippuDTO;
import bugivelhot.ticketguru.dto.LippuResponseDTO;
import bugivelhot.ticketguru.dto.MyyntitapahtumaJaLiputDTO;
import bugivelhot.ticketguru.dto.MyyntitapahtumaResponseDTO;

// Entiteetit
import bugivelhot.ticketguru.model.Kayttaja;
import bugivelhot.ticketguru.model.Lippu;
import bugivelhot.ticketguru.model.Maksutapa;
import bugivelhot.ticketguru.model.Myyntitapahtuma;
import bugivelhot.ticketguru.model.Tapahtuma;
import bugivelhot.ticketguru.model.TapahtumanLipputyyppi;

// repositoryt
import bugivelhot.ticketguru.repository.KayttajaRepository;
import bugivelhot.ticketguru.repository.LippuRepository;
import bugivelhot.ticketguru.repository.MaksutapaRepository;
import bugivelhot.ticketguru.repository.MyyntitapahtumaRepository;
import bugivelhot.ticketguru.repository.TapahtumaRepository;
import bugivelhot.ticketguru.repository.TapahtumanLipputyyppiRepository;

// Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import jakarta.transaction.Transactional;

// Java util
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

@Service
public class MyyntitapahtumaService {

    @Autowired
    private MyyntitapahtumaRepository myyntitapahtumaRepository;

    @Autowired
    private LippuRepository lippuRepository;

    @Autowired
    private TapahtumaRepository tapahtumaRepository;

    @Autowired
    private TapahtumanLipputyyppiRepository tapahtumaLipputyyppiRepository;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private MaksutapaRepository maksutapaRepository;

    @Transactional // Pitää huolen, että kaikki operaatiot suoritetaan yhdessä transaktiossa, jos jokin niistä epäonnistuu, niin kaikki peruutetaan
    public MyyntitapahtumaResponseDTO luoMyyntitapahtumaJaLiput(MyyntitapahtumaJaLiputDTO dto) {

        // Hae käyttäjä, joka luo myyntitapahtuman
        Optional<Kayttaja> kayttajaOptional = kayttajaRepository.findById(dto.getKayttajaId());
        if (!kayttajaOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Käyttäjää ei löydy");
        }
        Kayttaja kayttaja = kayttajaOptional.get();

        // Luo uusi myyntitapahtuma ja aseta käyttäjä sille
        Myyntitapahtuma myyntitapahtuma = new Myyntitapahtuma();
        myyntitapahtuma.setKayttaja(kayttaja);

        // Tallenna myyntitapahtuma
        myyntitapahtuma = myyntitapahtumaRepository.save(myyntitapahtuma);

        // Luo ja tallenna liput
        List<Lippu> lippuLista = new ArrayList<>();
        Double yhteissumma = 0.0;

        // Käy läpi kaikki liput, jotka halutaan luoda
        for (LippuDTO lippuDTO : dto.getLiput()) {
            // Hae tapahtuma tietokannasta ja heitä virhe jos tapahtumaa ei löydy
            Optional<Tapahtuma> tapahtumaOptional = tapahtumaRepository.findById(lippuDTO.getTapahtumaId());
            if (!tapahtumaOptional.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tapahtumaa ei löydy");
            }
            Tapahtuma tapahtuma = tapahtumaOptional.get();

            // Hae tapahtuman ja lipputyypin yhdistelmä (TapahtumaLipputyyppi) ja heitä virhe jos ei löydy
            Optional<TapahtumanLipputyyppi> tapahtumaLipputyyppiOptional = tapahtumaLipputyyppiRepository
                    .findById_TapahtumaIdAndId_LipputyyppiId(
                            lippuDTO.getTapahtumaId(), lippuDTO.getLipputyyppiId());
            if (!tapahtumaLipputyyppiOptional.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lipputyyppi ei kuulu tapahtumaan");
            }
            TapahtumanLipputyyppi tapahtumanLipputyyppi = tapahtumaLipputyyppiOptional.get();

            // Luo ja tallenna liput (lippuDTO.getMaara() kertaa)
            for (int i = 0; i < lippuDTO.getMaara(); i++) {
                Lippu lippu = new Lippu();
                lippu.setTapahtuma(tapahtuma);
                lippu.setLipputyyppi(tapahtumanLipputyyppi.getLipputyyppi());
                lippu.setMyyntitapahtuma(myyntitapahtuma);

                lippuLista.add(lippu);
                yhteissumma += tapahtumanLipputyyppi.getHinta(); // Päivitä myyntitapahtuman summa käyttäen TapahtumaLipputyyppi-hintaa
            }
        }

        // Tallenna luodut liput
        lippuRepository.saveAll(lippuLista);

        // Päivitä myyntitapahtuman summa
        myyntitapahtuma.setSumma(yhteissumma);

        // hae maksutapa ja aseta se myyntitapahtumaan
        Optional<Maksutapa> maksutapaOptional = maksutapaRepository.findByMaksutapaId(dto.getMaksutapaId());
        if (!maksutapaOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Maksutapaa ei löydy");
        }
        myyntitapahtuma.setMaksutapa(maksutapaOptional.get());

        // Tallenna myyntitapahtuma
        myyntitapahtumaRepository.save(myyntitapahtuma);

        // Palautetaan tiedot luodusta myyntitapahtumasta vastauksena
        MyyntitapahtumaResponseDTO responseDTO = new MyyntitapahtumaResponseDTO();
        responseDTO.setMyyntitapahtumaId(myyntitapahtuma.getMyyntitapahtumaId());
        responseDTO.setSumma(myyntitapahtuma.getSumma());
        responseDTO.setMaksupvm(myyntitapahtuma.getMaksupvm());
        responseDTO.setKayttajaId(myyntitapahtuma.getKayttaja().getKayttajaId());
        responseDTO.setMaksutapa(myyntitapahtuma.getMaksutapa().getMaksutapaNimi());

        // Lista luoduista lipuista, jotka sisältävät vain oleelliset tiedot
        List<LippuResponseDTO> lippuResponseDTOLista = new ArrayList<>();
        for (Lippu lippu : lippuLista) {
            LippuResponseDTO lippuResponseDTO = new LippuResponseDTO();
            lippuResponseDTO.setKoodi(lippu.getKoodi());
            lippuResponseDTO.setTapahtumaId(lippu.getTapahtuma().getTapahtumaId());
            lippuResponseDTO.setLipputyyppi(lippu.getLipputyyppi().getLipputyyppiNimi());
            lippuResponseDTO.setTila(lippu.getLipunTila());

            lippuResponseDTOLista.add(lippuResponseDTO);
        }

        // lisätään lippulista vastaukseen
        responseDTO.setLiput(lippuResponseDTOLista);

        return responseDTO;
    }
}
