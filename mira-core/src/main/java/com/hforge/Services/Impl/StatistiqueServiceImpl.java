package com.hforge.Services.Impl;

import com.hforge.Model.Mosque;
import com.hforge.Model.Predication;
import com.hforge.Repository.MosqueRepository;
import com.hforge.Repository.PredicationRepository;
import com.hforge.Repository.UtilisateurRepository;
import com.hforge.Services.StatistiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatistiqueServiceImpl implements StatistiqueService {
    @Autowired
    private MosqueRepository mosqueRepository;

    @Autowired
    private PredicationRepository predicationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // 📊 Nombre de mosquées par localisation
    @Override
    public Map<String, Long> getMosquesByLocation() {
//        return mosqueRepository.findAll().stream()
//                .collect(Collectors.groupingBy(Mosque::getQuartier, Collectors.counting()));
        return mosqueRepository.findAll().stream()
                //TODO pour evité les null point exception
                .filter(mosque -> mosque.getLocalisation() != null && mosque.getLocalisation().getVille() != null)
                .collect(Collectors.groupingBy(
                        mosque -> mosque.getLocalisation().getVille().getName(),
                        Collectors.counting()
                ));
    }

    // 📊 Superficie moyenne des mosquées par quartier
    @Override
    public Map<String, Double> getAvgMosqueSizeByQuartier() {
        return mosqueRepository.findAll().stream()
                .filter(mosque -> mosque.getLocalisation() != null && mosque.getLocalisation().getVille() != null)
                .collect(Collectors.groupingBy(
                        mosque -> mosque.getLocalisation().getVille().getName(),
                        Collectors.averagingInt(Mosque::getSuperficie)
                ));

    }

    // 📊 Répartition des mosquées ouvertes le vendredi
    @Override
    public Map<String, Map<String, Long>> getMosquesByFridayStatus() {
        return mosqueRepository.findAll().stream()
                .filter(mosque -> mosque.getLocalisation() != null && mosque.getLocalisation().getVille() != null)
                .collect(Collectors.groupingBy(
                        mosque -> mosque.getLocalisation().getVille().getName(), // Grouper par ville
                        Collectors.groupingBy(
                                m -> m.getIsVendredi() ? "Ouvert le Vendredi" : "Non Ouvert", // Puis par ouverture vendredi
                                Collectors.counting()
                        )
                ));

    }

    // 📊 Nombre moyen de mosquées par ville/localisation
    @Override
    public Map<String, Double> getAvgMosquesByLocation() {
        return mosqueRepository.findAll().stream()
                .collect(Collectors.groupingBy(m -> m.getLocalisation().getVille().getName(),
                        Collectors.averagingInt(m -> 1)));
    }

    // 📊 Nombre de sermons par mosquée
    @Override
    public Map<String, Long> getSermonsByMosque() {
        return predicationRepository.findAll().stream()
                .collect(Collectors.groupingBy(Predication::getNomMosque, Collectors.counting()));
    }

    // 📊 Répartition des prêches par type
    @Override
    public Map<String, Long> getPredicationsByType() {
        return predicationRepository.findAll().stream()
                .collect(Collectors.groupingBy(p -> p.getType().name(), Collectors.counting()));
    }

    // 📊 Analyse des thèmes les plus abordés dans les prêches
    @Override
    public Map<String, Long> getTopThemes() {
        return predicationRepository.findAll().stream()
                .collect(Collectors.groupingBy(Predication::getTheme, Collectors.counting()));
    }

    // 📊 Répartition des utilisateurs par type de compte
    @Override
    public Map<String, Long> getUsersByAccountType() {
        return utilisateurRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        u -> u.getTypecompte() != null ? u.getTypecompte().name() : "NON_DÉFINI",
                        Collectors.counting()
                ));

    }

    // 📊 Évolution du nombre d'inscriptions par mois
    @Override
    public Map<String, Long> getUserRegistrationsByMonth() {
        return utilisateurRepository.findAll().stream()
                .collect(Collectors.groupingBy(u -> {
                    LocalDate date = u.getDateDeNaissance().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
                }, Collectors.counting()));
    }

    // 📊 Répartition géographique des utilisateurs
    @Override
    public Map<String, Long> getUsersByLocation() {
        return utilisateurRepository.findAll().stream()
                .collect(Collectors.groupingBy(u -> u.getLocalisation().getVille().getName(), Collectors.counting()));
    }

}
