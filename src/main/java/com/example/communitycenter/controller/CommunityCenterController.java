package com.example.communitycenter.controller;

import com.example.communitycenter.dtos.CreateCommunityCenterFormDTO;
import com.example.communitycenter.dtos.UpdateOccupancyFormDTO;
import com.example.communitycenter.model.CommunityCenter;
import com.example.communitycenter.response.AverageResourcesResponse;
import com.example.communitycenter.service.CommunityCenterService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/community-centers")
public class CommunityCenterController {

    @Autowired
    private CommunityCenterService communityCenterService;

    @PostMapping("/")
    @Operation(summary = "Create a new Community Center")
    public ResponseEntity<CommunityCenter> create(@Valid @RequestBody CreateCommunityCenterFormDTO communityCenter) {
        CommunityCenter savedCommunityCenter = communityCenterService.create(communityCenter);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCommunityCenter.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedCommunityCenter);
    }

    @PatchMapping("/{name}")
    @Operation(summary = "Update Community Center Ocuupancy")
    public ResponseEntity<Void> updateOccupancy(@PathVariable String name, @Valid @RequestBody UpdateOccupancyFormDTO form) {
        communityCenterService.updateOccupancy(name, form);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/highOccupancy")
    @Operation(summary = "List High Occupancy Community Center")
    public ResponseEntity<List<CommunityCenter>> listHighOccupancy(){
        return ResponseEntity.ok().body(communityCenterService.listHighOccupancyCenters());
    }

    @GetMapping("/averageResources")
    @Operation(summary = "Get Resources Average")
    public ResponseEntity<AverageResourcesResponse> getAverageResources(){
        return ResponseEntity.ok().body(communityCenterService.getAverageResources());
    }
}
