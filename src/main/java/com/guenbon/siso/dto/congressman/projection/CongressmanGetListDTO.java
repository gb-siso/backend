package com.guenbon.siso.dto.congressman.projection;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class CongressmanGetListDTO {
    private Long id;
    private String name;
    private Double rate;
    private String timesElected;
    private String party;

    @Override
    public String toString() {
        return "CongressmanGetListDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rate=" + rate +
                ", timesElected=" + timesElected +
                ", party='" + party + '\'' +
                '}';
    }
}
