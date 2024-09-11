package com.example.communitycenter.controller;


import com.example.communitycenter.dtos.CreateNegociationFormDTO;
import com.example.communitycenter.model.Negociation;
import com.example.communitycenter.service.NegociationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/negociations")
public class NegociationController {
    @Autowired
    private NegociationService negociationService;

    @PostMapping("/")
    public ResponseEntity<Negociation> create(@Valid @RequestBody CreateNegociationFormDTO form) {
        Negociation savedNegociation = negociationService.create(form);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedNegociation.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedNegociation);
    }
}
