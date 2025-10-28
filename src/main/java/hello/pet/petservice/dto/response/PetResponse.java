package hello.pet.petservice.dto.response;

import hello.pet.petservice.entity.AnimalType;
import hello.pet.petservice.entity.Gender;
import hello.pet.petservice.entity.Health;
import hello.pet.petservice.entity.Pet;
import hello.pet.petservice.entity.PetStatus;
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
    private Long shelterId;
    private AnimalType animalType;
    private String breed;
    private Gender gender;
    private Health health;
    private String personality;
    private Integer age;
    private String thumbnailUrl;  // 리스트용 썸네일 이미지 URL
    private String imageUrl;      // 상세 페이지용 이미지 URL
    private PetStatus status;

    public static PetResponse from(Pet pet, String s3BaseUrl, String defaultImageUrl) {
        return PetResponse.builder()
                          .id(pet.getId())
                          .shelterId(pet.getShelterId())
                          .animalType(pet.getAnimalType())
                          .breed(pet.getBreed())
                          .gender(pet.getGender())
                          .health(pet.getHealth())
                          .personality(pet.getPersonality())
                          .age(pet.getAge())
                          .thumbnailUrl(generateThumbnailUrl(pet.getImageS3Key(), s3BaseUrl, defaultImageUrl))
                          .imageUrl(generateImageUrl(pet.getImageS3Key(), s3BaseUrl, defaultImageUrl))
                          .status(pet.getStatus())
                          .build();
    }

    /**
     * 썸네일 URL 생성
     */
    private static String generateThumbnailUrl(String imageS3Key, String s3BaseUrl, String defaultImageUrl) {
        return imageS3Key != null
               ? s3BaseUrl + "/" + imageS3Key + "_thumb.jpg"
               : defaultImageUrl;
    }

    /**
     * 피드 이미지 URL 생성
     */
    private static String generateImageUrl(String imageS3Key, String s3BaseUrl, String defaultImageUrl) {
        return imageS3Key != null
               ? s3BaseUrl + "/" + imageS3Key + "_feed.jpg"
               : defaultImageUrl;
    }
}
