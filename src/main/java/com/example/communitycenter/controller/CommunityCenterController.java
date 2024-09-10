package com.example.communitycenter.controller;

import com.example.communitycenter.dtos.CreateCommunityCenterFormDTO;
import com.example.communitycenter.model.CommunityCenter;
import com.example.communitycenter.service.CommunityCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/community-centers")
public class CommunityCenterController {

    @Autowired
    private CommunityCenterService communityCenterService;

    @PostMapping
    public ResponseEntity<CommunityCenter> createCommunityCenter(@Valid @RequestBody CreateCommunityCenterFormDTO communityCenter) {
        return ResponseEntity.ok(communityCenterService.createCommunityCenter(communityCenter.transformToObject()));
    }
}
