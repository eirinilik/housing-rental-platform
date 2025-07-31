package gr.hua.dit.ds.ds_lab_2024.service;

import gr.hua.dit.ds.ds_lab_2024.entities.RentalApplication;
import gr.hua.dit.ds.ds_lab_2024.entities.ApplicationStatus;
import gr.hua.dit.ds.ds_lab_2024.entities.Property;
import gr.hua.dit.ds.ds_lab_2024.entities.User;
import gr.hua.dit.ds.ds_lab_2024.repositories.RentalApplicationRepository;
import gr.hua.dit.ds.ds_lab_2024.repositories.PropertyRepository; // Χρειάζεται για να βρούμε το ακίνητο
import gr.hua.dit.ds.ds_lab_2024.repositories.UserRepository; // Χρειάζεται για να βρούμε τον ενοικιαστή
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RentalApplicationService {

    private final RentalApplicationRepository rentalApplicationRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public RentalApplicationService(RentalApplicationRepository rentalApplicationRepository,
                                    UserRepository userRepository,
                                    PropertyRepository propertyRepository) {
        this.rentalApplicationRepository = rentalApplicationRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
    }

    @Transactional
    public RentalApplication submitApplication(RentalApplication application, Long tenantId, Integer propertyId) {
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + propertyId));

        application.setTenant(tenant);
        application.setProperty(property);
        application.setApplicationDate(LocalDate.now()); // Ορίζουμε την τρέχουσα ημερομηνία
        application.setStatus(ApplicationStatus.PENDING); // Αρχική κατάσταση PENDING

        return rentalApplicationRepository.save(application);
    }

    @Transactional
    public List<RentalApplication> getApplicationsForProperty(Integer propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + propertyId));
        return rentalApplicationRepository.findByProperty(property);
    }

    @Transactional
    public List<RentalApplication> getApplicationsByTenant(Long tenantId) {
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));
        return rentalApplicationRepository.findByTenant(tenant);
    }

    @Transactional
    public RentalApplication approveApplication(Integer applicationId) {
        RentalApplication application = rentalApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Rental Application not found with ID: " + applicationId));
        application.setStatus(ApplicationStatus.APPROVED);
        return rentalApplicationRepository.save(application);
    }

    @Transactional
    public RentalApplication rejectApplication(Integer applicationId) {
        RentalApplication application = rentalApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Rental Application not found with ID: " + applicationId));
        application.setStatus(ApplicationStatus.REJECTED);
        return rentalApplicationRepository.save(application);
    }

    @Transactional
    public RentalApplication getApplicationDetails(Integer applicationId) {
        return rentalApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Rental Application not found with ID: " + applicationId));
    }
}