package gr.hua.dit.ds.ds_lab_2024.controllers;

import gr.hua.dit.ds.ds_lab_2024.entities.Role;
import gr.hua.dit.ds.ds_lab_2024.entities.User;
import gr.hua.dit.ds.ds_lab_2024.repositories.RoleRepository;
import gr.hua.dit.ds.ds_lab_2024.service.UserService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private UserService userService;

    private RoleRepository roleRepository;

    public UserController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }
    //GET για τη σελίδα εγγραφής.
    @GetMapping("/register")
    public String register(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "auth/register";
    }

    //Χειρίζεται τα  POST από τον διαχειριστή για την έγκριση του αιτούμενου ρόλου ενός χρήστη
    @Secured("ROLE_ADMIN")
    @PostMapping("/user/approve-role/{id}")
    public String approveRequestedRole(@PathVariable Long id, @RequestParam("requestedRole") String roleName, Model model){
        userService.approveRequestedRole(id, roleName); // Νέα μέθοδος στο service
        model.addAttribute("users", userService.getUsers());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("successMessage", "Ο ρόλος του χρήστη εγκρίθηκε επιτυχώς!");
        return "redirect:/users";
    }
//     Χειρίζεται τα  POST για την εγγραφή νέου χρήστη.
    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user, @RequestParam("role") String roleName, Model model){
        // Προσθέστε αυτή τη γραμμή για να δείτε τι τιμή λαμβάνει ο controller
        System.out.println("Επιλεγμένος ρόλος από τη φόρμα: " + roleName);

        Long id = userService.saveUserWithRequestedRole(user, roleName);
        String message = "User '"+id+"' saved successfully! The requested role '" + roleName + "' awaits admin approval.";
        model.addAttribute("msg", message);
        return "index";
    }
   // Χειρίζεται τα αιτήματα GET για την εμφάνιση της σελίδας διαχείρισης χρηστών για τον admin
    @GetMapping("/users")
    public String showUsers(Model model){
        model.addAttribute("users", userService.getUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "auth/users";
    }
//    Χειρίζεται τα  GET για την εμφάνιση των λεπτομερειών ενός χρήστη προς επεξεργασία.
    @GetMapping("/user/{user_id}")
    public String showUser(@PathVariable Long user_id, Model model){
        model.addAttribute("user", userService.getUser(user_id));
        return "auth/user";
    }
//     Χειρίζεται τα  POST για την αποθήκευση των ενημερωμένων πληροφοριών ενός χρήστη.
    @PostMapping("/user/{user_id}")
    public String saveStudent(@PathVariable Long user_id, @ModelAttribute("user") User user, Model model) {
        User the_user = (User) userService.getUser(user_id);
        the_user.setEmail(user.getEmail());
        the_user.setUsername(user.getUsername());
        userService.updateUser(the_user);
        model.addAttribute("users", userService.getUsers());
        return "auth/users";
    }
//    Χειρίζεται τα  GET για τη διαγραφή ενός συγκεκριμένου ρόλου από έναν χρήστη
    @GetMapping("/user/role/delete/{user_id}/{role_id}")
    public String deleteRolefromUser(@PathVariable Long user_id, @PathVariable Integer role_id, Model model){
        User user = (User) userService.getUser(user_id);
        Role role = roleRepository.findById(role_id).get();
        user.getRoles().remove(role);
        System.out.println("Roles: "+user.getRoles());
        userService.updateUser(user);
        model.addAttribute("users", userService.getUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "auth/users";

    }
//     Χειρίζεται τα αιτήματα GET για την προσθήκη ενός συγκεκριμένου ρόλου σε έναν χρήστη.
    @GetMapping("/user/role/add/{user_id}/{role_id}")
    public String addRoletoUser(@PathVariable Long user_id, @PathVariable Integer role_id, Model model){
        User user = (User) userService.getUser(user_id);
        Role role = roleRepository.findById(role_id).get();
        user.getRoles().add(role);
        System.out.println("Roles: "+user.getRoles());
        userService.updateUser(user);
        model.addAttribute("users", userService.getUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "auth/users";

    }
}