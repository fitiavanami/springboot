package org.insi.javaspring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.insi.javaspring.dto.CitoyenDTO;
import org.insi.javaspring.model.Citoyen;
import org.insi.javaspring.service.CitoyenServices;
import org.insi.javaspring.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/citoyens")
public class CitoyenPageController {

    private final CitoyenServices citoyenServices;
    private final UserService userService;

    @GetMapping
    public String listCitoyens(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model,
            Authentication authentication
    ) {
        Page<Citoyen> citoyenPage = citoyenServices.searchCitoyens(keyword, page, size);

        String email = authentication.getName();
        var connectedUser = userService.findByEmail(email);
        boolean isAdmin = connectedUser.getRole().name().equals("ADMIN");

        model.addAttribute("citoyens", citoyenPage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", citoyenPage.getTotalPages());
        model.addAttribute("isAdmin", isAdmin);

        model.addAttribute("title", "Gestion des citoyens");
        model.addAttribute("content", "home/accueil");

        return "index";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("citoyen", new CitoyenDTO());
        model.addAttribute("title", "Ajouter citoyen");
        model.addAttribute("content", "citoyens/form");
        model.addAttribute("isEdit", false);
        return "index";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String addCitoyen(
            @Valid @ModelAttribute("citoyen") CitoyenDTO dto,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Ajouter citoyen");
            model.addAttribute("content", "citoyens/form");
            model.addAttribute("isEdit", false);
            return "index";
        }

        citoyenServices.saveFromDto(dto);
        return "redirect:/citoyens";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Citoyen citoyen = citoyenServices.getById(id)
                .orElseThrow(() -> new RuntimeException("Citoyen non trouvé avec l'id " + id));

        CitoyenDTO dto = new CitoyenDTO();
        dto.setCin(citoyen.getCin());
        dto.setNom(citoyen.getNom());
        dto.setPrenom(citoyen.getPrenom());
        dto.setDateNaissance(citoyen.getDateNaissance());
        dto.setLieuNaissance(citoyen.getLieuNaissance());
        dto.setSexe(citoyen.getSexe());
        dto.setAdresse(citoyen.getAdresse());
        dto.setProfession(citoyen.getProfession());
        dto.setPhoto(citoyen.getPhoto());

        model.addAttribute("citoyen", dto);
        model.addAttribute("citoyenId", id);
        model.addAttribute("title", "Modifier citoyen");
        model.addAttribute("content", "citoyens/form");
        model.addAttribute("isEdit", true);

        return "index";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit/{id}")
    public String updateCitoyen(
            @PathVariable Long id,
            @Valid @ModelAttribute("citoyen") CitoyenDTO dto,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("citoyenId", id);
            model.addAttribute("title", "Modifier citoyen");
            model.addAttribute("content", "citoyens/form");
            model.addAttribute("isEdit", true);
            return "index";
        }

        citoyenServices.updateCitoyen(id, dto);
        return "redirect:/citoyens";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteCitoyen(@PathVariable Long id) {
        citoyenServices.deleteById(id);
        return "redirect:/citoyens";
    }

    @GetMapping("/verify/{id}")
    public String verifyCitoyen(@PathVariable Long id, Model model) {
        Citoyen citoyen = citoyenServices.getById(id)
                .orElseThrow(() -> new RuntimeException("Citoyen non trouvé avec l'id " + id));

        model.addAttribute("citoyen", citoyen);
        model.addAttribute("title", "Vérification citoyen");
        model.addAttribute("content", "citoyens/verify");

        return "index";
    }
}