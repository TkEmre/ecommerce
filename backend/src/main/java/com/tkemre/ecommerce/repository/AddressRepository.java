package com.tkemre.ecommerce.repository;

import com.tkemre.ecommerce.model.Address;
import com.tkemre.ecommerce.model.User; // User modelini de import etmeyi unutmayın
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional; // Optional için import

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByUser(User user);
    Optional<Address> findByIdAndUser(Long id, User user);
}