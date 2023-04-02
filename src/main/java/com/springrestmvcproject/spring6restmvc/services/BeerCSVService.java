package com.springrestmvcproject.spring6restmvc.services;

import com.springrestmvcproject.spring6restmvc.model.BeerCSVRecord;

import java.io.File;
import java.util.List;


public interface BeerCSVService {

    List<BeerCSVRecord> convertCSV(File csvFile);
}
