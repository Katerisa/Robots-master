package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

import javax.swing.*;

import log.Logger;
import sun.rmi.runtime.Log;

public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);

        try {
            File file = new File("locationAndSize.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            if (file.length()==0)
                throw new Exception("File Is Empty");
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(" ");
                int dataInt[] = new int[4];
                for (int i = 0; i < 4; i++)
                    dataInt[i] = Integer.parseInt(data[i]);
                if (data[4].equals("log"))
                {
                    LogWindow logWindow = createLogWindow(dataInt[0], dataInt[1], dataInt[2], dataInt[3]);
                    addWindow(logWindow);
                }
                else {
                    GameWindow gameWindow = createGameWindow(dataInt[0], dataInt[1], dataInt[2], dataInt[3]);
                    addWindow(gameWindow);
                }

            }
        }   catch (Exception e) {
            newGame();
            Logger.debug(e.toString());
            Logger.debug("Использованы значения по умолчанию");
        }

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                closeWindow();
            }
        });
    }
    
    protected LogWindow createLogWindow()
    {
        return createLogWindow(10, 10, 300, 800);
    }

    protected LogWindow createLogWindow(int x, int y, int width, int height){
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setName("log");
        logWindow.setLocation(x,y);
        logWindow.setSize(width, height);
        setMinimumSize(logWindow.getSize());
        //logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected GameWindow createGameWindow()
    {
        return createGameWindow(0, 0, 400, 400);
    }

    protected GameWindow createGameWindow(int x, int y, int width, int height){
        GameWindow gameWindow = new GameWindow();
        gameWindow.setName("game");
        gameWindow.setLocation(x,y);
        gameWindow.setSize(width,  height);
        return gameWindow;
    }

    protected void newGame(){
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);
        GameWindow gameWindow = createGameWindow();
        addWindow(gameWindow);
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    protected void closeWindow() {
        Object[] options = new Object[] {"Да", "Нет"};
        int dialogWindow = JOptionPane.showOptionDialog(this, "Вы уверены?", "Подтверждение",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (dialogWindow == JOptionPane.YES_OPTION) {
            Component[] comp = desktopPane.getComponents();
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("locationAndSize.txt"));
                for (Component e : comp) {
                    writer.write(e.getX() + " " + e.getY() + " " + e.getWidth() + " " + e.getHeight() + " " + e.getName());
                    writer.newLine();
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.exit(0);
            }
        }
    }
    
    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription(
                "Управление приложением");
        generateJMenuItem(fileMenu, "Новая игра", (event) -> newGame());
        generateJMenuItem(fileMenu, "Выход", (event) -> closeWindow());


        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");
        
//        {
//            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
//            systemLookAndFeel.addActionListener((event) -> {
//                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                this.invalidate();
//            });
//            lookAndFeelMenu.add(systemLookAndFeel);
//        }


        generateJMenuItem(lookAndFeelMenu,"Системная схема",(event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });

        generateJMenuItem(lookAndFeelMenu, "Универсальная схема", (event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });

//        {
//            JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
//            crossplatformLookAndFeel.addActionListener((event) -> {
//                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//                this.invalidate();
//            });
//            lookAndFeelMenu.add(crossplatformLookAndFeel);
//        }

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");
        
//        {
//            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
//            addLogMessageItem.addActionListener((event) -> Logger.debug("Новая строка"));
//            testMenu.add(addLogMessageItem);
//        }

        generateJMenuItem(testMenu, "Сообщение в лог", (event) -> Logger.debug("Новая строка"));

        menuBar.add(fileMenu);
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        return menuBar;
    }

    private void generateJMenuItem (JMenu menu, String name, ActionListener act ) {
        JMenuItem addItem = new JMenuItem(name, KeyEvent.VK_S);
        addItem.addActionListener(act);
        menu.add(addItem);
    }
    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
