package com.hforge.Repository.Metadata;

import com.hforge.Model.MetaData.NewLocationToken;
import com.hforge.Model.MetaData.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewLocationTokenRepository extends JpaRepository<NewLocationToken, Long> {
    NewLocationToken findByToken(String token);

    NewLocationToken findByUserLocation(UserLocation userLocation);
}
