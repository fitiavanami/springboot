package org.insi.javaspring.repository;

import org.insi.javaspring.model.Demandecin;
import org.insi.javaspring.model.Etat;
import org.insi.javaspring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandeRepository extends JpaRepository<Demandecin, Long> {
    long countByStatus(Etat status);
    List<Demandecin> findByUser(User user);
}