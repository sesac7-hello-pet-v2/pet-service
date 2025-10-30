package hello.pet.petservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
        name = "image-service",
        url = "${IMAGE_SERVICE_URL:http://localhost:8088}"
)
public interface ImageServiceClient {

    /**
     * 이미지를 S3에 업로드
     *
     * @param userId 사용자(보호소) ID
     * @param postId 게시글 ID (펫의 경우 사용하지 않음)
     * @param type   이미지 타입 (pet/user/feed)
     * @param file   업로드할 이미지 파일
     * @return S3 key를 포함한 응답
     */
    @PostMapping(value = "/internal/v1/images/post/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ImageUploadResponse> uploadImage(
            @RequestPart("userId") Long userId,
            @RequestPart(value = "postId", required = false) String postId,
            @RequestPart("type") String type,
            @RequestPart("file") MultipartFile file
    );
}
