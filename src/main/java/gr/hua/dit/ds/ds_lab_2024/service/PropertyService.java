package gr.hua.dit.ds.ds_lab_2024.service;

import gr.hua.dit.ds.ds_lab_2024.entities.Property;
import gr.hua.dit.ds.ds_lab_2024.entities.User;
import gr.hua.dit.ds.ds_lab_2024.repositories.PropertyRepository;
import gr.hua.dit.ds.ds_lab_2024.repositories.UserRepository; // Αν χρειαστεί για να βρούμε User objects
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository; // Χρειάζεται για να βρούμε τον ιδιοκτήτη

    public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Property createProperty(Property property, Long ownerId) {
        // Βρίσκουμε τον ιδιοκτήτη από το ID του
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));

        property.setOwner(owner);
        property.setIsApproved(false); // Νέα ακίνητα απαιτούν έγκριση διαχειριστή
        return propertyRepository.save(property);
    }

    @Transactional
    public List<Property> getApprovedProperties() {
        // Επιστρέφει μόνο τα εγκεκριμένα ακίνητα για εμφάνιση στους ενοικιαστές
        return propertyRepository.findByIsApprovedTrue();
    }

    @Transactional
    public List<Property> getAllProperties() {
        // Επιστρέφει όλα τα ακίνητα (εγκεκριμένα και μη) - πιθανόν για διαχειριστή
        return propertyRepository.findAll();
    }

    @Transactional
    public List<Property> getPropertiesForOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));
        return propertyRepository.findByOwner(owner);
    }

    @Transactional
    public Property getPropertyDetails(Integer propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + propertyId));
    }

    @Transactional
    public Property approveProperty(Integer propertyId) {
        Property property = getPropertyDetails(propertyId);
        property.setIsApproved(true);
        return propertyRepository.save(property);
    }

    @Transactional
    public void deleteProperty(Integer propertyId) {
        // Ελέγχουμε αν υπάρχει το ακίνητο πριν το διαγράψουμε
        if (!propertyRepository.existsById(propertyId)) {
            throw new RuntimeException("Property not found with ID: " + propertyId);
        }
        propertyRepository.deleteById(propertyId);
    }
}