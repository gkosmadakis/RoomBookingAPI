package com.example.scannerapi.service;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.scannerapi.model.Barcodes;
import com.example.scannerapi.model.Users;

public interface BarcodeService {
	
	String getClientNames(String clientDb);
	@Query(value = "SELECT * from Barcodes Where Barcode=:barcode", nativeQuery=true)
	List<Barcodes> findByBarcode(@Param("barcode") long barcode, String clientDb);
	
	List<Barcodes> findAllBarcodes(String clientDb);
	Barcodes findById(Long id, String clientDb);
	Barcodes update(Barcodes barcodes, String clientDb) throws NotFoundException;
	Barcodes save(Barcodes newBarcode, String clientDb) throws NotFoundException;
	Users findByUsername(String username);
}
