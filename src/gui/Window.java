// Making Chat Window using Java Swing.
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import javax.print.attribute.standard.JobMessageFromOperator;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import javax.swing.undo.UndoManager;

public class Window
{
	JFrame mainFrame;
	JTextArea textArea;
	UndoManager undo;
	JMenuBar menuBar;
	JPopupMenu autocompleteSuggestions;
	JMenu autocompleteSuggestions2;
	JPopupMenu rightClickMenu;
	JList<String> list;
	JList<String> list2;
	JMenuItem undoMenuItem, redoMenuItem;
	String currentFile;

	JMenuItem newFile, open, save, saveAs, close;
	JMenu menu2;
	JCheckBoxMenuItem wordWrap;
	
	JPanel p;
	/**
	 * Constructor create all GUI components
	 */
	public Window()
	{
		/*
		 * Creating Main Frame of Osmos
		 */
		mainFrame = new JFrame("Osmos");
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.setMinimumSize(new Dimension(500, 500));
		mainFrame.addWindowListener(new java.awt.event.WindowAdapter()
		{
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent)
		    {
				if(currentFile!=null || textArea.getDocument().getLength()!=0)
				{
					int ret = showSavePopUp();
					if(ret == JOptionPane.YES_OPTION)
						save.doClick();
					else if(ret != JOptionPane.NO_OPTION)
						return;
				}	
				mainFrame.dispose();
		    }
		});
		/*
		 * Creating Text Area, setting properties, adding undo, scrollpane and listeners
		 */
		textArea = new JTextArea();
		textArea.setVisible(true);
		textArea.setEnabled(false);
		textArea.setEditable(false);
//		textArea.setBackground(Color.gray);
		textArea.setTabSize(3);
		textArea.setFont(new Font("Courier", Font.PLAIN, 13));
		textArea.addMouseListener(getTextAreaMouseListener());
		textArea.addKeyListener(getTextAreaKeyListener());
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		undo = new UndoManager();
		textArea.getDocument().addUndoableEditListener(undo);
		JScrollPane scrollPane = new JScrollPane(textArea);

		
		/*
		 * Creating File Menu and adding menuItems in the Application.
		 */
		JMenu menu1 = new JMenu("File");
		newFile = new JMenuItem(getNewAction());
		menu1.add(newFile);
		open = new JMenuItem(getOpenAction());
		menu1.add(open);
		save = new JMenuItem(getSaveAction());
		save.setEnabled(false);
		menu1.add(save);
		saveAs = new JMenuItem(getSaveAsAction());
		saveAs.setEnabled(false);
		menu1.add(saveAs);
		close = new JMenuItem(getCloseAction());
		close.setEnabled(false);
		menu1.add(close);

		/*
		 * Creating Edit Menu and adding menuItems
		 */
		menu2 = new JMenu("Edit");
		menu2.setEnabled(false);
		undoMenuItem = new JMenuItem(getUndoAction());
		menu2.add(undoMenuItem);
		redoMenuItem = new JMenuItem(getRedoAction());
		menu2.add(redoMenuItem);
		menu2.addSeparator();
		
		JMenuItem copy = new JMenuItem(getCopyAction());
		menu2.add(copy);
		JMenuItem cut = new JMenuItem(getCutAction());
		menu2.add(cut);
		JMenuItem paste = new JMenuItem(getPasteAction());
		menu2.add(paste);
		JMenuItem selectAll = new JMenuItem(getSelectAllAction());
		menu2.add(selectAll);
		menu2.addSeparator();
		
