package za.ac.cput.StudentEnrolment;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import za.ac.cput.StudentEnrolment.Admin;
import za.ac.cput.StudentEnrolment.Student;

/**
 *
 * @author PURE
 */
public class LoginGUI extends JFrame implements ActionListener {

    private JPanel northPanel, centerPanel, southPanel, panel;
    private JLabel siteLogin, detailsLogin, usernameLbl, passwordLbl, imageLbl, emptyLbl;
    private JTextField usernameTxt;
    private JPasswordField passwordTxt;
    private JRadioButton student, admin;
    private JButton loginBtn, signUp, exitBtn;
    private ButtonGroup group;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private static Socket socket;
    private Thread serverThread;
    private Thread clientThread;
    private static volatile StudentEnrolmentSystemServer runServer;
    public Student newStudent;

    public LoginGUI() {
        super("STUDENT ENROLMENT SYSTEM");

        northPanel = new JPanel();
        centerPanel = new JPanel();
        southPanel = new JPanel();
        panel = new JPanel();

        siteLogin = new JLabel("Student Enrolment System");

        usernameLbl = new JLabel("Student Number:");
        passwordLbl = new JLabel("Password:");
        emptyLbl = new JLabel("");

        usernameTxt = new JTextField(10);
        passwordTxt = new JPasswordField(10);

        loginBtn = new JButton("LOGIN");
        signUp = new JButton("SIGN UP");
        exitBtn = new JButton("EXIT");

        student = new JRadioButton("Student");
        admin = new JRadioButton("Admin");

        group = new ButtonGroup();

        runServer = new StudentEnrolmentSystemServer();

        setGui();
        startThreads();
    }

