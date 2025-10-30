package hello.pet.petservice.facade;

import hello.pet.petservice.client.ImageServiceClient;
import hello.pet.petservice.client.ImageUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageServiceFacade {

    private final ImageServiceClient imageServiceClient;

    /**
     * 펫 이미지를 S3에 업로드
     *
     * @param shelterId 보호소 ID (userId로 사용)
     * @param image     업로드할 이미지 파일
     * @return S3 key 값, 실패 시 null
     */
    public String uploadPetImage(Long shelterId, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            log.debug("이미지 파일이 없거나 비어있음");
            return null;
        }

        try {
            log.info("펫 이미지 업로드 시작 - shelterId: {}, fileName: {}, fileSize: {}",
                    shelterId, image.getOriginalFilename(), image.getSize());

            ResponseEntity<ImageUploadResponse> response = imageServiceClient.uploadImage(
                    shelterId,
                    null,  // postId는 펫의 경우 사용하지 않음
                    "pet", // 이미지 타입
                    image
            );

            if (response.getBody() != null && response.getBody().getS3Key() != null) {
                String s3Key = response.getBody().getS3Key();
                log.info("펫 이미지 업로드 성공 - S3 key: {}", s3Key);
                return s3Key;
            }

            log.warn("이미지 업로드 응답이 비어있음");
            return null;
        } catch (Exception e) {
            log.error("펫 이미지 업로드 실패 - shelterId: {}, fileName: {}",
                    shelterId, image.getOriginalFilename(), e);
            // 이미지 업로드 실패 시 null 반환 (기본 이미지 사용)
            return null;
        }
    }

}
