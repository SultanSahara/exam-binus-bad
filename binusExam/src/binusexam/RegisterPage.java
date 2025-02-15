package binusexam;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterPage {
    public RegisterPage() {
        JFrame frame = new JFrame("Register Page");
        frame.setSize(400, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel, frame);
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel, JFrame frame) {
        panel.setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 20, 80, 25);
        panel.add(nameLabel);

        JTextField nameText = new JTextField(20);
        nameText.setBounds(150, 20, 165, 25);
        panel.add(nameText);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 60, 80, 25);
        panel.add(emailLabel);

        JTextField emailText = new JTextField(20);
        emailText.setBounds(150, 60, 165, 25);
        panel.add(emailText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 100, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(150, 100, 165, 25);
        panel.add(passwordText);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(50, 140, 80, 25);
        panel.add(phoneLabel);

        JTextField phoneText = new JTextField(20);
        phoneText.setBounds(150, 140, 165, 25);
        panel.add(phoneText);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(150, 180, 100, 25);
        panel.add(registerButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(260, 180, 80, 25);
        panel.add(backButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameText.getText();
                String email = emailText.getText();
                String password = new String(passwordText.getPassword());
                String phone = phoneText.getText();

                boolean registered = AuthAccount.registerUser(name, email, password, phone);
                if (registered) {
                    JOptionPane.showMessageDialog(null, "Registration successful! Please log in.");
                    frame.dispose();
                    new LoginPage();
                } else {
                    JOptionPane.showMessageDialog(null, "Registration failed! Email might already be used.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new LoginPage();
            }
        });
    }

    public static void main(String[] args) {
        new RegisterPage();
    }
}

