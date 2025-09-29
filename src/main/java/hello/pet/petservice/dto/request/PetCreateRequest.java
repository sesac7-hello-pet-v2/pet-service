package hello.pet.petservice.dto.request;

import hello.pet.petservice.entity.AnimalType;
import hello.pet.petservice.entity.Gender;
import hello.pet.petservice.entity.Health;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PetCreateRequest {
    private AnimalType animalType;
    private String breed;
    private Gender gender;
    private Health health;
    private String personality;
    private Integer age;
    private String imageUrl;
}
