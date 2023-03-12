package frontend;

import backend.BiometricAuthentication;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


public class Window extends JFrame
{
    JTextPane responsePanel = new JTextPane();
    BiometricAuthentication biometricAuthentication;
    public Window()
    {
        biometricAuthentication = new BiometricAuthentication("localhost", 888,"DLVGAPT22RYVN2ANA7SJ");
        super.setTitle("Sample application");
        super.setPreferredSize(new Dimension(600,300));
        super.setLayout(null);
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        super.setResizable(false);
        super.setVisible(true);
    }

    public void createGui()
    {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(0, 0, 300, 300);

        JPanel loginPanel = new LoginPanel(responsePanel, biometricAuthentication);
        tabbedPane.addTab("Login", loginPanel);

        JPanel registrationPanel = new RegistrationPanel(responsePanel, biometricAuthentication);
        tabbedPane.addTab("Registration", registrationPanel);

        responsePanel.setBounds(301, 20, 286, 245);
        responsePanel.setEditable(false);

        Border border = BorderFactory.createLineBorder(Color.gray);
        responsePanel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        super.add(tabbedPane);
        super.add(responsePanel);
        super.pack();
    }
}
