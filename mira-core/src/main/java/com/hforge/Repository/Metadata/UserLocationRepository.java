package com.hforge.Repository.Metadata;

import com.hforge.Model.MetaData.UserLocation;
import com.hforge.Model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
    UserLocation findByCountryAndUser(String country, Utilisateur user);
}
