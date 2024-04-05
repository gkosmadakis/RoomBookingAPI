package com.example.scannerapi.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.scannerapi.config.ClientNames;
import com.example.scannerapi.config.DBContextHolder;
import com.example.scannerapi.model.Barcodes;
import com.example.scannerapi.model.Users;
import com.example.scannerapi.repository.demo.BarcodeDemoRepository;
import com.example.scannerapi.repository.demo.UsersDemoRepository;
import com.example.scannerapi.repository.dev.BarcodeDevRepository;


@Service
public class BarcodeServiceImpl implements BarcodeService {
	private static final Logger logger = LoggerFactory.getLogger(BarcodeServiceImpl.class);
	
	@Autowired
    private BarcodeDemoRepository barcodeDemoRepository; 
	@Autowired
	private BarcodeDevRepository barcodeDevRepository;
	@Autowired
	private UsersDemoRepository usersDemoRepository;

	public String getClientNames(String client) {
        switch (client) {
            case "demo":
                DBContextHolder.setCurrentDb(ClientNames.Demo);
                break;
            case "dev":
                DBContextHolder.setCurrentDb(ClientNames.Dev);
                break;
        }
        return  client;
    }

	@Override
	public List<Barcodes> findAllBarcodes(String clientDb) {
		if(clientDb.equals("demo")) {			
			return (List<Barcodes>) barcodeDemoRepository.findAll();
		}
		else if (clientDb.equals("dev")){
			return (List<Barcodes>) barcodeDevRepository.findAll();
		}
		return null;
	}
	 
	@Override
	public List<Barcodes> findByBarcode(long barcode, String clientDb) {
		if(clientDb.equals("demo")) {
			return barcodeDemoRepository.findByBarcode(barcode);
		}
		else if (clientDb.equals("dev")) {
			return barcodeDevRepository.findByBarcode(barcode);
		}
		return null;
	}
	
	@Override
	public Barcodes findById(Long id, String clientDb) {
		if(clientDb.equals("demo")) {
			return barcodeDemoRepository.findById(id).get();
		}
		else if (clientDb.equals("dev")) {
			return barcodeDevRepository.findById(id).get();
		}
		return null;
	}

	@Override
	public Barcodes update(Barcodes barcodes, String clientDb) throws NotFoundException {
		if(clientDb.equals("demo")) {
			return updateDemoDatabase(barcodes, clientDb);
		}
		else if (clientDb.equals("dev")) {
			return updateDevDatabase(barcodes, clientDb);
		}
		return null;
	}
	
	@Transactional(transactionManager = "transactionManagerDemo")
	private Barcodes updateDemoDatabase(Barcodes barcodes, String clientDb) throws NotFoundException {
		Barcodes toUpdate = findById(barcodes.getID(),clientDb);
		if(toUpdate == null)
			throw new NotFoundException();
		toUpdate = barcodeDemoRepository.save(barcodes);
		return toUpdate;
	}
	
	@Transactional(transactionManager = "transactionManagerDev")
	private Barcodes updateDevDatabase(Barcodes barcodes, String clientDb) throws NotFoundException {
		Barcodes toUpdate = findById(barcodes.getID(),clientDb);
		if(toUpdate == null)
			throw new NotFoundException();
		toUpdate = barcodeDevRepository.save(barcodes);
		return toUpdate;
	}

	@Override
	public Barcodes save(Barcodes newBarcode, String clientDb) throws NotFoundException {
		if(clientDb.equals("demo")) {
			return insertToDemo(newBarcode);
		}
		else if (clientDb.equals("dev")) {
			return insertToDev(newBarcode);
		}
		return null;
	}

	@Transactional(transactionManager = "transactionManager")
	private Barcodes insertToDemo(Barcodes newBarcode) throws NotFoundException {
		if(newBarcode != null && !StringUtils.isEmpty(String.valueOf(newBarcode.getBarcode()))
				&& newBarcode.getBarcode() !=0 ) {
			return barcodeDemoRepository.save(newBarcode);
		}
		else {
			logger.info("newBarcode was null");
			//TODO: Change it with another proper exception to send to the app
			throw new NotFoundException();
		}
	}

	@Transactional(transactionManager = "transactionManagerDev")
	private Barcodes insertToDev(Barcodes newBarcode) throws NotFoundException {
		if(newBarcode != null && !StringUtils.isEmpty(String.valueOf(newBarcode.getBarcode()))
				&& newBarcode.getBarcode() !=0 ) {
			return barcodeDevRepository.save(newBarcode);
		}
		else {
			logger.info("newBarcode was null");
			//TODO: Change it with another proper exception to send to the app
			throw new NotFoundException();
		}
	}

	@Override
	public Users findByUsername(String username) {
		return usersDemoRepository.findByUsername(username);
	}

}
