import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.management.Query;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class StockApp extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private String db_location;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StockApp frame = new StockApp();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public StockApp() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnDB = new JButton("Open Database");
		
		btnDB.setBounds(6, 17, 144, 29);
		contentPane.add(btnDB);
		
		JLabel lblNewLabel = new JLabel("Stock:");
		lblNewLabel.setBounds(172, 22, 61, 16);
		contentPane.add(lblNewLabel);
		
		JComboBox comboBox = new JComboBox();
		
		comboBox.setBounds(220, 18, 134, 27);
		contentPane.add(comboBox);
		
		JButton btnUpd = new JButton("Update Selected");
		
		btnUpd.setBounds(6, 58, 144, 29);
		contentPane.add(btnUpd);
		
		JButton btnNewButton = new JButton("Update All");
		
		btnNewButton.setBounds(163, 58, 134, 29);
		contentPane.add(btnNewButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 105, 414, 145);
		contentPane.add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		btnDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				db_location = Sandbox.select_file_and_return_path(contentPane, "db", "SQLite DB");
				String sql_query = "Select stock from stocks";
				Sandbox.populate_JComboBox("jdbc:sqlite:"+db_location, sql_query, comboBox, 1);
			}
		});
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selection = comboBox.getSelectedItem().toString();
				String query = "Select * from stock_price where stock = '" + selection + "'";
				Sandbox.populate_JTable("jdbc:sqlite:" + db_location, query, table);
			}
		});
		btnUpd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selection = comboBox.getSelectedItem().toString();
				String wheretosave = Sandbox.select_folder_and_return_path(contentPane);
				Sandbox.download_URL("https://money.cnn.com/quote/quote.html?symb="+ selection, wheretosave + "/download/txt");
				String content = Sandbox.read_file_into_string(wheretosave + "/download.txt");
		
		/** REGEX price:
		 * streamFormat\s*=\s*\"ToHundredth\"\s*streamFeed\s*=\s*\"MorningstarQuote\"\>(.*?)\<
		 * REGEX previous close:
		 * \<td\?Previous\s*close\s*\<\/td\>\<td\s*class=\"wsod_quoteDataPoint\"\>(.*?)\<
		 * Today's Open:
		 * \<td\>Today\&rsquo\;s\s*open\<\/td\>\<td\s*class=\"wsod_quoteDataPoint\"\>(.*?)\< */
		
				String price = "streamFormat\\s*=\\s*\\\"ToHundredth\\\"\\s*streamFeed\\s*=\\s*\\\"MorningstarQuote\\\"\\>(.*?)\\<";
				String close = "\\<td\\?Previous\\s*close\\s*\\<\\/td\\>\\<td\\s*class=\\\"wsod_quoteDataPoint\\\"\\>(.*?)\\<";
				String open = "\\<td\\>Today\\&rsquo\\;s\\s*open\\<\\/td\\>\\<td\\s*class=\\\"wsod_quoteDataPoint\\\"\\>(.*?)\\<";
		
				String [] priceArr = Sandbox.easy_regex(price, content, false);
				String [] closeArr = Sandbox.easy_regex(close, content, false);
				String [] openArr = Sandbox.easy_regex(open, content, false);
				System.out.print(priceArr[0] + " " + closeArr[0] + " " + openArr[0]);
				String query = "Insert into stock_price (stock, previousclose, currentopen. currentprice, datetime) " +
						"values ('" + selection + "'," + priceArr[0] + "," + closeArr[0] + "," + openArr[0] + ", strftime('%s','now'))";
				Sandbox.easy_sqlite_modify("jdbc:sqlite:" + db_location, query);
			}
		});
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String query1 = "select stock from stocks";
				String[] stocks = Sandbox.easy_sqlite_1D_array("jdbc:sqlite:" + db_location, query1, 1);
				String wheretosave = Sandbox.select_folder_and_return_path(contentPane);
				
				for(int i = 0; i < stocks.length; i++)
				{
					String selection = stocks[i];
					Sandbox.download_URL("https://money.cnn.com/quote/quote.html?symb="+ selection, wheretosave + "/download/txt");
					String content = Sandbox.read_file_into_string(wheretosave + "/download.txt");
			
			/** REGEX price:
			 * streamFormat\s*=\s*\"ToHundredth\"\s*streamFeed\s*=\s*\"MorningstarQuote\"\>(.*?)\<
			 * REGEX previous close:
			 * \<td\?Previous\s*close\s*\<\/td\>\<td\s*class=\"wsod_quoteDataPoint\"\>(.*?)\<
			 * Today's Open:
			 * \<td\>Today\&rsquo\;s\s*open\<\/td\>\<td\s*class=\"wsod_quoteDataPoint\"\>(.*?)\< */
			
					String price = "streamFormat\\s*=\\s*\\\"ToHundredth\\\"\\s*streamFeed\\s*=\\s*\\\"MorningstarQuote\\\"\\>(.*?)\\<";
					String close = "\\<td\\?Previous\\s*close\\s*\\<\\/td\\>\\<td\\s*class=\\\"wsod_quoteDataPoint\\\"\\>(.*?)\\<";
					String open = "\\<td\\>Today\\&rsquo\\;s\\s*open\\<\\/td\\>\\<td\\s*class=\\\"wsod_quoteDataPoint\\\"\\>(.*?)\\<";
			
					String [] priceArr = Sandbox.easy_regex(price, content, false);
					String [] closeArr = Sandbox.easy_regex(close, content, false);
					String [] openArr = Sandbox.easy_regex(open, content, false);
					System.out.print(priceArr[0] + " " + closeArr[0] + " " + openArr[0]);
					String query = "Insert into stock_price (stock, previousclose, currentopen. currentprice, datetime) " +
							"values ('" + selection + "'," + priceArr[0] + "," + closeArr[0] + "," + openArr[0] + ", strftime('%s','now'))";
					Sandbox.easy_sqlite_modify("jdbc:sqlite:" + db_location, query);
				}
				
			}
		});
	}
}
