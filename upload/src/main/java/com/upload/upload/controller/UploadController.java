package com.upload.upload.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.upload.upload.Exceptions.FileException;
import com.upload.upload.Service.ImageRepository;
import com.upload.upload.model.ImageModel;

@RestController
@RequestMapping(path = "image")
public class UploadController {

	@Autowired
	ImageRepository imageRepository;
	
	@PostMapping("/upload")
	public ResponseEntity uplaodImage(@RequestParam("imageFile") MultipartFile file) throws SerialException, SQLException {
		try {
			ImageModel img = new ImageModel(file.getOriginalFilename(), file.getContentType(),
					compressBytes(file.getBytes()));
		  	imageRepository.save(img);
			return new  ResponseEntity<>("File upload successfully..",HttpStatus.OK);
		} catch (FileException e) {
			System.out.println("nnn");
			return new ResponseEntity<>("error upload", HttpStatus.NOT_FOUND);
		} catch (IOException e) {
			System.out.println("ll");
			return new ResponseEntity<> ("error upload", HttpStatus.BAD_REQUEST);
		}	
	}
	
	@GetMapping(path = { "/get/{imageName}" })
	public ResponseEntity<Resource>  getImage(@PathVariable("imageName") String imageName) throws IOException {
		final Optional<ImageModel> retrievedImage = imageRepository.findByName("data2.PNG");
		ImageModel img = new ImageModel(retrievedImage.get().getName(), retrievedImage.get().getType(),
				decompressBytes(retrievedImage.get().getPicByte()));
		//System.out.println("bac Image Byte Size - " +decodedBytes.length);
		 HttpHeaders headers = new HttpHeaders();
		    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + retrievedImage.get().getName() + "\"");
		    headers.setContentType(MediaType.parseMediaType(retrievedImage.get().getType()));
		 return ResponseEntity.ok()
				 .headers(headers)
				 .body(new ByteArrayResource(img.getPicByte()));
	    }
		


// uncompress the image bytes before returning it to the angular application
public static byte[] decompressBytes(byte[] data) {
	Inflater inflater = new Inflater();
	inflater.setInput(data);
	ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
	byte[] buffer = new byte[1024];
	try {
		while (!inflater.finished()) {
			int count = inflater.inflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		outputStream.close();
	} catch (IOException ioe) {
	} catch (DataFormatException e) {
	}
	return outputStream.toByteArray();
}

	// compress the image bytes before storing it in the database
	public static byte[] compressBytes(byte[] data) {
		Deflater deflater = new Deflater();
		deflater.setInput(data);
		deflater.finish();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[1024];
		while (!deflater.finished()) {
			int count = deflater.deflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		try {
			outputStream.close();
		} catch (IOException e) {
		}
		System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);

		return outputStream.toByteArray();
	}
	
	
}
