package com.fixeddeposit.authservice.repository;

import com.fixeddeposit.authservice.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Integer> {
    Optional<Staff> findByStaffemail(String staffemail);
}
