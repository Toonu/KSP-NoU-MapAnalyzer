import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.GraphicsDevice;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * @author Toonu NoU
 *
 * Class for simple analyse of .craft files for all parts.
 */
public class Loader {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final Color BACKGROUND = new Color(-16505734);
    public static final Color FOREGROUND = new Color(-14336);
    private static File path = null;

    /**
     * Main method.
     * @param args args.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Craft Analyser");

            JFileChooser jfc = new JFileChooser();
            jfc.setBackground(BACKGROUND);
            jfc.setForeground(FOREGROUND);
            jfc.setApproveButtonText("Analyse Craft");

            jfc.addChoosableFileFilter(new FileNameExtensionFilter("Craft Files","craft"));
            jfc.setAcceptAllFileFilterUsed(false);

            if (path == null) {
                jfc.setCurrentDirectory(new File((".")));
            } else {
                jfc.setCurrentDirectory(path);
            }

            try (BufferedReader br = new BufferedReader(new FileReader("config.cfg"))) {
                path = new File(br.readLine());
            } catch (IOException | NullPointerException e) {
                path = null;
            }

            jfc.addActionListener(e1 -> {
                if (e1.getActionCommand().equals("CancelSelection")) {
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                } else if (e1.getActionCommand().equals("ApproveSelection")) {
                    File file = jfc.getSelectedFile();
                    setPath(jfc.getSelectedFile());
                    try {
                        FileWriter fileWriter = new FileWriter("config.cfg");
                        fileWriter.write(String.valueOf(path));
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    AnalysedCraft newCraft = getParts(file);

                    Object[] options = {"Show Details", "Close"};

                    if (JOptionPane.showOptionDialog(frame, newCraft, "Analyzer",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]) == 0) {
                        JOptionPane.showMessageDialog(frame, newCraft.toPartString());
                    }
                }
            });

            //Monitor size
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            int width = gd.getDisplayMode().getWidth();
            int height = gd.getDisplayMode().getHeight();

            frame.setLocation((width / 2) - (WIDTH / 2), (height / 2) - (HEIGHT / 2));
            frame.setSize(WIDTH, HEIGHT);

            frame.add(jfc);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    /**
     * Method makes String of parts from .craft file.
     * @param file .craft file to analyse.
     * @return String to print.
     */
    private static AnalysedCraft getParts(File file) {
        ArrayList<String> parts = new ArrayList<>();
        int counter = 0;
        int missileCounter = 0;
        int gunCounter = 0;
        int hardpoints = 0;
        int avionics = 0;
        String craftName = null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int internalcounter = 0;
            String line = br.readLine();
            while (line != null) {
                if (Pattern.matches(".*part = .*", line)) {
                    line = line.trim();
                    line = line.substring(7, line.length()-11);
                    parts.add(line);
                    internalcounter += line.length();
                    if (internalcounter > 40) {
                        parts.add("\n");
                        internalcounter = 0;
                    }
                    ++counter;
                    if (Pattern.matches(".*bahaAdjustableRail.*", line)) {
                        hardpoints += 3;
                    }
                }

                if (Pattern.matches(".*name = MissileLauncher.*", line)) {
                    ++missileCounter;
                } else if (Pattern.matches(".*name = ModuleWeapon.*", line)) {
                    ++gunCounter;
                } else if (Pattern.matches(".*name = ModuleRadar.*", line) ||
                        Pattern.matches("name = CM.....", line) ||
                Pattern.matches(".*ModuleECMJammer.*", line) || Pattern.matches(".*ModuleTargetingCamera.*", line)) {
                    ++avionics;
                }
                if (Pattern.matches(".*ship = .*", line)) {
                    line = line.trim();
                    craftName = line.substring(7);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            parts.add(String.format("[ERR %s] Error initializing stream. Exception: %s",
                    LocalTime.now().truncatedTo(ChronoUnit.SECONDS), e));
        }
        return new AnalysedCraft(parts, counter, missileCounter, gunCounter, hardpoints, avionics, craftName);
    }

    public static File getPath() {
        return path;
    }

    public static void setPath(File path) {
        Loader.path = path;
    }
}
