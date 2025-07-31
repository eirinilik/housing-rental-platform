package gr.hua.dit.ds.ds_lab_2024.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "rental_applications")
public class RentalApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Η ημερομηνία αίτησης είναι υποχρεωτική")
    @Column(name = "application_date")
    private LocalDate applicationDate;

    @NotNull(message = "Η κατάσταση της αίτησης είναι υποχρεωτική")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ApplicationStatus status; // Νέο Enum: ApplicationStatus

    @Size(max = 500, message = "Το μήνυμα δεν μπορεί να υπερβαίνει τους 500 χαρακτήρες")
    @Column(name = "message", length = 500)
    private String message; // Προαιρετικό μήνυμα από τον ενοικιαστή

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "tenant_id") // Το πεδίο που συνδέει με τον πίνακα users
    private User tenant; // Σχέση Many-to-One με τον ενοικιαστή (User)

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "property_id") // Το πεδίο που συνδέει με τον πίνακα properties
    private Property property; // Σχέση Many-to-One με το ακίνητο

    // Constructors
    public RentalApplication() {
        this.applicationDate = LocalDate.now(); // Αυτόματη αρχικοποίηση με την τρέχουσα ημερομηνία
        this.status = ApplicationStatus.PENDING; // Αρχική κατάσταση PENDING
    }

    public RentalApplication(String message, User tenant, Property property) {
        this(); // Καλούμε τον default constructor για να αρχικοποιήσει ημερομηνία και κατάσταση
        this.message = message;
        this.tenant = tenant;
        this.property = property;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getTenant() {
        return tenant;
    }

    public void setTenant(User tenant) {
        this.tenant = tenant;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return "RentalApplication{" +
                "id=" + id +
                ", applicationDate=" + applicationDate +
                ", status=" + status +
                ", tenant=" + (tenant != null ? tenant.getUsername() : "null") +
                ", property=" + (property != null ? property.getTitle() : "null") +
                '}';
    }
}