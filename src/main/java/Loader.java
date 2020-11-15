import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.io.*;
import java.util.*;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

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
            JFrame frame = new JFrame("Map Analyser");

            JFileChooser jfc = new JFileChooser();
            jfc.setBackground(BACKGROUND);
            jfc.setForeground(FOREGROUND);
            jfc.setApproveButtonText("Analyse Craft");

            jfc.addChoosableFileFilter(new FileNameExtensionFilter("Map Files","xml"));
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

                    SAXBuilder saxBuilder = new SAXBuilder();
                    try {
                        Document document = saxBuilder.build(file);
                        Element units = (Element) document.getRootElement().getContent(1);
                        units = (Element) units.getContent(1);
                        units = (Element) units.getContent(1);

                        for (Content item: units.getContent()) {
                            if (item instanceof Element) {
                                if (((Element) item).getAttribute("value") != null && ((Element) item).getAttribute("value").toString().length() > 30) {
                                    System.out.println(((Element) item).getAttribute("value").toString());
                                }
                            }
                        }





                        //Finalization
                        XMLOutputter xmlOutput = new XMLOutputter();
                        xmlOutput.setFormat(Format.getPrettyFormat());
                        xmlOutput.output(document,  new FileOutputStream("Test.xml"));

                    } catch (JDOMException | IOException e) {
                        e.printStackTrace();
                    }

                    Object[] options = {"Show Details", "Close"};

                    /**
                    if (JOptionPane.showOptionDialog(frame, newCraft, "Analyzer",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]) == 0) {
                        JOptionPane.showMessageDialog(frame, newCraft.toPartString());
                    }**/
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

    public static void setPath(File path) {
        Loader.path = path;
    }
}
