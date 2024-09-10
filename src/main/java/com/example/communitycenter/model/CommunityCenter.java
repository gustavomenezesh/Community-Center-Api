package com.example.communitycenter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "community_centers")
public class CommunityCenter {

    @Id
    private String id;
    private String name;
    private Address address;  // Corrigido para ser um objeto
    private int capacity;
    private int currentOccupancy;
    private Resources resources;  // Incluído para os recursos
}