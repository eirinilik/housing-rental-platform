package gr.hua.dit.ds.ds_lab_2024.controllers;

import gr.hua.dit.ds.ds_lab_2024.entities.Property;
import gr.hua.dit.ds.ds_lab_2024.entities.PropertyType;
import gr.hua.dit.ds.ds_lab_2024.entities.User;
import gr.hua.dit.ds.ds_lab_2024.service.PropertyService;
import gr.hua.dit.ds.ds_lab_2024.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Controller
@RequestMapping("/")
public class PropertyController {

    private final PropertyService propertyService;
    private final UserService userService;

    public PropertyController(PropertyService propertyService, UserService userService) {
        this.propertyService = propertyService;
        this.userService = userService;
    }

    // GET /properties: Εμφάνιση όλων των εγκεκριμένων ακινήτων (με φίλτρα)
    @GetMapping("/properties")
    public String listApprovedProperties(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) Double minRent,
            @RequestParam(required = false) Double maxRent,
            Model model) {

        // ΚΑΛΟΥΜΕ τη νέα μέθοδο αναζήτησης
        List<Property> properties = propertyService.searchApprovedProperties(address, propertyType, minRent, maxRent);

        model.addAttribute("properties", properties);
        model.addAttribute("address", address);
        model.addAttribute("propertyType", propertyType);
        model.addAttribute("minRent", minRent);
        model.addAttribute("maxRent", maxRent);
        model.addAttribute("propertyTypes", PropertyType.values());

        return "property/properties";
    }

    // GET /properties/{id}: Λεπτομέρειες ακινήτου
    @GetMapping("/properties/{id}")
    public String showPropertyDetails(@PathVariable Integer id, Model model) {
        try {
            Property property = propertyService.getPropertyDetails(id);
            model.addAttribute("property", property);
            return "property/property-details";
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found", e);
        }
    }

    // GET /properties/new (ROLE_OWNER): Φόρμα για νέα καταχώρηση ακινήτου
    @Secured("ROLE_OWNER")
    @GetMapping("/properties/new")
    public String showPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        model.addAttribute("propertyTypes", PropertyType.values());
        return "property/property-form";
    }

    // POST /properties/new (ROLE_OWNER): Υποβολή νέας καταχώρησης
    @Secured("ROLE_OWNER")
    @PostMapping("/properties/new")
    public String saveProperty(@Valid @ModelAttribute("property") Property property,
                               BindingResult bindingResult,
                               Authentication authentication,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("propertyTypes", PropertyType.values());
            return "property/property-form";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.getUserByUsername(userDetails.getUsername());

        propertyService.createProperty(property, currentUser.getId());
        model.addAttribute("successMessage", "Το ακίνητο καταχωρήθηκε επιτυχώς! Αναμένει έγκριση διαχειριστή.");
        return "redirect:/properties/my";
    }

    // GET /properties/my (ROLE_OWNER): Λίστα ακινήτων του συνδεδεμένου ιδιοκτήτη
    @Secured("ROLE_OWNER")
    @GetMapping("/properties/my")
    public String listMyProperties(Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.getUserByUsername(userDetails.getUsername());

        List<Property> myProperties = propertyService.getPropertiesForOwner(currentUser.getId());
        model.addAttribute("properties", myProperties);
        return "property/my-properties";
    }


    // GET /admin/properties (ROLE_ADMIN): Λίστα όλων των ακινήτων (περιλαμβανομένων των μη εγκεκριμένων)
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/properties")
    public String listAllProperties(Model model) {
        List<Property> properties = propertyService.getAllProperties();
        model.addAttribute("properties", properties);
        return "admin/properties";
    }

    // POST /admin/properties/{id}/approve (ROLE_ADMIN): Έγκριση ακινήτου
    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/properties/{id}/approve")
    public String approveProperty(@PathVariable Integer id, Model model) {
        try {
            propertyService.approveProperty(id);
            model.addAttribute("successMessage", "Το ακίνητο εγκρίθηκε επιτυχώς.");
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Σφάλμα κατά την έγκριση ακινήτου: " + e.getMessage());
        }
        return "redirect:/admin/properties";
    }

    // POST /properties/{id}/delete (ROLE_ADMIN/OWNER): Διαγραφή ακινήτου
    @Secured({"ROLE_ADMIN", "ROLE_OWNER"})
    @PostMapping("/properties/{id}/delete")
    public String deleteProperty(@PathVariable Integer id, Authentication authentication, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User currentUser = userService.getUserByUsername(userDetails.getUsername());
            Property propertyToDelete = propertyService.getPropertyDetails(id);

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isOwner = propertyToDelete.getOwner() != null && propertyToDelete.getOwner().getId().equals(currentUser.getId());

            if (isAdmin || isOwner) {
                propertyService.deleteProperty(id);
                model.addAttribute("successMessage", "Το ακίνητο διαγράφηκε επιτυχώς.");
            } else {
                model.addAttribute("errorMessage", "Δεν έχετε δικαίωμα να διαγράψετε αυτό το ακίνητο.");
            }
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Σφάλμα κατά τη διαγραφή ακινήτου: " + e.getMessage());
        }
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/properties";
        } else {
            return "redirect:/properties/my";
        }
    }
}