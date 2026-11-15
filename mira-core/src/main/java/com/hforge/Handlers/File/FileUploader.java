package com.hforge.Handlers.File;

public class FileUploader {
	
//	public static void saveFile(String Dir, String FileName, MultipartFile multipartFile) throws IOException{
//		Path uploadDir = Paths.get(Dir);
//		if(!Files.exists(uploadDir)) {
//			Files.createDirectories(uploadDir);
//		}
//		try (InputStream inputStream=multipartFile.getInputStream()){
//			Path filePath = uploadDir.resolve(FileName);
//			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
//		}catch (IOException ioe) {
//			throw new IOException("Impossible d'enregistrer l'image:"+FileName,ioe);
//		}
//	}
//
//	public static Resource Download(String fileName) {
//		String url ="G://HAMIDOU/Projets/ProjetPersonnel/API/test/Documents/photo/19/";
//		Path path = Paths.get(url).toAbsolutePath().resolve("img.jpg");
//		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//				.path("/downloadFile/")
//				.path(fileName)
//				.toUriString();
//
//		Resource resource = null;
//		try {
//			resource = (Resource) new UrlResource(path.toUri());
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return resource;
//
//	}

}
