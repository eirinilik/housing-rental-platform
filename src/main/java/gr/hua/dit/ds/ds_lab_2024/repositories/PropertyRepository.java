package gr.hua.dit.ds.ds_lab_2024.repositories;

import gr.hua.dit.ds.ds_lab_2024.entities.Property;
import gr.hua.dit.ds.ds_lab_2024.entities.User; // Χρειάζεται για το findByOwner
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Integer> {

    // Custom finder methods (σύμφωνα με τις απαιτήσεις)
    List<Property> findByIsApprovedTrue(); // Βρίσκει όλα τα εγκεκριμένα ακίνητα
    List<Property> findByOwner(User owner); // Βρίσκει ακίνητα βάσει ιδιοκτήτη
    List<Property> findByIsApprovedFalse(); // Βρίσκει ακίνητα που περιμένουν έγκριση (για Admin)

    // Μπορείτε να προσθέσετε και άλλες μεθόδους αναζήτησης/φιλτραρίσματος αργότερα, π.χ.
    // List<Property> findByAddressContainingIgnoreCase(String keyword);
    // List<Property> findByPropertyType(PropertyType type);
    // List<Property> findByRentAmountBetween(Double minRent, Double maxRent);
}