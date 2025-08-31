package gr.hua.dit.ds.ds_lab_2024.repositories;

import gr.hua.dit.ds.ds_lab_2024.entities.RentalApplication;
import gr.hua.dit.ds.ds_lab_2024.entities.Property; // Χρειάζεται για το findByProperty
import gr.hua.dit.ds.ds_lab_2024.entities.User; // Χρειάζεται για το findByTenant
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalApplicationRepository extends JpaRepository<RentalApplication, Integer> {

    // Custom finder methods
    List<RentalApplication> findByProperty(Property property); // Βρίσκει αιτήσεις για ένα συγκεκριμένο ακίνητο
    List<RentalApplication> findByTenant(User tenant); // Βρίσκει αιτήσεις που υποβλήθηκαν από έναν συγκεκριμενο ενοικιαστή


}