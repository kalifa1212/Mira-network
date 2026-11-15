package com.hforge.Services;

import com.hforge.Dto.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    NotificationDto save(NotificationDto dto);
    Page<NotificationDto> findAll(Pageable pageable);
    List<NotificationDto> findById(Integer id);
    Page<NotificationDto> findByType(String type,Pageable pageable);
    void delete(Integer id);
}
