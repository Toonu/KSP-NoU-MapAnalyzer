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
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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

                        TreeMap<String, ArrayList<Element>> objects = new TreeMap<>();

                        List<Content> content = units.getContent();
                        for (Content item : content) {
                            if (item instanceof Element) {
                                if (((Element) item).getAttribute("value") != null) {
                                    if (!objects.containsKey(((Element) item).getAttributeValue("parent"))) {
                                        ArrayList<Element> assets = new ArrayList<>();
                                        objects.put(((Element) item).getAttributeValue("parent"), assets);
                                    }
                                    objects.get(((Element) item).getAttributeValue("parent")).add((Element) item);

                                    //System.out.println(((Element) item).getAttribute("value").getValue());
                                }
                            }
                        }

                        //Filter out non-radar and non-national-layer elements.
                        TreeMap<String, ArrayList<Element>> assets = new TreeMap<>(objects);
                        assets.forEach((k, v) -> {
                            if (!k.startsWith("N") && !k.startsWith("Radar")) {
                                objects.remove(k);
                            }
                        });

                        TreeMap<String, TreeMap<String, Integer>> results = new TreeMap<>();

                        objects.forEach((k, v) -> {
                            if (k.endsWith("B")) {
                                v.forEach(asset -> {
                                    String[] items = asset.getAttributeValue("style").split(";");
                                    String name = asset.getAttributeValue("Value");
                                    if (name != null) {
                                        name = name.substring(0, 3).trim();
                                        if (results.get(name) != null) {
                                            results.put(name, new TreeMap<>());
                                        }
                                        for (String item: items) {
                                            if (item.startsWith("image=data")) {
                                                String data[] = null;
                                                if (item.equals("image=data:image/png,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")) {
                                                    results.get(name).putIfAbsent("Airport", 0);
                                                    results.get(name).put("Airport", results.get(name).get("Airport") + 1);
                                                } else if (item.equals("image=data:image/png,iVBORw0KGgoAAAANSUhEUgAAAGwAAABsCAYAAACPZlfNAAADn0lEQVR4Xu3d4ccUURzF8fP8kxERkYiIiIhKREREJCLiob+yXFq22ZnZe+69u2dnn+/yvHp+c++Z89nZnd03eyMem2rgZlNpCaslsLeSyh+PXAOzBnNgZfCNpHegxbQWDaZgu8FdUtDOb7ZqsA82HQQtj3VgML3CXkt6P5OTK+30eFUXzNx72CtJH0A7vdDeDlVYZX7pLvGlpI+gnQWtGmsNrPzvhaRPoJ0UzcI6Blb+/1zS55nIZaPyvsajvYHy0Wnus+7q/ULNNx3PJH0BrV1m5sgmrJorbLfXU0lfQRuC1ozlgJXZJ5K+gdaF1oXlgpX5x5K+g9aE1o3VAlaOeSTpB2gW2hCsVrBy3ENJP7nlr0Kzb93XVq25S1w6/oGkX1WRGZo20PxVXw9YCXFf0i0eVgPNWD0vifsJ70n6bUW+u8NdWKPAyjp/7q6Bdea9r2iLX/5aKWbAuoO5AS50fvpE7u6le4F/RQ0PdqEAbqzhvQDmEnjzgHl9xacBixN4AQDz+opPAxYn8AIA5vUVnwYsTuAFAMzrKz4NWJzACwCY11d8GrA4gRcAMK+v+DRgcQIvAGBeX/FpwOIEXgDAvL7i04DFCbwAgHl9xacBixN4AQDz+opPAxYn8AIA5vUVnwYsTuAFAMzrKz4NWJzACwCY11d8GrA4gRcAMK+v+DRgcQIvAGBeX/FpwOIEXgDAvL7i04DFCbwAgHl9xacBixN4AQDz+opPAxYn8AIA5vUVnwYsTuAFAMzrKz4NWJzACwCY11d8GrA4gRcAMK+v+DRgcQIvAGBeX/FpwOIEXgDAvL7i04DFCbwAgHl9xacBixN4AQDz+opPAxYn8AIA5vUVnwYsTuAFAMzrKz4NWJzACwCY11d8GrA4gRcAMK+v+DRgcQIvAGBeX/FpwOIEXgDAvL7i05sBizd1oQG6f6+tewFJS79TfKGdRWPFf7QULN+/C63nCgPLx9od0YzWCjb059rbz3sTRw7tqgVsaIBNVN4fclhnLtiwjfs72NwKQ7pzwIZsuLmaxwbu7rAWrHujsee96dW6uqwB69pg09WeLnxzp8fAmhc+3blezcpN3a6BNS14NXWe50TsjpfA7IXOc35XuYvV9RyYtcBVVnj+k6rufApWfeD5z+nqd6zqfh+s6oCrry17gkcNjl1hzV9SZs9707tP0f4zWHsPAyvnvkM7MFi7SywH8cg1UPo/MDj2wTkXl51nGwBsY0+Mv7M2BnygEDxQAAAAAElFTkSuQmCC")) {
                                                    results.get(name).putIfAbsent("FOB", 0);
                                                    results.get(name).put("FOB", results.get(name).get("FOB") + 1);
                                                } else if (item.equals("image=data:image/png,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")) {
                                                    results.get(name).putIfAbsent("PORT", 0);
                                                    results.get(name).put("PORT", results.get(name).get("PORT") + 1);
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });


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


/*
 Step one

 Analyze units per side, their types.
 Range icons to every unit.

 Step two

 Work on creating new units and replacing them with different icons.
 Spotting based on scanning all units and their relative coordinates to all units of one side, then adding the to the right layer.

 */