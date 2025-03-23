//package com.guenbon.siso.service.congressman;
//
//import com.guenbon.siso.dto.congressman.SyncCongressmanDTO;
//import com.guenbon.siso.dto.congressman.response.CongressmanBatchResultDTO;
//import com.guenbon.siso.entity.congressman.AssemblySession;
//import com.guenbon.siso.entity.congressman.Congressman;
//import com.guenbon.siso.repository.assemblysession.AssemblySessionRepository;
//import com.guenbon.siso.repository.congressman.CongressmanRepository;
//import com.guenbon.siso.support.fixture.congressman.CongressmanFixture;
//import com.guenbon.siso.support.fixture.congressman.SyncCongressmanDTOFixture;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Set;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//
//@SpringBootTest
//@Slf4j
//@Transactional
//public class CongressmanServiceIntegrationTest {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Autowired
//    CongressmanService congressmanService;
//
//    @Autowired
//    CongressmanRepository congressmanRepository;
//
//    @Autowired
//    AssemblySessionRepository assemblySessionRepository;
//
//
//    @BeforeEach
//    void beforeEach() {
//        congressmanRepository.deleteAll();
//    }
//
//    @AfterEach
//    void afterEach() {
//        congressmanRepository.deleteAll();
//    }
//
//    @Test
//    @DisplayName("syncCongressman 이 db에 없는 데이터는 insert, db와 다른 데이터는 update, db 에만 있는 데이터는 delete 한다.")
//    void valid_syncCongressman_success() {
//        // given
//        // 삽입 : 최신에 있고 db에 없음
//        Congressman son = CongressmanFixture.builder().setName("son").setCode("abc123").build();
//        // 수정 : 최신, db에 있는데 상태가 다름
//        Congressman leeDatabase = CongressmanFixture.builder().setName("lee").setCode("abc456").build();
//        // 삭제 : db에 있고  최신에 없음
//        Congressman kim = CongressmanFixture.builder().setName("kim").setCode("abc789").build();
//
//        // db에 저장
//        Congressman leeSaved = congressmanRepository.save(leeDatabase);
//        log.info("leeSaved 확인 : {}", leeSaved);
//        Congressman kimSaved = congressmanRepository.save(kim);
//
//        AssemblySession lee21 = assemblySessionRepository.save(AssemblySession.builder().congressman(leeSaved).session(21).build());
//        AssemblySession lee22 = assemblySessionRepository.save(AssemblySession.builder().congressman(leeSaved).session(22).build());
//        AssemblySession kim22 = assemblySessionRepository.save(AssemblySession.builder().congressman(kimSaved).session(22).build());
//        log.info("kim 22 대수 : " + kim22.toString());
//
//        Congressman leeRecentEntity = CongressmanFixture.builder().setName("lee new").setCode("abc456").build();
//
//        // lee 는 최신 데이터가 업데이트됨
//        SyncCongressmanDTO sonRecent = SyncCongressmanDTOFixture.builder().setCongressman(son).build();
//        SyncCongressmanDTO leeRecent = SyncCongressmanDTOFixture.builder().setCongressman(leeRecentEntity).setAssemblySessions(Set.of(22, 23)).build();
//
//        List<SyncCongressmanDTO> recent = List.of(sonRecent, leeRecent);
//
//        // when
//        CongressmanBatchResultDTO congressmanBatchResultDTO = congressmanService.syncCongressman(recent);
//
//        // then
//        List<Congressman> result = congressmanRepository.findAll();
//        Congressman leeResult = congressmanRepository.findById(leeSaved.getId()).get();
//        boolean kimPresent = congressmanRepository.findById(kim.getId()).isPresent();
//
//        boolean lee21Present = assemblySessionRepository.findById(lee21.getId()).isPresent();
//        boolean lee22Present = assemblySessionRepository.findById(lee22.getId()).isPresent();
//        boolean kim22Present = assemblySessionRepository.findById(kim22.getId()).isPresent();
//
//        log.info("결과 확인");
//        log.info(leeResult.toString());
//
//        assertThat(lee21Present).isFalse();
//        assertThat(lee22Present).isTrue();
//        assertThat(kim22Present).isFalse();
//        assertThat(result.size()).isEqualTo(2);
//        assertThat(leeResult.getName()).isEqualTo(leeRecentEntity.getName());
//        assertThat(kimPresent).isFalse();
//        assertThat(congressmanBatchResultDTO.getBatchInsertResult().size()).isEqualTo(1);
//        assertThat(congressmanBatchResultDTO.getBatchRemoveCount()).isEqualTo(1);
//        assertThat(congressmanBatchResultDTO.getBatchUpdateCount()).isEqualTo(1);
//    }
//}
