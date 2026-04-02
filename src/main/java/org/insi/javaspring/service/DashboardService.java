package org.insi.javaspring.service;

import lombok.RequiredArgsConstructor;
import org.insi.javaspring.model.Etat;
import org.insi.javaspring.repository.CitoyenRepository;
import org.insi.javaspring.repository.DemandeRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CitoyenRepository citoyenRepository;
    private final DemandeRepository demandeRepository;

    public Map<String, Long> getStats() {
        Map<String, Long> stats = new LinkedHashMap<>();

        stats.put("totalCitoyens", citoyenRepository.count());
        stats.put("totalDemandes", demandeRepository.count());
        stats.put("enAttente", demandeRepository.countByStatus(Etat.EN_ATTENTE));
        stats.put("enCours", demandeRepository.countByStatus(Etat.EN_COURS));
        stats.put("validees", demandeRepository.countByStatus(Etat.VALIDEE));
        stats.put("rejetees", demandeRepository.countByStatus(Etat.REJETEE));
        stats.put("imprimees", demandeRepository.countByStatus(Etat.IMPRIMEE));

        return stats;
    }
}