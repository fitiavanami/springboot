package org.insi.javaspring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.insi.javaspring.dto.DemandeCinForm;
import org.insi.javaspring.model.Etat;
import org.insi.javaspring.model.Type;
import org.insi.javaspring.model.User;
import org.insi.javaspring.service.CitoyenServices;
import org.insi.javaspring.service.DemandeService;
import org.insi.javaspring.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/demandes")
public class DemandeController {

    private final DemandeService demandeService;
    private final UserService userService;
    private final CitoyenServices citoyenServices;

    @GetMapping
    public String index(Model model, Authentication authentication) {
        User connectedUser = getConnectedUser(authentication);
        prepareDemandePage(model, connectedUser, new DemandeCinForm());
        return "demandes/demande";
    }

    @PostMapping
    public String addDemande(@Valid @ModelAttribute("newDemande") DemandeCinForm form,
                             BindingResult result,
                             Authentication authentication,
                             Model model) {

        User connectedUser = getConnectedUser(authentication);

        if (result.hasErrors()) {
            prepareDemandePage(model, connectedUser, form);
            return "demandes/demande";
        }

        demandeService.createDemande(form, connectedUser);
        return "redirect:/demandes";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam Etat status) {
        demandeService.updateStatus(id, status);
        return "redirect:/demandes";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String deleteDemande(@PathVariable Long id) {
        demandeService.deleteById(id);
        return "redirect:/demandes";
    }

    private User getConnectedUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email);
    }

    private void prepareDemandePage(Model model, User connectedUser, DemandeCinForm form) {
        boolean isAdmin = connectedUser.getRole().name().equals("ADMIN");

        model.addAttribute("newDemande", form);
        model.addAttribute("types", Type.values());
        model.addAttribute("etats", Etat.values());
        model.addAttribute("citoyens", citoyenServices.getAllCitoyens());
        model.addAttribute("isAdmin", isAdmin);

        if (isAdmin) {
            model.addAttribute("demandes", demandeService.findAll());
        } else {
            model.addAttribute("demandes", demandeService.findByUser(connectedUser));
        }
    }
}