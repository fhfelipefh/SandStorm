package com.fhfelipefh.sandstorm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GuiSymmetryAndTextClippingArchitectureTest {

    private static final Path GUI_DIR = Path.of("src", "client", "java", "com", "fhfelipefh", "sandstorm", "client", "gui");

    private static final Pattern EXTRACT_LABELS_OVERRIDE = Pattern.compile(
            "protected\\s+void\\s+extractLabels\\s*\\(");

    private static final Pattern ADAPTIVE_TEXT_METHOD = Pattern.compile(
            "(protected|private)\\s+void\\s+(drawAdaptiveText|drawScaledText)\\s*\\(");

    private static final Pattern RAW_TEXT_IN_EXTRACT_LABELS = Pattern.compile(
            "extractor\\.text\\s*\\(\\s*this\\.font\\s*,\\s*(this\\.title|this\\.playerInventoryTitle)");

    @Test
    void allContainerScreensMustOverrideExtractLabels() throws IOException {
        List<String> violations = new ArrayList<>();
        if (!Files.exists(GUI_DIR)) {
            return;
        }

        try (Stream<Path> paths = Files.list(GUI_DIR)) {
            paths.filter(p -> p.toString().endsWith("Screen.java")).forEach(path -> {
                try {
                    String content = Files.readString(path);
                    String fileName = path.getFileName().toString();

                    if (fileName.equals("SurvivalDatapadScreen.java")) {
                        return;
                    }

                    boolean extendsAbstractDirect = content.contains("extends AbstractContainerScreen<");
                    boolean extendsBaseMachine = content.contains("extends BaseMachineScreen<");

                    if (extendsAbstractDirect && !extendsBaseMachine) {
                        Matcher labelsMatcher = EXTRACT_LABELS_OVERRIDE.matcher(content);
                        if (!labelsMatcher.find()) {
                            violations.add(fileName + " extends AbstractContainerScreen directly but does not override extractLabels(). " +
                                    "Vanilla label rendering clips long translated titles. Override extractLabels and use drawAdaptiveText.");
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(),
                "Found Screen classes missing extractLabels override (risk of clipped titles):\n" + String.join("\n", violations));
    }

    @Test
    void extractLabelsMustUseAdaptiveTextNotRawText() throws IOException {
        List<String> violations = new ArrayList<>();
        if (!Files.exists(GUI_DIR)) {
            return;
        }

        try (Stream<Path> paths = Files.list(GUI_DIR)) {
            paths.filter(p -> p.toString().endsWith("Screen.java")).forEach(path -> {
                try {
                    List<String> lines = Files.readAllLines(path);
                    String fileName = path.getFileName().toString();

                    boolean insideExtractLabels = false;
                    int braceCount = 0;
                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i);

                        if (EXTRACT_LABELS_OVERRIDE.matcher(line).find()) {
                            insideExtractLabels = true;
                            braceCount = 0;
                        }

                        if (insideExtractLabels) {
                            for (char c : line.toCharArray()) {
                                if (c == '{') braceCount++;
                                if (c == '}') braceCount--;
                            }

                            if (RAW_TEXT_IN_EXTRACT_LABELS.matcher(line).find()) {
                                violations.add(fileName + ":" + (i + 1) +
                                        " -> Uses raw extractor.text() for title/inventoryTitle inside extractLabels. " +
                                        "Must use drawAdaptiveText() to prevent clipping on translated text.");
                            }

                            if (braceCount <= 0 && i > 0) {
                                insideExtractLabels = false;
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(),
                "Found raw extractor.text() calls for titles/labels inside extractLabels (risk of text clipping):\n" + String.join("\n", violations));
    }

    @Test
    void allNonBaseMachineScreensMustDefineTextScalingMethod() throws IOException {
        List<String> violations = new ArrayList<>();
        if (!Files.exists(GUI_DIR)) {
            return;
        }

        try (Stream<Path> paths = Files.list(GUI_DIR)) {
            paths.filter(p -> p.toString().endsWith("Screen.java")).forEach(path -> {
                try {
                    String content = Files.readString(path);
                    String fileName = path.getFileName().toString();

                    if (fileName.equals("SurvivalDatapadScreen.java") || fileName.equals("DatapadClientHelper.java")) {
                        return;
                    }

                    boolean extendsAbstractDirect = content.contains("extends AbstractContainerScreen<");
                    boolean extendsBaseMachine = content.contains("extends BaseMachineScreen<");

                    if (extendsAbstractDirect && !extendsBaseMachine) {
                        boolean hasLabelsOverride = EXTRACT_LABELS_OVERRIDE.matcher(content).find();
                        boolean hasAdaptiveMethod = ADAPTIVE_TEXT_METHOD.matcher(content).find();

                        if (hasLabelsOverride && !hasAdaptiveMethod) {
                            boolean labelsBodyEmpty = content.contains("extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {\n    }");
                            if (!labelsBodyEmpty) {
                                violations.add(fileName + " overrides extractLabels with content but lacks a text-scaling method " +
                                        "(drawAdaptiveText or drawScaledText). All screens with custom label rendering must define " +
                                        "a text-scaling method for clipping prevention.");
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(),
                "Found screens overriding extractLabels without a text-scaling method:\n" + String.join("\n", violations));
    }

    @Test
    void chassisBordersMustBeSymmetric() throws IOException {
        List<String> violations = new ArrayList<>();
        if (!Files.exists(GUI_DIR)) {
            return;
        }

        try (Stream<Path> paths = Files.list(GUI_DIR)) {
            paths.filter(p -> p.toString().endsWith("Screen.java")).forEach(path -> {
                try {
                    String content = Files.readString(path);
                    String fileName = path.getFileName().toString();

                    if (fileName.equals("SurvivalDatapadScreen.java") || fileName.equals("DatapadClientHelper.java")) {
                        return;
                    }

                    boolean hasRenderChassis = content.contains("private void renderChassis(");
                    if (!hasRenderChassis) {
                        return;
                    }

                    List<String> lines = Files.readAllLines(path);
                    boolean insideChassis = false;
                    int braceCount = 0;
                    int borderCount = 0;
                    boolean hasCornerTopLeft = false;
                    boolean hasCornerTopRight = false;
                    boolean hasCornerBottomLeft = false;
                    boolean hasCornerBottomRight = false;

                    for (String line : lines) {
                        if (line.contains("private void renderChassis(")) {
                            insideChassis = true;
                            braceCount = 0;
                        }

                        if (insideChassis) {
                            for (char c : line.toCharArray()) {
                                if (c == '{') braceCount++;
                                if (c == '}') braceCount--;
                            }

                            if (line.contains("0xFF00E5FF") && line.contains("extractor.fill(")) {
                                borderCount++;
                            }

                            if (line.contains("y + 1, x") && line.contains("y + 4")) {
                                hasCornerTopLeft = true;
                            }
                            if (line.contains("- 4, y + 1") && line.contains("- 1, y + 4")) {
                                hasCornerTopRight = true;
                            }
                            if (line.contains("Height - 4") && line.contains("Height - 1") && line.contains("x + 1,")) {
                                hasCornerBottomLeft = true;
                            }
                            if (line.contains("Height - 4") && line.contains("Height - 1") && !line.contains("x + 1,") && line.contains("- 4,") && line.contains("- 1,")) {
                                hasCornerBottomRight = true;
                            }

                            if (braceCount <= 0 && insideChassis) {
                                insideChassis = false;
                            }
                        }
                    }

                    if (borderCount >= 4) {
                        if (hasCornerTopLeft && !hasCornerTopRight) {
                            violations.add(fileName + " has top-left corner accent but not top-right (asymmetric frame).");
                        }
                        if (hasCornerBottomLeft && !hasCornerBottomRight) {
                            violations.add(fileName + " has bottom-left corner accent but not bottom-right (asymmetric frame).");
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(),
                "Found asymmetric chassis border rendering:\n" + String.join("\n", violations));
    }

    @Test
    void tooltipLiteralStringsMustNotExceedSafeLength() throws IOException {
        int maxSafeLength = 50;
        List<String> violations = new ArrayList<>();

        Pattern tooltipLiteralPattern = Pattern.compile(
                "(setTooltipForNextFrame|setComponentTooltipForNextFrame)\\s*\\(");
        Pattern stringLiteralPattern = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"");
        Pattern formatCodePattern = Pattern.compile("§.");

        if (!Files.exists(GUI_DIR)) {
            return;
        }

        try (Stream<Path> paths = Files.list(GUI_DIR)) {
            paths.filter(p -> p.toString().endsWith("Screen.java")).forEach(path -> {
                try {
                    List<String> lines = Files.readAllLines(path);
                    String fileName = path.getFileName().toString();

                    boolean insideTooltip = false;
                    int parenDepth = 0;

                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i);

                        if (tooltipLiteralPattern.matcher(line).find()) {
                            insideTooltip = true;
                            parenDepth = 0;
                        }

                        if (insideTooltip) {
                            for (char c : line.toCharArray()) {
                                if (c == '(') parenDepth++;
                                if (c == ')') parenDepth--;
                            }

                            Matcher litMatcher = stringLiteralPattern.matcher(line);
                            while (litMatcher.find()) {
                                String literal = litMatcher.group();
                                if (literal.startsWith("\"gui.") || literal.startsWith("\"tooltip.") || literal.startsWith("\"megastructure.")) {
                                    continue;
                                }
                                String inner = literal.substring(1, literal.length() - 1);
                                String clean = formatCodePattern.matcher(inner).replaceAll("");
                                if (clean.length() > maxSafeLength) {
                                    violations.add(fileName + ":" + (i + 1) +
                                            " -> Tooltip literal exceeds " + maxSafeLength + " chars: \"" + clean +
                                            "\" (length=" + clean.length() + "). Split into List<Component>.");
                                }
                            }

                            if (parenDepth <= 0) {
                                insideTooltip = false;
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(),
                "Found tooltip literals exceeding safe length (" + maxSafeLength + " chars):\n" + String.join("\n", violations));
    }

    @Test
    void allContainerScreensMustRenderSlotFrames() throws IOException {
        List<String> violations = new ArrayList<>();
        if (!Files.exists(GUI_DIR)) {
            return;
        }

        try (Stream<Path> paths = Files.list(GUI_DIR)) {
            paths.filter(p -> p.toString().endsWith("Screen.java")).forEach(path -> {
                try {
                    String content = Files.readString(path);
                    String fileName = path.getFileName().toString();

                    if (fileName.equals("SurvivalDatapadScreen.java") || fileName.equals("DatapadClientHelper.java")) {
                        return;
                    }

                    boolean isContainerScreen = content.contains("extends AbstractContainerScreen<") || content.contains("extends BaseMachineScreen<");
                    if (!isContainerScreen) {
                        return;
                    }

                    boolean inheritsBaseMachine = content.contains("extends BaseMachineScreen<");
                    if (inheritsBaseMachine) {
                        return;
                    }

                    boolean hasSlotIteration = content.contains("for (Slot slot : this.menu.slots)") || content.contains("for (Slot slot :");
                    boolean hasSlotFrameMethod = content.contains("renderSlotFrame(");
                    boolean hasManualSlotRendering = content.contains("sx + 17, sy + 17") || content.contains("sx + 16, sy + 16");

                    boolean isFullscreen = content.contains("this.leftPos = 0") && content.contains("this.topPos = 0");

                    if (!hasSlotIteration && !hasSlotFrameMethod && !hasManualSlotRendering && !isFullscreen) {
                        violations.add(fileName + " extends AbstractContainerScreen but does not render slot frames. " +
                                "All custom-rendered GUIs must render slot borders for visual consistency.");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(),
                "Found screens missing slot frame rendering:\n" + String.join("\n", violations));
    }
}
