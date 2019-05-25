package gui;
import dictionary.Dictionary;

public class GUI
{
	public static final Dictionary D = new Dictionary("WordList");
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		javax.swing.SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				@SuppressWarnings("unused")
				Window W = new Window ( );
			}
		});
	}

}
