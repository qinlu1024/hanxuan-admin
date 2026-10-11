package com.hanxuan.admin.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token 有效期信息 VO
 *
 * @author hanxuan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token 有效期信息")
public class TokenInfoVO {

    @Schema(description = "Token 值")
    private String token;

    @Schema(description = "Token 名称（请求头名）")
    private String tokenName;

    @Schema(description = "剩余绝对有效期（秒），自登录起最长保留时间，到期需重新登录；-1 表示永不过期")
    private Long tokenTimeout;

    @Schema(description = "剩余活跃有效期（秒），超过该时长无操作需重新登录；-1 表示不限制")
    private Long activeTimeout;
}
