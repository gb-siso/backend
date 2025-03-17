package com.guenbon.siso.entity;


import com.guenbon.siso.dto.congressman.CongressmanInfoDTO;
import com.guenbon.siso.entity.common.DateEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Congressman extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String party;

    @Column(nullable = false)
    private String timesElected;

    // 국회의원 코드
    @Column(nullable = false, unique = true)
    private String code;

    // 직책
    private String position;

    // 소속 정당
    private String electoralDistrict;

    // 선거구
    private String electoralType;

    // 선거구 구분
    @ElementCollection
    @Column(name = "assembly_sessions")
    @CollectionTable(name = "assembly_session", joinColumns = @JoinColumn(name = "congressman_id"))
    private List<Integer> assemblySessions;

    private String sex;

    private String imageUrl;


    @Override
    public String toString() {
        return "Congressman{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", party='" + party + '\'' +
                ", timesElected=" + timesElected +
                '}';
    }

    public static Congressman from(CongressmanInfoDTO dto) {
        return Congressman.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .party(dto.getParty())
                .position(dto.getPosition())
                .electoralDistrict(dto.getElectoralDistrict())
                .electoralType(dto.getElectoralType())
                .timesElected(dto.getTimesElected())
                .assemblySessions(dto.getAssemblySessions())
                .sex(dto.getSex())
                .imageUrl(dto.getImageUrl())
                .build();
    }
}
