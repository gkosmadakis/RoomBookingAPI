package com.example.scannerapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.scannerapi.model.Barcodes;
import com.example.scannerapi.model.Users;
import com.example.scannerapi.service.BarcodeService;

@RestController
@RequestMapping("api/scanner")
public class ScannerController {

    @Autowired 
    private BarcodeService barcodeService; 

	@GetMapping("/greeting")
	public String greeting() {
		return "Hello";
	}
	
	@GetMapping("/{clientdb}") // TODO test it
    public String findFromDatabase(@RequestParam String clientdb) {
        return barcodeService.getClientNames(clientdb);
    }
	
	
	@GetMapping("/{clientdb}/barcodes")
	public List<Barcodes> fetchDepartmentList(@PathVariable String clientdb) {
		return barcodeService.findAllBarcodes(clientdb);
	}

	@RequestMapping(path = "/{clientdb}/{barcode}", method=RequestMethod.GET)
	public List<Barcodes> findByBarcode(@PathVariable long barcode, @PathVariable String clientdb) {
		//String dbClient = barcodeService.getClientNames(clientdb);
		//System.out.println("ClientDb is "+ dbClient);
		return barcodeService.findByBarcode(barcode, clientdb);
	}

	@PutMapping("/{clientdb}/{id}")
	public Barcodes updateBarcode(@PathVariable final Long id, @RequestBody final Barcodes barcodes, @PathVariable String clientdb)
			throws NotFoundException {
		//String dbClient = barcodeService.getClientNames(clientdb);
		//System.out.println("ClientDb is "+ dbClient);
		return barcodeService.update(barcodes, clientdb);
	}

	@PostMapping("/{clientdb}/addBarcode")
	public Barcodes insertBarcode(@RequestBody Barcodes newBarcode, @PathVariable String clientdb) throws NotFoundException {
		//String dbClient = barcodeService.getClientNames(clientdb);
		//System.out.println("ClientDb is "+ dbClient);
		return barcodeService.save(newBarcode, clientdb);
	}
	
	@RequestMapping(path = "/user/{username}", method=RequestMethod.GET)
	public Users findByUsername(@PathVariable String username) {
		//String dbClient = barcodeService.getClientNames(clientdb);
		//System.out.println("ClientDb is "+ dbClient);
		return barcodeService.findByUsername(username);
	}
	 
}