package com.guenbon.siso.service.congressman;

import com.guenbon.siso.dto.congressman.response.CongressmanBatchResultDTO;
import com.guenbon.siso.entity.congressman.Congressman;
import com.guenbon.siso.repository.congressman.CongressmanRepository;
import com.guenbon.siso.support.fixture.congressman.CongressmanFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@SpringBootTest
@Transactional
public class CongressmanServiceIntegrationTest {

    @Autowired
    CongressmanService congressmanService;

    @MockitoSpyBean(name = "congressmanRepository")
    CongressmanRepository congressmanRepository;


    @Test
    @DisplayName("syncCongressman 이 db에 없는 데이터는 insert, db와 다른 데이터는 update, db 에만 있는 데이터는 delete 한다.")
    void valid_syncCongressman_success() {
        // given
        // 삽입 : 최신에 있고 db에 없음
        Congressman son = CongressmanFixture.builder().setName("son").setCode("abc123").build();
        // 수정 : 최신, db에 있는데 상태가 다름
        Congressman leeDatabase = CongressmanFixture.builder().setName("lee lee").setCode("abc456").build();
        // 삭제 : db에 있고  최신에 없음
        Congressman kim = CongressmanFixture.builder().setName("kim").setCode("abc789").build();

        // db에 저장
        Congressman leeSaved = congressmanRepository.save(leeDatabase);
        congressmanRepository.save(kim);

        // lee 는 최신 데이터가 업데이트됨
        Congressman leeRecent = CongressmanFixture.builder().setId(leeSaved.getId()).setName("lee").setCode("abc456").build();
        List<Congressman> recent = List.of(son, leeRecent);

        // when
        CongressmanBatchResultDTO congressmanBatchResultDTO = congressmanService.syncCongressman(recent);

        // then

        Congressman leeUpdated = congressmanRepository.findById(leeSaved.getId()).get();
        assertThat(leeUpdated.getName()).isEqualTo(leeRecent.getName());

        assertAll(
                () -> assertThat(congressmanBatchResultDTO.getBatchRemoveResultCount()).isEqualTo(1),
                () -> assertThat(congressmanBatchResultDTO.getBatchInsertResult().stream().map(CongressmanBatchResultDTO.CongressmanDTO::getCode))
                        .containsExactly(son.getCode()),
                () -> assertThat(leeUpdated.getName()).isEqualTo(leeRecent.getName())  // 수정해야할 국회의원이 제대로 업데이트 되었는지
        );
    }
}
