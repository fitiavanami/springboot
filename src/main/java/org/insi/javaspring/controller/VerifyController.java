package org.insi.javaspring.controller;

import lombok.RequiredArgsConstructor;
import org.insi.javaspring.model.Citoyen;
import org.insi.javaspring.service.CitoyenServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class VerifyController {

    private final CitoyenServices citoyenServices;

    @GetMapping("/verify/citoyen/{id}")
    public String verifyCitoyen(@PathVariable Long id, Model model) {
        Citoyen citoyen = citoyenServices.getById(id)
                .orElseThrow(() -> new RuntimeException("Citoyen non trouvé avec l'id " + id));

        model.addAttribute("citoyen", citoyen);
        model.addAttribute("title", "Vérification citoyen");
        model.addAttribute("content", "citoyens/verify");

        return "index";
    }
}