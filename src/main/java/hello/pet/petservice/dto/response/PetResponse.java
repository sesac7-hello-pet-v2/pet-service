package hello.pet.petservice.dto.response;

import hello.pet.petservice.entity.AnimalType;
import hello.pet.petservice.entity.Gender;
import hello.pet.petservice.entity.Health;
import hello.pet.petservice.entity.Pet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetResponse {
    private Long id;
    private AnimalType animalType;
    private String breed;
    private Gender gender;
    private Health health;
    private String personality;
    private Integer age;
    private String imageUrl;
    private Boolean announced;

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
                          .announced(pet.getAnnounced())
                          .build();
    }
}
