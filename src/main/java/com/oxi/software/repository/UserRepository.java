package com.oxi.software.repository;

import com.oxi.software.entity.User;
import com.oxi.software.repository.projection.DeliveryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    boolean existsByUsername(String username);

    @Query("""
    SELECT 
        u.id AS id,
        i.name AS name,
        i.phone AS phone,
        r.name AS roleName
    FROM User u
    JOIN u.rolType r
    JOIN u.individual i
    WHERE r.name = 'DOMICILIARIO' 
    AND u.state = true
""")
    List<DeliveryProjection> findActiveDeliveries();
}