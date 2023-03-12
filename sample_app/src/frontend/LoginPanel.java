package frontend;

import backend.BiometricAuthentication;
import javax.swing.*;


public class LoginPanel extends JPanel
{
    public LoginPanel(JTextPane responsePanel, BiometricAuthentication biometricAuthentication)
    {
        setLayout(null);

        JLabel tokenLabel = new JLabel("Token:");
        tokenLabel.setBounds(20, 10, 80, 25);
        add(tokenLabel);

        JTextField tokenField = new JTextField(20);
        tokenField.setBounds(100, 10, 170, 25);
        add(tokenField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(20, 40, 80, 25);
        add(emailLabel);

        JTextField emailField = new JTextField(20);
        emailField.setBounds(100, 40, 170, 25);
        add(emailField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(20, 90, 250, 25);
        add(loginButton);

        loginButton.addActionListener((e ->
        {
            String responseMsg = biometricAuthentication.login(tokenField.getText(), emailField.getText());

            responsePanel.setText(
                            "Request:\n" +
                            "token - " + tokenField.getText() + "\n" +
                            "email - " + emailField.getText() + "\n" +
                            "\nResponse:\n" + responseMsg);

            tokenField.setText("");
            emailField.setText("");
        }));
    }
}
