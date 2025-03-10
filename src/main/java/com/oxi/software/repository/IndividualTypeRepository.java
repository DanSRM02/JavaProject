package com.oxi.software.repository;

import com.oxi.software.entity.IndividualType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndividualTypeRepository extends JpaRepository<IndividualType, Long> {
}
