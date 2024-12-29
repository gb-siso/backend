package com.guenbon.siso.dto.congressman.projection;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class CongressmanGetListDTO {
    private Long id;
    private String name;
    private Double rate;

    @Override
    public String toString() {
        return "CongressmanGetListDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rate=" + rate +
                '}';
    }
}
