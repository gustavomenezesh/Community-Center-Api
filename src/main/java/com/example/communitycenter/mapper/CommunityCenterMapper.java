package com.example.communitycenter.mapper;

import com.example.communitycenter.dtos.CreateCommunityCenterFormDTO;
import com.example.communitycenter.model.CommunityCenter;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommunityCenterMapper {
    CommunityCenter toEntity(CreateCommunityCenterFormDTO dto);
}
