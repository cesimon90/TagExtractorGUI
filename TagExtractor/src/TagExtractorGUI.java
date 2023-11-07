import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class TagExtractorGUI {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private Set<String> stopWords;

    public TagExtractorGUI() {
        JFrame frame = new JFrame("Tag Extractor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JButton openButton = new JButton("Open File");
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        JButton extractButton = new JButton("Extract Tags");
        extractButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                extractTags();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);
        buttonPanel.add(extractButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        // Load stop words from a file into the stopWords set
        stopWords = loadStopWords("stopwords.txt");
    }

    private void openFile() {
        fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Display the name of the selected file
            textArea.append("File: " + selectedFile.getName() + "\n");
        }
    }

    private void extractTags() {
        if (fileChooser == null || stopWords == null) {
            return;
        }

        File selectedFile = fileChooser.getSelectedFile();
        Map<String, Integer> tagFrequencyMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into words, remove non-letter characters, and convert to lowercase
                String[] words = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");

                // Count the frequency of words, excluding stop words
                for (String word : words) {
                    if (!stopWords.contains(word) && !word.isEmpty()) {
                        tagFrequencyMap.put(word, tagFrequencyMap.getOrDefault(word, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Sort the map by frequency (descending order)
        java.util.List<Map.Entry<String, Integer>> sortedTags = new ArrayList<>(tagFrequencyMap.entrySet());
        sortedTags.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        // Display the extracted tags and their frequencies in the JTextArea
        textArea.setText(""); // Clear existing text
        for (Map.Entry<String, Integer> entry : sortedTags) {
            textArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
    }


    private Set<String> loadStopWords(String fileName) {
        Set<String> stopWordsSet = new TreeSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Convert each stop word to lowercase and add it to the set
                stopWordsSet.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWordsSet;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TagExtractorGUI();
            }
        });
    }
}
