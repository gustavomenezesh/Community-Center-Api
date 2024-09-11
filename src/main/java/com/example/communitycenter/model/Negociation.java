package com.example.communitycenter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Document(collection = "negociations")
public class Negociation {
    @Id
    private String id;
    private String originCenterName;
    private String destinationCenterName;
    private List<NegociationResources> originResources;
    private List<NegociationResources> destinationResources;
    private LocalDateTime timestamp;
}
