package com.xuan.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuan.handle.GlobalExceptionHandler;
import com.xuan.service.ISkillService;
import com.xuan.service.ISocialMediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 验证图标长度与现有数据库字段一致，覆盖新增、修改以及可选图标。
 */
class ProfileIconValidationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private ISkillService skillService;
    private ISocialMediaService socialMediaService;
    private MockMvc mockMvc;

    /**
     * 使用真实 MVC 参数校验与异常处理器，隔离业务写入。
     */
    @BeforeEach
    void setUp() {
        skillService = mock(ISkillService.class);
        socialMediaService = mock(ISocialMediaService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new SkillController(skillService), new SocialMediaController(socialMediaService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    /**
     * 提供空图标、短链接及恰好达到字段上限的链接。
     */
    static Stream<Object[]> acceptedIcons() {
        return Stream.of("/admin/skill", "/admin/socialMedia")
                .flatMap(path -> Stream.of(HttpMethod.POST, HttpMethod.PUT)
                        .flatMap(method -> Stream.of(null, "", iconUrl(51), iconUrl(255))
                                .map(icon -> new Object[]{path, method, icon})));
    }

    /**
     * 覆盖技能、社交媒体的新增和修改接口。
     */
    static Stream<Object[]> endpoints() {
        return Stream.of("/admin/skill", "/admin/socialMedia")
                .flatMap(path -> Stream.of(HttpMethod.POST, HttpMethod.PUT)
                        .map(method -> new Object[]{path, method}));
    }

    /**
     * 校验合法图标原样交给业务层，避免静默截断。
     */
    @ParameterizedTest
    @MethodSource("acceptedIcons")
    void acceptsIconsWithinDatabaseLimitWithoutTruncation(String path, HttpMethod method, String icon) throws Exception {
        mockMvc.perform(request(method, path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(icon)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        if (path.endsWith("skill")) {
            if (method == HttpMethod.POST) {
                verify(skillService).addSkill(argThat(dto -> Objects.equals(icon, dto.getIcon())));
            } else {
                verify(skillService).updateSkill(argThat(dto -> Objects.equals(icon, dto.getIcon())));
            }
        } else if (method == HttpMethod.POST) {
            verify(socialMediaService).addSocialMedia(argThat(dto -> Objects.equals(icon, dto.getIcon())));
        } else {
            verify(socialMediaService).updateSocialMedia(argThat(dto -> Objects.equals(icon, dto.getIcon())));
        }
    }

    /**
     * 校验超限请求在数据库写入前返回具体的 HTTP 400 错误。
     */
    @ParameterizedTest
    @MethodSource("endpoints")
    void rejectsOversizedIconsBeforePersistenceWithUsefulMessage(String path, HttpMethod method) throws Exception {
        mockMvc.perform(request(method, path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(iconUrl(256))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg", containsString("图标链接不能超过255个字符")));

        verifyNoInteractions(skillService, socialMediaService);
    }

    /**
     * 构建具有有效名称的请求体，单独检验图标字段。
     */
    private String body(String icon) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", 1);
        body.put("name", "GitHub");
        body.put("icon", icon);
        return objectMapper.writeValueAsString(body);
    }

    /**
     * 构造指定字符数的图标 URL，查询参数也计入长度。
     */
    private static String iconUrl(int length) {
        String prefix = "https://api.iconify.design/feather/github.svg?x=";
        return prefix + "x".repeat(length - prefix.length());
    }

}
