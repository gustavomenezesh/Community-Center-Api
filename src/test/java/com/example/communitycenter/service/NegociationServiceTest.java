package com.example.communitycenter.service;

import com.example.communitycenter.dtos.CreateNegociationFormDTO;
import com.example.communitycenter.dtos.NegociationResourcesFormDTO;
import com.example.communitycenter.model.*;
import com.example.communitycenter.repository.CommunityCenterRepository;
import com.example.communitycenter.repository.NegociationRepository;
import com.example.communitycenter.repository.NegociationRepositoryCustom;
import com.example.communitycenter.repository.ResourcePointsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NegociationServiceTest {

    @InjectMocks
    private NegociationService negociationService;

    @Mock
    private ResourcePointsRepository resourcePointsRepository;

    @Mock
    private NegociationRepository negociationRepository;

    @Mock
    private NegociationRepositoryCustom negociationRepositoryCustom;

    @Mock
    private CommunityCenterRepository communityCenterRepository;

    @Test
    public void shouldCreateNegociationWithoutHighOccupancyAndEqualPoints() {
        CreateNegociationFormDTO formDTO = new CreateNegociationFormDTO();
        formDTO.setOriginCenterName("Centro A");
        formDTO.setDestinationCenterName("Centro B");

        List<NegociationResourcesFormDTO> originResources = Arrays.asList(
                new NegociationResourcesFormDTO("doctors", 1),
                new NegociationResourcesFormDTO("volunteers", 1)
        );
        formDTO.setOriginResources(originResources);

        List<NegociationResourcesFormDTO> destinationResources = Arrays.asList(
                new NegociationResourcesFormDTO("medicalSuppliesKits", 1)
        );
        formDTO.setDestinationResources(destinationResources);

        CommunityCenter centerA = new CommunityCenter("1", "Centro A", new Address(), 100, 70, new Resources(1,1,0,0,0));
        CommunityCenter centerB = new CommunityCenter("2", "Centro B", new Address(), 200, 100, new Resources(0,1,1,0,0));

        Mockito.when(communityCenterRepository.findByName("Centro A")).thenReturn(Optional.of(centerA));
        Mockito.when(communityCenterRepository.findByName("Centro B")).thenReturn(Optional.of(centerB));

        Mockito.when(resourcePointsRepository.findByName("doctors")).thenReturn(Optional.of(new ResourcesPoints("doctors", 4)));
        Mockito.when(resourcePointsRepository.findByName("volunteers")).thenReturn(Optional.of(new ResourcesPoints("volunteers", 3)));
        Mockito.when(resourcePointsRepository.findByName("medicalSuppliesKits")).thenReturn(Optional.of(new ResourcesPoints("medicalSuppliesKits", 7)));

        Negociation expectedNegociation = new Negociation(
                "1",  // ID de exemplo
                "Centro A",
                "Centro B",
                originResources.stream()
                        .map(resourceFormDTO -> new NegociationResources(resourceFormDTO.getName(), resourceFormDTO.getQuantity()))
                        .toList(),
                destinationResources.stream()
                        .map(resourceFormDTO -> new NegociationResources(resourceFormDTO.getName(), resourceFormDTO.getQuantity()))
                        .toList(),
                LocalDateTime.now()
        );

        Mockito.when(negociationRepository.save(Mockito.argThat(negociation ->
                negociation.getOriginCenterName().equals("Centro A") &&
                        negociation.getDestinationCenterName().equals("Centro B") &&
                        negociation.getOriginResources().size() == originResources.size() &&
                        negociation.getDestinationResources().size() == destinationResources.size()
        ))).thenReturn(expectedNegociation);

        Negociation createdNegociation = negociationService.create(formDTO);

        assertEquals(expectedNegociation.getOriginCenterName(), createdNegociation.getOriginCenterName());
        assertEquals(expectedNegociation.getDestinationCenterName(), createdNegociation.getDestinationCenterName());
        assertEquals(expectedNegociation.getOriginResources().size(), createdNegociation.getOriginResources().size());
        assertEquals(expectedNegociation.getDestinationResources().size(), createdNegociation.getDestinationResources().size());
    }

    @Test
    public void shouldNotCreateNegociationWithoutHighOccupancyAndUnequalPoints() {
        CreateNegociationFormDTO formDTO = new CreateNegociationFormDTO();
        formDTO.setOriginCenterName("Centro A");
        formDTO.setDestinationCenterName("Centro B");

        List<NegociationResourcesFormDTO> originResources = Collections.singletonList(
                new NegociationResourcesFormDTO("doctors", 2)
        );
        formDTO.setOriginResources(originResources);

        List<NegociationResourcesFormDTO> destinationResources = Collections.singletonList(
                new NegociationResourcesFormDTO("medicalSuppliesKits", 1)
        );
        formDTO.setDestinationResources(destinationResources);

        CommunityCenter centerA = new CommunityCenter("1", "Centro A", new Address(), 100, 70, new Resources());
        CommunityCenter centerB = new CommunityCenter("2", "Centro B", new Address(), 200, 100, new Resources());

        Mockito.when(communityCenterRepository.findByName("Centro A")).thenReturn(Optional.of(centerA));
        Mockito.when(communityCenterRepository.findByName("Centro B")).thenReturn(Optional.of(centerB));

        Mockito.when(resourcePointsRepository.findByName("doctors")).thenReturn(Optional.of(new ResourcesPoints("doctors", 4)));
        Mockito.when(resourcePointsRepository.findByName("medicalSuppliesKits")).thenReturn(Optional.of(new ResourcesPoints("medicalSuppliesKits", 7)));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            negociationService.create(formDTO);
        });

        assertEquals("The negociation points aren't equal", exception.getMessage());
    }

    @Test
    public void shouldGetNegociationHistory() {
        List<Negociation> negociationHistory = Arrays.asList(
                new Negociation("1", "Centro A", "Centro B", new ArrayList<>(), new ArrayList<>(), LocalDateTime.now().minusDays(1)),
                new Negociation("2", "Centro A", "Centro C", new ArrayList<>(), new ArrayList<>(), LocalDateTime.now())
        );

        Mockito.when(negociationRepositoryCustom.findHistoric(Mockito.eq("Centro A"), Mockito.any(LocalDateTime.class)))
                .thenReturn(negociationHistory);

        Mockito.when(communityCenterRepository.findByName("Centro A")).thenReturn(Optional.of(new CommunityCenter(null, "Centro A", null, 100, 0, null)));
        List<Negociation> result = negociationService.historic("Centro A", LocalDateTime.now().minusHours(3));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Centro A", result.get(0).getOriginCenterName());
        assertEquals("Centro C", result.get(1).getDestinationCenterName());
    }


}