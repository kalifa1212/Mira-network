package com.hforge.Services.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Slf4j
public class GeolocalisationService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // Évite de rechercher plusieurs fois les mêmes coordonnées
    private final Map<String, String> cache = new ConcurrentHashMap<>();


    public String obtenirVille(double latitude, double longitude) {
        String cle = String.format(
                java.util.Locale.ROOT,
                "%.5f,%.5f",
                latitude,
                longitude
        );
        log.error("debut de la recherche en ligne pour obtention du lieux");
        if (cache.containsKey(cle)) {
            return cache.get(cle);
        }

        try {
            String url = "https://nominatim.openstreetmap.org/reverse"
                    + "?lat=" + latitude
                    + "&lon=" + longitude
                    + "&format=jsonv2"
                    + "&addressdetails=1"
                    + "&accept-language=fr";

            HttpRequest requete = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(
                            "User-Agent",
                            "MiraApp/1.0 (contact: ibrahimsandah12@gmail.com)"
                    )
                    .GET()
                    .build();

            HttpResponse<String> reponse = client.send(
                    requete,
                    HttpResponse.BodyHandlers.ofString()
            );
            log.error("requete effectuer  ");
            if (reponse.statusCode() != 200) {
                System.err.println(
                        "Erreur Nominatim : HTTP " + reponse.statusCode()
                );
                log.info("erreur de connexion ");
                log.error("Erreur Nominatim : HTTP {}", reponse.statusCode());
                log.error("Corps de la réponse : {}", reponse.body());
                log.error("URL appelée : {}", url);
                return null;
            }

            JsonNode adresse = mapper.readTree(reponse.body())
                    .path("address");

            String[] champs = {
                    "city",
                    "town",
                    "village",
                    "municipality",
                    "county"
            };

            for (String champ : champs) {
                String ville = adresse.path(champ).asText("");

                if (!ville.isBlank()) {
                    cache.put(cle, ville);
                    log.info("nom ville trouver {}",ville);
                    return ville;
                }
            }
            log.error("non trouver  ");
            // Aucun nom de ville trouvé
            return "Ville inconnue";

        } catch (Exception e) {
            System.err.println(
                    "Impossible de récupérer la ville : "
                            + e.getMessage()
            );
            log.info("impossible de recuperer la ville");
            return null;
        }
    }

}