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

    private static final Parser parser = Parser.builder().build();
    private static final HtmlRenderer renderer = HtmlRenderer.builder().build();
    private static final Pattern HEADING_PUNCTUATION = Pattern.compile(
            "[^\\p{L}\\p{M}\\p{N}_\\s-]", Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern WHITESPACE = Pattern.compile("\\s", Pattern.UNICODE_CHARACTER_CLASS);

    public static String toHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        Node document = parser.parse(markdown);
        return sanitizeHtml(renderer.render(document));
    }

    public static boolean isHtml(String content) {
        return content != null && content.trim().startsWith("<");
    }

    public static String sanitize(String html) {
        if (html == null || html.trim().isEmpty()) {
            return "";
        }
        return sanitizeHtml(html);
    }

    private static String sanitizeHtml(String html) {
        Safelist safelist = Safelist.relaxed()
                .addTags("code", "pre", "hr")
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

    private static String headingSlug(String text) {
        String slug = HEADING_PUNCTUATION.matcher(text.trim().toLowerCase(Locale.ROOT)).replaceAll("");
        slug = WHITESPACE.matcher(slug).replaceAll("-");
        return slug.isEmpty() ? "section" : slug;
    }
}