    public void captureStudentDetails() {

        try {
            out.writeObject("fetch#" + newStudent.getStddNum());
            out.flush();

            newStudent = (Student) in.readObject();

            System.out.println(newStudent.getStddNum() + "," + newStudent.getStdName());

        } catch (IOException ex) {
            Logger.getLogger(LoginGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LoginGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public StudentEnrolmentSystemServer startThreads() {

        serverThread = new Thread(() -> {
            //Calling Connect() method to start the server
            runServer.Connect();
        });

        //Server Thread begins execution
        serverThread.start();

        //Open a client Thread
        clientThread = new Thread(() -> {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(LoginGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                System.out.println("about to connect to servr");
                socket = new Socket("127.0.0.1", 12345);
                System.out.println("pssed connecting line");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Connection failed in client");
            }

            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Failed to get streams in client");
            }
        });
        //Client Thread begins execution
        clientThread.start();

        return runServer;
    }

    public void setGui() {

        group.add(student);
        group.add(admin);

        panel.setLayout(new GridLayout(1, 2));
        panel.add(student);
        student.setSelected(true);
        panel.add(admin);

        northPanel.setLayout(new FlowLayout());
        northPanel.setBackground(Color.DARK_GRAY);

        siteLogin.setFont(new Font("Arial", Font.BOLD, 20));
        siteLogin.setForeground(Color.WHITE);
        northPanel.add(siteLogin);
        northPanel.add(panel);

        centerPanel.setLayout(new GridLayout(3, 2));
        centerPanel.add(panel);
        centerPanel.add(emptyLbl);
        centerPanel.add(usernameLbl);
        centerPanel.add(usernameTxt);
        centerPanel.add(passwordLbl);
        centerPanel.add(passwordTxt);

        southPanel.setLayout(new GridLayout(1, 3));
        southPanel.add(loginBtn);
        southPanel.add(signUp);
        southPanel.add(exitBtn);

        this.add(northPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);

        loginBtn.addActionListener(this);
        signUp.addActionListener(this);
        exitBtn.addActionListener(this);

        this.pack();
        this.setSize(700, 300);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);

        //Set the username label to Student Number for Student
        student.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == student) {
                    usernameLbl.setText("Student Number: ");
                }
            }
        });
        //Set the username label to Username for Admin
        admin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == admin) {
                    usernameLbl.setText("Username: ");

                }
            }
        });
    }

    //Validate the student's login details and display the appropriate message
    private void stdLogin() {
        String username = usernameTxt.getText();
        String password = passwordTxt.getText();
        newStudent = new Student(username, password);

        try {
            out.writeObject("Validate");
            out.flush();
            out.writeObject(newStudent);
            out.flush();
            String serverResponse = (String) in.readObject();

            if (serverResponse.equalsIgnoreCase("valid")) {
                JOptionPane.showMessageDialog(null, "Logging in as Student");
                captureStudentDetails();

                closeConnection();
                //Call the StudentGUI object;
                StudentGUI runStudent = new StudentGUI(newStudent);

            } else {
                JOptionPane.showMessageDialog(null, "Logging in failed,Wrong Password or Student Number");
                usernameTxt.setText("");
                passwordTxt.setText("");
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LoginGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //Validate the admin's login details and display the appropriate message
    private void adminLogin() {
        String username = usernameTxt.getText();
        String password = passwordTxt.getText();
        Admin admin = new Admin(username, password);

        try {
            out.writeObject("Validate");
            out.flush();
            out.writeObject(admin);
            out.flush();
            String serverResponse = (String) in.readObject();

            if (serverResponse.equalsIgnoreCase("valid")) {
                JOptionPane.showMessageDialog(null, "Logging in as Admin");

                closeConnection();

                //Call the AdminGUI object
                new AdminGUI2();
            } else {
                JOptionPane.showMessageDialog(null, "Logging in failed,Wrong Password or Username");
                usernameTxt.setText("");
                passwordTxt.setText("");
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Failed to login");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LoginGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void signUp() {
        JPanel signUpPanel = new JPanel();
        JLabel studentNumberLbl = new JLabel("Student Number :");
        JLabel firstNameLbl = new JLabel("First Name :");
        JLabel surnameLbl = new JLabel("Surname :");
        JLabel passwordLbl = new JLabel("Password:");

        JTextField studentNumberTxt = new JTextField();

        JTextField firstNameTxt = new JTextField();

        JTextField surnameTxt = new JTextField();

        JTextField passwordTxt = new JTextField();

        signUpPanel.setLayout(new GridLayout(4, 2));

        signUpPanel.add(studentNumberLbl);
        signUpPanel.add(studentNumberTxt);

        signUpPanel.add(firstNameLbl);
        signUpPanel.add(firstNameTxt);

        signUpPanel.add(surnameLbl);
        signUpPanel.add(surnameTxt);

        signUpPanel.add(passwordLbl);
        signUpPanel.add(passwordTxt);

        int result = JOptionPane.showConfirmDialog(null, signUpPanel, "Sign UP", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = firstNameTxt.getText();
                String surname = surnameTxt.getText();
                String stdNum = studentNumberTxt.getText();
                String stdPassword = passwordTxt.getText();

                Student newStd = new Student(name, surname, stdNum, stdPassword);

                out.writeObject(newStd);
                System.out.println(newStd + " SENT");
                out.flush();
                String response = (String) in.readObject();
                JOptionPane.showMessageDialog(null, response);

            } catch (IOException ex) {
                Logger.getLogger(StudentGUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(StudentGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //Close all connections, streams and sockets.
    public void closeConnection() {

        try {

            runServer.closeConnection();
            in.close();
            out.close();
            socket.close();
            System.out.println("Client Closed connection");
            serverThread.interrupt();
            clientThread.interrupt();
            //System.exit(0);
        } catch (IOException ex) {
            System.out.println("ERROR: " + ex);
        }
    }

    //various operations performed based on the button clicked
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn && student.isSelected()) {
            stdLogin();
        } else if (e.getSource() == loginBtn && admin.isSelected()) {
            adminLogin();
        } else if (e.getSource() == signUp) {
            signUp();
        } else if (e.getSource() == exitBtn) {
            closeConnection();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new LoginGUI();
    }
}
