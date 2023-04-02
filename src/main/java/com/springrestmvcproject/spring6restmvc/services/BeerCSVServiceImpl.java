package com.springrestmvcproject.spring6restmvc.services;

import com.opencsv.bean.CsvToBeanBuilder;
import com.springrestmvcproject.spring6restmvc.model.BeerCSVRecord;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

@Service
public class BeerCSVServiceImpl implements BeerCSVService {
    @Override
    public List<BeerCSVRecord> convertCSV(File csvFile) {

        try {
            List<BeerCSVRecord> beerCSVRecords = new CsvToBeanBuilder<BeerCSVRecord>(new FileReader(
                    csvFile
            )).withType(BeerCSVRecord.class)
                    .build().parse();

            return beerCSVRecords;
        }
        catch (FileNotFoundException fe){
            throw new RuntimeException(fe);
        }

    }
}
