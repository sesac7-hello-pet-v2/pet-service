package hello.pet.petservice.facade;

import hello.pet.petservice.client.AnnouncementServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnnouncementServiceFacade {

    private final AnnouncementServiceClient announcementServiceClient;

    /**
     * 특정 펫으로 등록된 활성 공고가 있는지 확인
     *
     * @param petId 펫 ID
     * @return 활성 공고 존재 여부
     */
    public boolean hasActiveAnnouncements(Long petId) {
        try {
            log.debug("펫 ID {}의 활성 공고 존재 여부 확인", petId);
            Boolean result = announcementServiceClient.hasActiveAnnouncements(petId);
            return result != null && result;
        } catch (Exception e) {
            log.error("공고 존재 여부 확인 중 오류 발생 - petId: {}", petId, e);
            // 안전하게 false 반환 (서비스 장애 시 펫 삭제는 허용)
            return false;
        }
    }
}
