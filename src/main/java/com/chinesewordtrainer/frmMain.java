/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinesewordtrainer;

import static com.chinesewordtrainer.LearningMode.PINYIN;
import static com.chinesewordtrainer.LearningMode.SIMPLIFIED_HANZI;
import static com.chinesewordtrainer.LearningMode.TRADITIONAL_HANZI;
import static com.chinesewordtrainer.LearningMode.TRANSLATE_DE_ZH;
import static com.chinesewordtrainer.LearningMode.TRANSLATE_ZH_DE;
import static com.chinesewordtrainer.LearningMode.TINGXIE;
import java.awt.Font;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import java.awt.Component;
import java.util.Collection;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.LinkedHashMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

/**
 *
 * @author Deus
 */
public class frmMain extends javax.swing.JFrame {

    /**
     * Creates new form frmMain
     */
    public frmMain() {
	initComponents();

	cmdTest.setVisible(false);

	txtConsole.setEditable(false);
	txtConsole.setFont(new Font("Arial Unicode MS", txtConsole.getFont().getStyle(), 11));
	txtConsole.setDefaultLocale(java.util.Locale.SIMPLIFIED_CHINESE);
	console = new ConsoleHandler(txtConsole);
	csvReader = CSVReader.getInstance(console);
	soundFileHandler = SoundFileHandler.getInstance(console);
	tokenizer = new PinyinAudioTokenizer(soundFileHandler.getAudioFiles());
	soundPlayer = new SoundPlayer(console);

	try {
	    ChineseTtsService ttsService = ChineseTtsService.fromEnvironment();
	    wordAudioService = new WordAudioService(ttsService, console, Paths.get("C:\\Users\\Deus\\OneDrive\\Sinologie\\Zhongwen\\AzureTTS\\Cache"));
	    console.logMsg("Word audio service initialized successfully.");
	} catch (Exception ex) {
	    wordAudioService = null;
	    console.logErr("Could not initialize word audio service: " + ex.getMessage());
	    System.exit(-1);
	}

	if (false) {
	    LessonTextProcessor ltp = new LessonTextProcessor(console, allWords);
	    ltp.generateNamedTextDictionaries();
	    System.exit(0);
	}

	if (false) {
	    csvReader.prepareNewVocabList();
	    System.exit(0);
	}

	allWords = csvReader.readWords();

	if (allWords == null || allWords.isEmpty()) {
	    console.logErr("ERROR: Could not build word list, allWords was null or empty. Please restart program!");
	    cboLearningMode.setEnabled(false);
	    cmdCorrect.setEnabled(false);
	    cmdLearningModeApply.setEnabled(false);
	    cmdResetWordStats.setEnabled(false);
	    cmdRevealAnswer.setEnabled(false);
	    cmdWrong.setEnabled(false);
	}

	if (allWords != null && !allWords.isEmpty()) {
	    Set<String> availableLessons = new LinkedHashSet<>();

	    for (Word w : allWords) {
		if (w.getLesson() != null && !w.getLesson().isBlank()) {
		    availableLessons.add(w.getLesson());
		}
	    }

	    populateLessonList(availableLessons);

	    console.logMsg("Loaded " + availableLessons.size()
		    + " available lessons.");
	}

	cmdRevealAnswer.setEnabled(false);
	cmdWrong.setEnabled(false);
	cmdCorrect.setEnabled(false);

	weightedShuffle = new WeightedShuffle(console, isDevMode);

	if (!isDevMode) {
	    cmdResetWordStats.setEnabled(false);
	    cmdResetWordStats.setVisible(false);
	}

	// Custom JSpinner-implementation for WeightedShuffleBeta-Spinner
	SpinnerNumberModel betaModel = new SpinnerNumberModel(1.0, 0.0, 15.0, 0.1);
	numShuffleBeta.setModel(betaModel);
	numShuffleBeta.setEditor(new javax.swing.JSpinner.NumberEditor(numShuffleBeta, "0.0"));

	cmdPreviousFont.setVisible(false);
	cmdPreviousFont.setEnabled(false);
	cmdNextFont.setVisible(false);
	cmdNextFont.setEnabled(false);

	if (fontTest) {
	    fonts = FontProbe.findFontFamilies(true);

	    cmdPreviousFont.setVisible(true);
	    cmdPreviousFont.setEnabled(true);
	    cmdNextFont.setVisible(true);
	    cmdNextFont.setEnabled(true);

	    txtQuestion.setText("我们一起去图书馆吧！");
	    txtAnswer.setText("ÜbäbÖbÄöüß!");

	    changeFontTest();
	} else {
	    smallFont_latin = new java.awt.Font(displayFont_latin, java.awt.Font.PLAIN, 24);
	    mediumFont_latin = new java.awt.Font(displayFont_latin, java.awt.Font.PLAIN, 36);
	    largeFont_latin = new java.awt.Font(displayFont_latin, java.awt.Font.PLAIN, 54);
	    smallFont_hanzi = new java.awt.Font(displayFont_hanzi, java.awt.Font.PLAIN, 24);
	    mediumFont_hanzi = new java.awt.Font(displayFont_hanzi, java.awt.Font.PLAIN, 36);
	    largeFont_hanzi = new java.awt.Font(displayFont_hanzi, java.awt.Font.PLAIN, 54);

	    txtQuestion.setFont(largeFont_hanzi);
	    txtHint.setFont(smallFont_latin);
	    txtAnswer.setFont(mediumFont_latin);

	    txtQuestion.setText("欢迎使用中文单词训练器！");
	    txtHint.setText("Willkommen bei ChineseWordTrainer!");
	    txtAnswer.setText("Willkommen bei ChineseWordTrainer!");

	    console.logMsg("displayFont_latin: " + displayFont_latin);
	    console.logMsg("displayFont_hanzi: " + largeFont_hanzi.getFontName());

	    repaintCustomTextFields();
	}

	if (true) {
	    curWord = allWords.get(59);
	    playWordAudio();
	}
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtConsole = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtQuestion = new CenteredTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtHint = new CenteredTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtAnswer = new CenteredTextArea();
        cmdWrong = new javax.swing.JButton();
        cmdCorrect = new javax.swing.JButton();
        cmdEnd = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        cboLearningMode = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        cmdLessonsSelectAll = new javax.swing.JButton();
        cmdLessonsSelectNone = new javax.swing.JButton();
        scrLessons = new javax.swing.JScrollPane();
        pnlLessonList = new javax.swing.JPanel();
        cmdLearningModeApply = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        chkTradHanziOnlyIfDifference = new javax.swing.JCheckBox();
        numShuffleBeta = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        chkOnlyUnknownWords = new javax.swing.JCheckBox();
        chkAlwaysShowHint = new javax.swing.JCheckBox();
        chkPlayAudioImmediately = new javax.swing.JCheckBox();
        chkPlayAudioOnlyForTingXie = new javax.swing.JCheckBox();
        cmdRevealAnswer = new javax.swing.JButton();
        cmdResetWordStats = new javax.swing.JButton();
        cmdPreviousFont = new javax.swing.JButton();
        cmdNextFont = new javax.swing.JButton();
        cmdTest = new javax.swing.JButton();
        cmdPlayAudio = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane1.setViewportView(txtConsole);

        txtQuestion.setEditable(false);
        txtQuestion.setColumns(1);
        txtQuestion.setFont(new java.awt.Font("FangSong", 0, 48)); // NOI18N
        txtQuestion.setRows(2);
        txtQuestion.setText("Question");
        jScrollPane2.setViewportView(txtQuestion);

        txtHint.setEditable(false);
        txtHint.setColumns(1);
        txtHint.setFont(new java.awt.Font("FangSong", 0, 24)); // NOI18N
        txtHint.setRows(2);
        txtHint.setText("Click to reveal hint");
        txtHint.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                txtHintMousePressed(evt);
            }
        });
        jScrollPane3.setViewportView(txtHint);

        txtAnswer.setEditable(false);
        txtAnswer.setColumns(1);
        txtAnswer.setFont(new java.awt.Font("FangSong", 0, 54)); // NOI18N
        txtAnswer.setRows(2);
        txtAnswer.setText("Solution");
        jScrollPane4.setViewportView(txtAnswer);

        cmdWrong.setText("Wrong");
        cmdWrong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdWrongActionPerformed(evt);
            }
        });

        cmdCorrect.setText("Correct");
        cmdCorrect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCorrectActionPerformed(evt);
            }
        });

        cmdEnd.setText("End Program");
        cmdEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEndActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Learning Mode", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N

        cboLearningMode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Learn simplified Hanzi", "Learn traditional Hanzi", "Learn Pinyin", "Learn Translations de -> zh", "Learn Translations zh -> de", "Tingxie" }));
        cboLearningMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLearningModeActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Include Lessons"));

        cmdLessonsSelectAll.setText("Select all");
        cmdLessonsSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdLessonsSelectAllActionPerformed(evt);
            }
        });

        cmdLessonsSelectNone.setText("Select none");
        cmdLessonsSelectNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdLessonsSelectNoneActionPerformed(evt);
            }
        });

        scrLessons.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        javax.swing.GroupLayout pnlLessonListLayout = new javax.swing.GroupLayout(pnlLessonList);
        pnlLessonList.setLayout(pnlLessonListLayout);
        pnlLessonListLayout.setHorizontalGroup(
            pnlLessonListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 439, Short.MAX_VALUE)
        );
        pnlLessonListLayout.setVerticalGroup(
            pnlLessonListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 149, Short.MAX_VALUE)
        );

        scrLessons.setViewportView(pnlLessonList);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrLessons)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cmdLessonsSelectAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmdLessonsSelectNone)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdLessonsSelectAll)
                    .addComponent(cmdLessonsSelectNone))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrLessons, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                .addContainerGap())
        );

        cmdLearningModeApply.setText("Apply and restart session");
        cmdLearningModeApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdLearningModeApplyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboLearningMode, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cmdLearningModeApply, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(cboLearningMode, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdLearningModeApply, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N

        chkTradHanziOnlyIfDifference.setText("Traditional Hanzi: Show only words where trad. Hanzi differs from simplified");

        numShuffleBeta.setModel(new javax.swing.SpinnerNumberModel(1.0d, 0.0d, 15.0d, 1.0d));

        jLabel1.setText("Weighted shuffle Beta");

        chkOnlyUnknownWords.setText("Include only words not yet learned in word list");

        chkAlwaysShowHint.setText("Always show hint immediately");
        chkAlwaysShowHint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAlwaysShowHintActionPerformed(evt);
            }
        });

        chkPlayAudioImmediately.setText("Play Audio for word immediately");

        chkPlayAudioOnlyForTingXie.setText("Play Audio only for tingxie training mode");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numShuffleBeta, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkTradHanziOnlyIfDifference)
                    .addComponent(chkOnlyUnknownWords)
                    .addComponent(chkAlwaysShowHint)
                    .addComponent(chkPlayAudioImmediately)
                    .addComponent(chkPlayAudioOnlyForTingXie))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numShuffleBeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkTradHanziOnlyIfDifference)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkOnlyUnknownWords, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkAlwaysShowHint, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPlayAudioImmediately)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkPlayAudioOnlyForTingXie)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        cmdRevealAnswer.setText("Reveal Answer");
        cmdRevealAnswer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRevealAnswerActionPerformed(evt);
            }
        });

        cmdResetWordStats.setText("Reset word stats");
        cmdResetWordStats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdResetWordStatsActionPerformed(evt);
            }
        });

        cmdPreviousFont.setText("Previous font");
        cmdPreviousFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPreviousFontActionPerformed(evt);
            }
        });

        cmdNextFont.setText("Next font");
        cmdNextFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdNextFontActionPerformed(evt);
            }
        });

        cmdTest.setText("Test");
        cmdTest.setEnabled(false);
        cmdTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdTestActionPerformed(evt);
            }
        });

        cmdPlayAudio.setText("Play Audio");
        cmdPlayAudio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPlayAudioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(cmdRevealAnswer, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(cmdPlayAudio, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cmdWrong, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdCorrect, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cmdPreviousFont, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdNextFont, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cmdResetWordStats, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                        .addComponent(cmdTest, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdRevealAnswer, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cmdPlayAudio, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cmdCorrect, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmdWrong, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cmdPreviousFont, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmdNextFont, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 172, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmdEnd, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(cmdResetWordStats, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmdTest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboLearningModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLearningModeActionPerformed

    }//GEN-LAST:event_cboLearningModeActionPerformed

    private void cmdLearningModeApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdLearningModeApplyActionPerformed
	applyLearningMode();
    }//GEN-LAST:event_cmdLearningModeApplyActionPerformed

    private void applyLearningMode() {
	String selectedMode = cboLearningMode.getSelectedItem().toString();
	System.out.println("SELECTED MODE: " + selectedMode);

	switch (selectedMode) {
	    case "Learn simplified Hanzi":
		curLearningMode = LearningMode.SIMPLIFIED_HANZI;
		txtQuestion.setFont(mediumFont_latin); // Pinyin
		txtHint.setFont(smallFont_latin); // Translation
		txtAnswer.setFont(largeFont_hanzi); // Simple Hanzi
		break;
	    case "Learn traditional Hanzi":
		curLearningMode = LearningMode.TRADITIONAL_HANZI;
		txtQuestion.setFont(largeFont_hanzi); // Traditional Hanzi
		txtHint.setFont(smallFont_latin); // Translation
		txtAnswer.setFont(largeFont_hanzi); // Simple Hanzi
		break;
	    case "Learn Pinyin":
		curLearningMode = LearningMode.PINYIN;
		txtQuestion.setFont(largeFont_hanzi); // Simple Hanzi
		txtHint.setFont(smallFont_latin); // Translation
		txtAnswer.setFont(mediumFont_latin); // Pinyin
		break;
	    case "Learn Translations de -> zh":
		curLearningMode = LearningMode.TRANSLATE_DE_ZH;
		txtQuestion.setFont(smallFont_latin); // Translation
		txtHint.setFont(mediumFont_latin); // Pinyin
		txtAnswer.setFont(largeFont_hanzi); // Simple Hanzi
		break;
	    case "Learn Translations zh -> de":
		curLearningMode = LearningMode.TRANSLATE_ZH_DE;
		txtQuestion.setFont(largeFont_hanzi); // Simple Hanzi
		txtHint.setFont(mediumFont_latin); // Pinyin
		txtAnswer.setFont(smallFont_latin); // Translation
		break;
	    case "Tingxie":
		curLearningMode = LearningMode.TINGXIE;
		txtQuestion.setFont(mediumFont_latin); // "Listen to audio"
		txtHint.setFont(smallFont_latin);      // Pinyin/translation hint
		txtAnswer.setFont(largeFont_hanzi);    // Hanzi answer
		break;
	    default:
		curLearningMode = null;
		console.logErr("ERROR in frmMain.cboLearningModeActionPerformed(): Unknown mode \"" + selectedMode + "\". Can't switch learning mode. Please restart the program.");
	}

	if (curLearningMode == null) {
	    return;
	}

	// Collect selected lessons
	Set<String> selectedLessons = new LinkedHashSet<>();

	for (Map.Entry<String, JCheckBox> entry
		: lessonCheckboxes.entrySet()) {

	    if (entry.getValue().isSelected()) {
		selectedLessons.add(entry.getKey());
	    }
	}

	if (selectedLessons.isEmpty()) {
	    JOptionPane.showMessageDialog(
		    this,
		    "Please select at least one lesson.",
		    "No lessons selected",
		    JOptionPane.WARNING_MESSAGE
	    );
	    return;
	}

	console.logMsg("Active lessons: " + selectedLessons);
	console.logMsg("Building session word list...");

	// Build new session list
	ArrayList<Word> newList = new ArrayList<>();

	boolean onlyUnknown = chkOnlyUnknownWords.isSelected();

	boolean onlyDifferentTraditional
		= curLearningMode == LearningMode.TRADITIONAL_HANZI
		&& chkTradHanziOnlyIfDifference.isSelected();

	for (Word w : allWords) {

	    // Lesson filter
	    if (!selectedLessons.contains(w.getLesson())) {
		continue;
	    }

	    // Traditional Hanzi filter
	    if (onlyDifferentTraditional
		    && w.getSimpleHanzi().equals(w.getTraditionalHanzi())) {
		continue;
	    }

	    // Unknown words filter
	    if (onlyUnknown && w.getDifficulty(curLearningMode) > 0.0) {
		continue;
	    }

	    newList.add(w);
	}

	if (newList.isEmpty()) {
	    JOptionPane.showMessageDialog(
		    this,
		    "No words match the selected lessons and filters.",
		    "Empty session",
		    JOptionPane.WARNING_MESSAGE
	    );
	    return;
	}

	curList = newList;


	// Collections.shuffle(curList);
	double beta = ((Number) numShuffleBeta.getValue()).doubleValue();
	weightedShuffle.interleavingShuffle(curList, beta, curLearningMode);

	System.out.println("Successfully build session list of " + curList.size() + " words!");

	curListIndex = 0;
	cmdRevealAnswer.setEnabled(true);
	loadNextWord();
    }

    public void loadNextWord() {
	if (curListIndex == curList.size()) {
	    console.logMsg("End of list reached, restarting list...");
	    curListIndex = 0;
	}

	if ((curSessionCorrect + curSessionWrong) % 10 == 0) {
	    console.logMsg("Saving current word data...");
	    csvReader.writeToFile(allWords);
	    console.logMsg("Current word data successfully saved!");
	}

	curWord = curList.get(curListIndex);

	console.cprintln("  Word " + curListIndex + " / " + curList.size() + String.format(" (Difficulty: %.2f)", curWord.getDifficulty(curLearningMode)));

	cmdWrong.setEnabled(false);
	cmdCorrect.setEnabled(false);

	switch (curLearningMode) {
	    case SIMPLIFIED_HANZI:
		txtQuestion.setText(curWord.getPinyin());
		txtHint.setText("Click to reveal hint");
		txtAnswer.setText("");
		if (chkPlayAudioImmediately.isSelected() && !chkPlayAudioOnlyForTingXie.isSelected()) {
		    playWordAudio();
		}
		break;
	    case TRADITIONAL_HANZI:
		txtQuestion.setText(curWord.getTraditionalHanzi());
		txtHint.setText("Click to reveal hint");
		txtAnswer.setText("");
		if (chkPlayAudioImmediately.isSelected() && !chkPlayAudioOnlyForTingXie.isSelected()) {
		    playWordAudio();
		}
		break;
	    case PINYIN:
		txtQuestion.setText(curWord.getSimpleHanzi());
		txtHint.setText("Click to reveal hint");
		txtAnswer.setText("");
		if (chkPlayAudioImmediately.isSelected() && !chkPlayAudioOnlyForTingXie.isSelected()) {
		    playWordAudio();
		}
		break;
	    case TRANSLATE_DE_ZH:
		txtQuestion.setText(curWord.getTranslation());
		txtHint.setText("Click to reveal hint");
		txtAnswer.setText("");
		if (chkPlayAudioImmediately.isSelected() && !chkPlayAudioOnlyForTingXie.isSelected()) {
		    playWordAudio();
		}
		break;
	    case TRANSLATE_ZH_DE:
		txtQuestion.setText(curWord.getSimpleHanzi());
		txtHint.setText("Click to reveal hint");
		txtAnswer.setText("");
		if (chkPlayAudioImmediately.isSelected() && !chkPlayAudioOnlyForTingXie.isSelected()) {
		    playWordAudio();
		}
		break;
	    case TINGXIE:
		txtQuestion.setText("Listen to audio");
		txtHint.setText("Click to reveal hint");
		txtAnswer.setText("");
		playWordAudio();
		break;
	}

	if (chkAlwaysShowHint.isSelected()) {
	    revealHint();
	}

	curListIndex++;

	repaintCustomTextFields();
    }

    private void cmdEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEndActionPerformed
	if (isDevMode) {
	    for (Word w : allWords) {
		w.setNumWrong_simplified_hanzi(0);
		w.setNumCorrect_simplified_hanzi(0);
		w.setNumWrong_traditional_hanzi(0);
		w.setNumCorrect_traditional_hanzi(0);
	    }
	}

	if (isListContentChanged) {
	    console.logMsg("Saving current word data...");
	    csvReader.writeToFile(allWords);
	    console.logMsg("Current word data successfully saved. Exiting program...");
	} else if (!skipSaveDialog) {
	    int choice = JOptionPane.showConfirmDialog(
		    this,
		    "Word list is unchanged, save anyway?",
		    "Save word list without changes?",
		    JOptionPane.YES_NO_OPTION,
		    JOptionPane.QUESTION_MESSAGE
	    );

	    if (choice == JOptionPane.YES_OPTION) {
		console.logMsg("Saving current word data...");
		csvReader.writeToFile(allWords);
		console.logMsg("Current word data successfully saved. Exiting program...");
	    }
	}

	System.exit(0);
    }//GEN-LAST:event_cmdEndActionPerformed

    private void cmdRevealAnswerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRevealAnswerActionPerformed
	if (curWord != null) {
	    switch (curLearningMode) {
		case SIMPLIFIED_HANZI:
		    txtAnswer.setText(curWord.getSimpleHanzi());
		    break;
		case TRADITIONAL_HANZI:
		    txtAnswer.setText(curWord.getSimpleHanzi());
		    break;
		case PINYIN:
		    txtAnswer.setText(curWord.getPinyin());
		    break;
		case TRANSLATE_DE_ZH:
		    txtAnswer.setText(curWord.getSimpleHanzi());
		    break;
		case TRANSLATE_ZH_DE:
		    txtAnswer.setText(curWord.getTranslation());
		    break;
		case TINGXIE:
		    txtAnswer.setText(curWord.getSimpleHanzi());
		    break;
	    }
	} else {
	    JOptionPane.showMessageDialog(this, "Please start a learning session with the menu on the right side first!", "Error", JOptionPane.ERROR_MESSAGE);
	}

	cmdWrong.setEnabled(true);
	cmdCorrect.setEnabled(true);

	revealHint(); // already includes repaintCustomTextFields()!
    }//GEN-LAST:event_cmdRevealAnswerActionPerformed

    private void txtHintMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtHintMousePressed
	revealHint();
    }//GEN-LAST:event_txtHintMousePressed

    private void revealHint() {
	if (curWord != null) {
	    switch (curLearningMode) {
		case SIMPLIFIED_HANZI:
		    txtHint.setText(curWord.getTranslation());
		    break;
		case TRADITIONAL_HANZI:
		    txtHint.setText(curWord.getTranslation());
		    break;
		case PINYIN:
		    txtHint.setText(curWord.getTranslation());
		    break;
		case TRANSLATE_DE_ZH:
		    txtHint.setText(curWord.getPinyin());
		    break;
		case TRANSLATE_ZH_DE:
		    txtHint.setText(curWord.getPinyin());
		    break;
		case TINGXIE:
		    txtHint.setText(curWord.getTranslation());
		    break;
	    }
	} else {
	    JOptionPane.showMessageDialog(this, "Please start a learning session with the menu on the right side first!", "Error", JOptionPane.ERROR_MESSAGE);
	}

	repaintCustomTextFields();
    }

    private void cmdWrongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdWrongActionPerformed
	switch (curLearningMode) {
	    case SIMPLIFIED_HANZI:
		curWord.setNumWrong_simplified_hanzi(curWord.getNumWrong_simplified_hanzi() + 1);
		console.cprintln("   " + curWord.getSimpleHanzi() + "/" + curWord.getPinyin() + ": Wrong answer. Stats (wrong/correct): " + curWord.getNumWrong_simplified_hanzi() + " / " + curWord.getNumCorrect_simplified_hanzi());
		break;
	    case TRADITIONAL_HANZI:
		curWord.setNumWrong_traditional_hanzi(curWord.getNumWrong_traditional_hanzi() + 1);
		console.cprintln("   " + curWord.getSimpleHanzi() + "/" + curWord.getPinyin() + ": Wrong answer. Stats (wrong/correct): " + curWord.getNumWrong_traditional_hanzi() + " / " + curWord.getNumCorrect_traditional_hanzi());
		break;
	    case PINYIN:
		curWord.setNumWrong_pinyin(curWord.getNumWrong_pinyin() + 1);
		console.cprintln("   " + curWord.getSimpleHanzi() + "/" + curWord.getPinyin() + ": Wrong answer. Stats (wrong/correct): " + curWord.getNumWrong_pinyin() + " / " + curWord.getNumCorrect_pinyin());
		break;
	    case TRANSLATE_DE_ZH:
		curWord.setNumWrong_translate_de_zh(curWord.getNumWrong_translate_de_zh() + 1);
		console.cprintln("   " + curWord.getSimpleHanzi() + "/" + curWord.getPinyin() + ": Wrong answer. Stats (wrong/correct): " + curWord.getNumWrong_translate_de_zh() + " / " + curWord.getNumCorrect_translate_de_zh());
		break;
	    case TRANSLATE_ZH_DE:
		curWord.setNumWrong_translate_zh_de(curWord.getNumWrong_translate_zh_de() + 1);
		console.cprintln("   " + curWord.getSimpleHanzi() + "/" + curWord.getPinyin() + ": Wrong answer. Stats (wrong/correct): " + curWord.getNumWrong_translate_zh_de() + " / " + curWord.getNumCorrect_translate_zh_de());
		break;
	    case TINGXIE:
		curWord.setNumWrong_tingxie(curWord.getNumWrong_tingxie() + 1);
		console.cprintln("   " + curWord.getSimpleHanzi() + "/" + curWord.getPinyin()
			+ ": Wrong answer. Stats (wrong/correct): "
			+ curWord.getNumWrong_tingxie() + " / " + curWord.getNumCorrect_tingxie());
		break;
	}

	curSessionWrong++;
	console.cprintln("Current session (correct/total): " + curSessionCorrect + "/" + (curSessionCorrect + curSessionWrong) + ", percentage: "
		+ String.format("%.2f", (curSessionCorrect / (double) (curSessionCorrect + curSessionWrong)) * 100) + "%");

	isListContentChanged = true;

	loadNextWord();
    }//GEN-LAST:event_cmdWrongActionPerformed

    private void cmdCorrectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCorrectActionPerformed
	switch (curLearningMode) {
	    case SIMPLIFIED_HANZI:
		curWord.setNumCorrect_simplified_hanzi(curWord.getNumCorrect_simplified_hanzi() + 1);
		console.cprintln("   " + curWord.getSimpleHanzi() + "/" + curWord.getPinyin() + ": Correct answer. Stats (wrong/correct): " + curWord.getNumWrong_simplified_hanzi() + " / " + curWord.getNumCorrect_simplified_hanzi());
		break;
	    case TRADITIONAL_HANZI:
		curWord.setNumCorrect_traditional_hanzi(curWord.getNumCorrect_traditional_hanzi() + 1);
		console.cprintln("   " + curWord.getSimpleHanzi() + "/" + curWord.getPinyin() + ": Correct answer. Stats (wrong/correct): " + curWord.getNumWrong_traditional_hanzi() + " / " + curWord.getNumCorrect_traditional_hanzi());
		break;
	    case PINYIN:
		curWord.setNumCorrect_pinyin(curWord.getNumCorrect_pinyin() + 1);
		console.cprintln("   " + curWord.getSimpleHanzi() + "/" + curWord.getPinyin() + ": Correct answer. Stats (wrong/correct): " + curWord.getNumWrong_pinyin() + " / " + curWord.getNumCorrect_pinyin());
		break;
	    case TRANSLATE_DE_ZH:
		curWord.setNumCorrect_translate_de_zh(curWord.getNumCorrect_translate_de_zh() + 1);
		console.cprintln("   " + curWord.getSimpleHanzi() + "/" + curWord.getPinyin() + ": Correct answer. Stats (wrong/correct): " + curWord.getNumWrong_translate_de_zh() + " / " + curWord.getNumCorrect_translate_de_zh());
		break;
	    case TRANSLATE_ZH_DE:
		curWord.setNumCorrect_translate_zh_de(curWord.getNumCorrect_translate_zh_de() + 1);
		console.cprintln("   " + curWord.getSimpleHanzi() + "/" + curWord.getPinyin() + ": Correct answer. Stats (wrong/correct): " + curWord.getNumWrong_translate_zh_de() + " / " + curWord.getNumCorrect_translate_zh_de());
		break;
	    case TINGXIE:
		curWord.setNumCorrect_tingxie(curWord.getNumCorrect_tingxie() + 1);
		console.cprintln("   " + curWord.getSimpleHanzi() + "/" + curWord.getPinyin()
			+ ": Correct answer. Stats (wrong/correct): "
			+ curWord.getNumWrong_tingxie() + " / " + curWord.getNumCorrect_tingxie());
		break;
	}

	curSessionCorrect++;
	console.cprintln("Current session (correct/total): " + curSessionCorrect + "/" + (curSessionCorrect + curSessionWrong) + ", percentage: "
		+ String.format("%.2f", (curSessionCorrect / (double) (curSessionCorrect + curSessionWrong)) * 100) + "%");

	isListContentChanged = true;

	loadNextWord();
    }//GEN-LAST:event_cmdCorrectActionPerformed

    private void cmdResetWordStatsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdResetWordStatsActionPerformed
	for (Word w : allWords) {
	    w.resetWordStats();
	}

	console.logMsg("Saving current word data...");
	csvReader.writeToFile(allWords);
	console.logMsg("Current word data successfully saved. Resetting learning mode...");

	applyLearningMode();
    }//GEN-LAST:event_cmdResetWordStatsActionPerformed

    private void cmdLessonsSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdLessonsSelectAllActionPerformed
	setAllLessonCheckmarks(true);
    }//GEN-LAST:event_cmdLessonsSelectAllActionPerformed

    private void cmdLessonsSelectNoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdLessonsSelectNoneActionPerformed
	setAllLessonCheckmarks(false);
    }//GEN-LAST:event_cmdLessonsSelectNoneActionPerformed

    private void cmdPreviousFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPreviousFontActionPerformed
	if (curFontIndex > 0) {
	    curFontIndex--;
	}

	changeFontTest();
    }//GEN-LAST:event_cmdPreviousFontActionPerformed

    private void cmdNextFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdNextFontActionPerformed
	if (curFontIndex < (fonts.size() - 1)) {
	    curFontIndex++;
	}
	changeFontTest();
    }//GEN-LAST:event_cmdNextFontActionPerformed

    private void chkAlwaysShowHintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAlwaysShowHintActionPerformed
	// TODO add your handling code here:
    }//GEN-LAST:event_chkAlwaysShowHintActionPerformed

    private void cmdTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdTestActionPerformed
	curWord = allWords.get(59);
	System.out.println("Trying to play audio for " + curWord.getPinyin());
	playWordAudio();
    }//GEN-LAST:event_cmdTestActionPerformed

    private void cmdPlayAudioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPlayAudioActionPerformed
	playWordAudio();
    }//GEN-LAST:event_cmdPlayAudioActionPerformed

    private void changeFontTest() {
	displayFont_latin = fonts.get(curFontIndex);
	displayFont_hanzi = fonts.get(curFontIndex);

	console.cprintln("############\n   Display font: " + displayFont_latin + " (" + curFontIndex + ")\n############");

	smallFont_latin = new java.awt.Font(displayFont_latin, java.awt.Font.PLAIN, 12);
	mediumFont_latin = new java.awt.Font(displayFont_latin, java.awt.Font.PLAIN, 24);
	largeFont_latin = new java.awt.Font(displayFont_latin, java.awt.Font.PLAIN, 48);
	smallFont_hanzi = new java.awt.Font(displayFont_hanzi, java.awt.Font.PLAIN, 12);
	mediumFont_hanzi = new java.awt.Font(displayFont_hanzi, java.awt.Font.PLAIN, 24);
	largeFont_hanzi = new java.awt.Font(displayFont_hanzi, java.awt.Font.PLAIN, 48);

	txtQuestion.setFont(largeFont_hanzi);
	txtAnswer.setFont(mediumFont_latin);

	System.out.println("Large font: " + largeFont_hanzi.getFontName());
	System.out.println("Medium font: " + mediumFont_latin.getFontName());

	repaintCustomTextFields();
    }

    private void setAllLessonCheckmarks(boolean val) {
	for (JCheckBox checkbox : lessonCheckboxes.values()) {
	    checkbox.setSelected(val);
	}
    }

    private void repaintCustomTextFields() {
	txtQuestion.revalidate();
	txtQuestion.repaint();
	txtHint.revalidate();
	txtHint.repaint();
	txtAnswer.revalidate();
	txtAnswer.repaint();
    }

    private void playWordAudio() {
	if (curWord != null) {
	    wordAudioService.playWordAsync(curWord);
	}
    }

    private void playWordAudio_mp3(Word word) {
	List<String> stems = tokenizer.toAudioStems(word.getPinyin());

	new Thread(() -> {
	    for (String stem : stems) {
		soundPlayer.play("/audio/" + stem + ".mp3");
	    }
	}).start();
    }

    private final Map<String, JCheckBox> lessonCheckboxes
	    = new LinkedHashMap<>();

    
    private void populateLessonList(Collection<String> lessonIds) {
	pnlLessonList.removeAll();
	lessonCheckboxes.clear();

	pnlLessonList.setLayout(
		new BoxLayout(pnlLessonList, BoxLayout.Y_AXIS)
	);

	Map<String, List<String>> groups = new LinkedHashMap<>();

	for (String id : lessonIds) {
	    if (id == null || id.isBlank()) {
		continue;
	    }

	    groups.computeIfAbsent(
		    getLessonGroup(id),
		    k -> new ArrayList<>()
	    ).add(id);
	}

	for (Map.Entry<String, List<String>> group : groups.entrySet()) {

	    List<String> ids = group.getValue();

	    ids.sort((a, b) -> {
		int cmp = Integer.compare(
			getLessonNumber(a),
			getLessonNumber(b)
		);
		return cmp != 0 ? cmp : a.compareTo(b);
	    });

	    // Group heading
	    JLabel heading = new JLabel(group.getKey());
	    heading.setFont(
		    heading.getFont().deriveFont(Font.BOLD)
	    );
	    heading.setAlignmentX(Component.LEFT_ALIGNMENT);

	    pnlLessonList.add(Box.createVerticalStrut(8));
	    pnlLessonList.add(heading);

	    // Checkboxes
	    for (String id : ids) {

		String label = id.matches("\\d+")
			? "Lesson " + Integer.parseInt(id)
			: id;

		JCheckBox checkbox = new JCheckBox(label);
		checkbox.setAlignmentX(Component.LEFT_ALIGNMENT);

		pnlLessonList.add(checkbox);
		lessonCheckboxes.put(id, checkbox);
	    }
	}

	pnlLessonList.revalidate();
	pnlLessonList.repaint();

	scrLessons.getVerticalScrollBar().setUnitIncrement(16);
    }
    
    
    private String getLessonGroup(String id) {

	if (id.matches("\\d+")) {
	    return "Original Lessons";
	}

	if (id.matches("HJ\\d+[a-zA-Z]L\\d+")) {
	    String book = id.substring(2, id.lastIndexOf('L'));
	    return "Hanyu Jiaocheng " + book.toUpperCase();
	}

	if (id.startsWith("HSK")) {
	    return "HSK";
	}

	return "Other";
    }

    private int getLessonNumber(String id) {

	int i = id.length() - 1;

	while (i >= 0 && Character.isDigit(id.charAt(i))) {
	    i--;
	}

	if (i == id.length() - 1) {
	    return Integer.MAX_VALUE;
	}

	try {
	    return Integer.parseInt(id.substring(i + 1));
	} catch (NumberFormatException e) {
	    return Integer.MAX_VALUE;
	}
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
	/* Set the Nimbus look and feel */
	//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
	/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
	 */
	try {
	    for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
		if ("Nimbus".equals(info.getName())) {
		    javax.swing.UIManager.setLookAndFeel(info.getClassName());
		    break;
		}
	    }
	} catch (ClassNotFoundException ex) {
	    java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	} catch (InstantiationException ex) {
	    java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	} catch (IllegalAccessException ex) {
	    java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	} catch (javax.swing.UnsupportedLookAndFeelException ex) {
	    java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	}
	//</editor-fold>

	/* Create and display the form */
	java.awt.EventQueue.invokeLater(new Runnable() {
	    public void run() {
		new frmMain().setVisible(true);
	    }
	});
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboLearningMode;
    private javax.swing.JCheckBox chkAlwaysShowHint;
    private javax.swing.JCheckBox chkOnlyUnknownWords;
    private javax.swing.JCheckBox chkPlayAudioImmediately;
    private javax.swing.JCheckBox chkPlayAudioOnlyForTingXie;
    private javax.swing.JCheckBox chkTradHanziOnlyIfDifference;
    private javax.swing.JButton cmdCorrect;
    private javax.swing.JButton cmdEnd;
    private javax.swing.JButton cmdLearningModeApply;
    private javax.swing.JButton cmdLessonsSelectAll;
    private javax.swing.JButton cmdLessonsSelectNone;
    private javax.swing.JButton cmdNextFont;
    private javax.swing.JButton cmdPlayAudio;
    private javax.swing.JButton cmdPreviousFont;
    private javax.swing.JButton cmdResetWordStats;
    private javax.swing.JButton cmdRevealAnswer;
    private javax.swing.JButton cmdTest;
    private javax.swing.JButton cmdWrong;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSpinner numShuffleBeta;
    private javax.swing.JPanel pnlLessonList;
    private javax.swing.JScrollPane scrLessons;
    private javax.swing.JTextArea txtAnswer;
    private javax.swing.JTextPane txtConsole;
    private javax.swing.JTextArea txtHint;
    private javax.swing.JTextArea txtQuestion;
    // End of variables declaration//GEN-END:variables

    ConsoleHandler console;
    CSVReader csvReader;
    SoundFileHandler soundFileHandler;
    PinyinAudioTokenizer tokenizer;
    SoundPlayer soundPlayer;
    WordAudioService wordAudioService;
    ArrayList<Word> allWords;
    ArrayList<Word> curList;
    int curListIndex;
    LearningMode curLearningMode;
    Word curWord;
    WeightedShuffle weightedShuffle;

    int curSessionWrong = 0;
    int curSessionCorrect = 0;

    List<String> fonts;
    int curFontIndex = 0;
    String displayFont_latin = "Dialog";
    // String displayFont = "STFangsong";
    String displayFont_hanzi = "FangSong";
    Font smallFont_latin;
    Font mediumFont_latin;
    Font largeFont_latin;
    Font smallFont_hanzi;
    Font mediumFont_hanzi;
    Font largeFont_hanzi;
    // Font smallFont = new java.awt.Font(displayFont, java.awt.Font.PLAIN, 12);
    // Font mediumFont = new java.awt.Font(displayFont, java.awt.Font.PLAIN, 24);
    // Font largeFont = new java.awt.Font(displayFont, java.awt.Font.PLAIN, 48);

    boolean isDevMode = false;
    boolean fontTest = false;
    boolean skipSaveDialog = false;
    boolean isListContentChanged = false;
}
