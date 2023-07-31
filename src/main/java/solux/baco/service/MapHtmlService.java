//origin/develop merge 테스트
package solux.baco.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import solux.baco.domain.Member;
import solux.baco.domain.Review;
import solux.baco.repository.ReviewRepository;
import solux.baco.service.ReviewModel.ReviewDetailDTO;
import solux.baco.service.ReviewModel.returnReviewDataDTO;
import solux.baco.service.RouteModel.JsonDataEntity;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class MapHtmlService {

    private final ReviewRepository reviewRepository;
    private final MemberService memberService;
    private final ReviewDetailDTO reviewDetailDTO;


    public MapHtmlService(ReviewRepository reviewRepository, MemberService memberService, ReviewDetailDTO reviewDetailDTO) {

        this.reviewRepository = reviewRepository;
        this.memberService = memberService;
        this.reviewDetailDTO = reviewDetailDTO;
    }


    //데이터 저장할 때
    @Transactional
    public returnReviewDataDTO saveReview(String startPlace, String endPlace, String content, String routePoint) {
        log.info("checklog: review.setRoutePoint(routePoint): {}", startPlace);
        log.info("checklog: review.setRoutePoint(routePoint): {}", endPlace);
        log.info("checklog: review.setRoutePoint(routePoint): {}", content);
        log.info("checklog: review.setRoutePoint(routePoint): {}", routePoint);


        Review review = new Review();
        returnReviewDataDTO returnReviewDataDTO = new returnReviewDataDTO();
        String mapUrl;

        String email = "maptest@test.com";


        //2. 이메일을 통해서 작성자의 Member객체 받아오기
        Optional<Member> writerInfo = memberService.findByEmail(email);
        log.info("checklog: writerInfo: {}", writerInfo);

        //3. 받아온 Member객체를 통해서 member_id 추출하기
        if (writerInfo.isPresent()) {
            //Optional 객체 속의 요소인 Member객체를 가져오기 위해 .get() //(null이 아닐 때만 get으로 가져올 수 있음.)
            Member member = writerInfo.get();
            //Long member_id = member.getMember_id();
            log.info("checklog: review.routePoint: {}", routePoint);


            review.setMember(member); //작성자의 member테이블 레코드 저장
            review.setContent(content);
            review.setStartPlace(startPlace);
            review.setEndPlace(endPlace);
            review.setDate(LocalDate.now());
            review.setRoute_point(routePoint); //(7/30)수정-route테이블은 삭제, 후기테이블에 routePoint 컬럼 추가.
            log.info("checklog: review.setRoutePoint(routePoint): {}", review.getRoute_point());

            reviewRepository.save(review);
            mapUrl = "html 동적 렌더링 구현 후 html 주소 저장 예정";

            returnReviewDataDTO.setStartPlace(review.getStartPlace());
            returnReviewDataDTO.setEndPlace(review.getEndPlace());
            returnReviewDataDTO.setContent(review.getContent());
            returnReviewDataDTO.setMapUrl(mapUrl);

        } else {
            mapUrl = "실패";
        }
        return returnReviewDataDTO;
    }


    /**
     * 상세조회 관련 메서드
     */


    //컨트롤러에서 호출당하는메서드(경로데이터 빼올 때)
    public String getJsonData(Long review_id) {
        log.info("checklog: MapHtmlService_getJsonData");

        try {
            String routePointString = reviewRepository.routeData(review_id); //review_id로 route데이터 빼오는 과정
            log.info("checklog: MapHtmlService_getJsonData-routePoint: {}", routePointString);
            //checklog: ReviewService_getJsonData-routePoint: [[37.5668417,126.978588], ... ,[37.5754594,126.9761248]]


            Map<String, Object> makeJson = new HashMap<>();
            makeJson.put("route_point", routePointString);

            ObjectMapper objectMapper = new ObjectMapper();
            routePointString = objectMapper.writeValueAsString(makeJson);
             return routePointString; //일단 controller로 다시 반환 (html 동적 렌더링을 하기 위해서)
//checklog: ReviewService_getJsonData-routePointString: {"route_point":"[[37.5668417,126.978588]], ... ,[37.5754594,126.9761248]]"} => 이 형태가 html에서 파싱 더 편리.
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //(데이터 빼올 때 )
    public ReviewDetailDTO reviewDetail(Long review_id, String mapUrl) {
        ReviewDetailDTO reviewDetailDTO = new ReviewDetailDTO();
        log.info("checklog: MapHtmlService_reviewDetail-review_id: {},mapUrl: {}", review_id, mapUrl);

        //review_id에 해당되는 저장값 가져오도록 repository 호출
        try {
            Optional<Review> reviewEntity = reviewRepository.detailReview(review_id);
            log.info("checklog: MapHtmlService_reviewDetail-reviewEntity: {}", reviewEntity);

            // Optional이기 때문에 null일 수도 있음.
            if (reviewEntity.isPresent()) {
                Review review = reviewEntity.get();
                //String startPlace = review.getStartPlace();
                //String endPlace = review.getEndPlace();
                String content = review.getContent();
                Long member_id = review.getMember().getMember_id();
                log.info("checklog: MapHtmlService_reviewDetail-content: {}", content);
                log.info("checklog: MapHtmlService_reviewDetail-member_id: {}", member_id);

                //return할 dto로 변환

                reviewDetailDTO.setContent(content);
                reviewDetailDTO.setMapUrl(mapUrl);
            }

        } catch (Exception e) {

            return null; //예외처리 구현예정
        }
        return reviewDetailDTO;
    }
}

