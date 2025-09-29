package hello.pet.petservice.dto.request;

import hello.pet.petservice.entity.AnimalType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PetUpdateRequest {
    private AnimalType animalType;
    private String breed;
    private String gender;
    private String health;
    private String personality;
    private Integer age;
    private String imageUrl;
}
