package com.inghubs.brokerageapi.entity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inghubs.brokerageapi.constant.CommonConstants;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


/**
 * Represents a user in the system with authentication and authorization details.
 */
@Getter // Generates getters for all fields
@Setter // Generates setters for all fields
@Entity // Indicates that this class is a JPA entity
@Table(name = "\"USER\"") // Specifies the table name for this entity
public class User implements UserDetails {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Specifies that the ID is auto-generated
    private Long id; // Unique identifier for the user

    @Column(unique = true, nullable = false) // Indicates that the username must be unique and not null
    private String username; // The user's username

    @Column(nullable = false) // Indicates that the password cannot be null
    @JsonIgnore // Prevents the password from being serialized in JSON responses
    private String password; // The user's password

    @ElementCollection(fetch = FetchType.EAGER) // Indicates that roles are stored as a collection
    @CollectionTable(name = CommonConstants.USER_ROLES, joinColumns = @JoinColumn(name = CommonConstants.USER_ID)) // Defines the join table for user roles
    @Column(name = CommonConstants.ROLE) // Specifies the column name for roles
    private List<String> roles; // List of roles assigned to the user

    @OneToOne // Defines a one-to-one relationship with the Customer entity
    @JoinColumn(name = CommonConstants.CUSTOMER_ID) // Specifies the foreign key column name for the customer
    @JsonBackReference // Prevents circular references during JSON serialization
    private Customer customer; // The customer associated with this user

    private boolean isEnabled = true; // Indicates if the user account is enabled
    private boolean isAccountNonExpired = true; // Indicates if the account is not expired
    private boolean isAccountNonLocked = true; // Indicates if the account is not locked
    private boolean isCredentialsNonExpired = true; // Indicates if the credentials are not expired

    /**
     * Returns the authorities granted to the user.
     *
     * @return a collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converts role strings to GrantedAuthority objects with the "ROLE_" prefix
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", roles=" + roles +
                ", customerId=" + (customer != null ? customer.getId() : "null") + // Displays the customer ID
                ", isEnabled=" + isEnabled +
                ", isAccountNonExpired=" + isAccountNonExpired +
                ", isAccountNonLocked=" + isAccountNonLocked +
                ", isCredentialsNonExpired=" + isCredentialsNonExpired +
                '}';
    }

    // Additional UserDetails methods can be overridden if necessary
}