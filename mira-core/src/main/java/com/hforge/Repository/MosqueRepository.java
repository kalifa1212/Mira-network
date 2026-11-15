package com.hforge.Repository;

import com.hforge.Model.Mosque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;


public interface MosqueRepository extends JpaRepository<Mosque,Integer>{
	//TODO modification majeur ajout de ContainingIgnoreCase pour les recherche de string

		// comptage des entité
	int countByIsVendredi(boolean isVendredi);
	int countMosqueByLocalisation_Ville_NameContaining(String ville);
	int countMosqueByIsVendrediAndLocalisation_Ville_NameContaining(boolean isvendredi,String ville);


	List<Mosque> findMosqueByNomContainingIgnoreCase(String nom);
	//Page<Mosque> findMosqueByLocalisationVilleContainingOrLocalisationPays(String ville,String pays,Pageable pageable);
	Page<Mosque> findByLocalisation_Ville_NameContainingOrLocalisation_Ville_Pays_Name(String ville,String pays,Pageable pageable);
	List<Mosque> findByCreationDateGreaterThanEqual(Instant date);
	List<Mosque> findByLastModifiedDateGreaterThanEqual(Instant date);

	//TODO test de pagination
	List<Mosque> findAll(Sort sort);
	Page<Mosque> findAll(Pageable page);
	Page<Mosque> findMosqueByNomContainingIgnoreCase(Pageable page,String nom);
	Page<Mosque> findMosqueByIsVendredi(Boolean Vendredi,Pageable page);

	@Query("SELECT m FROM Mosque m " +
			"JOIN FETCH m.localisation l " +
			"JOIN FETCH l.ville v " +
			"JOIN FETCH v.pays")
	Page<Mosque> findAllWithFullLocation(Pageable page);
}
