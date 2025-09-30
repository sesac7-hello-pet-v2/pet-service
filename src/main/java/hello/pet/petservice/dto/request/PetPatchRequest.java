package hello.pet.petservice.dto.request;

import hello.pet.petservice.entity.AnimalType;
import hello.pet.petservice.entity.Gender;
import hello.pet.petservice.entity.Health;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetPatchRequest {
    private AnimalType animalType;

    private String breed;

    private Gender gender;

    private Health health;

    private String personality;

    @Positive(message = "나이는 양수여야 합니다")
    private Integer age;

    @Pattern(regexp = "^https?://.*", message = "올바른 URL 형식이어야 합니다")
    private String imageUrl;
}
