package com.oxi.software.repository;

import com.oxi.software.entity.RolType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolTypeRepository extends JpaRepository<RolType, Long> {
}
