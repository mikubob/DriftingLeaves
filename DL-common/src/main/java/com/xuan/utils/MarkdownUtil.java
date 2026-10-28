package com.xuan.utils;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/** Markdown 渲染及安全过滤，前后端共用相同的标题锚点规则。 */
public class MarkdownUtil {

    private static final java.util.List<org.commonmark.Extension> EXTENSIONS = java.util.List.of(
            org.commonmark.ext.gfm.tables.TablesExtension.create(),
            org.commonmark.ext.autolink.AutolinkExtension.create(),
            org.commonmark.ext.task.list.items.TaskListItemsExtension.create());
    private static final Parser parser = Parser.builder().extensions(EXTENSIONS).build();
    private static final HtmlRenderer renderer = HtmlRenderer.builder().extensions(EXTENSIONS).build();
    private static final Pattern HEADING_PUNCTUATION = Pattern.compile(
            "[^\\p{L}\\p{M}\\p{N}_\\s-]", Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern WHITESPACE = Pattern.compile("\\s", Pattern.UNICODE_CHARACTER_CLASS);

    /**
     * 渲染支持扩展的 Markdown，并安全过滤 HTML 和生成标题锚点。
     * @param markdown Markdown 源文本
     * @return 安全 HTML，空输入返回空字符串
     */
    public static String toHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        Node document = parser.parse(markdown);
        return sanitizeHtml(renderer.render(document));
    }

    /**
     * 识别以标签起始的内容，供文章保存选择清理或 Markdown 渲染。
     * @param content 原始正文
     * @return 去除首尾空格后是否以小于号开头
     */
    public static boolean isHtml(String content) {
        return content != null && content.trim().startsWith("<");
    }

    /**
     * 清理已有 HTML，保留受支持的格式、链接及标题锚点。
     * @param html 原始 HTML
     * @return 安全 HTML，空输入返回空字符串
     */
    public static String sanitize(String html) {
        if (html == null || html.trim().isEmpty()) {
            return "";
        }
        return sanitizeHtml(html);
    }

    /**
     * 按白名单清理正文，并为缺失 ID 的标题生成不重复的锚点。
     */
    private static String sanitizeHtml(String html) {
        Safelist safelist = Safelist.relaxed()
                .addTags("code", "pre", "hr", "input")
                .addAttributes("input", "checked")
                .addEnforcedAttribute("input", "type", "checkbox")
                .addEnforcedAttribute("input", "disabled", "disabled")
                .addAttributes("code", "class")
                .addAttributes("h1", "id").addAttributes("h2", "id")
                .addAttributes("h3", "id").addAttributes("h4", "id")
                .addAttributes("h5", "id").addAttributes("h6", "id")
                .addAttributes("a", "id", "name", "target", "rel")
                // jsoup requires an explicit # protocol for same-document links.
                .addProtocols("a", "href", "http", "https", "mailto", "#")
                .preserveRelativeLinks(true)
                .addEnforcedAttribute("a", "rel", "nofollow noopener noreferrer");

        Document document = Jsoup.parseBodyFragment(Jsoup.clean(html, safelist));
        Set<String> used = new HashSet<>();
        for (Element element : document.select("[id]")) {
            if (!element.id().isEmpty()) used.add(element.id());
        }
        for (Element heading : document.select("h1, h2, h3, h4, h5, h6")) {
            if (!heading.id().isEmpty()) continue;
            String base = headingSlug(heading.text());
            String id = base;
            int suffix = 0;
            while (!used.add(id)) id = base + "-" + ++suffix;
            heading.attr("id", id);
        }
        return document.body().html();
    }

    /**
     * 生成与前端规则一致的标题锚点基名，空标题使用 section。
     */
    private static String headingSlug(String text) {
        String slug = HEADING_PUNCTUATION.matcher(text.trim().toLowerCase(Locale.ROOT)).replaceAll("");
        slug = WHITESPACE.matcher(slug).replaceAll("-");
        return slug.isEmpty() ? "section" : slug;
    }
}
