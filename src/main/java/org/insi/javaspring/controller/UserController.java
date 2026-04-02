package org.insi.javaspring.controller;

import lombok.RequiredArgsConstructor;
import org.insi.javaspring.model.Role;
import org.insi.javaspring.model.User;
import org.insi.javaspring.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/admin/users")
    public String listUsers(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "") String keyword) {

        int pageSize = 5;

        Page<User> userPage = userService.searchUsers(
                keyword,
                PageRequest.of(page, pageSize, Sort.by("createdAt").descending())
        );

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("roles", Role.values());
        model.addAttribute("keyword", keyword);

        return "users/list";
    }

    @PostMapping("/admin/users/{id}/role")
    public String updateRole(@PathVariable Long id,
                             @RequestParam Role role,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "") String keyword) {
        userService.updateRole(id, role);
        return "redirect:/admin/users?page=" + page + "&keyword=" + keyword;
    }

    @PostMapping("/admin/users/{id}/enabled")
    public String setEnabled(@PathVariable Long id,
                             @RequestParam boolean enabled,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "") String keyword) {
        userService.setEnabled(id, enabled);
        return "redirect:/admin/users?page=" + page + "&keyword=" + keyword;
    }

    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "") String keyword) {
        userService.deleteUser(id);
        return "redirect:/admin/users?page=" + page + "&keyword=" + keyword;
    }
}