package gr.hua.dit.ds.ds_lab_2024.controllers;

import gr.hua.dit.ds.ds_lab_2024.entities.Property;
import gr.hua.dit.ds.ds_lab_2024.entities.RentalApplication;
import gr.hua.dit.ds.ds_lab_2024.entities.User;
import gr.hua.dit.ds.ds_lab_2024.service.PropertyService;
import gr.hua.dit.ds.ds_lab_2024.service.RentalApplicationService;
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
public class RentalApplicationController {

    private final RentalApplicationService rentalApplicationService;
    private final PropertyService propertyService; // Χρειάζεται για να βρούμε τα ακίνητα
    private final UserService userService;

    public RentalApplicationController(RentalApplicationService rentalApplicationService,
                                       PropertyService propertyService,
                                       UserService userService) {
        this.rentalApplicationService = rentalApplicationService;
        this.propertyService = propertyService;
        this.userService = userService;
    }

    // GET /properties/{id}/apply (ROLE_TENANT): Φόρμα αίτησης ενοικίασης για συγκεκριμένο ακίνητο
    @Secured("ROLE_TENANT") // Μόνο ενοικιαστές μπορούν να υποβάλουν αίτηση
    @GetMapping("/properties/{propertyId}/apply")
    public String showApplyForm(@PathVariable Integer propertyId, Model model) {
        try {
            Property property = propertyService.getPropertyDetails(propertyId);
            model.addAttribute("property", property);
            model.addAttribute("rentalApplication", new RentalApplication()); // Κενή αίτηση για τη φόρμα
            return "application/apply-form"; // Θα δημιουργήσουμε αυτό το template
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found", e);
        }
    }

    // POST /properties/{id}/apply (ROLE_TENANT): Υποβολή αίτησης
    @Secured("ROLE_TENANT")
    @PostMapping("/properties/{propertyId}/apply")
    public String submitApplication(@PathVariable Integer propertyId,
                                    @Valid @ModelAttribute("rentalApplication") RentalApplication rentalApplication,
                                    BindingResult bindingResult,
                                    Authentication authentication,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            Property property = propertyService.getPropertyDetails(propertyId);
            model.addAttribute("property", property);
            return "application/apply-form";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.getUserByUsername(userDetails.getUsername());

        try {
            rentalApplicationService.submitApplication(rentalApplication, currentUser.getId(), propertyId);
            model.addAttribute("successMessage", "Η αίτησή σας υποβλήθηκε επιτυχώς!");
            return "redirect:/tenant/my-applications"; // Ανακατεύθυνση στις αιτήσεις του ενοικιαστή
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Σφάλμα κατά την υποβολή αίτησης: " + e.getMessage());
            Property property = propertyService.getPropertyDetails(propertyId);
            model.addAttribute("property", property);
            return "application/apply-form";
        }
    }

    // GET /owner/applications (ROLE_OWNER): Λίστα αιτήσεων για ακίνητα του ιδιοκτήτη
    @Secured("ROLE_OWNER")
    @GetMapping("/owner/applications")
    public String listOwnerApplications(Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.getUserByUsername(userDetails.getUsername());

        // Παίρνουμε όλα τα ακίνητα του ιδιοκτήτη
        List<Property> ownedProperties = propertyService.getPropertiesForOwner(currentUser.getId());
        List<RentalApplication> ownerApplications = ownedProperties.stream()
                .flatMap(property -> rentalApplicationService.getApplicationsForProperty(property.getId()).stream())
                .toList(); // .collect(Collectors.toList()) για παλαιότερες Java versions

        model.addAttribute("applications", ownerApplications);
        return "application/owner-applications"; // Θα δημιουργήσουμε αυτό το template
    }

    // POST /applications/{id}/approve (ROLE_OWNER): Έγκριση αίτησης
    @Secured("ROLE_OWNER")
    @PostMapping("/applications/{id}/approve")
    public String approveApplication(@PathVariable Integer id, Authentication authentication, Model model) {
        try {
            RentalApplication application = rentalApplicationService.getApplicationDetails(id);
            // Ελέγχουμε αν ο συνδεδεμένος χρήστης είναι ο ιδιοκτήτης του ακινήτου
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User currentUser = userService.getUserByUsername(userDetails.getUsername());

            if (application.getProperty().getOwner().getId().equals(currentUser.getId())) {
                rentalApplicationService.approveApplication(id);
                model.addAttribute("successMessage", "Η αίτηση εγκρίθηκε επιτυχώς.");
            } else {
                model.addAttribute("errorMessage", "Δεν έχετε δικαίωμα να εγκρίνετε αυτή την αίτηση.");
            }
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Σφάλμα κατά την έγκριση αίτησης: " + e.getMessage());
        }
        return "redirect:/owner/applications";
    }

    // POST /applications/{id}/reject (ROLE_OWNER): Απόρριψη αίτησης
    @Secured("ROLE_OWNER")
    @PostMapping("/applications/{id}/reject")
    public String rejectApplication(@PathVariable Integer id, Authentication authentication, Model model) {
        try {
            RentalApplication application = rentalApplicationService.getApplicationDetails(id);
            // Ελέγχουμε αν ο συνδεδεμένος χρήστης είναι ο ιδιοκτήτης του ακινήτου
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User currentUser = userService.getUserByUsername(userDetails.getUsername());

            if (application.getProperty().getOwner().getId().equals(currentUser.getId())) {
                rentalApplicationService.rejectApplication(id);
                model.addAttribute("successMessage", "Η αίτηση απορρίφθηκε επιτυχώς.");
            } else {
                model.addAttribute("errorMessage", "Δεν έχετε δικαίωμα να απορρίψετε αυτή την αίτηση.");
            }
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Σφάλμα κατά την απόρριψη αίτησης: " + e.getMessage());
        }
        return "redirect:/owner/applications";
    }

    // GET /tenant/my-applications (ROLE_TENANT): Λίστα των αιτήσεων του ενοικιαστή
    @Secured("ROLE_TENANT")
    @GetMapping("/tenant/my-applications")
    public String listTenantApplications(Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.getUserByUsername(userDetails.getUsername());

        List<RentalApplication> myApplications = rentalApplicationService.getApplicationsByTenant(currentUser.getId());
        model.addAttribute("applications", myApplications);
        return "application/tenant-applications"; // Θα δημιουργήσουμε αυτό το template
    }
}