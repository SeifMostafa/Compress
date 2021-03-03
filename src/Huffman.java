import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Huffman {

	PriorityQueue<HuffmanNode> huffmanTree = new PriorityQueue<HuffmanNode>();
	Map<Character, String> dictionary = new HashMap<>();

	private BufferedOutputStream out; // the output stream
	private int buffer; // 8-bit buffer of bits to write out
	private int n; // number of bits remaining in buffer

	private static TextArea codeView;

	public static void main(String[] args) {
		Huffman huffman = new Huffman();

		JFrame frame = new JFrame("HUFFMAN");
		frame.setVisible(true);
		frame.setSize(500, 250);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		frame.add(panel);
		JButton button = new JButton("compress");
		panel.add(button);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				huffman.compress();

			}
		});

		JButton button2 = new JButton("de-compress");
		panel.add(button2);
		button2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				huffman.decompress();

			}
		});

		codeView = new TextArea();

		panel.add(codeView);

	}

	private File compress() {
		File file = chooseFile();

		long executionTime;
		double compressionRatio;

		File encryptedFile = new File(file.getParent() + "/encrypted_" + file.getName());
		try {

			/// read file
			InputStream inputStream = new FileInputStream(file);
			String text = readFromInputStream(inputStream);

			System.out.println("org: " + text);
			long startTime = System.currentTimeMillis();
			// process ..
			// get frequency per char
			countChars(text);

			// generate Huffman tree
			while (huffmanTree.size() > 1) {
				HuffmanNode smallest1 = huffmanTree.poll();
				HuffmanNode smallest2 = huffmanTree.poll();
				HuffmanNode newBranchHeadNode = new HuffmanNode(null, smallest1.data + smallest2.data);
				newBranchHeadNode.left = smallest1;
				newBranchHeadNode.right = smallest2;

				huffmanTree.add(newBranchHeadNode);
			}

			/// generate codes
			generateCodes(huffmanTree.peek(), "");

			// form encryptedTest
			String encryptedTest = "";
			for (int i = 0; i < text.length(); i++) {
				encryptedTest += dictionary.get(text.charAt(i));
			}
			long endTime = System.currentTimeMillis();

			System.out.println("encryptedTest: " + encryptedTest);
			/// write encrypted file
			BinaryOut binaryOut = new BinaryOut(encryptedFile.getAbsolutePath());
			for (int i = 0; i < encryptedTest.length(); i++) {
				if (encryptedTest.charAt(i) == '1') {
					binaryOut.write(true);
				} else {
					binaryOut.write(false);
				}
			}
			binaryOut.flush();
			this.codeView.setText(dictionary.toString());
			// System.out.println(dictionary.toString());
			compressionRatio = (double) (text.length() * 8) / (encryptedTest.length());
			executionTime = endTime - startTime;
			JOptionPane.showMessageDialog(new JFrame(),
					"file is successfully compressed in same directory you chose file from with name: encrypted_"
							+ file.getName() + "\nCompression Ratio= " + compressionRatio + "\nExecution Time="
							+ executionTime
							+ " milliseconds\n ***Codes in Main program panel won't saved anywhere, closing program wipe codes! so You can compress & de-compress using same program instance please!");

		} catch (IOException e) {
			e.printStackTrace();
		}

		return encryptedFile;

	}

	public void decompress() {

		File encryptedFile = chooseFile();
		double executionTime = 0;
		try {
			File decryptedFile = new File(encryptedFile.getParent() + "/decrypted_" + encryptedFile.getName());
//		read file
			String textToDecompress = "";
			BinaryIn binaryIn = new BinaryIn(encryptedFile.getAbsolutePath());
			while (!binaryIn.isEmpty()) {
				if (binaryIn.readBoolean()) {
					textToDecompress += "1";
				} else {
					textToDecompress += "0";
				}
			}
			System.out.println("textToDecompress: " + textToDecompress);
			String originalText = "";
			String code = "";

			long startTime = System.currentTimeMillis();
			int j = 1;
			int start = 0;
			for (int i = 0; i < textToDecompress.length(); i++) {

				code = textToDecompress.substring(start, j);

				if (dictionary.containsValue(code)) {

					Iterator it = dictionary.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry) it.next();
						if (pair.getValue().equals(code)) {
							originalText += pair.getKey();
							code = "";
							start = i + 1;
						}

					}
				}
				j++;

			}
			originalText = originalText.substring(0, originalText.length() - 1); // remove last byte which was appended
			long endTime = System.currentTimeMillis();
			// by last flush read.
			System.out.println("originalText: " + originalText);

			Files.write(decryptedFile.toPath(), originalText.getBytes());
			executionTime = endTime - startTime;
			JOptionPane.showMessageDialog(new JFrame(),
					"file is successfully de-compressed in same directory you chose file from with name: decrypted_"
							+ encryptedFile.getName() + "\nExecution Time=" + executionTime + " milliseconds");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private File chooseFile() {
		JFileChooser jf = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
		jf.setFileFilter(filter);
		jf.showSaveDialog(null);
		return jf.getSelectedFile();
	}

	private void countChars(String fileContent) {
		Map<Character, Integer> charFreq = new HashMap<>();

		for (int i = 0; i < fileContent.length(); i++) {
			Integer currentValue = charFreq.putIfAbsent(new Character(fileContent.charAt(i)), 1);
			if (currentValue != null) {
				charFreq.replace(new Character(fileContent.charAt(i)), currentValue + 1);
			}
		}
		Iterator it = charFreq.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			huffmanTree.add(new HuffmanNode((Character) pair.getKey(), (Integer) pair.getValue()));
			it.remove();
		}
	}

	class HuffmanNode implements Comparable<HuffmanNode> {

		public HuffmanNode(Character c, int freq) {
			this.c = c;
			this.data = freq;
		}

		int data;
		Character c;

		HuffmanNode left;
		HuffmanNode right;

		@Override
		public boolean equals(Object obj) {
			return ((HuffmanNode) obj).c.equals(this.c);
		}

		@Override
		public int compareTo(Huffman.HuffmanNode o) {
			return this.data - o.data;
		}
	}

	public void generateCodes(HuffmanNode root, String s) {
		if (root.left == null && root.right == null && root.c != null) {
			this.dictionary.put(root.c, s);
			return;
		}
		generateCodes(root.left, s + "0");
		generateCodes(root.right, s + "1");
	}

	private String readFromInputStream(InputStream inputStream) throws IOException {
		StringBuilder resultStringBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				resultStringBuilder.append(line).append("\n");
			}
		}
		return resultStringBuilder.toString();
	}
}
