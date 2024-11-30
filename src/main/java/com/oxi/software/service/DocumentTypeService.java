package com.oxi.software.service;

import com.oxi.software.entities.DocumentType;
import com.oxi.software.repository.DocumentTypeRepository;
import com.oxi.software.service.dao.Idao;
import com.oxi.software.utilities.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentTypeService implements Idao<DocumentType, Long> {

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Override
    public DocumentType findBy(Long id) {
        return this.documentTypeRepository.findById(id).orElseThrow(()->
                new CustomException("Document type with id " + id + " not found", HttpStatus.NO_CONTENT));
    }

    @Override
    public void save(DocumentType obje) {
        this.documentTypeRepository.save(obje);
    }

    @Override
    public void saveAll(Iterable<DocumentType> obje) {
        this.documentTypeRepository.saveAll(obje);
    }

    @Override
    public void delete(DocumentType obje) {
        this.documentTypeRepository.delete(obje);
    }

    @Override
    public List<DocumentType> findAll() {
        return this.documentTypeRepository.findAll();
    }
}
