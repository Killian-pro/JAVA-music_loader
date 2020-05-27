import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;

import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Label;


public class Main {

	protected Shell shlSongPlayer;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Main window = new Main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 * @throws LineUnavailableException 
	 * @throws IOException 
	 * @throws UnsupportedAudioFileException 
	 */
	public void open() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		Display display = Display.getDefault();
		createContents();
		shlSongPlayer.open();
		shlSongPlayer.layout();
		while (!shlSongPlayer.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public String myfile()
	{

		JFileChooser chooser = new JFileChooser("\\");
		FileNameExtensionFilter filter = new FileNameExtensionFilter( "mp3 & wav","wav");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(getParent());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: " + chooser.getSelectedFile().getPath());
		}
		String song=chooser.getSelectedFile().getPath();
		return song;
	}
	private Component getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	static final long RECORD_TIME = 5000;  // 5SEC 
	// CHANGER LA VALEUR DU TEMPS DE RECORD

	// path of the wav file
	File wavFile = new File("recordAudio.wav");

	// format of audio file
	AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

	// the line from which audio data is captured
	SourceDataLine sourceDataLine;
	TargetDataLine targetDataLine;
	byte tempBuffer[] = new byte[10000];

	static Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();

	/**
	 * Defines an audio format
	 */
	AudioFormat getAudioFormat() {
		float sampleRate = 16000;
		int sampleSizeInBits = 8;
		int channels = 2;
		boolean signed = true;
		boolean bigEndian = true;
		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
				channels, signed, bigEndian);
		return format;
	}

	void  start() {
		try {
			//Mixer.Info[] mixerinfo=AudioSystem.getMixerInfo();
			AudioFormat audioFormat = getAudioFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
			//				sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
			//	            sourceDataLine.open(audioFormat);
			//	            sourceDataLine.start();
			// checks if system supports the data line
			if (!AudioSystem.isLineSupported(Port.Info.SPEAKER)) {
				System.out.println("Line not supported");
				System.exit(0);
			}
			
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();   // start capturing


			System.out.println("Start capturing...");

			AudioInputStream ais = new AudioInputStream(targetDataLine);

			System.out.println("Start recording...");

			// start recording
			AudioSystem.write(ais, fileType, wavFile);

		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		} 
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	void finish() {
		targetDataLine.stop();
		targetDataLine.close();
		System.out.println("Finished");
	}


	public static void record() {
		final Main recorder = new Main();

		// creates a new thread that waits for a specified
		// of time before stopping
		 Thread t = new Thread() {
			 public void run() {
			        System.out.println("Magique le \"Thread\" qui permet de lire plusieurs fonctions en même temps");
			        recorder.start();
			      }
		};
		
		Thread stopper = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(RECORD_TIME);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				recorder.finish();
			}
		});

		t.start();
		stopper.start();

		// start recording
		//
	
	}


	/**
	 * Create contents of the window.
	 * @throws IOException 
	 * @throws UnsupportedAudioFileException 
	 * @throws LineUnavailableException 
	 */
	protected void createContents() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		shlSongPlayer = new Shell();
		shlSongPlayer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlSongPlayer.setSize(450, 300);
		shlSongPlayer.setText("SONG PLAYER");
		Button btnInput = new Button(shlSongPlayer, SWT.NONE);

		Clip clip1 = AudioSystem.getClip();
		Clip clip2 = AudioSystem.getClip();
		Clip audioclip= AudioSystem.getClip();


		Button btnPlay_2 = new Button(shlSongPlayer, SWT.NONE);
		btnPlay_2.setEnabled(false);
		btnPlay_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clip1.start();
				btnPlay_2.setVisible(false);
			}
		});

		Button btnPause = new Button(shlSongPlayer, SWT.NONE);
		btnPause.setEnabled(false);
		btnPause.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clip1.stop();
				btnPlay_2.setVisible(true);
			}
		});

		Button btnRestart_1 = new Button(shlSongPlayer, SWT.NONE);
		btnRestart_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clip1.setMicrosecondPosition(0);
			}
		});



		btnInput.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clip1.close();
				btnPlay_2.setEnabled(true);
				btnPause.setEnabled(true);
				btnRestart_1.setEnabled(true);

				try {
					AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(myfile()).getAbsoluteFile());
					try {
						clip1.open(audioIn);
					} catch (LineUnavailableException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (UnsupportedAudioFileException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch(Exception ex) {
					System.out.println("Erreur : " + ex);
				}


			}
		});


		//		  URL url = this.getClass().getClassLoader().getResource("dax.wav");
		//   AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(myfile()).getAbsoluteFile());
		//   AudioInputStream audioIn2 = AudioSystem.getAudioInputStream(new File(myfile()).getAbsoluteFile());


		Button btnPlay = new Button(shlSongPlayer, SWT.NONE);
		btnPlay.setEnabled(false);
		btnPlay.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				clip2.start();
				btnPlay.setVisible(false);
			}
		}
				);

		Button btnStop = new Button(shlSongPlayer, SWT.NONE);
		btnStop.setEnabled(false);
		btnStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clip2.stop();
				btnPlay.setVisible(true);

			}
		});

		Button btnRestart = new Button(shlSongPlayer, SWT.NONE);
		btnRestart.setEnabled(false);
		btnRestart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clip2.setMicrosecondPosition(0);
			}
		});

		Button btnInput_1 = new Button(shlSongPlayer, SWT.NONE);
		btnInput_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clip2.close();
				AudioInputStream audioIn2;
				try {
					audioIn2 = AudioSystem.getAudioInputStream(new File(myfile()).getAbsoluteFile());
					clip2.open(audioIn2);
				} catch (UnsupportedAudioFileException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (LineUnavailableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}  catch(Exception ex) {
					System.out.println("Erreur : " + ex);
				}

				btnPlay.setEnabled(true);
				btnStop.setEnabled(true);
				btnRestart.setEnabled(true);
			}
		});


		Button btnCheckButton = new Button(shlSongPlayer, SWT.CHECK);
		btnCheckButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clip2.loop(Clip.LOOP_CONTINUOUSLY);
			}
		});

		Button btnCheckButton_1 = new Button(shlSongPlayer, SWT.CHECK);
		btnCheckButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clip1.loop(Clip.LOOP_CONTINUOUSLY);
			}
		});
		
		Button btnPlayRec = new Button(shlSongPlayer, SWT.NONE);
		btnPlayRec.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				audioclip.close();
				AudioInputStream audioIn3;
				try {
					audioIn3 = AudioSystem.getAudioInputStream(wavFile);
					audioclip.open(audioIn3);
					audioclip.start();
				} catch (UnsupportedAudioFileException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (LineUnavailableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}  catch(Exception ex) {
					System.out.println("Erreur : " + ex);
				}

			}
				
		});

		Button btnRec = new Button(shlSongPlayer, SWT.NONE);
		btnRec.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				record();
				btnPlayRec.setEnabled(true);
			}
		});
		
	


		///LES BOUTONS

		btnPlay_2.setBounds(283, 73, 75, 25);
		formToolkit.adapt(btnPlay_2, true, true);
		btnPlay_2.setText("PLAY");


		btnPlay.setBounds(55, 73, 75, 25);
		formToolkit.adapt(btnPlay, true, true);
		btnPlay.setText("PLAY");


		btnStop.setBounds(55, 73, 75, 25);
		formToolkit.adapt(btnStop, true, true);
		btnStop.setText("PAUSE");


		btnRestart.setBounds(55, 116, 75, 25);
		formToolkit.adapt(btnRestart, true, true);
		btnRestart.setText("RESTART");

		Label lblPlayer = new Label(shlSongPlayer, SWT.NONE);
		lblPlayer.setFont(SWTResourceManager.getFont("Roman", 11, SWT.NORMAL));
		lblPlayer.setAlignment(SWT.CENTER);
		lblPlayer.setBounds(55, 28, 75, 15);
		formToolkit.adapt(lblPlayer, true, true);
		lblPlayer.setText("Player 1");


		btnInput.setBounds(283, 147, 75, 25);
		formToolkit.adapt(btnInput, true, true);
		btnInput.setText("INPUT");

		Label lblPlayer_2 = new Label(shlSongPlayer, SWT.NONE);
		lblPlayer_2.setText("Player 2");
		lblPlayer_2.setFont(SWTResourceManager.getFont("Roman", 11, SWT.NORMAL));
		lblPlayer_2.setAlignment(SWT.CENTER);
		lblPlayer_2.setBounds(283, 28, 75, 15);
		formToolkit.adapt(lblPlayer_2, true, true);


		btnPause.setBounds(283, 73, 75, 25);
		formToolkit.adapt(btnPause, true, true);
		btnPause.setText("PAUSE");


		btnRestart_1.setEnabled(false);
		btnRestart_1.setBounds(283, 116, 75, 25);
		formToolkit.adapt(btnRestart_1, true, true);
		btnRestart_1.setText("RESTART");

		Label label = new Label(shlSongPlayer, SWT.SEPARATOR | SWT.VERTICAL);
		label.setBounds(210, -28, 2, 221);
		formToolkit.adapt(label, true, true);

		Label label_1 = new Label(shlSongPlayer, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_1.setBounds(0, 191, 434, 2);
		formToolkit.adapt(label_1, true, true);

		btnRec.setBounds(55, 209, 75, 25);
		formToolkit.adapt(btnRec, true, true);
		btnRec.setText("REC");

		btnPlayRec.setEnabled(false);
		btnPlayRec.setBounds(283, 209, 75, 25);
		formToolkit.adapt(btnPlayRec, true, true);
		btnPlayRec.setText("PLAY REC");


		btnInput_1.setBounds(55, 148, 75, 25);
		formToolkit.adapt(btnInput_1, true, true);
		btnInput_1.setText("INPUT");


		btnCheckButton.setBounds(161, 82, 18, 16);
		formToolkit.adapt(btnCheckButton, true, true);

		Label lblLoop = new Label(shlSongPlayer, SWT.NONE);
		lblLoop.setText("Loop");
		lblLoop.setFont(SWTResourceManager.getFont("Arial", 11, SWT.NORMAL));
		lblLoop.setBounds(151, 56, 37, 25);
		formToolkit.adapt(lblLoop, true, true);

		Label lblLoop_1 = new Label(shlSongPlayer, SWT.NONE);
		lblLoop_1.setText("Loop");
		lblLoop_1.setFont(SWTResourceManager.getFont("Arial", 11, SWT.NORMAL));
		lblLoop_1.setBounds(370, 56, 37, 25);
		formToolkit.adapt(lblLoop_1, true, true);


		btnCheckButton_1.setBounds(380, 82, 18, 16);
		formToolkit.adapt(btnCheckButton_1, true, true);

	}

}

