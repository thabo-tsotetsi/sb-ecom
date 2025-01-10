package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;
    @NotBlank
    @Size(min = 5, message = "Street name must be at least 5 characters")
    private String street;
    @NotBlank
    @Size(min = 3, message = "Building name must be at least 3 characters")
    private String buildingName;
    @NotBlank
    @Size(min = 3, message = "City name must be at least 3 characters")
    private String city;
    @NotBlank
    @Size(min = 2, message = "State name must be at least 2 characters")
    private String stateName;
    @NotBlank
    @Size(min = 2, message = "State name must be at least 2 characters")
    private String countryName;
    @NotBlank
    @Size(min = 4, message = "State name must be at least 4 characters")
    private String pincode;
    //used to exclude values from the class to string method
    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users = new ArrayList<>();

    public Address(String street, String buildingName, String city, String stateName, String countryName, String pincode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.stateName = stateName;
        this.countryName = countryName;
        this.pincode = pincode;
    }
}
