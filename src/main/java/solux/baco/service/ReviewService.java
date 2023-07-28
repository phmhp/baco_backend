//origin/develop merge 테스트
package solux.baco.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import solux.baco.domain.Member;
import solux.baco.domain.Review;
import solux.baco.repository.ReviewRepository;
import solux.baco.service.ReviewModel.ReviewDTO;
import solux.baco.service.ReviewModel.ReviewDetailDTO;
import solux.baco.service.RouteModel.RouteDTO;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberService memberService;


    public ReviewService(ReviewRepository reviewRepository, MemberService memberService) {


        this.reviewRepository = reviewRepository;
        this.memberService = memberService;
    }


    public ReviewDetailDTO reviewDetail(Long review_id) {
        //review_id에 해당되는 저장값 가져오도록 repository 호출
        ReviewDetailDTO reviewDetailDTO=new ReviewDetailDTO();
        try {
            Optional<Review> reviewEntity = reviewRepository.detailReview(review_id);
            //startPlace,endPlace,content,date,member_id(nickname을 구하기 위해서 member_id도 포함)
            //Optional이기 때문에 null일 수도 있음.
            if (reviewEntity.isPresent()) {
                Review review = reviewEntity.get();
                String startPlace = review.getStartPlace();
                String endPlace = review.getEndPlace();
                String content = review.getContent();
                LocalDate date = review.getDate();
                Long member_id = review.getMember().getMember_id();

                //Optional이기 때문에 null일 수도 있음.
                Optional<Member> memberEntity = reviewRepository.detailMember(member_id);
                if (memberEntity.isPresent()) {
                    Member member = memberEntity.get();
                    String nickname = member.getNickname();

                    //dto로 변환

                    reviewDetailDTO.setStartPlace(startPlace);
                    reviewDetailDTO.setEndPlace(endPlace);
                    reviewDetailDTO.setContent(content);
                    reviewDetailDTO.setDate(date);
                    reviewDetailDTO.setNickname(nickname);
                }
            }


            /**  String startPlace = reviewData.getStartPlace();
             String endPlace = reviewData.getEndPlace();
             String content = reviewData.getContent();
             */



        } catch (Exception e) {
            //예외처리 예정
        }
        return reviewDetailDTO;

    }

    public void saveReview(HttpSession session, String startPlace, String endPlace, String content) {
        //수정된 기능)경로 좌표 반환 api 호출해서 경로좌표,(경로기준)출발좌표,(경로기준)도착좌표 받아오기=>저장할 데이터 준비 완료 => 기능 변경

        //1. 세션에서 이메일 추출하기
        String email = (String) session.getAttribute("loginEmail");
        log.info("checklog: loginEmail : {}", email);
        //전달받은 데이터 예외처리

/**
 //memberService의 findEmail메서드를 호출하고,
 // memberService의 findEmail 메서드는 memberRepository의 findByEmail을 호출하고,
 // memberRepository의 findByEmail메서드는 Member객체를 반환함. (Optional<Member>)
 //Member객체에서 get으로 member_id를 찾으면 됨.
 */

        log.info("checklog: ReviewService");
        //2. 이메일을 통해서 작성자의 Member객체 받아오기
        Optional<Member> writerInfo = memberService.findByEmail(email);
        log.info("checklog: writerInfo: {}", writerInfo);

        //3. 받아온 Member객체를 통해서 member_id 추출하기
        if (writerInfo.isPresent()) {
            //Optional 객체 속의 요소인 Member객체를 가져오기 위해 .get() //(null이 아닐 때만 get으로 가져올 수 있음.)
            Member member = writerInfo.get();
            //Long member_id = member.getMember_id();

            //LocalDate date = LocalDate.now();

            Review review = new Review();
            review.setMember(member); //작성자의 member테이블 레코드 저장
            review.setContent(content);
            review.setStartPlace(startPlace);
            review.setEndPlace(endPlace);
            review.setDate(LocalDate.now());
            log.info("checklog: review: {}", review);

            reviewRepository.save(review);

        }


    }

    //응답 반환


}


