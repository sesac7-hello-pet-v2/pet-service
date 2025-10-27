package hello.pet.petservice.dto.request;

import hello.pet.petservice.entity.AnimalType;
import hello.pet.petservice.entity.Gender;
import hello.pet.petservice.entity.Health;
import hello.pet.petservice.entity.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetCreateRequest {
    @NotNull(message = "동물 종류는 필수입니다")
    private AnimalType animalType;

    @NotBlank(message = "품종은 필수입니다")
    private String breed;

    @NotNull(message = "성별은 필수입니다")
    private Gender gender;

    @NotNull(message = "건강 상태는 필수입니다")
    private Health health;

    @NotBlank(message = "성격은 필수입니다")
    private String personality;

    @NotNull(message = "나이는 필수입니다")
    @Positive(message = "나이는 양수여야 합니다")
    private Integer age;

    @Pattern(regexp = "^$|^https?://.*", message = "올바른 URL 형식이어야 합니다")
    private String imageUrl;

    public Pet toEntity(Long userId) {
        return Pet.builder()
                  .shelterId(userId)
                  .animalType(animalType)
                  .breed(breed)
                  .gender(gender)
                  .health(health)
                  .age(age)
                  .personality(personality)
                  .imageUrl(imageUrl)
                  .build();
    }
}
