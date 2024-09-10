package com.example.communitycenter.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "resource_points")
public class ResourcesPoints {
    private String name;
    private int point;
}
