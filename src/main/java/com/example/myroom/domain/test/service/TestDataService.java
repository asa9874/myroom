package com.example.myroom.domain.test.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myroom.domain.member.model.Member;
import com.example.myroom.domain.member.model.Role;
import com.example.myroom.domain.member.repository.MemberRepository;
import com.example.myroom.domain.model3D.model.Model3D;
import com.example.myroom.domain.model3D.repository.Model3DRepository;
import com.example.myroom.domain.post.model.Category;
import com.example.myroom.domain.post.model.Post;
import com.example.myroom.domain.post.model.VisibilityScope;
import com.example.myroom.domain.post.repository.PostRepository;
import com.example.myroom.domain.test.dto.TestDataResultDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TestDataService {

    private final MemberRepository memberRepository;
    private final Model3DRepository model3DRepository;
    private final PostRepository postRepository;
    private final Random random = new Random();

    public TestDataResultDto createTestMembers(int count) {
        List<Long> createdIds = new ArrayList<>();
        
        for (int i = 1; i <= count; i++) {
            Member member = Member.builder()
                    .name("테스트유저" + i)
                    .email("testuser" + i + "@example.com")
                    .password("password" + i)
                    .build();
            
            Member savedMember = memberRepository.save(member);
            createdIds.add(savedMember.getId());
        }
        
        String message = count + "개의 테스트 회원이 생성되었습니다.";
        log.info("테스트 데이터 생성: {}", message);
        
        return TestDataResultDto.of(count, message, createdIds);
    }

    public TestDataResultDto createTestModel3Ds(int count) {
        List<Member> members = memberRepository.findAll();
        if (members.isEmpty()) {
            throw new IllegalStateException("테스트 회원이 먼저 생성되어야 합니다.");
        }
        
        List<Long> createdIds = new ArrayList<>();
        String[] furnitureTypes = {"chair", "table", "bed", "sofa", "desk"};
        
        for (int i = 1; i <= count; i++) {
            Member randomMember = members.get(random.nextInt(members.size()));
            String furnitureType = furnitureTypes[random.nextInt(furnitureTypes.length)];
            
            Model3D model3D = Model3D.builder()
                    .name("테스트3D모델" + i)
                    .createdAt(LocalDateTime.now())
                    .link("https://s3.example.com/test-model-" + i + ".glb")
                    .creatorId(randomMember.getId())
                    .isShared(random.nextBoolean())
                    .description("테스트용 3D 모델 " + i + "번입니다.")
                    .thumbnailUrl("https://s3.example.com/test-thumbnail-" + i + ".jpg")
                    .isVectorDbTrained(false)
                    .furnitureType(furnitureType)
                    .build();
            
            Model3D savedModel = model3DRepository.save(model3D);
            createdIds.add(savedModel.getId());
        }
        
        String message = count + "개의 테스트 3D 모델이 생성되었습니다.";
        log.info("테스트 데이터 생성: {}", message);
        
        return TestDataResultDto.of(count, message, createdIds);
    }

    public TestDataResultDto createTestPosts(int count) {
        List<Member> members = memberRepository.findAll();
        List<Model3D> model3Ds = model3DRepository.findAll();
        
        if (members.isEmpty()) {
            throw new IllegalStateException("테스트 회원이 먼저 생성되어야 합니다.");
        }
        
        List<Long> createdIds = new ArrayList<>();
        Category[] categories = Category.values();
        VisibilityScope[] scopes = VisibilityScope.values();
        
        String[] titles = {
            "모던한 가구 추천해주세요",
            "인테리어 조언 구합니다",
            "이 제품 어떤가요?",
            "후기 남깁니다",
            "질문이 있어요",
            "새로운 디자인 공유",
            "가구 구매 후기",
            "인테리어 완성했어요"
        };
        
        String[] contents = {
            "거실에 어울릴 만한 가구를 찾고 있습니다. 추천 부탁드려요.",
            "작은 공간을 효율적으로 사용할 수 있는 방법이 있을까요?",
            "이 제품의 품질이나 사용감이 궁금합니다.",
            "최근에 구매한 제품에 대한 솔직한 후기를 남깁니다.",
            "가구 배치에 대해 궁금한 것이 있어서 질문드립니다.",
            "새로운 디자인을 완성했는데 의견을 들어보고 싶습니다."
        };
        
        for (int i = 1; i <= count; i++) {
            Member randomMember = members.get(random.nextInt(members.size()));
            Category category = categories[random.nextInt(categories.length)];
            VisibilityScope scope = scopes[random.nextInt(scopes.length)];
            
            // 30% 확률로 3D 모델 첨부
            Model3D attachedModel = null;
            if (!model3Ds.isEmpty() && random.nextFloat() < 0.3f) {
                // 같은 회원이 만든 3D 모델만 첨부 가능
                List<Model3D> memberModels = model3Ds.stream()
                        .filter(model -> model.getCreatorId().equals(randomMember.getId()))
                        .toList();
                if (!memberModels.isEmpty()) {
                    attachedModel = memberModels.get(random.nextInt(memberModels.size()));
                }
            }
            
            String title = titles[random.nextInt(titles.length)];
            String content = contents[random.nextInt(contents.length)];
            
            Post post = Post.builder()
                    .member(randomMember)
                    .model3D(attachedModel)
                    .title(title + " #" + i)
                    .content(content + " (테스트 게시글 " + i + "번)")
                    .category(category)
                    .visibilityScope(scope)
                    .build();
            
            Post savedPost = postRepository.save(post);
            createdIds.add(savedPost.getId());
        }
        
        String message = count + "개의 테스트 게시글이 생성되었습니다.";
        log.info("테스트 데이터 생성: {}", message);
        
        return TestDataResultDto.of(count, message, createdIds);
    }

    public TestDataResultDto createFullTestDataSet() {
        // 순서대로 생성: Member -> Model3D -> Post
        createTestMembers(10);
        createTestModel3Ds(15);
        createTestPosts(25);
        
        long memberCount = memberRepository.count();
        long modelCount = model3DRepository.count();
        long postCount = postRepository.count();
        
        String message = String.format("전체 테스트 데이터 세트가 생성되었습니다. (회원: %d, 3D모델: %d, 게시글: %d)", 
                memberCount, modelCount, postCount);
        
        log.info("테스트 데이터 생성: {}", message);
        
        return TestDataResultDto.of((int)(memberCount + modelCount + postCount), message, 
                List.of(memberCount, modelCount, postCount));
    }

    public TestDataResultDto deleteAllTestData() {
        long postCount = postRepository.count();
        long modelCount = model3DRepository.count();
        long memberCount = memberRepository.count();
        
        // 역순으로 삭제: Post -> Model3D -> Member
        postRepository.deleteAll();
        model3DRepository.deleteAll();
        memberRepository.deleteAll();
        
        long totalDeleted = postCount + modelCount + memberCount;
        String message = String.format("모든 테스트 데이터가 삭제되었습니다. (총 %d개)", totalDeleted);
        
        log.info("테스트 데이터 삭제: {}", message);
        
        return TestDataResultDto.of((int)totalDeleted, message, 
                List.of(postCount, modelCount, memberCount));
    }

    public TestDataResultDto deleteTestMembers() {
        long count = memberRepository.count();
        memberRepository.deleteAll();
        
        String message = count + "개의 테스트 회원이 삭제되었습니다.";
        log.info("테스트 데이터 삭제: {}", message);
        
        return TestDataResultDto.of((int)count, message, List.of());
    }

    public TestDataResultDto deleteTestModel3Ds() {
        long count = model3DRepository.count();
        model3DRepository.deleteAll();
        
        String message = count + "개의 테스트 3D 모델이 삭제되었습니다.";
        log.info("테스트 데이터 삭제: {}", message);
        
        return TestDataResultDto.of((int)count, message, List.of());
    }

    public TestDataResultDto deleteTestPosts() {
        long count = postRepository.count();
        postRepository.deleteAll();
        
        String message = count + "개의 테스트 게시글이 삭제되었습니다.";
        log.info("테스트 데이터 삭제: {}", message);
        
        return TestDataResultDto.of((int)count, message, List.of());
    }
}