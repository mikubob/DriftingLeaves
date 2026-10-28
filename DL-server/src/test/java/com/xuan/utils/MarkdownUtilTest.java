package com.xuan.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownUtilTest {
    @Test
    void rendersChineseDirectoryAndInlineHeadingMarkup() {
        Document html = Jsoup.parseBodyFragment(MarkdownUtil.toHtml("""
                - [第2章 数据类型与表设计](#第2章-数据类型与表设计)
                  - [2.1 数值类型](#21-数值类型)
                  - [2.4 JSON 类型](#24-json-类型)

                ## 第2章 数据类型与表设计
                ### 2.1 **数值**类型
                ### 2.4 `JSON` 类型
                """));
        assertEquals(List.of("第2章-数据类型与表设计", "21-数值类型", "24-json-类型"),
                html.select("h2, h3").eachAttr("id"));
        for (var link : html.select("a")) {
            assertNotNull(html.getElementById(link.attr("href").substring(1)));
        }
    }

    @Test
    void generatesUniqueIdsForDuplicatesCollisionsAndAllHeadingLevels() {
        Document html = Jsoup.parseBodyFragment(MarkdownUtil.toHtml("""
                # A
                ## A
                ### A-1
                #### A
                ##### 中文_标题
                ###### !!!
                """));
        assertEquals(List.of("a", "a-1", "a-1-1", "a-2", "中文_标题", "section"),
                html.select("h1, h2, h3, h4, h5, h6").eachAttr("id"));
        assertEquals("a", Jsoup.parse(MarkdownUtil.toHtml("# A")).selectFirst("h1").id());
    }

    @Test
    void preservesEditorIdsCustomAnchorsAndEncodedFragments() {
        Document html = Jsoup.parseBodyFragment(MarkdownUtil.sanitize("""
                <a href="#%E4%B8%AD%E6%96%87">目录</a>
                <h2 id="中文">中文</h2>
                <a id="custom" name="legacy"></a><a href="#legacy">旧锚点</a>
                <h3>2.1 数值类型</h3>
                """));
        assertEquals("#%E4%B8%AD%E6%96%87", html.selectFirst("a[href]").attr("href"));
        assertNotNull(html.getElementById("中文"));
        assertNotNull(html.getElementById("custom"));
        assertNotNull(html.getElementById("21-数值类型"));
        assertEquals("legacy", html.selectFirst("a[name]").attr("name"));
        assertEquals(html.body().html(), MarkdownUtil.sanitize(html.body().html()));
    }

    @Test
    void retainsXssProtectionAndSafeExternalLinks() {
        String html = MarkdownUtil.sanitize("""
                <script>alert(1)</script><h2 onclick="alert(1)">标题</h2>
                <a href="javascript:alert(1)">bad</a><a href="data:text/html,bad">bad</a>
                <a href="https://example.com">external</a><img src="x" onerror="alert(1)">
                """);
        assertFalse(html.contains("<script"));
        assertFalse(html.contains("onclick"));
        assertFalse(html.contains("onerror"));
        assertFalse(html.contains("javascript:"));
        assertFalse(html.contains("data:text/html"));
        assertTrue(html.contains("https://example.com"));
    }
}
