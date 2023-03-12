package frontend;

import backend.BiometricAuthentication;
import javax.swing.*;


public class RegistrationPanel extends JPanel
{
    public RegistrationPanel(JTextPane responsePanel, BiometricAuthentication biometricAuthentication)
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

        JLabel nameLabel = new JLabel("First name:");
        nameLabel.setBounds(20, 70, 80, 25);
        add(nameLabel);

        JTextField nameField = new JTextField(20);
        nameField.setBounds(100, 70, 170, 25);
        add(nameField);

        JLabel surnameLabel = new JLabel("Last name:");
        surnameLabel.setBounds(20, 100, 80, 25);
        add(surnameLabel);

        JTextField surnameField = new JTextField(20);
        surnameField.setBounds(100, 100, 170, 25);
        add(surnameField);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(20, 150, 250, 25);
        add(registerButton);

        registerButton.addActionListener((e ->
        {
            String responseMsg = biometricAuthentication.register(tokenField.getText(), emailField.getText(), nameField.getText(), surnameField.getText());

            responsePanel.setText(
                            "Request:\n" +
                            "token - " + tokenField.getText() + "\n" +
                            "email - " + emailField.getText() + "\n" +
                            "name - " + nameField.getText() + "\n" +
                            "surname - " + surnameField.getText() + "\n" +
                            "\nResponse:\n" + responseMsg);

            tokenField.setText("");
            emailField.setText("");
            nameField.setText("");
            surnameField.setText("");
        }));
    }
}
