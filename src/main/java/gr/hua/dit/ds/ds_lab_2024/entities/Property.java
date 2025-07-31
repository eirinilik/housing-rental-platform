package gr.hua.dit.ds.ds_lab_2024.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "properties") // Ονομασία πίνακα στη βάση δεδομένων
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Ο τίτλος είναι υποχρεωτικός")
    @Size(min = 3, max = 100, message = "Ο τίτλος πρέπει να είναι μεταξύ 3 και 100 χαρακτήρων")
    @Column(name = "title")
    private String title;

    @NotBlank(message = "Η περιγραφή είναι υποχρεωτική")
    @Column(name = "description", length = 1000) // Μεγαλύτερο μήκος για την περιγραφή
    private String description;

    @NotBlank(message = "Η διεύθυνση είναι υποχρεωτική")
    @Column(name = "address")
    private String address;

    @NotNull(message = "Ο τύπος ακινήτου είναι υποχρεωτικός")
    @Enumerated(EnumType.STRING) // Αποθήκευση του enum ως String στη βάση
    @Column(name = "property_type")
    private PropertyType propertyType; // Νέο Enum: PropertyType

    @NotNull(message = "Ο αριθμός υπνοδωματίων είναι υποχρεωτικός")
    @Min(value = 0, message = "Ο αριθμός υπνοδωματίων δεν μπορεί να είναι αρνητικός")
    @Column(name = "bedrooms")
    private Integer bedrooms;

    @NotNull(message = "Ο αριθμός μπάνιων είναι υποχρεωτικός")
    @Min(value = 0, message = "Ο αριθμός μπάνιων δεν μπορεί να είναι αρνητικός")
    @Column(name = "bathrooms")
    private Integer bathrooms;

    @NotNull(message = "Τα τετραγωνικά μέτρα είναι υποχρεωτικά")
    @Min(value = 1, message = "Τα τετραγωνικά μέτρα πρέπει να είναι τουλάχιστον 1")
    @Column(name = "square_meters")
    private Integer squareMeters;

    @NotNull(message = "Το ποσό ενοικίου είναι υποχρεωτικό")
    @Min(value = 0, message = "Το ποσό ενοικίου δεν μπορεί να είναι αρνητικό")
    @Column(name = "rent_amount")
    private Double rentAmount;

    @Column(name = "availability_date")
    private LocalDate availabilityDate; // Ημερομηνία διαθεσιμότητας

    @Column(name = "is_approved")
    private Boolean isApproved = false; // Αρχικά false, απαιτεί έγκριση διαχειριστή

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "owner_id") // Το πεδίο που συνδέει με τον πίνακα users
    private User owner; // Σχέση Many-to-One με τον ιδιοκτήτη (User)

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RentalApplication> applications; // Σχέση One-to-Many με τις αιτήσεις ενοικίασης

    // Constructors
    public Property() {
    }

    public Property(String title, String description, String address, PropertyType propertyType,
                    Integer bedrooms, Integer bathrooms, Integer squareMeters, Double rentAmount,
                    LocalDate availabilityDate, User owner) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.propertyType = propertyType;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.squareMeters = squareMeters;
        this.rentAmount = rentAmount;
        this.availabilityDate = availabilityDate;
        this.owner = owner;
        this.isApproved = false; // Νέα ακίνητα απαιτούν έγκριση
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public Integer getSquareMeters() {
        return squareMeters;
    }

    public void setSquareMeters(Integer squareMeters) {
        this.squareMeters = squareMeters;
    }

    public Double getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(Double rentAmount) {
        this.rentAmount = rentAmount;
    }

    public LocalDate getAvailabilityDate() {
        return availabilityDate;
    }

    public void setAvailabilityDate(LocalDate availabilityDate) {
        this.availabilityDate = availabilityDate;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean approved) {
        isApproved = approved;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<RentalApplication> getApplications() {
        return applications;
    }

    public void setApplications(List<RentalApplication> applications) {
        this.applications = applications;
    }

    @Override
    public String toString() {
        return "Property{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", propertyType=" + propertyType +
                ", isApproved=" + isApproved +
                '}';
    }
}