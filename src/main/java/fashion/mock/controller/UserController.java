package fashion.mock.controller;

import fashion.mock.model.User;
import fashion.mock.model.UserRole;
import fashion.mock.service.RoleService;
import fashion.mock.service.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;
   
    @GetMapping
    public String listUsers(Model model, 
                            @RequestParam(defaultValue = "0") int page, 
                            @RequestParam(defaultValue = "3") int size) {
        Page<User> userPage = userService.getAllUsers(PageRequest.of(page, size));
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        return "adminlistuser";
    }

    @GetMapping("/new")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "adminformuser";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("userRoles", user.getUserRoles().stream().map(UserRole::getRole).toList());
        return "adminformuser";
    }

    @PostMapping
    public String addUser(@ModelAttribute User user, @RequestParam List<Long> roleIds, RedirectAttributes redirectAttributes) {
        try {
            userService.addUserWithRoles(user, roleIds);
            redirectAttributes.addFlashAttribute("successMessage", "User added successfully!");
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/users/new";
        }
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user, @RequestParam List<Long> roleIds, RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserWithRoles(user, roleIds);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/users/edit/" + user.getId();
        }
    }
    
    // Xóa category
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = userService.deleteUser(id);
        if (!deleted) {
            redirectAttributes.addFlashAttribute("message", "User không tồn tại");
            redirectAttributes.addFlashAttribute("messageType", "error");
        } else {
            redirectAttributes.addFlashAttribute("message", "User đã được xóa thành công");
            redirectAttributes.addFlashAttribute("messageType", "success");
        }
        return "redirect:/users";
    }
    
    @GetMapping("/search")
    public String searchUsers(@RequestParam(value = "searchTerm", required = false) String searchTerm, 
                              @RequestParam(defaultValue = "0") int page, 
                              @RequestParam(defaultValue = "3") int size,
                              Model model) {
        Page<User> userPage = userService.searchUsers(searchTerm, PageRequest.of(page, size));
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("searchTerm", searchTerm);
        return "adminlistuser";
    }
}