		JMenuItem findReplace = new JMenuItem(getFindAction());
		menu2.add(findReplace);
		/*
		 * Creating View Menu and adding MenuItems
		 */
		JMenuItem menu3 = new JMenu("View");
		wordWrap = new JCheckBoxMenuItem(getWordWrapAction());
		wordWrap.setSelected(true);
		menu3.add(wordWrap);
		JMenu theme = new JMenu("Theme");
		JRadioButtonMenuItem defaultTheme = new JRadioButtonMenuItem(new AbstractAction("Default")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				textArea.setBackground(Color.WHITE);
				textArea.setCaretColor(Color.BLACK);
				textArea.setForeground(Color.BLACK);
			}
		});
		defaultTheme.setSelected(true);
		JRadioButtonMenuItem darkTheme = new JRadioButtonMenuItem(new AbstractAction("Dark")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				textArea.setBackground(new Color(10, 10, 10));
				textArea.setCaretColor(Color.WHITE);
				textArea.setForeground(Color.CYAN);
			}
		});
		ButtonGroup grp = new ButtonGroup();
		grp.add(darkTheme);
		grp.add(defaultTheme);
		theme.add(defaultTheme);
		theme.add(darkTheme);
		menu3.add(theme);
		JMenuItem docStats = new JMenuItem(new AbstractAction("Document Statistics")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String text = textArea.getText();
				String message = "Character Count: " + text.length() + "\n";
				message += "Word Count: " + (text.length() == 0 ? 0:text.split("\\W+").length) + "\n";
				message += "Line Count: " + (text.length() == 0 ? 0:text.split("\n").length) + "\n";
				String title = "Document Statistics";
				JOptionPane.showMessageDialog(mainFrame, message, title, JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menu3.add(docStats);
		
		/*
		 * Adding menus to menu bar, and menubar to main frame
		 */
		menuBar = new JMenuBar();
		menuBar.add(menu1);
		menuBar.add(menu2);
		menuBar.add(menu3);
		menuBar.setVisible(true);
		mainFrame.add(menuBar, BorderLayout.PAGE_START);

		mainFrame.add(scrollPane, BorderLayout.CENTER);
		mainFrame.setVisible(true);
	}

	public KeyListener getTextAreaKeyListener()
	{
		return new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent arg0)
			{
				if (arg0.getKeyCode() == KeyEvent.VK_DOWN
						&& autocompleteSuggestions != null
						&& autocompleteSuggestions.isVisible())
				{
					arg0.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0)
			{
				if (arg0.getKeyCode() == KeyEvent.VK_DOWN
						&& autocompleteSuggestions != null
						&& autocompleteSuggestions.isVisible())
				{
					list.requestFocusInWindow();
					arg0.consume();
					return;
				}
				if (autocompleteSuggestions != null)
					autocompleteSuggestions.setVisible(false);
				if (!Character.isLetter(arg0.getKeyChar())
						&& arg0.getKeyCode() != KeyEvent.VK_BACK_SPACE)
					return;
				try
				{
					int start = Math.max(textArea.getCaretPosition() - 45, 0);
					String string = textArea.getDocument().getText(start,
							textArea.getCaretPosition() - start);
					if (string == null || string.length() == 0)
						return;
					int begIndex = string.length() - 1;

					while (Character.isLetter(string.charAt(begIndex))
							&& begIndex > 0)
						begIndex--;
					if (!Character.isLetter(string.charAt(begIndex)))
						begIndex++;
					if (begIndex >= string.length())
						return;
					String currentWord = string.substring(begIndex);
					ArrayList<String> sugg = GUI.D.getSuggestions(currentWord);
					if (sugg != null)
						showPopUp(sugg, currentWord);
				} catch (Exception e)
				{
				}
			}

			@Override
			public void keyPressed(KeyEvent arg0)
			{
				if (arg0.getKeyCode() == KeyEvent.VK_DOWN
						&& autocompleteSuggestions != null
						&& autocompleteSuggestions.isVisible())
				{
					arg0.consume();
				}

			}
		};
	}

	private void showRightClickMenu(MouseEvent arg0) throws BadLocationException
	{
		if (rightClickMenu == null)
			rightClickMenu = new JPopupMenu();
		rightClickMenu.removeAll();
		Point mousePosition = textArea.getMousePosition();
		int offset;
		if (mousePosition != null)
		{
			offset = textArea.viewToModel(textArea.getMousePosition());
			int rowStart = Utilities.getRowStart(textArea, offset);
			int rowEnd = Utilities.getRowEnd(textArea, offset);
			if (!textArea.modelToView(rowStart)
					.union(textArea.modelToView(rowEnd))
					.contains(mousePosition))
				return;
		} else
			offset = textArea.getCaretPosition();
		while (offset > 0
				&& Character.isLetter(textArea.getDocument()
						.getText(offset - 1, 1).charAt(0)))
			offset--;
		final Integer offs = offset;
		String word = "";
		char ch;
		while (offset < textArea.getDocument().getLength()
				&& Character.isLetter(ch = textArea.getDocument()
						.getText(offset, 1).charAt(0)))
		{
			offset++;
			word = word + ch;
		}
		if (word.length() == 0)
			return;
		JMenu sugg = new JMenu("Suggestions");
		if (!GUI.D.hasWord(word))
		{
			Vector<String> V = new Vector<String>(GUI.D.getSuggestionsForWord(
					word, 10));
			list2 = new JList(V);
			if (V.size() == 0)
			{
				list2.setEnabled(false);
				V.add("No Suggestions");
			}
			list2.setFocusable(true);
			// TODO keyboard control
			sugg.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					list2.requestFocusInWindow();
					list2.setSelectedIndex(0);
				}
			});
			sugg.add(list2);
			final Integer length = word.length();
			list2.addKeyListener(new KeyListener()
			{
				public void keyTyped(KeyEvent arg0)
				{
				}

				public void keyReleased(KeyEvent arg0)
				{
					if (arg0.getKeyCode() == KeyEvent.VK_ENTER
							&& list2.getSelectedValue() != null)
					{
						textArea.replaceRange(list2.getSelectedValue(), offs,
								offs + length);
						rightClickMenu.setVisible(false);
					}
				}

				public void keyPressed(KeyEvent arg0)
				{
				}
			});
			list2.addMouseListener(new MouseListener()
			{
				public void mouseReleased(MouseEvent arg0)
				{
					if (list2.getSelectedValue() != null)
					{
						textArea.replaceRange(list2.getSelectedValue(), offs,
								offs + length);
						textArea.setCaretPosition(offs
								+ list2.getSelectedValue().length());
						rightClickMenu.setVisible(false);
					}
				}

				public void mousePressed(MouseEvent arg0)
				{
				}

				public void mouseExited(MouseEvent arg0)
				{
				}

				public void mouseEntered(MouseEvent arg0)
				{
				}

				public void mouseClicked(MouseEvent arg0)
				{
				}
			});
		} else
			sugg.setEnabled(false);
		rightClickMenu.add(sugg);
		rightClickMenu.add(getCutAction());
		rightClickMenu.add(getCopyAction());
		rightClickMenu.add(getPasteAction());
		rightClickMenu.setFocusable(true);
		rightClickMenu.show(textArea, arg0.getX(), arg0.getY());
		rightClickMenu.setVisible(true);
	}

	public void showPopUp(ArrayList<String> words, String currentWord)
	{
		if (words == null)
			return;
		if (autocompleteSuggestions == null)
			autocompleteSuggestions = new JPopupMenu();
		if (autocompleteSuggestions2 == null)
			autocompleteSuggestions2 = new JMenu();
		autocompleteSuggestions.setFocusable(true);
		autocompleteSuggestions.removeAll();
		Point P = textArea.getCaret().getMagicCaretPosition();
		int count = 0;
		Vector<String> V = new Vector<String>();
		for (String i : words)
		{
			if (i.length() >= 2)
			{
				final String suggestion = currentWord + i;
				final Integer length = currentWord.length();
				V.add(suggestion);
				count++;
			}
			if (count == 10)
				break;
		}
		list = new JList(V);
		list.setFocusable(true);
		final Integer length = currentWord.length();
		list.addKeyListener(new KeyListener()
		{
			public void keyTyped(KeyEvent arg0)
			{
			}

			public void keyReleased(KeyEvent arg0)
			{
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER
						&& list.getSelectedValue() != null)
				{
					String string = list.getSelectedValue().substring(length);
					textArea.insert(string, textArea.getCaretPosition());
					autocompleteSuggestions.setVisible(false);
				}
			}

			public void keyPressed(KeyEvent arg0)
			{
			}
		});
		list.addMouseListener(new MouseListener()
		{
			public void mouseReleased(MouseEvent arg0)
			{
				if (list.getSelectedValue() != null)
				{
					String string = list.getSelectedValue().substring(length);
					textArea.insert(string, textArea.getCaretPosition());
					autocompleteSuggestions.setVisible(false);
				}
			}

			public void mousePressed(MouseEvent arg0)
			{
			}

			public void mouseExited(MouseEvent arg0)
			{
			}

			public void mouseEntered(MouseEvent arg0)
			{
			}

			public void mouseClicked(MouseEvent arg0)
			{
			}
		});
		autocompleteSuggestions.add(list);
		autocompleteSuggestions.show(textArea, P.x, P.y);
		autocompleteSuggestions.setFocusable(true);

		textArea.requestFocus();
	}

	public MouseListener getTextAreaMouseListener()
	{
		return new MouseListener()
		{
			public void mouseReleased(MouseEvent arg0)
			{
			}

			public void mousePressed(MouseEvent arg0)
			{
			}

			public void mouseExited(MouseEvent arg0)
			{
			}

			public void mouseEntered(MouseEvent arg0)
			{
			}

			public void mouseClicked(MouseEvent arg0)
			{
				if (arg0.getButton() == MouseEvent.BUTTON3)
				{
					try
					{
						showRightClickMenu(arg0);
					} catch (BadLocationException e)
					{
					}
				}
			}
		};
	}

	public int showSavePopUp()
	{
		String message = "Would you like to save the current file?";
		return JOptionPane.showConfirmDialog(mainFrame, message, "Save?", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
	}
	
	public AbstractAction getNewAction()
	{
		AbstractAction A = new AbstractAction("New")
		{
			private static final long serialVersionUID = 1024105350285122953L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(currentFile!=null || textArea.getDocument().getLength()!=0)
				{
					int ret = showSavePopUp();
					if(ret == JOptionPane.YES_OPTION)
						save.doClick();
					else if(ret != JOptionPane.NO_OPTION)
						return;
				}	
				mainFrame.setTitle("Osmos - Untitled");
				currentFile = null;
				textArea.setText("");
				textArea.setEnabled(true);
				textArea.setEditable(true);
//				textArea.setBackground(Color.white);
				textArea.requestFocus();
				undo.discardAllEdits();

				save.setEnabled(true);
				saveAs.setEnabled(true);
				close.setEnabled(true);
				menu2.setEnabled(true);
			}
		};
		return A;
	}

	public AbstractAction getOpenAction()
	{
		AbstractAction A = new AbstractAction("Open")
		{
			private static final long serialVersionUID = 7674996967982637924L;
			final JFileChooser openDialog = new JFileChooser();

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(currentFile!=null || textArea.getDocument().getLength()!=0)
				{
					int ret = showSavePopUp();
					if(ret == JOptionPane.YES_OPTION)
						save.doClick();
					else if(ret != JOptionPane.NO_OPTION)
						return;
				}	
				if (openDialog.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION)
				{
					openFile(openDialog.getSelectedFile());
				}
			}
		};
		return A;
	}

	public void openFile(File file)
	{
		if (file == null)
			return;
		try
		{

			Scanner fileReader = new Scanner(file);
			mainFrame.setTitle("Osmos - " + file.getName() + " ("
					+ file.getAbsolutePath() + ")");
			currentFile = file.getAbsolutePath();
			textArea.setEnabled(true);
			textArea.setEditable(true);
//			textArea.setBackground(Color.white);
			textArea.setText(new String(Files.readAllBytes(Paths.get(file
					.getAbsolutePath()))));
			fileReader.close();
			undo.discardAllEdits();
			save.setEnabled(true);
			saveAs.setEnabled(true);
			close.setEnabled(true);
			menu2.setEnabled(true);
		} catch (Exception e)
		{
			JOptionPane.showConfirmDialog(textArea, "Unable to open file.",
					"Error", JOptionPane.PLAIN_MESSAGE);
			return;
		}
	}

	public AbstractAction getSaveAction()
	{
		AbstractAction save = new AbstractAction("Save")
		{
			private static final long serialVersionUID = -2566845091096724223L;
			private final JFileChooser saveDialog = new JFileChooser();

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (currentFile == null)
					saveToFile(saveDialog);
				else
					try
					{
						PrintWriter P = new PrintWriter(new File(currentFile));
						P.print(textArea.getText());
						P.close();
					} catch (Exception e1)
					{
						JOptionPane.showConfirmDialog(null,
								"Unable to save file.", "Error",
								JOptionPane.OK_OPTION);
					}
			}
		};
		return save;
	}

	public AbstractAction getSaveAsAction()
	{
		AbstractAction save = new AbstractAction("Save As")
		{
			private static final long serialVersionUID = -2566845091096724223L;
			private final JFileChooser saveDialog = new JFileChooser();

			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveToFile(saveDialog);
			}
		};
		return save;
	}

	private void saveToFile(JFileChooser saveDialog)
	{
		saveDialog.showSaveDialog(mainFrame);
		File f = saveDialog.getSelectedFile();
		if (f == null)
			return;
		if (f.exists())
		{
			int dialogResult = JOptionPane.showConfirmDialog(textArea,
					"Do you want to replace the file?", "Replace?",
					JOptionPane.YES_NO_OPTION);
			if (dialogResult != JOptionPane.YES_OPTION)
			{
				return;
			}
		}
		try
		{
			PrintWriter fileWriter = new PrintWriter(f);
			fileWriter.print(textArea.getText());
			fileWriter.close();
			currentFile = f.getAbsolutePath();
			mainFrame.setTitle("Osmos - " + f.getName() + " ("
					+ f.getAbsolutePath() + ")");
		} catch (Exception e1)
		{
			JOptionPane.showConfirmDialog(textArea, "Unable to create file.",
					"Error", JOptionPane.OK_OPTION);
		}
	}

	public AbstractAction getCloseAction()
	{
		return new AbstractAction("Close File")
		{
			private static final long serialVersionUID = 5019066038928861963L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(currentFile!=null || textArea.getDocument().getLength()!=0)
				{
					int ret = showSavePopUp();
					if(ret == JOptionPane.YES_OPTION)
						save.doClick();
					else if(ret != JOptionPane.NO_OPTION)
						return;
				}	

				mainFrame.setTitle("Osmos");
				currentFile = null;
				textArea.setText("");
				textArea.setEnabled(false);
				textArea.setEditable(false);
//				textArea.setBackground(Color.gray);
				undo.discardAllEdits();
				menu2.setEnabled(false);
				if(p != null)
					p.setVisible(false);
				
				save.setEnabled(false);
				saveAs.setEnabled(false);
				close.setEnabled(false);
				
			}
		};
	}

	public AbstractAction getSuggestionAction(final String sugg,
			final Integer length)
	{
		return new AbstractAction(sugg)
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				String string = sugg.substring(length);
				textArea.insert(string, textArea.getCaretPosition());
				autocompleteSuggestions.setVisible(false);
			}
		};
	}

	public AbstractAction getUndoAction()
	{
		return new AbstractAction("Undo")
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if (undo.canUndo())
					undo.undo();
			}
		};
	}

	public AbstractAction getRedoAction()
	{
		return new AbstractAction("Redo")
		{
			public void actionPerformed(ActionEvent e)
			{
				if (undo.canRedo())
					undo.redo();
			}
		};
	}

	public AbstractAction getCopyAction()
	{
		return new AbstractAction("Copy")
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				String string = textArea.getSelectedText();
				if (string != null && string.length() > 0)
				{
					StringSelection S = new StringSelection(
							textArea.getSelectedText());
					Toolkit.getDefaultToolkit().getSystemClipboard()
							.setContents(S, S);
				}
			}
		};
	}

	public AbstractAction getCutAction()
	{
		return new AbstractAction("Cut")
		{
			public void actionPerformed(ActionEvent arg0)
			{
				String string = textArea.getSelectedText();
				if (string != null && string.length() > 0)
				{
					StringSelection S = new StringSelection(
							textArea.getSelectedText());
					Toolkit.getDefaultToolkit().getSystemClipboard()
							.setContents(S, S);
					textArea.replaceSelection("");
				}
			}
		};
	}

	public AbstractAction getPasteAction()
	{
		return new AbstractAction("Paste")
		{
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					String data = (String) Toolkit.getDefaultToolkit()
							.getSystemClipboard()
							.getData(DataFlavor.stringFlavor);
					textArea.replaceSelection(data);
				} catch (Exception e)
				{
				}
			}
		};
	}

	public AbstractAction getSelectAllAction()
	{
		return new AbstractAction("Select All")
		{
			public void actionPerformed(ActionEvent arg0)
			{
				textArea.setSelectionStart(0);
				textArea.setSelectionEnd(textArea.getDocument().getLength());
			}
		};
	}

	public AbstractAction getFindAction()
	{
		return new AbstractAction("Find/Replace")
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				findReplaceDialog();
			}
		};
	}
	
	private void findReplaceDialog()
	{
		if(p!=null)
		{
			p.setVisible(!p.isVisible());
			return;
		}
		p = new JPanel();
		p.setLayout(new GridLayout(2, 1));
		JToolBar toolbar =new JToolBar(JToolBar.HORIZONTAL);
		toolbar.setFloatable(false);
		toolbar.setRollover(false);
		toolbar.setMinimumSize(new Dimension(mainFrame.getWidth(), 75));
		JLabel find = new JLabel("Find What");
		final JTextField findField = new JTextField();
		JButton findNext = new JButton("Find Next");
		JButton findPrev = new JButton("Find Previous");
		findNext.addActionListener(new AbstractAction()
		{			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String searchFor = findField.getText();
				System.out.println("Test");
				int pos = textArea.getSelectionStart();
				if(searchFor.equals(textArea.getSelectedText()))
					pos++;
				try
				{
					String doc = textArea.getText(pos, textArea.getDocument().getLength() - pos);
					int find = doc.indexOf(searchFor);
					if(find != -1)
					{
						textArea.setSelectionEnd(pos + find + searchFor.length());
						textArea.setSelectionStart(find + pos);
					}
				}
				catch(BadLocationException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				textArea.requestFocus();
			}
		});
		findPrev.addActionListener(new AbstractAction()
		{			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String searchFor = findField.getText();
				int pos = textArea.getSelectionEnd();
				if(searchFor.equals(textArea.getSelectedText()))
					pos--;
				try
				{
					String doc = textArea.getText(0, pos);
					int find = doc.lastIndexOf(searchFor);
					if(find != -1)
					{
						textArea.setSelectionStart(find);
						textArea.setSelectionEnd(find + searchFor.length());
					}
				}
				catch(BadLocationException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				textArea.requestFocus();
			}
		});
		toolbar.add(find);
		toolbar.add(findField);
		toolbar.add(findNext);
		toolbar.add(findPrev);
		toolbar.setVisible(true);
		p.add(toolbar);
//		mainFrame.pack();

		JToolBar replaceToolbar =new JToolBar(JToolBar.HORIZONTAL);
		replaceToolbar.setFloatable(false);
		replaceToolbar.setRollover(false);
		replaceToolbar.setMinimumSize(new Dimension(mainFrame.getWidth(), 75));
		JLabel replaceWith = new JLabel("Replace with:");
		final JTextField replaceField = new JTextField();
		JButton replace = new JButton("Replace");
		JButton replaceAll = new JButton("Replace All");
		replace.addActionListener(new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(textArea.getSelectedText() != null && textArea.getSelectedText().length() != 0)
					textArea.replaceSelection(replaceField.getText());
			}
		});
		replaceAll.addActionListener(new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(findField.getText()!=null && findField.getText().length()!=0)
					textArea.setText(textArea.getText().replaceAll(findField.getText(), replaceField.getText()));
			}
		});
		replaceToolbar.add(replaceWith);
		replaceToolbar.add(replaceField);
		replaceToolbar.add(replace);
		replaceToolbar.add(replaceAll);
		replaceToolbar.setVisible(true);
		p.add(replaceToolbar);
		p.setVisible(true);
		mainFrame.add(p, BorderLayout.PAGE_END);
		mainFrame.pack();
	}
	
	private AbstractAction getWordWrapAction()
	{
		return new AbstractAction("Word Wrap")
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				textArea.setLineWrap(wordWrap.isSelected());
			}
		};
	}

}
