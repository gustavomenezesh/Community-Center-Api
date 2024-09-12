package com.example.communitycenter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@Document(collection = "resource_points")
public class ResourcesPoints {
    private String name;
    private int point;
}
