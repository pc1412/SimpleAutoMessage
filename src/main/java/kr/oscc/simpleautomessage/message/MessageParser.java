package kr.oscc.simpleautomessage.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public final class MessageParser {
    private MessageParser() {
    }

    public static Component parseLegacy(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }

        String text = input.replace("\\n", "\n");
        TextComponent.Builder root = Component.text();
        TextComponent.Builder current = Component.text();
        TextColor color = null;
        boolean bold = false;
        boolean italic = false;
        boolean underlined = false;
        boolean strikethrough = false;
        boolean obfuscated = false;

        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '&' && i + 1 < text.length()) {
                if (i + 7 < text.length() && text.charAt(i + 1) == '#') {
                    String hex = text.substring(i + 2, i + 8);
                    if (hex.matches("[0-9A-Fa-f]{6}")) {
                        appendStyled(root, buffer, color, bold, italic, underlined, strikethrough, obfuscated);
                        color = TextColor.fromHexString("#" + hex);
                        i += 7;
                        continue;
                    }
                }

                char code = Character.toLowerCase(text.charAt(i + 1));
                TextColor legacyColor = legacyColor(code);
                if (legacyColor != null) {
                    appendStyled(root, buffer, color, bold, italic, underlined, strikethrough, obfuscated);
                    color = legacyColor;
                    bold = italic = underlined = strikethrough = obfuscated = false;
                    i++;
                    continue;
                }

                switch (code) {
                    case 'l' -> { appendStyled(root, buffer, color, bold, italic, underlined, strikethrough, obfuscated); bold = true; i++; continue; }
                    case 'o' -> { appendStyled(root, buffer, color, bold, italic, underlined, strikethrough, obfuscated); italic = true; i++; continue; }
                    case 'n' -> { appendStyled(root, buffer, color, bold, italic, underlined, strikethrough, obfuscated); underlined = true; i++; continue; }
                    case 'm' -> { appendStyled(root, buffer, color, bold, italic, underlined, strikethrough, obfuscated); strikethrough = true; i++; continue; }
                    case 'k' -> { appendStyled(root, buffer, color, bold, italic, underlined, strikethrough, obfuscated); obfuscated = true; i++; continue; }
                    case 'r' -> {
                        appendStyled(root, buffer, color, bold, italic, underlined, strikethrough, obfuscated);
                        color = null;
                        bold = italic = underlined = strikethrough = obfuscated = false;
                        i++;
                        continue;
                    }
                    default -> { }
                }
            }
            buffer.append(c);
        }
        appendStyled(root, buffer, color, bold, italic, underlined, strikethrough, obfuscated);
        return root.build();
    }

    public static Component parseJson(String json) {
        return GsonComponentSerializer.gson().deserialize(json);
    }

    private static void appendStyled(TextComponent.Builder root, StringBuilder buffer, TextColor color,
                                     boolean bold, boolean italic, boolean underlined,
                                     boolean strikethrough, boolean obfuscated) {
        if (buffer.length() == 0) {
            return;
        }
        Component part = Component.text(buffer.toString());
        if (color != null) part = part.color(color);
        if (bold) part = part.decorate(TextDecoration.BOLD);
        if (italic) part = part.decorate(TextDecoration.ITALIC);
        if (underlined) part = part.decorate(TextDecoration.UNDERLINED);
        if (strikethrough) part = part.decorate(TextDecoration.STRIKETHROUGH);
        if (obfuscated) part = part.decorate(TextDecoration.OBFUSCATED);
        root.append(part);
        buffer.setLength(0);
    }

    private static TextColor legacyColor(char code) {
        return switch (code) {
            case '0' -> NamedTextColor.BLACK;
            case '1' -> NamedTextColor.DARK_BLUE;
            case '2' -> NamedTextColor.DARK_GREEN;
            case '3' -> NamedTextColor.DARK_AQUA;
            case '4' -> NamedTextColor.DARK_RED;
            case '5' -> NamedTextColor.DARK_PURPLE;
            case '6' -> NamedTextColor.GOLD;
            case '7' -> NamedTextColor.GRAY;
            case '8' -> NamedTextColor.DARK_GRAY;
            case '9' -> NamedTextColor.BLUE;
            case 'a' -> NamedTextColor.GREEN;
            case 'b' -> NamedTextColor.AQUA;
            case 'c' -> NamedTextColor.RED;
            case 'd' -> NamedTextColor.LIGHT_PURPLE;
            case 'e' -> NamedTextColor.YELLOW;
            case 'f' -> NamedTextColor.WHITE;
            default -> null;
        };
    }
}
