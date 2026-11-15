package com.hforge.Repository;

import com.hforge.Model.Pays;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaysRepository extends JpaRepository<Pays,Integer> {
    Optional<Pays> findByName(String name);
}
