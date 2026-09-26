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
    private static final Path MENU_DIR = Path.of("src", "main", "java", "com", "fhfelipefh", "sandstorm", "content", "gui");

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

    private record SlotRect(int x1, int y1, int x2, int y2) {
        boolean intersects(SlotRect o) {
            return this.x1 < o.x2 && this.x2 > o.x1 && this.y1 < o.y2 && this.y2 > o.y1;
        }
    }

    private List<SlotRect> extractDirectSlots(String content) {
        List<SlotRect> slots = new ArrayList<>();
        Matcher m = Pattern.compile("addSlot\\s*\\([^;]+?,\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*\\)").matcher(content);
        while (m.find()) {
            int x = Integer.parseInt(m.group(1));
            int y = Integer.parseInt(m.group(2));
            if (x >= 0 && y >= 0) {
                slots.add(new SlotRect(x - 1, y - 1, x + 17, y + 17));
            }
        }
        return slots;
    }

    @Test
    void noSlotsMayOverlapWithinSameMenu() throws IOException {
        List<String> violations = new ArrayList<>();
        if (!Files.exists(MENU_DIR)) {
            return;
        }

        try (Stream<Path> paths = Files.list(MENU_DIR)) {
            paths.filter(p -> p.toString().endsWith("Menu.java")).forEach(path -> {
                String fileName = path.getFileName().toString();
                if (fileName.equals("MachineMenu.java") || fileName.equals("SandStormMenus.java") || fileName.equals("SandstoneWorkbenchMenu.java")) {
                    return;
                }
                try {
                    String content = Files.readString(path);
                    List<SlotRect> directSlots = extractDirectSlots(content);

                    for (int i = 0; i < directSlots.size(); i++) {
                        SlotRect s1 = directSlots.get(i);
                        for (int j = i + 1; j < directSlots.size(); j++) {
                            SlotRect s2 = directSlots.get(j);
                            if (s1.intersects(s2)) {
                                violations.add(fileName + ": Slot at (" + (s1.x1 + 1) + ", " + (s1.y1 + 1) +
                                        ") overlaps Slot at (" + (s2.x1 + 1) + ", " + (s2.y1 + 1) + ")");
                            }
                        }
                    }

                    Matcher invMatcher = Pattern.compile("playerInventory,\\s*col\\s*\\+\\s*row\\s*\\*\\s*9\\s*\\+\\s*9,\\s*\\d+\\s*\\+\\s*col\\s*\\*\\s*18,\\s*(\\d+)").matcher(content);
                    if (invMatcher.find()) {
                        int playerInvY = Integer.parseInt(invMatcher.group(1));
                        for (SlotRect slot : directSlots) {
                            if (slot.y2 > playerInvY) {
                                violations.add(fileName + ": Slot at y=" + (slot.y1 + 1) +
                                        " overlaps player inventory starting at y=" + playerInvY);
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(),
                "Found menus with overlapping slots:\n" + String.join("\n", violations));
    }

    @Test
    void baseMachineScreensMustNotHaveSlotsCollidingWithProgressBarOrWidgets() throws IOException {
        List<String> violations = new ArrayList<>();
        if (!Files.exists(GUI_DIR)) {
            return;
        }

        SlotRect defaultProgressBar = new SlotRect(69, 38, 107, 52);
        SlotRect defaultEnergyMeter = new SlotRect(7, 18, 25, 46);
        SlotRect defaultWptIndicator = new SlotRect(9, 5, 21, 15);

        Pattern menuTypePattern = Pattern.compile("extends\\s+BaseMachineScreen<([A-Za-z0-9_]+)>");

        try (Stream<Path> paths = Files.list(GUI_DIR)) {
            paths.filter(p -> p.toString().endsWith("Screen.java")).forEach(path -> {
                try {
                    String screenContent = Files.readString(path);
                    Matcher matcher = menuTypePattern.matcher(screenContent);
                    if (!matcher.find()) {
                        return;
                    }
                    String screenName = path.getFileName().toString();
                    String menuClassName = matcher.group(1);

                    Path menuPath = MENU_DIR.resolve(menuClassName + ".java");
                    if (!Files.exists(menuPath)) {
                        return;
                    }
                    String menuContent = Files.readString(menuPath);
                    List<SlotRect> directSlots = extractDirectSlots(menuContent);

                    boolean overridesProgressBar = screenContent.contains("void renderProgressBar(");
                    boolean overridesChassis = screenContent.contains("void renderChassis(");
                    boolean callsSuperChassis = screenContent.contains("super.renderChassis(");

                    boolean defaultChassisActive = !overridesChassis || callsSuperChassis;
                    boolean rendersDefaultProgressBar = defaultChassisActive && !overridesProgressBar;
                    boolean rendersDefaultChassis = defaultChassisActive;

                    for (SlotRect slot : directSlots) {
                        if (rendersDefaultProgressBar && slot.intersects(defaultProgressBar)) {
                            violations.add(screenName + " (" + menuClassName + "): Slot at (" +
                                    (slot.x1 + 1) + ", " + (slot.y1 + 1) + ") collides with default progress bar [69, 38, 107, 52]!");
                        }

                        if (rendersDefaultChassis && slot.intersects(defaultEnergyMeter)) {
                            violations.add(screenName + " (" + menuClassName + "): Slot at (" +
                                    (slot.x1 + 1) + ", " + (slot.y1 + 1) + ") collides with default energy meter [7, 18, 25, 46]!");
                        }

                        if (rendersDefaultChassis && slot.intersects(defaultWptIndicator)) {
                            violations.add(screenName + " (" + menuClassName + "): Slot at (" +
                                    (slot.x1 + 1) + ", " + (slot.y1 + 1) + ") collides with default WPT indicator [9, 5, 21, 15]!");
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(),
                "Found BaseMachineScreen implementations with slots colliding with widgets:\n" + String.join("\n", violations));
    }

    @Test
    void screensWithCustomChambersMustSuppressDefaultProgressBar() throws IOException {
        List<String> customChamberScreens = List.of(
                "Printer3DScreen.java",
                "HydroponicChamberScreen.java",
                "MolecularModifierScreen.java",
                "BioRegenerationPodScreen.java"
        );

        List<String> violations = new ArrayList<>();
        for (String screenFileName : customChamberScreens) {
            Path screenPath = GUI_DIR.resolve(screenFileName);
            if (!Files.exists(screenPath)) {
                continue;
            }
            String content = Files.readString(screenPath);
            if (!content.contains("void renderProgressBar(")) {
                violations.add(screenFileName + " has a custom visual chamber or monitor but does not override renderProgressBar to suppress the default progress bar at (70, 39)!");
            }
        }

        assertTrue(violations.isEmpty(),
                "Found custom chamber screens not suppressing default progress bar:\n" + String.join("\n", violations));
    }

    @Test
    void specializedScreenWidgetsMustNotOverlapMenuSlots() throws IOException {
        List<String> violations = new ArrayList<>();

        Path railgunScreenPath = GUI_DIR.resolve("KineticRailgunScreen.java");
        if (Files.exists(railgunScreenPath)) {
            String content = Files.readString(railgunScreenPath);
            if (content.contains("gw = 160")) {
                violations.add("KineticRailgunScreen: Energy gauge width gw=160 overlaps 3x3 ammo slots starting at x=62. Must be gw <= 48.");
            }
        }

        Path orbitalMenuPath = MENU_DIR.resolve("OrbitalGroundStationMenu.java");
        if (Files.exists(orbitalMenuPath)) {
            String content = Files.readString(orbitalMenuPath);
            for (SlotRect slot : extractDirectSlots(content)) {
                if (slot.y2 > 70) {
                    violations.add("OrbitalGroundStationMenu: Slot at y=" + (slot.y1 + 1) + " overlaps launch button starting at y=70!");
                }
            }
        }

        Path spireMenuPath = MENU_DIR.resolve("HoloTacticalSpireMenu.java");
        if (Files.exists(spireMenuPath)) {
            String content = Files.readString(spireMenuPath);
            for (SlotRect slot : extractDirectSlots(content)) {
                if (slot.x1 < 146) {
                    violations.add("HoloTacticalSpireMenu: Slot at x=" + (slot.x1 + 1) + " overlaps tactical order buttons ending at x=146!");
                }
            }
        }

        assertTrue(violations.isEmpty(),
                "Found specialized widgets overlapping menu slots:\n" + String.join("\n", violations));
    }

    private static int estimateMinecraftFontWidth(String text) {
        int width = 0;
        for (char c : text.toCharArray()) {
            if (c == 'i' || c == ':' || c == '.' || c == '|' || c == '!' || c == '\'') {
                width += 2;
            } else if (c == 'l' || c == 'I' || c == '[' || c == ']' || c == ' ') {
                width += 4;
            } else if (c == 'f' || c == 'k' || c == '\"' || c == '*') {
                width += 5;
            } else if (c == '@' || c == '~' || c == '%' || c == 'M' || c == 'W') {
                width += 8;
            } else {
                width += 6;
            }
        }
        return width;
    }

    private static final Pattern LITERAL_TEXT_AT_OFFSET = Pattern.compile(
            "extractor\\.text\\s*\\([^,]+,\\s*Component\\.literal\\s*\\(\\s*\"([^\"]+)\"\\s*\\)\\s*,\\s*(?:x\\s*\\+\\s*|this\\.leftPos\\s*\\+\\s*)?(\\d+)\\s*,");

    private static final Pattern HIGH_OFFSET_RAW_TEXT = Pattern.compile(
            "extractor\\.text\\s*\\([^,]+,\\s*(?:Component\\.literal\\s*\\([^)]+\\)|[A-Za-z0-9_]+)\\s*,\\s*(?:x\\s*\\+\\s*|this\\.leftPos\\s*\\+\\s*)(\\d+)\\s*,");

    private static final Pattern ADAPTIVE_TEXT_AT_OFFSET = Pattern.compile(
            "drawAdaptiveText\\s*\\([^,]+,\\s*[^,]+,\\s*(?:x\\s*\\+\\s*|this\\.leftPos\\s*\\+\\s*)?(\\d+)\\s*,\\s*[^,]+,\\s*(\\d+)\\s*,");

    @Test
    void noTextRenderingMayOverflowOrClipChassisBounds() throws IOException {
        List<String> violations = new ArrayList<>();
        if (!Files.exists(GUI_DIR)) {
            return;
        }

        try (Stream<Path> paths = Files.list(GUI_DIR)) {
            paths.filter(p -> p.toString().endsWith("Screen.java")).forEach(path -> {
                String fileName = path.getFileName().toString();
                if (fileName.equals("SurvivalDatapadScreen.java") || fileName.equals("AutonomousSonicTurretScreen.java") || fileName.equals("DatapadClientHelper.java")) {
                    return;
                }

                try {
                    List<String> lines = Files.readAllLines(path);
                    String content = String.join("\n", lines);

                    int chassisWidth = 176;
                    Matcher widthMatcher = Pattern.compile("CHASSIS_WIDTH\\s*=\\s*(\\d+)").matcher(content);
                    if (widthMatcher.find()) {
                        chassisWidth = Integer.parseInt(widthMatcher.group(1));
                    }

                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i);

                        Matcher literalMatcher = LITERAL_TEXT_AT_OFFSET.matcher(line);
                        if (literalMatcher.find()) {
                            String literal = literalMatcher.group(1);
                            int xOffset = Integer.parseInt(literalMatcher.group(2));
                            int textW = estimateMinecraftFontWidth(literal);
                            if (xOffset + textW > chassisWidth - 8) {
                                violations.add(fileName + ":" + (i + 1) + " -> Raw literal text \"" + literal +
                                        "\" rendered at x=" + xOffset + " has estimated width " + textW +
                                        "px, reaching x=" + (xOffset + textW) + " which overflows chassis width (" +
                                        chassisWidth + " - 8 = " + (chassisWidth - 8) + ")!");
                            }
                        } else {
                            Matcher highOffsetMatcher = HIGH_OFFSET_RAW_TEXT.matcher(line);
                            if (highOffsetMatcher.find()) {
                                int xOffset = Integer.parseInt(highOffsetMatcher.group(1));
                                if (xOffset >= 110 && !line.contains("(bw - ") && !line.contains("(w - ") && !line.contains("drawAdaptiveText")) {
                                    violations.add(fileName + ":" + (i + 1) + " -> Dynamic text rendered at high x=" + xOffset +
                                            " must use drawAdaptiveText() or centering to prevent clipping past chassis border!");
                                }
                            }
                        }

                        Matcher adaptiveMatcher = ADAPTIVE_TEXT_AT_OFFSET.matcher(line);
                        if (adaptiveMatcher.find()) {
                            int xOffset = Integer.parseInt(adaptiveMatcher.group(1));
                            int maxW = Integer.parseInt(adaptiveMatcher.group(2));
                            if (xOffset + maxW > chassisWidth - 6) {
                                violations.add(fileName + ":" + (i + 1) + " -> drawAdaptiveText at x=" + xOffset +
                                        " with maxPixelWidth=" + maxW + " reaches x=" + (xOffset + maxW) +
                                        " which exceeds chassis width " + chassisWidth + "!");
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(),
                "Found text rendering overflowing or clipping screen chassis bounds:\n" + String.join("\n", violations));
    }

    @Test
    void screenTitlesMustHaveMinimumPaddingFromRightChassisBorder() throws IOException {
        List<String> violations = new ArrayList<>();
        if (!Files.exists(GUI_DIR)) {
            return;
        }

        Pattern titleAdaptivePattern = Pattern.compile(
                "drawAdaptiveText\\s*\\(\\s*extractor\\s*,\\s*this\\.title\\s*,\\s*(?:this\\.titleLabelX|(\\d+))\\s*,\\s*[^,]+,\\s*([^,]+)\\s*,");

        try (Stream<Path> paths = Files.list(GUI_DIR)) {
            paths.filter(p -> p.toString().endsWith("Screen.java")).forEach(path -> {
                String fileName = path.getFileName().toString();
                if (fileName.equals("SurvivalDatapadScreen.java") || fileName.equals("AutonomousSonicTurretScreen.java") || fileName.equals("DatapadClientHelper.java")) {
                    return;
                }

                try {
                    String content = Files.readString(path);
                    if (!content.contains("extends AbstractContainerScreen<") && !content.contains("extends BaseMachineScreen<")) {
                        return;
                    }

                    int chassisWidth = 176;
                    Matcher widthMatcher = Pattern.compile("CHASSIS_WIDTH\\s*=\\s*(\\d+)").matcher(content);
                    if (widthMatcher.find()) {
                        chassisWidth = Integer.parseInt(widthMatcher.group(1));
                    }

                    int titleX = 8;
                    Matcher titleXMatcher = Pattern.compile("this\\.titleLabelX\\s*=\\s*(\\d+)").matcher(content);
                    if (titleXMatcher.find()) {
                        titleX = Integer.parseInt(titleXMatcher.group(1));
                    }

                    Matcher titleMatcher = titleAdaptivePattern.matcher(content);
                    if (titleMatcher.find()) {
                        String maxWExpr = titleMatcher.group(2).trim();
                        int maxW = -1;
                        if (maxWExpr.matches("\\d+")) {
                            maxW = Integer.parseInt(maxWExpr);
                        } else if (maxWExpr.contains("CHASSIS_WIDTH - ")) {
                            int sub = Integer.parseInt(maxWExpr.replace("CHASSIS_WIDTH - ", "").trim());
                            maxW = chassisWidth - sub;
                        } else if (maxWExpr.contains("imageWidth - ")) {
                            int sub = Integer.parseInt(maxWExpr.replaceAll(".*imageWidth\\s*-\\s*(?:this\\.titleLabelX\\s*-\\s*)?(\\d+).*", "$1").trim());
                            maxW = chassisWidth - titleX - sub;
                        }

                        if (maxW > 0 && titleX + maxW > chassisWidth - 6) {
                            violations.add(fileName + " -> Title rendering reaches x=" + (titleX + maxW) +
                                    " exceeding right chassis bound " + (chassisWidth - 6) + " (lacks right padding)!");
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        assertTrue(violations.isEmpty(),
                "Found screens where title rendering lacks right chassis padding:\n" + String.join("\n", violations));
    }
}
