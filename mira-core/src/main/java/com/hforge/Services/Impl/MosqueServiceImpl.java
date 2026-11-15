package com.hforge.Services.Impl;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.hforge.Dto.MosqueDto;
import com.hforge.Dto.NotificationDto;
import com.hforge.Exceptions.EntityNotFoundException;
import com.hforge.Exceptions.ErrorCodes;
import com.hforge.Exceptions.InvalidEntityException;
import com.hforge.Model.*;
import com.hforge.Repository.*;
import com.hforge.Services.MosqueService;
import com.hforge.Validation.MosqueValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service @Slf4j
public class MosqueServiceImpl implements MosqueService {
	
	private MosqueRepository mosqueRepository;
	private UtilisateurRepository utilisateurRepository;
	private LocalisationRepository localisationRepository;
	private NotificationRepository notificationRepository;
	private VilleRepository villeRepository;
	private PaysRepository paysRepository;
	private SuivreRepository suivreRepository;
	
	@Autowired
	public MosqueServiceImpl(
			MosqueRepository mosqueRepository,
			LocalisationRepository localisationRepository,
			UtilisateurRepository utilisateurRepository,
			NotificationRepository notificationRepository,
			SuivreRepository suivreRepository,
            VilleRepository villeRepository,
            PaysRepository paysRepository
	) {
		this.mosqueRepository=mosqueRepository;
		this.localisationRepository=localisationRepository;
		this.utilisateurRepository=utilisateurRepository;
		this.notificationRepository=notificationRepository;
		this.suivreRepository=suivreRepository;
        this.villeRepository=villeRepository;
        this.paysRepository=paysRepository;
	}
	
	@Override
	public MosqueDto save(MosqueDto dto, Boolean update) {
		//  Auto-generated method stub
		if(!update) {
			List<String> errors = MosqueValidator.validate(dto);
			if(!errors.isEmpty()) {
				throw new InvalidEntityException("Les information de la mosque ne sont pas valide ", ErrorCodes.MOSQUE_NOT_VALID,errors);
			}
			Optional<Localisation> localisation = localisationRepository.findById(dto.getLocalisation().getId());
			if(localisation.isEmpty()){
				throw new InvalidEntityException("Localisation non trouver", ErrorCodes.LOCALISATION_NOT_FOUND,errors);
			}
		}

		//TODO Notification pour la creation d'une mosque
		log.info("Notification ....");
		Notification(dto,update);
		return MosqueDto.fromEntity(mosqueRepository.save(MosqueDto.toEntity(dto)));
	}

	@Override
	public void exportData(PrintWriter writer) throws IOException {
		log.info("Exporting Mosque Data ....");
			List<MosqueDto> mosques = mosqueRepository.findAll().stream().map(MosqueDto::fromEntity).collect(Collectors.toList());

			CSVWriter csvWriter = new CSVWriter(writer);

			// Écrire l'en-tête du CSV
			String[] header = {
					"nom","code","imam","superficie","description",
					"balte","asr","magrib","icha","soub","zour","djouma","isVendredi",
					"quartier", "Ville","pays"
			};
			csvWriter.writeNext(header);

			// Écrire les données
			for (MosqueDto mosque : mosques) {
				String[] data = {
						mosque.getNom(),
						mosque.getCode(),
						mosque.getImam(),
						String.valueOf(mosque.getSuperficie()),
						mosque.getDescription(),
						String.valueOf(mosque.getBalte()),
						String.valueOf(mosque.getAsr()),
						String.valueOf(mosque.getMagrib()),
						String.valueOf(mosque.getIcha()),
						String.valueOf(mosque.getSoub()),
						String.valueOf(mosque.getZour()),
						String.valueOf(mosque.getDjouma()),
						String.valueOf(mosque.getIsVendredi()),
						mosque.getQuartier(),
						mosque.getLocalisation().getVilleDto().getName(),
						mosque.getLocalisation().getVilleDto().getCountry().getName()
				};
				csvWriter.writeNext(data);
			}

			csvWriter.close();

	}

