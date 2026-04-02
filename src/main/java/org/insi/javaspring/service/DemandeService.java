package org.insi.javaspring.service;

import lombok.RequiredArgsConstructor;
import org.insi.javaspring.dto.DemandeCinForm;
import org.insi.javaspring.model.Citoyen;
import org.insi.javaspring.model.Demandecin;
import org.insi.javaspring.model.Etat;
import org.insi.javaspring.model.User;
import org.insi.javaspring.repository.CitoyenRepository;
import org.insi.javaspring.repository.DemandeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemandeService {

    private final DemandeRepository demandeRepository;
    private final CitoyenRepository citoyenRepository;

    public List<Demandecin> findAll() {
        return demandeRepository.findAll();
    }

    public void createDemande(DemandeCinForm form, User connectedUser) {
        Citoyen citoyen = citoyenRepository.findById(form.getCitoyenId())
                .orElseThrow(() -> new RuntimeException("Citoyen introuvable"));

        Demandecin demande = new Demandecin();
        demande.setNumeroDossier(generateNumeroDossier());
        demande.setDemande(form.getDemande());
        demande.setStatus(Etat.EN_ATTENTE);
        demande.setDateDepot(LocalDate.now());
        demande.setUser(connectedUser);
        demande.setCitoyen(citoyen);
        demande.setAgentResponsable("En attente");

        demandeRepository.save(demande);
    }

    public void updateStatus(Long id, Etat status) {
        Demandecin demande = demandeRepository.findById(id).orElseThrow();
        demande.setStatus(status);
        demandeRepository.save(demande);
    }

    public List<Demandecin> findByUser(User user) {
        return demandeRepository.findByUser(user);
    }

    public void deleteById(Long id) {
        demandeRepository.deleteById(id);
    }

    private String generateNumeroDossier() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = demandeRepository.count() + 1;
        return "DOS-" + date + "-" + count;
    }
}