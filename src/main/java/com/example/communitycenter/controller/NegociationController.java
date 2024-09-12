package com.example.communitycenter.controller;


import com.example.communitycenter.dtos.CreateNegociationFormDTO;
import com.example.communitycenter.model.Negociation;
import com.example.communitycenter.service.NegociationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/negociations")
public class NegociationController {
    @Autowired
    private NegociationService negociationService;

    @PostMapping("/")
    @Operation(summary = "Create a new negotiation")
    public ResponseEntity<Negociation> create(@Valid @RequestBody CreateNegociationFormDTO form) {
        Negociation savedNegociation = negociationService.create(form);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedNegociation.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedNegociation);
    }

    @GetMapping("/{communityCenterName}/history")
    @Operation(summary = "Get Community Center Negociations Historic")
    public ResponseEntity<List<Negociation>> listHighOccupancy(
            @PathVariable String communityCenterName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime negociationDate
    ) {
        return ResponseEntity.ok().body(negociationService.historic(communityCenterName, negociationDate));
    }
}
