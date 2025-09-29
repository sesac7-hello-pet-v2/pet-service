package hello.pet.petservice.dto.response;

import hello.pet.petservice.entity.AnimalType;
import hello.pet.petservice.entity.Pet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PetResponse {
    private Long id;
    private AnimalType animalType;
    private String breed;
    private String gender;
    private String health;
    private String personality;
    private Integer age;
    private String imageUrl;

    public static PetResponse from(Pet pet) {
        return PetResponse.builder()
                          .id(pet.getId())
                          .animalType(pet.getAnimalType())
                          .breed(pet.getBreed())
                          .gender(pet.getGender())
                          .health(pet.getHealth())
                          .personality(pet.getPersonality())
                          .age(pet.getAge())
                          .imageUrl(pet.getImageUrl())
                          .build();
    }
}
