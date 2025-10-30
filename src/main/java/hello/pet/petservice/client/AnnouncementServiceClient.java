package hello.pet.petservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "announcement-service",
        url = "${ANNOUNCEMENT_SERVICE_URL:http://localhost:8084}"
)
public interface AnnouncementServiceClient {

    /**
     * 특정 펫으로 등록된 활성 공고가 있는지 확인
     *
     * @param petId 펫 ID
     * @return 활성 공고 존재 여부
     */
    @GetMapping("/v1/announcements/check-pet/{petId}")
    Boolean hasActiveAnnouncements(@PathVariable("petId") Long petId);
}
