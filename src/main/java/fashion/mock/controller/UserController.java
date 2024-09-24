	/**
	 * Author: Ngô Văn Quốc Thắng 11/05/1996
	 */
package fashion.mock.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fashion.mock.model.User;
import fashion.mock.model.UserRole;
import fashion.mock.service.RoleService;
import fashion.mock.service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;
   
	/**
	 * Author: Ngô Văn Quốc Thắng 11/05/1996
	 */
    @GetMapping
    public String listUsers(Model model, 
                            @RequestParam(defaultValue = "0") int page, 
                            @RequestParam(defaultValue = "5") int size) {
        Page<User> userPage = userService.getAllUsers(PageRequest.of(page, size));
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        return "adminlistuser";
    }

	/**
	 * Author: Ngô Văn Quốc Thắng 11/05/1996
	 */
    @GetMapping("/new")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "adminformuser";
    }

	/**
	 * Author: Ngô Văn Quốc Thắng 11/05/1996
	 */
    @GetMapping("/edit/{id}")
    public String showUpdateUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("userRoles", user.getUserRoles().stream().map(UserRole::getRole).toList());
        return "adminformuser";
    }

	/**
	 * Author: Ngô Văn Quốc Thắng 11/05/1996
	 */
    @PostMapping
    public String addUser(@Valid @ModelAttribute User user, BindingResult bindingResult, 
                          @RequestParam(required = false) List<Long> roleIds, 
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (bindingResult.hasErrors() || roleIds == null || roleIds.isEmpty()) {
            if (roleIds == null || roleIds.isEmpty()) {
                model.addAttribute("roleError", "Vui lòng chọn ít nhất một vai trò.");
            }
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "adminformuser";
        }

        try {
            userService.addUserWithRoles(user, roleIds); // Role validation now handled in the service
            redirectAttributes.addFlashAttribute("successMessage", "User added successfully!");
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("email", "error.user", e.getMessage());
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "adminformuser";
        }
    }

    @PostMapping("/update")
    public String updateUser(@Valid @ModelAttribute User user, BindingResult bindingResult,
                             @RequestParam(required = false) List<Long> roleIds, 
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors() || roleIds == null || roleIds.isEmpty()) {
            if (roleIds == null || roleIds.isEmpty()) {
                model.addAttribute("roleError", "Vui lòng chọn ít nhất một vai trò.");
            }
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "adminformuser";
        }
        try {
            userService.updateUserWithRoles(user, roleIds);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("email", "error.user", e.getMessage());
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "adminformuser";
        }
    }
    
	/**
	 * Author: Ngô Văn Quốc Thắng 11/05/1996
	 */
    // Xóa category
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = userService.deleteUser(id);
        if (!deleted) {
            redirectAttributes.addFlashAttribute("message", "Người dùng không tồn tại");
            redirectAttributes.addFlashAttribute("messageType", "error");
        } else {
            redirectAttributes.addFlashAttribute("message", "Người dùng đã được xóa thành công");
            redirectAttributes.addFlashAttribute("messageType", "success");
        }
        return "redirect:/users";
    }
    
	/**
	 * Author: Ngô Văn Quốc Thắng 11/05/1996
	 */
    @GetMapping("/search")
    public String searchUsers(@RequestParam(value = "searchTerm", required = false) String searchTerm, 
                              @RequestParam(defaultValue = "0") int page, 
                              @RequestParam(defaultValue = "5") int size,
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