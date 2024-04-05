package com.example.scannerapi.repository.dev;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.scannerapi.model.Barcodes;

@Repository 
public interface BarcodeDevRepository extends JpaRepository<Barcodes, Long> {

	List<Barcodes> findByBarcode(long barcode);
	
}
