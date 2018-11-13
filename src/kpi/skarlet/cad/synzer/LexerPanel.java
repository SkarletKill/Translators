package kpi.skarlet.cad.synzer;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LexerPanel extends JPanel {
    private JButton b_openFile;
    private JButton b_saveIntoFile;
    private JTextArea inputingText;

    private int hPadding = 10;
    private int vPadding = 10;
    private AbstractBorder border = createBorder(vPadding, hPadding, vPadding, hPadding);

    public LexerPanel() {
        setLayout(new BorderLayout());
        init();
        this.setBorder(border);
    }

    private void init() {
        b_openFile = new JButton("Open");
        b_openFile.addActionListener(createOpenFileListener());
        b_saveIntoFile = new JButton("Save in file");
        b_saveIntoFile.addActionListener(createSaveFileListener());
        inputingText = new JTextArea();
        inputingText.setTabSize(3);

        GridLayout gridLayout = new GridLayout(1, 2);
        gridLayout.setHgap(hPadding);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(gridLayout);
        buttonsPanel.setBorder(createBorder(0, 0, vPadding, 0));
        buttonsPanel.add(b_openFile);
        buttonsPanel.add(b_saveIntoFile);
//        b_openFile.addActionListener(l1);
        add(buttonsPanel, BorderLayout.NORTH);
        add(new JScrollPane(inputingText), BorderLayout.CENTER);
    }

    private CompoundBorder createBorder(int top, int left, int bottom, int right) {
        return new CompoundBorder(new EmptyBorder(top, left, bottom, right), new EmptyBorder(0, 0, 0, 0));
    }

    private static String readFile(File file, Charset encoding) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(file.getPath()));
            return new String(encoded, encoding);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "error";
    }

    private ActionListener createOpenFileListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser choosedFile = new JFileChooser("./");
//                int ret = choosedFile.showDialog(null, "Открыть файл");
                int ret = choosedFile.showOpenDialog(LexerPanel.this);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = choosedFile.getSelectedFile();
                    inputingText.setText(file.getName());
                    inputingText.setText(readFile(file, Charset.defaultCharset()));
                }
            }
        };
    }

    private ActionListener createSaveFileListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser choosedFile = new JFileChooser("./");
//                int ret = choosedFile.showDialog(null, "Открыть файл");
                int ret = choosedFile.showSaveDialog(LexerPanel.this);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = choosedFile.getSelectedFile();
                    try {
                        FileUtils.writeStringToFile(file, inputingText.getText());
                        JOptionPane.showMessageDialog(null, "Successfully saved");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Saving file error");
                    }
                }
            }
        };
    }
}