	//test import to db with an other csv
	@Override
	public void importDataToDB(MultipartFile file) throws IOException {
		Pays pays= paysRepository.findById(1).orElseThrow();
		List<Mosque> mosquees =
				Importcsv.lireCsv(
						file
				);



		for (Mosque mosquee : mosquees) {

			log.info("Début d'enregistrement de la mosquée : {}",
					mosquee.getNom());

			// 1. Vérifier les données nécessaires
			if (mosquee.getLocalisation() == null
					|| mosquee.getLocalisation().getVille() == null
					|| mosquee.getLocalisation().getVille().getName() == null
					|| mosquee.getLocalisation().getVille().getName().isBlank()) {

				log.warn("Mosquée ignorée : ville absente pour {}",
						mosquee.getNom());
				continue;
			}

			// 2. Récupérer le nom de la ville
			String nomVille = mosquee.getLocalisation()
					.getVille()
					.getName()
					.trim();

			// 3. Rechercher une localisation existante
			Localisation localisationFinale =
					localisationRepository.findByVille_NameIgnoreCase(nomVille);

			// 4. Si elle n'existe pas, la créer
			if (localisationFinale == null) {

				log.info("Aucune localisation trouvée pour {}. Création...",
						nomVille);

				// Créer et enregistrer la ville
				Ville nouvelleVille = new Ville(nomVille, pays);
				nouvelleVille = villeRepository.save(nouvelleVille);

				// Créer et enregistrer la localisation liée à la ville
				Localisation nouvelleLocalisation =
						new Localisation("", "", "", nouvelleVille);

				localisationFinale =
						localisationRepository.save(nouvelleLocalisation);

				log.info("Nouvelle localisation enregistrée : {}",
						localisationFinale);

			} else {
				log.info("Localisation existante réutilisée pour {}",
						nomVille);
			}

			// 5. Associer la localisation finale à la mosquée
			mosquee.setLocalisation(localisationFinale);

			// 6. Enregistrer la mosquée
			Mosque mosqueeEnregistree = mosqueRepository.save(mosquee);

			log.info(
					"Mosquée enregistrée : nom={}, ville={}, latitude={}, longitude={}",
					mosqueeEnregistree.getNom(),
					nomVille,
					mosqueeEnregistree.getLatitude(),
					mosqueeEnregistree.getLongitude()
			);
		}


		System.out.println(
				"Nombre de mosquées : " + mosquees.size()
		);
	}
//	@Override
//	public void importDataToDB(MultipartFile file) throws IOException {
//		log.info("Importing Mosque Data ....");
//		try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
//			 CSVReader csvReader = new CSVReader(reader))
//		{
//			SimpleDateFormat format = new SimpleDateFormat("HH:mm");
//			String[] nextRecord;
//			boolean isHeader = true;
//
//			List<MosqueDto> mosques = new ArrayList<>();
//			int i=1;
//			while ((nextRecord = csvReader.readNext()) != null) {
//				if (isHeader) {  // Ignorer l'en-tête
//					isHeader = false;
//					continue;
//				}
//				Localisation local =localisationRepository.findByVille_NameIgnoreCase(nextRecord[14]);
//				Mosque newMosque= new Mosque();
//				newMosque.setNom(nextRecord[0]);
//				newMosque.setCode(nextRecord[1]);
//				newMosque.setImam(nextRecord[2]);
//				newMosque.setSuperficie(Integer.parseInt(nextRecord[3]));
//				newMosque.setDescription(nextRecord[4]);
//				//newMosque.setBalte(nextRecord[6]);
//				newMosque.setBalte(format.parse(nextRecord[5]));
//				newMosque.setAsr(format.parse(nextRecord[6]));
//				newMosque.setMagrib(format.parse(nextRecord[7]));
//				newMosque.setIcha(format.parse(nextRecord[8]));
//				newMosque.setSoub(format.parse(nextRecord[9]));
//				newMosque.setZour(format.parse(nextRecord[10]));
//				newMosque.setDjouma(format.parse(nextRecord[11]));
//				newMosque.setIsVendredi(Boolean.parseBoolean(nextRecord[12]));
//				newMosque.setLocalisation(local);
//				mosqueRepository.save(newMosque);
//				i++;
//
//			}
//		} catch (CsvValidationException e) {
//			throw new RuntimeException(e);
//		} catch (ParseException e) {
//			throw new RuntimeException(e);
//		}
//
//	}

