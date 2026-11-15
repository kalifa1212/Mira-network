package com.hforge.Services.Impl;

import com.hforge.Dto.NotificationDto;
import com.hforge.Exceptions.ErrorCodes;
import com.hforge.Exceptions.InvalidEntityException;
import com.hforge.Repository.NotificationRepository;
import com.hforge.Repository.UtilisateurRepository;
import com.hforge.Services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private NotificationRepository notificationRepository;
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UtilisateurRepository utilisateurRepository
    ) {
        this.notificationRepository=notificationRepository;
        this.utilisateurRepository=utilisateurRepository;
    }

    @Override
    public NotificationDto save(NotificationDto dto) {
        List<String> errors = validate(dto);
        if(!errors.isEmpty()) {
            //log.error("La mosque est non valide {}",dto);
            throw new InvalidEntityException("Les information entrer ne sont pas valide ", ErrorCodes.NOTIFICATION_NOT_VALID,errors);
        }
        return NotificationDto.fromEntity(notificationRepository.save(NotificationDto.toEntity(dto)));
    }


    @Override
    public Page<NotificationDto> findAll(Pageable pageable) {
        return notificationRepository.findAll(pageable).map(NotificationDto::fromEntity);
    }

    @Override
    public List<NotificationDto> findById(Integer id) {
        return notificationRepository.findById(id).stream().map(NotificationDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public Page<NotificationDto> findByType(String type,Pageable pageable) {
        return notificationRepository.findByType(type,pageable).map(NotificationDto::fromEntity);
    }

    @Override
    public void delete(Integer id) {

    }

    public static List<String> validate(NotificationDto notificationDto){
        List<String> errors = new ArrayList<>();
        if(notificationDto==null ) {
            errors.add("veullez renseigner les données");
        }
        return errors;
    }
}
