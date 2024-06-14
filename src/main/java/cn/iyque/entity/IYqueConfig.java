package cn.iyque.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "iyque_config")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IYqueConfig {
    //主键为id且自增
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String corpId;

    private String agentId;

    private String agentSecert;

    private String token;

    private String encodingAESKey;
}
