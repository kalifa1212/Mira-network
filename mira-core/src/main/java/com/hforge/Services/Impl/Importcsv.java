package com.hforge.Services.Impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.hforge.Model.Localisation;
import com.hforge.Model.Mosque;
import com.hforge.Model.Pays;
import com.hforge.Model.Ville;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class Importcsv {

    public static List<Mosque> lireCsv(MultipartFile fichier) throws IOException {
        GeolocalisationService geo = new GeolocalisationService();


        List<Mosque> mosquees = new ArrayList<>();

        try (CSVParser parser = CSVParser.parse(
                fichier.getInputStream(),
                java.nio.charset.StandardCharsets.UTF_8,
                CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .get())) {

            for (CSVRecord ligne : parser) {
                String nomVille = valeur(ligne, "city");
                Pays pays=new Pays("Cameroun","CMR");
                Ville ville=new Ville();
                ville.setPays(pays);
                if (nomVille != null && !nomVille.isBlank()) {
                    ville.setName(nomVille.trim());
                } else {
                    Thread.sleep(1500);
                    double latitude = nombreDouble(ligne, "latitude", 0.0);
                    double longitude = nombreDouble(ligne, "longitude", 0.0);
                    log.info(
                            "Recherche de ville : latitude={}, longitude={}",
                            latitude, longitude
                    );
                    String villeTrouvee = geo.obtenirVille(latitude, longitude);
                    log.info("Ville après recherche : {}", villeTrouvee);
                    ville.setName(villeTrouvee);
                    log.error("operation de recherche de ville terminer {}",ville.getName());
                }
                Localisation localisation= new Localisation();
                localisation.setVille(ville);
                log.info("localisation apres operation {}",localisation);

                Mosque mosquee = new Mosque();

                // Correspondance entre les colonnes CSV et l'entité
                mosquee.setNom(valeur(ligne, "name"));

                mosquee.setLatitude(
                        nombreDouble(ligne, "latitude", 0.0)
                );

                mosquee.setLongitude(
                        nombreDouble(ligne, "longitude", 0.0)
                );

                // Champs absents du CSV : null ou valeur par défaut
                mosquee.setCode(null);
                mosquee.setPhoto(null);
                mosquee.setDescription(null);
                mosquee.setImam(null);
                mosquee.setImagedata(null);
                mosquee.setQuartier(null);

                mosquee.setBalte(null);
                mosquee.setAsr(null);
                mosquee.setMagrib(null);
                mosquee.setIcha(null);
                mosquee.setSoub(null);
                mosquee.setZour(null);
                mosquee.setDjouma(null);

               // mosquee.setIsVendredi(false);

                // int superficie ne peut pas contenir null
                mosquee.setSuperficie(0);
                mosquee.setLocalisation(localisation);

                mosquees.add(mosquee);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return mosquees;
    }

    private static String valeur(CSVRecord ligne, String colonne) {
        if (!ligne.isMapped(colonne)) {
            return null;
        }

        String valeur = ligne.get(colonne).trim();

        return valeur.isEmpty() ? null : valeur;
    }

    private static double nombreDouble(
            CSVRecord ligne,
            String colonne,
            double valeurParDefaut) {

        String valeur = valeur(ligne, colonne);

        if (valeur == null) {
            return valeurParDefaut;
        }

        try {
            return Double.parseDouble(valeur);
        } catch (NumberFormatException e) {
            return valeurParDefaut;
        }
    }
}
