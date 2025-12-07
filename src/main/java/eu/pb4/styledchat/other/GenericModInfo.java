package eu.pb4.styledchat.other;

import net.fabricmc.loader.api.ModContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import javax.imageio.ImageIO;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GenericModInfo {
    private static Component[] icon = new Component[0];
    private static Component[] about = new Component[0];
    private static Component[] consoleAbout = new Component[0];

    public static void build(ModContainer container) {
        boolean useIcon = true;

        {
            final String chr = "█";
            var icon = new ArrayList<MutableComponent>();
            try {
                var source = ImageIO.read(Files.newInputStream(container.getPath("assets/icon_small.png")));

                for (int y = 0; y < source.getHeight(); y++) {
                    var base = Component.literal("");
                    int line = 0;
                    int color = source.getRGB(0, y) & 0xFFFFFF;
                    for (int x = 0; x < source.getWidth(); x++) {
                        int colorPixel = source.getRGB(x, y) & 0xFFFFFF;

                        if (color == colorPixel) {
                            line++;
                        } else {
                            base.append(Component.literal(chr.repeat(line)).setStyle(Style.EMPTY.withColor(color).withShadowColor(color | 0xFF000000)));
                            color = colorPixel;
                            line = 1;
                        }
                    }

                    base.append(Component.literal(chr.repeat(line)).setStyle(Style.EMPTY.withColor(color).withShadowColor(color | 0xFF000000)));
                    icon.add(base);
                }
            } catch (Throwable e) {
                useIcon = false;
                e.printStackTrace();
                while (icon.size() < 16) {
                    icon.add(Component.literal("/!\\ [ Invalid icon file ] /!\\").setStyle(Style.EMPTY.withColor(0xFF0000).withItalic(true)));
                }
            }

            GenericModInfo.icon = icon.toArray(new Component[0]);
        }

        {
            var about = new ArrayList<Component>();
            var aboutBasic = new ArrayList<Component>();
            var output = new ArrayList<Component>();

            try {
                about.add(Component.literal(container.getMetadata().getName()).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW).withBold(true).withClickEvent(new ClickEvent.OpenUrl(URI.create(container.getMetadata().getContact().get("github").orElse("https://pb4.eu"))))));
                about.add(Component.translatable("Version: ").setStyle(Style.EMPTY.withColor(0xf7e1a7))
                        .append(Component.literal(container.getMetadata().getVersion().getFriendlyString()).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE))));

                aboutBasic.addAll(about);
                aboutBasic.add(Component.empty());
                aboutBasic.add(Component.nullToEmpty(container.getMetadata().getDescription()));

                var contributors = new ArrayList<String>();
                contributors.addAll(container.getMetadata().getAuthors().stream().map((p) -> p.getName()).collect(Collectors.toList()));
                contributors.addAll(container.getMetadata().getContributors().stream().map((p) -> p.getName()).collect(Collectors.toList()));

                about.add(Component.literal("")
                        .append(Component.translatable("Contributors")
                                .setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)
                                        .withHoverEvent(new HoverEvent.ShowText(
                                                Component.literal(String.join(", ", contributors)
                                        ))
                                )))
                        .append("")
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)));
                about.add(Component.empty());

                var desc = new ArrayList<>(List.of(container.getMetadata().getDescription().split(" ")));

                if (desc.size() > 0) {
                    StringBuilder descPart = new StringBuilder();
                    while (!desc.isEmpty()) {
                        (descPart.isEmpty() ? descPart : descPart.append(" ")).append(desc.remove(0));

                        if (descPart.length() > 16) {
                            about.add(Component.literal(descPart.toString()).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                            descPart = new StringBuilder();
                        }
                    }

                    if (descPart.length() > 0) {
                        about.add(Component.literal(descPart.toString()).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                    }
                }

                if (icon.length > about.size() + 2 && useIcon) {
                    int a = 0;
                    for (int i = 0; i < icon.length; i++) {
                        if (i == (icon.length - about.size() - 1) / 2 + a && a < about.size()) {
                            output.add(icon[i].copy().append(Component.literal("  ").setStyle(Style.EMPTY.withItalic(false)).append(about.get(a++))));
                        } else {
                            output.add(icon[i]);
                        }
                    }
                } else {
                    Collections.addAll(output, icon);
                    output.addAll(about);
                }
            } catch (Exception e) {
                e.printStackTrace();
                var invalid = Component.literal("/!\\ [ Invalid about mod info ] /!\\").setStyle(Style.EMPTY.withColor(0xFF0000).withItalic(true));

                output.add(invalid);
                about.add(invalid);
            }

            GenericModInfo.about = output.toArray(new Component[0]);
            GenericModInfo.consoleAbout = aboutBasic.toArray(new Component[0]);
        }
    }

    public static Component[] getIcon() {
        return icon;
    }

    public static Component[] getAboutFull() {
        return about;
    }

    public static Component[] getAboutConsole() {
        return consoleAbout;
    }
}
