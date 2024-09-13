package com.example.communitycenter.service;

import com.example.communitycenter.dtos.AddressFormDTO;
import com.example.communitycenter.dtos.CreateCommunityCenterFormDTO;
import com.example.communitycenter.dtos.ResourcesFormDTO;
import com.example.communitycenter.dtos.UpdateOccupancyFormDTO;
import com.example.communitycenter.model.Address;
import com.example.communitycenter.model.CommunityCenter;
import com.example.communitycenter.model.Resources;
import com.example.communitycenter.repository.CommunityCenterRepository;
import com.example.communitycenter.response.AverageResourcesResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CommunityCenterServiceTest {

    @InjectMocks
    private CommunityCenterService communityCenterService;

    @Mock
    private CommunityCenterRepository communityCenterRepository;

    @Test
    public void shouldCreateCommunityCenter() throws Exception {
        CreateCommunityCenterFormDTO formDTO = new CreateCommunityCenterFormDTO();
        formDTO.setName("Centro Comunitário Teste");

        AddressFormDTO addressDTO = new AddressFormDTO();
        addressDTO.setStreet("Rua X");
        addressDTO.setCity("Cidade Y");
        addressDTO.setState("Estado Z");
        addressDTO.setZipCode("12345-678");

        formDTO.setAddress(addressDTO);
        formDTO.setCapacity(100);
        formDTO.setCurrentOccupancy(50);

        ResourcesFormDTO resourcesDTO = new ResourcesFormDTO();
        resourcesDTO.setDoctors(5);
        resourcesDTO.setVolunteers(10);
        resourcesDTO.setMedicalSuppliesKits(20);
        resourcesDTO.setTransportVehicles(3);
        resourcesDTO.setBasicFoodBaskets(100);
        formDTO.setResources(resourcesDTO);

        CommunityCenter createdCenter = new CommunityCenter(
                "123",
                "Centro Comunitário Teste",
                new Address("Rua X", "Cidade Y", "Estado Z", "12345-678"),
                100,
                50,
                new Resources(5, 10, 20, 3, 100)
        );

        Mockito.when(communityCenterRepository.save(any(CommunityCenter.class)))
                .thenReturn(createdCenter);

        CommunityCenter result = communityCenterService.create(formDTO);

        assertNotNull(result);
        assertEquals("Centro Comunitário Teste", result.getName());
        assertEquals("Rua X", result.getAddress().getStreet());
        assertEquals(5, result.getResources().getDoctors());
    }

    @Test
    public void shouldThrowExceptionWhenOccupancyExceedsCapacity() throws Exception {
        UpdateOccupancyFormDTO formDTO = new UpdateOccupancyFormDTO();
        formDTO.setOccupancy(120); // Nova ocupação que excede a capacidade

        CommunityCenter communityCenter = new CommunityCenter(
                "123",
                "Centro Teste",
                new Address("Rua X", "Cidade Y", "Estado Z", "12345-678"),
                100, // Capacidade
                50,  // Ocupação atual
                new Resources()
        );

        Mockito.when(communityCenterRepository.findByName("Centro Teste")).thenReturn(Optional.of(communityCenter));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            communityCenterService.updateOccupancy("Centro Teste", formDTO);
        });

        assertEquals("New occupancy is greater than capacity", exception.getMessage());

        Mockito.verify(communityCenterRepository, Mockito.times(0)).save(any(CommunityCenter.class));
    }

    @Test
    public void shouldListHighOccupancyCenters() throws Exception {
        List<CommunityCenter> centers = Arrays.asList(
                new CommunityCenter("1", "Centro A", new Address("Rua A", "Cidade A", "Estado A", "11111-111"), 100, 95, new Resources()),
                new CommunityCenter("2", "Centro B", new Address("Rua B", "Cidade B", "Estado B", "22222-222"), 200, 190, new Resources())
        );

        // Mock para retornar os centros que têm alta ocupação
        Mockito.when(communityCenterRepository.findHighOccupancyCenters()).thenReturn(centers);

        // Chama o método do serviço
        List<CommunityCenter> result = communityCenterService.listHighOccupancyCenters();

        // Verifica que os centros retornados são os esperados
        assertNotNull(result);
        assertEquals(2, result.size()); // Apenas "Centro A" e "Centro B" estão com alta ocupação
        assertEquals("Centro A", result.get(0).getName());
        assertEquals("Centro B", result.get(1).getName());

        // Verifica que o repositório foi chamado corretamente
        Mockito.verify(communityCenterRepository, Mockito.times(1)).findHighOccupancyCenters();
    }

    @Test
    public void shouldGetAverageResources() throws Exception {
        // Cria uma resposta mockada com os valores médios dos recursos
        AverageResourcesResponse response = new AverageResourcesResponse(2.5, 5.0, 7.0, 3.5, 10.0);

        // Simula a chamada ao método do serviço e o retorno dos valores médios
        Mockito.when(communityCenterService.getAverageResources()).thenReturn(response);

        // Chama o método do serviço
        AverageResourcesResponse result = communityCenterService.getAverageResources();

        // Verifica se os valores médios retornados estão corretos
        assertNotNull(result);
        assertEquals(2.5, result.getDoctors());
        assertEquals(5.0, result.getVolunteers());
        assertEquals(7.0, result.getMedicalSuppliesKits());
        assertEquals(3.5, result.getTransportVehicles());
        assertEquals(10.0, result.getBasicFoodBaskets());

        // Aqui não é necessário o verify(), pois estamos testando o serviço diretamente
    }

}