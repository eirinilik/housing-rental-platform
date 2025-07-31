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

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/") // Το βασικό path για την αρχική σελίδα και properties
public class PropertyController {

    private final PropertyService propertyService;
    private final UserService userService;

    public PropertyController(PropertyService propertyService, UserService userService) {
        this.propertyService = propertyService;
        this.userService = userService;
    }

    // GET /properties: Εμφάνιση όλων των εγκεκριμένων ακινήτων (για ενοικιαστές/επισκέπτες)
    @GetMapping("/properties")
    public String listApprovedProperties(Model model) {
        List<Property> properties = propertyService.getApprovedProperties();
        model.addAttribute("properties", properties);
        return "property/properties"; // Θα δημιουργήσουμε αυτό το template
    }

    // GET /properties/{id}: Λεπτομέρειες ακινήτου
    @GetMapping("/properties/{id}")
    public String showPropertyDetails(@PathVariable Integer id, Model model) {
        try {
            Property property = propertyService.getPropertyDetails(id);
            model.addAttribute("property", property);
            return "property/property-details"; // Θα δημιουργήσουμε αυτό το template
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found", e);
        }
    }

    // GET /properties/new (ROLE_OWNER): Φόρμα για νέα καταχώρηση ακινήτου
    @Secured("ROLE_OWNER") // Μόνο ο ιδιοκτήτης μπορεί να καταχωρήσει
    @GetMapping("/properties/new")
    public String showPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        model.addAttribute("propertyTypes", PropertyType.values()); // Για το dropdown των τύπων ακινήτων
        return "property/property-form"; // Θα δημιουργήσουμε αυτό το template
    }

    // POST /properties/new (ROLE_OWNER): Υποβολή νέας καταχώρησης
    @Secured("ROLE_OWNER")
    @PostMapping("/properties/new")
    public String saveProperty(@Valid @ModelAttribute("property") Property property,
                               BindingResult bindingResult,
                               Authentication authentication, // Για να πάρουμε τον συνδεδεμένο χρήστη
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("propertyTypes", PropertyType.values());
            return "property/property-form";
        }

        // Λαμβάνουμε το username του συνδεδεμένου χρήστη
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.getUserByUsername(userDetails.getUsername()); // Θα προσθέσουμε αυτή τη μέθοδο στο UserService

        propertyService.createProperty(property, currentUser.getId()); // Δημιουργία ακινήτου με τον ιδιοκτήτη
        model.addAttribute("successMessage", "Το ακίνητο καταχωρήθηκε επιτυχώς! Αναμένει έγκριση διαχειριστή.");
        return "redirect:/properties/my"; // Ανακατεύθυνση στη λίστα ακινήτων του ιδιοκτήτη
    }

    // GET /properties/my (ROLE_OWNER): Λίστα ακινήτων του συνδεδεμένου ιδιοκτήτη
    @Secured("ROLE_OWNER")
    @GetMapping("/properties/my")
    public String listMyProperties(Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.getUserByUsername(userDetails.getUsername());

        List<Property> myProperties = propertyService.getPropertiesForOwner(currentUser.getId());
        model.addAttribute("properties", myProperties);
        return "property/my-properties"; // Θα δημιουργήσουμε αυτό το template
    }


    // GET /admin/properties (ROLE_ADMIN): Λίστα όλων των ακινήτων (περιλαμβανομένων των μη εγκεκριμένων)
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/properties")
    public String listAllProperties(Model model) {
        List<Property> properties = propertyService.getAllProperties();
        model.addAttribute("properties", properties);
        return "admin/properties"; // Θα δημιουργήσουμε αυτό το template
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
    @Secured({"ROLE_ADMIN", "ROLE_OWNER"}) // Διαχειριστής ή ο ίδιος ο ιδιοκτήτης μπορεί να διαγράψει
    @PostMapping("/properties/{id}/delete")
    public String deleteProperty(@PathVariable Integer id, Authentication authentication, Model model) {
        try {
            // Ελέγχουμε αν ο χρήστης είναι Admin ή ο ιδιοκτήτης του ακινήτου
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
                // Προαιρετικά, μπορείτε να πετάξετε ResponseStatusException(HttpStatus.FORBIDDEN)
            }
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Σφάλμα κατά τη διαγραφή ακινήτου: " + e.getMessage());
        }
        // Ανακατεύθυνση ανάλογα με τον ρόλο μετά τη διαγραφή
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/properties";
        } else {
            return "redirect:/properties/my";
        }
    }
}