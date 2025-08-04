package gr.hua.dit.ds.ds_lab_2024.service;

import gr.hua.dit.ds.ds_lab_2024.entities.Property;
import gr.hua.dit.ds.ds_lab_2024.entities.PropertyType;
import gr.hua.dit.ds.ds_lab_2024.entities.User;
import gr.hua.dit.ds.ds_lab_2024.repositories.PropertyRepository;
import gr.hua.dit.ds.ds_lab_2024.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Property createProperty(Property property, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));

        property.setOwner(owner);
        property.setIsApproved(false);
        return propertyRepository.save(property);
    }

    // Η μέθοδος αναζήτησης και φιλτραρίσματος
    @Transactional
    public List<Property> searchApprovedProperties(String address, String propertyType, Double minRent, Double maxRent) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Property> cq = cb.createQuery(Property.class);
        Root<Property> property = cq.from(Property.class);
        List<Predicate> predicates = new ArrayList<>();

        // 1. Φιλτράρουμε μόνο τα εγκεκριμένα ακίνητα
        predicates.add(cb.isTrue(property.get("isApproved")));

        // 2. Φιλτράρισμα βάσει διεύθυνσης
        if (address != null && !address.isBlank()) {
            predicates.add(cb.like(property.get("address"), "%" + address + "%"));
        }

        // 3. Φιλτράρισμα βάσει τύπου ακινήτου
        if (propertyType != null && !propertyType.isBlank()) {
            predicates.add(cb.equal(property.get("propertyType"), PropertyType.valueOf(propertyType)));
        }

        // 4. Φιλτράρισμα βάσει ελάχιστου ενοικίου
        if (minRent != null) {
            predicates.add(cb.greaterThanOrEqualTo(property.get("rentAmount"), minRent));
        }

        // 5. Φιλτράρισμα βάσει μέγιστου ενοικίου
        if (maxRent != null) {
            predicates.add(cb.lessThanOrEqualTo(property.get("rentAmount"), maxRent));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(cq).getResultList();
    }

    @Transactional
    public List<Property> getAllProperties() {
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
        if (!propertyRepository.existsById(propertyId)) {
            throw new RuntimeException("Property not found with ID: " + propertyId);
        }
        propertyRepository.deleteById(propertyId);
    }
}