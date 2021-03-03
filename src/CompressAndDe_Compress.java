
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class CompressAndDe_Compress {

	private JFrame frame;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CompressAndDe_Compress window = new CompressAndDe_Compress();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public CompressAndDe_Compress() {
		initialize();

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		final LZ77 lz77 = new LZ77();
		JButton Compress = new JButton("Compress");
		JButton extract = new JButton("extract");
		JButton ShowTags = new JButton("Show Tags");
		final JTextField Data = new JTextField();
		final JTextArea Area = new JTextArea();
		lz77.setString(Data.getText().toString());
		Compress.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lz77.setString(Data.getText().toString());
				lz77.process();
				JOptionPane.showMessageDialog(null, "Successful operation", "InfoBox: ",
						JOptionPane.INFORMATION_MESSAGE);
				Area.setText(lz77.show_tags());
			}

		});
		extract.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lz77.setString(Data.getText().toString());
				lz77.de_process();
				JOptionPane.showMessageDialog(null, "Successful operation", "InfoBox: ",
						JOptionPane.INFORMATION_MESSAGE);
				Area.setText(lz77.getString());
			}
		});
		Data.setSize(200, 150);
		Data.setText("Enter your data here:");
		JPanel panal = new JPanel();
		panal.add(Data);
		panal.add(Compress);
		panal.add(extract);
		panal.add(Area);
		frame = new JFrame();
		frame.add(panal);
		frame.setBounds(300, 300, 200, 200);
		frame.setTitle("Compress and Decompress");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
