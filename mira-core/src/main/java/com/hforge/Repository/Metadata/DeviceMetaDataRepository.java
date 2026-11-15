package com.hforge.Repository.Metadata;

import com.hforge.Model.MetaData.DeviceMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceMetaDataRepository extends JpaRepository<DeviceMetaData, Long> {
    List<DeviceMetaData> findByUserId(Long userId);
}