	@Override
	public MosqueDto findById(Integer id) {
		//  Auto-generated method stub
		if(id==null ) {
			throw new InvalidEntityException("L'id entre est NULL");
		}
		Optional<Mosque> mosque= mosqueRepository.findById(id);
		
		return Optional.of(MosqueDto.fromEntity(mosque.get())).orElseThrow(() ->
				new EntityNotFoundException(
						"Aucune mosque avec l'id "+id+"n'a ete trouver dans la BD",
						ErrorCodes.MOSQUE_NOT_FOUND)
		);
	}

	@Override
	public List<MosqueDto> findByNom(String nom) {
		//  Auto-generated method stub
		if(!StringUtils.hasLength(nom)) {
			throw new InvalidEntityException("Le nom entrée est NULL");
		}

		return this.mosqueRepository.findMosqueByNomContainingIgnoreCase(nom).stream()
				.map(MosqueDto::fromEntity)
				.collect(Collectors.toList());
	}

	@Override
	public List<MosqueDto> findMosqueByVilleOrQuartier(String str) {
		if(!StringUtils.hasLength(str)) {
			throw new InvalidEntityException("Le nom entrée est NULL");
		}

		return null;
	}

	@Override
	public Page<MosqueDto> findByVendredis(Boolean vendredi, Pageable page) {
		
		return this.mosqueRepository.findMosqueByIsVendredi(vendredi,page)
				.map(MosqueDto::fromEntity);
	}

	@Override
	public Page<MosqueDto> findAll(Pageable page) {
		//  Auto-generated method stub
		return mosqueRepository.findAll(page)
				.map(MosqueDto::fromEntity);
	}

	@Override
	public Page<MosqueDto> findAllByName(String str, Pageable page) {
		Page<MosqueDto> mosqueList=mosqueRepository.findMosqueByNomContainingIgnoreCase(page,str).map(MosqueDto::fromEntity);
		return mosqueList;
	}
	// TODO Conversion Function
	public  static <T> Page<T> convertListToPage(List<T> list,Pageable page) {
		PagedListHolder pageC= new PagedListHolder(list);
		pageC.setPage(page.getPageNumber());
		pageC.setPageSize(page.getPageSize());
		return (Page<T>) pageC;
	}

	@Override
	public void delete(Integer id) {
		//  Auto-generated method stub
		if(id==null) {
			throw new InvalidEntityException("L'Id entrée est NULL");
		}
		
		mosqueRepository.deleteById(id);

	}
	void Notification(MosqueDto dto,Boolean update){
		NotificationDto notificationDto= new NotificationDto();
		notificationDto.setType(TypeNotification.MOSQUE);
		notificationDto.setRead(false);
		notificationDto.setDateTime(LocalDateTime.now());
		Localisation localisation=localisationRepository.findById(dto.getLocalisation().getId()).orElseThrow();
		if(update){
		notificationDto.setMessage("les Information de la mosque "+dto.getNom()+"de la ville "+
				localisation.getVille().getName()+ " quartier "+ dto.getQuartier()+" On eté modifier");
		}
		notificationDto.setMessage("Creation d'une nouvelle mosque a "+localisation.getVille().getName()+" quartier "+
				dto.getQuartier()+"");
		notificationRepository.save(NotificationDto.toEntity(notificationDto));
	}
}
