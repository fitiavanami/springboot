package org.insi.javaspring.service;

import lombok.RequiredArgsConstructor;
import org.insi.javaspring.dto.CitoyenDTO;
import org.insi.javaspring.model.Citoyen;
import org.insi.javaspring.repository.CitoyenRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CitoyenServices {

    private final CitoyenRepository repository;
    private final AesService aesService;

    public Citoyen saveFromDto(CitoyenDTO dto) {
        Citoyen citoyen = Citoyen.builder()
                .cin(aesService.encrypt(dto.getCin()))
                .nom(aesService.encrypt(dto.getNom()))
                .prenom(aesService.encrypt(dto.getPrenom()))
                .dateNaissance(dto.getDateNaissance())
                .lieuNaissance(aesService.encrypt(dto.getLieuNaissance()))
                .sexe(dto.getSexe())
                .adresse(aesService.encrypt(dto.getAdresse()))
                .profession(aesService.encrypt(dto.getProfession()))
                .photo(dto.getPhoto())
                .build();

        Citoyen saved = repository.save(citoyen);
        return decryptCitoyen(saved);
    }

    public List<Citoyen> getAllCitoyens() {
        return repository.findAll()
                .stream()
                .map(this::decryptCitoyen)
                .toList();
    }

    public Optional<Citoyen> getById(Long id) {
        return repository.findById(id).map(this::decryptCitoyen);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Page<Citoyen> searchCitoyens(String keyword, int page, int size) {
        List<Citoyen> all = repository.findAll()
                .stream()
                .map(this::decryptCitoyen)
                .toList();

        List<Citoyen> filtered;

        if (keyword == null || keyword.isBlank()) {
            filtered = all;
        } else {
            String search = keyword.toLowerCase();
            filtered = all.stream()
                    .filter(c ->
                            c.getNom().toLowerCase().contains(search) ||
                                    c.getPrenom().toLowerCase().contains(search) ||
                                    c.getCin().toLowerCase().contains(search))
                    .toList();
        }

        int start = page * size;
        int end = Math.min(start + size, filtered.size());

        List<Citoyen> content = start >= filtered.size()
                ? List.of()
                : filtered.subList(start, end);

        return new PageImpl<>(content, PageRequest.of(page, size), filtered.size());
    }

    public Citoyen updateCitoyen(Long id, CitoyenDTO dto) {
        Citoyen updated = repository.findById(id)
                .map(c -> {
                    c.setCin(aesService.encrypt(dto.getCin()));
                    c.setNom(aesService.encrypt(dto.getNom()));
                    c.setPrenom(aesService.encrypt(dto.getPrenom()));
                    c.setDateNaissance(dto.getDateNaissance());
                    c.setLieuNaissance(aesService.encrypt(dto.getLieuNaissance()));
                    c.setSexe(dto.getSexe());
                    c.setAdresse(aesService.encrypt(dto.getAdresse()));
                    c.setProfession(aesService.encrypt(dto.getProfession()));
                    c.setPhoto(dto.getPhoto());
                    return repository.save(c);
                })
                .orElseThrow(() -> new RuntimeException("Citoyen non trouvé avec l'id " + id));

        return decryptCitoyen(updated);
    }

    private Citoyen decryptCitoyen(Citoyen c) {
        return Citoyen.builder()
                .id(c.getId())
                .cin(aesService.decrypt(c.getCin()))
                .nom(aesService.decrypt(c.getNom()))
                .prenom(aesService.decrypt(c.getPrenom()))
                .dateNaissance(c.getDateNaissance())
                .lieuNaissance(aesService.decrypt(c.getLieuNaissance()))
                .sexe(c.getSexe())
                .adresse(aesService.decrypt(c.getAdresse()))
                .profession(aesService.decrypt(c.getProfession()))
                .photo(c.getPhoto())
                .build();
    }
}