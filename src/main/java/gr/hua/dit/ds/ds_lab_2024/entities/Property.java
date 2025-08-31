package gr.hua.dit.ds.ds_lab_2024.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * The Property class represents a property entity within the application.
 * It stores all the necessary details of a property and its relationships
 * with other entities, such as the owner and rental applications.
 */
@Entity
@Table(name = "properties") // Table name in the database
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "The title is required")
    @Size(min = 3, max = 100, message = "The title must be between 3 and 100 characters")
    @Column(name = "title")
    private String title;

    @NotBlank(message = "The description is required")
    @Column(name = "description", length = 1000) // Μεγαλύτερο μήκος για την περιγραφή
    private String description;

    @NotBlank(message = "The address is required")
    @Column(name = "address")
    private String address;

    @NotNull(message = "The property type is required")
    @Enumerated(EnumType.STRING) // Αποθήκευση του enum ως String στη βάση
    @Column(name = "property_type")
    private PropertyType propertyType;

    @NotNull(message = "The number of bedrooms is required")
    @Min(value = 0, message = "The number of bedrooms cannot be negative")
    @Column(name = "bedrooms")
    private Integer bedrooms;

    @NotNull(message = "The number of bathrooms is required")
    @Min(value = 0, message = "The number of bathrooms cannot be negative")
    @Column(name = "bathrooms")
    private Integer bathrooms;

    @NotNull(message = "The square meters are required")
    @Min(value = 1, message = "The square meters must be at least 1")
    @Column(name = "square_meters")
    private Integer squareMeters;

    @NotNull(message = "The rent amount is required")
    @Min(value = 0, message = "The rent amount cannot be negative")
    @Column(name = "rent_amount")
    private Double rentAmount;

    @Column(name = "availability_date")
    private LocalDate availabilityDate; // Ημερομηνία διαθεσιμότητας

    @Column(name = "is_approved")
    private Boolean isApproved = false; // Αρχικά false απαιτει έγκριση διαχειριστή

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "owner_id") // Το πεδίο που συνδέει με τον πίνακα users
    private User owner; // Σχέση Many-to-One με τον ιδιοκτήτη

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RentalApplication> applications; // Σχέση One to-Many με τις αιτήσεις ενοικίασης

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
        this.isApproved = false; // New properties require approval
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
